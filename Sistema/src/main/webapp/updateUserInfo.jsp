<%-- 
    Document   : updateUserInfo
    Created on : 7-mag-2024, 14.28.23
    Author     : raffy
--%>

<%@page import="application.RegistrazioneService.Indirizzo"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Information</title>
    <link rel="stylesheet" href="common/style.css">
    <link rel="stylesheet" href="common/update_user_info_bar.css">
    <script src="${pageContext.request.contextPath}/view/validations.js"></script>
    <!-- Include any necessary scripts -->
</head>
<body>
    <jsp:include page="common/header.jsp" flush="true"/>
    <div id="mobile">
        <button class="openbtn" onclick="openUpdateBar()" type="button">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
            <path fill="#2DA0F2" d="M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z"/>
        </button>         
    </div>
    <div class="update_bar" id="update_bar">       
        <ul>           
            <li><a href="#" onclick="showUpdateForm('email')">Update Email</a></li>
            <li><a href="#" onclick="showUpdateForm('telefono')">Update Phone Number</a></li>
            <li><a href="#" onclick="showUpdateForm('address', 'addAddress')">Add Address</a></li>
            <li><a href="#" onclick="showUpdateForm('address', 'modifyAddress')">Modify Address</a></li>
            <li><a href="#" onclick="showUpdateForm('address', 'deleteAddress')">Delete Address</a></li>
            <li><a href="#" id="close" onClick="closeUpdateBar()">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                    <path fill="#2DA0F2" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z"/>
                </svg>                
                </a></li> 
            <!-- Add more options for updating information -->
        </ul>       
    </div>
    <div class="section-p1">
        <h2>Update Your Information</h2>
        <form id="updateInfoForm" name="client" action="UpdateProfileController" method="post">
             <div class="row" id="updateEmail">
                <div class="input-wrapper">
                    <p>Email:</p>
                    <input type="email" id="email" name="email" oninput="validateEmail()">
                </div>
             </div>
            <div class="row" id="updateTelefono" style="display: none;">
                <div class="input-wrapper">                   
                    <p>Telefono:</p>
                    <input type="tel" id="phoneNumber" name="telefono" oninput="validatePhoneNumber()">
                    <!-- Other fields for updating information -->
                </div>
            </div> 
             <div class="row" id="updateAddress" style="display: none;">
                <div class="input-wrapper">
                    <p>Indirizzo:</p>
                    <input type="text" name="newVia" id="road" placeholder="Via" oninput="validateAddress()">
                </div>
                <div class="input-wrapper">
                    <input type="text" name="newNumCivico" id="cv" placeholder="Numero Civico" oninput="validateAddress()">
                </div>  
                <div class="input-wrapper">            
                    <input type="text" name="newCap" id="cap" placeholder="Cap" oninput="validateAddress()">
                </div> 
                <div class="input-wrapper">            
                    <input type="text" name="newCitta" id="city" placeholder="Città" oninput="validateAddress()">
                </div> 
                <div class="input-wrapper">  
                    <input type="text" name="newProvincia" id="province" placeholder="Provincia" oninput="validateAddress()">
                </div>                
            </div>
            <input value="Update" type="button" class="confirm_button" name="update" onclick="validateForm()" >           
        </form>     
            <p id="error"></p>
             <% 
                String err = (String)request.getSession().getAttribute("error");
                if (err != null && !err.isEmpty()) {
             %>
            <%=err%>               
            <% } %>      
            <%  
                if (request.getSession().getAttribute("user") == null) {
                    out.println("");
                } else {%>
                  <%
                    ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>)request.getAttribute("Indirizzi");
                if (indirizzi != null && !indirizzi.isEmpty()) {%>
                <% for (Indirizzo indirizzo : indirizzi) { %>
             <p>Via: <%= indirizzo.getVia() %> <%= indirizzo.getNumCivico() %></p>
             <p><%= indirizzo.getCap() %> <%= indirizzo.getCitta() %> (<%= indirizzo.getProvincia() %>)</p>    

            <% } %>
            <% } else { %>
                <p>No address available.</p>
            <% }} %>
        <script src="view/modInfoAccount.js"></script>  
    </div>     
    <jsp:include page="common/footer.jsp" flush="true"/>   
    </body>
</html>