/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.AutenticazioneControl;

import application.AutenticazioneService.AutenticazioneException;
import application.AutenticazioneService.AutenticazioneServiceImpl;
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
@WebServlet(name = "UpdateProfileController", urlPatterns = {"/UpdateProfileController"})
public class UpdateProfileController extends HttpServlet {
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
        try { 
            String updated_email = (String)request.getParameter("email");
            String updated_tel =   (String)request.getParameter("telefono");
            
            ProxyUtente user = getUser(request);
            ProxyUtente updated_user = user;
            
            //Check if the email and phone number is the same one as before;
            Utente real_user = user.mostraUtente();
            if( real_user.getProfile().getEmail().equals(updated_email) || real_user.getProfile().getTelefono().equals(updated_tel)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.sendRedirect("AreaRiservata.jsp");
                return;
            }
            
           // Check which fields the user wants to update
            if (updated_email != null && !updated_email.isEmpty()) {
              updated_user = as.updateProfile(user, "EMAIL", updated_email);     
            }
            if (updated_tel != null &&  !updated_tel.isEmpty()) {
               updated_user = as.updateProfile(updated_user, "TELEFONO", updated_tel);
            }
                         
            request.getSession().setAttribute("user", updated_user);
            request.getRequestDispatcher("AreaRiservata.jsp").forward(request, response);      
            
        }catch(SQLException e){
           Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, e);      
        } catch (AutenticazioneException.FormatoEmailException ex) {
            Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AutenticazioneException.ProfiloInesistenteException ex) {
            Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AutenticazioneException.EmailEsistenteException ex) {
            Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AutenticazioneException.TelefonoEsistenteException ex) {
            Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AutenticazioneException.FormatoTelefonoException ex) {
            Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AutenticazioneException.InformazioneDaModificareException ex) {
            Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private ProxyUtente getUser(HttpServletRequest request) {
        ProxyUtente user = (ProxyUtente) request.getSession().getAttribute("user");
        if (user == null) {
            return null;
        }
        return user;
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