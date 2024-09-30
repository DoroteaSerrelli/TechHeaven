/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function addInvalidMessage(msg, errorField){
    var errField = document.getElementById(errorField);
    errField.innerHTML=msg;
    errField.style.display= "block";
    errField.style.color= "red";
    errField.classList.remove("valid");
    errField.classList.add("invalid");	
}

function removeInvalidMessage(errorField){
    var errField = document.getElementById(errorField);
    errField.style.display= "none";
    errField.classList.remove("invalid");									
    errField.classList.add("valid");	
}

function validateProductID(input, object) {
    var pridRegex = /^[0-9]+$/;
    if (!pridRegex.test(input.value)) {      
        addInvalidMessage(object+" deve essere numerico", "prod"+object+"Error");			
        return false;
    }
    else{
        removeInvalidMessage("prod"+object+"Error");
        return true;					
    }
}


function validatePrice(input, object) {
    var priceRegex = /^[0-9]+(\.[0-9]+)?$/; // Allow whole numbers and decimal numbers
    if (!priceRegex.test(input.value)) {
        addInvalidMessage("Questo campo non può essere vuoto", "prod" + object + "Error");
        return false;
    } else {
        removeInvalidMessage("prod" + object + "Error");
        return true;
    }
}


function validateProductNameorModel(input, object) {
    const productNameRegex = /^[A-Za-z0-9\s]+$/;
    if (!productNameRegex.test(input.value)) {
        addInvalidMessage("Il "+ object +" deve contenere numeri e/o lettere","prod"+object+"Error");  	     			
        return false;
    }
    else{
        removeInvalidMessage("prod"+object+"Error");         
        return true;					
    }
}

function validateBrand(input){
     const productBrandRegex = /^[A-Za-z]+$/;
    if (!productBrandRegex.test(input.value)) {
        addInvalidMessage("La marca del prodotto deve contenere lettere ed eventualmente spazi", "prodBrandError");	     			
        return false;
    }
    else{
        removeInvalidMessage("prodBrandError");
        return true;					
    }   
}

function validateDettailsAndDescription (input, object) {
//    const regex = /^(?!.*<[^>]+)[a-zA-Z0-9\s!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{10,300}$/; 
    // Check if the input matches the regex pattern after trimming whitespace
    if (input.value.trim()==="") { // Corrected closing parenthesis
        if(object==='Descrizione'){
            addInvalidMessage("La descrizione di presentazione non può essere vuota", "prod" + object + "Error");        
        }
        else{
            addInvalidMessage("Le caratteristiche in dettaglio di un prodotto devono essere specificate", "prod" + object + "Error");
        }
        return false;
    } else {
        removeInvalidMessage("prod" + object + "Error");
        return true;					
    }
}
// Global form validation before submission
function validateForm() {
    var valid = true;
    valid = validateProductID(document.getElementById('number'), 'ID') && valid;
    valid = validateProductNameorModel(document.getElementById('productName'), 'Nome') && valid;
    valid = validateDettailsAndDescription(document.querySelector('[name="topDescrizione"]'), 'Descrizione') && valid;
    valid = validateDettailsAndDescription(document.querySelector('[name="dettagli"]'), 'Dettagli') && valid;
    valid = validatePrice(document.querySelector('[name="price"]'), 'Prezzo') && valid;
    valid = validateBrand(document.getElementById('marca')) && valid;
    valid = validateProductNameorModel(document.getElementById('modello'), 'Modello') && valid;
    valid = validateProductID(document.getElementById('quantità'), 'Quantità') && valid;

    if (!valid) {
        document.getElementById("error").innerHTML = "Ci sono errori nel modulo. Si prega di correggerli prima di inviare.";
        return false; // Prevent form submission
    }

    return true; // Allow form submission
}


 // Function to validate the entire form
    function validateModifyForm() {
        let isValid = true;

        // Validate Product Details
        if ($('#productDetailsCheckbox').is(':checked')) {
            isValid &= validateProductDetails();
        }

        // Validate Descriptions
        if ($('#descriptionCheckbox').is(':checked')) {
            isValid &= validateDescriptions();
        }

        // Validate Pricing
        if ($('#pricingCheckbox').is(':checked')) {
            isValid &= validatePricing();
        }

        // Validate Quantity
        isValid &= validateQuantity($('#quantità'));

        // Return the overall validity
        return isValid;
    }

    // Validation functions for individual sections
    function validateProductDetails() {
        let isValid = true;
        const marca = $('#marca')[0];
        const modello = $('#modello')[0];

        if (!validateBrand(marca)) {
            isValid = false;
        }

        if (!validateProductNameorModel(modello, 'Modello')) {
            isValid = false;
        }

        return isValid;
    }

    function validateDescriptions() {
        let isValid = true;

        const topDescrizione = $('textarea[name="topDescrizione"]')[0];
        const dettagli = $('textarea[name="dettagli"]')[0];

        if (!validateDettailsAndDescription(topDescrizione, 'Descrizione')) {
            isValid = false;
        }

        if (!validateDettailsAndDescription(dettagli, 'Dettagli')) {
            isValid = false;
        }

        return isValid;
    }

    function validatePricing() {
        let isValid = true;

        const price = $('input[name="price"]')[0];

        if (!validatePrice(price, 'Prezzo')) {
            isValid = false;
        }

        return isValid;
    }

   

    function validateQuantity(input) {
        if (!validateProductID(input[0], 'Quantità')) {
            return false;
        }
        return true;
    }