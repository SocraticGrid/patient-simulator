/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.fact.ModelRecommendation;
import com.cognitive.nsf.management.fact.control.DiseaseActionRequested;
import com.cognitive.nsf.management.fact.control.Enabler;
import com.cognitive.nsf.management.fact.control.Lock;
import com.cognitive.nsf.management.fact.control.Phase;
import com.cognitive.nsf.management.fact.control.Phase.PhaseName;
import com.cognitive.nsf.management.jcpsim.JCpSimDataGatherer;
import com.cognitive.nsf.management.jcpsim.JCpSimDataReceivedEventListener;
import com.cognitive.nsf.management.model.DiseaseModel;
import com.cognitive.nsf.management.rule.BestModelAccumulateFunction;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataManager;
import org.jcpsim.data.JCpSimParameter;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.builder.conf.AccumulateFunctionOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

/**
 *
 * @author esteban
 */
public class Manager {

    private static final Logger LOG = Logger.getLogger(Manager.class.getName());
    private final Map<DiseaseModel, FactHandle> models = new LinkedHashMap<DiseaseModel, FactHandle>();
    private final JCpSimDataGatherer dataGatherer;
    private final JCpSimDataManager dataManager;
    protected KieSession ksession;
    private Map<Resource, ResourceType> internalResources = new LinkedHashMap<Resource, ResourceType>();
    private Map<String, Object> internalGlobals = new HashMap<String, Object>();
    private List<AgendaEventListener> agendaEventListeners = new ArrayList<AgendaEventListener>();
    private List<RuleRuntimeEventListener> workingMemoryEventListeners = new ArrayList<RuleRuntimeEventListener>();
    private FactHandle patientSafetyLock;
    private FactHandle recommendationsSafetyLock;
    private FactHandle automaticModelSwitchLock;
    private ManagerEventListener eventListener;
    private final Semaphore ksessionLock = new Semaphore(1);
    private FactHandle expectationEnabler;
    private double threshold;

    protected Manager(List<DiseaseModel> models, JCpSimDataManager dataManager, long sampleRate) {
        this(models, dataManager, new JCpSimDataGatherer(dataManager, sampleRate));
    }

    protected Manager(List<DiseaseModel> models, JCpSimDataManager dataManager, JCpSimDataGatherer dataGatherer) {
        //store models without associated facthandle
        for (DiseaseModel diseaseModel : models) {
            this.models.put(diseaseModel, null);
        }

        this.dataManager = dataManager;
        this.dataGatherer = dataGatherer;
        this.dataGatherer.addEventListener(new JCpSimDataReceivedEventListener() {
            public void onDataReceived(JCpSimData data) {
                if (!ksessionLock.tryAcquire()) {
                    //discard concurrent data
                    return;
                }
                try {
                    ksession.insert(data);
                    ksession.fireAllRules();
                    Thread.sleep(200);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error processing incoming data", e);
                } finally {
                    ksessionLock.release();
                }
            }
        });

        //compile internal resources
        internalResources.put(ResourceFactory.newClassPathResource("manager/rules/modelManagement.drl"), ResourceType.DRL);
        internalResources.put(ResourceFactory.newClassPathResource("manager/rules/modelManagementQuery.drl"), ResourceType.DRL);
        internalResources.put(ResourceFactory.newClassPathResource("manager/rules/modelManagementSafety.drl"), ResourceType.DRL);
        internalResources.put(ResourceFactory.newClassPathResource("manager/rules/generalVentilatorSafety.drl"), ResourceType.DRL);
        internalResources.put(ResourceFactory.newClassPathResource("manager/rules/generalPatientSafety.drl"), ResourceType.DRL);

        //add ventilator model rules
        internalResources.put(ResourceFactory.newClassPathResource("rules/VentilatorManagementModels.xls"), ResourceType.DTABLE);

        //add diagnosis rules
        
        
        internalResources.put(ResourceFactory.newClassPathResource("rules/SimpleDiagnosisRules.xls"), ResourceType.DTABLE);
        internalResources.put(ResourceFactory.newClassPathResource("rules/WindowedDiagnosisRules.xls"), ResourceType.DTABLE);

        //add lifecycle rules
        internalResources.put(ResourceFactory.newClassPathResource("manager/rules/lifecycleManagement.drl"), ResourceType.DRL);

        //add models' resources
        for (DiseaseModel model : this.models.keySet()) {
            internalResources.putAll(model.getResources());
        }
    }

    public synchronized void startGatheringData() {
        this.goToInitialPhase();
        dataGatherer.start();
    }

    public synchronized void stopGatheringData() {
        ksession.halt();
        dataGatherer.stop();
    }

    public synchronized void goToInitialPhase() {
        this.goToPhase(PhaseName.NORMAL);
    }

    public void goToPhase(PhaseName phaseName) {
        this.retractCurrentPhase();
        this.ksession.insert(new Phase(phaseName, System.currentTimeMillis()));
    }

