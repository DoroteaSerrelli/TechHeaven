<%-- 
    Document   : AreaRiservata
    Created on : 12-mar-2024, 19.34.52
    Author     : raffy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
	import="application.AutenticazioneControl.AutenticazioneController,
				  application.RegistrazioneService.ProxyUtente,
				  application.RegistrazioneService.Indirizzo,
				  java.util.ArrayList,
				  application.RegistrazioneService.Ruolo,
				  application.RegistrazioneService.Utente"%>

<%           
    ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");

    //I checks li faccio nella servlet questa parte qui è inutile           
    if (u==null || u.getUsername().equals("")) {
       response.sendRedirect("Autenticazione");
       return ;
   }
   else {           
    Utente real_user = u.mostraUtente();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>TechHeaven - Area Riservata</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
</head>
<body>

	<jsp:include page="/common/header.jsp" flush="true" />
	<jsp:include page="/protected/cliente/roleSelector.jsp" flush="true" />

	<form action="AutenticazioneController?action=logout" method="POST">
		<button class="confirm_button" type="submit">Logout</button>
	</form>

	<div id="product1">
		<h2>Dettagli Utente</h2>
		<a href="AutenticazioneController?action=updateUserInfo"><img
			src="<%= request.getContextPath()%>/images/site_images/modificaInfoAccount.png" alt = "ModificaAccount"></a>
		<ul>
			<h3>
				Nome Utente:
				<%=u.getUsername()%></h3>
			<h3>
				Email:
				<%=real_user.getProfile().getEmail()%>
			</h3>
			<h3>
				Contatti:
				<%=real_user.getProfile().getTelefono()%></h3>
		</ul>
		<div id="address">
			<h3>Indirizzi:</h3>
			<%  
                      if (request.getSession().getAttribute("user") == null) {
                          out.println("");
                      } else {
                          ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>)request.getAttribute("Indirizzi");
                      if (indirizzi != null && !indirizzi.isEmpty()) {%>
			<% for (Indirizzo indirizzo : indirizzi) { %>
			<p>
				Via:
				<%= indirizzo.getVia() %>
				<%= indirizzo.getNumCivico() %></p>
			<p><%= indirizzo.getCap() %>
				<%= indirizzo.getCitta() %>
				(<%= indirizzo.getProvincia() %>)
			</p>

			<% } %>
			<% } else { %>
			<p>No address available.</p>
			<% }} %>
		</div>
		<% } %>
	</div>
	<jsp:include page="/common/footer.jsp" flush="true" />
</body>

</html>