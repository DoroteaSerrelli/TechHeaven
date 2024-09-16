<%-- 
    Document   : wishlist
    Created on : 3 ago 2024, 17:25:46
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
		import = "application.NavigazioneService.ProxyProdotto,
				  java.util.ArrayList,
				  application.GestioneWishlistService.Wishlist"%>
<!DOCTYPE html>
<html lang = "en">
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/style.css">
        <link rel="stylesheet" href="<%=request.getContextPath() %>/style/cart.css">
        
    </head>
    <body>
        <jsp:include page="<%=request.getContextPath() %>/common/header.jsp"  flush="true"/>              
        <div id="showpr" class="section-p1">
        <%
            Wishlist wishlist; 
            wishlist = (Wishlist) request.getSession().getAttribute("Wishlist");
            if(wishlist.getProdotti().isEmpty()){
        %>
            <div id="emptycart">
                <h4>La tua wishlist è vuota!</h4>
                <p>Aggiungi uno dei nostri prodotti alla tua lista dei desideri.</p>
            </div>
            
        <%
            } else {  
        %>
        <div class="errormsg">         
             <% 
                String err = (String)request.getAttribute("error");
                if (err != null && !err.isEmpty()) {
             %>
            <p id="error" class="invalid"><%=err%></p>               
            <% } %>
        </div>        
        <h1>Wishlist</h1>
            <%
                ArrayList <ProxyProdotto> ItemWishlist = wishlist.getProdotti();
                for(ProxyProdotto p: ItemWishlist){                         
            %>            
            <a href="GestioneWishlistController?action=removefromwishlist&productId=<%=p.getCodiceProdotto()%>"><button class="delete_button">Rimuovi</button></a>
            <div class="row">			
                <img src="image?productId=<%= p.getCodiceProdotto() %>" alt="Prodotto" onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/images/site_images/placeholder.png';"/>
                <p><%=p.getNomeProdotto()+" "%> <%=p.getMarca()%></p>
                        <h3><%=p.getPrezzo()%>€</h3>
            </div>     
            <% }
            %> 
            
        <%}%>
        </div>        
        <jsp:include page="<%=request.getContextPath() %>/common/footer.jsp"  flush="true"/> 	       		
    </body>
</html>
