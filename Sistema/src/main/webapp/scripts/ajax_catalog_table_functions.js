/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function enableModify(){
    $('#modifyPropertiesForm input[name="productId"]').attr('disabled', true);
    $('#modifyPropertiesForm input[name="productName"]').attr('readonly', true);
    $('#modifyPropertiesForm input[name="marca"]').attr('readonly', false);
    $('#modifyPropertiesForm input[name="price"]').attr('readonly', false);
    $('#modifyPropertiesForm input[name="modello"]').attr('readonly', false);
    $('#modifyPropertiesForm input[name="quantita"]').attr('readonly', false);
    
    //Textareas
    $('#modifyPropertiesForm textarea[name="topDescrizione"]').attr('readonly', false);
    $('#modifyPropertiesForm textarea[name="dettagli"]').attr('readonly', false);
    //Selects
    $('#modifyPropertiesForm select[name="categoria"]').removeAttr('disabled');
    $('#modifyPropertiesForm select[name="sottocategoria"]').removeAttr('disabled');
    //Checkboxes
    $('#modifyPropertiesForm input[name="inVetrina"]').removeAttr('disabled');
    $('#modifyPropertiesForm input[name="inCatalogo"]').removeAttr('disabled');
}

function disableModify(){
    $('#modifyPropertiesForm input[name="productId"]').attr('readonly', true);
    $('#modifyPropertiesForm input[name="productName"]').attr('readonly', true);
    $('#modifyPropertiesForm input[name="marca"]').attr('readonly', true);
    $('#modifyPropertiesForm input[name="price"]').attr('readonly', true);
    $('#modifyPropertiesForm input[name="modello"]').attr('readonly', true);
    $('#modifyPropertiesForm input[name="quantita"]').attr('readonly', true);
    
    //Textareas
    $('#modifyPropertiesForm textarea[name="topDescrizione"]').attr('readonly', true);
    $('#modifyPropertiesForm textarea[name="dettagli"]').attr('readonly', true);
    //Selects
    $('#modifyPropertiesForm select[name="categoria"]').attr('disabled', true);
    $('#modifyPropertiesForm select[name="sottocategoria"]').attr('disabled', true);
    //Checkboxes
    $('#modifyPropertiesForm input[name="inVetrina"]').attr('disabled', true);
    $('#modifyPropertiesForm input[name="inCatalogo"]').attr('disabled', true);
}

function populateFields(product){
    $('#modifyPropertiesForm input[name="productId"]').val(product.codiceProdotto);
    $('#modifyPropertiesForm input[name="productName"]').val(product.nomeProdotto);
    $('#modifyPropertiesForm input[name="marca"]').val(product.marca);
    $('#modifyPropertiesForm input[name="price"]').val(product.prezzo);
    $('#modifyPropertiesForm input[name="modello"]').val(product.modello);
    $('#modifyPropertiesForm input[name="quantita"]').val(product.quantita);
    
    $('#modifyPropertiesForm textarea[name="topDescrizione"]').val(product.topDescrizione);
    $('#modifyPropertiesForm textarea[name="dettagli"]').val(product.dettagli); 
    
    $('#modifyPropertiesForm select[name="categoria"]').val(product.categoria);
    $('#modifyPropertiesForm select[name="sottocategoria"]').val(product.sottocategoria);
    
    // Example for a checkbox
    $('#modifyPropertiesForm input[name="inVetrina"]').prop('checked', product.inVetrina);
    $('#modifyPropertiesForm input[name="inCatalogo"]').prop('checked', product.inCatalogo);
    
    displayTopImage(product);
    
}

function clearFormState() {
    sessionStorage.removeItem('selectedProduct');
    sessionStorage.removeItem('selectedAction');
}

function addOriginalProductDetailsToForm(productDetails) {
    const form = document.getElementById('productForm');
    
    // Serialize the original product details to JSON
    const originalProductJson = JSON.stringify(productDetails);
    
    // Check if the hidden input already exists
    let hiddenOriginalProduct = document.querySelector('input[name="originalProductDetails"]');
    if (!hiddenOriginalProduct) {
        hiddenOriginalProduct = document.createElement('input');
        hiddenOriginalProduct.type = 'hidden';
        hiddenOriginalProduct.name = 'originalProductDetails';
        form.appendChild(hiddenOriginalProduct);
    }
    // Check if the hidden input for original quantity already exists
    let hiddenOriginalQuantity = document.querySelector('input[name="originalQuantity"]');
    if (!hiddenOriginalQuantity) {
        hiddenOriginalQuantity = document.createElement('input');
        hiddenOriginalQuantity.type = 'hidden';
        hiddenOriginalQuantity.name = 'originalQuantity';
        form.appendChild(hiddenOriginalQuantity);
    }
    
    // Set the value of the hidden input to the original quantity
    hiddenOriginalQuantity.value = productDetails.quantita;
    // Set the value of the hidden input to the JSON string
    hiddenOriginalProduct.value = originalProductJson;
}

