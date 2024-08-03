<%-- 
    Document   : searchResult
    Created on : 18-apr-2024, 18.39.30
    Author     : raffy
--%>

<%@page import="java.util.Base64"%>
<%@page import="application.NavigazioneService.NavigazioneServiceImpl"%>
<%@page import="java.lang.String"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="application.NavigazioneService.ProxyProdotto"%>
<%@page import="java.util.Collection"%>
<%@page import="application.NavigazioneService.Prodotto"%>
<%
    Collection<ProxyProdotto> products = (Collection<ProxyProdotto>) request.getAttribute("products");
   
    if(products==null ||products.isEmpty()){ %>
    <h4>Nessun prodotto trovato con la keyword: <%=request.getAttribute("keyword")%></h4>
    <%}
    int totalPages = (int) request.getAttribute("totalPages");
    String keyword = (String) request.getAttribute("keyword");
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
   <head>
    <meta charset="UTF-8" name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Product Search Results</title>
    <!-- Include any CSS stylesheets if needed -->
    </head>
    <body>
       <link rel="stylesheet" href="common/style.css">
       <jsp:include page="common/header.jsp"  flush="true"/>
    <h1>Product Search Results</h1>   
    <!-- Display search results here -->
    <div class="container">
        <div class="sidebar">
            <h2>Categorie:</h2>
           <% 
            Set<String> uniqueBrands = new HashSet<>();
            for (ProxyProdotto product : products) { 
                String brand = product.getMarca();
                // Check if the brand is not already in the set to avoid duplicates
                if (!uniqueBrands.contains(brand)) { 
            %>
                <li><%= brand %></li> <!-- Display brand as a list item -->
            <% 
                    // Add the brand to the set to keep track of unique brands
                    uniqueBrands.add(brand);
                } 
            } 
            %>    
        </div>
        <div class="search_results">
        <section id="product1">      
            <div class="pro-container"> 
                <% 
                    for (ProxyProdotto product : products) { %>
                    <%                        
                        
                    %>
                    
                    <div class="pro">
                        <img src="image?productId=<%= product.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"/>
                        <div class="des">
                            <h3><%=product.getNomeProdotto()%></h3>
                            <span><%=product.getMarca()%></span>
                            <h5><%=product.getTopDescrizione()%></h5>
                            <div class="star">                              
                            </div>
                            <h4><%=product.getPrezzo()%>€</h4>
                        </div>
                        <a href="#" onClick="modifyCart(<%=product.getCodiceProdotto()%>,'aggiungiAlCarrello')">
                            <img class="cart" src="${pageContext.request.contextPath}/view/img/icon_carrello2.png">
                        </a>
                        <a href="GestioneWishlistController?action=addtowishlist">
                            <img  src="${pageContext.request.contextPath}/view/img/icon_wishlist.png" style="margin: 1px; width: 25px; height: 25px">
                        </a>
                        <div id="error">
                            <% String errormsg="";
                                errormsg= (String)request.getAttribute("error");
                                if(errormsg==null) errormsg="";                                                       
                            %>
                            <%=errormsg%>
                        </div>
                    </div>                    
             <%}%>
              </div>
        </section>
        </div>
    </div>      
    <!-- Pagination links -->
    <div id="pagination">
        <% if (totalPages > 1) { %>
            <% for (int pager = 1; pager <= totalPages; pager++) { %>
                <%
                    String pageUrl = "NavigazioneController?page=" + pager + "&keyword=" + keyword + "&search_type="+request.getAttribute("search_type");
                %>
                <a href="<%= pageUrl %>"><%=pager%></a>
            <% } %>
        <% } %>
    </div>
    <jsp:include page="common/footer.jsp"  flush="true"/> 
    </body>
</html>
