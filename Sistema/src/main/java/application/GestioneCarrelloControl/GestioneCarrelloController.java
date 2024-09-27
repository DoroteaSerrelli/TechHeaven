/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneCarrelloControl;

import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.CarrelloException;
import application.GestioneCarrelloService.GestioneCarrelloServiceImpl;
import application.GestioneCarrelloService.ItemCarrello;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProxyProdotto;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffy
 */
@WebServlet(name = "GestioneCarrelloController", urlPatterns = {"/GestioneCarrelloController"})
public class GestioneCarrelloController extends HttpServlet {
    private GestioneCarrelloServiceImpl gc;
    private ProdottoDAODataSource pdao;
    @Override
    public void init() throws ServletException {
        // Initialize any services or resources needed by the servlet
        pdao = new ProdottoDAODataSource();
        gc = new GestioneCarrelloServiceImpl();
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
            throws ServletException, IOException{          
            String action = request.getParameter("action");

            if (action == null) {
                // Default action or error handling
                response.getWriter().println("Invalid request");
            } else {
                switch (action) {
                    case "viewCart":
                        request.getSession().getAttribute("usercart");
                    break;
                    case "updateQuantità":
                        updateQuantità(request, response);
                    break;    
                    case "aggiungiAlCarrello":                        
                        aggiungiAlCarrello(request, response);
                    break;   
                    case "rimuoviDalCarrello":
                        rimuoviDalCarrello(request, response);
                    break;    
                    // Handle other actions...
                    default:
                        response.getWriter().println("Invalid action");
                }
            }
    }
    public void aggiungiAlCarrello(HttpServletRequest request, HttpServletResponse response) throws IOException{
            ItemCarrello inCart = null;
            HashMap hs = (HashMap) request.getSession().getAttribute("products_available_inStock");
            if(hs==null) hs = new HashMap();
            try{
                 // Parse request parameters
                int productId = parseProductId(request.getParameter("productId"));
                
                    ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);                   
                    //ItemCarrello prCheckinsCart = new ItemCarrello();
                    inCart = new ItemCarrello();
                    //prCheckinsCart.setCodiceProdotto(prodotto.getCodiceProdotto());
                    Carrello cart = getUserCart(request);
                    setInfoItemCarrello(prodotto, inCart);
                    
                    if(!cart.isPresent(inCart)){
                        gc.aggiungiAlCarrello(cart, inCart);
                        // Se l'ID del prodotto non è presente nella Mappa vuol dire che
                        // il Range non e' settato con la Q.Max di prodotto disponibile in magazzino.
                        if(!hs.containsKey(productId)){
                            hs.put(productId, prodotto.getQuantita());
                        }
                        double updatedPrice = inCart.getPrezzo() * inCart.getQuantita();
                        double totalAmount = cart.totalAmount();
                        request.getSession().setAttribute("error", "Item aggiunto nel carrello con successo");
                        request.getSession().setAttribute("status", "valid");
                        prepareJsonOutputMessage("valid", "Item aggiunto nel carrello con successo", updatedPrice, inCart.getQuantita(), totalAmount, request, response);                   
                    }
                    else {  
                        //response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                        request.getSession().setAttribute("error", "Item già inserito nel carrello");
                         request.getSession().setAttribute("status", "invalid");
                        prepareJsonOutputMessage("invalid", "Item già inserito nel carrello", 0, 0, cart.totalAmount(), request, response);                        //response.sendError(1, "Item già inserito nel carrello");
                        //return;
                    }
                /// Retrieving the product_left in stock amount to use for the range between 1 and max_amount left in stock - 80% (?)
                // you could theoretically change the amount based on how many max you want the user to have, documentation states that
                // it has to be max amount available.    
                request.getSession().setAttribute("products_available_inStock", hs); 
                request.getSession().setAttribute("usercart", cart);
                
        } catch (SQLException | CarrelloException.ProdottoPresenteException | CarrelloException.ProdottoNulloException | ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
            prepareJsonOutputMessage("invalid", ex.getMessage(), 0, 0, 0, request, response);                            
            response.sendRedirect(request.getContextPath() + "/cart");
        } 
    }
    
