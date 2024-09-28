<%-- 
    Document   : resetPassword - verifica delle credenziali di accesso dell'utente
    			  richiedente la password
    			  
    Created on : 13-apr-2024, 18.44.25
    Author     : raffy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>TechHeaven - Reimpostazione Password</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- <script>
	function validate() {
		if (!validateUsername() || !validateEmail())
			return false;
	}
	function validateUsername() {
		let n = document.forms["client"]["name"].value;
		var pattern = "^[A-Za-z]{5,}$";
		if (!n.match(pattern)) {
			error.classList.remove("valid");
			error.classList.add("invalid");
			return false;
		} else {
			error.classList.remove("invalid");
			error.classList.add("valid");
			return true;
		}
	}
	function validateEmail() {
		let n = document.forms["client"]["email"].value;
		var pattern = /^\S+@\S+\.\S+$/;
		if (!n.match(pattern)) {
			error.classList.remove("valid");
			error.classList.add("invalid");
			return false;
		} else {
			error.classList.remove("invalid");
			error.classList.add("valid");
			return true;
		}
	}
</script>  -->
<script
	src="<%=request.getContextPath()%>/scripts/validationResetPassword.js"></script>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/style/style.css">
</head>
<body>

	<jsp:include page="/common/header.jsp" flush="true" />
	<div class="section-p1">
		<form name="client" method="post" action="ReimpostaPasswordController">
			<h1>Reimpostazione password</h1>
			<p>Compila il seguente form per poter creare una nuova password
				di accesso.</p>
			<hr>
			<input type="hidden" name="action" value="resetPasswordRequest">
			<div class="row">
				<div class="input-wrapper">
					<p>*Username:</p>
					<input type="text" name="username" onchange="validateUsername()"
						required>
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper">
					<p>*Email:</p>
					<input type="text" name="email" onchange="validateEmail()" required>
				</div>
			</div>
			<div class="input-wrapper">
				<input value="Crea nuova password" type="submit"
					class="confirm_button" name="submit" onclick="return validate()">
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
