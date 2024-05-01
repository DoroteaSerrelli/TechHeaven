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
import application.NavigazioneService.ProxyProdotto;
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
                   // case "viewCart":
                     //   viewCart(request, response);
                    //    break;
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
    public void aggiungiAlCarrello(HttpServletRequest request, HttpServletResponse response){
            ItemCarrello inCart = null;
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
                    }
                    else {  
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        //response.sendError(1, "Item già inserito nel carrello");
                        return;
                    }
                    
                request.getSession().setAttribute("usercart", cart);
                
        } catch (SQLException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CarrelloException.ProdottoPresenteException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CarrelloException.ProdottoNulloException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void updateQuantità(HttpServletRequest request, HttpServletResponse response){
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
                    gc.aumentaQuantitaNelCarrello(cart, inCart, quantità);
                    
                    System.out.println("quanittà input: "+quantità);                    
                    
                }
                ///Carrello svuotoTemp = new Carrello();
                request.getSession().setAttribute("usercart", cart);                
            } catch (CarrelloException.ProdottoNulloException ex) {
                Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CarrelloException.ProdottoPresenteException ex) {
                Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CarrelloException.CarrelloVuotoException ex) {
                Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CarrelloException.ProdottoNonPresenteException ex) {
                Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CarrelloException.QuantitaProdottoException ex) {
                Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void rimuoviDalCarrello(HttpServletRequest request, HttpServletResponse response){
            try{
                String pid = request.getParameter("productId");
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

                if(cart.isPresent(inCart)) gc.rimuoviDalCarrello(cart, inCart);
                request.getSession().setAttribute("usercart", cart);

        } catch (SQLException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CarrelloException.ProdottoNulloException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CarrelloException.CarrelloVuotoException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CarrelloException.ProdottoNonPresenteException ex) {
            Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
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
