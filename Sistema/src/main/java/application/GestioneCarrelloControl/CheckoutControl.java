/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneCarrelloControl;

import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.CarrelloException;
import application.GestioneCarrelloService.ItemCarrello;
import application.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdiniService.ObjectOrdine;
import application.GestioneOrdiniService.ObjectOrdine.TipoSpedizione;
import application.GestioneOrdiniService.Ordine;
import application.GestioneOrdiniService.OrdineException;
import application.PagamentoService.Pagamento;
import application.PagamentoService.PagamentoCartaCredito;
import application.PagamentoService.PagamentoContrassegno;
import application.PagamentoService.PagamentoException;
import application.PagamentoService.PagamentoPaypal;
import application.PagamentoService.PagamentoServiceImpl;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
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
    PagamentoServiceImpl ps;
    @Override
    public void init(){
        gos = new GestioneOrdiniServiceImpl();
        ps = new PagamentoServiceImpl();
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
        if(action.equals("confirmPayment"))
            finalizeOrder(request, response);
    }
    
    private boolean processPayment(HttpServletRequest request, Pagamento pagamento, ProxyUtente user, Carrello cart, Ordine preview_order){
          // If validation is successful, proceed with payment processing...                                    
        try {
            preview_order.setStato(ObjectOrdine.Stato.Richiesta_effettuata);
            gos.commissionaOrdine(cart, preview_order, pagamento, user);
            request.getSession().removeAttribute("usercart");
            request.getSession().removeAttribute("preview_order");            
            return true;
            // Handle other payment methods (e.g., Paypal)
        } catch (SQLException | CarrelloException.ProdottoNonPresenteException | CarrelloException.CarrelloVuotoException | CarrelloException.ProdottoNulloException | OrdineException.OrdineVuotoException | PagamentoException.ModalitaAssenteException | CloneNotSupportedException ex) {
            Logger.getLogger(CheckoutControl.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
            request.getSession().setAttribute("error", ex.getMessage());        
            return false;
        }  
    }
    
    private boolean validateCreditCardPayment(HttpServletRequest request, String titolare, String ccNumber, String ccExpiry, String ccCVC) throws IOException{
        // Server-side validation
        if (titolare == null || ccNumber == null || ccExpiry == null || ccCVC == null ||
            titolare.isEmpty() || ccNumber.isEmpty() || ccExpiry.isEmpty() || ccCVC.isEmpty()) {
            request.getSession().setAttribute("error", "Inserisci tutte le informazioni della carta di credito.");            
            return false;
        }
        // Check if card expiry is valid
        String[] expiryParts = ccExpiry.split("/");
        int expMonth = Integer.parseInt(expiryParts[0]);
        int expYear = Integer.parseInt(expiryParts[1]);

        java.util.Calendar currentDate = java.util.Calendar.getInstance();
        int currentYear = currentDate.get(java.util.Calendar.YEAR);
        int currentMonth = currentDate.get(java.util.Calendar.MONTH) + 1; // Months are 0-based

        if (expYear < currentYear || (expYear == currentYear && expMonth < currentMonth)) {
            request.getSession().setAttribute("errorMessage", "La carta di credito è scaduta.");
            return false;
        }
      return true;
}
    private void finalizeOrder(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String metodoPagamento = request.getParameter("metodoPagamento");
        Ordine preview_order = (Ordine) request.getSession().getAttribute("preview_order");
        Carrello cart = (Carrello) request.getSession().getAttribute("usercart");
        ProxyUtente user = (ProxyUtente) request.getSession().getAttribute("user");
        Pagamento pagamento = null;
        Boolean success = false ;
        switch(metodoPagamento){
            case "CreditCard":
                // Extract credit card details from the request
                String titolare = request.getParameter("titolare");
                String ccNumber = request.getParameter("cc_number");
                String ccExpiry = request.getParameter("cc_expiry");
                String ccCvc = request.getParameter("cc_cvc");
                if(!validateCreditCardPayment(request, titolare, ccNumber, ccExpiry, ccCvc)){
                    response.sendRedirect(request.getContextPath()+"/Pagamento");
                    return;
                }
                // Initialize the credit card payment object
                pagamento= new PagamentoCartaCredito(1, preview_order, (float) cart.totalAmount(), titolare, ccNumber);
                success = processPayment(request, pagamento, user, cart, preview_order);                  
            break;
            case "Paypal":
                pagamento = new PagamentoPaypal(1, preview_order, (float) cart.totalAmount());
                success = processPayment(request, pagamento, user, cart, preview_order);
            break;    
            case "Contrassegno":
                pagamento = new PagamentoContrassegno(1, preview_order, (float) cart.totalAmount());
                success = processPayment(request, pagamento, user, cart, preview_order);
            break;    
        }
        if(!success){
            response.sendRedirect(request.getContextPath()+"/Pagamento");
            return;
        }
        response.sendRedirect(request.getContextPath()+"/AreaRiservata");
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
            preview_order.setData(LocalDate.now());
            preview_order.setOra(LocalTime.now()); 
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