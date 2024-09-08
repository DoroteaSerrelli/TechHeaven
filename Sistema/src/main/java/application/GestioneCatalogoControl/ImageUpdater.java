/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneCatalogoControl;

import application.GestioneCatalogoService.CatalogoException;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
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
@MultipartConfig
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
        String action = (String) request.getParameter("action");
        
        // Retrieve the JSON product data from the request
        String productJson = request.getParameter("product"); 
        System.out.println(productJson);
        if (productJson != null) {
            Gson gson = new Gson();
            Prodotto product = gson.fromJson(productJson, Prodotto.class);
            if(action.equals("updateFotoPresentazione")){
                try {
                    String main_photoAction = (String) request.getParameter("main_photoAction");
                    // Retrieve the file part from the request
                    Part filePart = request.getPart("presentazione"); // "presentazione" is the name attribute in the form
                    InputStream fileContent = retrieveFileContent(filePart);
                    switch(main_photoAction){
                        case "update":
                            case"add":         
                                gcs.inserimentoTopImmagine(product, fileContent, 1, perPage);
                            break;
                        case "delete":                        
                            gcs.cancellazioneImmagineInGalleria(product, fileContent, 1, perPage);
                            break;
                    }
                } catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException ex) {
                    Logger.getLogger(ImageUpdater.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex.getMessage());
                }

            }
        }
       
       
    }
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
