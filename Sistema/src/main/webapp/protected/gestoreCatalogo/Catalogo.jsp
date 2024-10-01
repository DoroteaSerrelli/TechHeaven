<%-- 
    Document   : Catalogo
    Created on : 1 ott 2024, 17:11:39
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>TechHeaven - Catalogo</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_options.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/product_table.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_form.css">
        <jsp:include page="/common/header.jsp"  flush="true"/>
        <script src="${pageContext.request.contextPath}/scripts/pagination.js"></script>
        <script src="${pageContext.request.contextPath}/scripts/indexedDBUtils.js"></script>
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
            $(document).ready(function() {
                fetchProducts(1, ''); // Fetch products without any specific action (just viewing)
            });
        </script> 
    </head>
    <body>
         <jsp:include page="/protected/cliente/roleSelector.jsp"  flush="true"/> 
         <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>
        <jsp:include page="/protected/gestoreCatalogo/catalogo_toolbar.jsp"  flush="true"/> 
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
             </table> 
           </section>
        <script src="${pageContext.request.contextPath}/scripts/shifting_menu_manag_functions_sidebar.js"></script>
       <script src="${pageContext.request.contextPath}/scripts/validateNewProduct.js"></script>      
       <script src="${pageContext.request.contextPath}/scripts/ajax_catalog_table_functions.js?ts=<%=System.currentTimeMillis()%>"></script>                    
    <jsp:include page="/common/footer.jsp"  flush="true"/>
    </body>
</html>
