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
            document.getElementById("error").innerHTML="L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com)";
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
        road: /^[A-Za-z\s]+$/,
        cv: /^[0-9]+[A-Z]?$/, // Changed to valid regex format
        cap: /^\d{5}$/,
        province: /^[A-Za-z]{2}$/,
        city: /^[A-Za-z\s]+$/
    };

    const errorMessages = {
        road: "La via deve contenere solo lettere e spazi",
        cv: "Il numero civico è composto da numeri e, eventualmente, una lettera",
        cap: "Il CAP è formato da 5 numeri",
        province: "La provincia è composta da due lettere maiuscole",
        city: "La città deve essere composta solo da lettere e spazi"
    };

    let anyInvalid = false;

    for (const field in elements) {
        const element = elements[field];
        const value = element.value.trim(); // Remove leading/trailing spaces
        const errorElement = document.getElementById("error " + field); // Get the error div

        if (!value.match(patterns[field])) {
            anyInvalid = true;
            element.classList.add("invalid");
            element.classList.remove("valid");
            errorElement.textContent = errorMessages[field]; // Display the appropriate error message
            errorElement.classList.add("invalid-message");
            errorElement.classList.remove("valid-message");
        } else {
            element.classList.remove("invalid");
            element.classList.add("valid");
            errorElement.textContent = " "; // Clear error message when input is valid
            errorElement.classList.remove("invalid-message");
            errorElement.classList.add("valid-message");
        }
    }
    return !anyInvalid; // Return true if no fields are invalid, false otherwise
}

    function validatePhoneNumber(){
        let n= document.forms["client"]["phoneNumber"].value;
        ///^(\((00|\+)39\)|(00|\+)39)?(38[890]|34[7-90]|36[680]|33[3-90]|32[89])\d{7}$/
        var pattern= "^[3][0-9]{2}-[0-9]{3}-[0-9]{4}$";
        if(!n.match(pattern)){
            document.getElementById("error").innerHTML="Il formato del numero di telefono deve essere xxx-xxx-xxxx";
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
    
    function validateForm() {
        var isValid= false ;
        // Clear hidden email field if it exists
        var emailField = document.getElementById('updateEmail');
        if (emailField.style.display === 'none') {
            document.getElementById('email').value = '';
        }

        // Clear hidden phone number field if it exists
        var phoneField = document.getElementById('updateTelefono');
        if (phoneField.style.display === 'none') {
            document.getElementById('phoneNumber').value = '';
        }

        // Check if email field is visible, then validate
        if (emailField.style.display !== 'none') {
            isValid= validateEmail();
            
        }

        // Check if phone number field is visible, then validate
        if (phoneField.style.display !== 'none') {
            isValid= validatePhoneNumber();
        }
        
        var addressField = document.getElementById('updateAddress');
        // Check if phone number field is visible, then validate
        if (addressField.style.display !== 'none') {
            isValid= validateAddress();
        }
        
        // Add any additional validation logic here

        // If both email and phone number are valid, submit the form
        if (isValid) {
            document.getElementById("updateInfoForm").submit();
        } else {
            // Handle validation failure, e.g., display error messages
        }
    }


