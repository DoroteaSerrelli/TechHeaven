<%-- 
    Document   : resetPassword
    Created on : 13-apr-2024, 18.44.25
    Author     : raffy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   <head>
       <title>Reimpostazione Password</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script>
            function validate(){
		if(!validateUsername() || !validatePassword() || !validateEmail()) return false;
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
            function validateEmail(){
                let n= document.forms["client"]["email"].value;
                var pattern=/^\S+@\S+\.\S+$/;
		if(!n.match(pattern)){
                    document.getElementById("error").innerHTML="Email must be in the form ____@____.___";
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
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/style.css">
        <jsp:include page="common/header.jsp"  flush="true"/>
        <div class="section-p1">
            <form name="client" method="post" action="ReimpostaPasswordController">           
                <div class="row">
                    <div class="input-wrapper">
                        <p>  *Username: </p>  
                        <input type="text" name="username" onchange="validateUsername()" required>
                    </div>
                </div>
                <div class="row">
                    <div class="input-wrapper">
                        <p> *Email: </p>
                        <input type="text" name="email" onchange="validateEmail()" required>
                    </div>
               </div>
                <div class="row">
                    <div class="input-wrapper">
                        <p> *Nuova Password: </p>
                        <input type="password" name="password" onchange="validatePassword()" required>
                    </div>
                </div>    
                <div class="input-wrapper"> 
                    <input value="Submit" type="submit" class="confirm_button" name="submit" onclick="return validate()">                
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
            <jsp:include page="common/footer.jsp"  flush="false"/>  
    </body>      
</html>
