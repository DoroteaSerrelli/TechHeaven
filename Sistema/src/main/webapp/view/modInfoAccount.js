
function showNewAddressForm() {
    // Show the hidden input fields for adding a new address
    var addressForm_inputs = document.getElementById("new-address");
    // Set the action parameter to the desired value
    var updateAddressForm = document.getElementById("updateAddressForm"); 
    updateAddressForm.action = "UpdateAddressController?action=AddIndirizzo";
    
    addressForm_inputs.style.display = addressForm_inputs.style.display === 'none' ? 'block' : 'none';
    
}

function submitForms() {
    // Submit both forms when the Update button is clicked
    var updtInfoform = document.getElementById("updateInfoForm");
    var updtAddrform = document.getElementById("updateAddressForm");
    // Check if form elements are valid
    updtInfoform.submit();
    updtAddrform.submit();
}

