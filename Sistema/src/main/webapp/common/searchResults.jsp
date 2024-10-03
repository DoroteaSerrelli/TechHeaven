<%-- 
    Document   : searchResult
    Created on : 18-apr-2024, 18.39.30
    Author     : raffy
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"
		import = "java.util.Currency,
				  java.text.NumberFormat,
				  java.text.DecimalFormat,
				  java.net.URLEncoder,
				  com.google.gson.Gson,
				  java.util.Base64,
				  application.NavigazioneService.NavigazioneServiceImpl,
				  java.lang.String,
				  java.util.Set,
				  java.util.HashSet,
				  application.NavigazioneService.ProxyProdotto,
				  java.util.Collection,
				  application.NavigazioneService.Prodotto"%>

<%
    Collection<ProxyProdotto> products = (Collection<ProxyProdotto>) request.getSession().getAttribute("products"); 
    String keyword = (String) request.getAttribute("keyword");
%>

<!DOCTYPE html>
<html lang="en">
   <head>
    <meta charset="UTF-8" name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TechHeaven - Risultati Ricerca</title>
    <link rel="stylesheet" href="<%= request.getContextPath()%>/style/style.css">
    <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script> 
    </head>
    <body>
       
       <jsp:include page="/common/header.jsp"  flush="true"/>
    <h1>Risultati prodotti dalla ricerca</h1>   
    <!-- Display search results here -->
    <div class="container">
        <div class="sidebar">
            <h2>Brands:</h2>
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
               <!-- Pagination links -->
        <%   if(products==null ||products.isEmpty()){ %>
                <h4>Nessun prodotto trovato con la keyword: <%=request.getAttribute("keyword")%></h4>
        <%  } else{ %>
            <jsp:include page="/common/pagination_research.jsp"  flush="true"/>
        <!--    <div class="errormsg">                                             
               <p id="error" class="error"></p>                                        
            </div> -->
        <section id="product1">      
            <div class="pro-container"> 
                <% 
                    for (ProxyProdotto product : products) { %>
                    <%                        
                        
                    %>
                    
                    <div class="pro">
                        <%
                            String productJson = new Gson().toJson(product);
                            String encodedProductJson = URLEncoder.encode(productJson, "UTF-8");
                        %>
                      <!-- pageContext.request.contextPath <a href="/ProductInfos?product=<// encodedProductJson %>"> </a>-->                                  
                            <img src="image?productId=<%= product.getCodiceProdotto() %>" alt="alt" 
                                onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/images/site_images/placeholder.png';" />
                      
                            <div class="des">
                                <h3><%=product.getNomeProdotto()%></h3>
                                <span><%=product.getMarca()%></span>
                                <div class="star">                              
                                </div>
                                <%
                                    double prezzo = product.getPrezzo();
                                       // Optionally, specify a particular currency
                                     // Create a NumberFormat instance for currency formatting
                                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

                                        // Optionally, specify a particular currency
                                        currencyFormatter.setCurrency(Currency.getInstance("EUR")); // Use "USD" for US dollars, "EUR" for Euros, etc.

                                        String prezzoFormattato = currencyFormatter.format(prezzo);
                                    %>
                                <h4 class="prezzo"><%=prezzoFormattato%></h4>
                            <a class="dettagli" id="<%= product.getCodiceProdotto()%>" href="javascript:void(0);" 
                                  onclick="submitProductDetails('<%= encodedProductJson %>');">
                                <p>  Dettagli </p>
                            </a> 
                            </div>
                            <%if(product.getQuantita() > 0) {%>
                            <a href="#"
                                onClick="addToCartAndRedirect(<%= product.getCodiceProdotto() %>, 'addToCart', '<%= request.getContextPath() %>/cart');">
                                <img class="cart"
                                src="<%=request.getContextPath()%>/images/site_images/icon_carrello2.png">
                            </a>  
                            <%} %>                                         
                            <a href="GestioneWishlistController?action=addtowishlist&productId=<%= product.getCodiceProdotto() %>">
                                <img class="wishlist" src="<%= request.getContextPath()%>/images/site_images/icon_wishlist.png" alt = "wishlist">
                            </a>    
                    </div>                    
             <%} 
        }%>
              </div>
        </section>
        </div>
    </div> 
    <jsp:include page="/common/footer.jsp"  flush="true"/> 
    </body>
</html>
