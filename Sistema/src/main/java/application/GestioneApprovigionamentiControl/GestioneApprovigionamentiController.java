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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
        String action = retrieveActionAndDetectChanges(request);
        // Continue with your logic
       
       int page = Integer.parseInt(request.getParameter("page"));
       request.getSession().setAttribute("action", action);
       request.getSession().setAttribute("page", page);
        // Fetch the previosly_fetched_page being the last page retrieved in the flow of instruction:
        // nextPageItems = > (if page==previous nextPage) I don't need to retrieve the items from the db
        // as I already have them available inside the session.
       int previoslyFetchedPage = getSessionAttributeAsInt(request, "previosly_fetched_page", 0);
       
        if(action!=null && action.equals("viewProductList")){
           try {
               Collection <ProxyProdotto> currentPageResults;
               Collection <ProxyProdotto> nextPageResults;
               if(page==previoslyFetchedPage){                 
                   currentPageResults = getSessionCollection(request, "nextPageResults", ProxyProdotto.class);
                   request.getSession().setAttribute("products", currentPageResults);              
               }
               else {
                   currentPageResults = pdao.doRetrieveAll(null, page, perPage);
                   request.getSession().setAttribute("products", currentPageResults);                  
               }               
               nextPageResults = pdao.doRetrieveAll(null, page+1, perPage);
               request.getSession().setAttribute("nextPageResults", nextPageResults);
               
               request.getSession().setAttribute("previosly_fetched_page", page+1);
               
               boolean hasNextPage = checkIfItsTheSamePage (currentPageResults, nextPageResults, ProxyProdotto.class);   
                            
               request.getSession().setAttribute("hasNextPage", hasNextPage);
               response.sendRedirect("Approvigionamento");
           } catch (SQLException ex) {
               Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
               request.getSession().setAttribute("error", "Recupero Prodotti Fallito");
           } catch (Exception ex) {
               Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
               
       if(action!=null && action.equals("viewList")){
           try {                                                    
                // Use a generic method to get the collection
                Collection<RichiestaApprovvigionamento> supplyRequests;
                Collection <RichiestaApprovvigionamento> nextPageResults;
               
               if(page==previoslyFetchedPage){                 
                   //Datas about the current supply_request gets stored to compare it with the next Page data to make sure it's not
                   //the same page being fetched, and disabling navigation control.
                   supplyRequests = getSessionCollection(request, "nextPageResults", RichiestaApprovvigionamento.class);
                  // Handle the case where the session attribute is null               
                   // Store the current page data in the session that being the previously fetched that in this case.
                   request.getSession().setAttribute("supply_requests", supplyRequests);              
               }
               else {
                   //I need to retrieve the data for supply_requests from the db othervise.
                   supplyRequests = gas.visualizzaRichiesteFornitura(page, perPage);
                   request.getSession().setAttribute("supply_requests", supplyRequests);                  
               }               
               
               //Retrieving the nextPage data from the db and setting it as nextPageResults:
               nextPageResults = gas.visualizzaRichiesteFornitura(page+1, perPage);
               request.getSession().setAttribute("nextPageResults", nextPageResults);
               
               //Setting the previosly_fetched page attribute inside the session to the value of nextPage. 
               request.getSession().setAttribute("previosly_fetched_page", page+1);
               
               //Verifico se le pagine sono identiche in caso affermativo il valore viene settato a false.
               // Impedendo nella jsp la navigazione alla prossima pagina.          
               // Get current and next page items
               boolean hasNextPage = checkIfItsTheSamePage (supplyRequests, nextPageResults, RichiestaApprovvigionamento.class);   
                            
               request.getSession().setAttribute("hasNextPage", hasNextPage);
               
               response.sendRedirect("Approvigionamento");
           } catch (RichiestaApprovvigionamentoException.FornitoreException | RichiestaApprovvigionamentoException.DescrizioneDettaglioException | RichiestaApprovvigionamentoException.QuantitaProdottoException | RichiestaApprovvigionamentoException.ProdottoVendibileException | SQLException ex) {
               Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
               request.getSession().setAttribute("error", ex);
               //Servlet DO-GET GestioneOrdini TO-DO:
               response.sendRedirect("GestioneOrdini");
           } catch (Exception ex) {
               Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }
    // This method retrieve the parameter action and last_action from the session that keeps track
    // of the last selected action if they are different this means the user selected something different
    // and I need to reset session attributes related to the other action to avoid trobules with pagination.
    private String retrieveActionAndDetectChanges(HttpServletRequest request){
        String action = request.getParameter("action");
        String lastAction = (String) request.getSession().getAttribute("last_action");

        if (lastAction == null || !lastAction.equals(action)) {
            // Action has changed, reset all session attributes related to pagination
            request.getSession().removeAttribute("previosly_fetched_page");
            request.getSession().removeAttribute("nextPageResults");
            request.getSession().removeAttribute("supply_requests");
            request.getSession().removeAttribute("hasNextPage");
        }

        // Update the session with the current action
        request.getSession().setAttribute("last_action", action);
        return action;
    }
    
    // Utility method to retrieve session attribute as an Integer with a default value if null.
    private int getSessionAttributeAsInt(HttpServletRequest request, String attributeName, int defaultValue) {
        Integer value = (Integer) request.getSession().getAttribute(attributeName);
        return value != null ? value : defaultValue;
    }
    
    @SuppressWarnings("unchecked")
    private <T> Collection<T> getSessionCollection(HttpServletRequest request, String attributeName, Class<T> type) {
        Collection<T> collection = (Collection<T>) request.getSession().getAttribute(attributeName);
        if (collection == null) {
            collection = new ArrayList<>();
        }
        return collection;
    }
    private Integer getId(Object item, Class<?> clazz) throws Exception {
        // Dynamically determine the method based on the class type
        String methodName = clazz == RichiestaApprovvigionamento.class ? "getCodiceRifornimento" : "getCodiceProdotto";
        java.lang.reflect.Method method = clazz.getMethod(methodName);
        Object result = method.invoke(item);
        return result != null ? (Integer) result : null;
    }
    
    //Metodo che verifica se sto osservando la stessa pagina
    private  <T> boolean checkIfItsTheSamePage(Collection <T> currentPageItems, Collection <T> nextPageItems, Class<T> clazz) throws Exception{       
        Integer currentPageItemId = 1;
        Integer nextPageItemId = 1;
        // Using Generic Types to avoid redundant code we retrieve the first item of each Collection.
        // Extract the first item from each collection (changes based on the action attribute)
        T firstCurrentPageItem = currentPageItems.isEmpty() ? null : currentPageItems.iterator().next();
        T firstNextPageItem = nextPageItems.isEmpty() ? null : nextPageItems.iterator().next();

        ///We retrieve the first item identifier based on the Collection class.
        if (firstCurrentPageItem != null) {
        currentPageItemId = getId(firstCurrentPageItem, clazz);
        }

        if (firstNextPageItem != null) {
            nextPageItemId = getId(firstNextPageItem, clazz);
        }      
        // Debugging: Print IDs
        System.out.println("Current Page Item ID: " + currentPageItemId);
        System.out.println("Next Page Item ID: " + nextPageItemId);

        // Check if the first item ID of the next page is the same as the first item ID of the current page
        boolean isSameAsCurrentPage = currentPageItemId != null && currentPageItemId.equals(nextPageItemId);

        // Set hasNextPage based on whether nextPageItems is empty or has the same first item ID as currentPageItems
        return nextPageItems != null && !nextPageItems.isEmpty() && !isSameAsCurrentPage;
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
        System.out.println(fornitore);
        RichiestaApprovvigionamento supply = new RichiestaApprovvigionamento(fornitore, email_fornitore, descrizione, quantity, prodotto);
        gas.effettuaRichiestaApprovvigionamento(supply);
        request.getSession().setAttribute("error", "Richiesta Approvigionamento Avvenuta Con Successo!");            
        response.sendRedirect("Approvigionamento");
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
