/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneApprovigionamentiControl;

import application.GestioneApprovvigionamenti.RichiestaApprovvigionamento;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneControl.SearchResult;
import application.NavigazioneService.ProxyProdotto;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffa
 */
public class Approvigionamento extends HttpServlet {

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
            out.println("<title>Servlet Approvigionamento</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Approvigionamento at " + request.getContextPath() + "</h1>");
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
    private int perPage=50;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if(request.getSession().getAttribute("action").equals("viewProductList")){
            //Checking if the value if the user accesses Approvigionamento directly.
            if(request.getSession().getAttribute("products")==null){
                response.sendRedirect("GestioneApprovigionamentiController?action=viewProductList&page=1");
                return;
            }
            Collection<ProxyProdotto> all_products_list = (Collection<ProxyProdotto>) request.getSession().getAttribute("products");
            request.setAttribute("all_pr_list", all_products_list);
            int page = (int)request.getSession().getAttribute("page");
            request.setAttribute("page", page);
            boolean hasNextPage = (boolean)request.getSession().getAttribute("hasNextPage");
            request.setAttribute("hasNextPage", hasNextPage);
            request.getRequestDispatcher("protected/gestoreOrdini/approvigionamento.jsp").forward(request, response);
        }     
        else{
            if(request.getSession().getAttribute("supply_requests")==null){
                response.sendRedirect("GestioneApprovigionamentiController?action=viewList&page=1");
                return;
            }
            Collection<RichiestaApprovvigionamento> supply_requests = (Collection<RichiestaApprovvigionamento>) request.getSession().getAttribute("supply_requests");        
            request.setAttribute("supply_requests", supply_requests);   
            int page = (int)request.getSession().getAttribute("page");
            request.setAttribute("page", page);
            boolean hasNextPage = (boolean)request.getSession().getAttribute("hasNextPage");
            request.setAttribute("hasNextPage", hasNextPage);
            request.getRequestDispatcher("protected/gestoreOrdini/richiesteApprovigionamento.jsp").forward(request, response);
        }
        String error = (String)request.getSession().getAttribute("error");
        request.getSession().removeAttribute("error");
        request.setAttribute("error", error);
              
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
