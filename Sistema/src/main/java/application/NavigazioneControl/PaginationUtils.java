/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.NavigazioneControl;

import application.GestioneApprovigionamentiControl.GestioneApprovigionamentiController;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamento;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdiniService.Ordine;
import application.GestioneOrdiniService.ProxyOrdine;
import application.NavigazioneService.NavigazioneService;
import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProxyProdotto;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffy
 */
public class PaginationUtils {
    public static Collection<ProxyProdotto> performPagination(NavigazioneServiceImpl productService, String keyword, int page, int resultsPerPage, String searchType) throws SQLException {
        ProdottoDAODataSource pdao = new ProdottoDAODataSource();
        SearchResult res = new SearchResult();
        Collection<ProxyProdotto> results;
        switch (searchType) {
            case "bar" -> {
                try {
                    results = productService.ricercaProdottoBar(keyword, page, resultsPerPage);
                    return results;
                } catch (ProdottoException.SottocategoriaProdottoException ex) {
                    Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ProdottoException.CategoriaProdottoException ex) {
                    Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }

            case "menu" -> {
                try {
                    results = productService.ricercaProdottoMenu(Categoria.valueOf(keyword), page, resultsPerPage);
                    return results;
                } catch (SQLException ex) {
                    Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ProdottoException.CategoriaProdottoException ex) {
                Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ProdottoException.SottocategoriaProdottoException ex) {
                Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
            }

            default -> // Handle unknown search types or throw an exception
                throw new IllegalArgumentException("Invalid search type: " + searchType);
        }
        return null;
    }
    public static Collection<ProxyProdotto> performPagination(GestioneCatalogoServiceImpl catalogoService, int page, int resultsPerPage) {
        try {      
            return catalogoService.visualizzaCatalogo(page, resultsPerPage);
        } catch (SQLException | ProdottoException.CategoriaProdottoException | ProdottoException.SottocategoriaProdottoException ex) {
            Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Collection<ProxyOrdine> performPagination(GestioneOrdiniServiceImpl ordiniService, int page, int resultsPerPage, String action) throws SQLException {
        if(action.matches("fetch_da_spedire")){
           return ordiniService.visualizzaOrdiniDaEvadere(page, resultsPerPage); 
        }   
        else if(action.matches("fetch_spediti"))
            return ordiniService.visualizzaOrdiniEvasi(page, resultsPerPage);
        else return null;
    }  
    
    NavigazioneService navi_service = new NavigazioneServiceImpl();
    public void paginateSearchedProducts(HttpServletRequest request, int page, int resultsPerPage,String keyword, String searchType){
       //searchType ---> pseudo_action                 
       request.getSession().setAttribute("page", page);
        // Fetch the previosly_fetched_page being the last page retrieved in the flow of instruction:
        // nextPageItems = > (if page==previous nextPage) I don't need to retrieve the items from the db
        // as I already have them available inside the session.
       int previoslyFetchedPage = getSessionAttributeAsInt(request, "previosly_fetched_page", 0);
        Collection <ProxyProdotto> currentPageResults;
        Collection <ProxyProdotto> nextPageResults;
           try {            
               if(page==previoslyFetchedPage){                 
                   currentPageResults = getSessionCollection(request, "nextPageResults", ProxyProdotto.class);
                   request.getSession().setAttribute("products", currentPageResults);              
               }
               else {
                   if(searchType!=null && searchType.equals("bar"))
                       currentPageResults = navi_service.ricercaProdottoBar(keyword, page, resultsPerPage);
                   else 
                        currentPageResults = navi_service.ricercaProdottoMenu(Categoria.valueOf(keyword), page, resultsPerPage);
                   
                   request.getSession().setAttribute("products", currentPageResults);                  
               }          
               if(searchType!=null && searchType.equals("bar"))
                       nextPageResults = navi_service.ricercaProdottoBar(keyword, page+1, resultsPerPage);
                   else 
                        nextPageResults = navi_service.ricercaProdottoMenu(Categoria.valueOf(keyword), page+1, resultsPerPage);
                   
               request.getSession().setAttribute("nextPageResults", nextPageResults);
               
               request.getSession().setAttribute("previosly_fetched_page", page+1);
               
               boolean hasNextPage = checkIfItsTheSamePage (currentPageResults, nextPageResults, ProxyProdotto.class);                               
               request.getSession().setAttribute("hasNextPage", hasNextPage);
               request.getSession().setAttribute("keyword", keyword);
           } catch (SQLException ex) {
               Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
               request.getSession().setAttribute("error", "Recupero Prodotti Fallito");
           } catch (Exception ex) {
               Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
           }
        }   
    
     // Utility method to retrieve session attribute as an Integer with a default value if null.
    public int getSessionAttributeAsInt(HttpServletRequest request, String attributeName, int defaultValue) {
        Integer value = (Integer) request.getSession().getAttribute(attributeName);
        return value != null ? value : defaultValue;
    }
    
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getSessionCollection(HttpServletRequest request, String attributeName, Class<T> type) {
        Collection<T> collection = (Collection<T>) request.getSession().getAttribute(attributeName);
        if (collection == null) {
            collection = new ArrayList<>();
        }
        return collection;
    }
    public Integer getId(Object item, Class<?> clazz) throws Exception {
        // Dynamically determine the method based on the class type
        // Determine the method name based on the class type
        String methodName;
        if (clazz == ProxyProdotto.class) {
            methodName = "getCodiceProdotto";
        } else if (clazz == ProxyOrdine.class) {
            methodName = "getCodiceOrdine";
        } else if (clazz == RichiestaApprovvigionamento.class) {
            methodName = "getCodiceRifornimento";
        } else {
            throw new IllegalArgumentException("Unsupported class type: " + clazz.getName());
        }      
        java.lang.reflect.Method method = clazz.getMethod(methodName);
        Object result = method.invoke(item);
        return result != null ? (Integer) result : null;
    }
    //Metodo che verifica se sto osservando la stessa pagina
    public  <T> boolean checkIfItsTheSamePage(Collection <T> currentPageItems, Collection <T> nextPageItems, Class<T> clazz) throws Exception{       
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
    
    // This method retrieve the parameter action and last_action from the session that keeps track
    // of the last selected action if they are different this means the user selected something different
    // and I need to reset session attributes related to the other action to avoid trobules with pagination.
    public void detectActionChanges(HttpServletRequest request, String action){
        String lastAction = (String) request.getSession().getAttribute("last_action");

        if (lastAction == null || !lastAction.equals(action)) {
            // Action has changed, reset all session attributes related to pagination
            request.getSession().removeAttribute("previosly_fetched_page");
            request.getSession().removeAttribute("nextPageResults");
            request.getSession().removeAttribute("products");
            request.getSession().removeAttribute("hasNextPage");
        }

        // Update the session with the current action
        request.getSession().setAttribute("last_action", action);
    }
}


  /*In caso di "si voglia" cambiare l'implementazione lascio questi metodi commentati che includon getTotalRecords.
    public static void setPaginationAttributes(HttpServletRequest request, SearchResult searchResult, String keyword, int resultsPerPage) {
        int totalRecords = searchResult.getTotalRecords();
        int totalPages = (int) Math.ceil((double) totalRecords / resultsPerPage);

        request.setAttribute("products", searchResult.getProducts());
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
        
    }
    public static void setPaginationAttributes(HttpServletRequest request, SearchResult searchResult, int resultsPerPage) {
        int totalRecords = searchResult.getTotalRecords();
        int totalPages = (int) Math.ceil((double) totalRecords / resultsPerPage);

        request.setAttribute("products", searchResult.getProducts());
        request.setAttribute("totalPages", totalPages);        
    }*/