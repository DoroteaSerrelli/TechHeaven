function openUpdateBar() {
  document.getElementById("update_bar").style.left = "0";
  
}

function closeUpdateBar() {
  document.getElementById("update_bar").style.left = "-100%";
}

function showUpdateForm(field, action) {
    // Hide all update forms
    var updateForms = document.querySelectorAll('.row[id^="update"]');
    updateForms.forEach(function(form) {
        form.style.display = 'none';
    });

    // Show the selected update form
    var selectedForm = document.getElementById('update' + field.charAt(0).toUpperCase() + field.slice(1));
    if (selectedForm) {
        selectedForm.style.display = 'block';
        // Change action attribute of the form based on selection
        if(field==='address'){
            switch(action){
                case 'addAddress':
                    document.getElementById('updateInfoForm').action = 'UpdateAddressController?action=AddIndirizzo';
                    break;
                case 'modifyAddress':
                    document.getElementById('updateInfoForm').action = 'UpdateAddressController?action=UpdateIndirizzo';
                    break;
                case 'deleteAddress':
                    document.getElementById('updateInfoForm').action = 'UpdateAddressController?action=RemoveIndirizzo';
                    break;
                default:
                    document.getElementById('updateInfoForm').action = 'UpdateProfileController';
                    break;    
            }
        }
        else{
            document.getElementById('updateInfoForm').action = 'UpdateProfileController';
        }
    }
}
 