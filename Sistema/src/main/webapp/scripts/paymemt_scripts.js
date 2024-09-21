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
            alert("Inserisci tutte le informazioni della carta di credito.");
            return false;
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