    public void updateQuantità(HttpServletRequest request, HttpServletResponse response) throws IOException{
        int productId = parseProductId(request.getParameter("productId"));
        int quantità = parseQuantity(request.getParameter("prod_quantità"));
        
        if(quantità>0){
            try {
                System.out.println("id_prodotto"+ productId);
                ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);
                ItemCarrello inCart = new ItemCarrello();
                Carrello cart = getUserCart(request);
                setInfoItemCarrello(prodotto, inCart);
                if(!cart.isPresent(inCart)){                    
                    gc.aggiungiAlCarrello(cart, inCart);
                }
                else{
                    System.out.println("quanittà nel carrello: "+inCart.getQuantita());                    
                    //inCart.setQuantita(qUpdated);
                    int quantità_deposito =  prodotto.getQuantita();
                    if(quantità<=quantità_deposito){
                        gc.aumentaQuantitaNelCarrello(cart, inCart, quantità);                        
                 //       System.out.println("quanittà input: "+quantità);
                 //       System.out.println("quanittà magazzino: "+quantità_deposito);
                        // Prepare the updated price and cart total for response
                        double updatedPrice = inCart.getPrezzo() * quantità;
                        double totalAmount = cart.totalAmount();
                        prepareJsonOutputMessage("valid", "Quantità modificata nel carrello con successo", updatedPrice, quantità, totalAmount, request, response);
                    }
                    else {
                        prepareJsonOutputMessage("invalid", "La quantià inserita supera le scorte presenti in magazzino", 0, 0, cart.totalAmount(), request, response);                    
                    }
                }
                ///Carrello svuotoTemp = new Carrello();
                request.getSession().setAttribute("usercart", cart);                
            } catch (CarrelloException.ProdottoNulloException | CarrelloException.ProdottoPresenteException | SQLException | CarrelloException.CarrelloVuotoException | CarrelloException.ProdottoNonPresenteException | CarrelloException.QuantitaProdottoException | ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
                Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
                prepareJsonOutputMessage("invalid", ex.getMessage(), 0, 0, 0, request, response);               
                response.sendRedirect(request.getContextPath() + "/cart");
            }
        }
    }
    
    public void rimuoviDalCarrello(HttpServletRequest request, HttpServletResponse response) throws IOException{
            try{
                String pid = request.getParameter("productId");
                HashMap hs = (HashMap) request.getSession().getAttribute("products_available_inStock"); 
                int productId = 0;
                if (pid != null && !pid.isEmpty()) {
                    productId = Integer.parseInt(pid);
                    // Proceed with your logic
                } else {}
                
                ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);
                ItemCarrello inCart = new ItemCarrello();
                setInfoItemCarrello(prodotto, inCart);
                Carrello cart = (Carrello)request.getSession().getAttribute("usercart");
                if(cart==null){
                    cart = new Carrello();
                }

                if(cart.isPresent(inCart)){
                    int quantità_deposito =  prodotto.getQuantita();
                    gc.rimuoviDalCarrello(cart, inCart);
                    //pdao.updateQuantity(productId,quantità_deposito+inCart.getQuantita());
                    System.out.println("quanittà magazzino: "+quantità_deposito);
                    // Rimuovo la Quantità Max in deposito dalla Mappa che gestisce il Range, usando l'ID 
                    // del prodotto come chiave.
                    hs.remove(productId);
                    // Prepare the updated cart total for response
                    double totalAmount = cart.totalAmount();
                    prepareJsonOutputMessage("valid", "Item rimosso con successo dal carrello", 0, 0, totalAmount, request, response);                }
                //Aggiorno la Mappa nella Sessione.
                request.getSession().setAttribute("products_available_inStock", hs);
                request.getSession().setAttribute("usercart", cart);

        } catch (SQLException | CarrelloException.ProdottoNulloException | CarrelloException.CarrelloVuotoException | CarrelloException.ProdottoNonPresenteException | ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
             prepareJsonOutputMessage("invalid", ex.getMessage(), 0, 0, 0, request, response);            
                
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
    
    public void setInfoItemCarrello(ProxyProdotto prodotto, ItemCarrello inCart){
         // Set product details in ItemCarrello
            inCart.setCodiceProdotto(prodotto.getCodiceProdotto());
            inCart.setNomeProdotto(prodotto.getNomeProdotto());
            inCart.setCategoria(prodotto.getCategoria());
            inCart.setMarca(prodotto.getMarca());
            inCart.setPrezzo(prodotto.getPrezzo());
            inCart.setModello(prodotto.getModello());
            inCart.setDettagli(prodotto.getTopDescrizione());
    }
    
   private void prepareJsonOutputMessage(String status, String msg, double updatedPrice, int updatedQuantity, double totalAmount, HttpServletRequest request, HttpServletResponse response) {
        try {
            // Prepare the data to be returned as JSON
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("message", msg);
            jsonResponse.put("status", status);
            jsonResponse.put("updatedPrice", String.format("%.2f", updatedPrice));  // Format the price to two decimal places
            jsonResponse.put("updatedQuantity", updatedQuantity);
            jsonResponse.put("totalAmount", String.format("%.2f", totalAmount));    // Format total amount to two decimal places

            // Convert the Map to JSON using Gson
            Gson gson = new Gson();
            String jsonResponseString = gson.toJson(jsonResponse);

            // Set response content type to JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Write the JSON response back to the client
            response.getWriter().write(jsonResponseString);
        } catch (IOException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    private int parseProductId(String pid) {
        if (pid != null && !pid.isEmpty()) {
            return Integer.parseInt(pid);
        }
        return 0;
    }

    private int parseQuantity(String q) {
        if (q != null && !q.isEmpty()) {
            return Integer.parseInt(q);
        }
        return 1;
    }

    private Carrello getUserCart(HttpServletRequest request) {
        Carrello cart = (Carrello) request.getSession().getAttribute("usercart");
        if (cart == null) {
            cart = new Carrello();
        }
        return cart;
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
