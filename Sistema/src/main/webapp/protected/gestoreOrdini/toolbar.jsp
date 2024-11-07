<%-- 
    Document   : toolbar
    Created on : 21 ago 2024, 17:30:35
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang = "en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <aside class="options_sidebar visible" id="options_sidebar">
            <button id="sidebar_toggle"><img src="<%= request.getContextPath()%>/images/site_images/sidebar_toggle.png" onclick="toggleSidebar()"></button>     
            <div class="fe-box" id="viewOrders">
                <a href="${pageContext.request.contextPath}/GestioneOrdini" onclick="toggleView()"><img src="<%= request.getContextPath()%>/images/site_images/ordinidaspedire.png" alt="Visualizza Ordini Clienti">
                <h5>Visualizza Ordini Da Spedire</h5>
            </div>
            <div class="fe-box" id="viewSentOrders">
                <a href="${pageContext.request.contextPath}/GestioneOrdini"><img src="<%= request.getContextPath()%>/images/site_images/ordinispediti.png" alt="Visualizza Ordini Spediti">
                </a>
                <h5>Visualizza Ordini Spediti</h5>
            </div>
            <div class="fe-box" id="supplyingRequests">
                <a href="GestioneApprovigionamentiController?action=viewList&page=1"><img src="<%= request.getContextPath()%>/images/site_images/supplyrequests.png" alt="Richieste Approvvigionamento"></a>
                <h5>Richieste Approvvigionamento</h5>
            </div>
            <div class="fe-box" id="supplyingRequest" onclick="moveToSidebar('supplyingRequest', 'supplyingRequestForm')">
                <a href="GestioneApprovigionamentiController?action=viewProductList&page=1"><img src="<%= request.getContextPath()%>/images/site_images/approvvigionamento.png" alt="Approvvigionamento">
                </a>
                <h5>Approvvigionamento</h5>
            </div>
        </aside>
    </body>
</html>