    private void retractCurrentPhase() {
        Phase phase = this.executeSingleResultQuery("queryCurrentPhase", "$p");

        if (phase != null) {
            ksession.retract(ksession.getFactHandle(phase));
        }
    }

    public synchronized void setActiveModel(DiseaseModel model) {

        //find active model and deactivate it
        for (DiseaseModel diseaseModel : models.keySet()) {

            if (diseaseModel == model) {
                continue;
            }

            if (diseaseModel.isActive()) {
                diseaseModel.setActive(false);
                this.ksession.update(models.get(diseaseModel), diseaseModel);
                break;
            }
        }

        //activate the desidered one
        if (model != null) {
            model.setLastActivationDate(new Date());
            model.setActive(true);
            this.ksession.update(models.get(model), model);
        }

        ksession.fireAllRules();

    }

    public synchronized void setActiveExpectations(String id) {
        this.disableExpectations();

        expectationEnabler = ksession.insert(new Enabler(id, new Date()));
    }

    public synchronized void disableExpectations() {
        if (expectationEnabler != null) {
            ksession.retract(expectationEnabler);
        }
        ksession.fireAllRules();
    }

    public synchronized void enablePatientSafetyRules() {
        if (patientSafetyLock == null) {
            //not currently locked
            return;
        }

        ksession.retract(patientSafetyLock);
        patientSafetyLock = null;
        ksession.fireAllRules();
    }

    public synchronized void disablePatientSafetyRules() {
        if (patientSafetyLock != null) {
            //already locked
            return;
        }

        patientSafetyLock = ksession.insert(new Lock("Patient Safety"));
    }

    public synchronized void enableRecommendationsSafetyRules() {
        if (recommendationsSafetyLock == null) {
            //not currently locked
            return;
        }

        ksession.retract(recommendationsSafetyLock);
        recommendationsSafetyLock = null;
        ksession.fireAllRules();
    }

    public synchronized void disableRecommendationsSafetyRules() {
        if (recommendationsSafetyLock != null) {
            //already locked
            return;
        }

        recommendationsSafetyLock = ksession.insert(new Lock("Recommendations Safety"));
    }

    public synchronized void enableAutomaticModelSwitch() {
        if (automaticModelSwitchLock == null) {
            //not currently locked
            return;
        }

        ksession.retract(automaticModelSwitchLock);
        recommendationsSafetyLock = null;
        ksession.fireAllRules();
    }

    public synchronized void disableAutomaticModelSwitch() {
        if (automaticModelSwitchLock != null) {
            //already locked
            return;
        }

        automaticModelSwitchLock = ksession.insert(new Lock("Disable Automatic Model Switch"));
    }

    public synchronized void requestDiseaseAction(DiseaseActionRequested actionRequest) {
        if (eventListener != null) {
            eventListener.onDiseaseActionRequested(actionRequest);
        }
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public <T extends Object> List<T> executeQuery(String queryName, String resultId, Object... arguments) {

        List<T> results = new ArrayList<T>();
        QueryResults queryResults = this.ksession.getQueryResults(queryName, arguments);

        for (QueryResultsRow resultRow : queryResults) {
            Object result = resultRow.get(resultId);
            try {
                results.add((T) result);
            } catch (ClassCastException e) {
                //nothing to do here
            }
        }
        return results;
    }

    public <T extends Object> T executeSingleResultQuery(String queryName, String resultId, Object... arguments) {

        List<T> results = new ArrayList<T>();
        QueryResults queryResults = this.ksession.getQueryResults(queryName, arguments);

        for (QueryResultsRow resultRow : queryResults) {
            Object result = resultRow.get(resultId);
            try {
                return (T) result;
            } catch (ClassCastException e) {
                //nothing to do here
            }
        }
        return null;
    }

    public synchronized void applyRecommendation(ModelRecommendation recommendation) {

        for (Map.Entry<JCpSimParameter, Double> entry : recommendation.getValidRecommendedValues().entrySet()) {
            this.dataManager.set(entry.getKey(), entry.getValue());
        }

    }

    public void notifyActiveModelSwitch(DiseaseModel newModel) {
        if (eventListener != null) {
            eventListener.onModelChanged(newModel);
        }
    }

    protected void addExtraResource(Resource resource, ResourceType type) {
        this.internalResources.put(resource, type);
    }

    protected void registerGlobal(String key, Object value) {
        internalGlobals.put(key, value);
    }

    protected void addAgendaEventListener(AgendaEventListener agendaEventListener) {
        agendaEventListeners.add(agendaEventListener);
    }

    protected void addWorkingMemoryEventListener(RuleRuntimeEventListener workingMemoryEventListener) {
        workingMemoryEventListeners.add(workingMemoryEventListener);
    }

    protected void createAndConfigureKSession() {

        //create the kbase
        KieBase kbase = this.createKbase(internalResources);

        KieSession newKsession = kbase.newKieSession();

        CompositeSessionListener listener = new CompositeSessionListener(agendaEventListeners, workingMemoryEventListeners);

        newKsession.addEventListener((AgendaEventListener) listener);
        newKsession.addEventListener((RuleRuntimeEventListener) listener);

        newKsession.setGlobal("manager", this);

        newKsession.setGlobal("modelThreshold", this.threshold);

        //add any exta global
        for (Map.Entry<String, Object> entry : this.internalGlobals.entrySet()) {
            newKsession.setGlobal(entry.getKey(), entry.getValue());
        }

        //lets the models to configure the globals
        for (DiseaseModel diseaseModel : models.keySet()) {
            diseaseModel.setGlobals(newKsession);
        }

        //insert each model as a fact and a ModelRecommendation for each of them
        for (DiseaseModel model : this.models.keySet()) {
            models.put(model, newKsession.insert(model));
            newKsession.insert(new ModelRecommendation(model));
        }

        //lets the models insert any initial fact
        for (DiseaseModel diseaseModel : models.keySet()) {
            diseaseModel.insertInitialFacts(newKsession);
        }

        this.ksession = newKsession;
    }

    private KieBase createKbase(Map<Resource, ResourceType> resources) {

        //"drools.accumulate.function.bestModel"
        KieHelper helper = new KieHelper(AccumulateFunctionOption.get("bestModel", new BestModelAccumulateFunction()));
        
        for (Map.Entry<Resource, ResourceType> entry : resources.entrySet()) {
            helper.addResource(entry.getKey(), entry.getValue());
        }
        
        Results results = helper.verify();
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                String outMessage = String.format("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
                System.out.printf(outMessage);
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, outMessage);
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }

        
        KieBaseConfiguration conf = KieServices.Factory.get().newKieBaseConfiguration();
        conf.setOption(EventProcessingOption.STREAM);
        
        return helper.build(conf);
    }

