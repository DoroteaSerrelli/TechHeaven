/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

 function toggleView() {
    if (window.innerWidth <= 768) {
        $('#table-container').hide();
        $('#card-container').show();
    } else {
        $('#table-container').show();
        $('#card-container').hide();
    }
}
function fetchProducts(page, action) {
    const url = `/TechHeaven/GestioneOrdiniController?page=`+page+'&action='+action;
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
            const codiceCell = $('<td></td>').text(order.codiceOrdine);
            const statoCell = $('<td></td>').text(order.stato);
            const indirizzoSpCell = $('<td></td>').text(order.indirizzoSpedizione);
            row1.append(codiceCell, statoCell, indirizzoSpCell);
            $('#showpr tbody').append(row1);

            const row2 = $('<tr></tr>');
            const tipoSpCell = $('<td></td>').text(order.spedizione);
            const dataCell = $('<td></td>').text(order.data);
            const oraCell = $('<td></td>').text(order.ora);
            row2.append(tipoSpCell, dataCell, oraCell);
            $('#showpr tbody').append(row2);

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
