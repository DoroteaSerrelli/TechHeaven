<%-- 
    Document   : richiesteApprovigionamento
    Created on : 21 ago 2024, 16:11:35
    Author     : raffa
--%>

<%@page import="application.GestioneApprovvigionamenti.RichiestaApprovvigionamento"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Richieste Approvvigionamento</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/style.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/product_table.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/extra_manager_style.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/catalog_options.css">
        <script src="<%= request.getContextPath()%>/scripts/ajax_orders_table_functions.js?ts=<%= System.currentTimeMillis() %>"></script> 
        
        <%
            Collection<RichiestaApprovvigionamento> requests = (Collection<RichiestaApprovvigionamento>) request.getAttribute("supply_requests");
            if(requests==null ||requests.isEmpty()){ %>
            <h4>Nessun richiesta trovata%></h4>
            <%}
            int pagen = (int) request.getAttribute("page");
           
        %>
    </head>
    <body>
        <jsp:include page="/common/header.jsp"  flush="true"/>
        <button id="sidebar_toggle"><img src="<%= request.getContextPath()%>/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>
        <input type="text" id="productFilter" onkeyup="filterProducts()" placeholder="Search for request by company name...">
        <jsp:include page="/protected/gestoreOrdini/toolbar.jsp"  flush="true"/>
        
        <!-- Search Input Field -->
        <!-- Pagination links -->
        <jsp:include page="/common/pagination.jsp"  flush="true"/>   
        <table id="showpr" style="width: 80%; margin: 0 auto">
            <tr>
                <th><strong>Codice Richiesta</strong></th><!-- Codice Richesta -->
                <th><strong>Nominativo Fornitore</strong></th><!-- Nominativo Fornitore -->
                <th><strong>Email Fornitore</strong></th><!-- Nome prodotto -->
                <th><strong>Quantità Rifornimento</strong></th><!-- Quantità Richiesta Rifornimento -->
                <!-- <th><strong>Marca</strong></th> Marca -->             
                <th><strong>Descrizione</strong></th><!-- Descrizione dettagliata richiesta approvigionamento -->
            </tr> 
             <% 
                    for (RichiestaApprovvigionamento rquest : requests) { %>
                    <%                        
                        
                    %>
            <tr id="row-<%= rquest.getCodiceRifornimento() %>">
                <td><%= rquest.getCodiceRifornimento() %></td> 
                <td class="productName">  
                    <h3><%=rquest.getFornitore()%></h3>
                </td>               
                <td>  
                    <h3><%=rquest.getEmailFornitore()%></h3>
                </td>  
                <td><span><%=rquest.getQuantitaRifornimento()%></span></td>  
                <td><%= rquest.getDescrizione()%></td>    
            </tr>
       <%}%>
        </table> 
        
    </body>
</html>
