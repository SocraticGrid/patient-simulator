/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.alert;

import com.cognitive.bp.poc.alert.mock.MockUCSClient;
import com.cognitive.bp.poc.util.EndPointHelper;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.uc.interfaces.AlertingIntf;
import org.socraticgrid.hl7.services.uc.interfaces.ClientIntf;
import org.socraticgrid.hl7.services.uc.interfaces.ConversationIntf;
import org.socraticgrid.hl7.services.uc.interfaces.ManagementIntf;
import org.socraticgrid.hl7.services.uc.model.AlertMessage;
import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.MessageModel;
import org.socraticgrid.hl7.ucs.nifi.api.UCSNiFiSession;
import org.socraticgrid.hl7.ucs.nifi.common.util.AlertMessageBuilder;
import org.socraticgrid.hl7.ucs.nifi.common.util.MessageBuilder;
import org.socraticgrid.hl7.ucs.nifi.common.util.MessageBuilder.Recipient;

/**
 * Utility class to configure a UCS-Nifi client and send messages through it.
 * This class supports a {@link MockUCSClient} nifiHost, in which case a MOCK client can be used
 * for dev purposes.
 * @author esteban
 */
public class UCSNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(UCSNotifier.class);
    
    
    private final ScheduledExecutorService jobExecutorService = Executors.newScheduledThreadPool(1);
    
    private UCSNiFiSession session = null;
    private ClientIntf client;
    private AlertingIntf alerting;
    private ManagementIntf management;
    private ConversationIntf conversation;

    public void configure() throws IOException, InterruptedException {
        
        //Nifi Specific configuration
        String nifiHost = EndPointHelper.getProperty("nifiHost");
        
        if ("MOCK".equals(nifiHost)){
            LOGGER.debug("MOCK UCS client is being used. Alerting, Management and Conversation interfaces are not available in this mode.");
            this.client = new MockUCSClient();
            return;
        }
        
        nifiHost = "http://"+nifiHost;
        
        String alertingCommandURL = nifiHost+":"+EndPointHelper.getProperty("alertingCommandPort")+"/contentListener";
        String clientCommandURL = nifiHost+":"+EndPointHelper.getProperty("clientCommandPort")+"/contentListener";
        String managementCommandURL = nifiHost+":"+EndPointHelper.getProperty("managementCommandPort")+"/contentListener";
        String conversationCommandURL = nifiHost+":"+EndPointHelper.getProperty("conversationCommandPort")+"/contentListener";
        String sendMessageURL = nifiHost+":"+EndPointHelper.getProperty("sendMessagePort")+"/contentListener";
        
        
        //Nifi Client Specific Configuration
        String ucsClientHost = EndPointHelper.getProperty("ucsClientHost");
        int ucsClientPort = EndPointHelper.getPropertyAsInt("ucsClientPort");
        
        LOGGER.debug("UCSNotifier Configuration:");
        LOGGER.debug("\tnifiHost: {}", nifiHost);
        LOGGER.debug("\talertingCommandURL: {}", alertingCommandURL);
        LOGGER.debug("\tclientCommandURL: {}", clientCommandURL);
        LOGGER.debug("\tmanagementCommandURL: {}", managementCommandURL);
        LOGGER.debug("\tconversationCommandURL: {}", conversationCommandURL);
        LOGGER.debug("\tsendMessageURL: {}", sendMessageURL);
        LOGGER.debug("\tucsClientHost: {}", ucsClientHost);
        LOGGER.debug("\tucsClientPort: {}", ucsClientPort);
        
        session = new UCSNiFiSession.UCSNiFiSessionBuilder()
                .withAlertingCommandURL(alertingCommandURL)
                .withClientCommandURL(clientCommandURL)
                .withManagementCommandURL(managementCommandURL)
                .withConversationCommandURL(conversationCommandURL)
                .withNifiSendMessageURL(sendMessageURL)
                .withUCSClientHost(ucsClientHost)
                .withUCSClientPort(ucsClientPort)
                .withUCSAlertingHost(ucsClientHost)
                .withManagementHost(ucsClientHost)
                .withConversationHost(ucsClientHost)
                .withUCSClientListener(new UCSClientAdapter())
                .withUCSAlertingListener(new UCSAlertingAdapter()).build();

        client = session.getNewClient();
        alerting = session.getNewAlerting();
        management = session.getNewManagement();
        conversation = session.getNewConversation();
    }
    
    public void dispose() throws IOException{
        if (session != null){
            session.dispose();
        }
    }

    public void sendMessage(Message m) throws Exception{
        if (m instanceof AlertMessage){
            MessageModel<AlertMessage> mm = new MessageModel<>((AlertMessage)m);
            client.sendMessage(mm);
        } else if (m instanceof Message){
            MessageModel<Message> mm = new MessageModel<>(m);
            client.sendMessage(mm);
        } else {
            throw new IllegalArgumentException("Can't send message of type "+m.getClass().getName());
        }
    }
    
    public void scheduleSendMessageInvokation(final Message m, long timeInSeconds) {
        jobExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.debug("Notifying UCS about {}."+m);
                    UCSNotifier.this.sendMessage(m);
                } catch (Exception ex) {
                    LOGGER.error("Error sending Message to UCS", ex);
                }
            }
        } , timeInSeconds, TimeUnit.SECONDS);
    }
    
    public MessageBuilder getMessageBuilder() throws IOException{
        return new MessageBuilder();
    }
    
    public AlertMessageBuilder getAlertMessageBuilder() throws IOException{
        return new AlertMessageBuilder();
    }
    
    public Recipient createMessageRecipient(String address, String serviceId){
        return new MessageBuilder.Recipient(address, serviceId);
    }
    
    public Recipient createAlertMessageRecipient(String address){
        return this.createMessageRecipient(address, "ALERT");
    }
    
}
