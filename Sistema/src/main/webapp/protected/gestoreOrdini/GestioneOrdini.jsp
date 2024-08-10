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
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="${pageContext.request.contextPath}/view/ajax_orders_table_functions.js"></script>
    </head>    
    <body>     
       <jsp:include page="../../common/header.jsp"  flush="true"/>
       <jsp:include page="../../roleSelector.jsp"  flush="true"/>      
        <aside class="options_sidebar hidden" id="options_sidebar">
            <!-- Sidebar will be populated by JavaScript -->
        </aside>
        <main class="main-content" id="mainContent">
            <section id="centerMenu" class="center-menu">
                <div class="fe-box" id="viewOrders" onclick="moveToSidebar('viewOrders', 'viewOrdersForm'); fetchProducts(1, 'fetch_da_spedire')">
                    <img src="../../view/img/ordinidaspedire.png" alt="Visualizza Ordini Clienti">
                    <h6>Visualizza Ordini Da Spedire</h6>
                </div>
                    <div class="fe-box" id="viewSentOrders" onclick="moveToSidebar('viewSentOrders', 'viewOrdersForm'); fetchProducts(1, 'fetch_spediti')">
                        <img src="../../view/img/ordinispediti.png" alt="Visualizza Ordini Spediti">
                        <h6>Visualizza Ordini Spediti</h6>
                    </div>
                    <div class="fe-box" id="supplyingRequest" onclick="moveToSidebar('supplyingRequest', 'supplyingRequestForm')">
                        <img src="../../view/img/apprrovigionamento.png" alt="Elimina un prodotto">
                        <h6>Approvigionamento</h6>
                    </div>
                </section>
           </main>
                
                 <section id="forms">
                    <section id="viewOrdersForm" class="form-section hidden">
                        <h2>Visualizza Prodotti</h2>
                        <div id="table-container">
                            <table id="showpr">
                                <thead>
                                    <tr class="header-row-1">
                                        <th><strong>Codice</strong></th>
                                        <th><strong>Stato</strong></th>
                                        <th><strong>IndirizzoSpedizione</strong></th>
                                    </tr>
                                    <tr class="header-row-2">
                                        <th><strong>TipoSpedizione</strong></th>
                                        <th><strong>DataOrdine</strong></th>
                                        <th><strong>OraOrdine</strong></th>
                                    </tr>
                                </thead>
                                <tbody>                                                                 
                                    <!-- Rows will be added here -->
                                </tbody>                   
                            </table>
                        </div>    
                        <div id="card-container" style="display: none;">
                            <!-- Cards will be inserted here -->
                        </div>
                        <div id="error">
                            <% String errormsg="";
                                errormsg= (String)request.getAttribute("error");
                                if(errormsg==null) errormsg="";                                                       
                            %>
                            <%=errormsg%>
                        </div>                                          
                    <%%>
                         
                       <div id="pagination"></div>
            </section>
                
                <section id="addProductForm" class="form-section hidden">
                    <!-- Your form for adding a new product -->
                    <h2>Visualizza Ordini Spediti</h2>
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
           
       <script src="${pageContext.request.contextPath}/view/shifting_menu_manag_functions_sidebar.js"></script>        
    <jsp:include page="../../common/footer.jsp"  flush="true"/>
    </body>
</html>
