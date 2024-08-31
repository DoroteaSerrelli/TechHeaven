/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.NavigazioneControl;

import application.NavigazioneService.ProxyProdotto;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffa
 */
@WebServlet(name = "ResultsPage", urlPatterns = "/ResultsPage")
public class ResultsPage extends HttpServlet {

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
            out.println("<title>Servlet ResultsPage</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ResultsPage at " + request.getContextPath() + "</h1>");
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
        Collection<ProxyProdotto> searchResult = (Collection<ProxyProdotto>) request.getSession().getAttribute("products");
        if(searchResult==null){
            String keyword = (String) request.getParameter("keyword");
            response.sendRedirect("NavigazioneController?keyword="+keyword);
            return;
        }
        String keyword = (String) request.getSession().getAttribute("keyword");
        request.setAttribute("keyword", keyword);
   //     PaginationUtils.setPaginationAttributes(request, searchResult, keyword, 10);
        request.getSession().getAttribute("search_type");
        request.setAttribute("page",(int)request.getSession().getAttribute("page"));
        request.setAttribute("hasNextPage", request.getSession().getAttribute("hasNextPage"));
        
        /// Retrieving the product_left in stock amount to use for the range between 1 and max_amount left in stock - 80% (?)
        // you could theoretically change the amount based on how many max you want the user to have, documentation states that
        // it has to be max amount available.
        HashMap hs = new HashMap();
        for(ProxyProdotto item : searchResult){
            hs.put(item.getCodiceProdotto(),  item.getQuantita());                    
        }
        request.getSession().setAttribute("products_available_inStock", hs); 
        // Forward to JSPorder_products_available
        request.getRequestDispatcher("searchResults.jsp").forward(request, response);
        
        //Clear Session Attributes
        //request.getSession().removeAttribute("searchResult");
        //request.getSession().removeAttribute("keyword");
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
