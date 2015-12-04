/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
