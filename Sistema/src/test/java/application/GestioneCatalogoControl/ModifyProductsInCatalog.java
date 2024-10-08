/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package application.GestioneCatalogoControl;

import application.GestioneCatalogoService.CatalogoException;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffa
 */
public class ModifyProductsInCatalog extends HttpServlet {
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
            out.println("<title>Servlet ModifyProductsInCatalog</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ModifyProductsInCatalog at " + request.getContextPath() + "</h1>");
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
        String action = null;
        if(request.getParameter("action") != null){
            action = request.getParameter("action");
            request.getSession().setAttribute("action", action);
        }
        
        if(action==null) request.getSession().setAttribute("action", "modify");
        response.sendRedirect(request.getContextPath()+"/UpdateProductInfos");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static int pr_pagina = 50; 
    private Gson gson = new Gson(); // Initialize Gson
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       StringBuilder jsonBuilder = new StringBuilder();
    try (BufferedReader reader = request.getReader()) {
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
    }
    String jsonData = jsonBuilder.toString();
    //Stampa DEBUG RICEZIONE DATI JSON
    //System.out.println("Received JSON Data: " + jsonData);

    // Parse the JSON
    Gson gson = new Gson();
    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);

    JsonObject modifiedData = jsonObject.getAsJsonObject("modifiedData");
    JsonObject originalProductDetails = jsonObject.getAsJsonObject("originalProductDetails");
   // String productId = jsonObject.get("productId").getAsString();

    // Log parsed data
    //System.out.println("Modified Data: " + modifiedData);
    //System.out.println("Original Product Details: " + originalProductDetails);
    //System.out.println("Product ID: " + productId);
        // Now you can use modifiedDataJson, originalProductJson, and productId as needed
        // For example, you can deserialize these JSON strings into Java objects:

        
        // Call the method to update product information
        String jsonResponse = updateProductInfos(modifiedData, originalProductDetails, request, response);
        // Set response content type and encoding
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }

    private String updateProductInfos(JsonObject modifiedDataJson, JsonObject originalProductJson, HttpServletRequest request, HttpServletResponse response) 
        throws IOException {
        String outputMessage = "";
        Gson gson = new Gson();
        Map<String, String> responseMap = new HashMap<>();
        // Deserialize the original product JSON into a Prodotto object
        Prodotto originalProduct = null;
        if (originalProductJson != null && !originalProductJson.isJsonNull()) {
            originalProduct = gson.fromJson(originalProductJson, Prodotto.class);
        }

        // Proceed only if there's modified data
        if (modifiedDataJson != null && !modifiedDataJson.isJsonNull()) {
            // Parse the modified data JSON into a Map
            Map<String, Map<String, String>> modifiedData = gson.fromJson(modifiedDataJson, Map.class);

            // Update product details if present in the modified data
            if (modifiedData.containsKey("productDetails")) {
                Map<String, String> productDetails = modifiedData.get("productDetails");
                outputMessage += updateProductDetail("MARCA", "marca", originalProduct, productDetails);
                outputMessage += updateProductDetail("MODELLO", "modello", originalProduct, productDetails);
            }

            // Update descriptions if present in the modified data
            if (modifiedData.containsKey("descriptions")) {
                Map<String, String> descriptions = modifiedData.get("descriptions");
                outputMessage += updateProductDetail("DESCRIZIONE_EVIDENZA", "topDescrizione", originalProduct, descriptions);
                outputMessage += updateProductDetail("DESCRIZIONE_DETTAGLIATA", "dettagli", originalProduct, descriptions);
            }

            // Update pricing if present in the modified data
            if (modifiedData.containsKey("pricing")) {
                Map<String, String> pricing = modifiedData.get("pricing");
                String priceStr = pricing.get("price");
                try {
                    double price = Double.parseDouble(priceStr);
                    try {
                        gcs.aggiornamentoPrezzoProdotto(originalProduct, (float) price, 1, pr_pagina);
                    } catch (ProdottoException.CategoriaProdottoException | ProdottoException.SottocategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | ProdottoException.PrezzoProdottoException ex) {
                        request.setAttribute("error", ex.getMessage());
                        Logger.getLogger(ModifyProductsInCatalog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Invalid price format.");                    
                }
            }
            int quantità=1 ;     
            // Check if 'quantità' is present in modifiedData
            if (modifiedData.containsKey("quantita")) {                
                Map<String, String> quantitàStr = modifiedData.get("quantita");
                String quantityValue = quantitàStr.get("quantita");
          
                try {
                // Convert to integer
                quantità = Integer.parseInt(quantityValue);
                // Call method to update quantity
                outputMessage += updateQuantita(originalProduct, quantità);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid quantity format.");
            }
             
        }
        }
        // Create a map to store JSON response
        responseMap.put("message", outputMessage);
        responseMap.put("redirectUrl", request.getContextPath() + "/Catalogo");
        
        // Convert map to JSON string using GSON
        return gson.toJson(responseMap);
    
    }
    private String updateQuantita(Prodotto originalProduct, int quantità){
        try {
            gcs.aggiornamentoDisponibilitàProdotto(originalProduct, quantità, 1, pr_pagina);
            return "Aggiornamento Quantità Avvenuto con Successo!";
        } catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | ProdottoException.QuantitaProdottoException ex) {
            Logger.getLogger(ModifyProductsInCatalog.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }
        return "Non e' Stato Possibile Aggiornare la Quantità";
    }
    // Helper method to update product details based on comparison
    private String updateProductDetail(String field, String key, Prodotto originalProduct, 
                                  Map<String, String> modifiedDetails) {
        String modifiedValue = modifiedDetails.get(key);
        if (originalProduct != null) {
            Object originalValue = getFieldByName(field, originalProduct); // Return type Object to handle different data types
           //STAMPE DI DEBUGGING:
            // System.out.println(field);
           // System.out.println(originalProduct);
           // System.out.println(originalValue);
            
            if (!originalValue.equals(convertToCorrectType(modifiedValue, originalValue))) {
                try {
                    gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
                    return "Field aggiornata con successo:"+field+" Nuovo Valore"+modifiedValue+ "\n";
                    
                } catch (CatalogoException.ProdottoAggiornatoException | ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | CatalogoException.ErroreSpecificaAggiornamentoException ex) {
                    Logger.getLogger(ModifyProductsInCatalog.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex);
                    return ex.getMessage();                  
                }
            }
        } else {
            if (modifiedValue != null && !modifiedValue.isEmpty()) {
                try {
                    gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
                    
                    return "Field aggiornata con successo:"+field+" Nuovo Valore"+modifiedValue+ "\n";
                } catch (CatalogoException.ProdottoAggiornatoException | ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | CatalogoException.ErroreSpecificaAggiornamentoException ex) {
                    Logger.getLogger(ModifyProductsInCatalog.class.getName()).log(Level.SEVERE, null, ex);
                    return ex.getMessage();  
                }
            }
        }
        return "Non è stato possibile aggiornare:"+field+" \n";
    }
    // Convert the modified value to the same type as the original value
    private Object convertToCorrectType(String modifiedValue, Object originalValue) {
        if (originalValue instanceof Integer) {
            return Integer.parseInt(modifiedValue);
        } else if (originalValue instanceof Double) {
            return Double.parseDouble(modifiedValue);
        } else {
            return modifiedValue; // Assume it's a String if not a number
        }
    }            
    // Helper Method to select update field to modify based on the string passed as a parameter.
    private Object getFieldByName(String field, Prodotto prod){
        switch(field){               
            case "MARCA":    
                return prod.getMarca();
                
            case "MODELLO": 
                return prod.getModello();
             
            case "DESCRIZIONE_EVIDENZA":
                return prod.getTopDescrizione();
            
            case "DESCRIZIONE_DETTAGLIATA":
                return prod.getDettagli();
            
            case "CATEGORIA":
                return prod.getCategoriaAsString();
            
            case "SOTTOCATEGORIA":
                return prod.getSottocategoriaAsString();
            default:
                return null;
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
