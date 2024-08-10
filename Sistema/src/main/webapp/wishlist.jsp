<%-- 
    Document   : wishlist
    Created on : 3 ago 2024, 17:25:46
    Author     : raffa
--%>

<%@page import="application.NavigazioneService.ProxyProdotto"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.GestioneWishlistService.Wishlist"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="common/style.css">
        <link rel="stylesheet" href="cart.css">
    </head>
    <body>
        <jsp:include page="common/header.jsp"  flush="true"/>              
        <div id="showpr" class="section-p1">
        <%
            Wishlist wishlist; 
            wishlist = (Wishlist) request.getSession().getAttribute("Wishlist");
            if(wishlist==null){
        %>
            <div id="emptycart">
                <h4>La tua wishlist è vuoto!</h4>
                <p>Inserisci uno dei nostri prodotti per mantenerti aggiornato sulle novità!</p>
            </div>
            
        <%
            } else {  
        %>
        <h1>Wishlist</h1>
            <%
                ArrayList <ProxyProdotto> ItemWishlist = wishlist.getProdotti();
                for(ProxyProdotto p: ItemWishlist){                         
            %>            
        
            <div class="row">			
                <img src="image?productId=<%= p.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"/>
                <p><%=p.getNomeProdotto()+" "%> <%=p.getMarca()%></p>
                        <h3><%=p.getPrezzo()%>€</h3>
            </div>
            <div class="row item-carrello">
                <p>Quantità: <%=p.getQuantita()%></p>                           
            </div>       
            <% }
            %> 
            
        <%}%>
        </div>        
        <jsp:include page="common/footer.jsp"  flush="true"/> 	       		
    </body>
</html>
