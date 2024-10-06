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
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/extra_manager_style.css">
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
            <div id="order_details">
              <table>
                    <tr>
                        <th>Stato:</th>
                        <td><%= selected_ordine.getStatoAsString() %></td>
                    </tr>
                    <tr>
                        <th>Indirizzo Spedizione:</th>
                        <td><%= selected_ordine.getIndirizzoSpedizione() %></td>
                    </tr>
                    <tr>
                        <th>Tipo Spedizione:</th>
                        <td><%= selected_ordine.getSpedizioneAsString() %></td>
                    </tr>
                    <tr>
                        <th>Info Acquirente:</th>
                        <td><%= selected_ordine.getAcquirente() %></td>
                    </tr>
                </table>
            </div>  
        <div class="section-p1">             
            <h1>Informazioni di spedizione:</h1>
            <form id="fill_order_form" class="reg_form" action="<%=request.getContextPath()%>/GestioneOrdiniController?action=complete_order" method="post">
                <p>Lista Prodotti E Relative Quantità Richieste:</p>
                <% for (ItemCarrello item : order_products){%>
                <div class="row">
                    <h2>ID: <%= item.getCodiceProdotto()%></h2>
                    <h2>Quantità Richiesta: <%= item.getQuantita() %></h2>
                </div>
                   <h3>Nome: <%= item.getNomeProdotto()%></h3> 
                    <img src="image?productId=<%= item.getCodiceProdotto() %>" alt="alt" width="200" height="200"
                     onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/images/site_images/placeholder.png';"/>
                <p id="range_value"><%=item.getQuantita()%></p>
                <div class="input-wrapper row">     
                    <input type="hidden" name="product_id[]" value="<%=item.getCodiceProdotto()%>">
                    <input type="range" id="item_amount" name="item_amount[]" min="<%=item.getQuantita()%>" 
                           max="<%= order_products_available.get(item.getCodiceProdotto())%>" oninput="validateQuantity(this.value)">         
                </div>
              <%}%>  
                <p>Inserisci informazioni sull'imballaggio:</p>
                <div class="input-wrapper">               
                    <textarea id="Imballaggio" name="Imballaggio" rows="4" cols="50" required></textarea>
                    <span id="charCountImballaggio">0/100</span> <!-- Added for character count -->
                    <span id="charWarningImballaggio" class="warning">Superato il limite di caratteri ammissibili!</span><br>                
                </div>
                <div id="errorImballaggio" class="erromsg"></div>
                <p>Inserisci informazioni sull'azienda di spedizioni:</p>
                <div class="input-wrapper">                   
                    <textarea id="Corriere" name="Corriere" rows="4" cols="50" required></textarea>
                    <span id="charCountCorriere">0/60</span> <!-- Added for character count -->
                    <span id="charWarningCorriere" class="warning">Superato il limite di caratteri ammissibili!</span><br>                 
                </div>
                <div id="errorCorriere" class="erromsg"></div>
                <button class="confirm_button" onClick="setActionForOrderSent()" type="submit">Conferma Preparazione Ordine</button>
                <button class="cancel_button"  onclick="setActionAndRedirect('incomplete_order')" type="submit">Annulla Preparazione Ordine</button></a>      
            </form><!--<a href="GestioneOrdiniController?action=incomplete_order" >-->
        </div>
                <div class="errormsg">
                    <p id="error"></p>                  
                </div>         
                
        <script src="<%= request.getContextPath()%>/scripts/validate_fill_order.js?ts=<%= System.currentTimeMillis() %>"></script>
    </body>
</html> 
			
