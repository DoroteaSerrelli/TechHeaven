/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function validateSupplyRequestForm(){
        if(!validateCompanyName() || !validateCompanyEmail()) return false;
    }   
    function validateCompanyName(){
        let n= document.forms["supplyingRequestForm"]["fornitore"].value;
        var nominativoPattern = /^[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*$/;
        
        var error = document.getElementById("errormsg");        
        if(!n.match(nominativoPattern)){
            error.innerHTML="Il fornitore deve essere una sequenza di lettere, spazi ed, eventualmente, numeri";
            error.classList.remove("valid");
            error.classList.add("invalid");				
        return false;
        }
        else{
            error.innerHTML="OK";
            error.classList.remove("invalid");									
            error.classList.add("valid");	
        return true;					
        }
        
    }
    function validateCompanyEmail(){
        let n= document.forms["supplyingRequestForm"]["email_fornitore"].value;
        var emailPattern = /^[\w]+@[\w.-]+\.[a-zA-Z]{2,}$/;
        var error = document.getElementById("errormsg");               
        if(!n.match(emailPattern)){
            error.innerHTML="L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com)";
            error.classList.remove("valid");
            error.classList.add("invalid");				
        return false;
        }
        else{
            error.innerHTML="OK";
            error.classList.remove("invalid");									
            error.classList.add("valid");	
        return true;					
        }
        
    }


//Textarea Validations:

// Function to add character limit validation to a textarea
function addCharacterLimitValidation(textareaId, charCountId, charWarningId, maxLength) {
    const textarea = document.getElementById(textareaId);
    const charCount = document.getElementById(charCountId);
    const charWarning = document.getElementById(charWarningId);

    textarea.addEventListener('input', function() {
        const currentLength = textarea.value.length;
        charCount.textContent = `${currentLength}/${maxLength}`;

        if (currentLength > maxLength) {
            charWarning.style.display = 'inline';
        } else {
            charWarning.style.display = 'none';
        }
    });
}
// Function to check if all textareas are within the character limit
function validateForm() {
    let isValid = true;

    const validations = [
        { id: 'Imballaggio', charCountId: 'charCountImballaggio', charWarningId: 'charWarningImballaggio', maxLength: 100 },
        { id: 'Corriere', charCountId: 'charCountCorriere', charWarningId: 'charWarningCorriere', maxLength: 60 }
    ];

    validations.forEach(validation => {
        const textarea = document.getElementById(validation.id);
        const charCount = document.getElementById(validation.charCountId);
        const charWarning = document.getElementById(validation.charWarningId);

        if (textarea && charCount) {
            const currentLength = textarea.value.length;

            if (currentLength > validation.maxLength) {
                charWarning.style.display = 'inline';
                isValid = false;
            } else {
                charWarning.style.display = 'none';
            }
        }
    });

    return isValid;
}

// Attach validation to form submit
document.getElementById('fill_order_form').addEventListener('submit', function(event) {
    if (!validateForm()) {
        event.preventDefault(); // Prevent form submission
        alert('Sono presenti errori nel form da correggere.');
    }
});

// Initialize validations
addCharacterLimitValidation('Imballaggio', 'charCountImballaggio', 'charWarningImballaggio', 100);
addCharacterLimitValidation('Corriere', 'charCountCorriere', 'charWarningCorriere', 60);