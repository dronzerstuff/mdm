/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mdmserver.controller;

import com.google.gson.Gson;
import com.mdmserver.constant.WebResponseCodeConstants;
import com.mdmserver.managers.DatabaseManager;
import com.mdmserver.model.AppPackage;
import com.mdmserver.web.model.RegisterRequest;
import com.mdmserver.web.model.RegisterResponse;
import com.mdmserver.web.model.AppAnalyticsResponse;
import com.mdmserver.web.model.AppAnalyticsRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author avin
 */
@WebServlet(name = "AppAnalytics", urlPatterns = {"/AppAnalytics"})
public class AppAnalytics extends HttpServlet {

    @EJB
    DatabaseManager dbManager;
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
        
        //if you reached this point then you are already validated as admin
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json");
        Gson gson= new Gson();
        String accountId= request.getParameter("id");
        AppAnalyticsResponse rResponse= new AppAnalyticsResponse();
        if(accountId==null){
            rResponse.setResponseCode(WebResponseCodeConstants.RESP_INVALID_REQUEST); 
            rResponse.setResponseMessage(WebResponseCodeConstants.RESP_INVALID_REQUEST_MSG);
        }
        else{
            
            try{
                 int accountIdparsed= Integer.parseInt(accountId);
                 List<AppPackage> appPackages= dbManager.getAppPackagesForAccount(accountIdparsed);
                 rResponse.setAppPackages(appPackages);
                rResponse.setResponseCode(WebResponseCodeConstants.RESP_SUCCESS);
                rResponse.setResponseMessage(WebResponseCodeConstants.RESP_SUCCESS_MSG);
            }catch(NumberFormatException e){
                rResponse.setResponseCode(WebResponseCodeConstants.RESP_INVALID_REQUEST); 
                rResponse.setResponseMessage(WebResponseCodeConstants.RESP_INVALID_REQUEST_MSG);
            }
        }
        
        response.getWriter().print(gson.toJson(rResponse));
        
        
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
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json");
        Gson gson= new Gson();
        AppAnalyticsRequest rRequest =gson.fromJson(request.getReader(), AppAnalyticsRequest.class);
        AppAnalyticsResponse rResponse= new AppAnalyticsResponse();
        if(null==rRequest.getAccount()){
             rResponse.setResponseCode(WebResponseCodeConstants.RESP_INVALID_REQUEST); 
             rResponse.setResponseMessage(WebResponseCodeConstants.RESP_INVALID_REQUEST_MSG);
        }
        else{
            //this means the user wants to fetch the list
            if(null==rRequest.getAppPackages()){
                List<AppPackage> appPackages= dbManager.getAppPackagesForAccount(rRequest.getAccount());
                rResponse.setAppPackages(appPackages);
                rResponse.setResponseCode(WebResponseCodeConstants.RESP_SUCCESS);
                rResponse.setResponseMessage(WebResponseCodeConstants.RESP_SUCCESS_MSG);
            }
            //this means to update the lise
            else{
                int internalResultCode= dbManager.updateAppPackagesForAccount(rRequest.getAccount(), rRequest.getAppPackages());
                switch(internalResultCode){
                    case AppPackage.RETURN_CODE_SUCCESS:
                        rResponse.setResponseCode(WebResponseCodeConstants.RESP_SUCCESS);
                        rResponse.setResponseMessage(WebResponseCodeConstants.RESP_SUCCESS_MSG);
                    break;
                    case AppPackage.RETURN_CODE_ERROR_DOES_NOT_EXIST:
                        rResponse.setResponseCode(WebResponseCodeConstants.RESP_NO_USER_EXISTS);
                        rResponse.setResponseMessage(WebResponseCodeConstants.RESP_NO_USER_EXISTS_MSG);
                    default:
                        rResponse.setResponseCode(WebResponseCodeConstants.RESP_INVALID_REQUEST); 
                        rResponse.setResponseMessage(WebResponseCodeConstants.RESP_INVALID_REQUEST_MSG);
                    break;

                }
            }
        }
        
        
//        response.getWriter().println("size of app packages"+rRequest.getAppPackages().size());
        
        response.getWriter().print(gson.toJson(rResponse));
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
