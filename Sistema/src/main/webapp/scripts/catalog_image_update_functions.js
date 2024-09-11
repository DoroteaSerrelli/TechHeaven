/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function updateGallery(images) {
    const galleryContainer = document.getElementById('gallery-container');
    const thumbnailsContainer = document.querySelector('.gallery-thumbnails');

    galleryContainer.innerHTML = '';
    thumbnailsContainer.innerHTML = '';

    images.forEach((imgData, index) => {
        const imageContainer = document.createElement('div');
        imageContainer.classList.add('thumbnail-container');
        imageContainer.id = `image-container-${index}`;

        const imgElement = document.createElement('img');
        imgElement.src = imgData;
        imgElement.alt = `Product Image ${index + 1}`;
        imgElement.classList.add('gallery-thumbnail');
        imgElement.loading = 'lazy';

        imgElement.onclick = () => changeImage(imgElement);

        const deleteButton = document.createElement('button');
        deleteButton.classList.add('delete-image-btn');
        deleteButton.setAttribute('data-image-index', index);
        deleteButton.textContent = 'Delete';

        imageContainer.appendChild(imgElement);
        imageContainer.appendChild(deleteButton);

        thumbnailsContainer.appendChild(imageContainer);
    });
    attachDeleteButtonListeners();
}

function changeImage(imgElement) {
    const currentImage = document.getElementById('currentImage');
    const thumbnails = document.querySelectorAll('.gallery-thumbnail');

    currentImage.src = imgElement.src;
    thumbnails.forEach(thumbnail => thumbnail.classList.remove('selected'));
    imgElement.classList.add('selected');
}

function attachDeleteButtonListeners() {
    const deleteButtons = document.querySelectorAll('.delete-image-btn');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const imageIndex = this.getAttribute('data-image-index');            
            const imageContainer = document.getElementById(`image-container-${imageIndex}`);

            if (imageContainer) {
                imageContainer.remove();  // Remove the image from the UI
                console.log("Image at index ${imageIndex} deleted.");

                retrieveAllData(function(data) {
                    const { product, galleryImages } = data;          
                // Create a copy of the product object
                let productCopy = { ...product };

                // Store the image separately and remove it from the product
                let productImage = productCopy.topImmagine || null; // Assuming img is the image attribute
                delete productCopy.topImmagine;
                
                // Remove the selected image from the gallery
               
                //galleryImages = galleryImages.filter((_, index) => index !== parseInt(imageIndex, 10));

                // Create a copy of the product object without the gallery images
                delete productCopy.galleriaImmagini;
                
                console.log("Product without image:", productCopy);
                console.log("Image data:", productImage);

                   $.ajax({
                        url: window.contextPath + '/ImageUpdater',
                        method: 'POST',
                        data: {
                            gallery_photoActions: 'delete',
                            imageIndex: imageIndex,  // Send the base64 image to remove
                            product: JSON.stringify(productCopy) // Send product details as JSON
                        },
                        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', // Set the correct content type
                        success: function(response) {
                            console.log('Image deleted on the server');

                            // Update gallery UI with the new list of images
                            //updateGallery(galleryImages);
                        },
                        error: function(xhr, status, error) {
                            console.error('Error deleting image:', error);
                        }
                    });
                });
            }
        });
    });
}
document.getElementById('imageUploadBtn').addEventListener('click', function(e) {
    e.preventDefault(); // Prevent the default form submission

    const form = document.getElementById('photoForm');
    const formData = new FormData(form);

    // Retrieve all data (product and gallery images) from IndexedDB
    retrieveAllData(function(data) {
        if (data.product) {
            document.getElementById('productData').value = JSON.stringify(data.product); // Add product data to hidden field
        }

        // Add product data to FormData
        formData.append('product', JSON.stringify(data.product)); // Append product as JSON string

        // Send AJAX request with FormData
        $.ajax({
            url: form.action,
            method: 'POST',
            data: formData,
            processData: false, // Important for FormData
            contentType: false, // Important for FormData
            success: function(response) {
                const imageBytes  = response.galleryImages;
                const updated_product = response.product;

                // Store the gallery images as bytes in IndexedDB
                storeGalleryImages(imageBytes);

                // Convert image bytes to base64 for display
                const base64GalleryImages = imageBytes.map(img => arrayBufferToBase64(img));

                // Update the gallery UI with base64 images
                updateGallery(base64GalleryImages);
            },
            error: function(xhr, status, error) {
                console.error('Error uploading image:', error);
            }
        });
    });
});


document.getElementById('resetFormBtn').addEventListener('click', function() {
    const form = document.getElementById('photoForm');
    
    // Reset form fields
    form.reset();
    
});
