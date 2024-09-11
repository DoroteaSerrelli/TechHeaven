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
    $('#modifyPropertiesForm input[name="quantità"]').attr('readonly', false);
    
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
    $('#modifyPropertiesForm input[name="quantità"]').attr('readonly', true);
    
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
    $('#modifyPropertiesForm input[name="quantità"]').val(product.quantita);
    
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
    
    // Set the value of the hidden input to the JSON string
    hiddenOriginalProduct.value = originalProductJson;
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
            console.log('Product details:', response);
            const productDetails = response.product;
          //  const prDetails = response.product;
            const galleryImages = response.base64Gallery;
            
            storeProductDetails(productDetails, 'selectedProduct');  // Store product details in IndexedDB
            // Store the gallery images in IndexedDB
            storeGalleryImages(galleryImages);
            updateGallery(galleryImages);    
            sessionStorage.setItem('selectedAction', action);            
            // Retrieve and handle product details for form
            getProductDetails('selectedProduct', function(details) {
                if (action === 'modify') {
                    openModifyForm(productDetails);
                } else if (action === 'delete') {
                    openDeleteForm(productDetails);
                }    
                // Adding original product details as hidden input field
            addOriginalProductDetailsToForm(details);
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
    showAndEnableCheckboxes();
    $('#addProductForm').addClass('hidden');
    $('#viewProductsForm').addClass('hidden');
    $('#removeProductForm').addClass('hidden');
    $('#modifyPropertiesForm').removeClass('hidden');
    
    $('#changeable').html("Modify Product Informations");
    
    enableModify();
    
    populateFields(product);   
    // Set other fields as needed

    $('#productForm').attr('action', `${window.contextPath}/ModifyProductsInCatalog`);
}

function openDeleteForm(product) {
    unHideAllInfoGroups();
    hideAndDisableCheckboxes();
    
    $('#addProductForm').addClass('hidden');
    $('#viewProductsForm').addClass('hidden');
    $('#removeProductForm').addClass('hidden');
    $('#modifyPropertiesForm').removeClass('hidden');
    
    $('#changeable').html("Delete Product - Verify Deletion");
    
    disableModify();
    populateFields(product);
    // Set other fields as needed and make them readonly

    $('#productForm').attr('action', `${window.contextPath}/GestioneCatalogoController?action=deleteProduct`);
}


function toggleSidebar(){
    var options_sidebar = document.getElementById("options_sidebar"); 
    if(options_sidebar.classList.contains("visible")){
        options_sidebar.classList.remove("visible");         
    }
    else{
        options_sidebar.classList.add("visible");
    }
 }

    function fetchProducts(page, action) {
        const url = `${window.contextPath}/GestioneCatalogoController?page=${page}`;
        console.log('Fetching URL:', url); // Debug URL
        
        // Capture the action variable in a closure
        const capturedAction = action;
        $.ajax({
            url: url,
            method: 'GET',
            contentType: 'application/json',
            success: function(data) {
                console.log('Received Data:', data); // Verify data structure
                
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
                    console.log(`Image URL: ${imgSrc}`); // Log the image URL to check

                    const imgCell = $('<td></td>').append(
                    $('<img>').attr('src', imgSrc)
                              .attr('alt', 'alt')
                              .on('error', function() { 
                                  this.onerror = null; // Prevent infinite loop
                                  this.src = `${window.contextPath}/view/img/placeholder.png`; 
                              })
                    );
                    const nomeCell = $('<td></td>').append(
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
    $('#viewProducts').click(function() {
        fetchProducts(initialPage, ''); // Fetch products without any specific action (just viewing)
    });
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
    $('#addProduct').click(function() {
        enableModify();
    });
    
});


document.addEventListener('DOMContentLoaded', function() {
    // Toggle group visibility based on checkbox selection
    document.querySelectorAll('input[type="checkbox"]').forEach(function(checkbox) {
        checkbox.addEventListener('change', function() {
            const group = this.parentElement.querySelector('div');
            if (this.checked) {
                group.classList.remove('hidden');
            } else {
                group.classList.add('hidden');
            }
        });
    });
});

document.getElementById('submitBtn').addEventListener('click', function(e) {
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
        
        const modifiedData = {};
        const productId = formData.get('productId');
        // Check and add Product Details Group
        if (document.getElementById('productDetailsCheckbox').checked) {
            const productName = formData.get('productName');
            const marca = formData.get('marca');
            const modello = formData.get('modello');

            if (productName && marca && modello) {
                modifiedData['productDetails'] = {
                    productName,
                    marca,
                    modello
                };
            }
        }

        // Check and add Description Group
        if (document.getElementById('descriptionCheckbox').checked) {
            const topDescrizione = formData.get('topDescrizione');
            const dettagli = formData.get('dettagli');
            if (topDescrizione && dettagli) {
                modifiedData['descriptions'] = {
                    topDescrizione,
                    dettagli
                };
            }
        }

        // Check and add Pricing Group
        if (document.getElementById('pricingCheckbox').checked) {
            const price = formData.get('price');

            if (price) {
                modifiedData['pricing'] = { price };
            }
        }

        // Check and add Category Group
        if (document.getElementById('categoryCheckbox').checked) {
            const categoria = formData.get('categoria');
            const sottocategoria = formData.get('sottocategoria');

            if (categoria && sottocategoria) {
                modifiedData['category'] = {
                    categoria,
                    sottocategoria
                };
            }
        }
        
        // Add Quantity
        const quantita = formData.get('quantità');
        if (quantita) {
            modifiedData['quantita'] = 
                    { quantita };
        }
        
        // Final Validation before submission
        if (Object.keys(modifiedData).length > 0) {
            const jsonData = JSON.stringify({
                modifiedData: modifiedData,
                originalProductDetails: JSON.parse(document.querySelector('input[name="originalProductDetails"]').value)
            });
            console.log('JSON Data to be sent:', jsonData);
            // Submit the form after preparing all the data
            $.ajax({
                url: form.action,
                method: 'POST',
                contentType: 'application/json',
                data: jsonData,
                success: function(response) {
                    // Assuming the response is a JSON object with message and redirectUrl
                    console.log(response);
                    clearFormState();
                    // Store the message in sessionStorage or localStorage
                    sessionStorage.setItem('outputMessage', response.message);

                    // Redirect to the provided URL
                    window.location.href = response.redirectUrl;
                },
                error: function(xhr, status, error) {
                    clearFormState();
                    // Assuming the response is a JSON object with message and redirectUrl
                    // Store the message in sessionStorage or localStorage
                    sessionStorage.setItem('outputMessage', xhr.message);

                    // Redirect to the provided URL
                    window.location.href = xhr.redirectUrl;
                }
            });

        } else {
            alert('Please make sure to modify at least one section and ensure all fields are filled correctly.');
        } 
} 
});
  
