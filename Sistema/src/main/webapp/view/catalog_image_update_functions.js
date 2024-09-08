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
