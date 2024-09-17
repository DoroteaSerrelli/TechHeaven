<%-- 
    Document   : UpdateProductInfos
    Created on : 4 set 2024, 15:19:48
    Author     : raffa
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="application.GestioneCatalogoControl.ImageResizer"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <title>Select a Product From The List to Update It</title> 
    <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_options.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/product_table.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_form.css">
      <%
        // Retrieve the base64 gallery list from the session
        List<byte[]> originalGallery = (List<byte[]>) request.getSession().getAttribute("originalGallery");
        List <String> base64Gallery = new ArrayList<>();
        if(originalGallery!=null){
             base64Gallery = ImageResizer.processGalleryAndConvertToBase64(originalGallery, 500, 500);
        }
            String base64GalleryJson = base64Gallery != null ? new Gson().toJson(base64Gallery) : "[]"; 
    %>
    <script>
        var base64Gallery = <%= base64GalleryJson %>; // Convert session data to JavaScript array
    </script> 
    

    <script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha512-894YE6QWD5I59HgZOGReFYm4dnWc1Qt5NtvYSaNcOP+u1T9qYdvdihz0PPSiiqn/+/3e7Jo4EaG7TubfWGUrMQ==" 
    crossorigin="anonymous"></script>
    <script src="${pageContext.request.contextPath}/scripts/pagination.js"></script>
    <script src="${pageContext.request.contextPath}/scripts/indexedDBUtils.js"></script>
    <script>
        $(document).ready(function() {
            openDB(2); // Open the database when the document is ready

            let action = '<%= session.getAttribute("action") %>';           

            retrieveAllData(function(data) {
                const { product } = data;  
                
                // Proceed with handling product and action
                if (product && action) {
                    addOriginalProductDetailsToForm(product);
                     updateGallery(base64Gallery);
                    if (action === 'modify') {
                        openModifyForm(product);
                        $('#modifyPropertiesForm').removeClass('hidden');
                        $('#viewProductsForm').addClass('hidden');
                    } else if (action === 'delete') {
                        openDeleteForm(product);
                    }
                } else if (action) {
                    const initialPage = 1;
                    fetchProducts(initialPage, action);
                } else {
                    console.error('No action provided');
                }
            });
        });
    </script>
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script>
    </head>    
    <body>     
        <!-- DA AGGIUNGERE PATH NEL WEB.XML + FILTRO -->
       <jsp:include page="/common/header.jsp"  flush="true"/>
       <jsp:include page="/roleSelector.jsp"  flush="true"/>
       <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>                   
       <aside class="options_sidebar visible" id="options_sidebar">
           <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>        
            <div class="fe-box" id="viewProducts" onclick="moveToSidebar('viewProducts', 'viewProductsForm')">
                    <img src="${pageContext.request.contextPath}/images/site_images/listaprodotto.png" alt="Visualizza Prodotti">
                    <h6>Visualizza Prodotti</h6>
            </div>
            <div class="fe-box" id="addProduct">
                <a href="/GestioneCatalogo" onclick="moveToSidebar('addProduct', 'addProductForm');"><img src="${pageContext.request.contextPath}/view/img/addprodotto.png" alt="Aggiungi un nuovo prodotto"></a>
                <h6>Aggiungi un nuovo prodotto</h6>
            </div>
            <div class="fe-box" id="removeProduct">
                <img src="${pageContext.request.contextPath}/images/site_images/removeprodotto.png" 
                     onclick="$('#viewProductsForm').removeClass('hidden'); 
                     $('#modifyPropertiesForm').addClass('hidden');" 
                     alt="Elimina un prodotto">
                <h6>Elimina un prodotto</h6>
            </div>
            <div class="fe-box" id="modifyProperties">
                <img src="${pageContext.request.contextPath}/images/site_images/modproperties.png" alt="Modifica caratteristiche prodotto"
                onclick="$('#viewProductsForm').removeClass('hidden'); 
                     $('#modifyPropertiesForm').addClass('hidden');">
                <h6>Modifica caratteristiche prodotto</h6>
            </div>
       </aside>
        <section id="forms">                     
                    <section id="viewProductsForm" class="form-section">
                        <div id="pagination"></div>   
                        <h2>Visualizza Prodotti</h2>
                         <table id="showpr">
                             <tr>
                                 <th><strong>#</strong></th>
                                 <th><strong>Image</strong></th><!-- Immagine -->
                                 <th><strong>Nome</strong></th><!-- Nome prodotto -->
                                 <th><strong>Marca</strong></th><!-- Marca -->
                                 <th><strong>TopDescr</strong></th><!-- Top Descrizione -->
                                 <th><strong>Prezzo</strong></th><!-- Prezzo -->
                             </tr>
                        <div id="error">
                            <% String errormsg="";
                                errormsg= (String)request.getAttribute("error");
                                if(errormsg==null) errormsg="";                                                       
                            %>
                            <%=errormsg%>
                        </div>                                          
                    <%%>
                         </table> 
                       <div id="pagination"></div>
            </section>
                         <section id="modifyPropertiesForm" class="form-section hidden">
                    <h2 id="changeable">Modifica Prodotto</h2>
                    <form id="productForm" action="${pageContext.request.contextPath}/GestioneCatalogoController" method="post" enctype="multipart/form-data">
                        <!-- Product Details Group -->                       
                        <div class="form-group">
                            <label for="productID">ID Prodotto</label>
                            <input type="number" id="productId" name="productId" oninput="validateProductID(this, 'ID')">
                            <input type="checkbox" id="productDetailsCheckbox" name="productDetailsCheckbox">
                            <label for="productDetailsCheckbox">Update Product Details</label>
                            <div id="productDetailsGroup" class="hidden">                              
                                <label for="productName">Nome Prodotto</label>
                                <input type="text" id="productName" name="productName" oninput="validateProductName(this, 'productName')">
                                <label for="marca">Marca</label>
                                <input type="text" id="marca" name="marca" oninput="validateBrand(this)">
                                <label for="modello">Modello</label>
                                <input type="text" id="modello" name="modello">
                            </div>
                        </div>
                        
                    <!-- Description Group -->
                    <div class="form-group">
                        <input type="checkbox" id="descriptionCheckbox" name="descriptionCheckbox">
                        <label for="descriptionCheckbox">Update Descriptions</label>
                        <div id="descriptionGroup" class="hidden">
                            <label for="TopDescrizione">Top Descrizione</label>
                            <textarea name="topDescrizione" rows="5" cols="40"></textarea>
                            <label for="Dettagli">Dettagli</label>
                            <textarea name="dettagli" rows="5" cols="40"></textarea>
                        </div>
                    </div>

                    <!-- Pricing Group -->
                    <div class="form-group">
                        <input type="checkbox" id="pricingCheckbox" name="pricingCheckbox">
                        <label for="pricingCheckbox">Update Pricing</label>
                        <div id="pricingGroup" class="hidden">
                            <label for="prezzo">Prezzo</label>
                            <input type="text" name="price" oninput="validatePrice(this)">
                        </div>
                    </div>

                    <!-- Category Group -->
                    <div class="form-group">
                        <input type="checkbox" id="categoryCheckbox" name="categoryCheckbox">
                        <label for="categoryCheckbox">Update Category</label>
                        <div id="categoryGroup" class="hidden">
                            <label for="categoria">Categoria</label>
                            <select name="categoria">
                                <option value="GRANDI_ELETTRODOMESTICI">Grandi Elettrodomestici</option>
                                <option value="PICCOLI_ELETTRODOMESTICI">Piccoli Elettrodomestici</option>
                                <option value="TELEFONIA">Telefonia</option>
                                <option value="PRODOTTI_ELETTRONICA">Prodotti Elettronica</option>
                            </select>
                            <label for="sottocategoria">Sottocategoria</label>
                            <select name="sottocategoria">
                                <option value="null">Nessuna Sottocategoria</option>
                                <option value="TABLET">Tablet</option>
                                <option value="SMARTPHONE">Smartphone</option>
                                <option value="PC">PC</option>
                                <option value="SMARTWATCH">Smartwatch</option>
                            </select>
                        </div>
                        </div>

                        <!-- Image and Other Details -->
                        <div class="form-group">
                            <label for="quantità">Quantità</label>
                            <input type="number" id="quantità" name="quantità" oninput="validateProductID(this, 'Quantità')">                    
                        </div>
                            <button id="submitBtn" type="submit">Update</button>
                        </form>
                        <h2>Modifica, Aggiungi o Elimina Foto di Presentazione</h2> 
                        <div class="product-image">
                            <img id="topImage" src="" alt="alt" loading="lazy">
                        </div>
                        <div id="updatePhotoLog" style="flex: 1; padding: 10px">
                            <h2>Logs</h2>
                         </div>
                        <section style="display: flex;">       
                            <!-- The log on the right -->                         
                            <div style="flex: 1;">                      
                                  <!-- Image Preview -->                               
                                <form id="photoForm" action="${pageContext.request.contextPath}/ImageUpdater" method="post" enctype="multipart/form-data">
                                   <input type="hidden" id="productData" name="productData">
                                        <!-- Option Sections -->
                                        <div class="option-group">
                                        <p>Seleziona un'Azione per la Foto:</p>
                                        <!-- Main Photo Actions -->
                                        <div class="option">
                                            <label for="update">Aggiorna Foto</label>
                                            <input type="radio" id="update" name="main_photoAction" value="update">
                                        </div>
                                        <div class="option">
                                            <label for="add">Aggiungi Foto Presentazione</label>
                                            <input type="radio" id="add" name="main_photoAction" value="add">
                                        </div>

                                        <!-- Gallery Photo Actions -->
                                        <div class="option">
                                            <label for="addToGallery">Aggiungi Foto Galleria</label>
                                            <input type="radio" id="addToGallery" name="gallery_photoActions" value="addToGallery">
                                        </div>
                                    </div>

                                <!-- File Input -->
                                <div class="file-upload">
                                    <label for="file">Immagine</label>
                                    <input type="file" id="file" name="presentazione" accept="image/*">
                                </div>

                                <!-- Buttons -->
                                <input type="submit" class="confirm_button" id="imageUploadBtn" value="Aggiorna Immagini">
                                <button type="button" id="resetFormBtn">Clear Selections</button>                      
                            </form>                           
                         </div>                                 
                            <div id="gallery-container"></div>
                            <div class="product-gallery">                                
                                <div class="main-image" >
                                    <!-- Display the first image as the main image -->
                                    <img id="currentImage"  alt="alt" />
                                </div>
                                <div class="gallery-thumbnails">                                   
                                </div>
                            </div>    
                        </section>  
                    </section>                        
            </section>
        <script src="${pageContext.request.contextPath}/scripts/shifting_menu_manag_functions_sidebar.js"></script> 
        <script src="${pageContext.request.contextPath}/scripts/validateNewProduct.js"></script>
        <script src="${pageContext.request.contextPath}/scripts/catalog_image_update_functions.js?ts=<%=System.currentTimeMillis()%>"></script>      
        <script src="${pageContext.request.contextPath}/scripts/ajax_catalog_table_functions.js?ts=<%=System.currentTimeMillis()%>"></script>      
        <jsp:include page="/common/footer.jsp"  flush="true"/>             
</html>