function fetchSessionData(callback) {
    $.ajax({
        url: `${window.contextPath}/GestioneImmaginiProdotto`,
        method: 'GET',
        success: function(response) {
            //Stampa debug Ricezione Galleria tramite ajax (Recupero degli Oggetti in Sessione)
           console.log('Session data retrieved successfully:', response);
            const base64Gallery = response.base64Gallery;
            if (callback) {
                callback(base64Gallery);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error fetching session data:', error);
        }
    });
}


function fetchProductFullInfos(product, action) {
    const productJson = JSON.stringify(product);
    $.ajax({
        url: `${window.contextPath}/ProductInfos`,
        method: 'POST',
        data: {
            product: productJson,
            action: 'retrieveInfosForUpdate'
        },
        success: function(response) {
            // After updating the session data, fetch the updated session attribute
            fetchSessionData(function(base64Gallery) {
                // Now you have the updated base64Gallery
                const productDetails = response.product;
                
                storeProductDetails(productDetails, 'selectedProduct');  // Store product details in IndexedDB
               // storeGalleryImages(base64Gallery);  // Store the gallery images in IndexedDB <--- POTENZIALE CACHING DELLA GALLERIA.
                updateGallery(base64Gallery);  // Update the gallery with the retrieved images
                
                sessionStorage.setItem('selectedAction', action);            
                getProductDetails('selectedProduct', function(details) {
                    if (action === 'modify') {
                        openModifyForm(productDetails);
                    } else if (action === 'delete') {
                        openDeleteForm(productDetails);
                    }                   
                });
            });
        },
        error: function(xhr, status, error) {
            console.error('Error fetching product details:', error);
        }
    });
}

function unHideAllInfoGroups(){ 
    $('#productDetailsGroup').removeClass('hidden');
    $('#descriptionGroup').removeClass('hidden');
    $('#pricingGroup').removeClass('hidden');
    $('#categoryGroup').removeClass('hidden');
    
}

function hideAllInfoGroups(){
    $('#productDetailsGroup').addClass('hidden');
    $('#descriptionGroup').addClass('hidden');
    $('#pricingGroup').addClass('hidden');
    $('#categoryGroup').addClass('hidden');
    
}

function showAndEnableCheckboxes() {
     // Get all checkboxes
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');

    // Loop through each checkbox and hide & disable it
    checkboxes.forEach(checkbox => {
        checkbox.style.display = 'block';  // Show the checkbox
        checkbox.disabled = false;         // Enable the checkbox
    });   
}

function hideAndDisableCheckboxes() {
    // Get all checkboxes
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');

    // Loop through each checkbox and hide & disable it
    checkboxes.forEach(checkbox => {
        checkbox.style.display = 'none';  // Hide the checkbox
        checkbox.disabled = true;         // Disable the checkbox
    });
}

function displayTopImage(product){
    $('#topImage').attr('src', `${window.contextPath}/image?productId=` + product.codiceProdotto);
}


function openModifyForm(product) {
    addOriginalProductDetailsToForm(product);
    showAndEnableCheckboxes();
    $('#addProductForm').addClass('hidden');
    $('#viewProductsForm').addClass('hidden');
    $('#removeProductForm').addClass('hidden');
    $('#modifyPropertiesForm').removeClass('hidden');
    
    $('#changeable').html("Modifica Informazioni del Prodotto");  
    $('#updateGalleryForm').removeClass('hidden');
    
    enableModify();
    populateFields(product);   
    // Set other fields as needed

    $('#productForm').attr('action', `${window.contextPath}/ModificaInfoProdottoController`);
}

function openDeleteForm(product) {
    addOriginalProductDetailsToForm(product);
    unHideAllInfoGroups();
    hideAndDisableCheckboxes();
    
    $('#addProductForm').addClass('hidden');
    $('#viewProductsForm').addClass('hidden');
    $('#removeProductForm').addClass('hidden');
    $('#modifyPropertiesForm').removeClass('hidden');
    
    $('#changeable').html("Elimina Prodotto - Verifica Cancellazione");
    $('#updateGalleryForm').addClass('hidden');
    
    disableModify();
    populateFields(product);
    // Set other fields as needed and make them readonly

    $('#productForm').attr('action', `${window.contextPath}/GestioneCatalogoController?action=deleteProduct`);
}

    function fetchProducts(page, action) {
        const url = `${window.contextPath}/GestioneCatalogoController?page=${page}&action=${action}`;
        console.log('Fetching URL:', url); // Debug URL
        
        // Capture the action variable in a closure
        const capturedAction = action;
        $.ajax({
            url: url,
            method: 'GET',
            contentType: 'application/json',
            success: function(data) {
                // Stampa Debug Ricezione Lista Prodotti Tramite Ajax
               // console.log('Received Data:', data); // Verify data structure
                
                // Handle and display the products and pagination
                const products = data.products;
                const hasNextPage = data.hasNextPage;
                const table = $('#showpr');

                table.html(`
                    <tr>
                        <th><strong>Image</strong></th>
                        <th><strong>Nome</strong></th>
                        <th><strong>Marca</strong></th>
                        <th><strong>TopDescr</strong></th>
                        <th><strong>Prezzo</strong></th>
                    </tr>
                `);

                products.forEach(product => {
                    const row = $('<tr></tr>');
                  
                    if (capturedAction) {
                        const actionCell = $('<td></td>');
                        const actionButton = $('<button></button>').text(capturedAction === 'delete' ? 'Delete' : 'Modify');

                        actionButton.on('click', function() {
                             console.log('Button clicked! Action:', capturedAction);
                            fetchProductFullInfos(product, capturedAction);
                        });

                        actionCell.append(actionButton);
                        row.append(actionCell);
                    }
                     
                    const imgSrc = `${window.contextPath}/image?productId=`+product.codiceProdotto;
                    // Stampa di Debug delle Immagini e Rispettiva Servlet che si occupa delle immagini
                    // console.log(`Image URL: ${imgSrc}`); // Log the image URL to check 

                    const imgCell = $('<td></td>').append(
                    $('<img>').attr('src', imgSrc)
                              .attr('alt', 'alt')
                              .on('error', function() { 
                                  this.onerror = null; // Prevent infinite loop
                                  this.src = `${window.contextPath}/view/img/placeholder.png`; 
                              })
                    );
                   const nomeCell = $('<td class="productName"</td>').append(
                        $('<h3></h3>').text(product.nomeProdotto)
                    );

                    const marcaCell = $('<td></td>').append(
                        $('<span></span>').text(product.marca)
                    );

                    const descrCell = $('<td></td>').append(
                        $('<h5></h5>').text(product.topDescrizione)
                    );

                    const prezzoCell = $('<td></td>').append(
                        $('<h4></h4>').text(product.prezzo+"€")
                    );
                              
                    row.append(imgCell, nomeCell, marcaCell, descrCell, prezzoCell);
                    table.append(row);
                });
            var action="";        
     // Update pagination
     updatePagination(page, action, hasNextPage); // Ensure pagination is updated with new data
    },
    error: function(xhr, status, error) {
        console.error('Error fetching data:', error);
    }
        });
}


$(document).ready(function() {
    const initialPage = 1; // Example page number
    // Event handler for viewing products
    //$('#viewProducts').click(function() {
    //    fetchProducts(initialPage, ''); // Fetch products without any specific action (just viewing)
    //});
     // Event handler for deleting products
    $('#removeProduct').click(function() {
        fetchProducts(initialPage, 'delete'); // Fetch products with delete action
    });

    // Event handler for modifying products
    $('#modifyProperties').click(function() {
        hideAllInfoGroups();
        fetchProducts(initialPage, 'modify'); // Fetch products with modify action
    });
    
    // Event handler for adding products
    //$('#addProduct').click(function() {
    //    enableModify();
    //});
    
});

document.addEventListener('DOMContentLoaded', () => {
    // Toggle group visibility based on checkbox selection
    document.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const group = this.parentElement.querySelector('div');
            group.classList.toggle('hidden', !this.checked);
        });
    });
});

