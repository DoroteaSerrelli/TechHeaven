/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneOrdiniControl;

import application.GestioneCarrelloService.ItemCarrello;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdiniService.ObjectOrdine;
import application.GestioneOrdiniService.Ordine;
import application.GestioneOrdiniService.OrdineException;
import application.GestioneOrdiniService.ProxyOrdine;
import application.GestioneOrdiniService.ReportSpedizione;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProxyProdotto;
import application.PagamentoService.PagamentoException;
import application.RegistrazioneService.Cliente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffy
 */
@MultipartConfig
@WebServlet(name = "GestioneOrdiniController", urlPatterns = {"/GestioneOrdiniController"})
public class GestioneOrdiniController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private GestioneOrdiniServiceImpl gos;
    private OrdineDAODataSource odao;
    private ProdottoDAODataSource pdao;
    @Override
    public void init() throws ServletException {
        // Initialize any services or resources needed by the servlet
        odao = new OrdineDAODataSource();
        gos = new GestioneOrdiniServiceImpl();
        pdao = new ProdottoDAODataSource();
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet GestioneCatalogoController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GestioneCatalogoController at " + request.getContextPath() + "</h1>");
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
        try{
            //Visualizza prodotti selezionato forse il retrieve lo faccio con ajax <._.>
            int page = 1;
            PaginationUtils pu = new PaginationUtils ();
            String action = "";
            try{
                if (request.getParameter("page") != null)
                    page = Integer.parseInt(
                            request.getParameter("page"));
                if(request.getParameter("action") != null){
                    action = request.getParameter("action");
                    pu.detectActionChanges(request, action);
                    
                }
            } catch(Exception e) {
                Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, e);
                page=1;
            }
            
            // Perform pagination and fetch products for the requested page
            Collection<ProxyOrdine> orders_to_send = (Collection<ProxyOrdine>) request.getSession().getAttribute("products");
            if (orders_to_send == null) {
                // If the list is not in the session, fetch it again
                orders_to_send = PaginationUtils.performPagination(
                    new GestioneOrdiniServiceImpl(), page, pr_pagina, action);

                // Store it in the session for future requests
                request.getSession().setAttribute("products", orders_to_send);
            }
            int totalPages = (int) Math.ceil((double) orders_to_send.size() / pr_pagina);
            // Set content type to JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Create a response object to hold products and total pages
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orders", orders_to_send);
            responseData.put("totalPages", totalPages);
            
            // Convert response object to JSON
            GsonBuilder gsonBuilder = new GsonBuilder(); // Using Gson for JSON conversion
            gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
            gsonBuilder.registerTypeAdapter(LocalTime.class, new LocalTimeAdapter());
            
            Gson gson = gsonBuilder.create();
            String jsonResponse = gson.toJson(responseData);
            
            // Write JSON response
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();
            
        } catch(SQLException ex) {
           Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, ex);
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
    private static int pr_pagina = 50;       
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {   
            
            String action = request.getParameter("action");
            System.out.println(action);
            // Se action -> accept_order posta a true Setto lo stato dell'ordine in lavorazione e 
            // chiamo il Dispatcher della pagina fill_order_details.jsp per inserire le informazioni
            // sull'ordine i.e.(Aggiungere gli item richieste dall'ordine, la ditta di spedizione ecc...)          
            if(action.equals("accept_order")){
                try {
                    int orderID = Integer.parseInt(request.getParameter("orderId"));
                    System.out.println("ORDER_ID :"+orderID);
                    
                    ArrayList<ItemCarrello> order_products = odao.doRetrieveAllOrderProducts(orderID);
                    ProxyOrdine proxy_ordine = odao.doRetrieveProxyByKey(orderID);
                    
                    proxy_ordine.setStato(ObjectOrdine.Stato.In_lavorazione);
                    request.getSession().setAttribute("proxy_ordine", proxy_ordine);
                    request.getSession().setAttribute("selected_ordine", proxy_ordine.mostraOrdine());
                    
                    HashMap hs = new HashMap();
                    for(ItemCarrello item : order_products){
                        ProxyProdotto pr = pdao.doRetrieveProxyByKey(item.getCodiceProdotto());
                        hs.put(item.getCodiceProdotto(),  pr.getQuantita());                    
                    }
                    request.getSession().setAttribute("order_products", order_products);                  
                    request.setAttribute("order_products_available", hs);                    
                    
                    request.getRequestDispatcher("fill_order_details").forward(request, response);
                } catch (SQLException | OrdineException.OrdineVuotoException | ProdottoException.CategoriaProdottoException ex) {
                    Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
            else if(action.equals("complete_order")){             
                try {
                    ArrayList <ItemCarrello> carrello = (ArrayList <ItemCarrello>)request.getSession().getAttribute("order_products");
                    String[] productIds = request.getParameterValues("product_id");
                    String[] itemAmounts = request.getParameterValues("item_amount");
                    if (productIds != null && itemAmounts != null) {
                        boolean errorOccurred = false;
        
                        // Process each product in the order
                        for (int i = 0; i < productIds.length; i++) {
                            String productId = productIds[i];
                            int quantity = Integer.parseInt(itemAmounts[i]);

                            // Find the corresponding product in the cart
                            ItemCarrello item = carrello.stream()
                                .filter(c -> String.valueOf(c.getCodiceProdotto()).equals(productId))
                                .findFirst()
                                .orElse(null);

                        if (item != null) {
                            // Validate the quantity
                            if (quantity < item.getQuantita()) {
                                request.setAttribute("error", "La quantità non può essere inferiore alla quantità richiesta per il prodotto " + item.getNomeProdotto());
                                errorOccurred = true;
                                break;
                            }
                            // Update the item's quantity to be shipped
                            item.setQuantita(quantity);
                        }
                        }
                        if (errorOccurred) {
                            request.getRequestDispatcher("GestioneOrdini.jsp").forward(request, response);
                            return;
                        }
                    } 
                    
                    String imballaggio = request.getParameter("Imballaggio");
                    String corriere = request.getParameter("Corriere");
                                      
                    Ordine order= (Ordine)request.getSession().getAttribute("selected_ordine");
                    ProxyOrdine order_proxy = (ProxyOrdine)request.getSession().getAttribute("proxy_ordine");
                    
                    order_proxy.setStato(ObjectOrdine.Stato.Spedito);
                    order.setStato(ObjectOrdine.Stato.Spedito);
                    order.setProdotti(carrello);
                    
                    ReportSpedizione report = new ReportSpedizione(order_proxy.getCodiceOrdine(), corriere, imballaggio, order_proxy);          
                
                    
                    gos.preparazioneSpedizioneOrdine(order, report);
                    request.getSession().setAttribute("error", "Ordine Spedito Con Successo!");
                    
                    request.getSession().removeAttribute("proxy_ordine");                    
                    request.getSession().removeAttribute("selected_ordine");
                    request.getSession().removeAttribute("order_products");
                    
                    response.sendRedirect("GestioneOrdini");                 
                    
                } catch(IOException | NumberFormatException | SQLException e){} catch (OrdineException.ErroreSpedizioneOrdineException | OrdineException.OrdineVuotoException | PagamentoException.ModalitaAssenteException | CloneNotSupportedException ex) {
                    Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex);
                  //  request.getRequestDispatcher("fill_order_details").forward(request, response);
                  //  request.setAttribute("error","C'è stato un errore durante la preparazione dell'ordine");
                    request.getSession().removeAttribute("selected_ordine");
                    
                } catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
                    Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex);
                }
            }
            else{
                Ordine ordine = (Ordine)request.getSession().getAttribute("selected_ordine");
                ordine.setStato(ObjectOrdine.Stato.Preparazione_incompleta);
                request.getSession().removeAttribute("selected_ordine");
                request.getSession().removeAttribute("order_products");
                response.sendRedirect("GestioneOrdini");  
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
