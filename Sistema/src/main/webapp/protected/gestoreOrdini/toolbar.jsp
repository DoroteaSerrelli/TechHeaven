<%-- 
    Document   : toolbar
    Created on : 21 ago 2024, 17:30:35
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <aside class="options_sidebar visible" id="options_sidebar">
            <button id="sidebar_toggle"><img src="${pageContext.request.contextPath}/view/img/sidebar_toggle.png" onclick="toggleSidebar()"></button>     
            <div class="fe-box" id="viewOrders">
                <a href="${pageContext.request.contextPath}/GestioneOrdini"><img src="${pageContext.request.contextPath}/view/img/ordinidaspedire.png" alt="Visualizza Ordini Clienti">
                <h5>Visualizza Ordini Da Spedire</h5>
            </div>
            <div class="fe-box" id="viewSentOrders">
                <a href="${pageContext.request.contextPath}/GestioneOrdini"><img src="${pageContext.request.contextPath}/view/img/ordinispediti.png" alt="Visualizza Ordini Spediti">
                </a>
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
        </aside>
    </body>
</html>
