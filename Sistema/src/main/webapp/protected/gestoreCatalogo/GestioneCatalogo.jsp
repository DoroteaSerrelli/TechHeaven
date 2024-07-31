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
        <script src="view/roleFunctions.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                document.getElementById('viewProducts').addEventListener('click', function() {
                    fetchProducts(1); // Fetch the first page of products initially
                });
                function fetchProducts(page) {
                    const url = `/GestioneCatalogoController?page=${page}`; // Adjust URL if necessary
                    fetch(url)
                        .then(response => response.json())
                        .then(data => {
                             console.log('Products:', data.products);
                            console.log('Total Pages:', data.totalPages);
                            // Handle the data and update the UI accordingly
                            const products = data.products;
                            const totalPages = data.totalPages;
                            const table = document.getElementById('showpr');

                            // Clear existing rows except for the header
                            table.innerHTML = `
                                <tr>
                                    <th><strong>Image</strong></th>
                                    <th><strong>Nome</strong></th>
                                    <th><strong>Marca</strong></th>
                                    <th><strong>TopDescr</strong></th>
                                    <th><strong>Prezzo</strong></th>
                                </tr>
                            `;

                            products.forEach(product => {
                                const row = document.createElement('tr');
                                row.innerHTML = `
                                    <td><img src="image?productId=${product.codiceProdotto}" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"/></td>
                                    <td><h3>${product.nomeProdotto}</h3></td>
                                    <td><span>${product.marca}</span></td>
                                    <td><h5>${product.topDescrizione}</h5></td>
                                    <td><h4>${product.prezzo}€</h4></td>
                                `;
                                table.appendChild(row);
                            });

                            // Update pagination links
                            const pagination = document.getElementById('pagination');
                            pagination.innerHTML = '';
                            for (let i = 1; i <= totalPages; i++) {
                                const link = document.createElement('a');
                                link.href = '#';
                                link.textContent = i;
                                link.addEventListener('click', function(e) {
                                    e.preventDefault();
                                    fetchProducts(i);
                                });
                                pagination.appendChild(link);
                            }
                        })
                        .catch(error => console.error('Error fetching products:', error));
                }
            });
            </script>
    </head>    
    <body>
        <link rel="stylesheet" href="../../common/style.css">
        <link rel="stylesheet" href="../../view/style/catalog_options.css">
       <jsp:include page="../../common/header.jsp"  flush="true"/>
       <jsp:include page="../../roleSelector.jsp"  flush="true"/>      
       <div class="container">
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
            </section>
            </div>
        </div>      
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
        </main>
    </div>
       <div id="dynamicContent">
           <form action="${pageContext.request.contextPath}/GestioneCatalogoController" method="post" enctype="multipart/form-data">
               <label>ID del prodotto:</label>
               <input type="text" name="prod_id"/>
               <input type="file" name="file" accept="image/*"/>
               <input type="submit" class="confirm_button" value="Upload"/>
           </form>
           
       </div>
       <script src="${pageContext.request.contextPath}/view/shifting_menu_manag_functions_sidebar.js"></script>        
    <jsp:include page="../../common/footer.jsp"  flush="true"/>       
    </body>
</html>
