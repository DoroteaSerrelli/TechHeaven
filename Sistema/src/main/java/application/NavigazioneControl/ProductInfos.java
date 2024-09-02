/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.NavigazioneControl;

import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProxyProdotto;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffa
 */
public class ProductInfos extends HttpServlet {

    NavigazioneServiceImpl ns;
     public void init() throws ServletException {
        // Initialize any services or resources needed by the servlet
        ns = new NavigazioneServiceImpl();
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
         String productJson = request.getParameter("product");
        if (productJson != null) {
             try {
                 productJson = URLDecoder.decode(productJson, "UTF-8");
                 Gson gson = new Gson();
                 ProxyProdotto proxy_prod = gson.fromJson(productJson, ProxyProdotto.class);
                 Prodotto selected_prod = ns.visualizzaProdotto(proxy_prod);
                 
                 // Checks whetever or not the action is set up or not this way it can give back
                 // product's full details to Catalog Manager Form for further Elaborations.
                 if(request.getParameter("action")!=null){
                    String action = request.getParameter("action");
                    if(action.equals("retrieveInfosForUpdate")){
                        String jsonResponse = gson.toJson(selected_prod);
                         response.setContentType("application/json"); // Ensure content type is set to JSON
                        // Write JSON response
                        PrintWriter out = response.getWriter();
                        out.print(jsonResponse);
                        out.flush();                      
                        
                    }
                 }
                 else{
                    // Process product object
                    request.setAttribute("product", selected_prod);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/productDetails.jsp");
                    dispatcher.forward(request, response);
                 }
             } catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
                 Logger.getLogger(ProductInfos.class.getName()).log(Level.SEVERE, null, ex);
                 response.sendRedirect(request.getContextPath() + "/index");
             }
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
