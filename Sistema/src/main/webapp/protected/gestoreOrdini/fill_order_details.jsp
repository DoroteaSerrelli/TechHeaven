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
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Fill Order Infos Page</title>
        <script src="${pageContext.request.contextPath}/view/validations.js"></script>
        
        <%
            Ordine selected_ordine = (Ordine) request.getSession().getAttribute("selected_ordine");
            if(selected_ordine==null){
                response.sendRedirect("GestioneOrdini.jsp");
                return;
            }
            ArrayList<ItemCarrello> order_products = (ArrayList<ItemCarrello>) request.getSession().getAttribute("order_products");
            HashMap order_products_available = (HashMap) request.getAttribute("order_products_available");
        %>
    </head>
    <body>
       <jsp:include page="${pageContext.request.contextPath}/common/header.jsp"  flush="true"/>
       <jsp:include page="${pageContext.request.contextPath}/roleSelector.jsp"  flush="true"/>
        <h1>Informazioni sull'Ordine Selezionato:</h1>
        <div id="product1">
            <h2><%=selected_ordine.getStatoAsString()%></h2>
            <h4><%=selected_ordine.getIndirizzoSpedizione()%></h4>
            <h4><%=selected_ordine.getSpedizioneAsString()%></h4>
            <h4><%=selected_ordine.getAcquirente()%></h4>
        </div>       
        <div class="section-p1">
            <h4>Informazioni di spedizione:</h4>
            <form class="reg_form" action="GestioneOrdiniController?action=complete_order" method="post">
                <p>Lista Prodotti E Relative Quantità Richieste:</p>
                <% for (ItemCarrello item : order_products){%>
                <div class="row">
                    <h2><%= item.getCodiceProdotto()%></h2>
                    <h3><%= item.getNomeProdotto()%></h3>
                    <img src="image?productId=<%= item.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"/>
                </div>
                <p id="range_value"><%=item.getQuantita()%></p>
                <div class="input-wrapper row">     
                    <input type="hidden" name="product_id[]" value="<%=item.getCodiceProdotto()%>">
                    <input type="range" id="item_amount" name="item_amount[]" min="<%=item.getQuantita()%>" max="<%= order_products_available.get(item.getCodiceProdotto())%>">         
                </div>
              <%}%>  
                <div class="input-wrapper">  
                    <p>Inserisci informazioni sull'imballaggio:</p>
                    <textarea name="Imballaggio" rows="4" cols="50" required></textarea>
                </div>
                <div class="input-wrapper">  
                    <p>Inserisci informazioni sull'azienda di spedizioni:</p>
                    <textarea name="Corriere" rows="4" cols="50" required></textarea>
                </div>
                <button class="confirm_button" type="submit">Conferma Preparazione Ordine</button>
            </form>
                <a href="GestioneOrdiniController?action=incomplete_order"<button class="confirm_button" type="submit">Anulla Preparazione Ordine</button></a>    
        </div>       
        <script>
            document.addEventListener('DOMContentLoaded', (event) => {
            // Get the range input element and the span where the value will be displayed
            const rangeInput = document.getElementById('item_amount');
            const rangeValue = document.getElementById('range_value');

            // Function to update the value display
            function updateRangeValue() {
                rangeValue.textContent = rangeInput.value;
            }

            // Initialize the display with the current value
            updateRangeValue();

            // Add an event listener to update the value when the slider is moved
            rangeInput.addEventListener('input', updateRangeValue);
        });
        </script>
    </body>
</html> 
			
