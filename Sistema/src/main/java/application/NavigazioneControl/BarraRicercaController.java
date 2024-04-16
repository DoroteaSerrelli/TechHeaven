/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.NavigazioneControl;

import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProxyProdotto;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffy
 */
@WebServlet(name = "BarraRicercaController", urlPatterns = {"/BarraRicercaController"})
public class BarraRicercaController extends HttpServlet {

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
          String searched_prod = request.getParameter("keyword");
       // Check if productIdString is null or empty
        if (searched_prod == null || searched_prod.isEmpty()) {
        // Handle the case where id parameter is missing
        // For example, you could return an error response or redirect the user
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID parameter is missing");
            return;
        }  
        try {
            NavigazioneServiceImpl productService = new NavigazioneServiceImpl();
            List<ProxyProdotto> prodotti = productService.ricercaProdottoBar(searched_prod);
            
            request.setAttribute("prodottiFound", prodotti);
        }
        catch(NumberFormatException bn){}
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
