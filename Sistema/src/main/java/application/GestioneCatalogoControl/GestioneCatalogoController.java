/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneCatalogoControl;

import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneControl.SearchResult;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
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
import storage.NavigazioneDAO.PhotoControl;

/**
 *
 * @author raffy
 */
@MultipartConfig
@WebServlet(name = "GestioneCatalogoController", urlPatterns = {"/GestioneCatalogoController"})
public class GestioneCatalogoController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private GestioneCatalogoServiceImpl gcs;
    private PaginationUtils pu;
    @Override
    public void init() throws ServletException {
        // Initialize any services or resources needed by the servlet
        gcs = new GestioneCatalogoServiceImpl();
        pu = new PaginationUtils();
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
            try{
                if (request.getParameter("page") != null)
                    page = Integer.parseInt(
                            request.getParameter("page"));
            } catch(Exception e) {
                Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, e);
                page=1;
            }
            
            Collection <ProxyProdotto> products = paginateProducts(request, page);
            
            // Set content type to JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Create a response object to hold products and total pages
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("products", products);
            responseData.put("hasNextPage", request.getSession().getAttribute("hasNextPage"));
            
            // Convert response object to JSON
            Gson gson = new Gson(); // Using Gson for JSON conversion
            String jsonResponse = gson.toJson(responseData);
            
            // Write JSON response
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();
            
        } catch(Exception ex) {
           Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, ex);
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
    private Collection<ProxyProdotto> paginateProducts(HttpServletRequest request, int page) throws SQLException, Exception{
        
        Collection <ProxyProdotto> currentPageResults;
        Collection <ProxyProdotto> nextPageResults;
        int previoslyFetchedPage = pu.getSessionAttributeAsInt(request, "previosly_fetched_page", 0);
            if(page==previoslyFetchedPage){                 
                currentPageResults = pu.getSessionCollection(request, "nextPageResults", ProxyProdotto.class);
                request.getSession().setAttribute("products", currentPageResults);              
            }
            else {
                currentPageResults = PaginationUtils.performPagination(
                gcs, page, pr_pagina);
                request.getSession().setAttribute("products", currentPageResults);                  
            }     
            nextPageResults = PaginationUtils.performPagination(
                 gcs, page+1, pr_pagina);
            
            request.getSession().setAttribute("nextPageResults", nextPageResults);
            request.getSession().setAttribute("previosly_fetched_page", page+1);
            
            boolean hasNextPage = pu.checkIfItsTheSamePage (currentPageResults, nextPageResults, ProxyProdotto.class);   
            request.getSession().setAttribute("hasNextPage", hasNextPage);
            return currentPageResults;
    }
    
    private static int pr_pagina = 50;       
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {   
        if(request.getParameter("action").matches("addPhoto")){      
            // Retrieve the file part from the request
           Part filePart = request.getPart("file"); // "file" is the name attribute in the form   

           int prod_id = Integer.parseInt(request.getParameter("prod_id"));

           if (filePart != null) {
               try {
                   // Get the input stream of the uploaded file
                   InputStream fileContent = filePart.getInputStream();
                   PhotoControl.updateTopImage(prod_id, fileContent);
                   response.sendRedirect("GestioneCatalogo");
               } catch (SQLException ex) {
                   Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, ex);
                   System.out.println(ex.getMessage());
               }

           }
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
