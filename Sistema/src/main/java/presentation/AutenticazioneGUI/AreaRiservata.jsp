<%-- 
    Document   : AreaRiservata
    Created on : 12-mar-2024, 19.34.52
    Author     : raffy
--%>

<%@page import="application.RegistrazioneService.Indirizzo"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.RegistrazioneService.Ruolo"%>
<%@page import="application.RegistrazioneService.Utente"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="view/roleFunctions.js"></script>
    </head>    
   <body>
        <link rel="stylesheet" href="style.css">
       <jsp:include page="header.jsp"  flush="true"/>
       <% if (request.getSession().getAttribute("user") == null) {
            response.sendRedirect("Autenticazione.jsp");
        } else {
            Utente u = (Utente) request.getSession().getAttribute("user");
       %>
       <jsp:include page="roleSelector.jsp"  flush="true"/>
      
        <div id="product1">
              <h2>Dettagli Utente</h2>
            <ul>
                <h3>Nome Utente: <%=u.getUsername()%></h3>
                <h3>Email: <%=u.getProfile().getEmail()%></h3>
                <h3>Contatti: <%=u.getProfile().getTelefono()%></h3>
                </ul>                               
                <div id="address">
                    <h3>Indirizzo:</h3>
                      <%  
                        if (request.getSession().getAttribute("user") == null) {
                            out.println("");
                        } else {                                
                            ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>)request.getAttribute("Indirizzi");
                        if (indirizzi != null && !indirizzi.isEmpty()) {%>
                     <% for (Indirizzo indirizzo : indirizzi) { %>
                     <p>Via: <%= indirizzo.getVia() %> <%= indirizzo.getNumCivico() %></p>
                     <p><%= indirizzo.getCap() %> <%= indirizzo.getCitta() %> (<%= indirizzo.getProvincia() %>)</p>    
          
                    <% } %>
                    <% } else { %>
                        <p>No address available.</p>
                    <% } %>                     
                </div>        
        <% }} %>
          </div>
      <jsp:include page="footer.jsp"  flush="true"/> 
    </body>   
        
</html>
