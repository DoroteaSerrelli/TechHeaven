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
	<div id="showpr" class="section-p1">
		<%
            Carrello carrello; 
            carrello = (Carrello) request.getSession().getAttribute("usercart");%>
		<div class="errormsg">
			<% 
                    String err = (String)request.getSession().getAttribute("error");
                    if (err != null && !err.isEmpty()) {
                 %>
			<p id="error" class="error invalid"><%=err%></p>
			<% request.getSession().removeAttribute("error");
                    } %>
		</div>
		<% if(carrello==null || carrello.getProducts().isEmpty()){
        %>
		<div id="emptycart">
			<h4>Il tuo carrello è vuoto!</h4>
			<p>Cerca il tuo prodotto nelle nostre categorie di prodotti.</p>
		</div>

		<%
            } else {  
        %>
		<h1>Carrello:</h1>
		<%
            carrello.totalAmount();
        %>
		<div id="complete_order">
			<h1>Totale provvisorio:</h1>
			<h3><%=String.format("%.2f", carrello.totalAmount())%>€
			</h3>
			<a href="complete_order.jsp">Click here to proceed with the order</a>
		</div>
		<%
                for(ItemCarrello p: carrello.getProducts()){                         
            %>

		<div class="row">
			<img src="image?productId=<%= p.getCodiceProdotto() %>" alt="alt"
				onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/images/site_images/placeholder.png';" />
			<p><%=p.getNomeProdotto()+" "%>
				<%=p.getMarca()%></p>
			<h3><%=p.getPrezzo()%>€
			</h3>
		</div>
		<div class="row item-carrello">
			<p>
				Quantità:
				<%=p.getQuantita()%></p>
		</div>
		<% }
            %>

		<%}%>
	</div>
	<jsp:include page="common/footer.jsp"
		flush="true" />

</body>
</html>
