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
package com.cognitive.bp.poc.alert;


/**
 *
 * @author esteban
 */
public class ActionAgentNotifier {
/*
    private final static Logger logger = LoggerFactory.getLogger("session");
    private final static ActionAgentNotifier INSTANCE = new ActionAgentNotifier();

    private final ScheduledExecutorService jobExecutorService = Executors.newScheduledThreadPool(1);

    public static class ActionAgentNotificationJob implements Runnable {

        private final String sender;
        private final ActionAgentDialogueHelper helper;
        private final SimpleActionAgentNotificationFact notification;

        public ActionAgentNotificationJob(String sender, ActionAgentDialogueHelper helper, SimpleActionAgentNotificationFact notification) {
            this.sender = sender;
            this.helper = helper;
            this.notification = notification;
        }

        @Override
        public void run() {
            
            RulesLoggerHelper.debug(logger, "ActionAgentNotifier", "Notifying AA about {} ", notification);
            
            List<String> receivers = new ArrayList<>();
            List<String> subjects = new ArrayList<>();
            List<String> channels = new ArrayList<>();
            List<String> templates = new ArrayList<>();
            List<String> timeouts = new ArrayList<>();

            receivers.add(notification.getReceiver());
            subjects.add(notification.getSubject1());      //The first subject has to be the patient!
            subjects.add(notification.getSubject2());
            channels.add("ALERT");
            templates.add(notification.getTemplate());
            timeouts.add(notification.getTimeout());

            Map<String, Object> templateVariables = new HashMap<>();

            helper.invokeActionAgent(sender, receivers, subjects, channels, templates, timeouts, templateVariables);
            RulesLoggerHelper.debug(logger, "ActionAgentNotifier", "AA notified!");
        }

    }

    public static ActionAgentNotifier getInstance() {
        return INSTANCE;
    }

    private ActionAgentNotifier() {
    }

    public void scheduleInvokation(String sender, ActionAgentDialogueHelper helper, SimpleActionAgentNotificationFact notification, long timeInSeconds) {
        jobExecutorService.schedule(new ActionAgentNotificationJob(sender, helper, notification), timeInSeconds, TimeUnit.SECONDS);
    }
*/
}
