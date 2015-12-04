/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognitive.bp.poc.recommendation.gnome;

import com.cognitive.bp.poc.model.gnome.SNP;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author esteban
 */
public class GenomeDataService {

    private static final GenomeDataService INSTANCE = new GenomeDataService();
    
    private Cache<String, SNP> snpCache = null;
    private final HttpSparqlExecutionProvider sparqlExecutionProvider;

    public static GenomeDataService getInstance(){
        return INSTANCE;
    }
    
    private GenomeDataService() {
        try{
            snpCache = CacheBuilder.newBuilder().maximumSize(10000)
                    // .recordStats()
                    .build();
            this.sparqlExecutionProvider = new HttpSparqlExecutionProvider();
        } catch (IOException ex) {
            throw new IllegalStateException("Exception initializing GenomeDataService", ex);
        }
    }

    public SNP getSNPByRsid(final String patientId, final String rsid) throws ExecutionException {
        try{
            return snpCache.get(rsid, new Callable<SNP>() {

                @Override
                public SNP call() throws Exception {

                    String query = ""
                            + "PREFIX cog: <http://cognitive.com/ontology#>\n"
                            + "SELECT ?position ?genotype ?chromosome WHERE {\n"
                            + "GRAPH <http://cognitive.com/patient"+patientId+"/genome> {\n"
                            + "[]  a cog:snp;\n"
                            + "    cog:rsid '"+rsid+"' ;\n"
                            + "    cog:position ?position ;\n"
                            + "    cog:genotype ?genotype ;\n"
                            + "    cog:chromosome ?chromosome .\n"
                            + "    }}";

                    QueryExecution qe = GenomeDataService.this.sparqlExecutionProvider.provideQueryExecution(query);
                    ResultSet rs = qe.execSelect();

                    if (rs.hasNext()){
                        QuerySolution solution = rs.nextSolution();
                        int position = solution.getLiteral("position").getInt();
                        String genotype = solution.getLiteral("genotype").getString();
                        String chromosome = solution.getLiteral("chromosome").getString();

                        return new SNP(rsid, position, genotype, chromosome);
                    }

                    throw new NoSNPFoundException(rsid);
                }
            });
        } catch (com.google.common.util.concurrent.UncheckedExecutionException e){
            return null;
        }
    }
    
    public void storeSNP(String patientId, SNP snp){
        Node graph = NodeFactory.createURI("http://cognitive.com/patient" + patientId + "/genome");
        Node genome = NodeFactory.createURI("http://cognitive.com/patient" + patientId + "/genome-" + UUID.randomUUID().toString());
        
        QuadDataAcc data = new QuadDataAcc();
        data.addQuad(new Quad(graph, genome, NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), NodeFactory.createURI("http://cognitive.com/ontology#snp")));
        data.addQuad(new Quad(graph, genome, NodeFactory.createURI("http://cognitive.com/ontology#chromosome"), NodeFactory.createLiteral(snp.getChromosome())));
        data.addQuad(new Quad(graph, genome, NodeFactory.createURI("http://cognitive.com/ontology#genotype"), NodeFactory.createLiteral(snp.getGenotype())));
        data.addQuad(new Quad(graph, genome, NodeFactory.createURI("http://cognitive.com/ontology#position"), NodeFactory.createLiteral(snp.getPosition()+"")));
        data.addQuad(new Quad(graph, genome, NodeFactory.createURI("http://cognitive.com/ontology#rsid"), NodeFactory.createLiteral(snp.getRsid())));
        
        sparqlExecutionProvider.provideUpdateProcessor(new UpdateDataInsert(data)).execute();
    }

}
