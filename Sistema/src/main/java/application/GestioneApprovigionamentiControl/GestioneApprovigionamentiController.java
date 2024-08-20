/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneApprovigionamentiControl;

import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiServiceImpl;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamento;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException;
import application.NavigazioneControl.SearchResult;
import application.NavigazioneService.ProxyProdotto;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffa
 */
public class GestioneApprovigionamentiController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private int perPage=50;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       String action = request.getParameter("action");
       int page = Integer.parseInt(request.getParameter("page"));
        if(action!=null && action.equals("viewProductList")){
           try {
               Collection <ProxyProdotto> all_products_list = pdao.doRetrieveAll(null, page, perPage);
               SearchResult sr = new SearchResult();
               sr.setProducts(all_products_list);
               sr.setTotalRecords(pdao.getTotalRecords(null));
               request.getSession().setAttribute("all_pr_list", sr);
               response.sendRedirect("Approvigionamento");
           } catch (SQLException ex) {
               Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
               request.getSession().setAttribute("error", "Recupero Prodotti Fallito");
           }
        }
               
       if(action!=null && action.equals("viewList")){
           try {
               Collection<RichiestaApprovvigionamento> supply_requests = gas.visualizzaRichiesteFornitura(page, perPage);
               request.getSession().setAttribute("supply_requests", supply_requests);
               response.sendRedirect("Approvigionamento");
           } catch (RichiestaApprovvigionamentoException.FornitoreException | RichiestaApprovvigionamentoException.DescrizioneDettaglioException | RichiestaApprovvigionamentoException.QuantitaProdottoException | RichiestaApprovvigionamentoException.ProdottoVendibileException | SQLException ex) {
               Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
               request.getSession().setAttribute("error", ex);
               //Servlet DO-GET GestioneOrdini TO-DO:
               response.sendRedirect("GestioneOrdini");
           }
       }
    }
    
    private ProdottoDAODataSource pdao;
    private GestioneApprovvigionamentiServiceImpl gas;
    @Override
    public void init() throws ServletException {
        // Initialize any services or resources needed by the servlet
        gas = new GestioneApprovvigionamentiServiceImpl();
        pdao = new ProdottoDAODataSource();
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
         String productIdParam = request.getParameter("product_id");

        if (productIdParam == null || productIdParam.isEmpty()) {
            // Handle the case where product_id is missing
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is required.");
            return;
        }
        try {
        
            int productId = Integer.parseInt(productIdParam); 
            ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            if(productId <=0 || quantity<=0) {
                request.getSession().setAttribute("error", "ID o Quantità non può essere inferiore a 1");
                response.sendRedirect("GestioneOrdini");
            }
        
        String fornitore = request.getParameter("fornitore");
        String email_fornitore = request.getParameter("email_fornitore");
        String descrizione = request.getParameter("descrizione");
        
        RichiestaApprovvigionamento supply = new RichiestaApprovvigionamento(fornitore, email_fornitore, descrizione, quantity, prodotto);
        gas.effettuaRichiestaApprovvigionamento(supply);
        } catch (NumberFormatException e) {
            // Handle invalid number format
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Product ID format.");
        } catch (SQLException | RichiestaApprovvigionamentoException.FornitoreException | RichiestaApprovvigionamentoException.QuantitaProdottoException | RichiestaApprovvigionamentoException.DescrizioneDettaglioException | RichiestaApprovvigionamentoException.ProdottoVendibileException ex) {
            Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
            request.getSession().setAttribute("error", "Richiesta approvigionamento non valida, c'è stato un errore.");
            response.sendRedirect("Approvigionamento");
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
