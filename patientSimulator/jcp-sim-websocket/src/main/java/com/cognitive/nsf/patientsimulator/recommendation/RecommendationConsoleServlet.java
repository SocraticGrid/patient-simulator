/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.patientsimulator.recommendation;

import com.cognitive.bp.poc.model.ToggleService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author esteban
 */
@WebServlet(name = "RecommendationConsoleServlet", urlPatterns = {"/RecommendationConsoleServlet"})
public class RecommendationConsoleServlet extends HttpServlet {

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
                case "toggle":
                    String service = request.getParameter("service");
                    String value = request.getParameter("value");
                    result = this.toggle(service, value);
                    break;
                case "getStatus":
                    result = this.getStatus();
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

    private JsonElement toggle(String service, String value) {
        ToggleService.SERVICE s = ToggleService.SERVICE.valueOf(service);
        try {
            RecommendationContextListener.recommendationSystem.toggleService(s, "ON".equalsIgnoreCase(value));
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonParser().parse("{status: 'error', message: '" + e.getMessage().replaceAll("'", "") + "'}");
        }
        return this.getStatus();
    }

    private JsonElement getStatus() {
        JsonObject alertService = new JsonObject();
        alertService.addProperty("enabled", RecommendationContextListener.recommendationSystem.areAlertsActive());
        
        JsonObject genomicService = new JsonObject();
        genomicService.addProperty("enabled", RecommendationContextListener.recommendationSystem.isGenomeServiceActive());

        
        JsonObject config = new JsonObject();
        config.add("alertService", alertService);
        config.add("genomicService", genomicService);

        return config;
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
