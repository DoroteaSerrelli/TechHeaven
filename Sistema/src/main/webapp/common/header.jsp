
<%@page import="java.util.HashMap"%>
<%@page import="application.GestioneCarrelloService.ItemCarrello"%>
<%@page import="application.GestioneCarrelloService.GestioneCarrelloServiceImpl"%>
<%@page import="application.NavigazioneService.Prodotto"%>
<%@page import="application.GestioneCarrelloService.Carrello"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html  lang="en">
    <head><title>Header</title>
        <!-- Basta includere uno script nella sezione di file in cui 
        viene usato e basta importarlo nel file contenente la sezione usata 
        senza doverlo importare da altre parti-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js" integrity="sha512-3gJwYpMe3QewGELv8k/BX9vcqhryRdzRMxVfq6ngyWXwo03GFEzjsUm8Q7RZcHPHksttq7/GFoxjCVUjkjvPdw==" crossorigin="anonymous"></script> 
    <script src="${pageContext.request.contextPath}/view/navi_script.js"></script>
    
    <script src="${pageContext.request.contextPath}/view/cartAndSearch_functions.js?ts=<%= System.currentTimeMillis() %>"></script>
    
    </head>    
       <link rel="stylesheet" href="${pageContext.request.contextPath}/common/style.css"> 
       <section id="header">
           <a href="#"> <img src="${pageContext.request.contextPath}\view\img\logo.png" height="100" width="100" class="logo" alt="alt"/></a>
        <div>              
            <ul id="navbar">
                <li><a class="active" href="${pageContext.request.contextPath}/">Home</a></li> 
                <li><a href="${pageContext.request.contextPath}/Autenticazione">Login</a></li> 
                <li><a href="${pageContext.request.contextPath}/Registrazione">Register</a></li> 
                <li><a href="${pageContext.request.contextPath}/GestioneWishlistController?action=viewwishlist"><img src="${pageContext.request.contextPath}/view/img/icon_wishlist.png" height="30" width="30"></a></li>
                <li><a href="${pageContext.request.contextPath}/AreaRiservata"><img src="${pageContext.request.contextPath}/view/img/icon_user.png" height="30" width="30"></a></li>
                                            
                <li><a id="lg-bag">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="30" height="30">
                        <path fill="#2DA0F2" d="M16 6h-1V5c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v1H8c-1.1 0-1.99.9-1.99 2L6 19c0 1.1.89 2 1.99 2H17c1.1 0 2-.9 2-2l.01-11c0-1.1-.89-2-1.99-2zM9 5h6v1H9V5zm8 14H7V9h10v10z"/>
                        <path fill="none" d="M0 0h24v24H0V0z"/>
                    </svg>
                    </a>
                </li>
                <a href="#" id="close" onClick="closeSidebar()">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                        <path fill="#2DA0F2" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z"/>
                    </svg>                
                </a>                     
            </ul>  
            <ul id="categories">            
                <li><a href="${pageContext.request.contextPath}/NavigazioneController?search_type=menu&keyword=TELEFONIA">Telefonia</a></li> 
                <li><a href="${pageContext.request.contextPath}/NavigazioneController?search_type=menu&keyword=GRANDI_ELETTRODOMESTICI">Grandi Elettrodomestici</a></li> 
                <li><a href="${pageContext.request.contextPath}/NavigazioneController?search_type=menu&keyword=PRODOTTI_ELETTRONICA">Prodotti Elettronica</a></li>
                <li><a href="${pageContext.request.contextPath}/NavigazioneController?search_type=menu&keyword=PICCOLI_ELETTRODOMESTICI">Piccoli Elettrodomestici</a></li>
           </ul> 
                <ul id="search_section">
                    <li>
                    <form id="searchForm" method="post" action="${pageContext.request.contextPath}/NavigazioneController?search_type=bar" onsubmit="return validateSearch()">
                        <button class="search" type="submit">
                            <img src="${pageContext.request.contextPath}/view/img/search.png" width="30" height="30" alt="Search">
                        </button>
                        <input id="searchInput" type="text" name="keyword" required="">                        
                    </form>     
                    </li> 
                </ul>     
        </div>
         <div id="carrello" > 
             <div id="carrelloroba">
             <a href="${pageContext.request.contextPath}/cart">Mosttra il Carrello</a>
             <br>
                <%
                    if(request.getSession().getAttribute("usercart")==null);
                    else{
                        Carrello cart = (Carrello)request.getSession().getAttribute("usercart"); 
                        HashMap products_available_inStock = (HashMap) request.getSession().getAttribute("products_available_inStock"); 
                        for(ItemCarrello p: cart.getProducts()){  
                %>
                <div class="cart-item">
                    <img src="data:image/jpg;base64, <%=p.getTopImmagine()%>" alt="alt">
                    <span class="product-name"><%= p.getNomeProdotto() %></span>
                    <span ><%= p.getTopDescrizione()%></span>               
                    <span class="quantity">Quantity: <%= p.getQuantita() %></span>
                    <a href="#" onClick="modifyCart(<%=p.getCodiceProdotto()%>,'rimuoviDalCarrello')">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                            <path fill="#2DA0F2" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z"/>
                        </svg>
                    </a>
                     <% if (p.getQuantita() >= 1) { %>
                        <!-- Show modify link if quantity is more than one -->
                        <p id="range_value_<%= p.getCodiceProdotto() %>" style="color: goldenrod"><%= p.getQuantita() %></p>
                        <div class="input-wrapper row">                                
                            <input type="range" id="prod_quantità_<%= p.getCodiceProdotto() %>" name="prod_quantità" min="1" max="<%= products_available_inStock.get(p.getCodiceProdotto()) %>">         
                        </div>
                        <a href="#" onclick="modifyCart(<%= p.getCodiceProdotto() %>, 'updateQuantità')">Update Quantity</a>         
                    <% } %>  
                </div>
               <% }}%>
             </div>  
        </div>  
        <div id="mobile">
            <a href="${pageContext.request.contextPath}/cart">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                    <path fill="#2DA0F2" d="M16 6h-1V5c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v1H8c-1.1 0-1.99.9-1.99 2L6 19c0 1.1.89 2 1.99 2H17c1.1 0 2-.9 2-2l.01-11c0-1.1-.89-2-1.99-2zM9 5h6v1H9V5zm8 14H7V9h10v10z"/>
                    <path fill="none" d="M0 0h24v24H0V0z"/>
                </svg>
            </a>
            <button class="openbtn" onclick="openSidebar()" type="button">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                <path fill="#2DA0F2" d="M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z"/>
            </button>         
        </div>
        <br>         
        </section>
    <script>
        // Add event listener for close button
        document.getElementById('close').addEventListener('click', function() {
            closeSidebar();
        });

        // Add event listener for search form submission
        document.getElementById('searchForm').addEventListener('submit', function(event) {
            event.preventDefault(); // Prevent default form submission
            if (validateSearch()) {
                this.submit(); // Submit the form
            } else {
                // Handle invalid search input
                displayNotification("Please enter a valid search query.");
            }
        });
    </script>      
</html>