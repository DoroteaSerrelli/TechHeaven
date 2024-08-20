<%-- 
    Document   : approvigionamento
    Created on : 19 ago 2024, 15:30:15
    Author     : raffa
--%>

<%@page import="java.util.HashMap"%>
<%@page import="application.NavigazioneService.ProxyProdotto"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Approvigionamento</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/common/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/product_table.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/catalog_options.css">
        <script src="${pageContext.request.contextPath}/view/ajax_orders_table_functions.js?ts=<%= System.currentTimeMillis() %>"></script>       
        <%
            Collection<ProxyProdotto> products = (Collection<ProxyProdotto>) request.getAttribute("products");
            if(products==null ||products.isEmpty()){ %>
            <h4>Nessun prodotto trovato con la keyword: <%=request.getAttribute("keyword")%></h4>
            <%}
            int totalPages = (int) request.getAttribute("totalPages");
           
        %>
    </head>
    <body>
        <aside class="options_sidebar visible" id="options_sidebar">
            <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/view/img/sidebar_toggle.png" onclick="toggleSidebar()"></button>     
            <div class="fe-box" id="viewOrders">
                    <a href="${pageContext.request.contextPath}/GestioneOrdini"><img src="${pageContext.request.contextPath}/view/img/ordinidaspedire.png" alt="Visualizza Ordini Clienti">
                    <h5>Visualizza Ordini Da Spedire</h5>
                </div>
                    <div class="fe-box" id="viewSentOrders">
                        <a href="${pageContext.request.contextPath}/GestioneOrdini"><img src="${pageContext.request.contextPath}/view/img/ordinispediti.png" alt="Visualizza Ordini Spediti">
                        </a>
                        <h5>Visualizza Ordini Spediti</h5>
                    </div>
                    <div class="fe-box" id="supplyingRequests">
                        <a href="GestioneApprovigionamentiController?action=viewList&&page=1"><img src="${pageContext.request.contextPath}/view/img/supplyrequests.png" alt="Richieste Approvigionamento"></a>
                        <h5>Richieste Approvigionamento</h5>
                    </div>
                    <div class="fe-box" id="supplyingRequest" onclick="moveToSidebar('supplyingRequest', 'supplyingRequestForm')">
                        <a href="GestioneApprovigionamentiController?action=viewProductList&page=1"><img src="${pageContext.request.contextPath}/view/img/apprrovigionamento.png" alt="Approvigionamento">
                        </a>
                        <h5>Approvigionamento</h5>
                    </div>
        </aside>
        <jsp:include page="${pageContext.request.contextPath}/common/header.jsp"  flush="true"/>
        <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/view/img/sidebar_toggle.png" onclick="toggleSidebar()"></button>
        <!-- Search Input Field -->
        <input type="text" id="productFilter" onkeyup="filterProducts()" placeholder="Search for products by name...">
        <div id="pagination">
            <% if (totalPages > 1) { %>
                <% for (int pager = 1; pager <= totalPages; pager++) { %>
                    <%
                        String pageUrl = "GestioneApprovigionamentiController?page=" + pager + "&action=viewProductList";
                    %>
                    <a href="<%= pageUrl %>"><%=pager%></a>
                <% } %>
            <% } %>
        </div>
        <table id="showpr" style="width: 80%; margin: 0 auto">
            <tr>
                <th><strong>Codice Prodotto</strong></th><!-- Codice Prodotto -->
                <th><strong>Image</strong></th><!-- Immagine -->
                <th><strong>Nome</strong></th><!-- Nome prodotto -->
                <!-- <th><strong>Marca</strong></th> Marca -->             
                <th><strong>Quantità Residua</strong></th><!-- Amount Left In Store -->
            </tr> 
             <% 
                    for (ProxyProdotto product : products) { %>
                    <%                        
                        
                    %>
            <tr>
                <td>  
                    <h3><%=product.getCodiceProdotto()%></h3>
                </td>
                <td><img src="image?productId=<%= product.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"/>
                </td>
                <td class="productName" style="width: 25%;">  
                    <h3><%=product.getNomeProdotto()%></h3>
                </td>  
             <!--   <td><span><%//=product.getMarca()%></span></td>  -->
                <td><%= product.getQuantita()%></td>    
            </tr>
       <%}%>
        </table>
       <div id="error">
            <% String errormsg="";
                errormsg= (String)request.getAttribute("error");
                if(errormsg==null) errormsg="";                                                       
            %>
            <%=errormsg%>
        </div>
        <jsp:include page="${pageContext.request.contextPath}/common/footer.jsp"  flush="true"/>
    </body>
</html>
