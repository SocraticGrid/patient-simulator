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
package com.cognitive.nsf.management.model.drools;

import com.cognitive.nsf.management.model.Model;
import com.cognitive.nsf.management.model.ModelSessionManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcpsim.data.JCpSimData;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

/**
 * Drools' based model. This class holds an internal Drools session that is
 * notified each time a new data set from JCpSim is available.
 *
 * @author esteban
 */
public class DroolsModel implements Model {

    public static class Factory {

        private final Map<Resource, ResourceType> resources = new LinkedHashMap<Resource, ResourceType>();

        public Factory() {
            this.resources.put(ResourceFactory.newClassPathResource("com/cognitive/nsf/management/model/drools/DroolsModelRules.drl"), ResourceType.DRL);
        }

        public Factory addResource(Resource resource, ResourceType type) {
            this.resources.put(resource, type);
            return this;
        }

        public DroolsModel createDroolsModelInstance() {
            KieBase kbase = this.createKBase();
            DroolsModel model = new DroolsModel(kbase);

            return model;
        }

        private KieBase createKBase() {

            KieHelper helper = new KieHelper();

            for (Map.Entry<Resource, ResourceType> entry : resources.entrySet()) {
                helper.addResource(entry.getKey(), entry.getValue());
            }

            Results results = helper.verify();

            if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
                List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
                for (Message message : messages) {
                    String errorMessage = String.format("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
                    System.out.println(errorMessage);
                    Logger.getLogger(DroolsModel.class.getName()).log(Level.SEVERE, errorMessage);
                }
                throw new IllegalStateException("Compilation errors were found. Check the logs.");
            }

            KieBaseConfiguration config = KieServices.Factory.get().newKieBaseConfiguration();
            config.setOption(EventProcessingOption.STREAM);

            return helper.build(config);
        }
    }

    private final KieBase kbase;
    private KieSession ksession;
    private ModelSessionManager manager;

    private DroolsModel(KieBase kbase) {
        this.kbase = kbase;
        this.ksession = this.kbase.newKieSession();
    }

    public void init(ModelSessionManager manager) {
        this.manager = manager;
        this.ksession.setGlobal("model", this);
        this.ksession.setGlobal("manager", manager);
    }

    public void processData(JCpSimData data) {
        this.ksession.insert(data);
        this.ksession.fireAllRules();
    }

    public void dispose() {
        ksession.dispose();
    }

    @Override
    public String toString() {
        return "DroolsModel{" + '}';
    }

}
