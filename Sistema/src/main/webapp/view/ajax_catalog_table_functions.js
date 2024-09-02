/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function enableModify(){
    $('#addProductForm input[name="productId"]').attr('readonly', false);
    $('#addProductForm input[name="productName"]').attr('readonly', false);
}

function openModifyForm(product) {
    $('#addProductForm').removeClass('hidden');
    $('#viewProductsForm').addClass('hidden');
    $('#removeProductForm').addClass('hidden');
    $('#modifyPropertiesForm').addClass('hidden');
    
    $('#changeable').html("Modify Product Informations");
    
    enableModify();
    $('#addProductForm input[name="productId"]').val(product.codiceProdotto);
    $('#addProductForm input[name="productName"]').val(product.nomeProdotto);
    // Set other fields as needed

    $('#addProductForm').attr('action', `${window.contextPath}/GestioneCatalogoController?action=updateProduct`);
}

function openDeleteForm(product) {
    $('#addProductForm').removeClass('hidden');
    $('#viewProductsForm').addClass('hidden');
    $('#removeProductForm').addClass('hidden');
    $('#modifyPropertiesForm').addClass('hidden');
    
    $('#changeable').html("Delete Product - Verify Deletion");
    
    $('#addProductForm input[name="productId"]').val(product.codiceProdotto).attr('readonly', true);
    $('#addProductForm input[name="productName"]').val(product.nomeProdotto).attr('readonly', true);
    // Set other fields as needed and make them readonly

    $('#addProductForm').attr('action', `${window.contextPath}/GestioneCatalogoController?action=deleteProduct`);
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
                            if (capturedAction === 'delete') {
                                openDeleteForm(product);
                            } else if (capturedAction === 'modify') {
                                openModifyForm(product);
                            }
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
        fetchProducts(initialPage, 'modify'); // Fetch products with modify action
    });
    
    // Event handler for adding products
    $('#addProduct').click(function() {
        $('#changeable').html("Aggiungi un nuovo prodotto");
        enableModify();
    });
    
});