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
                    if(addresses.length > 0){
                        loadAddress(addresses[0].id);
                    }
                    document.getElementById('updateInfoForm').action = 'UpdateAddressController?action=UpdateIndirizzo';
                    break;
                case 'deleteAddress':
                    selectedForm.style.display = 'none'; // Hide the input form
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
 
function deleteAddress(index) {
    // Hide the address input form
    document.getElementById('updateAddress').style.display = 'none';
    // Change the form action for deletion and include the address index
    document.getElementById('updateInfoForm').action = `UpdateAddressController?action=RemoveIndirizzo&addressIndex=${index}`;
    
    // Optionally, show a confirmation message or button
    const confirmation = confirm("Are you sure you want to delete this address?");
    if (confirmation) {
        document.getElementById('updateInfoForm').submit();
    }
}

function loadAddress(index) {  
    
     // Log the ID to ensure it's being passed correctly
    console.log("ID passed to loadAddress:", index);

    // Log the addresses array to ensure it contains the data
    console.log("Addresses array:", addresses);

    // Use the find function to get the address object based on the ID
    var address = addresses.find(addr => String(addr.id) === String(index));
    
    // Populate the form fields with the selected address's data
    document.getElementById('road').value = address.via;
    document.getElementById('cv').value = address.numCivico;
    document.getElementById('cap').value = address.cap;
    document.getElementById('city').value = address.citta;
    document.getElementById('province').value = address.provincia;
    
    // Update the form action with the selected address index for modification
    document.getElementById('updateInfoForm').action = `UpdateAddressController?action=UpdateIndirizzo&addressIndex=${index}`;
}
