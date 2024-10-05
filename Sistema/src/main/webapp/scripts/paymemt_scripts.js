/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


// Function to show/hide the credit card fields based on the selected payment method
function toggleCreditCardFields() {
    const creditCardFields = document.getElementById('creditCardFields');
    const creditCardRadio = document.getElementById('creditCardRadio');
    
    if (creditCardRadio.checked) {
        creditCardFields.style.display = 'block'; // Show credit card fields
    } else {
        creditCardFields.style.display = 'none';  // Hide credit card fields
    }
}

// Client-side form validation function
function validatePaymentForm() {
    const paymentMethod = document.querySelector('input[name="metodoPagamento"]:checked');
    saveSelectedPaymentMethod(paymentMethod);
    if (!paymentMethod) {
        alert("Seleziona un metodo di pagamento.");
        return false;
    }

    if (paymentMethod.value === "CreditCard") {
        const titolare = document.getElementById('titolare').value;
        const ccNumber = document.getElementById('cc_number').value;
        const ccExpiry = document.getElementById('cc_expiry').value;
        const ccCVC = document.getElementById('cc_cvc').value;

        if (!titolare || !ccNumber || !ccExpiry || !ccCVC) {
            addInvalidMessage("Inserisci tutte le informazioni della carta di credito.","error");
            return false;
        }
        // Validate individual fields
        if (!validateTitolare(titolare) || !validateCCNumber(ccNumber) || !validateCVV(ccCVC)) {
            return false; // Prevent form submission if validation fails
        }
        // Simple validation for credit card expiration
        const [expMonth, expYear] = ccExpiry.split('/').map(Number);
        const currentDate = new Date();
        const expiryDate = new Date(expYear, expMonth); // Month is 0-based (Jan = 0)

        if (expiryDate < currentDate) {
            alert("La carta di credito è scaduta.");
            return false;
        }
    }

    return true;
}

function validateTitolare(titolare){
    const titolarePattern = /^[A-Za-z\s]+$/;
    const isValidCardholder = titolarePattern.test(titolare);
    if(!isValidCardholder) addInvalidMessage("Il titolare deve essere una sequenza di lettere e spazi", "errorTitolare");
    else removeInvalidMessage("errorTitolare");
    return isValidCardholder;
}

function validateCCNumber(ccNumber){
    const numeroCartaPattern = /^\d{16}$/;
    const isValidCardNumber = numeroCartaPattern.test(ccNumber);
    if(!isValidCardNumber) addInvalidMessage("Il numero della carta è formato da 16 numeri", "errorcc_number");
    else removeInvalidMessage("errorcc_number");
    return isValidCardNumber;
}

function validateCVV(ccCVC){
    const cvvPattern = /^\d{3}$/;
    const isValidCVV = cvvPattern.test(ccCVC);
    if(!isValidCVV) addInvalidMessage("Il numero CVV è formato da 3 numeri", "errorcc_cvc");
    else removeInvalidMessage("errorcc_cvc");
    return isValidCVV;
}

// Save the selected payment method before form submission
function saveSelectedPaymentMethod() {
    const selectedMethod = document.querySelector('input[name="metodoPagamento"]:checked');
    if (selectedMethod) {
        localStorage.setItem('selectedPaymentMethod', selectedMethod.value);
    }
}
// Restore the last selected payment method on page load
   window.onload = function () {
       const savedMethod = localStorage.getItem('selectedPaymentMethod');

       if (savedMethod) {
           // Set the radio button to the saved method
           document.querySelector(`input[name="metodoPagamento"][value="${savedMethod}"]`).checked = true;
           toggleCreditCardFields(); // Show/hide fields based on saved selection
       }
   };