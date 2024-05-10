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
		if(!validateEmail() || !validateName() || !validateSurname() || !validateAddress() || !validatePhoneNumber() || !validatePassword()) return false;
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
            function validateAddress() {
                const elements = {
                  road: document.getElementById('road'),
                  cv: document.getElementById('cv'),
                  cap: document.getElementById('cap'),
                  province: document.getElementById('province'),
                  city: document.getElementById('city')
                };

                const patterns = {
                  road: /^\w+(?:\s+\w+)*$/,
                  cv: "^[0-9]+[A-Z]?$",
                  cap: /^\d{5}$/,
                  province: /^[A-Za-z]{2}$/,
                  city: /^[A-Za-z\s]+$/
                };
                
                const errorMessages = {
                  road: "Must be a valid road name (letters, spaces, and numbers allowed)",
                  cv: "Must be a valid civic number (numbers and letters allowed like 32A)",
                  cap: "Must be a valid postal code (5 digits)",
                  province: "Must be a valid province name two letters only allowed",
                  city: "Must be a valid city name (letters and spaces allowed)"
                };
                let anyInvalid = false;

                for (const field in elements) {
                  const element = elements[field];
                  const value = element.value.trim(); // Remove leading/trailing spaces

                   if (!value.match(patterns[field])) {
                      anyInvalid = true;
                      element.classList.add("invalid");
                      document.getElementById("error").textContent = errorMessages[field];
                      return false; // Early return if any field is invalid
                  } else {
                      element.classList.remove("invalid");
                      element.classList.add("valid");
                      }
                  }

                if (!anyInvalid) {
                    document.getElementById("error").textContent = "OK";
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
            
             function validateUsername(){
                let n= document.forms["client"]["username"].value;
               var pattern= "^[A-Za-z]{5,}$";
		if(!n.match(pattern)){
                    document.getElementById("error").innerHTML="Must have 5 or more alphabet characters only";
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
                let n= document.forms["client"]["password"].value;
           	var pattern= /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{5,}$/;
		if(!n.match(pattern)){
                    document.getElementById("error").innerHTML="Must have 5 or more symbols between characters and numbers";
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
        <div class="section-p1"><form name="client" method="post" action="RegistrazioneController">           
            <div class="row">
                <div class="input-wrapper reg_form">
                    <p>  *Name: </p>  
                    <input type="text" name="name" onchange="validateName()" required>
                </div>   
                <div class="input-wrapper reg_form">
                    <p> *Surname: </p>
                    <input type="text" name="surname" onchange="validateSurname()" required>
                </div>
            </div>  
            <div class="row">     
                 <div class="input-wrapper reg_form">             
                    <p>  *Address Road: </p>
                    <input id="road" type="text" name="road" onchange="validateAddress()"required>
                 </div>    
                 <div class="input-wrapper reg_form">                           
                    <p> *Civic Number: </p>
                    <input id="cv" type="text" name="cv" onchange="validateAddress()"required>
                 </div>
            </div>  
            <div class="row">
                 <div class="input-wrapper reg_form">                      
                    <p>  *Address City: </p>
                    <input id="city" type="text" name="city" onchange="validateAddress()"required>
                 </div> 
               <div class="input-wrapper reg_form">            
                    <p>  *CAP: </p>
                    <input id="cap" type="text" name="cap" onchange="validateAddress()"required>
              </div>               
            </div>
            <div class="row">
                <div class="input-wrapper reg_form">             
                    <p>  *Province: </p>
                    <input id="province" type="text" name="province" onchange="validateAddress()"required>
              </div>
                 <div class="input-wrapper reg_form">             
                    <p>  *Sex: </p>
                    <select name="sesso" required>
                        <option value="M">Male</option> 
                        <option value="F">Female</option> 
                    </select>
                </div>
            </div>
            <div class="row"> 
                <div class="input-wrapper reg_form">             
                    <p> *E-mail: </p>
                    <input type="text" name="email" onchange="validateEmail()" required>
                </div>
                <div class="input-wrapper reg_form">
                    <p>  *Phone Number: </p>
                    <input type="text" name="phoneNumber" onchange="validatePhoneNumber()" required>
                </div>
            </div>
            <div class="row"> 
                 <div class="input-wrapper reg_form">              
                    <p>  *Username: </p>
                    <input type="text" name="username" onchange="validateUsername()" required>
                </div>
                <div class="input-wrapper reg_form">             
                    <p>  *Password: </p>
                    <input type="text" name="password" onchange="validatePassword()" required>
                </div>              
            </div>  
                 
             <div class="input-wrapper reg_form"> 
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
        </form></div>
            <jsp:include page="common/footer.jsp"  flush="true"/> 
    </body>   
        
</html>
