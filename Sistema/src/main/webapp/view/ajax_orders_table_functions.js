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

function filterProducts() {
    // Get the value from the input field and convert it to lowercase
    var input = document.getElementById("productFilter");
    var filter = input.value.toLowerCase();
    
    // Get the table and all its rows
    var table = document.getElementById("showpr");
    var tr = table.getElementsByTagName("tr");

    // Loop through all table rows (starting from the second row because the first is the header)
    for (var i = 1; i < tr.length; i++) {
        // Get the product name from the third column (index 2)
        var td = tr[i].getElementsByClassName("productName")[0];
        if (td) {
            var txtValue = td.textContent || td.innerText;
            // If the product name contains the filter text, display the row, otherwise hide it
            if (txtValue.toLowerCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }       
    }
}
function showSupplyForm(productId) {
    // Hide all rows except the one clicked
    var rows = document.querySelectorAll("#showpr tr");
    rows.forEach(function(row) {
        if (row.id !== "row-" + productId) {
            row.style.display = "none";
        }
    });
    
    // Get the specific row that was clicked
    var row = document.getElementById("row-" + productId);
    
    if(row){
        // Create the form element
        var form = document.getElementById("supplyingRequestForm");
        form.setAttribute("method", "post");
        form.setAttribute("action", "/GestioneApprovigionamentiController"); // Change this to your actual form handler URL

        // Example fields for the form
        form.innerHTML = `
            <h1>Supply Request for Product ID: ${productId}</h1>
            <div class="row">                            
            <div class="input-wrapper"> 
                <p>Inserisci La Quantità Da Richiedere</p>                             
                <input type="number" name="quantity" required>
            </div>    
            </div>    
            <div class="row">
                <div class="input-wrapper"> 
                    <p>Inserisci Il Fornitore:</p>
                    <input type="text" name="fornitore" oninput="validateCompanyName()" required>
                     <p>Inserisci L'Email Del Fornitore:</p>
                    <input type="text" name="email_fornitore" oninput="validateCompanyEmail()" required>
                </div>
            </div>
            <div class="row">
                <div class="input-wrapper">
                    <p>Inserisci Una Descrizione Dettagliata Della Richiesta:</p>
                    <textarea rows="4" cols="20" name="descrizione"></textarea>
                </div>    
            </div>
            <div class="errormsg">
               <h3 id="errormsg" style="color: #6f6fc9"></h3> 
            </div>
            <input type="hidden" name="product_id" value="${productId}">
            <button class="confirm_button" type="submit" onclick="return validateSupplyRequestForm()">Submit Request</button>
            <button type="button" onclick="cancelSupplyRequest()">Anulla Richiesta</button>
        `;
        
        form.style.display = "block"; // Ensure the form is visible
    }
}

function cancelSupplyRequest() {
    // Show the table again
    document.getElementById("showpr").style.display = "table";
    
    // Optionally, reload the page to reset the row to its original state
    location.reload();
}


 function toggleView() {
    if (window.innerWidth <= 768) {
        $('#table-container').hide();
        $('#card-container').show();
    } else {
        $('#table-container').show();
        $('#card-container').hide();
    }
}
function fetchOrders(page, action) {
    const url = `${window.contextPath}/GestioneOrdiniController?page=`+page+'&action='+action;
    console.log('Fetching URL:', url); // Debug URL

    $.ajax({
        url: url,
        method: 'GET',
        contentType: 'application/json',
        success: function(data) {
            console.log('Received Data:', data); // Verify data structure

            // Handle and display the products and pagination
            const orders = data.orders;
            console.log(orders);
            const totalPages = data.totalPages;
            // Clear both containers
            $('#showpr tbody').html('');
            $('#card-container').html('');

            orders.forEach(order => {
            // Table rows
            const row1 = $('<tr></tr>');
            // Create the acceptCell          
            // Create the accept button cell
             // Only add the accept button if the order status is not "Sent"
                if (order.stato !== 'Spedito') {
                    const acceptCell = $('<td></td>').html(
                        '<form action="'+window.contextPath+'/GestioneOrdiniController?action=accept_order" method="POST">' +
                        '<input type="hidden" name="orderId" value="' + order.codiceOrdine + '">' +
                        '<input type="submit" value="Accetta">' +
                        '</form>'
                    );
                    row1.append(acceptCell);
                }
            const codiceCell = $('<td></td>').text(order.codiceOrdine);
            const statoCell = $('<td></td>').text(order.stato);
            const indirizzoSpCell = $('<td></td>').text(order.indirizzoSpedizione);
            const tipoSpCell = $('<td></td>').text(order.spedizione);
            const dataCell = $('<td></td>').text(order.data);
            const oraCell = $('<td></td>').text(order.ora);
            row1.append(codiceCell, statoCell, indirizzoSpCell, tipoSpCell, dataCell, oraCell);
            $('#showpr tbody').append(row1);

        //    const row2 = $('<tr></tr>');
            
           // row2.append(tipoSpCell, dataCell, oraCell);
           // $('#showpr tbody').append(row2);

        // Card layout
        const card = $(
            '<div class="order-card">' +
                '<h3>Codice: ' + order.codiceOrdine + '</h3>' +
                '<p>Stato: ' + order.stato + '</p>' +
                '<p>Indirizzo Spedizione: ' + order.indirizzoSpedizione + '</p>' +
                '<p>Tipo Spedizione: ' + order.spedizione + '</p>' +
                '<p>Data Ordine: ' + order.data + '</p>' +
                '<p>Ora Ordine: ' + order.ora + '</p>' +
            '</div>'
        );

        $('#card-container').append(card);                    
        });                       

            // Update pagination
            const pagination = $('#pagination');
            pagination.html('');
            for (let i = 1; i <= totalPages; i++) {
                const link = $(`<a href="#">${i}</a>`);
                link.click(function(e) {
                    e.preventDefault();
                    fetchProducts(i,action);
                });
                pagination.append(link);
            }
            },
            error: function(xhr, status, error) {
                console.error('Error fetching data:', error);
            }
                });
            }
    $(document).ready(function() {
        const page = 1; // Example page number
        // Attach the resize event listener
        $(window).resize(function() {
            toggleView();
        });
    });
