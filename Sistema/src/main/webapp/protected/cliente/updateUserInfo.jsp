<%-- 
    Document   : updateUserInfo
    Created on : 7-mag-2024, 14.28.23
    Author     : raffy
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"
	import="application.Registrazione.RegistrazioneService.Indirizzo,
				  java.util.ArrayList"%>
<!DOCTYPE html>
<html lang="it">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>TechHeaven - Modifica profilo</title>
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/update_user_info_bar.css">
<script
	src="<%=request.getContextPath() %>/scripts/validations.js?ts=<%=System.currentTimeMillis()%>"></script>
<!-- Include any necessary scripts -->
<%
    // Get error, field, and action from the session  
    String field = (String) request.getSession().getAttribute("field");
    String currentAction = (String) request.getSession().getAttribute("currentAction");

    // Clear these attributes after displaying them
    request.getSession().removeAttribute("field");
    request.getSession().removeAttribute("currentAction");
%>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        var field = '<%= field != null ? field : "" %>';
        var currentAction = '<%= currentAction != null ? currentAction : "" %>';
        // Show the appropriate form based on field and currentAction
        if (field !== "") {
            showUpdateForm(field, currentAction);
        }
    });
</script>

<script>
        // Initialize addresses array
        var addresses = [];
        var currentAction = null; // Variable to store the current action
        <% 
            ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>)request.getAttribute("Indirizzi");
            if (indirizzi != null && !indirizzi.isEmpty()) {
                for (Indirizzo indirizzo : indirizzi) { 
                int indirizzoID = indirizzo.getIDIndirizzo(); %>  
                    
                    addresses.push({
                        id: '<%= indirizzo.getIDIndirizzo() %>',
                        via: '<%= indirizzo.getVia() %>',
                        numCivico: '<%= indirizzo.getNumCivico() %>',
                        cap: '<%= indirizzo.getCap() %>',
                        citta: '<%= indirizzo.getCitta() %>',
                        provincia: '<%= indirizzo.getProvincia() %>'
                    });

                    // Generate HTML for displaying addresses
                    document.addEventListener('DOMContentLoaded', function() {
                        var addressList = document.getElementById('addressList');
                        var addressHTML = '<div onclick="loadAddress(\'' + '<%= indirizzoID %>' + '\', currentAction)" class="address-item" id="address_'+ '<%= indirizzoID %>' + '">' +
                                '<p >'+
                          '<img class="arrow" id="arrow_' + <%= indirizzoID %> + '" src="images/site_images/modif_arrow.png" style="display:none;" />' 
                          +'Via: <%= indirizzo.getVia() %> <%= indirizzo.getNumCivico() %>' +
                            '</p>' +
                            '<p>' +
                            '<%= indirizzo.getCap() %> <%= indirizzo.getCitta() %> (<%= indirizzo.getProvincia() %>)' +
                            '</p>'+
                        '</div>';
                        addressList.innerHTML += addressHTML;
                    });
            <% } 
            } %>
    </script>
