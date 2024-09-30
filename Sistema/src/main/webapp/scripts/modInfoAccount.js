function openUpdateBar() {
  document.getElementById("update_bar").style.left = "0";
  
}

function closeUpdateBar() {
  document.getElementById("update_bar").style.left = "-100%";
}

function showUpdateForm(field, action) {
    // Hide all update forms
    currentAction = action; // Set the global current action
    var updateForms = document.querySelectorAll('.row[id^="update"]');
    updateForms.forEach(function(form) {
        form.style.display = 'none';
    });

    // Show the selected update form
    var selectedForm = document.getElementById('update' + field.charAt(0).toUpperCase() + field.slice(1));
    if (selectedForm) {
        selectedForm.style.display = 'block';
        // Change action attribute of the form based on selection
        var clickedOptionId;
        if(field==='address'){
            switch(action){
                case 'AddIndirizzo':                    
                    document.getElementById('conf_button').value = "Aggiungi Indirizzo";
                    document.getElementById('updateInfoForm').action = 'UpdateAddressController?action=AddIndirizzo';
                    clickedOptionId = 'addAddress'; // Set the correct ID for highlighting
                    break;
                case 'UpdateIndirizzo':                    
                    if(addresses.length > 0){
                        loadAddress(addresses[0].id, 'UpdateIndirizzo');
                    }
                    // Show the arrow for the first address
                    var firstArrow = document.getElementById('arrow_' + addresses[0].id);
                    if (firstArrow) {
                        firstArrow.style.display = 'inline';
                    }
                    document.getElementById('conf_button').value = "Modifica Indirizzo";
                    clickedOptionId = 'modifyAddress'; // Set the correct ID for highlighting
                    break; 
                case 'RemoveIndirizzo':
                    if(addresses.length > 0){
                        loadAddress(addresses[0].id, action);
                    }
                    // Show the arrow for the first address
                    var firstArrow = document.getElementById('arrow_' + addresses[0].id);
                    if (firstArrow) {
                        firstArrow.style.display = 'inline';
                    }
                    document.getElementById('conf_button').value = "Elimina Indirizzo";
                    clickedOptionId = 'deleteAddress'; // Set the correct ID for highlighting  
                    break;
                default:
                    document.getElementById('conf_button').value = "Aggiorna";
                    document.getElementById('updateInfoForm').action = 'UpdateProfileController';
                    break;    
            }
        }
        else{
            clickedOptionId = field; // For non-address fields, use the field directly
            document.getElementById('conf_button').value = "Aggiorna";
            document.getElementById('updateInfoForm').action = 'UpdateProfileController';
        }
        // Highlight the selected option
        var options = document.querySelectorAll('.update_bar ul li a');
        options.forEach(function(option) {
            option.classList.remove('selected');
        });

        // Add 'selected' class to the clicked option
        var clickedOption = document.querySelector('#' + clickedOptionId);
        clickedOption.classList.add('selected');
    }
}
 
function deleteAddress(index) {
    // Hide the address input form
    document.getElementById('updateAddress').style.display = 'none';
    
    // Hide the mod info input form button    
    
    // Use the find function to get the address object based on the ID
    var address = addresses.find(addr => String(addr.id) === String(index));
    
    // Populate the form fields with the selected address's data
    document.getElementById('road').value = address.via;
    document.getElementById('cv').value = address.numCivico;
    document.getElementById('cap').value = address.cap;
    document.getElementById('city').value = address.citta;
    document.getElementById('province').value = address.provincia;
    // Change the form action for deletion and include the address index
    document.getElementById('updateInfoForm').action = `UpdateAddressController?action=RemoveIndirizzo&addressIndex=${index}`;
    
    // Optionally, show a confirmation message or button
    const confirmation = confirm("Sei sicuro di voler cancellare l'indirizzo?");
    if (confirmation) {
        document.getElementById('updateInfoForm').submit();
    }
}

function loadAddress(index, action) {  
    console.log(action);
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
    document.getElementById('updateInfoForm').action = `UpdateAddressController?action=${action}&addressIndex=${index}`;
    
    // Hide all arrows and remove highlighting from all addresses
    var arrows = document.querySelectorAll('.arrow');
    var addressItems = document.querySelectorAll('.address-item');
    
    arrows.forEach(function(arrow) {
        arrow.style.display = 'none';
    });

    addressItems.forEach(function(item) {
        item.classList.remove('selected-address');
    });
    
    // Show the arrow and highlight the selected address
    var selectedArrow = document.getElementById('arrow_' + index);
    var selectedAddressItem = document.getElementById('address_' + index);
    
    if (selectedArrow) {
        selectedArrow.style.display = 'inline';
    }
    
    if (selectedAddressItem) {
        selectedAddressItem.classList.add('selected-address');
    }
    
}
