<%-- 
    Document   : CompletaOrdine
    Created on : 17 set 2024, 19:00:33
    Author     : raffa
--%>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="application.RegistrazioneService.Cliente"%>
<%@page import="application.RegistrazioneService.Utente"%>
<%@page import="java.util.Currency"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="application.GestioneCarrelloService.ItemCarrello"%>
<%@page import="application.GestioneCarrelloService.Carrello"%>
<%@page import="application.RegistrazioneService.Indirizzo"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.RegistrazioneService.ProxyUtente"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang='it'>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta charset="UTF-8">
        <link rel="stylesheet"
                href="<%= request.getContextPath()%>/style/style.css">
        <link rel="stylesheet"
                href="<%= request.getContextPath()%>/style/cart.css">
        <title>TechHeaven - Completa l'acquisto</title>    
        <jsp:include page="common/header.jsp"
		flush="true" />
    </head>
    <body>
        <div id="showpr" class="section-p1">
         <div class="errormsg">        
                <% 
                String err = (String)request.getSession().getAttribute("error");
                if (err != null && !err.isEmpty()) {
                %>
              <p id="error">  <%=err%></p>
                <% } %>
            </div>   
         <%           
            ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");

            //I checks li faccio nella servlet questa parte qui è inutile           
            if (u==null || u.getUsername().equals("")) {
               response.sendRedirect("Autenticazione");
               return ;
           }
           else {   
                Utente user = u.mostraUtente();
        %>            
        
       
        <%
            Carrello carrello = (Carrello) request.getSession().getAttribute("usercart");%>
            <button id="drawer-toggle" onclick="toggleDrawer()"><h1>Totale Ordine:</h1></button> 
        <div class="complete_order">    
            <div id="complete_order">   
                <h1>:Totale Ordine</h1>
                <div id="cart">
                <p><%=carrello.getNumProdotti()%> :Item nel Carrello</p>
                <%  for (ItemCarrello p : carrello.getProducts()) {
                        %> 
                    <div class="cart-item">
                        <p><%= p.getNomeProdotto() + "  " %> Quantità: <%= p.getQuantita() %></p>
                        <div class="row">
                            <img src="image?productId=<%= p.getCodiceProdotto() %>" alt="alt" width="20%" height="20%"
                                onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/images/site_images/placeholder.png';" />			
                            <%
                                double prezzo = p.getPrezzo() * p.getQuantita();
                                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
                                currencyFormatter.setCurrency(Currency.getInstance("EUR"));
                                String prezzoFormattato = currencyFormatter.format(prezzo);
                                %>
                                <h4 style="color: goldenrod" class="prezzo"><%=prezzoFormattato%></h4>
                        </div>
                    </div>
                    <%
                        }
                    %>  
                </div>
                <h3>Totale: <%= String.format("%.2f", carrello.totalAmount()) %>€</h3>          
            </div> 
        </div>
        <h1>Riepilogo Informazioni Ordine:</h1>
         <div class="pro-container">    
             <a href="/AreaRiservata">Clicca qui per Verifica e/o Aggiorna Dati Personali</a>  
            <%Cliente client_infos = user.getProfile();%>
            <p>Nome: <%=client_infos.getNome()%> <%=client_infos.getCognome()%></p>
            <p>Contatti: <%=client_infos.getTelefono()%> <%=client_infos.getEmail()%></p>
         </div>    
         <form action="<%=request.getContextPath()%>/CheckoutCarrello" method="post">
             <input type="hidden" name="action" value="confirmOrder">
        <h2>Seleziona un Indirizzo di Spedizione o Aggiungine uno Nuovo.</h2>
        <%            
            ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>)request.getAttribute("Indirizzi");
            if (indirizzi != null && !indirizzi.isEmpty()) {
                Map<Integer, Indirizzo> addressMap = new HashMap<>();%>
              <% for (Indirizzo indirizzo : indirizzi) { 
                    addressMap.put(indirizzo.getIDIndirizzo(), indirizzo);
              %>
              <p> <!-- Radio button with address ID as value -->
                <input type="radio" name="selectedAddress" value="<%= indirizzo.getIDIndirizzo()%>" required>                           
                      <%= indirizzo.getVia() %>
                      <%= indirizzo.getNumCivico() %> -
                      <%= indirizzo.getCap() %>
                      <%= indirizzo.getCitta() %>
                      (<%= indirizzo.getProvincia() %>)</p>
        <%      }
                request.getSession().setAttribute("addressMap", addressMap);
            }
        %>
              <h2>Seleziona la Modalità di Spedizione:</h2>
              <div class='shipping_options'> 
                <p>
                    <input type="radio" name="tipoSpedizione" value="Spedizione_standard" required>
                    Standard
                </p>
                <p>
                    <input type="radio" name="tipoSpedizione" value="Spedizione_assicurata">
                    Assicurata
                </p>
                <p>
                    <input type="radio" name="tipoSpedizione" value="Spedizione_prime">
                    Prime
                </p>
            </div>
            <h2>Seleziona la Modalità di Consegna:</h2> 
            <div class='shipping_options'> 
                <p>
                    <input type="radio" name="modalitaConsegna" value="Domicilio" required>
                    Domicilio
                </p>
                <p>
                    <input type="radio" name="modalitaConsegna" value="PuntoRitiro">
                    Punto di Ritiro
                </p>
                <p>
                    <input type="radio" name="modalitaConsegna" value="Priority">
                    Priority / Fascia Oraria
                </p>
            </div>
            <input value="Conferma Ordine" type="submit" class="confirm_button"> 
             <a href="/CheckoutCarrello?action=annullaOrdine"><button class="delete_button" type="button">Annulla Ordine</button></a>
         </form> 
        <%
            }
        %>
    </div>
    </div>
    </body>
</html>
