<%-- 
    Document   : UpdateProductInfos
    Created on : 4 set 2024, 15:19:48
    Author     : raffa
--%>

<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <title>Select a Product From The List to Update It</title> 
    <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/common/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/catalog_options.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/product_table.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/catalog_form.css">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="${pageContext.request.contextPath}/view/pagination.js"></script> 
        <script>
           $(document).ready(function() {
            // Retrieve the action from the session attribute set by the Servlet
            let action = '<%= session.getAttribute("action") %>';

            // Retrieve stored product and action information
            let storedProduct = sessionStorage.getItem('selectedProduct');
            
            let storedAction = sessionStorage.getItem('selectedAction');
            if (storedProduct && storedAction) {
                const product = JSON.parse(storedProduct);         
                if (storedAction === 'modify') {
                    openModifyForm(product);
                    $('#modifyPropertiesForm').removeClass('hidden');
                    $('#viewProductsForm').addClass('hidden');
                } else if (storedAction === 'delete') {
                    openDeleteForm(product);
                }             
            } else if (action) {
                const initialPage = 1;
                fetchProducts(initialPage, action); // Fetch products with the action
            } else {
                console.error('No action provided');
            }
        });

        </script> 
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script>
        <%List<String> galleryImages = (List<String>)request.getSession().getAttribute("galleryImages"); %>
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
                            <input type="number" id="productId" name="productId">
                            <input type="checkbox" id="productDetailsCheckbox" name="productDetailsCheckbox">
                            <label for="productDetailsCheckbox">Update Product Details</label>
                            <div id="productDetailsGroup" class="hidden">                              
                                <label for="productName">Nome Prodotto</label>
                                <input type="text" id="productName" name="productName">
                                <label for="marca">Marca</label>
                                <input type="text" id="marca" name="marca">
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
                            <input type="text" name="price">
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
                            <input type="number" id="quantità" name="quantità">                    
                        </div>
                            <button id="submitBtn" type="submit">Update</button>
                        </form>                       
                        <section>
                            <h2>Modifica, Aggiungi o Elimina Foto di Presentazione</h2>
                            <form id="photoForm" action="${pageContext.request.contextPath}/ImageUpdater" method="post" enctype="multipart/form-data">
                                <input type="hidden" id="productData" name="productData">
                                <label for="file">Immagine</label>
                                <label for="update">Aggiorna Foto</label>    
                                <input type="radio" name="main_photoAction" value="update">
                                <label for="add">Aggiungi Foto Presentazione</label>
                                <input type="radio" name="main_photoAction" value="add">                                
                                <input type="file" id="file" name="presentazione" accept="image/*"> 
                                <input type="submit" id="imageUploadBtn" value="Aggiorna Immagini">
                            <div class="product-image">
                                <img id="topImage" src="" alt="alt" loading="lazy"> 
                            </div>    
                                <label for="addToGallery">Aggiungi Foto Galleria</label>
                                <input type="radio" name="gallery_photoActions" value="addToGallery">
                                <div class="product-gallery">
                                    <% if(galleryImages!=null && !galleryImages.isEmpty()){%>        
                                    <div class="main-image" style="display:none" >
                                        <!-- Display the first image as the main image -->
                                        <img id="currentImage" src="<%= galleryImages.get(0) %>" alt="alt" />
                                    </div>
                                    <div class="thumbnails">
                                        <% 
                                        if (galleryImages != null && !galleryImages.isEmpty()) {
                                            for (int i = 0; i < galleryImages.size(); i++) {
                                                String img = galleryImages.get(i);
                                        %>
                                            <div class="thumbnail-container" id="image-container-<%= i %>">
                                                <!-- Use lazy loading for thumbnails -->
                                                <img src="<%= img %>" alt="Thumbnail" class="gallery-thumbnail" loading="lazy" onclick="changeImage('<%= img %>')">
                                                <button class="delete-image-btn" data-image-index="<%= i %>">Delete</button>
                                            </div>
                                        <% 
                                            }
                                        }
                                        }
                                        %>
                                    </div>
                                </div>    
                            </form>
                        </section>  
                    </section>                        
            </section>
        <script>
            function changeImage(src) {
                const currentImage = document.getElementById('currentImage');
                const thumbnails = document.querySelectorAll('.gallery-thumbnail');

                // Set the main image to the selected one
                currentImage.src = src;

                // Remove highlight from all thumbnails
                thumbnails.forEach(thumbnail => {
                    thumbnail.classList.remove('selected');
                });

                // Add highlight to the clicked thumbnail
                document.querySelector(`img[src="${src}"]`).classList.add('selected');
            }
        </script>
        <script src="${pageContext.request.contextPath}/view/shifting_menu_manag_functions_sidebar.js"></script> 
        <script src="${pageContext.request.contextPath}/view/ajax_catalog_table_functions.js?ts=<%=System.currentTimeMillis()%>"></script>      
                <script src="${pageContext.request.contextPath}/view/catalog_image_update_functions.js"></script> 
        <jsp:include page="/common/footer.jsp"  flush="true"/>             
</html>
