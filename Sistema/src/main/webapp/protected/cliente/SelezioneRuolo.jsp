<%-- 
    Document   : Selezione Ruolo prima di accedere all'area personale
    Created on : 11-sept-2024, 20.38
    Author     : Dorotea Serrelli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
	import="application.AutenticazioneControl.AutenticazioneController,
			  application.RegistrazioneService.ProxyUtente,
			  java.util.ArrayList,
			  application.RegistrazioneService.Ruolo,
			  application.RegistrazioneService.Utente"%>

<!DOCTYPE html>
<html lang="en">
<head>
<title>TechHeaven - Autenticazione</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
</head>
<body>
	<% ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");%>


	<jsp:include page="/common/header.jsp"
		flush="true" />
	<div class="section-p1">
		<div class="login-container">
			<form name="client" method="post" action="AutenticazioneController">
			<h1>Login - Ruolo</h1>
			<p>Bentornato! Seleziona la modalità con cui vuoi accedere al sistema</p>
			<hr>
				<input type="hidden" name="action" value="roleSelection">
				<div class="row">
					<div class="input-wrapper">
						<p>Ruolo:</p>
						<select name="ruolo" required>
							<% for(Ruolo r : u.getRuoli()){
                        	%>
							<option value="<%= r.getNomeRuolo()%>"><%= r.getNomeRuolo() %></option>
							<%} %>

						</select>
					</div>
				</div>
				<div class="row">
					<div class="input-wrapper">
						<input value="Conferma" type="submit" class="confirm_button"
							name="submit" onclick="return validate()">
					</div>
				</div>
			</form>
		</div>
	</div>
	<jsp:include page="/common/footer.jsp"
		flush="false" />
</body>
</html>
