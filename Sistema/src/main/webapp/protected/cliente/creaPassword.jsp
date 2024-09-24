<%-- 
    Document   : resetPassword - creazione password dell'utente
    Created on : 24-sept-2024, 19.09.20
    Author     : Dorotea Serrelli
--%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="ISO-8859-1">
<title>TechHeaven - Reimpostazione password</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script>
            function validate(){
		if(!validatePassword()) return false;
            }
            function validatePassword(){
		let n= document.forms["client"]["surname"].value;
		var pattern= /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{5,}$/;
		if(!n.match(pattern)){
                    document.getElementById("error").innerHTML="La password deve avere almeno 5 caratteri (lettere e numeri)";
                    error.classList.remove("valid");
                    error.classList.add("invalid");
		return false;
		}
		else{
                    error.classList.remove("invalid");									
                    error.classList.add("valid");	
		return true;		
		}		
            }
        </script>
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
</head>
<body>

	<jsp:include page="/common/header.jsp" flush="true" />
	<div class="section-p1">
		<form name="client" method="post"
			action="../../ReimpostaPasswordController">
			<h1>Reimpostazione password</h1>
			<p>Inserisci la nuova password di accesso</p>
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
					name="submit" onclick="return validate()">
			</div>
			<div class="errormsg">
				<% 
                    String err = (String)request.getSession().getAttribute("error");
                    if (err != null && !err.isEmpty()) {
                 %>
				<p id="error" class="error invalid"><%=err%></p>
				<% request.getSession().removeAttribute("error");
                    } %>
			</div>
		</form>
	</div>
	<jsp:include page="/common/footer.jsp" flush="false" />

</body>
</html>