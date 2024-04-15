<%-- 
    Document   : Autenticazione
    Created on : 11-mar-2024, 13.13.55
    Author     : raffy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
       <title>Autenticazione</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script>
            function validate(){
		if(!validateUsername() || !validatePassword()) return false;
            }
            function validateUsername(){
		let n= document.forms["client"]["name"].value;
		var pattern="^[A-Za-z]{5,}$";
		if(!n.match(pattern)){
                    document.getElementById("error").innerHTML="Name must have alphabet characters only";
                    error.classList.remove("valid");
                    error.classList.add("invalid");				
                return false;
		}
		else{
                    document.getElementById("error").innerHTML="OK";	
                    error.classList.remove("invalid");									
                    error.classList.add("valid");	
                return true;					
		}		
            }
            function validatePassword(){
		let n= document.forms["client"]["surname"].value;
		var pattern= /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{5,}$/;
		if(!n.match(pattern)){
                    document.getElementById("error").innerHTML="must have alphabet characters only";
                    error.classList.remove("valid");
                    error.classList.add("invalid");
		return false;
		}
		else{
                    document.getElementById("error").innerHTML="OK";	
                    error.classList.remove("invalid");									
                    error.classList.add("valid");	
		return true;		
		}		
            }
        </script>
    </head>
    <body>
        <link rel="stylesheet" href="common/style.css">
        <jsp:include page="common/header.jsp"  flush="true"/>
        <div class="section-p1">
            <form name="client" method="post" action="/AutenticazioneController">           
            <div class="row">
                <div class="input-wrapper">
                    <p>  *Username: </p>  
                    <input type="text" name="username" onchange="validateUsername()" required>
                </div>   
                <div class="input-wrapper">
                    <p> *Password: </p>
                    <input type="text" name="password" onchange="validatePassword()" required>
                </div>
            </div>  
            <div class="row">     
                 <div class="input-wrapper">             
                    <p>  *Role: </p>
                    <select name="ruolo" required>
                        <option value="Cliente">Cliente</option>
                        <option value="GestoreOrdini">Gestore Ordini</option>
                        <option value="GestoreCatalogo">Gestore Catalogo</option>
                    </select>    
                 </div> 
            </div>  
                <div class="row">
                    <div class="input-wrapper"> 
                    <input value="Submit" type="submit" class="confirm_button" name="submit" onclick="return validate()">                
                    </div>
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
            <button class="confirm_button"><a href="resetPassword.jsp">Reimposta password</a></button>
             
         </div>
            <jsp:include page="common/footer.jsp"  flush="false"/>  
    </body>      
</html>
