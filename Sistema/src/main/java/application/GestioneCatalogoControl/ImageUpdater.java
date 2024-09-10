/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneCatalogoControl;

import application.GestioneCatalogoService.CatalogoException;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneControl.ImageServlet;
import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author raffa
 */
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,  // 1MB
    maxFileSize = 1024 * 1024 * 10,   // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class ImageUpdater extends HttpServlet {
    private GestioneCatalogoServiceImpl gcs;
    private NavigazioneServiceImpl ns;
    @Override
    public void init() throws ServletException {
        // Initialize any services or resources needed by the servlet
        gcs = new GestioneCatalogoServiceImpl();
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
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ImageUpdater</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ImageUpdater at " + request.getContextPath() + "</h1>");
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
    private int perPage= 50;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {      
        // Retrieve the JSON product data from the request
        String productJson = request.getParameter("product"); 
        if (productJson != null) {
            Gson gson = new Gson();
            Prodotto product = gson.fromJson(productJson, Prodotto.class);
               try {
                    String main_photoAction = (String) request.getParameter("main_photoAction");
                    
                    ArrayList<byte[]> galleriaImmagini = product.getGalleriaImmagini();
                    String gallery_photoActions = (String) request.getParameter("gallery_photoActions");                  
                    if(main_photoAction!=null){
                        switch(main_photoAction){
                         case "update":
                            case "add":
                                 // Retrieve the file part from the request
                                Part filePart = request.getPart("presentazione"); // "presentazione" is the name attribute in the form
                                InputStream fileContent = retrieveFileContent(filePart);
                                gcs.inserimentoTopImmagine(product, fileContent, 1, perPage);
                                product.setTopImmagine(inputStreamToByteArray(fileContent));
                            break;                  
                        default:
                            // Handle unknown or missing action
                            throw new IllegalArgumentException("Unexpected value: " + main_photoAction);
                        }
                    }
                  
                    if (gallery_photoActions != null) {
                        switch (gallery_photoActions) {
                            case "delete":
                                // Retrieve image index and path
                                String imageIndexStr = request.getParameter("imageIndex");
                                int imageIndex = Integer.parseInt(imageIndexStr);
                                byte[] imageToRemove = product.getGalleriaImmagini().get(imageIndex);
                                System.out.println(imageIndex);
                                System.out.println(imageToRemove);
                                InputStream imageStream = new ByteArrayInputStream(imageToRemove);
                                 // Assuming you have a method to delete an image from the product's gallery by its index or identifier
                                gcs.cancellazioneImmagineInGalleria(product, imageStream, 1, perPage);
                                
                                galleriaImmagini.remove(imageIndex);
                                updateGalleriaImmaginiLocale(product, galleriaImmagini, response);
                                
                                break;
                            case "addToGallery":
                                 // Retrieve the file part from the request
                                Part filePart = request.getPart("presentazione"); // "presentazione" is the name attribute in the form
                                InputStream fileContent = retrieveFileContent(filePart);
                                gcs.inserimentoImmagineInGalleriaImmagini(product, fileContent, 1, perPage);
                                updateGalleriaImmaginiLocale(product, galleriaImmagini, response);
                                
                                // Updating The Local Gallery: That will Get Passed to JSON as Return Value
                                // To Update Gallery UI.
                                galleriaImmagini.add(inputStreamToByteArray(fileContent));
                                
                                break;
                            default:
                                throw new IllegalArgumentException("Unexpected gallery action: " + gallery_photoActions);
                        }
                    }         
                } catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException ex) {
                    Logger.getLogger(ImageUpdater.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex.getMessage());
                }       
        }     
       
    }
    
    private void updateGalleriaImmaginiLocale(Prodotto product, ArrayList<byte[]> galleriaImmagini, HttpServletResponse response) throws IOException{
        
        product.setGalleriaImmagini(galleriaImmagini);        
        Gson gson = new Gson();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("product", product);
        responseData.put("galleryImages", ImageServlet.loadGallery(product));
        
        String jsonResponse = gson.toJson(responseData);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
    
    //Utility Method to Write An Input Stream Into A Byte Array.
    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    // Utility Method to Convert a Byte Array into An Input Stream
    private InputStream byteArrayToInputStream(byte[] byteArray) {
        return new ByteArrayInputStream(byteArray);
    }
    // Utility Method to Retrieve Input Stream From a FilePart when Received 
    // From a Form
    private InputStream retrieveFileContent(Part filePart) throws IOException{
        InputStream fileContent = null;
        if (filePart != null) {
            // Get the input stream of the uploaded file
            //gcs.inserimentoTopImmagine(product, image, 0, 0)
            fileContent = filePart.getInputStream();          
        } 
        return fileContent;
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
