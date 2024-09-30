<%@page import="java.util.Currency"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"
	import="application.GestioneCarrelloService.ItemCarrello,
				  application.GestioneCarrelloService.Carrello"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>TechHeaven - Carrello</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/cart.css">
</head>
<body>
	<jsp:include page="common/header.jsp"
		flush="true" />
     <div class="errormsg">
        <%
            String err = (String) request.getSession().getAttribute("error");
            String status = (String) request.getSession().getAttribute("status");           
            if (err != null && !err.isEmpty()) {
        %>
        <p id="error" class="error <%=status%>"><%= err %></p>
        <%
            request.getSession().removeAttribute("error");
            request.getSession().removeAttribute("status");
            }
        else{%>
            <p id="error" class="error"></p>
          <%  }
        %>
    </div> 
	<div id="showpr" class="section-p1">
    <%
        Carrello carrello = (Carrello) request.getSession().getAttribute("usercart");
    %>  
        <%
            if (carrello == null || carrello.getProducts().isEmpty()) {
        %>
        <div id="emptycart">
            <h4>Il tuo carrello è vuoto!</h4>
            <p>Cerca il tuo prodotto nelle nostre categorie di prodotti.</p>
        </div>
        <%
            } else {
        %>   
    <div id="cart">
        <h1>Carrello:</h1>
        <div id="complete_order">
            <h1>Totale provvisorio:</h1>
            <h3 id="total_amount"><%= String.format("%.2f", carrello.totalAmount()) %>€</h3>
            <a href="<%= request.getContextPath() %>/CheckoutCarrello">Click here to proceed with the order</a>
        </div>

        <%
            for (ItemCarrello p : carrello.getProducts()) {
        %>          
        <div id="item_<%= p.getCodiceProdotto() %>" class="cart-item">
            <p><%= p.getNomeProdotto() + "  " %></p>
            <p> <%=p.getDettagli()%> </p>
            <span style="color: #F28C44; font-size: 20px"><%= p.getMarca() %></span>
            <div class="row">
                <img src="image?productId=<%= p.getCodiceProdotto() %>" alt="alt"
                    onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/images/site_images/placeholder.png';" />			
                <%
                    double prezzo = p.getPrezzo()*p.getQuantita();
                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
                    currencyFormatter.setCurrency(Currency.getInstance("EUR"));
                    String prezzoFormattato = currencyFormatter.format(prezzo);
                    %>
                    <h4 style="color: goldenrod" class="prezzo"><%=prezzoFormattato%></h4>
            </div>
            <div class="row item-carrello">
                <p>Quantità: <span class="quantita"><%= p.getQuantita() %></span></p>
                <%
                    if (p.getQuantita() >= 1) { 
                        HashMap products_available_inStock = (HashMap) request.getSession().getAttribute("products_available_inStock");
                %>                                              
                <div class="quantity_controls">
                    <p class="range" id="range_value_<%= p.getCodiceProdotto() %>" style="color: goldenrod"><%= p.getQuantita() %></p>
                    <div class="input-wrapper row">                       
                        <input type="range" id="prod_quantità_<%= p.getCodiceProdotto() %>"
                            name="prod_quantità" min="1"
                            max="<%= products_available_inStock.get(p.getCodiceProdotto()) %>">
                    </div>
                
                    <a href="#"
                        onclick="modifyCart(<%= p.getCodiceProdotto() %>, 'updateQuantità')">
                        <h3>Aggiorna Quantità</h3>
                    </a>
                    <a href="#"
                        onClick="modifyCart(<%= p.getCodiceProdotto() %>, 'rimuoviDalCarrello')">
                        <h3>Rimuovi dal Carrello</h3>
                    </a>
                </div>
                    <%
                        }
                    %>
                </div>
            </div>
            <%
                }
            %>
        </div>
        <%
            }
        %>
    </div>
        <jsp:include page="common/footer.jsp"
		flush="true" />

</body>
</html>
