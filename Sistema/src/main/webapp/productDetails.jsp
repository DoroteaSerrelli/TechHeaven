<%-- 
    Document   : productDetails
    Created on : 31 ago 2024, 17:53:50
    Author     : raffa
--%>

<%@page import="application.NavigazioneService.Prodotto"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Product Details Page</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/common/style.css">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/view/style/product_table.css">
       <jsp:include page="common/header.jsp"  flush="true"/>
       <%
           Prodotto product = (Prodotto)request.getAttribute("product");
           if(product==null){
                response.sendRedirect(request.getContextPath() + "/index");
                return;
           }
       
       %>
    </head>
    <body>
        <div class="product-page">  
            <div class="pro">       
                <img src="image?productId=<%= product.getCodiceProdotto() %>" 
                     alt="alt" 
                     onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/view/img/placeholder.png';" 
                     />
                <div class="product-details">    
                    <h3><%=product.getNomeProdotto()%></h3>
                    <span><%=product.getMarca()%></span>
                    <h5><%=product.getTopDescrizione()%></h5>
                    <h2><%=product.getDettagli()%></h2>
                    <div class="star">                              
                    </div>
                    <h4><%=product.getPrezzo()%>€</h4>
                </div>                    
            </div>
        </div>          
        <div class="product-actions">                
            <a class="add-to-cart" href="#" onClick="modifyCart(<%=product.getCodiceProdotto()%>,'aggiungiAlCarrello')">
                <img class="cart" src="${pageContext.request.contextPath}/view/img/icon_carrello2.png">
            </a>
            <a class="add-to-wishlist" href="GestioneWishlistController?action=addtowishlist&productId=<%= product.getCodiceProdotto() %>">
                <img src="${pageContext.request.contextPath}/view/img/icon_wishlist.png" style="margin: 1px; width: 25px; height: 25px">
            </a>
        </div>           
        <jsp:include page="common/footer.jsp"  flush="true"/> 
    </body>
</html>
