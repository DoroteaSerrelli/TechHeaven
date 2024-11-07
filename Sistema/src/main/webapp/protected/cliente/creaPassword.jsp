<%-- 
    Document   : resetPassword - creazione password dell'utente
    Created on : 24-sept-2024, 19.09.20
    Author     : Dorotea Serrelli
--%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>TechHeaven - Reimpostazione password</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="<%= request.getContextPath()%>/scripts/validationResetPassword.js"></script>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/style/style.css">
</head>
<body>

	<jsp:include page="/common/header.jsp" flush="true" />
	<div class="section-p1">
		<form name="client" method="post"
			action="../../ReimpostaPasswordController">
			<h1>Reimpostazione password</h1>
			<p>Inserisci la nuova password di accesso.</p>
			<p>
				In caso di inserimento corretto della nuova password, <br> sarai
				indirizzato verso la pagina di autenticazione <br> nella quale
				potrai inserire la tua nuova password.
			</p>
			<hr>
			<input type="hidden" name="action" value="resetPassword">
			<div class="row">
				<div class="input-wrapper">
					<p>*Nuova Password:</p>
					<input type="password" name="password"
						onchange="validatePassword()" required>
				</div>
			</div>
			<div class="input-wrapper">
				<input value="Conferma" type="submit" class="confirm_button"
					name="submit" onclick="return validateFormPassword()">
			</div>
			<div class="errormsg">
				<p id="error"></p>
				<p id="errorSession" style = "display : none;"></p>
				<%
				String err = (String) request.getSession().getAttribute("error");
				if (err != null && !err.isEmpty()) {
				%>
				<script>
				const element = document.getElementById('errorSession');
				if(element.style.display === "none" || element.style.visibility === "hidden"){
					document.getElementById('errorSession').style.display = "block";
	        		document.getElementById('errorSession').textContent = '<%=err%>';
				}else
					document.getElementById('errorSession').textContent = '<%=err%>';
				</script>
				<%
				request.getSession().removeAttribute("error");
				}
				%>
			</div>
		</form>
	</div>
	<jsp:include page="/common/footer.jsp" flush="false" />

</body>
</html>