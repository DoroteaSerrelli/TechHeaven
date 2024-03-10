<%-- 
    Document   : Registrazione
    Created on : 5-mar-2024, 18.09.47
    Author     : raffy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   <html lang="en">
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script>
            function validate(){
		if(!validateEmail() || !validateName() || !validateSurname() || !validateAddress() || !validatePhoneNumber()) return false;
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
            function validateName(){
		let n= document.forms["client"]["name"].value;
		var pattern=/^[A-Za-z]+$/;
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
            function validateSurname(){
		let n= document.forms["client"]["surname"].value;
		var pattern= /^[A-Za-z]+$/;
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
            function validateAddress(){
		let road= document.forms["client"]["address"].value;
                let rd = document.getElementById('road').value;
                let city= document.getElementById('city').value;
                let cap= document.getElementById('cap').value;
        
		//var pattern= /^[A-Za-z ,]*[0-9]{0,3}-[0-9]{5} \w*[(A-Z)]{4}$/;
                var city_pattern ="^[A-Za-z]*$";
                var cap_pattern ="^[0-9]{5}$";
                var road_pattern ="^[a-zA-Z]{3,9}(?:\s[a-zA-Z]+)*$";
		if(!rd.match(road_pattern) || !cap.match(cap_pattern) || !city.match(city_pattern)){
                    document.getElementById("error").innerHTML="Must be like [Piazza Bartolo Longo,36-80045 Pompei(NA)]";
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
            function validatePhoneNumber(){
                let n= document.forms["client"]["phoneNumber"].value;
                ///^(\((00|\+)39\)|(00|\+)39)?(38[890]|34[7-90]|36[680]|33[3-90]|32[89])\d{7}$/
		var pattern= "^[3][0-9]{2}-[0-9]{3}-[0-9]{4}$";
		if(!n.match(pattern)){
                    document.getElementById("error").innerHTML="Must be in the form +39 xxx-xxx-xxxx";
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
        <link rel="stylesheet" href="style.css">
        <jsp:include page="header.jsp"  flush="true"/>
         <div class="section-p1"><form name="client" method="post" action="Register">           
            <div class="row">
                <div class="input-wrapper">
                    <p>  *Name: </p>  
                    <input type="text" name="name" onchange="validateName()" required>
                </div>   
                <div class="input-wrapper">
                    <p> *Surname: </p>
                    <input type="text" name="surname" onchange="validateSurname()" required>
                </div>
            </div>  
            <div class="row">     
                 <div class="input-wrapper">             
                    <p>  *Address Road: </p>
                    <input id="road" type="text" name="address" onchange="validateAddress()"required>
                 </div>    
                 <div class="input-wrapper">                           
                    <p> *Civic Number: </p>
                    <input type="number" name="address" onchange="validateAddress()"required>
                 </div>
            </div>  
            <div class="row">
                 <div class="input-wrapper">                      
                    <p>  *Address City: </p>
                    <input id="city" type="text" name="address" onchange="validateAddress()"required>
                 </div> 
               <div class="input-wrapper">            
                    <p>  *CAP: </p>
                    <input id="cap" type="text" name="address" onchange="validateAddress()"required>
              </div>               
            </div>
            <div class="row">
                <div class="input-wrapper">             
                    <p>  *Province: </p>
                    <input type="text" name="address" onchange="validateAddress()"required>
              </div>
            </div>
            <div class="row"> 
                <div class="input-wrapper">             
                    <p> *E-mail: </p>
                    <input type="text" name="email" onchange="validateEmail()" required>
                </div>
                <div class="input-wrapper">
                    <p>  *Phone Number: </p>
                    <input type="text" name="phoneNumber" onchange="validatePhoneNumber()" required>
                </div>
            </div>
            <div class="row"> 
                 <div class="input-wrapper">              
                    <p>  *Username: </p>
                    <input type="text" name="username" onchange="validateUsername()" required>
                </div>
                <div class="input-wrapper">             
                    <p>  *Password: </p>
                    <input type="text" name="password" onchange="validatePassword()" required>
                </div>
            </div>  
                 
             <div class="input-wrapper"> 
                 <input value="Submit" type="submit" class="confirm_button" name="submit" onclick="return validate()">
             </div>
                 
            <div class="errormsg">
	      <p id="error"></p>
              <%String err = (String)request.getSession().getAttribute("error");
              if(err.isEmpty()|| err==null);
              else %>
              <%=err%>
	    </div>   
        </form></div>
            <jsp:include page="footer.jsp"  flush="true"/> 
    </body>   
        
</html>
