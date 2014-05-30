/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.patientsimulator.recommendation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author esteban
 */
@ServerEndpoint("/cdsEndpoint")
public class RecommendationConsoleWebSocket {

    private static final Map<String, Session> sessions = Collections.synchronizedMap(new HashMap<String, Session>());
    private static Gson gson = new Gson();
    
    @OnOpen
    public void open(Session session, EndpointConfig conf) {
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void close(Session session, CloseReason reason) {
        sessions.remove(session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message Arrived: "+message);
    }

    public static void broadcast(JsonObject data) throws IOException{
        
        String json = gson.toJson(data);
        
        for (Session s : sessions.values()) {
            s.getBasicRemote().sendText(json);
        }
    }
}
