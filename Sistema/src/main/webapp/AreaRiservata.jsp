<%-- 
    Document   : AreaRiservata
    Created on : 12-mar-2024, 19.34.52
    Author     : raffy
--%>

<%@page import="application.AutenticazioneControl.AutenticazioneController"%>
<%@page import="application.RegistrazioneService.ProxyUtente"%>
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
        <link rel="stylesheet" href="common/style.css">
       <jsp:include page="common/header.jsp"  flush="true"/>
       <% 
            AutenticazioneController servlet = new AutenticazioneController();
            servlet.loadUserAddresses(request);                       
            if (request.getSession().getAttribute("user") == null) {
            response.sendRedirect("Autenticazione.jsp");
        } else {
            ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
            Utente real_user = u.mostraUtente();
       %> 
        <jsp:include page="roleSelector.jsp"  flush="true"/>
        <div id="product1">
            <form action="ModificaDatiPersonaliController" method="POST" id="infoForm">
                <h2>Dettagli Utente</h2><img src="${pageContext.request.contextPath}/view/img/modificaInfoAccount.png" id="editInfoButton">
            <ul>
                <h3>Nome Utente: <%=u.getUsername()%></h3>
                <h3>Email: <%=real_user.getProfile().getEmail()%> </h3>
                <input type="text" name="email" id="editEmailInput" style="display: none;">
                <h3>Contatti: <%=real_user.getProfile().getTelefono()%></h3>
                <input type="text" name="email" id="editPhoneInput" style="display: none;">
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
        <input value="Submit" type="submit" class="confirm_button" name="submit" onclick="return validate()" id="editConfirmInput" style="display: none;">   
           </form>
          </div>
        <script src="view/modInfoAccount.js"></script>
      <jsp:include page="common/footer.jsp"  flush="true"/> 
    </body>   
        
</html>