document.getElementById('submitBtn').addEventListener('click', e => {
     e.preventDefault(); // Prevent the default form submission
    const form = document.getElementById('productForm');
    const formData = new FormData(form);
    
      const actionUrl = form.action; // Check form action
    if (actionUrl.includes('deleteProduct')) {
        // Re-enable disabled select fields before submission
        $('#modifyPropertiesForm select[name="categoria"]').attr('disabled', false);
        $('#modifyPropertiesForm select[name="sottocategoria"]').attr('disabled', false);
        $('#modifyPropertiesForm input[name="inVetrina"]').removeAttr('disabled');
        $('#modifyPropertiesForm input[name="inCatalogo"]').removeAttr('disabled');
        // Directly submit the form for deletion without extra data handling
        form.submit(); // Programmatically submit the form
        
    } else { 
        $('#modifyPropertiesForm input[name="productId"]').attr('disabled', false);
      if(validateModifyForm()){
        const form = document.getElementById('productForm');
        const formData = new FormData(form);
        const modifiedData = {};

        // Retrieve productId as an integer, ensuring it’s not null or empty
        const productId = parseInt(formData.get('productId'), 10);
        console.log(productId);

        // Helper function to populate modifiedData with selected fields
        const updateGroupData = (groupKey, fields, radioGroupName) => {
            const groupData = {};
            fields.forEach(field => {
                const value = formData.get(field);

                // Find the radio button associated with this field
                const radioElement = document.querySelector(`input[name="${radioGroupName}"][value="${field}"]`);

                // Check if the radio button exists and is selected
                if (radioElement && radioElement.checked && value) {
                    groupData[field] = value;
                }
            });
            if (Object.keys(groupData).length > 0) {
                modifiedData[groupKey] = groupData;
            }
        };

        // Update groups if their checkboxes are checked
        if (document.getElementById('productDetailsCheckbox').checked) {
            updateGroupData('productDetails', ['productName', 'marca', 'modello'], 'productDetailsField');
        }
        if (document.getElementById('descriptionCheckbox').checked) {
            updateGroupData('descriptions', ['topDescrizione', 'dettagli'], 'productDetailsField');
        }
        if (document.getElementById('pricingCheckbox').checked) {
            updateGroupData('pricing', ['price'], 'productDetailsField');
        }
         // Include category and subcategory if category checkbox is selected
        if (document.getElementById('categoryCheckbox').checked) {
            updateGroupData('category', ['categoria', 'sottocategoria'], 'productDetailsField');
        }
         // Handle 'quantita' field specifically, included within this function
            const quantitaValue = formData.get('quantita'); // Get the quantity value
           const quantitaRadio = document.querySelector(`input[name="productDetailsField"][value="quantita"]`);
           
            // If the quantita radio button is selected and has a value, add it to groupData
            if (quantitaRadio && quantitaRadio.checked && quantitaValue) {
                modifiedData['quantita'] = { 'quantita': quantitaValue }; // Wrap in an object
            }
        
        const inVetrinaTrue = document.getElementById('inVetrinaTrue');
        const inVetrinaFalse = document.getElementById('inVetrinaFalse');
        if (inVetrinaTrue.checked) {
            modifiedData['inVetrina'] = { 'inVetrina': inVetrinaTrue.value }; // Wrap in an object
        } else if (inVetrinaFalse.checked) {
            modifiedData['inVetrina'] = { 'inVetrina': inVetrinaFalse.value }; // Wrap in an object
        }

        // Send AJAX request if there is data to submit
        if (Object.keys(modifiedData).length > 0) {
            const jsonData = JSON.stringify({
                productId,
                modifiedData,
                originalProductDetails: JSON.parse(document.querySelector('input[name="originalProductDetails"]').value)
            });

            console.log('Data to be sent:', jsonData);
            $.ajax({
                url: form.action,
                method: 'POST',
                contentType: 'application/json',
                data: jsonData,
                success: response => {
                    
                    //Resetto il Messaggio visualizzato nella riga con id: errormsg
                    document.getElementById('addPrError').innerHTML="";
                    document.getElementById('addPrError').classList.remove('invalid');
                    // Check if the message starts with "invalid"
                    if (response.message.startsWith("invalid: ")) {
                        const actualMessage = response.message.substring("invalid: ".length); // Remove "invalid: " prefix
                        document.getElementById('addPrError').innerHTML = actualMessage;
                        document.getElementById('addPrError').classList.add('invalid');
                        console.log(actualMessage); // Optionally show the error message
                    } else {
                        window.location.href = response.redirectUrl;
                    }
                },
                error: (xhr, status, error) =>  {
                    // Assuming the response is a JSON object with message and redirectUrl
                    // Store the message in sessionStorage or localStorage
                    console.log(xhr.message);
                    sessionStorage.setItem('outputMessage', xhr.message);

                    // Redirect to the provided URL
                    window.location.href = xhr.redirectUrl;
                }
            });
        } else {
            alert("Seleziona un'informazione da modificare: modello, marca, descrizione in evidenza, descrizione dettagliata, categoria, sottocategoria.");
        }
    }
    }
});
