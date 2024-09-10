/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.NavigazioneControl;

import application.GestioneCatalogoControl.ImageResizer;
import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProxyProdotto;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Prodotto product = (Prodotto) request.getSession().getAttribute("product");
        List<String> galleryImages = (List<String>) request.getSession().getAttribute("galleryImages");

        if (product != null && galleryImages != null) {
            request.setAttribute("product", product);
            request.setAttribute("galleryImages", galleryImages);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/productDetails.jsp");
            dispatcher.forward(request, response);
        } else {
            // Redirect to a different page if no product is found
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action != null && action.equals("retrieveInfosForUpdate")) {
            handleProductUpdateRequest(request, response);
        } else {
            processProductDetails(request, response);
            response.sendRedirect(request.getContextPath() + "/ProductInfos"); // Redirect to GET request
        }
    }

    private void processProductDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String productJson = request.getParameter("product");
            if (productJson != null) {
                productJson = URLDecoder.decode(productJson, "UTF-8");
                Gson gson = new Gson();
                ProxyProdotto proxyProd = gson.fromJson(productJson, ProxyProdotto.class);
                Prodotto selectedProd = ns.visualizzaProdotto(proxyProd);
                List<String> resizedBase64Gallery = resizeAndProcessProductImages(selectedProd);
               
                request.getSession().setAttribute("product", selectedProd);
                request.getSession().setAttribute("galleryImages", resizedBase64Gallery);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product data is missing");
                return;
            }
        } catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
            Logger.getLogger(ProductInfos.class.getName()).log(Level.SEVERE, null, ex);
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }
    }

    private void handleProductUpdateRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String productJson = request.getParameter("product");
        if (productJson != null) {
            try {
                Gson gson = new Gson();
                ProxyProdotto proxyProd = gson.fromJson(productJson, ProxyProdotto.class);
                Prodotto selectedProd = ns.visualizzaProdotto(proxyProd);
                List<String> resizedBase64Gallery = resizeAndProcessProductImages(selectedProd);
                byte[] top_image_resized = ImageResizer.resizeTopImage(selectedProd, 400, 400);
                selectedProd.setTopImmagine(top_image_resized);
                // Add gallery to JSON response
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("product", selectedProd);
                responseData.put("base64Gallery", resizedBase64Gallery);

              //  request.getSession().setAttribute("galleryImages", base64Gallery);
                
                String jsonResponse = gson.toJson(responseData);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(jsonResponse);
                out.flush();
            } catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
                Logger.getLogger(ProductInfos.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product data is missing");
        }
    }
    // Utility Method that Resizes Product's Img Gallery
    // Returns Resized Gallery For Further Use. 
    private List<String> resizeAndProcessProductImages(Prodotto selectedProd) throws IOException{
        ArrayList<String> resizedBase64Gallery= ImageResizer.processGalleryAndConvertToBase64(selectedProd.getGalleriaImmagini());
        return resizedBase64Gallery;
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
