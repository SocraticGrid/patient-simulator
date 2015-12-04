/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.recommendation.patient;

import com.cognitive.bp.poc.model.PatientMedicationEvent;
import com.cognitive.bp.poc.recommendation.gnome.HttpSparqlExecutionProvider;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author esteban
 */
public class PatientDataService {

    private static final PatientDataService INSTANCE = new PatientDataService();
    private final HttpSparqlExecutionProvider sparqlExecutionProvider;
    private final SimpleDateFormat dateFormat;
    
    private static final Logger logger = LoggerFactory.getLogger(PatientDataService.class.getName());

    public static PatientDataService getInstance() {
        return INSTANCE;
    }

    private PatientDataService() {
        try {
            this.sparqlExecutionProvider = new HttpSparqlExecutionProvider();
            this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        } catch (IOException ex) {
            throw new IllegalStateException("Exception initializing PatientDataService", ex);
        }
    }

    public void storePatientMedications(PatientMedicationEvent e) {

        Node graph = NodeFactory.createURI("http://cognitive.com/patient" + e.getPatientId() + "/prescription");
        Node medicationPrescription = NodeFactory.createURI("http://cognitive.com/patient" + e.getPatientId() + "/prescription-" + UUID.randomUUID().toString());

        QuadDataAcc data = new QuadDataAcc();
        data.addQuad(new Quad(graph, medicationPrescription, NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), NodeFactory.createURI("http://hl7.com/fhir/medicationPrescription")));
        data.addQuad(new Quad(graph, medicationPrescription, NodeFactory.createURI("http://hl7.com/fhir/extension/drug"), NodeFactory.createURI(e.getDrugClass())));
        data.addQuad(new Quad(graph, medicationPrescription, NodeFactory.createURI("http://hl7.com/fhir/extension/drugLabel"), NodeFactory.createLiteral(e.getDrugLabel())));
        data.addQuad(new Quad(graph, medicationPrescription, NodeFactory.createURI("http://hl7.com/fhir/extension/dose"), NodeFactory.createLiteral(e.getDose())));
        data.addQuad(new Quad(graph, medicationPrescription, NodeFactory.createURI("http://hl7.com/fhir/extension/time"), NodeFactory.createLiteral(dateFormat.format(e.getTimestamp()), XSDDatatype.XSDdateTime)));

        sparqlExecutionProvider.provideUpdateProcessor(new UpdateDataInsert(data)).execute();
    }

    public void storePatientMedicationsAsynchronoulsy(final PatientMedicationEvent e) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                storePatientMedications(e);
            }
        }).start();
    }
    
    public List<PatientMedicationEvent> getPatientMedications(String patientId, long timestampFrom, long timestampTo) throws IOException {

        StringBuilder query = new StringBuilder("");
        query.append("SELECT ?s ?drugClass ?drugLabel ?dose ?time WHERE {");
        query.append("  GRAPH <http://cognitive.com/patient").append(patientId).append("/prescription> {");
        query.append("      ?s  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://hl7.com/fhir/medicationPrescription> ;");
        query.append("          <http://hl7.com/fhir/extension/drug> ?drugClass ;");
        query.append("          <http://hl7.com/fhir/extension/drugLabel> ?drugLabel ;");
        query.append("          <http://hl7.com/fhir/extension/dose> ?dose ;");
        query.append("          <http://hl7.com/fhir/extension/time> ?time .");
        query.append("      FILTER( ?time >= <http://www.w3.org/2001/XMLSchema#dateTime>('").append(dateFormat.format(new Date(timestampFrom))).append("') && ?time <= <http://www.w3.org/2001/XMLSchema#dateTime>('").append(dateFormat.format(new Date(timestampTo))).append("') )");
        query.append("  }");
        query.append("}");

        logger.info("PatientDataService query: \n"+query.toString());
        
        QueryExecution qe = this.sparqlExecutionProvider.provideQueryExecution(query.toString());
        ResultSet rs = qe.execSelect();

        List<PatientMedicationEvent> results = new ArrayList<>();
        while (rs.hasNext()) {
            try {
                QuerySolution solution = rs.nextSolution();
                
                Resource drugClassResource = solution.getResource("drugClass");
                String drugClass;
                if (drugClassResource.toString().startsWith("http://schemes.caregraf.info/rxnorm#")){
                    drugClass = "rxnorm:"+drugClassResource.toString().substring(drugClassResource.toString().indexOf("#")+1);
                } else {
                    throw new UnsupportedOperationException("Don't know how to process no RXNORM drugs!");
                }
                
                PatientMedicationEvent event = new PatientMedicationEvent();
                event.setDose(solution.getLiteral("dose").getString());
                event.setDrugClass(drugClass);
                event.setDrugLabel(solution.getLiteral("drugLabel").getString());
                event.setPatientId(patientId);
                event.setTimestamp(dateFormat.parse(solution.getLiteral("time").getString()).getTime());
                
                results.add(event);
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Error parsing date", ex);
            }

        }

        return results;
    }

}
