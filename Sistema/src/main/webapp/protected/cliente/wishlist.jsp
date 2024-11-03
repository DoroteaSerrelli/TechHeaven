<%-- 
    Document   : wishlist
    Created on : 3 ago 2024, 17:25:46
    Author     : raffa
--%>

<%@page import="java.util.Currency"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.google.gson.Gson"%>
<%@page contentType="text/html" pageEncoding="UTF-8"
		import = "application.Navigazione.NavigazioneService.ProxyProdotto,
				  java.util.Collection,application.GestioneWishlist.GestioneWishlistService.Wishlist"%>
<!DOCTYPE html>
<html lang = "en">
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/style.css">
        <link rel="stylesheet" href="<%=request.getContextPath() %>/style/cart.css">
          <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script> 
    </head>
    <body>
        <jsp:include page="/common/header.jsp"  flush="true"/>              
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
                String status = (String)request.getAttribute("status");
                
                if (err != null && !err.isEmpty()) {
             %>
            <p id="error" class="<%=status%>"><%=err%></p>               
            <% } %>
        </div>        
        <h1>Wishlist</h1>
            <%
                Collection <ProxyProdotto> ItemWishlist = wishlist.getProdotti();
                for(ProxyProdotto p: ItemWishlist){                         
            %>            
             <%
                String productJson = new Gson().toJson(p);
                String encodedProductJson = URLEncoder.encode(productJson, "UTF-8");
            %>
            <a class="dettagli" id="<%= p.getCodiceProdotto()%>" href="javascript:void(0);" 
                onclick="submitProductDetails('<%= encodedProductJson %>');">
              <p>  Dettagli </p>
             </a> 
            <a id="removeBtn<%=p.getCodiceProdotto()%>" href="GestioneWishlistController?action=removefromwishlist&productId=<%=p.getCodiceProdotto()%>"><button class="delete_button">Rimuovi</button></a>
            <div class="row" id="<%=p.getCodiceProdotto()%>">		               
                <img src="image?productId=<%= p.getCodiceProdotto() %>" alt="Prodotto" onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/images/site_images/placeholder.png';"/>
                <p><%=p.getNomeProdotto()+" "%> <%=p.getMarca()%></p>
                      <%
                    double prezzo = p.getPrezzo();
                       // Optionally, specify a particular currency
                     // Create a NumberFormat instance for currency formatting
                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

                        // Optionally, specify a particular currency
                        currencyFormatter.setCurrency(Currency.getInstance("EUR")); // Use "USD" for US dollars, "EUR" for Euros, etc.

                        String prezzoFormattato = currencyFormatter.format(prezzo);
                    %>
                <h4 style="color: goldenrod" class="prezzo"><%=prezzoFormattato%></h4>             
            </div>             
            <% }
            %> 
            
        <%}%>
        </div>        
        <jsp:include page="/common/footer.jsp"  flush="true"/> 	       		
    </body>
</html>
