<%-- 
    Document   : fill_order_details
    Created on : 10 ago 2024, 17:37:55
    Author     : raffa
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"
		import = "java.util.HashMap,
				  application.NavigazioneService.ProxyProdotto,
				  application.NavigazioneService.Prodotto,
				  application.GestioneCarrelloService.ItemCarrello,
				  java.util.ArrayList,
				  application.GestioneOrdiniService.Ordine"%>
				  
<!DOCTYPE html>
<html lang = "en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Fill Order Infos Page</title>
        
        <%
            Ordine selected_ordine = (Ordine) request.getSession().getAttribute("selected_ordine");
            if(selected_ordine==null){
                response.sendRedirect(request.getContextPath() + "protected/gestoreOrdini/GestioneOrdini.jsp");
                return;
            }
            ArrayList<ItemCarrello> order_products = (ArrayList<ItemCarrello>) request.getSession().getAttribute("order_products");
            HashMap order_products_available = (HashMap) request.getSession().getAttribute("order_products_available");
        %>
    </head>
    <body>
       <jsp:include page="/common/header.jsp"  flush="true"/>
       <jsp:include page="/protected/cliente/roleSelector.jsp"  flush="true"/>
        <h1>Informazioni sull'Ordine Selezionato:</h1>
        <div id="product1">
            <h2><%=selected_ordine.getStatoAsString()%></h2>
            <h4><%=selected_ordine.getIndirizzoSpedizione()%></h4>
            <h4><%=selected_ordine.getSpedizioneAsString()%></h4>
            <h4><%=selected_ordine.getAcquirente()%></h4>
        </div>       
        <div class="section-p1">
            <h4>Informazioni di spedizione:</h4>
            <form id="fill_order_form" class="reg_form" action="GestioneOrdiniController?action=complete_order" method="post">
                <p>Lista Prodotti E Relative Quantità Richieste:</p>
                <% for (ItemCarrello item : order_products){%>
                <div class="row">
                    <h2><%= item.getCodiceProdotto()%></h2>
                    <h3><%= item.getNomeProdotto()%></h3>
                    <img src="image?productId=<%= item.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/images/site_images/placeholder.png';"/>
                </div>
                <p id="range_value"><%=item.getQuantita()%></p>
                <div class="input-wrapper row">     
                    <input type="hidden" name="product_id[]" value="<%=item.getCodiceProdotto()%>">
                    <input type="range" id="item_amount" name="item_amount[]" min="<%=item.getQuantita()%>" max="<%= order_products_available.get(item.getCodiceProdotto())%>">         
                </div>
              <%}%>  
                <div class="input-wrapper">  
                    <p>Inserisci informazioni sull'imballaggio:</p>
                    <textarea id="Imballaggio" name="Imballaggio" rows="4" cols="50" required></textarea>
                    <span id="charCountImballaggio">0/100</span> <!-- Added for character count -->
                    <span id="charWarningImballaggio" class="warning">Character limit exceeded!</span><br><br>
                </div>
                <div class="input-wrapper">  
                    <p>Inserisci informazioni sull'azienda di spedizioni:</p>
                    <textarea id="Corriere" name="Corriere" rows="4" cols="50" required></textarea>
                    <span id="charCountCorriere">0/60</span> <!-- Added for character count -->
                    <span id="charWarningCorriere" class="warning">Character limit exceeded!</span><br><br>
                </div>
                <button class="confirm_button" onClick="setActionForOrderSent()" type="submit">Conferma Preparazione Ordine</button>
            </form><!--<a href="GestioneOrdiniController?action=incomplete_order" >-->
                 <button onclick="setActionAndRedirect('incomplete_order')" class="confirm_button" type="submit">Annulla Preparazione Ordine</button></a>    
        </div>
                <div class="errormsg">
                    <p id="error"></p>                  
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
        <script>
            function setActionAndRedirect(action) {
                sessionStorage.setItem('action', action); // Store action in session storage
                // Get the context path from the current URL
                var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

                // Redirect to the main page with the context path
                window.location.href = contextPath + '/GestioneOrdini';
            }
            function setActionForOrderSent() {
                sessionStorage.setItem('action', 'fetch_spediti'); // Store the 'order_sent' action
               // window.location.href = 'GestioneOrdini'; // Redirect to the main page
            }
            // Attach validation to form submit
        document.getElementById('fill_order_form').addEventListener('submit', function(event) {
            if (!validateForm()) {
                event.preventDefault(); // Prevent form submission
                alert('Please correct the errors in the form before submitting.');
            }
        });
    // Adding validation to different textareas with different character limits
    addCharacterLimitValidation('Corriere', 'charCountCorriere', 'charWarningCorriere', 60);
    addCharacterLimitValidation('Imballaggio', 'charCountImballaggio', 'charWarningImballaggio', 100);

    </script>
        <script src="<%= request.getContextPath()%>/scripts/validate_fill_order.js?ts=<%= System.currentTimeMillis() %>"></script>
    </body>
</html> 
			
