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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/extra_manager_style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/catalog_options.css">
        <script src="${pageContext.request.contextPath}/view/ajax_orders_table_functions.js?ts=<%= System.currentTimeMillis() %>"></script> 
        <script src="${pageContext.request.contextPath}/view/validate_fill_order.js?ts=<%= System.currentTimeMillis() %>"></script> 
        
        <%
            Collection<ProxyProdotto> products = (Collection<ProxyProdotto>) request.getAttribute("all_pr_list");
            if(products==null ||products.isEmpty()){ %>
            <h4>Nessun prodotto trovato.</h4>
            <%}          
        %>
    </head>
    <body>
        <div id="error">
            <% String errormsg="";
                errormsg= (String)request.getAttribute("error");
                if(errormsg==null) errormsg="";                                                       
            %>
            <h4><%=errormsg%></h4>
        </div>
        <jsp:include page="/protected/gestoreOrdini/toolbar.jsp"  flush="true"/>       
        <jsp:include page="/common/header.jsp"  flush="true"/>
        <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/view/img/sidebar_toggle.png" onclick="toggleSidebar()"></button>
        <!-- Search Input Field -->
        <input type="text" id="productFilter" onkeyup="filterProducts()" placeholder="Search for products by name...">
        <!-- Search Input Field -->
        <!-- Pagination links -->
        <jsp:include page="/common/pagination.jsp"  flush="true"/>       
        
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
            <tr id="row-<%= product.getCodiceProdotto() %>">
                <td>  
                    <h3><%=product.getCodiceProdotto()%></h3>
                </td>
                <td><img src="image?productId=<%= product.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"/>
                </td>
                <td class="productName" style="width: 25%;">  
                    <h3><%=product.getNomeProdotto()%></h3>
                </td>  
             <!--   <td><span><%//=product.getMarca()%></span></td>  -->
                <td><%= product.getQuantita()%> <button class="confirm_button" onclick="showSupplyForm('<%= product.getCodiceProdotto() %>')">Rifornisci</button></td>    
            </tr>
       <%}%>
        </table>
        <form id="supplyingRequestForm" class="">
            <input id="product_id" type="hidden" name="product_id" value="">
        </form>           
        <jsp:include page="/common/footer.jsp"  flush="true"/>
    </body>
</html>
