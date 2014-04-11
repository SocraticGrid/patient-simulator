/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.fact.ConstraintViolation;
import com.cognitive.nsf.management.model.DiseaseModel;
import com.cognitive.nsf.management.model.ManualRecommendation;
import com.cognitive.nsf.management.model.JCpSimAuxModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.jcpsim.data.JCpSimDataManager;
import org.jcpsim.data.JCpSimParameter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class ExpectationsUsingMockModelsAndRecommendationsTest {
    
    private MockJCpSimDataManager recommendationClient;
    private JCpSimAuxModel ardsModel;
    private JCpSimAuxModel pneumoniaModel;
    private JCpSimAuxModel asthmaModel;
    private MockJCpSimDataManager client;
    private MockJCpSimDataGatherer dataGatherer;
    private TestManager manager;
    
    @Before
    public void setUp() throws Exception{
        List<DiseaseModel> models = new ArrayList<DiseaseModel>();
        
        recommendationClient = new MockJCpSimDataManager();
        
        
        Map<Resource, ResourceType> ardsResources = new LinkedHashMap<Resource, ResourceType>();
        ardsResources.put(ResourceFactory.newClassPathResource("rules/ARDSExpectations.drl"), ResourceType.DRL);
        ardsModel = new JCpSimAuxModel("ARDS", ardsResources, recommendationClient);
        ardsModel.setActive(true);
        
        //For simplicity, all models uses the same recommendation client.
        Map<Resource, ResourceType> pneumoniaResources = new LinkedHashMap<Resource, ResourceType>();
        pneumoniaResources.put(ResourceFactory.newClassPathResource("rules/PneumoniaExpectations.drl"), ResourceType.DRL);
        pneumoniaModel = new JCpSimAuxModel("Pneumonia", pneumoniaResources, recommendationClient, true);
        
        //For simplicity, all models uses the same recommendation client.
        Map<Resource, ResourceType> asthmaResources = new LinkedHashMap<Resource, ResourceType>();
        asthmaResources.put(ResourceFactory.newClassPathResource("rules/AsthmaExpectations.drl"), ResourceType.DRL);
        asthmaModel = new JCpSimAuxModel("Asthma", asthmaResources, recommendationClient, true);
        
        
        models.add(ardsModel);
        models.add(pneumoniaModel);
        models.add(asthmaModel);
        
        client = new MockJCpSimDataManager();
        dataGatherer = new MockJCpSimDataGatherer(client);
        manager = new TestManager(models, client, dataGatherer);
    }
    
    @Test
    public void testValidRecommendation() throws Exception{
        
        manager.startGatheringData();
        dataGatherer.pushData();
        
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PIP, 21.0));
        dataGatherer.pushData();
        
        Thread.sleep(1000);

        Assert.assertEquals(21, client.getData().get(JCpSimParameter.V_PIP), 0.1);
        
    }
    
    @Test
    public void testFailCaseIncreasingPIPWillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PIP to 15
        client.set(JCpSimParameter.V_PIP, 15);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase PIP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PIP, 15.5));
        //dataGatherer.pushData();
        

        System.out.println("\nDATA 3");
        //PaCO2 increases instead of decrease
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing PIP will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        Assert.assertEquals(ardsModel, constraintViolations.get(0).getModel());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing PIP will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        Assert.assertEquals(pneumoniaModel, constraintViolations.get(0).getModel());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ASTHMA] Increasing PIP will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        Assert.assertEquals(asthmaModel, constraintViolations.get(0).getModel());
        
    }
    
    @Test
    public void testCorrectCaseIncreasingPIPWillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PIP to 15
        client.set(JCpSimParameter.V_PIP, 15);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase PIP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PIP, 15.5));
        //dataGatherer.pushData();
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(1000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing PIP will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing PIP will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
    }
    
    @Test
    public void testFailCaseDecreasingPIPWillIncreasePaCO2whenPIPis12_50() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PIP to 15
        client.set(JCpSimParameter.V_PIP, 23);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease PIP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PIP, 22.5));
        //dataGatherer.pushData();
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases instead of increase
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing PIP will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        
        Assert.assertEquals(8.0, constraintViolations.get(0).getWeight(), 0.1);
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing PIP will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        
        Assert.assertEquals(5.0, constraintViolations.get(0).getWeight(), 0.1);
        
    }
    
    @Test
    public void testCorrectCaseDecreasingPIPWillIncreasePaCO2whenPIPis12_50() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PIP to 15
        client.set(JCpSimParameter.V_PIP, 15);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease PIP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PIP, 14.5));
        //dataGatherer.pushData();
        

        System.out.println("\nDATA 3");
        //PaCO2 increases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing PIP will increase PaCO2");
        
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing PIP will increase PaCO2");
        
        Assert.assertTrue(constraintViolations.isEmpty());
        
    }
    
    
    
    @Test
    public void testFailCaseIncreasingRateWillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original FREQUENCY to 41
        client.set(JCpSimParameter.V_FREQUENCY, 41);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase FREQUENCY
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_FREQUENCY, 50));
        

        System.out.println("\nDATA 3");
        //PaCO2 increases instead of decrease
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing FREQUENCY will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(2.0, constraintViolations.get(0).getWeight(), 0.1);
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing FREQUENCY will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(4.0, constraintViolations.get(0).getWeight(), 0.1);
        
        
    }
    
    @Test
    public void testCorrectCaseIncreasingRateWillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original FREQUENCY to 15
        client.set(JCpSimParameter.V_FREQUENCY, 25);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase FREQUENCY
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_FREQUENCY, 35));
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(1000);
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing FREQUENCY will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing FREQUENCY will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        
    }
    
    @Test
    public void testFailCaseDecreasingRateWillIncreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original FREQUENCY to 10
        client.set(JCpSimParameter.V_FREQUENCY, 10);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease FREQUENCY
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_FREQUENCY, 5));
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases instead of increase
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing FREQUENCY will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing FREQUENCY will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        
    }
    
    @Test
    public void testCorrectCaseDecreasingRateWillIncreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original FREQUENCY to 10
        client.set(JCpSimParameter.V_FREQUENCY, 10);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease FREQUENCY
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_FREQUENCY, 5));
        

        System.out.println("\nDATA 3");
        //PaCO2 increases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing FREQUENCY will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing FREQUENCY will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
    }
    
    
    @Test
    public void testFailCaseIncreasingPEEPWillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PEEP to 6
        client.set(JCpSimParameter.V_PEEP, 6);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase PEEP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PEEP, 9));
        

        System.out.println("\nDATA 3");
        //PaCO2 increases instead of decrease
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing PEEP will decrease PaCO2 if PEEP <= 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(8.0, constraintViolations.get(0).getWeight(), 0.1);
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing PEEP will decrease PaCO2 if PEEP <= 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(6.0, constraintViolations.get(0).getWeight(), 0.1);
        
        
    }
    
    @Test
    public void testCorrectCaseIncreasingPEEPWillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PEEP to 6
        client.set(JCpSimParameter.V_PEEP, 6);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase PEEP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PEEP, 9));
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing PEEP will decrease PaCO2 if PEEP <= 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing PEEP will decrease PaCO2 if PEEP <= 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
    }
    
    @Test
    public void testFailCaseDecreasingPEEPWillIncreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PEEP to 9
        client.set(JCpSimParameter.V_PEEP, 9);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease PEEP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PEEP, 6));
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases instead of increase
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing PEEP will increase PaCO2 if PEEP <= 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(8.0, constraintViolations.get(0).getWeight(), 0.1);
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing PEEP will increase PaCO2 if PEEP <= 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(6.0, constraintViolations.get(0).getWeight(), 0.1);
        
        
    }
    
    @Test
    public void testCorrectCaseDecreasingPEEPWillIncreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PEEP to 9
        client.set(JCpSimParameter.V_PEEP, 9);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease PEEP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PEEP, 6));
        

        System.out.println("\nDATA 3");
        //PaCO2 increases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing PEEP will increase PaCO2 if PEEP <= 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing PEEP will increase PaCO2 if PEEP <= 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
    }
    
    
    @Test
    public void testCorrectCaseIncreasingPEEPWillIncreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PEEP to 19
        client.set(JCpSimParameter.V_PEEP, 19);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase PEEP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PEEP, 25));
        

        System.out.println("\nDATA 3");
        //PaCO2 increases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing PEEP will increase PaCO2 if PEEP > 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing PEEP will increase PaCO2 if PEEP > 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
    }
    
    @Test
    public void testCorrectCaseDecreasingPEEPWillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original PEEP to 25
        client.set(JCpSimParameter.V_PEEP, 25);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease PEEP
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_PEEP, 19));
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing PEEP will decrease PaCO2 if PEEP > 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing PEEP will decrease PaCO2 if PEEP > 20");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
    }
    
    
    @Test
    public void testFailCaseIncreasingFIO2WillIncreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original FIO2 to 35
        client.set(JCpSimParameter.V_FIO2, 35);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase FIO2
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_FIO2, 40));
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases instead of increase
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing FIO2 will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing FIO2 will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        
    }
    
    
    @Test
    public void testCorrectCaseIncreasingFIO2WillIncreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original FIO2 to 35
        client.set(JCpSimParameter.V_FIO2, 35);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to increase FIO2
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_FIO2, 40));
        

        System.out.println("\nDATA 3");
        //PaCO2 increases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Increasing FIO2 will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Increasing FIO2 will increase PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
    }
    
    @Test
    public void testFailCaseDecreasingFIO2WillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original FIO2 to 40
        client.set(JCpSimParameter.V_FIO2, 40);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease FIO2
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_FIO2, 35));
        

        System.out.println("\nDATA 3");
        //PaCO2 increases instead of decrease
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 + 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing FIO2 will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing FIO2 will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertFalse(constraintViolations.isEmpty());
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals(10.0, constraintViolations.get(0).getWeight(), 0.1);
        
    }
    
    @Test
    public void testCorrectCaseDecreasingFIO2WillDecreasePaCO2() throws Exception{

        double originalPaCO2 = client.getData().get(JCpSimParameter.O_PCO2);
        
        System.out.println("\nDATA 1");
        //set the original FIO2 to 40
        client.set(JCpSimParameter.V_FIO2, 40);
        manager.startGatheringData();
        dataGatherer.pushData();
        
        System.out.println("\nDATA 2");
        //recommend to decrease FIO2
        manager.insertFact(new ManualRecommendation(JCpSimParameter.V_FIO2, 35));
        

        System.out.println("\nDATA 3");
        //PaCO2 decreases
        client.set(JCpSimParameter.O_PCO2, originalPaCO2 - 0.2);
        dataGatherer.pushData();
        
        
        Thread.sleep(2000);
        
        
        List<ConstraintViolation> constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[ARDS] Decreasing FIO2 will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
        constraintViolations = manager.executeQuery("queryConstraintViolationsBySource", "$c", "[PNEUMONIA] Decreasing FIO2 will decrease PaCO2");
        
        Assert.assertNotNull(constraintViolations);
        Assert.assertTrue(constraintViolations.isEmpty());
        
    }
    
    private void changeGasCalculatedFlag(JCpSimDataManager dataManager){
        dataManager.set(JCpSimParameter.O_G_GAS_CALCULATED, Math.abs(dataManager.getData().get(JCpSimParameter.O_G_GAS_CALCULATED)-1));
    }
    
}
