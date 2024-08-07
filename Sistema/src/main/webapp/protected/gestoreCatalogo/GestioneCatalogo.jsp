<%-- 
    Document   : GestioneCatalogo
    Created on : 21-mar-2024, 17.36.51
    Author     : raffy
--%>

<%@page import="java.util.Collection"%>
<%@page import="application.NavigazioneService.ProxyProdotto"%>
<%@page import="application.RegistrazioneService.Ruolo"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.RegistrazioneService.Utente"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="../../common/style.css">
        <link rel="stylesheet" href="../../view/style/catalog_options.css">
        <link rel="stylesheet" href="../../view/style/product_table.css">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    </head>    
    <body>     
       <jsp:include page="../../common/header.jsp"  flush="true"/>
       <jsp:include page="../../roleSelector.jsp"  flush="true"/>      
        <aside class="options_sidebar hidden" id="options_sidebar">
            <!-- Sidebar will be populated by JavaScript -->
        </aside>
        <main class="main-content" id="mainContent">
            <section id="centerMenu" class="center-menu">
                <div class="fe-box" id="viewProducts" onclick="moveToSidebar('viewProducts', 'viewProductsForm')">
                    <img src="../../view/img/listaprodotto.png" alt="Visualizza Prodotti">
                    <h6>Visualizza Prodotti</h6>
                </div>
                    <div class="fe-box" id="addProduct" onclick="moveToSidebar('addProduct', 'addProductForm')">
                        <img src="../../view/img/addprodotto.png" alt="Aggiungi un nuovo prodotto">
                        <h6>Aggiungi un nuovo prodotto</h6>
                    </div>
                    <div class="fe-box" id="removeProduct" onclick="moveToSidebar('removeProduct', 'removeProductForm')">
                        <img src="../../view/img/removeprodotto.png" alt="Elimina un prodotto">
                        <h6>Elimina un prodotto</h6>
                    </div>
                    <div class="fe-box" id="modifyProperties" onclick="moveToSidebar('modifyProperties', 'modifyPropertiesForm')">
                        <img src="../../view/img/modproperties.png" alt="Modifica caratteristiche prodotto">
                        <h6>Modifica caratteristiche prodotto</h6>
                    </div>
                </section>
           </main>
                
                 <section id="forms">
                    <section id="viewProductsForm" class="form-section hidden">
                        <h2>Visualizza Prodotti</h2>
                         <table id="showpr">
                             <tr><th><strong>Image</strong></th><!-- Immagine -->
                                 <th><strong>Nome</strong></th><!-- Nome prodotto -->
                                 <th><strong>Marca</strong></th><!-- Marca -->
                                 <th><strong>TopDescr</strong></th><!-- Top Descrizione -->
                                 <th><strong>Prezzo</strong></th><!-- Prezzo -->
                             </tr>
                        <div id="error">
                            <% String errormsg="";
                                errormsg= (String)request.getAttribute("error");
                                if(errormsg==null) errormsg="";                                                       
                            %>
                            <%=errormsg%>
                        </div>                                          
                    <%%>
                         </table> 
                       <div id="pagination"></div>
            </section>
                
                <section id="addProductForm" class="form-section hidden">
                    <!-- Your form for adding a new product -->
                    <h2>Aggiungi un nuovo prodotto</h2>
                    <form>
                        <label for="productName">Nome prodotto:</label>
                        <input type="text" id="productName" name="productName">
                        <button type="submit">Aggiungi</button>
                    </form>
                </section>
                <section id="removeProductForm" class="form-section hidden">
                    <!-- Your form for removing a product -->
                    <h2>Elimina un prodotto</h2>
                    <p>Form content for removing a product...</p>
                </section>
                <section id="modifyPropertiesForm" class="form-section hidden">
                    <!-- Your form for modifying product properties -->
                    <h2>Modifica caratteristiche prodotto</h2>
                    <p>Form content for modifying product properties...</p>
                </section>
            </section>             
       <div id="dynamicContent">
           <form action="${pageContext.request.contextPath}/GestioneCatalogoController" method="post" enctype="multipart/form-data">
               <label>ID del prodotto:</label>
               <input type="text" name="prod_id"/>
               <input type="file" name="file" accept="image/*"/>
               <input type="submit" class="confirm_button" value="Upload"/>
           </form>
           
       </div>
                    <script>
           $(document).ready(function() {
    const page = 1; // Example page number

    $('#viewProducts').click(function() {
        fetchProducts(page); // Fetch the first page of products initially
    });

    function fetchProducts(page) {
        const url = `/TechHeaven/GestioneCatalogoController?page=`+page;
        console.log('Fetching URL:', url); // Debug URL

        $.ajax({
            url: url,
            method: 'GET',
            contentType: 'application/json',
            success: function(data) {
                console.log('Received Data:', data); // Verify data structure

                // Handle and display the products and pagination
                const products = data.products;
                const totalPages = data.totalPages;
                const table = $('#showpr');

                table.html(`
                    <tr>
                        <th><strong>Image</strong></th>
                        <th><strong>Nome</strong></th>
                        <th><strong>Marca</strong></th>
                        <th><strong>TopDescr</strong></th>
                        <th><strong>Prezzo</strong></th>
                    </tr>
                `);

               table.html(`
            <tr>
                <th><strong>Image</strong></th>
                <th><strong>Nome</strong></th>
                <th><strong>Marca</strong></th>
                <th><strong>TopDescr</strong></th>
                <th><strong>Prezzo</strong></th>
            </tr>
        `);

                products.forEach(product => {
                    const row = $('<tr></tr>');
                     
                    const imgSrc = `${pageContext.request.contextPath}/image?productId=`+product.codiceProdotto;
                    console.log(`Image URL: ${imgSrc}`); // Log the image URL to check

                    const imgCell = $('<td></td>').append(
                    $('<img>').attr('src', imgSrc)
                              .attr('alt', 'alt')
                              .on('error', function() { 
                                  this.onerror = null; // Prevent infinite loop
                                  this.src = `${pageContext.request.contextPath}/view/img/placeholder.png`; 
                              })
                    );
                    const nomeCell = $('<td></td>').append(
                        $('<h3></h3>').text(product.nomeProdotto)
                    );

                    const marcaCell = $('<td></td>').append(
                        $('<span></span>').text(product.marca)
                    );

                    const descrCell = $('<td></td>').append(
                        $('<h5></h5>').text(product.topDescrizione)
                    );

                    const prezzoCell = $('<td></td>').append(
                        $('<h4></h4>').text(product.prezzo+"€")
                    );

                    row.append(imgCell, nomeCell, marcaCell, descrCell, prezzoCell);
                    table.append(row);
                });

                // Update pagination
                const pagination = $('#pagination');
                pagination.html('');
                for (let i = 1; i <= totalPages; i++) {
                    const link = $(`<a href="#">${i}</a>`);
                    link.click(function(e) {
                        e.preventDefault();
                        fetchProducts(i);
                    });
                    pagination.append(link);
                }
                },
                error: function(xhr, status, error) {
                    console.error('Error fetching data:', error);
                }
                    });
                }
            });
            </script>
       <script src="${pageContext.request.contextPath}/view/shifting_menu_manag_functions_sidebar.js"></script>        
    <jsp:include page="../../common/footer.jsp"  flush="true"/>       
    </body>
</html>
