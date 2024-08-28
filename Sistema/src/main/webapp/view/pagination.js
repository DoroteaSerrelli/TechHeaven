/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function updatePagination (currentPage, action, hasNextPage) {
        const paginationDiv = document.getElementById('pagination');
        paginationDiv.innerHTML = ''; // Clear previous pagination

        // Add Previous Page Button
        if (currentPage > 1) {
            var previousPage = currentPage - 1;
            const prevButton = document.createElement('a');
            prevButton.innerHTML = '<img src="' + window.contextPath + '/view/img/arrow_back.png" alt="Previous Page">';
            prevButton.addEventListener('click', function(event) {
                event.preventDefault(); // Prevent default link behavior
                fetchOrders(previousPage, action); // Fetch previous page
            });
            paginationDiv.appendChild(prevButton);

            const prevText = document.createElement('h2');
            prevText.textContent = 'Pagina Precedente: ' + previousPage;
            paginationDiv.appendChild(prevText);
        }

        // Add Next Page Button
    if (hasNextPage) {
        var nextPage = currentPage + 1;
        const nextButton = document.createElement('a');
        nextButton.innerHTML = '<img src="' + window.contextPath + '/view/img/arrow_forward.png" alt="Next Page">';
        nextButton.addEventListener('click', function(event) {
            event.preventDefault(); // Prevent default link behavior
            fetchOrders(nextPage, action); // Fetch next page
        });
        paginationDiv.appendChild(nextButton);

        const nextText = document.createElement('h2');
        nextText.textContent = 'Pagina Successiva: ' + nextPage;
        paginationDiv.appendChild(nextText);
    } else {
        const nextDisabled = document.createElement('img');
        nextDisabled.src = window.contextPath + '/view/img/arrow_forward_disabled.png';
        nextDisabled.alt = 'No Next Page';
        paginationDiv.appendChild(nextDisabled);
    }
}
