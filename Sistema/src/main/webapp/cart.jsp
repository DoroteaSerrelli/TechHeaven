
<%@page import="application.GestioneCarrelloService.ItemCarrello"%>
<%@page import="application.GestioneCarrelloService.Carrello"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="common/style.css">
        <link rel="stylesheet" href="cart.css">
    </head>
    <body>
        <jsp:include page="common/header.jsp"  flush="true"/>              
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
                <p>Inserisci uno dei nostri prodotti per mantenerti aggiornato sulle novità!</p>
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
            <h3><%=String.format("%.2f", carrello.totalAmount())%>€</h3>
            <a href="complete_order.jsp">Click here to proceed with the order</a>
        </div>
            <%
                for(ItemCarrello p: carrello.getProducts()){                         
            %>            
        
            <div class="row">			
                <img src="image?productId=<%= p.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"/>
                <p><%=p.getNomeProdotto()+" "%> <%=p.getMarca()%></p>
                        <h3><%=p.getPrezzo()%>€</h3>
            </div>
            <div class="row item-carrello">
                <p>Quantità: <%=p.getQuantita()%></p>                           
            </div>       
            <% }
            %> 
            
        <%}%>
        </div>        
        <jsp:include page="common/footer.jsp"  flush="true"/> 	
        		
    </body>
</html>
