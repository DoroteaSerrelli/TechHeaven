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
   function modifyCart(productId, action) {
        // Make an AJAX request to the servlet
        var outputMessage= "";
        var quantity;
        var quantityInput = document.getElementById("prod_quantità_"+ productId); // Append productId here);
        if(quantityInput===null) quantity=1;
        else quantity = quantityInput.value;
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "GestioneCarrelloController?action="+action +"&prod_quantità="+ encodeURIComponent(quantity), true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    // Request completed successfully
                    // You can handle the response here if needed
                    // Request completed successfully
                    // Reload cart section
                 //   reloadCartSection();
                    // Display notification               
                   
                    // Parse the JSON response
                    var response = JSON.parse(xhr.responseText);

                    // Handle the response here
                    var outputMessage = response.message; // Retrieve the message from the response
                    var status = response.status; // Retrieve the status from the response

                    // Optionally reload cart section or take other actions based on status
                    reloadCartSection();
                    
                    // Display notification
                    displayNotification(outputMessage, status);
                  
                } 
                } else {
                    // Handle error response from server
                    outputMessage ="Item already in cart. Cannot add duplicate items.";              
                }
                // After the cart section is reloaded, re-bind range input events
                //rebindRangeInputs();
          //  displayNotification(outputMessage);
        };
        // Send the request with product ID as a parameter
        xhr.send("productId=" + encodeURIComponent(productId) );
    }
    // Function to reload cart section using AJAX
    function reloadCartSection() {

        $("#carrello").load(' #carrelloroba'); // Replace cart section content with response
    }

    // Function to display notification
    function displayNotification(message, status) {
        // Implement your notification display logic here
        var error = document.getElementById("error");
        error.innerHTML = message; // Example: Show an alert message
        //
        // Define the classes you want to toggle between
        const classesToRemove = ["valid", "invalid"];
        
        // Remove any existing class that matches the ones in classesToRemove
        classesToRemove.forEach(function(className) {
            if (error.classList.contains(className)) {
                error.classList.remove(className);
            }
        });
        
        // Add the new status class
        error.classList.add(status);
    }

    // Validate search input
    function validateSearch() {
        var searchInput = document.getElementById("searchInput").value.trim();
        return searchInput.length !== 0;
    }    
    
   document.addEventListener('DOMContentLoaded', (event) => {
    // Event delegation for all range inputs
    document.body.addEventListener('input', function(event) {
        if (event.target.matches('input[type="range"][id^="prod_quantità_"]')) {
            const rangeInput = event.target;
            const productCode = rangeInput.id.split('_')[2];
            const rangeValue = document.querySelector(`#range_value_${productCode}`);
            
            if (rangeValue) {
                rangeValue.textContent = rangeInput.value;
            }
        }
    });
});