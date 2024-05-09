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
        <form id="updateInfoForm" action="UpdateProfileController" method="post">
             <div class="row" id="updateEmail">
                <div class="input-wrapper">
                    <p>Email:</p>
                    <input type="email" id="email" name="email">
                </div>
             </div>
            <div class="row" id="updateTelefono" style="display: none;">
                <div class="input-wrapper">                   
                    <p>Telefono:</p>
                    <input type="tel" id="telefono" name="telefono">
                    <!-- Other fields for updating information -->
                </div>
            </div>             
            <input value="Update" type="button" class="confirm_button" name="update" onclick="submitForms()">            
        </form>   
        <form id="updateAddressForm" action="UpdateAddressController" method="post">
        <div id="address">
                <p>Indirizzi:  <button type="button" class="confirm_button" onclick="showNewAddressForm()">Aggiungi Indirizzo</button></p>
                <div id="new-address" style="display: none;">
                    <div class="row">
                        <div class="input-wrapper">
                            <input type="text" name="newVia" placeholder="Via">
                        </div>  
                        <div class="input-wrapper">
                            <input type="text" name="newNumCivico" placeholder="Numero Civico">
                        </div>  
                    </div> 
                            <input type="text" name="newCap" placeholder="Cap">
                        <div class="input-wrapper">
                        </div>
                     
                    <div class="row">
                        <div class="input-wrapper">            
                            <input type="text" name="newCitta" placeholder="Città">
                        </div> 
                        <div class="input-wrapper">  
                            <input type="text" name="newProvincia" placeholder="Provincia">
                        </div>  
                    </div>  
                </div>
                    <%  
                      if (request.getSession().getAttribute("user") == null) {
                          out.println("");
                      } else {%>
                        <%
                          ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>)request.getAttribute("Indirizzi");
                      if (indirizzi != null && !indirizzi.isEmpty()) {%>
                      <% for (Indirizzo indirizzo : indirizzi) { %> <a href="">Modifica Indirizzo</a> <a href="">Elimina Indirizzo</a>
                   <p>Via: <%= indirizzo.getVia() %> <%= indirizzo.getNumCivico() %></p>
                   <p><%= indirizzo.getCap() %> <%= indirizzo.getCitta() %> (<%= indirizzo.getProvincia() %>)</p>    

                  <% } %>
                  <% } else { %>
                      <p>No address available.</p>
                  <% }} %>                     
            </div> 
        </form>   
        <script src="view/modInfoAccount.js"></script>  
    </div>     
    <jsp:include page="common/footer.jsp" flush="true"/>   
    </body>
</html>
