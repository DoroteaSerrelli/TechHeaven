/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneCarrelloControl;

import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.ItemCarrello;
import application.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdiniService.ObjectOrdine.Stato;
import application.GestioneOrdiniService.ObjectOrdine.TipoSpedizione;
import application.GestioneOrdiniService.Ordine;
import application.GestioneOrdiniService.OrdineException;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
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
public class CheckoutControl extends HttpServlet {
    GestioneOrdiniServiceImpl gos;
    @Override
    public void init(){
        gos = new GestioneOrdiniServiceImpl();
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
            out.println("<title>Servlet CheckoutCarrello</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CheckoutCarrello at " + request.getContextPath() + "</h1>");
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
        String action = request.getParameter("action");
        if(action!=null && !action.isEmpty()){
            if(action.equals("annullaPagamento")){
                request.getSession().removeAttribute("preview_order");
                //Redirect alla pagina Iniziale:
                response.sendRedirect(request.getContextPath()+"/");
                return;
            }
        
        }
        ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
        if (u==null || u.getUsername().equals("")) {
           response.sendRedirect(request.getContextPath() + "/Autenticazione");
           return;
        }   
        Carrello c = (Carrello) request.getSession().getAttribute("usercart");
        if (c==null || c.getNumProdotti()==0){
            response.sendRedirect(request.getContextPath() + "/Autenticazione");
           return;    
        }
        // Retrieve data from request or session if needed
        ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>) request.getAttribute("Indirizzi");
        if(indirizzi==null){
            try {
                loadUserAddresses(request, u);
            } catch (SQLException ex) {
                Logger.getLogger(CheckoutControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Forward to JSP
        request.getRequestDispatcher("CompletaOrdine").forward(request, response);
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
        String action = request.getParameter("action");
        if(action.equals("confirmOrder"))
            elaborateCheckoutRequest(request, response);
    }
    
    private void elaborateCheckoutRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        try {
            int idIndirizzo = Integer.parseInt(request.getParameter("selectedAddress"));
            ProxyUtente user = (ProxyUtente)request.getSession().getAttribute("user");
            Map<Integer, Indirizzo> addressMap = (Map<Integer, Indirizzo>) request.getSession().getAttribute("addressMap");
            Indirizzo selectedAddress = addressMap.get(idIndirizzo);
            Carrello cart = (Carrello) request.getSession().getAttribute("usercart");
            String tipo_spedizione = request.getParameter("tipoSpedizione");
            Ordine preview_order = new Ordine(1, null, selectedAddress, TipoSpedizione.valueOf(tipo_spedizione), user.mostraUtente().getProfile(), (ArrayList<ItemCarrello>) cart.getProducts());
            request.getSession().setAttribute("preview_order", preview_order);
            
            response.sendRedirect(request.getContextPath()+"/Pagamento");
            
        } catch (OrdineException.OrdineVuotoException ex) {
            Logger.getLogger(CheckoutControl.class.getName()).log(Level.SEVERE, null, ex);
            request.getSession().setAttribute("error", ex);            
            response.sendRedirect(request.getContextPath()+"/CheckoutCarrello");           
        }
    }
    
    private void loadUserAddresses(HttpServletRequest request, ProxyUtente u) throws SQLException {
        IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();
        ArrayList<Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
        request.setAttribute("Indirizzi", indirizzi); 
       
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
