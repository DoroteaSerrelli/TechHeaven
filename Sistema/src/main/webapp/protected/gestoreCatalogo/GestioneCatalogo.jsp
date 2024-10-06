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
        <title>TechHeaven - Gestione Catalogo: Dashboard</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_options.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/product_table.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_form.css">
        <jsp:include page="/common/header.jsp"  flush="true"/>
        <script src="${pageContext.request.contextPath}/scripts/pagination.js"></script>  
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script> 
        <script src="${pageContext.request.contextPath}/scripts/indexedDBUtils.js"></script>       
        </head>    
    <body>     
        <!-- DA AGGIUNGERE PATH NEL WEB.XML + FILTRO -->
       <jsp:include page="/protected/cliente/roleSelector.jsp"  flush="true"/> 
       <!--  <aside class="options_sidebar hidden" id="options_sidebar">
           <button id="sidebar_toggle"><img src="/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>        
           
            Sidebar will be populated by JavaScript -->
        </aside>      
        <main class="main-content" id="mainContent">  
            <section id="centerMenu" class="center-menu">
                <div class="fe-box" id="viewProducts">
                    <a href="/Catalogo">
                        <img src="${pageContext.request.contextPath}/images/site_images/listaprodotto.png" alt="Visualizza Prodotti">
                    </a>
                    <h6>Visualizza Prodotti</h6>
                </div> <a href="<%=request.getContextPath()%>/AggiuntaAlCatalogo">
                    <div class="fe-box" id="addProduct" >
                        <a href="<%=request.getContextPath()%>/AggiuntaAlCatalogo">
                            <img src="${pageContext.request.contextPath}/images/site_images/addprodotto.png" alt="Aggiungi un nuovo prodotto">
                            <h6>Aggiungi un nuovo prodotto</h6>
                        </a>
                    </div>
                        <!-- Potenziale Caching del Prodotto che permetterebbe all'utente di continuare la modifica
                            di un prodotto anche chiudendo e riaprendo il browser. Faccio la Clear dello storage
                            cosi la lista viene mostrata e ricaricando la pagina di modifica dopo aver selezionato il prodotto
                            le caratteristiche rimangono.
                        -->
                    <div class="fe-box" id="removeProduct" onclick="resetProductData('delete')">
                        <a href="javascript:void(0)"><img src="${pageContext.request.contextPath}/images/site_images/removeprodotto.png" alt="Elimina un prodotto"></a>
                        <h6>Elimina un prodotto</h6>
                    </div>
                        <div class="fe-box" id="modifyProperties" onclick="resetProductData('modify')">
                        <a href="javascript:void(0)"> <img src="${pageContext.request.contextPath}/images/site_images/modproperties.png" alt="Modifica caratteristiche prodotto"> </a>
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
                    <a href="<%=request.getContextPath()%>/Catalogo">Annulla</a>
                    <h2>Aggiungi un nuovo prodotto</h2>
                    <form action="${pageContext.request.contextPath}/GestioneCatalogoController" method="post" enctype="multipart/form-data">
                         <div id="error"></div>
                        <div class="form-group">                    
                            <label for="productID"> ID Prodotto </label>
                            <input type="number" id="number" name="productId" oninput="validateProductID(this, 'ID')" required>
                            <div id="prodIDError" class="erromsg" style="display:none;"></div>                           
                            <label for="productName">Nome Prodotto:</label>
                            <input type="text" id="productName" name="productName" oninput="validateProductNameorModel(this, 'Nome')" required>
                            <div id="prodNomeError" class="erromsg" style="display:none;"></div>                                                           
                        </div>
                        <div class="form-group">
                            <label for="TopDescrizione">Top Descrizione:</label>
                            <textarea name="topDescrizione" rows="5" cols="40" oninput="validateDettailsAndDescription(this, 'Descrizione')" required></textarea>
                            <div id="prodDescrizioneError" class="erromsg" style="display:none;"></div>  
                            <label for="Dettagli">Dettagli:</label>
                            <textarea name="dettagli" rows="5" cols="40" oninput="validateDettailsAndDescription(this, 'Dettagli')" required></textarea>
                            <div id="prodDettagliError" class="erromsg" style="display:none;"></div>     
                        </div>   
                        <div class="form-group">
                            <label for="prezzo">Prezzo:</label>                       
                            <input type="text" name="price" oninput="validatePrice(this, 'Prezzo')" required>
                            <div id="prodPrezzoError" class="erromsg" style="display:none;"></div>  
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
                            <input type="text" id="marca" name="marca" oninput="validateBrand(this)" required>
                            <div id="prodBrandError" class="erromsg" style="display:none;"></div> 
                        </div>
                        <div class="form-group">
                            <label for="modello">Modello:</label>
                            <input type="text" id="modello" name="modello" oninput="validateProductNameorModel(this, 'Modello')" required>
                            <div id="prodModelloError" class="erromsg" style="display:none;"></div> 
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
                            <input type="number" id="quantità" name="quantita" oninput="validateProductID(this, 'Quantità')" required>
                            <div id="prodQuantitàError" class="erromsg" style="display:none;"></div>  
                        </div>                     
                        <div class="form-group">
                            <label for="file">Immagine:</label>
                            <input type="file" id="file" name="file" accept="image/*">
                        </div>
                         <button type="submit" id="addPrBtn" onclick="return validateForm()">Aggiungi</button>
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
       <script src="${pageContext.request.contextPath}/scripts/shifting_menu_manag_functions_sidebar.js"></script>
       <script src="${pageContext.request.contextPath}/scripts/validateNewProduct.js"></script>      
       <script src="${pageContext.request.contextPath}/scripts/ajax_catalog_table_functions.js?ts=<%=System.currentTimeMillis()%>"></script>                    
    <jsp:include page="/common/footer.jsp"  flush="true"/>       
    </body>
</html>
