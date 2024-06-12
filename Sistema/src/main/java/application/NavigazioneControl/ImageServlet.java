/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.NavigazioneControl;

import application.NavigazioneService.ProxyProdotto;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;

import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffa
 */
public class ImageServlet extends HttpServlet {

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
        try {
            // Retrieve image data from the database based on product ID
            String productId = request.getParameter("productId");
            ProdottoDAODataSource pdao = new ProdottoDAODataSource();
            ProxyProdotto product = pdao.doRetrieveProxyByKey(Integer.parseInt(productId));
            byte[] imageData = product.mostraProdotto().getTopImmagine();
            
            if (imageData != null) {
                // Set content type header
                response.setContentType(getImageFormat(imageData)); // Adjust the content type based on the image format
                
                // Write image data to the response output stream
                try (OutputStream out = response.getOutputStream()) {
                    out.write(imageData);
                }
            } else {
                // Handle case when image data is not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImageServlet.class.getName()).log(Level.SEVERE, null, ex);
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

    private String getImageFormat(byte[] imageData) {
         try {
            // Detect the image format based on the image data's file signature
            ImageFormat format = Imaging.guessFormat(imageData);

            // Map the detected format to a standard image format name
            if (format != null) {
                String formatName = format.getName();
                // Convert the format name to lowercase to match content type conventions
                return formatName.toLowerCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions (e.g., if the image data is invalid)
        }
        // If the format cannot be determined, default to JPEG
        return "jpeg";
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
