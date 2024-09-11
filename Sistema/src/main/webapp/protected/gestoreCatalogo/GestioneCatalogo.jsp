<%-- 
    Document   : GestioneCatalogo
    Created on : 21-mar-2024, 17.36.51
    Author     : raffy
--%>

<%@page import="java.util.Collection"%>
<%@page import="application.NavigazioneService.ProxyProdotto"%>
<%@page import="application.RegistrazioneService.Ruolo"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.RegistrazioneService.Utente"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="it">
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_options.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/product_table.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_form.css">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script> 
        <script src="${pageContext.request.contextPath}/view/pagination.js"></script>       
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script>      
    </head>    
    <body>     
        <!-- DA AGGIUNGERE PATH NEL WEB.XML + FILTRO -->
       <jsp:include page="/common/header.jsp"  flush="true"/>
       <jsp:include page="/roleSelector.jsp"  flush="true"/>
        <aside class="options_sidebar hidden" id="options_sidebar">
           <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>        
           
            <!-- Sidebar will be populated by JavaScript -->
        </aside>      
        <main class="main-content" id="mainContent">
              <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>                       
            <section id="centerMenu" class="center-menu">
                <div class="fe-box" id="viewProducts" onclick="moveToSidebar('viewProducts', 'viewProductsForm')">
                    <img src="${pageContext.request.contextPath}/images/site_images/listaprodotto.png" alt="Visualizza Prodotti">
                    <h6>Visualizza Prodotti</h6>
                </div>
                    <div class="fe-box" id="addProduct" onclick="moveToSidebar('addProduct', 'addProductForm')">
                        <img src="${pageContext.request.contextPath}/images/site_images/addprodotto.png" alt="Aggiungi un nuovo prodotto">
                        <h6>Aggiungi un nuovo prodotto</h6>
                    </div>
                    <div class="fe-box" id="removeProduct" onclick="moveToSidebar('viewProducts', 'viewProductsForm');">
                        <a href="${pageContext.request.contextPath}/ModifyProductsInCatalog?action=delete" ><img src="${pageContext.request.contextPath}/images/site_images/removeprodotto.png" alt="Elimina un prodotto"></a>
                        <h6>Elimina un prodotto</h6>
                    </div>
                    <div class="fe-box" id="modifyProperties">
                        <a href="${pageContext.request.contextPath}/ModifyProductsInCatalog?action=modify" > <img src="${pageContext.request.contextPath}/images/site_images/modproperties.png" alt="Modifica caratteristiche prodotto"> </a>
                        <h6>Modifica caratteristiche prodotto</h6>
                    </div>
                </section>
              
           </main>           
                
                <section id="forms">                     
                    <section id="viewProductsForm" class="form-section hidden">
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
                         </table> 
                       <div id="pagination"></div>
            </section>
                <section id="addProductForm" class="form-section hidden">
                    <!-- Your form for adding a new product -->
                    <h2>Aggiungi un nuovo prodotto</h2>
                    <form action="${pageContext.request.contextPath}/GestioneCatalogoController" method="post" enctype="multipart/form-data">
                        <div class="form-group">                    
                            <label for="productID"> ID Prodotto </label>
                            <input type="number" id="number" name="productId">
                            <label for="productName">Nome Prodotto:</label>
                            <input type="text" id="productName" name="productName">
                        </div>
                        <div class="form-group">
                            <label for="TopDescrizione">Top Descrizione:</label>
                            <textarea name="topDescrizione" rows="5" cols="40"></textarea>
                            <label for="Dettagli">Dettagli:</label>
                            <textarea name="dettagli" rows="5" cols="40"></textarea>
                        </div>   
                        <div class="form-group">
                            <label for="prezzo">Prezzo:</label>                       
                            <input type="text" name="price">
                        </div>
                        <div class="form-group">
                            <select name="categoria">
                                <option value="GRANDI_ELETTRODOMESTICI">Grandi Elettrodomestici</option>
                                <option value="PICCOLI_ELETTRODOMESTICI">Piccoli Elettrodomestici</option>
                                <option value="TELEFONIA">Telefonia</option>
                                <option value="PRODOTTI_ELETTRONICA">Prodotti Elettronica</option>                            
                            </select>
                            <select name="sottocategoria">
                                <option value="null">Nessuna Sottocategoria</option>
                                <option value="TABLET">Tablet</option>
                                <option value="SMARTPHONE">Smartphone</option>
                                <option value="PC">PC</option>
                                <option value="SMARTWATCH">Smartwatch</option>                               
                            </select>
                        </div>    
                        <div class="form-group">
                            <label for="marca">Marca:</label>
                            <input type="text" id="marca" name="marca">
                        </div>
                        <div class="form-group">
                            <label for="modello">Modello:</label>
                            <input type="text" id="modello" name="modello">
                        </div>
                        <div class="form-group">
                            <label for="inVetrina">In Vetrina:</label>
                            <input type="checkbox" id="inVetrina" name="inVetrina" value="true">
                        </div>
                        <div class="form-group">
                            <label for="inCatalogo">In Catalogo:</label>
                            <input type="checkbox" id="inCatalogo" name="inCatalogo" value="true">
                        </div>
                        <div class="form-group">
                            <label for="quantità">Quantità:</label>
                            <input type="number" id="quantità" name="quantita">
                        </div>                     
                        <div class="form-group">
                            <label for="file">Immagine:</label>
                            <input type="file" id="file" name="file" accept="image/*">
                        </div>
                        <button type="submit">Aggiungi</button>
                    </form>
                </section>
                <section id="removeProductForm" class="form-section hidden">
                    <!-- Your form for removing a product -->
                    <h2>Elimina un prodotto</h2>
                    <p>Form content for removing a product...</p>
                </section>
        </section>
        <div id="error">
            <% String errormsg="";
                errormsg= (String)request.getSession().getAttribute("error");
                if(errormsg==null) errormsg="";                                                       
            %>
            <h2> <%=errormsg%> </h2>
            <%request.getSession().removeAttribute("error");%>
        </div>                   
       <div class="errormsg"> 
           <p id="updateMessage"></p>
       </div>
        <script>
            $(document).ready(function() {
                const outputMessage = sessionStorage.getItem('outputMessage');
                console.log('Output message:', outputMessage); // Log the output message
                const errorElement = document.getElementById('updateMessage');
                console.log('Error element found:', errorElement); // Check if element is found
                errorElement.innerHTML = outputMessage;
                sessionStorage.removeItem('outputMessage');
            });
        </script>                
       <script src="${pageContext.request.contextPath}/scripts/shifting_menu_manag_functions_sidebar.js"></script> 
       <script src="${pageContext.request.contextPath}/scripts/ajax_catalog_table_functions.js?ts=<%=System.currentTimeMillis()%>"></script>                    
    <jsp:include page="/common/footer.jsp"  flush="true"/>       
    </body>
</html>
