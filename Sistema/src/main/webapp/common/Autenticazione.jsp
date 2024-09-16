<%-- 
    Document   : Autenticazione
    Created on : 11-mar-2024, 13.13.55
    Author     : raffy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

	<jsp:include page="<%=request.getContextPath() %>/common/header.jsp"
		flush="true" />
	<div class="section-p1">
		<div class="login-container">
			<form name="client" method="post" action="AutenticazioneController">
				<input type="hidden" name="action" value="login">
				<div class="row">
					<div class="input-wrapper">
						<p>*Username:</p>
						<input type="text" name="username" required>
					</div>
				</div>
				<div class="row">
					<div class="input-wrapper">
						<p>*Password:</p>
						<input type="password" name="password" required>
					</div>
				</div>
				<div class="row">
					<div class="input-wrapper">
						<input value="Conferma" type="submit" class="confirm_button"
							name="submit" onclick="return validate()">
					</div>
					<div class="input-wrapper">
						<button class="confirm_button">
							<a href="resetPassword">Reimposta password</a>
						</button>
					</div>
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
	</div>
	<jsp:include page="<%=request.getContextPath() %>/common/footer.jsp"
		flush="false" />
</body>
</html>
