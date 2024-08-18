/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.NavigazioneControl;

import application.NavigazioneService.NavigazioneServiceImpl;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffy
 */
@WebServlet(name = "NavigazioneController", urlPatterns = {"/NavigazioneController","/TechHeaven"})
public class NavigazioneController extends HttpServlet {
    private static final long serialVersionUID = 1L; 
    
    public NavigazioneController() { super(); } 
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
            throws ServletException, IOException {
        /*
            Servlet per LA visualizzazione e paginazione dei risultati.
        */
        String keyword = (String)request.getParameter("keyword");
        if (keyword == null || keyword.isEmpty()) {
        // Handle the case where id parameter is missing
        // For example, you could return an error response or redirect the user
        System.out.println("keyword");
            response.sendRedirect("index.jsp");
            return;
        }  
        int page = 1;
        try {
            if (request.getParameter("page") != null) 
            page = Integer.parseInt( 
                request.getParameter("page")); 
        }catch(NumberFormatException e){
            page=1;
        }    
        SearchResult searchResult= null;
        // L'utility di Paginazione effettua la ricerca per tipo di Ricerca (barra - menu)
        // e compila il tutto in una classe searchResult che incapsula
        // i risultati della ricerca e il numero di risultati trovati.
        // Questi vengono passati al termine dell'elaborazione alla servlet 
        // dei risultati che si occuperà di Paginare il risultato della Ricerca.
        String searchType = request.getParameter("search_type");
        if(searchType!=null && searchType.contains("bar")){                   
            searchResult = PaginationUtils.performPagination(new NavigazioneServiceImpl(), keyword, page, 10,"bar");             
        }
        else if(searchType != null && searchType.contains("menu")){
            searchResult = PaginationUtils.performPagination(new NavigazioneServiceImpl(), keyword, page, 10,"menu");                
        }
        else if (searchType==null){
            response.sendRedirect("index.jsp");
            return;
        }
        request.getSession().setAttribute("searchResult", searchResult);
        request.getSession().setAttribute("keyword", keyword);
        response.sendRedirect("/ResultsPage");
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
