<%-- 
    Document   : searchResult
    Created on : 18-apr-2024, 18.39.30
    Author     : raffy
--%>

<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="application.NavigazioneService.ProxyProdotto"%>
<%@page import="java.util.Collection"%>
<%@page import="application.NavigazioneService.Prodotto"%>
<%
    Collection<ProxyProdotto> products = (Collection<ProxyProdotto>) request.getAttribute("products");
    int totalPages = (int) request.getAttribute("totalPages");
    String keyword = (String) request.getAttribute("keyword");
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
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
                <% for (ProxyProdotto product : products) { %>                     
                    <div class="pro">
                       <img src="" alt="alt">
                        <div class="des">
                            <h3><%=product.getNomeProdotto()%></h3>
                            <span><%=product.getMarca()%></span>
                            <h5><%=product.getTopDescrizione()%></h5>
                            <div class="star">                              
                            </div>
                            <h4><%=product.getPrezzo()%>€</h4>
                        </div>
                        <a href="Shop">
                            <img class="cart" src="${pageContext.request.contextPath}/view/img/icon_carrello2.png">
                        </a>
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
                    String pageUrl = "/BarraRicercaController?page=" + pager + "&keyword=" + keyword;
                %>
                <a href="<%= pageUrl %>"><%=pager%></a>
            <% } %>
        <% } %>
    </div>
    <jsp:include page="common/footer.jsp"  flush="true"/> 
    </body>
</html>
