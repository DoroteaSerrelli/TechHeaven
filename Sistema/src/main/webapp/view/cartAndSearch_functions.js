/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

    function hasClass(element, clsName) {
       return (' ' + element.className + ' ').indexOf(' ' + clsName + ' ') > -1;
       }
        // Function to toggle cart visibility
     function toggleCartVisibility() {
         // Hide cart initially
         $('#carrello').hide();         

         // Add click event listener to cart button
         $('#lg-bag').click(function() {
             if ($('#carrello').hasClass("active")) {
                 $('#carrello').removeClass("active");
                 $('#carrello').hide();          
             } else {
                 $('#carrello').addClass("active");  
                 $('#carrello').show();                  
             }
         });       
     }

     // Call toggleCartVisibility function when document is ready
     $(document).ready(function() {
         toggleCartVisibility();
     });
   // Add item to cart via AJAX
   function addToCart(productId, action) {
        // Make an AJAX request to the servlet
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "GestioneCarrelloController?action="+action, true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    // Request completed successfully
                    // You can handle the response here if needed
                    // Request completed successfully
                    // Reload cart section
                    reloadCartSection();
                    // Display notification
                    displayNotification("Item added to cart successfully!");
                } else {
                    // Error handling
                }
            }
        };
        // Send the request with product ID as a parameter
        xhr.send("productId=" + encodeURIComponent(productId));
    }
    // Function to reload cart section using AJAX
    function reloadCartSection() {

        $("#carrello").load(' #carrelloroba'); // Replace cart section content with response
    }

    // Function to display notification
    function displayNotification(message) {
        // Implement your notification display logic here
        document.getElementById("error").innerHTML = message; // Example: Show an alert message
    }

    // Validate search input
    function validateSearch() {
        var searchInput = document.getElementById("searchInput").value.trim();
        return searchInput.length !== 0;
    }