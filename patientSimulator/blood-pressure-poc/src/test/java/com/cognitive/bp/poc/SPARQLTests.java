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
package com.cognitive.bp.poc;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class SPARQLTests {
    
    /**
     * Used only to test the remote insertion of quads into fuseki.
     * @throws IOException 
     */
    @Test
    @Ignore
    public void insertTriplesTest() throws IOException{
        
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        
        Node graph = NodeFactory.createURI("http://cognitive.com/patient2/prescription");
        Node medicationPrescription = NodeFactory.createURI("http://cognitive.com/patient2/prescription-"+UUID.randomUUID().toString());
        
        QuadDataAcc data = new QuadDataAcc();
        data.addQuad(new Quad( graph, medicationPrescription, NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), NodeFactory.createURI("http://hl7.com/fhir/medicationPrescription")));
        data.addQuad(new Quad( graph, medicationPrescription, NodeFactory.createURI("http://hl7.com/fhir/extension/drug"), NodeFactory.createURI("http://schemes.caregraf.info/rxnorm#866516")));
        data.addQuad(new Quad( graph, medicationPrescription, NodeFactory.createURI("http://hl7.com/fhir/extension/drugLabel"), NodeFactory.createLiteral("Metoprolol Tartrate 50 MG Oral Tablet [Lopressor]")));
        data.addQuad(new Quad( graph, medicationPrescription, NodeFactory.createURI("http://hl7.com/fhir/extension/dose"), NodeFactory.createLiteral("q6h")));
        data.addQuad(new Quad( graph, medicationPrescription, NodeFactory.createURI("http://hl7.com/fhir/extension/time"), NodeFactory.createLiteral(dateFormat.format(new Date()), XSDDatatype.XSDdateTime)));
        
        UpdateExecutionFactory.createRemote(new UpdateDataInsert(data), "http://localhost:3030/ds/update").execute();
                
    }
}
