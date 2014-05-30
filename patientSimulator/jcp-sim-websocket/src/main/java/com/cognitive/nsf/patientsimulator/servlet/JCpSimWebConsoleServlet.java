/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.patientsimulator.servlet;

import com.cognitive.nsf.patientsimulator.recommendation.RecommendationContextListener;
import com.cognitive.nsf.patientsimulator.websocket.JCpSimContextListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
@WebServlet(name = "JCpSimWebConsoleServlet", urlPatterns = {"/JCpSimWebConsoleServlet"})
public class JCpSimWebConsoleServlet extends HttpServlet {

    public static List<JCpSimParameter> selectedFields = Collections.synchronizedList(new ArrayList<JCpSimParameter>());
    public static String format = "JSON" ;
    private JsonArray availableFields;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        if (availableFields == null){
            availableFields = new JsonArray();
            for (JCpSimParameter p : JCpSimParameter.values()) {
                availableFields.add(new JsonPrimitive(p.name()));
            }
        }
        
        if (selectedFields.isEmpty()) {
            String selectedFieldsString = config.getServletContext().getInitParameter("jcpsim.selected-fields");
            if (selectedFieldsString != null) {
                StringTokenizer tokenizer = new StringTokenizer(selectedFieldsString, ",");
                while (tokenizer.hasMoreTokens()) {
                    selectedFields.add(JCpSimParameter.valueOf(tokenizer.nextToken().trim()));
                }
            }
        }
        
        if (format == null){
            format = config.getServletContext().getInitParameter("jcpsim.format") == null? "JSON" : config.getServletContext().getInitParameter("jcpsim.format").toUpperCase();
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (PrintWriter out = response.getWriter()) {
            String action = request.getParameter("action");

            Gson gson = new Gson();
            JsonElement result = null;
            switch (action) {
                case "getInitialConfiguration":
                    result = this.getInitialConfiguration();
                    break;
                case "getDataGathererStatus":
                    result = this.getDataGathererStatus();
                    break;
                case "changeDataGathererStatus":
                    String attribute = request.getParameter("attribute");
                    String value = request.getParameter("value");
                    result = this.changeDataGathererStatus(attribute, value);
                    break;
            }

            response.setContentType("text/json;charset=UTF-8");
            if (result != null) {
                out.write(gson.toJson(result));
            } else {
                out.write("{}");
            }
        }
    }

    private JsonElement getInitialConfiguration() {

        JsonObject config = new JsonObject();
        config.add("availableFields", availableFields);

        return config;
    }

    private JsonElement getDataGathererStatus() {
        JsonObject jcpsim = new JsonObject();
        jcpsim.addProperty("running", JCpSimContextListener.jCpSimDataGatherer.isRunning());
        jcpsim.addProperty("sampleRate", JCpSimContextListener.jCpSimDataGatherer.getSampleRate());
        jcpsim.addProperty("format", JCpSimWebConsoleServlet.format);

        JsonArray selectedFieldsArray = new JsonArray();
        for (JCpSimParameter p : JCpSimWebConsoleServlet.selectedFields) {
            selectedFieldsArray.add(new JsonPrimitive(p.name()));
        }
        jcpsim.add("selectedFields", selectedFieldsArray);
        
        JsonObject config = new JsonObject();
        config.add("jcpsim", jcpsim);

        return config;
    }

    private JsonElement changeDataGathererStatus(String attribute, String value) {
        try {
            switch (attribute) {
                case "running": {
                    Boolean realValue = Boolean.parseBoolean(value);
                    if (realValue) {
                        if (!JCpSimContextListener.jCpSimDataGatherer.isRunning()) {
                            JCpSimContextListener.jCpSimDataGatherer.start();
                        }
                    } else {
                        JCpSimContextListener.jCpSimDataGatherer.stop();
                    }
                    break;
                }
                case "sampleRate": {
                    int realValue = Integer.parseInt(value);
                    JCpSimContextListener.jCpSimDataGatherer.setSampleRate(realValue);
                    break;
                }
                case "format": {
                    if (value.toUpperCase().equals("JSON") || value.toUpperCase().equals("JSONLD")){
                        JCpSimWebConsoleServlet.format = value.toUpperCase();
                    } else {
                        throw new IllegalArgumentException("Invalid value '"+value+"' for attribute '" + attribute + "'");
                    }
                    break;
                }
                case "fields": {
                    selectedFields.clear();
                    StringTokenizer tokenizer = new StringTokenizer(value, ",");
                    while (tokenizer.hasMoreTokens()) {
                        String v = tokenizer.nextToken().trim();
                        if (!"multiselect-all".equals(v)){
                            selectedFields.add(JCpSimParameter.valueOf(v));
                        }
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown attribute '" + attribute + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonParser().parse("{status: 'error', message: '" + e.getMessage().replaceAll("'", "") + "'}");
        }
        return this.getDataGathererStatus();

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
