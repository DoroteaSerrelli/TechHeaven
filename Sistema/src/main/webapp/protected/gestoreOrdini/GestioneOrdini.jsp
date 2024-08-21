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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/common/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/catalog_options.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/view/style/product_table.css">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="${pageContext.request.contextPath}/view/ajax_orders_table_functions.js?ts=<%= System.currentTimeMillis() %>"></script>
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script>
    </head>    
    <body>     
       <jsp:include page="${pageContext.request.contextPath}/common/header.jsp"  flush="true"/>
       <jsp:include page="${pageContext.request.contextPath}/roleSelector.jsp"  flush="true"/>      
        <aside class="options_sidebar hidden" id="options_sidebar">
            <!-- Sidebar will be populated by JavaScript -->
        </aside>
        <main class="main-content" id="mainContent">
            <section id="centerMenu" class="center-menu">
                <div class="fe-box" id="viewOrders" onclick="moveToSidebar('viewOrders', 'viewOrdersForm'); fetchOrders(1, 'fetch_da_spedire')">
                    <img src="${pageContext.request.contextPath}/view/img/ordinidaspedire.png" alt="Visualizza Ordini Clienti">
                    <h5>Visualizza Ordini Da Spedire</h5>
                </div>
                    <div class="fe-box" id="viewSentOrders" onclick="moveToSidebar('viewSentOrders', 'viewOrdersForm'); fetchOrders(1, 'fetch_spediti')">
                        <img src="${pageContext.request.contextPath}/view/img/ordinispediti.png" alt="Visualizza Ordini Spediti">
                        <h5>Visualizza Ordini Spediti</h5>
                    </div>
                    <div class="fe-box" id="supplyingRequests">
                        <a href="GestioneApprovigionamentiController?action=viewList&page=1"><img src="${pageContext.request.contextPath}/view/img/supplyrequests.png" alt="Richieste Approvigionamento"></a>
                        <h5>Richieste Approvigionamento</h5>
                    </div>
                    <div class="fe-box" id="supplyingRequest" onclick="moveToSidebar('supplyingRequest', 'supplyingRequestForm')">
                        <a href="GestioneApprovigionamentiController?action=viewProductList&page=1"><img src="${pageContext.request.contextPath}/view/img/apprrovigionamento.png" alt="Approvigionamento">
                        </a>
                        <h5>Approvigionamento</h5>
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
        </section>             
           
       <script src="${pageContext.request.contextPath}/view/shifting_menu_manag_functions_sidebar.js"></script>        
    <jsp:include page="${pageContext.request.contextPath}/common/footer.jsp"  flush="true"/>
    </body>
</html>
