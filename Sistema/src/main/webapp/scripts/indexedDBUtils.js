/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


const dbName = 'ProductDB';
const dbVersion = 2; // Increment this version number when you need to update the schema
let db = null;

// Function to open IndexedDB and create object stores
function openDB(version) {
    return new Promise((resolve, reject) => {
        const request = indexedDB.open(dbName, version);
        request.onupgradeneeded = function(event) {
            const db = event.target.result;
            //console.log(db);
            if (!db.objectStoreNames.contains('productDetails')) {
                db.createObjectStore('productDetails', { keyPath: 'key' });
            }
            if (!db.objectStoreNames.contains('productImages')) {
                db.createObjectStore('productImages', { keyPath: 'id' });
            }
        };

        request.onsuccess = function(event) {
            db = event.target.result;
            resolve(db);
        };

        request.onerror = function(event) {
            reject(event.target.error);
        };
    });
}

 // Function to clear gallery images from IndexedDB
function clearGalleryImages() {
    openDB(dbVersion).then(db => {
        const transaction = db.transaction('productImages', 'readwrite');
        const store = transaction.objectStore('productImages');
        store.delete('galleryImages');  // Delete the gallery images entry

        transaction.oncomplete = function() {
            console.log('Gallery images cleared from IndexedDB');
        };

        transaction.onerror = function(event) {
            console.error('Error clearing gallery images from IndexedDB:', event.target.error);
        };
    }).catch(error => {
        console.error('Error opening IndexedDB:', error);
    });
}


// Function to retrieve data from IndexedDB
function retrieveAllData(callback) {
    openDB(dbVersion).then(db => {
        const transaction = db.transaction(['productDetails', 'productImages'], 'readonly');
        const productStore = transaction.objectStore('productDetails');
        const imageStore = transaction.objectStore('productImages');

        // Fetch all product details
        const productRequest = productStore.getAll();  
        // Fetch gallery images by specific key
        const galleryRequest = imageStore.get('galleryImages'); 

        let results = {};

        productRequest.onsuccess = function() {
            if (productRequest.result.length > 0) {
                results.product = productRequest.result[0].details;
            } else {
                console.log('No product details found.');
                results.product = null;
            }
        };

        galleryRequest.onsuccess = function() {
            if (galleryRequest.result) {
                results.galleryImages = galleryRequest.result.images;
               // console.log('Retrieved gallery images:', results.galleryImages);
            } else {
                console.log('No gallery images found.');
                results.galleryImages = [];
            }
        };

        transaction.oncomplete = function() {
            console.log('Transaction completed.');
            callback(results);  // Callback with the retrieved data
        };

        transaction.onerror = function(event) {
            console.error('Error retrieving data from IndexedDB:', event.target.error);
        };
    }).catch(error => {
        console.error('Error opening IndexedDB:', error);
    });
}


function performTransaction(storeName, mode, callback) {
    openDB(dbVersion).then(db => {
        const transaction = db.transaction(storeName, mode);
        const store = transaction.objectStore(storeName);
        callback(store, transaction);
        
        transaction.onerror = function(event) {
            console.error("Error performing transaction on ${storeName}:", event.target.error);
        };
    }).catch(error => {
        console.error('Error opening IndexedDB for transaction:', error);
    });
}


// Function to cache data locally
let cachedProduct = null;
let cachedGalleryImages = null;

function retrieveFromCache(key) {
    if (key === 'product') return cachedProduct;
    if (key === 'galleryImages') return cachedGalleryImages;
    return null;
}

function storeInCache(key, value) {
    if (key === 'product') cachedProduct = value;
    if (key === 'galleryImages') cachedGalleryImages = value;
}

function retrieveAllDataWithCache(callback) {
    openDB(dbVersion).then(db => {
        const transaction = db.transaction(['productDetails', 'productImages'], 'readonly');
        const productStore = transaction.objectStore('productDetails');
        const imageStore = transaction.objectStore('productImages');

        let product = retrieveFromCache('product');
        let galleryImages = retrieveFromCache('galleryImages');

        if (!product) {
            productStore.get('selectedPr').onsuccess = function(e) {
                product = e.target.result ? e.target.result.details : null;
                storeInCache('product', product);
                if (galleryImages) callback({ product, galleryImages });
            };
        }

        if (!galleryImages) {
            imageStore.get('galleryImages').onsuccess = function(e) {
                galleryImages = e.target.result ? e.target.result.images : [];
                storeInCache('galleryImages', galleryImages);
                if (product) callback({ product, galleryImages });
            };
        }

        transaction.onerror = function(event) {
            console.error('Error retrieving data from IndexedDB:', event.target.error);
        };
    }).catch(error => {
        console.error('Error opening IndexedDB:', error);
    });
}

function storeGalleryImages(images) {
    openDB(dbVersion).then(db => {
        const transaction = db.transaction('productImages', 'readwrite');
        const store = transaction.objectStore('productImages');
        store.put({ id: 'galleryImages', images: images });

        transaction.oncomplete = function() {
            console.log('Gallery images stored successfully');
        };

        transaction.onerror = function(event) {
            console.error('Error storing gallery images:', event.target.error);
        };
    }).catch(error => {
        console.error('Error opening IndexedDB:', error);
    });
}



function getGalleryImages(callback) {
    openDB(dbVersion).then(db => {
        const transaction = db.transaction('productImages', 'readonly');
        const store = transaction.objectStore('productImages');
        const getRequest = store.get('galleryImages');

        getRequest.onsuccess = function() {
            callback(getRequest.result ? getRequest.result.images : []);
        };

        getRequest.onerror = function(event) {
            console.error('Error retrieving gallery images:', event.target.error);
        };
    }).catch(error => {
        console.error('Error opening IndexedDB:', error);
    });
}

function storeProductDetails(productDetails, key) {
    openDB(dbVersion).then(db => {
        const transaction = db.transaction('productDetails', 'readwrite');
        const store = transaction.objectStore('productDetails');
        store.put({ key: key, details: productDetails });

        transaction.oncomplete = function() {
            console.log('Product details stored successfully');
        };

        transaction.onerror = function(event) {
            console.error('Error storing product details:', event.target.error);
        };
    }).catch(error => {
        console.error('Error opening IndexedDB:', error);
    });
}

function getProductDetails(key, callback) {
    openDB(dbVersion).then(db => {
        const transaction = db.transaction('productDetails', 'readonly');
        const store = transaction.objectStore('productDetails');
        const getRequest = store.get(key);

        getRequest.onsuccess = function() {
            callback(getRequest.result ? getRequest.result.details : {});
        };

        getRequest.onerror = function(event) {
            console.error('Error retrieving product details:', event.target.error);
        };
    }).catch(error => {
        console.error('Error opening IndexedDB:', error);
    });
}
