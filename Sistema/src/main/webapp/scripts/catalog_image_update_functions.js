/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

 document.getElementById('imageUploadBtn').addEventListener('click', function(e) {
    e.preventDefault(); // Prevent the default form submission

    const form = document.getElementById('photoForm');
    const formData = new FormData(form);

    // Retrieve product data from session storage
    const storedProduct = sessionStorage.getItem('selectedPr');
    console.log('Stored Product:', storedProduct);

    // Parse the stored product data back to an object
    const product = storedProduct ? JSON.parse(storedProduct) : {}; // Default to empty object if not found
    console.log(product);
    if (product) {
        document.getElementById('productData').value = product; // Add product data to hidden field
    }
    // Check the product object and retrieve values
    console.log('Product:', product);
    // Add product data to FormData
    formData.append('product', JSON.stringify(product)); // Append product as JSON string

    // Send AJAX request with FormData
    $.ajax({
        url: form.action,
        method: 'POST',
        data: formData,
        processData: false, // Important for FormData
        contentType: false, // Important for FormData
        success: function(response) {
           /* console.log(response);
            // Handle success
            sessionStorage.setItem('outputMessage', response.message);
            window.location.href = response.redirectUrl;*/
        },
        error: function(xhr, status, error) {
            /*
            console.error(error);
            sessionStorage.setItem('outputMessage', xhr.message);
            window.location.href = xhr.redirectUrl;*/
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const deleteButtons = document.querySelectorAll('.delete-image-btn');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Retrieve product data from session storage
            const storedProduct = sessionStorage.getItem('selectedPr');
            const product = storedProduct ? JSON.parse(storedProduct) : {}; // Default to empty object if not found
            
            console.log('Product associated with images:', product);

            // Get the index or unique identifier of the image to be deleted
            const imageIndex = this.getAttribute('data-image-index'); // Assuming each image has a unique index or id
            
            // Optionally remove the image container from the UI
            const imageContainer = document.getElementById(`image-container-${imageIndex}`);
            if (imageContainer) {
                imageContainer.remove();
                console.log(`Image at index ${imageIndex} deleted.`);

                // Send an AJAX request to delete the image on the server
                $.ajax({
                    url: window.contextPath+'/ImageUpdater', // Adjust the URL to your servlet's path
                    method: 'POST',
                    data: { 
                        gallery_photoActions: 'delete', // Specify the action for deletion
                        imageIndex: imageIndex, // Send the index or identifier for the image to be deleted
                        product: JSON.stringify(product) // Send the product info with the request
                    },
                    success: function(response) {
                        console.log('Image deleted on the server');
                        // Handle success (optional UI updates or redirects)
                    },
                    error: function(xhr, status, error) {
                        console.error('Error deleting image:', error);
                        // Handle error (optional UI feedback)
                    }
                });
            }
        });
    });
});
