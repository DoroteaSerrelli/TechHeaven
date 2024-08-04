/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.NavigazioneControl;

import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdiniService.Ordine;
import application.GestioneOrdiniService.ProxyOrdine;
import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author raffy
 */
public class PaginationUtils {
    public static SearchResult performPagination(NavigazioneServiceImpl productService, String keyword, int page, int resultsPerPage, String searchType) {
        if (searchType.equals("bar")) {
        return productService.ricercaProdottoBar(keyword, page);
        
    } else if (searchType.equals("menu")) {
        
        return productService.ricercaProdottoMenu(Categoria.valueOf(keyword), page);
    } else {
        // Handle unknown search types or throw an exception
        throw new IllegalArgumentException("Invalid search type: " + searchType);
     }
    }
    public static Collection<Prodotto> performPagination(GestioneCatalogoServiceImpl catalogoService, int page, int resultsPerPage) {
        return catalogoService.visualizzaCatalogo(page, resultsPerPage);      
    }
    
    public static Collection<ProxyOrdine> performPagination(GestioneOrdiniServiceImpl ordiniService, int page, int resultsPerPage, String action) {
        if(action.matches("fetch_da_spedire")){
           return ordiniService.visualizzaOrdinidaSpedire(page, resultsPerPage); 
        }   
        else return ordiniService.visualizzaOrdiniSpediti(page, resultsPerPage);
    }
    
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
        
    }
}
