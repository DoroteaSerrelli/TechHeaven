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

// Function to add character limit and real-time validation to a textarea
function addCharacterLimitValidation(textareaId, charCountId, charWarningId, maxLength, validationType) {
    const textarea = document.getElementById(textareaId);
    const charCount = document.getElementById(charCountId);
    const charWarning = document.getElementById(charWarningId);
    const errorElement = document.getElementById(`error${textareaId}`); // Error message display
    
    textarea.addEventListener('input', function() {
        const value = textarea.value;
        const currentLength = value.length;
        charCount.textContent = `${currentLength}/${maxLength}`;
        // Check if the length exceeds the maximum
        if (currentLength > maxLength) {
            charWarning.style.display = 'inline';
            errorElement.textContent = ''; // Clear specific error when character limit exceeded
        } else {
            charWarning.style.display = 'none';
        }

        // Check for empty or space-only input
        if (value.trim() === '') {
            addInvalidMessage(textareaId+`: Questo campo non deve essere vuoto.`, errorElement.id);       
        } else {
            removeInvalidMessage(errorElement.id);
        }
        
        // Specific validation for Corriere (letters and spaces only)
        if (textarea.id === 'Corriere'){ 
            if(!/^[A-Za-z\s]+$/.test(value) || value.trim() === '') {
                addInvalidMessage(`L’azienda di spedizione deve essere composta da lettere e spazi`, errorElement.id);           
            }
             else {
                removeInvalidMessage(errorElement.id);
            }
        }      
    });
}

// Function to check if all textareas are within the character limit
function validateForm() {
    let isValid = true;

    const validations = [
        {
            id: 'Imballaggio',
            charCountId: 'charCountImballaggio',
            charWarningId: 'charWarningImballaggio',
            maxLength: 100,
            emptyMessage: "Imballaggio: Questo campo non deve essere vuoto."
        },
        {
            id: 'Corriere',
            charCountId: 'charCountCorriere',
            charWarningId: 'charWarningCorriere',
            maxLength: 60,
            emptyMessage: "L’azienda di spedizione deve essere composta da lettere e spazi",
            invalidMessage: "L’azienda di spedizione deve essere composta da lettere e spazi"
        }
    ];
     validations.forEach(validation => {
        const textarea = document.getElementById(validation.id);
        const charCount = document.getElementById(validation.charCountId);
        const charWarning = document.getElementById(validation.charWarningId);

        if (textarea && charCount) {
            const value = textarea.value.trim(); // Trim spaces at both ends
            const currentLength = textarea.value.length;

            // Check for empty or spaces-only input
            if (value === '') {
                addInvalidMessage(validation.emptyMessage, "error"+validation.id);
                isValid = false;
                return;
            }

            // Specific validation for Corriere (letters and spaces only)
            if (validation.id === 'Corriere' && !/^[A-Za-z\s]+$/.test(value)) {
                addInvalidMessage(validation.invalidMessage, "error"+validation.id);
                isValid = false;
                return;
            }

            // Check if the input exceeds the maximum length
            if (currentLength > validation.maxLength) {
                charWarning.style.display = 'inline';
                charWarning.innerHTML = "Superato il limite di caratteri ammissibili!";
                isValid = false;
            } else {
                charWarning.style.display = 'none';
            }
        }
    });
    return isValid;
}

document.addEventListener('DOMContentLoaded', (event) => {
            // Get the range input element and the span where the value will be displayed
            const rangeInput = document.getElementById('item_amount');
            const rangeValue = document.getElementById('range_value');

            // Function to update the value display
            function updateRangeValue() {
                rangeValue.textContent = rangeInput.value;
            }

            // Initialize the display with the current value
            updateRangeValue();

            // Add an event listener to update the value when the slider is moved
            rangeInput.addEventListener('input', updateRangeValue);
        });

function setActionAndRedirect(action) {
    sessionStorage.setItem('action', action); // Store action in session storage
    // Get the context path from the current URL
    var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

    // Redirect to the main page with the context path
    window.location.href = contextPath + '/GestioneOrdini';
}
function setActionForOrderSent() {
    sessionStorage.setItem('action', 'fetch_spediti'); // Store the 'order_sent' action
   // window.location.href = 'GestioneOrdini'; // Redirect to the main page
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