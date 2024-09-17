<%-- 
    Document   : Registrazione
    Created on : 5-mar-2024, 18.09.47
    Author     : raffy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>TechHeaven - Registrazione</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="<%= request.getContextPath()%>/scripts/validations.js"></script>
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
</head>
<body>

	<jsp:include page="/common/header.jsp" flush="true" />
	<div class="section-p1">
		<form name="client" method="post" action="RegistrazioneController">
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Name:</p>
					<input type="text" name="name" oninput="validateName()" required>
				</div>
				<div class="input-wrapper reg_form">
					<p>*Surname:</p>
					<input type="text" name="surname" oninput="validateSurname()"
						required>
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Address Road:</p>
					<input id="road" type="text" name="road"
						oninput="validateAddress()" required>
				</div>
				<div class="input-wrapper reg_form">
					<p>*Civic Number:</p>
					<input id="cv" type="text" name="cv" oninput="validateAddress()"
						required>
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Address City:</p>
					<input id="city" type="text" name="city"
						oninput="validateAddress()" required>
				</div>
				<div class="input-wrapper reg_form">
					<p>*CAP:</p>
					<input id="cap" type="text" name="cap" oninput="validateAddress()"
						required>
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Province:</p>
					<input id="province" type="text" name="province"
						oninput="validateAddress()" required>
				</div>
				<div class="input-wrapper reg_form">
					<p>*Sex:</p>
					<select name="sesso" required>
						<option value="M">Male</option>
						<option value="F">Female</option>
					</select>
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*E-mail:</p>
					<input type="text" name="email" oninput="validateEmail()" required>
				</div>
				<div class="input-wrapper reg_form">
					<p>*Phone Number:</p>
					<input type="text" name="phoneNumber"
						oninput="validatePhoneNumber()" required>
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Username:</p>
					<input type="text" name="username" oninput="validateUsername()"
						required>
				</div>
				<div class="input-wrapper reg_form">
					<p>*Password:</p>
					<input type="password" name="password" oninput="validatePassword()"
						required>
				</div>
			</div>

			<div class="input-wrapper reg_form">
				<input value="Submit" type="submit" class="confirm_button"
					name="submit" onclick="return validate()">
			</div>
			<div class="errormsg">
				<p id="error"></p>
				<% 
                    String err = (String)request.getSession().getAttribute("error");
                    if (err != null && !err.isEmpty()) {
                 %>
				<%=err%>
				<% } %>
			</div>
		</form>
	</div>
	<jsp:include page="/common/footer.jsp" flush="true" />
</body>

</html>