    public void setEventListener(ManagerEventListener eventListener) {
        this.eventListener = eventListener;
    }

    private class CompositeSessionListener implements AgendaEventListener, RuleRuntimeEventListener {

        private List<AgendaEventListener> agendaEventListeners = new ArrayList<AgendaEventListener>();
        private List<RuleRuntimeEventListener> workingMemoryEventListeners = new ArrayList<RuleRuntimeEventListener>();

        public CompositeSessionListener() {
        }

        public CompositeSessionListener(List<AgendaEventListener> agendaEventListeners, List<RuleRuntimeEventListener> workingMemoryEventListeners) {
            this.agendaEventListeners.addAll(agendaEventListeners);
            this.workingMemoryEventListeners.addAll(workingMemoryEventListeners);
        }

        public void addAgendaEventListener(AgendaEventListener agendaEventListener) {
            agendaEventListeners.add(agendaEventListener);
        }

        public void addWorkingMemoryEventListener(RuleRuntimeEventListener workingMemoryEventListener) {
            workingMemoryEventListeners.add(workingMemoryEventListener);
        }

        @Override
        public void matchCreated(MatchCreatedEvent event) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.matchCreated(event);
            }
        }

        @Override
        public void matchCancelled(MatchCancelledEvent ace) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.matchCancelled(ace);
            }
        }

        @Override
        public void beforeMatchFired(BeforeMatchFiredEvent bafe) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.beforeMatchFired(bafe);
            }
        }

        @Override
        public void afterMatchFired(AfterMatchFiredEvent aafe) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.afterMatchFired(aafe);
            }
        }

        @Override
        public void agendaGroupPopped(AgendaGroupPoppedEvent agpe) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.agendaGroupPopped(agpe);
            }
        }

        @Override
        public void agendaGroupPushed(AgendaGroupPushedEvent agpe) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.agendaGroupPushed(agpe);
            }
        }

        @Override
        public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent rfgae) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.beforeRuleFlowGroupActivated(rfgae);
            }
        }

        @Override
        public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent rfgae) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.afterRuleFlowGroupActivated(rfgae);
            }
        }

        @Override
        public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent rfgde) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.beforeRuleFlowGroupDeactivated(rfgde);
            }
        }

        @Override
        public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent rfgde) {
            for (AgendaEventListener agendaEventListener : agendaEventListeners) {
                agendaEventListener.afterRuleFlowGroupDeactivated(rfgde);
            }
        }

        @Override
        public void objectInserted(ObjectInsertedEvent oie) {
            for (RuleRuntimeEventListener workingMemoryEventListener : workingMemoryEventListeners) {
                workingMemoryEventListener.objectInserted(oie);
            }
        }

        @Override
        public void objectUpdated(ObjectUpdatedEvent oue) {
            for (RuleRuntimeEventListener workingMemoryEventListener : workingMemoryEventListeners) {
                workingMemoryEventListener.objectUpdated(oue);
            }
        }

        @Override
        public void objectDeleted(ObjectDeletedEvent ore) {
            for (RuleRuntimeEventListener workingMemoryEventListener : workingMemoryEventListeners) {
                workingMemoryEventListener.objectDeleted(ore);
            }
        }
    }
}
