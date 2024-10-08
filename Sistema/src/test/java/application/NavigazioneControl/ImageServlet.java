/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.NavigazioneControl;

import application.GestioneCatalogoControl.ImageResizer;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProxyProdotto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.imaging.ImageFormat;
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
            String productId = request.getParameter("productId");
            
            if (productId == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing productId");
                return;
            }
            
            ProdottoDAODataSource pdao = new ProdottoDAODataSource();
            ProxyProdotto proxy = pdao.doRetrieveProxyByKey(Integer.parseInt(productId));
            Prodotto selectedProd = proxy.mostraProdotto();
            // Default placeholder image path
            String placeholderPath = getServletContext().getRealPath("/images/site_images/placeholder.png");
            
            BufferedImage imageToServe = null;
            
            if (selectedProd != null && selectedProd.getTopImmagine() != null) {
                byte[] topImageBytes = selectedProd.getTopImmagine();
                BufferedImage originalImage = ImageResizer.byteArrayToImage(topImageBytes);
                
                // Get resizing parameters from query
                int width = 400; // default width
                int height = 400; // default height
                
                try {
                    String widthParam = request.getParameter("width");
                    String heightParam = request.getParameter("height");
                    
                    if (widthParam != null) {
                        width = Integer.parseInt(widthParam);
                    }
                    if (heightParam != null) {
                        height = Integer.parseInt(heightParam);
                    }
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid width or height parameter");
                    return;
                }
                
                // Resize image to specified dimensions
                imageToServe = ImageResizer.resizeImage(originalImage, width, height);
            }
            
            if (imageToServe == null) {
                // Load and use placeholder image if no image to serve
                File placeholderFile = new File(placeholderPath);
                imageToServe = ImageIO.read(placeholderFile);
            }
            
            // Convert BufferedImage back to byte array
            byte[] imageBytes = ImageResizer.imageToByteArray(imageToServe, "jpg");
            
            // Set response content type
            response.setContentType("image/jpeg");
            
            try ( // Write image to response output stream
                    ServletOutputStream outStream = response.getOutputStream()) {
                outStream.write(imageBytes);
                outStream.flush();
            }
        } catch (SQLException | ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
            Logger.getLogger(ImageServlet.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }    
    
    // Method to convert byte[] to Base64 String
    public static String convertToBase64(byte[] imageBytes) {
        // Encode byte array into a Base64 string
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    
     public static List<String> loadGallery(Prodotto product) throws IOException {
        // Get the image gallery from Prodotto
        List<byte[]> galleryImages = product.getGalleriaImmagini(); 

        // Convert the byte[] images to base64 strings
        List<String> base64Gallery = new ArrayList<>();
        for (byte[] imageData : galleryImages) {
            if(imageData==null || imageData.length==0);
            else{
                // Detect image type (for simplicity, assume JPEG, but you can use libraries like Apache Tika)
                String base64Image = Base64.getEncoder().encodeToString(imageData);
                String mimeType = detectImageMimeType(imageData); // This method can be implemented to detect image type
                String imageUrl = "data:" + mimeType + ";base64," + base64Image;
                base64Gallery.add(imageUrl);
            }
        }
        return base64Gallery;
    }

    private static String detectImageMimeType(byte[] imageData) throws IOException {
        // Simple implementation using ImageIO to check image type
        InputStream is = new ByteArrayInputStream(imageData);
        BufferedImage bufferedImage = ImageIO.read(is);
        if (bufferedImage == null) {
            throw new IOException("Cannot detect image format");
        }
        String formatName = ImageIO.getImageReadersByFormatName("jpeg").hasNext() ? "jpeg" : "png"; // adjust as needed
        return "image/" + formatName;
    }
    // Static method to load presentation image for a product
    public static String loadPresentationPhoto(Prodotto product) throws IOException {
        // Get the image gallery directly from the Prodotto object
        byte[] presentationImage = product.getTopImmagine(); // Use the Prodotto method to get images

        // Convert the byte[] images to base64 strings
        String base64MainPhoto;
        
        String base64Image = Base64.getEncoder().encodeToString(presentationImage);
        String imageUrl = "data:image/jpeg;base64," + base64Image;
        base64MainPhoto= imageUrl;
        
        return base64MainPhoto;
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
