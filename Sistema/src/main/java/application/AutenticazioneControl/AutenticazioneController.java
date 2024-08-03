package application.AutenticazioneControl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import application.AutenticazioneService.AutenticazioneException;
import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Ruolo;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

@WebServlet(name = "AutenticazioneController", urlPatterns = {"/AutenticazioneController"})
public class AutenticazioneController extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AutenticazioneController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AutenticazioneController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        try {
            // Call loadUserAddresses when the page is accessed directly
            loadUserAddresses(request);
        } catch (SQLException ex) {
            Logger.getLogger(AutenticazioneController.class.getName()).log(Level.SEVERE, null, ex);
        }
         // Check if an action parameter is present and not empty
        String action = request.getParameter("action");
        if (action != null && !action.isEmpty()) {
            // Forward to updateUserInfo.jsp if action is specified
            if (action.equals("updateUserInfo")) {
                request.getRequestDispatcher("updateUserInfo.jsp").forward(request, response);
            }           
            // Add other actions if needed
        } else {
            // Forward to the default page (e.g., AreaRiservata.jsp) if no action is specified
            request.getRequestDispatcher("AreaRiservata.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private AutenticazioneServiceImpl loginService = new AutenticazioneServiceImpl();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String action = request.getParameter("action");
            if (action != null && !action.isEmpty()) {           
                if(action.equals("logout")){
                    request.getSession().invalidate();// Invalidate the session
                    response.sendRedirect("Autenticazione.jsp"); 
                    return;
                }
            }
            
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            ProxyUtente result;
            result = loginService.login(username, password);
            if (result!=null) {
                // Authentication successful
                request.getSession().setAttribute("user", result);
                loadUserAddresses(request);
                
                ArrayList<Ruolo> ruoli;
                ruoli = result.getRuoli();
                for(Ruolo r: ruoli){
                    System.out.println(r.getNomeRuolo());
                }
                
                request.getRequestDispatcher("AreaRiservata.jsp").forward(request,response);
            } else {
                // Authentication failed
                response.sendRedirect("Autenticazione.jsp?error=true");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AutenticazioneController.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("error", "password o username non corrette");
            response.sendRedirect("Autenticazione.jsp?error=true");
        } catch (AutenticazioneException.UtenteInesistenteException ex) {
            Logger.getLogger(AutenticazioneController.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("error",  "password o username non corrette");
            response.sendRedirect("Autenticazione.jsp?error=true");
        }
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
    public void loadUserAddresses(HttpServletRequest request) throws SQLException {
        ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
        if (u != null) {
            IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();
            ArrayList<Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
            request.setAttribute("Indirizzi", indirizzi); 

        }
    }
}
