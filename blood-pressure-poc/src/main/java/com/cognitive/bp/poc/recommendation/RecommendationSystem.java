/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.recommendation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcpsim.data.JCpSimData;
import com.cognitive.DroolsEventListener;
import com.cognitive.bp.poc.model.AbnormalBucket;
import com.cognitive.bp.poc.model.AlertRequest;
import com.cognitive.bp.poc.model.PeakJCpSimData;
import com.cognitive.bp.poc.model.RecommendationChange;
import com.cognitive.bp.poc.model.ToggleService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.drools.core.io.impl.ClassPathResource;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.internal.utils.KieHelper;

/**
 *
 * @author esteban
 */
public class RecommendationSystem {

    public static interface RecommendationSystemEventListener {

        public void newAbnormalBucket(AbnormalBucket abnormalBucket);

        public void newAlertRequest(AlertRequest alertRequest);

    }

    private boolean debugEnabled;
    private final Map<Resource, ResourceType> extraResources = new HashMap<>();
    private KieSession ksession;
    private final ScheduledExecutorService eventListenersThreadExecutor = Executors.newScheduledThreadPool(1);
    private final List<RecommendationSystemEventListener> eventListeners = Collections.synchronizedList(new ArrayList<RecommendationSystemEventListener>());

    protected RecommendationSystem() {
    }

    public void init() {
        
        KieHelper helper = new KieHelper();
        
        helper.addResource(new ClassPathResource("rules/recommendation-config.drl"), ResourceType.DRL);
        helper.addResource(new ClassPathResource("rules/recommendation-internal.drl"), ResourceType.DRL);
        helper.addResource(new ClassPathResource("rules/recommendation.drl"), ResourceType.DRL);
        helper.addResource(new ClassPathResource("rules/recommendation-genome.drl"), ResourceType.DRL);
        helper.addResource(new ClassPathResource("rules/recommendation-alerts.drl"), ResourceType.DRL);
        
        for (Map.Entry<Resource, ResourceType> entry : this.extraResources.entrySet()) {
            helper.addResource(entry.getKey(), entry.getValue());
        }
        
        Results results = helper.verify();
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                String outMessage = String.format("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
                System.out.printf(outMessage);
                Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, outMessage);
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }

        
        KieBaseConfiguration conf = KieServices.Factory.get().newKieBaseConfiguration();
        conf.setOption(EventProcessingOption.STREAM);
        
        KieBase kbase = helper.build(conf);

        ksession = kbase.newKieSession();

        if (this.debugEnabled) {
            DroolsEventListener droolsEventListener = new DroolsEventListener();
            ksession.addEventListener((AgendaEventListener) droolsEventListener);
            ksession.addEventListener((RuleRuntimeEventListener) droolsEventListener);
        }

        ksession.fireAllRules();

        ksession.openLiveQuery("getAbnormalBucket",
                null,
                new ViewChangedEventListener() {

                    @Override
                    public void rowInserted(Row row) {
                        final AbnormalBucket a = (AbnormalBucket) row.get("$a");
                        eventListenersThreadExecutor.schedule(new Runnable() {
                            @Override
                            public void run() {
                                for (RecommendationSystemEventListener recommendationSystemEventListener : eventListeners) {
                                    recommendationSystemEventListener.newAbnormalBucket(a);
                                }
                            }
                        }, 0, TimeUnit.MILLISECONDS);
                    }

                    @Override
                    public void rowDeleted(Row row) {
                    }

                    @Override
                    public void rowUpdated(Row row) {
                    }
                });

        ksession.openLiveQuery("getAlertRequest",
                null,
                new ViewChangedEventListener() {

                    @Override
                    public void rowInserted(Row row) {
                        final AlertRequest a = (AlertRequest) row.get("$a");
                        eventListenersThreadExecutor.schedule(new Runnable() {
                            @Override
                            public void run() {
                                for (RecommendationSystemEventListener recommendationSystemEventListener : eventListeners) {
                                    recommendationSystemEventListener.newAlertRequest(a);
                                }
                            }
                        }, 0, TimeUnit.MILLISECONDS);
                    }

                    @Override
                    public void rowDeleted(Row row) {
                    }

                    @Override
                    public void rowUpdated(Row row) {
                    }
                });
    }

    public void notifyData(JCpSimData data) {
        this.notifyObject(data);
    }

    public void notifyPeak(PeakJCpSimData peak) {
        this.notifyObject(peak);
    }

    private void notifyObject(Object obj) {
        try {
            ksession.insert(obj);
            ksession.fireAllRules();
            Thread.sleep(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(RecommendationSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addEventListener(RecommendationSystemEventListener listener) {
        this.eventListeners.add(listener);
    }

    public void enableAlerts() {
        this.toggleService(ToggleService.SERVICE.ALERTS, true);
    }

    public void disableAlerts() {
        this.toggleService(ToggleService.SERVICE.ALERTS, false);
    }

    public void enableGenomicData() {
        this.toggleService(ToggleService.SERVICE.GENOME, true);
    }

    public void disableGenomicData() {
        this.toggleService(ToggleService.SERVICE.GENOME, false);
    }

    public void toggleService(ToggleService.SERVICE service, boolean on) {
        ksession.insert(new ToggleService(service, on));
        ksession.fireAllRules();
    }
    
    public void changeParameter(RecommendationChange.PARAMETER parameter, String value) {
        ksession.insert(new RecommendationChange(parameter, value));
        ksession.fireAllRules();
    }

    public boolean areAlertsActive() {
        QueryResults queryResults = ksession.getQueryResults("areAlertsActive");
        QueryResultsRow row = queryResults.iterator().next();
        return (Boolean) row.get("$active");
    }
    
    public long getAlertsDelay() {
        QueryResults queryResults = ksession.getQueryResults("getAlertsDelay");
        QueryResultsRow row = queryResults.iterator().next();
        return (Long) row.get("$delay");
    }

    public boolean isGenomeServiceActive() {
        QueryResults queryResults = ksession.getQueryResults("isGenomeServiceActive");
        QueryResultsRow row = queryResults.iterator().next();
        return (Boolean) row.get("$active");
    }

    protected void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    protected void addExtraResource(Resource resource, ResourceType resourceType) {
        this.extraResources.put(resource, resourceType);
    }

}