</head>
<body>
	<jsp:include page="/common/header.jsp" flush="true" />
	<div id="mobile">
		<button class="openbtn" onclick="openUpdateBar()" type="button">
			<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
				width="24" height="24">
            <path fill="#2DA0F2"
					d="M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z" />

		</button>
	</div>

	<div class="update_bar" id="update_bar">
		<ul>
			<li><a id="email" href="#" onclick="showUpdateForm('email')">Modifica
					Email</a></li>
			<li><a id="telefono" href="#"
				onclick="showUpdateForm('telefono')">Modifica numero di telefono</a></li>
			<li><a id="addAddress" href="#"
				onclick="showUpdateForm('address', 'AGGIUNGERE-INDIRIZZO')">Aggiungi un
					Indirizzo</a></li>
			<li><a id="modifyAddress" href="#"
				onclick="showUpdateForm('address', 'AGGIORNARE-INDIRIZZO')">Modifica
					un Indirizzo</a></li>
			<li><a id="deleteAddress" href="#"
				onclick="showUpdateForm('address', 'RIMUOVERE-INDIRIZZO')">Elimina
					un Indirizzo</a></li>
			<li><a href="#" id="close" onClick="closeUpdateBar()"> <svg
						xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24"
						height="24">
                    <path fill="#2DA0F2"
							d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
                </svg>
			</a></li>

		</ul>
	</div>
	<h2 style="text-align: center">Aggiorna il tuo account</h2>
	<div class="container">
		<div class="form-container">
			<form id="updateInfoForm" name="client"
				action="UpdateProfileController?information=email" method="post">
				<div class="row" id="updateEmail">
					<div class="input-wrapper">
						<p>Email:</p>
						<input type="email" id="email" name="email"
							oninput="validateEmail()">
					</div>
				</div>
				<div class="row" id="updateTelefono" style="display: none;">
					<div class="input-wrapper">
						<p>Telefono:</p>
						<input type="tel" id="phoneNumber" name="telefono"
							oninput="validatePhoneNumber()">

					</div>
				</div>
				<div class="row" id="updateAddress" style="display: none;">
					<div class="input-wrapper" style="padding-bottom: 10px;">
						<p>Indirizzo:</p>
						<input type="text" name="newVia" id="road" placeholder="Via"
							oninput="validateAddress()">
						<div class="errormsgAddress">
							<div id="error road"></div>
						</div>
					</div>
					<div class="input-wrapper" style="padding-bottom: 10px;">
						<input type="text" name="newNumCivico" id="cv"
							placeholder="Numero Civico" oninput="validateAddress()">
						<div class="errormsgAddress">
							<div id="error cv" style="width: 240px;"></div>
						</div>
					</div>
					<div class="input-wrapper" style="padding-bottom: 10px;">
						<input type="text" name="newCap" id="cap" placeholder="Cap"
							oninput="validateAddress()">
						<div class="errormsgAddress">
							<div id="error cap"></div>
						</div>
					</div>
					<div class="input-wrapper" style="padding-bottom: 10px;">
						<input type="text" name="newCitta" id="city" placeholder="Città"
							oninput="validateAddress()">
						<div class="errormsgAddress">
							<div id="error city"></div>
						</div>
					</div>
					<div class="input-wrapper" style="padding-bottom: 10px;">
						<input type="text" name="newProvincia" id="province"
							placeholder="Provincia" oninput="validateAddress()">
						<div class="errormsgAddress">
							<div id="error province"></div>
						</div>
					</div>
				</div>
				<input id="conf_button" value="Aggiorna" type="button"
					class="confirm_button" name="update" onclick="validateForm()">


				<div class="errormsg">
					<p id="error"></p>
					<p id="errorSession" style="display: none;"></p>
					<%
				String err = (String) request.getAttribute("error");                  
				if (err != null && !err.isEmpty()) {
                                    //Provo a fare l'escape del apostrofo perchè sembra dargli fastidio.
                                    String escapedErr = err.replace("'", "\\'").replace("\"", "\\\"").replace("\n", "\\n");
				%>
					<script>
				const element = document.getElementById('errorSession');
				if(element.style.display === "none" || element.style.visibility === "hidden"){
					document.getElementById('errorSession').style.display = "block";
                                        document.getElementById('errorSession').textContent = '<%=escapedErr%>';
				}else
					document.getElementById('errorSession').textContent = '<%=escapedErr%>';
				</script>
					<%
				//request.removeAttribute("error");
				}
				%>
				</div>

			</form>
		</div>
		<%  
                if (request.getSession().getAttribute("user") == null) {
                    out.println("");
                } else {%>
		<div class="address-container">
			<div id="addressList"></div>

		</div>
		<% } %>
		<script
			src="<%= request.getContextPath() %>/scripts/modInfoAccount.js?ts=<%= System.currentTimeMillis() %>"></script>
	</div>
	<jsp:include page="/common/footer.jsp" flush="true" />
</body>
</html>
