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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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
    public static SearchResult performPagination(NavigazioneServiceImpl productService, String keyword, int page, int resultsPerPage, String searchType) {
        ProdottoDAODataSource pdao = new ProdottoDAODataSource();
        SearchResult res = new SearchResult();
        List<ProxyProdotto> results;
        switch (searchType) {
            case "bar" -> {
                try {
                    results = productService.ricercaProdottoBar(keyword, page, resultsPerPage);
                    res.setProducts(results);
                    res.setTotalRecords(pdao.getTotalRecords("NOME"));
                    return res;
                } catch (SQLException ex) {
                    Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            case "menu" -> {
                try {
                    results = productService.ricercaProdottoMenu(Categoria.valueOf(keyword), page, resultsPerPage);
                    res.setProducts(results);
                    res.setTotalRecords(pdao.getTotalRecords("NOME"));
                    return res;
                } catch (SQLException ex) {
                    Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            default -> // Handle unknown search types or throw an exception
                throw new IllegalArgumentException("Invalid search type: " + searchType);
        }
        return null;
    }
    public static Collection<Prodotto> performPagination(GestioneCatalogoServiceImpl catalogoService, int page, int resultsPerPage) {
        return catalogoService.visualizzaCatalogo(page, resultsPerPage);      
    }
    
    public static Collection<ProxyOrdine> performPagination(GestioneOrdiniServiceImpl ordiniService, int page, int resultsPerPage, String action) throws SQLException {
        if(action.matches("fetch_da_spedire")){
           return ordiniService.visualizzaOrdiniDaEvadere(page, resultsPerPage); 
        }   
        else if(action.matches("fetch_spediti"))
            return ordiniService.visualizzaOrdiniEvasi(page, resultsPerPage);
        else return null;
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
