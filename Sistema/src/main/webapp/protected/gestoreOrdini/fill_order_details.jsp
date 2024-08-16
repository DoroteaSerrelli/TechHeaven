<%-- 
    Document   : fill_order_details
    Created on : 10 ago 2024, 17:37:55
    Author     : raffa
--%>

<%@page import="java.util.HashMap"%>
<%@page import="application.NavigazioneService.ProxyProdotto"%>
<%@page import="application.NavigazioneService.Prodotto"%>
<%@page import="application.GestioneCarrelloService.ItemCarrello"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.GestioneOrdiniService.Ordine"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Fill Order Infos Page</title>
        <%
            Ordine selected_ordine = (Ordine) request.getAttribute("selected_ordine");
            if(selected_ordine==null){
                response.sendRedirect("/protected/gestoreOrdini/GestioneOrdini.jsp");
                return;
            }
            ArrayList<ItemCarrello> order_products = (ArrayList<ItemCarrello>) request.getAttribute("order_products");
            HashMap order_products_available = (HashMap) request.getAttribute("order_products_available");
        %>
    </head>
    <body>
       <jsp:include page="../../common/header.jsp"  flush="true"/>
       <jsp:include page="../../roleSelector.jsp"  flush="true"/>
        <h1>Informazioni sull'Ordine Selezionato:</h1>
        <div id="product1">
            <h2><%=selected_ordine.getStatoAsString()%></h2>
            <h4><%=selected_ordine.getIndirizzoSpedizione()%></h4>
            <h4><%=selected_ordine.getSpedizioneAsString()%></h4>
            <h4><%=selected_ordine.getAcquirente()%></h4>
        </div>       
        <h4>Informazioni di spedizione:</h4>
        <form class="form">                
            <p>Lista Prodotti E Relative Quantità Richieste:</p>
            <% for (ItemCarrello item : order_products){%>
            <div class="input-wrapper row">  
                <input type="range" name="item_amount" min="<%=item.getQuantita()%>" max="<%= order_products_available.get(item.getCodiceProdotto())%>">                      
            </div>
          <%}%>  
            <div class="input-wrapper">  
                <p>Inserisci informazioni sull'imballaggio:</p>
                <input type="textarea" name="Imballaggio" oninput="validateName()" required>
            </div>
            <div class="input-wrapper">  
                <p>Inserisci informazioni sull'azienda di spedizioni:</p>
                <input type="textarea" name="Corriere" oninput="validateName()" required>
            </div>
        </form>
    </body>
</html>
