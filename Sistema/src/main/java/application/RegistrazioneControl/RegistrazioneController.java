package application.RegistrazioneControl;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.RegistrazioneServiceImpl;
import application.RegistrazioneService.Utente;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

@WebServlet(name = "RegistrazioneController", urlPatterns = {"/RegistrazioneController"})
public class RegistrazioneController extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RegistrazioneController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegistrazioneController at " + request.getContextPath() + "</h1>");
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
       RegistrazioneServiceImpl reg = new RegistrazioneServiceImpl();
       String username= request.getParameter("username");
       String password= request.getParameter("password");
       String email= request.getParameter("email");
       String nome= request.getParameter("name");
       String cognome= request.getParameter("surname");
       String telefono= request.getParameter("phoneNumber");
       
       String via= request.getParameter("road");
       String cv= request.getParameter("cv");
       String citta= request.getParameter("city");
       String cap= request.getParameter("cap");
       String provincia= request.getParameter("province");
       String sesso= request.getParameter("sesso");
       Indirizzo indirizzo = new Indirizzo(via, cv, citta, cap, provincia);
       ProxyUtente u= reg.registraCliente(username, password, email, nome, cognome, Cliente.Sesso.valueOf(sesso), telefono, indirizzo);
       if(u==null)  {response.sendRedirect(request.getContextPath() + "/Registrazione");}
       else{
           IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();
           try {
               ArrayList <Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());        
               request.getSession().setAttribute("user", u);
               request.setAttribute("Indirizzi",indirizzi);
               response.sendRedirect(request.getContextPath() + "/AreaRiservata");
           } catch (SQLException ex) {
               Logger.getLogger(RegistrazioneController.class.getName()).log(Level.SEVERE, null, ex);
           }
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

}
