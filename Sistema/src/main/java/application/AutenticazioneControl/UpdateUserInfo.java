/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.AutenticazioneControl;

import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

/**
 *
 * @author raffa
 */
public class UpdateUserInfo extends HttpServlet {

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
        ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
        if (u==null || u.getUsername().equals("")) {
           response.sendRedirect(request.getContextPath() + "Autenticazione");
           return;
        }    
        // Retrieve data from request or session if needed
        ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>) request.getAttribute("Indirizzi");
        if(indirizzi==null){
            try {
                loadUserAddresses(request, u);
            } catch (SQLException ex) {
                Logger.getLogger(AreaRiservata.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Forward to JSP
        request.getRequestDispatcher("protected/cliente/updateUserInfo.jsp").forward(request, response);
    }
    private void loadUserAddresses(HttpServletRequest request, ProxyUtente u) throws SQLException {
        IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();
        ArrayList<Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
        request.setAttribute("Indirizzi", indirizzi); 
       
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
