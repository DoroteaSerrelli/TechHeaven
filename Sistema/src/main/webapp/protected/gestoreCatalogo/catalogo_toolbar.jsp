<%-- 
    Document   : catalogo_toolbar
    Created on : 1 ott 2024, 18:50:47
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
          <aside class="options_sidebar visible" id="options_sidebar">
            <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>        
             <div class="fe-box" id="viewProducts" onclick="moveToSidebar('viewProducts', 'viewProductsForm')">
                 <a href="/Catalogo">
                     <img src="${pageContext.request.contextPath}/images/site_images/listaprodotto.png" alt="Visualizza Prodotti">
                     <h6>Visualizza Prodotti</h6>
                 </a>    
             </div>
             <div class="fe-box" id="addProduct">
                 <a href="/GestioneCatalogo"><img src="${pageContext.request.contextPath}/view/img/addprodotto.png" alt="Aggiungi un nuovo prodotto"></a>
                 <h6>Aggiungi un nuovo prodotto</h6>
             </div>
              <div class="fe-box" id="removeProduct" onclick="resetProductData('delete')">
                         <a href="javascript:void(0)"><img src="${pageContext.request.contextPath}/images/site_images/removeprodotto.png" alt="Elimina un prodotto"></a>
                         <h6>Elimina un prodotto</h6>
             </div>
                 <div class="fe-box" id="modifyProperties" onclick="resetProductData('modify')">
                 <a href="javascript:void(0)"> <img src="${pageContext.request.contextPath}/images/site_images/modproperties.png" alt="Modifica caratteristiche prodotto"> </a>
                 <h6>Modifica caratteristiche prodotto</h6>
             </div>
            </aside>
    </body>
</html>
