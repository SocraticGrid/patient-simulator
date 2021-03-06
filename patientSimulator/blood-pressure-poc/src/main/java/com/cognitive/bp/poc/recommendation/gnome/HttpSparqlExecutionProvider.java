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

import com.cognitive.bp.poc.util.EndPointHelper;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.update.Update;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HttpSparqlExecutionProvider.
 *
 */
public class HttpSparqlExecutionProvider {

    private static final Logger logger = LoggerFactory.getLogger(HttpSparqlExecutionProvider.class.getName());
    
    private static final String SPARQL_QUERY_ENDPOINT_KEY = "SPARQLQueryEndpoint";
    private static final String SPARQL_UPDATE_ENDPOINT_KEY = "SPARQLUpdateEndpoint";
    private final String sparqlQueryService;
    private final String sparqlUpdateService;

    public HttpSparqlExecutionProvider() throws IOException {
        this.sparqlQueryService = EndPointHelper.getProperty(SPARQL_QUERY_ENDPOINT_KEY);
        logger.info("HttpQueryExecutionProvider configured to use '{}' query endpoint", this.sparqlQueryService);
        this.sparqlUpdateService = EndPointHelper.getProperty(SPARQL_UPDATE_ENDPOINT_KEY);
        logger.info("HttpQueryExecutionProvider configured to use '{}' update endpoint", this.sparqlQueryService);
    }

    /* (non-Javadoc)
     * @see edu.mayo.twinkql.context.QueryExecutionProvider#provideQueryExecution(com.hp.hpl.jena.query.Query)
     */
    public QueryExecution provideQueryExecution(String query) {
        QueryEngineHTTP qexec = new QueryEngineHTTP(
                this.sparqlQueryService, query);

        return qexec;
    }
    
    public UpdateProcessor provideUpdateProcessor(Update update){
        return UpdateExecutionFactory.createRemote(update, this.sparqlUpdateService);
    }

}
