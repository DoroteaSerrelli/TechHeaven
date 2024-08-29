/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


function toggleSidebar(){
    var options_sidebar = document.getElementById("options_sidebar"); 
    if(options_sidebar.classList.contains("visible")){
        options_sidebar.classList.remove("visible");         
    }
    else{
        options_sidebar.classList.add("visible");
    }
 }

$(document).ready(function() {
    const page = 1; // Example page number

    $('#viewProducts').click(function() {
        fetchProducts(page); // Fetch the first page of products initially
    });

    function fetchProducts(page) {
        const url = `${window.contextPath}/GestioneCatalogoController?page=${page}`;
        console.log('Fetching URL:', url); // Debug URL

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
});

