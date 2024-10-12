<%-- 
    Document   : GestioneOrdini
    Created on : 2 ago 2024, 15:09:57
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
		import = "java.util.ArrayList,
				  java.util.Collection"%>
<!DOCTYPE html>
<html lang = "en">
     <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/style.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/catalog_options.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/product_table.css">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="<%= request.getContextPath()%>/scripts/ajax_orders_table_functions.js?ts=<%= System.currentTimeMillis() %>"></script>
        <script src="<%= request.getContextPath()%>/scripts/pagination.js?ts=<%= System.currentTimeMillis() %>"></script>      
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script>
        <%
            System.out.println("Setting previosly_fetched_page to 0 in JSP");
           // request.getSession().setAttribute("previosly_fetched_page", 0);
            Collection<?> nextPageResults = (Collection<?>) session.getAttribute("nextPageResults");
            if (nextPageResults == null) {
                session.setAttribute("nextPageResults", new ArrayList<>());
            }
        %>

    </head>    
    <body>     
       <jsp:include page="/common/header.jsp"  flush="true"/>
       <jsp:include page="/protected/cliente/roleSelector.jsp"  flush="true"/>          
       <div class="errormsg">
            <% String errormsg="";
                errormsg= (String)request.getSession().getAttribute("error");
                String status = (String) request.getSession().getAttribute("status");
                request.getSession().removeAttribute("error");
                request.getSession().removeAttribute("status");               
                if(errormsg==null) errormsg="";                                                       
            %>
            <h2 style="text-align: center" class="error <%=status%>"><%=errormsg%></h2>
        </div>   
       <div id="pagination"></div>
        <aside class="options_sidebar hidden" id="options_sidebar">
            <!-- Sidebar will be populated by JavaScript -->
        </aside>
        <main class="main-content" id="mainContent">
            <section id="centerMenu" class="center-menu">
                <div class="fe-box" id="viewOrders" onclick="moveToSidebar('viewOrders', 'viewOrdersForm'); fetchOrders(1, 'fetch_da_spedire')">
                    <img src="<%= request.getContextPath()%>/images/site_images/ordinidaspedire.png" alt="Visualizza Ordini Clienti">
                    <h5>Visualizza Ordini Da Spedire</h5>
                </div>
                    <div class="fe-box" id="viewSentOrders" onclick="moveToSidebar('viewSentOrders', 'viewOrdersForm'); fetchOrders(1, 'fetch_spediti')">
                        <img src="<%= request.getContextPath()%>/images/site_images/ordinispediti.png" alt="Visualizza Ordini Spediti">
                        <h5>Visualizza Ordini Spediti</h5>
                    </div>
                    <div class="fe-box" id="supplyingRequests">
                        <a href="<%= request.getContextPath()%>/GestioneApprovigionamentiController?action=viewList&page=1"><img src="<%= request.getContextPath()%>/images/site_images/supplyrequests.png" alt="Richieste Approvvigionamento"></a>
                        <h5>Richieste Approvvigionamento</h5>
                    </div>
                    <div class="fe-box" id="supplyingRequest">
                        <a href="<%= request.getContextPath()%>/GestioneApprovigionamentiController?action=viewProductList&page=1"><img src="<%= request.getContextPath()%>/images/site_images/approvvigionamento.png" alt="Approvvigionamento">
                        </a>
                        <h5>Approvvigionamento</h5>
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
                                        <th><strong>TipoSpedizione</strong></th>
                                        <th><strong>DataOrdine</strong></th>
                                        <th><strong>OraOrdine</strong></th>
                                </thead>
                                <tbody>                                                                 
                                    <!-- Rows will be added here -->
                                </tbody>                   
                            </table>
                        </div>    
                        <div id="card-container" style="display: none;">
                            <!-- Cards will be inserted here -->
                        </div>                                                                                     
                       <div id="pagination"></div>
            </section>
        </section>                       
       <script src="<%= request.getContextPath()%>/scripts/shifting_menu_manag_functions_sidebar.js"></script>        
    <jsp:include page="/common/footer.jsp"  flush="true"/>
    </body>
</html>
