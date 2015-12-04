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
package com.cognitive.nsf.patientsimulator.websocket;

import com.cognitive.nsf.patientsimulator.servlet.JCpSimWebConsoleServlet;
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
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
@ServerEndpoint("/jcpsimEndpoint")
public class JCpSimWebSocket {

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

    public static void broadcast(JCpSimData data) throws IOException{
        
        JsonObject object = new JsonObject();

        switch (JCpSimWebConsoleServlet.format.toUpperCase()){
            case "JSON":
                    object.addProperty("format", "JSON");
                    if (JCpSimWebConsoleServlet.selectedFields != null){
                        for (JCpSimParameter jCpSimParameter : JCpSimWebConsoleServlet.selectedFields) {
                            object.addProperty(jCpSimParameter.name(), data.get(jCpSimParameter));
                        }
                    }
                break;
            case "JSONLD":
                    object.addProperty("@context", "http://cognitivemedicine.com/jcpsim/vital-sign.jsonld");
                    object.addProperty("@id", "http://cognitivemedicine.com/jcpsim/vital-sign/"+System.nanoTime());
                    object.addProperty("format", "JSONLD");
                    if (JCpSimWebConsoleServlet.selectedFields != null){
                        //TODO: use JSONLD format
                        for (JCpSimParameter jCpSimParameter : JCpSimWebConsoleServlet.selectedFields) {
                            object.addProperty(jCpSimParameter.name(), data.get(jCpSimParameter));
                        }
                    }
                break;
        }
        

        
        String json = gson.toJson(object);
        for (Session s : sessions.values()) {
            s.getBasicRemote().sendText(json);
        }
    }
}
