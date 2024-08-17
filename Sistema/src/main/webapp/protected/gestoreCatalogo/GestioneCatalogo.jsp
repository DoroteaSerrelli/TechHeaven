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
<html>
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/common/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/catalog_options.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/product_table.css">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="${pageContext.request.contextPath}/view/ajax_catalog_table_functions.js"></script>
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script>
    </head>    
    <body>     
       <jsp:include page="${pageContext.request.contextPath}/common/header.jsp"  flush="true"/>
       <jsp:include page="${pageContext.request.contextPath}/roleSelector.jsp"  flush="true"/>      
        <aside class="options_sidebar hidden" id="options_sidebar">
            <!-- Sidebar will be populated by JavaScript -->
        </aside>
        <main class="main-content" id="mainContent">
            <section id="centerMenu" class="center-menu">
                <div class="fe-box" id="viewProducts" onclick="moveToSidebar('viewProducts', 'viewProductsForm')">
                    <img src="${pageContext.request.contextPath}/view/img/listaprodotto.png" alt="Visualizza Prodotti">
                    <h6>Visualizza Prodotti</h6>
                </div>
                    <div class="fe-box" id="addProduct" onclick="moveToSidebar('addProduct', 'addProductForm')">
                        <img src="${pageContext.request.contextPath}/view/img/addprodotto.png" alt="Aggiungi un nuovo prodotto">
                        <h6>Aggiungi un nuovo prodotto</h6>
                    </div>
                    <div class="fe-box" id="removeProduct" onclick="moveToSidebar('removeProduct', 'removeProductForm')">
                        <img src="${pageContext.request.contextPath}/view/img/removeprodotto.png" alt="Elimina un prodotto">
                        <h6>Elimina un prodotto</h6>
                    </div>
                    <div class="fe-box" id="modifyProperties" onclick="moveToSidebar('modifyProperties', 'modifyPropertiesForm')">
                        <img src="${pageContext.request.contextPath}/view/img/modproperties.png" alt="Modifica caratteristiche prodotto">
                        <h6>Modifica caratteristiche prodotto</h6>
                    </div>
                </section>
           </main>
                
                 <section id="forms">
                    <section id="viewProductsForm" class="form-section hidden">
                        <h2>Visualizza Prodotti</h2>
                         <table id="showpr">
                             <tr><th><strong>Image</strong></th><!-- Immagine -->
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
                
                <section id="addProductForm" class="form-section hidden">
                    <!-- Your form for adding a new product -->
                    <h2>Aggiungi un nuovo prodotto</h2>
                    <form>
                        <label for="productName">Nome prodotto:</label>
                        <input type="text" id="productName" name="productName">
                        <button type="submit">Aggiungi</button>
                    </form>
                </section>
                <section id="removeProductForm" class="form-section hidden">
                    <!-- Your form for removing a product -->
                    <h2>Elimina un prodotto</h2>
                    <p>Form content for removing a product...</p>
                </section>
                <section id="modifyPropertiesForm" class="form-section hidden">
                    <!-- Your form for modifying product properties -->
                    <h2>Modifica caratteristiche prodotto</h2>
                    <p>Form content for modifying product properties...</p>
                </section>
            </section>             
       <div id="dynamicContent">
           <form action="${pageContext.request.contextPath}/GestioneCatalogoController" method="post" enctype="multipart/form-data">
               <label>ID del prodotto:</label>
               <input type="text" name="prod_id"/>
               <input type="file" name="file" accept="image/*"/>
               <input type="submit" class="confirm_button" value="Upload"/>
           </form>
           
       </div>
       <script src="${pageContext.request.contextPath}/view/shifting_menu_manag_functions_sidebar.js"></script>        
    <jsp:include page="${pageContext.request.contextPath}/common/footer.jsp"  flush="true"/>       
    </body>
</html>
