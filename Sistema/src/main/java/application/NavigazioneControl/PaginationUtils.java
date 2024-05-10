/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.NavigazioneControl;

import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.SearchResult;
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

    public static void setPaginationAttributes(HttpServletRequest request, SearchResult searchResult, String keyword, int resultsPerPage) {
        int totalRecords = searchResult.getTotalRecords();
        int totalPages = (int) Math.ceil((double) totalRecords / resultsPerPage);

        request.setAttribute("products", searchResult.getProducts());
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
    }
}
