/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.AutenticazioneControl;

import application.AutenticazioneService.AutenticazioneException;
import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Utente;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffy
 */
@WebServlet(name = "UpdateAddressController", urlPatterns = {"/UpdateAddressController"})
public class UpdateAddressController extends HttpServlet {

    AutenticazioneServiceImpl as;
    ProxyUtente user;
    
    @Override
    public void init() throws ServletException {
        // Initialize any services or resources needed by the servlet       
        as = new AutenticazioneServiceImpl();
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
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UpdateAddressController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateAddressController at " + request.getContextPath() + "</h1>");
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
        try {
            String action = request.getParameter("action");
            
            String via = request.getParameter("newVia");
            String numCivico = request.getParameter("newNumCivico");
            String cap = request.getParameter("newCap");
            String citta = request.getParameter("newCitta");
            String provincia = request.getParameter("newProvincia");
           
            Indirizzo target_ind = new Indirizzo(via, numCivico, citta, cap, provincia);
            
            ProxyUtente user = getUser(request);
            //If the user is null sends a redirect to login page.
            if(user==null) {
                response.sendRedirect(request.getContextPath() + "/Autenticazione");
                return;
            }
            
            ProxyUtente updated_user = user;
            
            //Check if the email and phone number is the same one as before;
            Utente real_user = user.mostraUtente();
            
            switch(action){
                case "UpdateIndirizzo":
                    int id_indirizzo = Integer.parseInt(request.getParameter("addressIndex"));
                    target_ind.setIDIndirizzo(id_indirizzo);
                    updated_user= as.updateAddressBook(user, "AGGIORNARE-INDIRIZZO", target_ind);
                break; 
                case "AddIndirizzo":
                    updated_user= as.updateAddressBook(user, "AGGIUNGERE-INDIRIZZO", target_ind);
                break; 
                case "RemoveIndirizzo":
                    id_indirizzo = Integer.parseInt(request.getParameter("addressIndex"));
                    target_ind.setIDIndirizzo(id_indirizzo);
                    
                    System.out.println("ID:"+target_ind.getIDIndirizzo());
                    
                    updated_user= as.updateAddressBook(user, "RIMUOVERE-INDIRIZZO", target_ind);
                break; 
                default:
                    System.out.println("Errore azione indirizzo non valida");
                break;    
                
            }
            request.getSession().setAttribute("user", updated_user);
            response.sendRedirect(request.getContextPath() + "/AreaRiservata");      
            
            
        } catch (AutenticazioneException.UtenteInesistenteException | AutenticazioneException.IndirizzoEsistenteException | AutenticazioneException.FormatoIndirizzoException | SQLException | AutenticazioneException.ModificaIndirizzoException | AutenticazioneException.InformazioneDaModificareException ex) {
            
            Logger.getLogger(UpdateAddressController.class.getName()).log(Level.SEVERE, null, ex);
            String errormsg = "Errore durante la modifica delle informazioni";
            request.getSession().setAttribute("error", ex.getMessage());
            //Retrieve address after update failure to allow the user to see them and update them 
            // If needed.
            response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");          
            
        }
            
            
            
    }
    private ProxyUtente getUser(HttpServletRequest request) {
        ProxyUtente user = (ProxyUtente) request.getSession().getAttribute("user");
        if (user == null) {
            return null;
        }
        return user;
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
