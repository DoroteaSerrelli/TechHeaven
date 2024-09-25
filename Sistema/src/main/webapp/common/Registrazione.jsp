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
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/update_user_info_bar.css">
</head>
<body>

	<jsp:include page="/common/header.jsp" flush="true" />
	
	<div class="section-p1">
		<form name="client" method="post" action="RegistrazioneController">
			<h1>Registrazione</h1>
			<p>Registrati al nostro sito per acquistare i nostri prodotti ed essere aggiornato
			sui nuovi arrivi.</p>
			<br>
			<hr>
			<br>
			<h2>Informazioni sull'account</h2>
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
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Nome:</p>
					<input type="text" name="name" oninput="validateName()" required>
				</div>
				<div class="input-wrapper reg_form">
					<p>*Cognome:</p>
					<input type="text" name="surname" oninput="validateSurname()"
						required>
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Sesso:</p>
					<select name="sesso" required>
						<option value="M">Uomo</option>
						<option value="F">Donna</option>
					</select>
				</div>
				</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*E-mail:</p>
					<input type="text" name="email" oninput="validateEmail()" required>
				</div>
				<div class="input-wrapper reg_form">
					<p>*Numero di cellulare:</p>
					<input type="text" name="phoneNumber"
						oninput="validatePhoneNumber()" required>
				</div>
			<br>
			</div>
			<hr>
			<h2>Indirizzo</h2>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Via:</p>
					<input id="road" type="text" name="road"
						oninput="validateAddress()" required>
                                         <div class="errormsgAddress">
                                            <div id="error road"></div>
                                        </div> 
				</div>
				<div class="input-wrapper reg_form">
					<p>*Numero civico:</p>
					<input id="cv" type="text" name="cv" oninput="validateAddress()"
						required>
                                        <div class="errormsgAddress">
                                            <div id="error cv"></div>
                                        </div>
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Città:</p>
					<input id="city" type="text" name="city"
						oninput="validateAddress()" required>
                                         <div class="errormsgAddress">
                                            <div id="error city"></div>
                                        </div> 
				</div>
				<div class="input-wrapper reg_form">
					<p>*CAP:</p>
					<input id="cap" type="text" name="cap" oninput="validateAddress()"
						required>
                                         <div class="errormsgAddress">
                                            <div id="error cap"></div>
                                        </div> 
				</div>
			</div>
			<div class="row">
				<div class="input-wrapper reg_form">
					<p>*Provincia:</p>
					<input id="province" type="text" name="province"
						oninput="validateAddress()" required>
                                        <div class="errormsgAddress">
                                            <div id="error province"></div>
                                        </div> 
				</div>
			</div>

			<div class="input-wrapper reg_form">
				<input value="Registrati" type="submit" class="confirm_button"
					name="submit" onclick="return validate()">
			</div>
			<div class="errormsg">
				<p id="error"></p>
				<% 
                    String err = (String)request.getSession().getAttribute("error");
                    if (err != null && !err.isEmpty()) {
                 %>
				<%=err%>
				<% 
                        request.getSession().removeAttribute("error");
                        } %>
			</div>
		</form>
	</div>
	<jsp:include page="/common/footer.jsp" flush="true" />
</body>

</html>
