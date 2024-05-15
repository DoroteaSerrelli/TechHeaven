/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
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

