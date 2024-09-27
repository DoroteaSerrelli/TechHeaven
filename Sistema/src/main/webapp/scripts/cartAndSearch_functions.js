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
 function modifyCart(productId, action, callback) {
    var quantityInput = document.getElementById("prod_quantità_" + productId);
    var quantity = quantityInput ? quantityInput.value : 1;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "GestioneCarrelloController?action=" + action + "&prod_quantità=" + encodeURIComponent(quantity), true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    //console.log(xhr);
    xhr.onreadystatechange = function () {
      ///// SOLO PER TESTING MA RICORDA CHE C'E' XMLHttpRequest.DONE ED E' EQUIVALENTE A 4.
      ///// POICHE' FUNZIONA IN QUESTO MODO:
      /*0 (UNSENT): The XMLHttpRequest has been created but has not yet been sent.
        1 (OPENED): The open() method has been called. The request is ready to be sent.
        2 (HEADERS_RECEIVED): The send() method has been called, and the headers of the response have been received.
        3 (LOADING): The response is being downloaded. If the response is large, this state may be reached before the request is completed.
        4 (DONE): The request has completed (either successfully or unsuccessfully).*/
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                // Parse JSON response from the server
                var response = JSON.parse(xhr.responseText);
            //     console.log('Response received: ', xhr.responseText);
                if (response.status === "valid") {   
              //       console.log('Response valid, calling update functions');
                    // Update the item price, quantity, and total amount in the DOM
                    updateCartItem(productId, response.updatedPrice, response.updatedQuantity);
                    updateCartTotal(response.totalAmount);
                }

                // If item is removed, remove it from the DOM
                if (action === "rimuoviDalCarrello") {
                    removeCartItem(productId);
                }
                // Display notification if needed
                displayNotification(response.message, response.status);
            } else {
                displayNotification("Error updating the cart", "error");
            }
        }
    };
    xhr.onerror = function () {
        console.error('Request failed');
        displayNotification("Request failed", "error");
    };
    xhr.send("productId=" + encodeURIComponent(productId));
}


function isCartEmpty() {
    // Assuming there's a way to determine if the cart is empty
    // For example, check the length of cart items or a specific DOM element
    return document.querySelectorAll('.cart-item').length === 0; // Adjust the selector as needed
}

function removeCartItem(productId) {
    var itemElement = document.getElementById("item_" + productId);
    if (itemElement) {
        itemElement.remove(); // This will remove the item from the DOM
    }
    // Check if the cart is empty
    if (isCartEmpty()) {
        reloadCartSection();
    }
}

    
function updateCartItem(productId, updatedPrice, updatedQuantity) {
    var priceElement = document.querySelector("#item_" + productId + " .prezzo"); // Updated selector    
    var quantityElement = document.querySelector("#item_" + productId + " .quantita"); // Updated selector   
    var quantityElementRange = document.getElementById("range_value_" + productId);
    // Replace the comma with a dot to ensure proper numerical conversion
    updatedPrice = updatedPrice.replace(',', '.');

    // Convert to a number
    updatedPrice = Number(updatedPrice);
    
    if (priceElement) {
        // Update the price with the currency symbol
        priceElement.textContent = updatedPrice.toFixed(2).replace('.', ',') + "€"; // Ensure two decimal places and use comma
    }
    if (quantityElementRange && quantityElement) {
        // Update the displayed quantity
        quantityElementRange.textContent = updatedQuantity;
        // Update the displayed quantity
        quantityElement.textContent = updatedQuantity;
    }
}

    function updateCartTotal(totalAmount) {
        var totalElement = document.getElementById("total_amount");
        if (totalElement) {
            totalElement.textContent = totalAmount + "€";
        }
    }

    // Function to reload cart section using AJAX
    function reloadCartSection() {
        $("#showpr").load(" #showpr > *"); // Reload only the cart section with updated content
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

// Add to cart and redirect logic
function addToCartAndRedirect(productId, action, cartUrl) {
    modifyCart(productId, action, function () {
        // This callback will be executed after the AJAX request is successful
        window.location.href = cartUrl; // Redirect to the cart page
    });
}
function toggleDrawer() {
    var sidebar = document.getElementById("complete_order");
    sidebar.classList.toggle("active");
    var drawer_toggle = document.getElementById("drawer-toggle");
    drawer_toggle.classList.toggle("open_drawer");
}


