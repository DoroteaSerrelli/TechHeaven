/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneWishlistControl;

import application.GestioneWishlistService.GestioneWishlistServiceImpl;
import application.GestioneWishlistService.Wishlist;
import application.GestioneWishlistService.WishlistException;
import application.NavigazioneService.ProxyProdotto;
import application.RegistrazioneService.ProxyUtente;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import storage.WishlistDAO.WishlistDAODataSource;

/**
 *
 * @author raffa
 */
public class GestioneWishlistController extends HttpServlet {
    private ProdottoDAODataSource pdao;
    private GestioneWishlistServiceImpl gws;
    @Override
    public void init() throws ServletException {
        // Initialize any services or resources needed by the servlet
        pdao = new ProdottoDAODataSource();
        gws = new GestioneWishlistServiceImpl();
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
            
            ProxyUtente user = (ProxyUtente) request.getSession().getAttribute("user");              
            if (user==null || user.getUsername().equals("")) {
               response.sendRedirect("Autenticazione");
               return ;
           }
            request.getSession().setAttribute("errormsg", null);                           
            String action = request.getParameter("action");
            if (action == null) {
                // Default action or error handling
                response.getWriter().println("Invalid request");
            } else {
                switch (action) {
                    case "viewwishlist":
                    {
                        Wishlist w = createNewWishlistIfNotExists(request, user);
                    }
                    break;    
                    case "addtowishlist":        
                       try { 
                           Wishlist w = createNewWishlistIfNotExists(request, user);
                            // Parse request parameters
                            int productId = parseProductId(request.getParameter("productId"));
                            ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);   
                            
                            w = gws.aggiungiProdottoInWishlist(w, prodotto, user);
                            request.getSession().setAttribute("Wishlist", w);
                            
                        } catch (WishlistException.ProdottoPresenteException | WishlistException.ProdottoNulloException | SQLException ex) {
                            Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
                            request.getSession().setAttribute("errormsg", "Item già Presente nella Wishlist");
                            response.sendRedirect("Wishlist");
                            return;
                        }
                    break;   
                    case "removefromwishlist":
                        try { 
                           Wishlist w = createNewWishlistIfNotExists(request, user);
                            // Parse request parameters
                            int productId = parseProductId(request.getParameter("productId"));
                            System.out.println(productId);
                            ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);   
                            
                            w = gws.rimuoviDallaWishlist(w, user, prodotto);
                            request.getSession().setAttribute("Wishlist", w);
                           
                        } catch (WishlistException.ProdottoNonPresenteException | WishlistException.ProdottoNulloException | SQLException | WishlistException.WishlistVuotaException ex) {
                            Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
                            request.getSession().setAttribute("errormsg", "Rimozione Item Fallita");
                            response.sendRedirect("Wishlist");
                            return;
                        }
                    break;    
    
    
                    // Handle other actions...
                    default:
                        response.getWriter().println("Invalid action");
                }
            }
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/Wishlist");
    }
     /**
      * Questo metodo tenta di recuperare la Wishlist dalla sessione nel caso in
      * cui questa sia nulla, cerca di recuperarla dal DB se nel database non è presente
      * e viene lanciata l'eccezione che segnala la non presenza della wishlist relativa
      * a quell'utente ne crea una nuova e restituisce la wishlist risultante.
      * @param request
      * @param user
      * @return la wishlist relativa all'utente.
      */
    private Wishlist createNewWishlistIfNotExists(HttpServletRequest request, ProxyUtente user){
        try {
            WishlistDAODataSource wdao = new WishlistDAODataSource();
            Wishlist w = (Wishlist)request.getSession().getAttribute("Wishlist");
            int check_user_wishlist =0;
            if(w==null){
                check_user_wishlist = wdao.getWishlistCount(user); 
                if(check_user_wishlist==0){
                    w =  new Wishlist(user);
                    w.setId(check_user_wishlist+1);
                    wdao.doSaveWishlist(w);
                }
                else{
                     Collection<Wishlist> wishlists = wdao.doRetrieveAllWishesUser("",user);
                    if (!wishlists.isEmpty()) {
                        Iterator<Wishlist> iterator = wishlists.iterator();
                        if (iterator.hasNext()) {
                            w = iterator.next();
                        }
                    }
                }
            }
            request.getSession().setAttribute("Wishlist", w);
            return w;
        } catch (SQLException ex) {
           Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
           System.out.println(ex.getMessage());
        }
        return null;
    } 
    private int parseProductId(String pid) {
        if (pid != null && !pid.isEmpty()) {
            return Integer.parseInt(pid);
        }
        return 0;
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
