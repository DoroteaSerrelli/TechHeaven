<%-- 
    Document   : GestioneOrdini
    Created on : 2 ago 2024, 15:09:57
    Author     : raffa
--%>
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
        <script src="${pageContext.request.contextPath}/view/roleFunctions.js"></script>
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
                <div class="fe-box" id="viewOrders" onclick="moveToSidebar('viewOrders', 'viewOrdersForm')">
                    <img src="../../view/img/ordinidaspedire.png" alt="Visualizza Ordini Clienti">
                    <h6>Visualizza Ordini Da Spedire</h6>
                </div>
                    <div class="fe-box" id="viewSentOrders" onclick="moveToSidebar('viewSentOrders', 'addProductForm')">
                        <img src="../../view/img/ordinispediti.png" alt="Aggiungi un nuovo prodotto">
                        <h6>Aggiungi un nuovo prodotto</h6>
                    </div>
                    <div class="fe-box" id="supplyingRequest" onclick="moveToSidebar('supplyingRequest', 'supplyingRequestForm')">
                        <img src="../../view/img/apprrovigionamento.png" alt="Elimina un prodotto">
                        <h6>Elimina un prodotto</h6>
                    </div>
                </section>
           </main>
                
                 <section id="forms">
                    <section id="viewOrdersForm" class="form-section hidden">
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
                <section id="supplyingRequestForm" class="form-section hidden">
                    <!-- Your form for modifying product properties -->
                    <h2>Modifica caratteristiche prodotto</h2>
                    <p>Form content for modifying product properties...</p>
                </section>
            </section>             
           <script>               
            $(document).ready(function() {
            const page = 1; // Example page number

            $('#viewOrders').click(function() {
                fetchProducts(page); // Fetch the first page of products initially
            });

            function fetchProducts(page) {
                const url = `/TechHeaven/GestioneOrdiniController?page=`+page;
                console.log('Fetching URL:', url); // Debug URL

                $.ajax({
                    url: url,
                    method: 'GET',
                    contentType: 'application/json',
                    success: function(data) {
                        console.log('Received Data:', data); // Verify data structure

                        // Handle and display the products and pagination
                        const orders = data.orders;
                        const totalPages = data.totalPages;
                        const table = $('#showpr');
                        //Tabella-Ordini : Caratteristiche Ordini 
                        table.html(`
                            <tr>
                                <th><strong>Stato</strong></th>
                                <th><strong>Email</strong></th>
                                <th><strong>IndirizzoSpedizione</strong></th>
                                <th><strong>TipoSpedizione</strong></th>
                                <th><strong>DataOrdine</strong></th>
                                <th><strong>OraOrdine</strong></th>           
                            </tr>
                        `);              

                        orders.forEach(order => {
                            const row = $('<tr></tr>');

                            const statoCell = $('<td></td>').append(
                                $('<h3></h3>').text(order.stato)
                            );
  
                            const indirizzoSpCell = $('<td></td>').append(
                                $('<span></span>').text(order.indirizzoSpedizione)
                            );

                            const tipoSpCell = $('<td></td>').append(
                                $('<h5></h5>').text(order.spedizione)
                            );
                    
                            row.append(statoCell, indirizzoSpCell, tipoSpCell);
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
