<%-- 
    Document   : error_preparazioneOrdine
    Created on : 17 ott 2024, 19:21:45
    Author     : raffa
--%>


<%@page import="application.Registrazione.RegistrazioneService.Cliente"%>
<%@page import="application.GestioneOrdini.GestioneOrdiniService.Ordine"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>TechHeaven - Errore Ordine</title>
         <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/style.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/extra_manager_style.css">
        <%
            Ordine  order = (Ordine)  request.getSession().getAttribute("selected_ordine");
            Cliente contact = order.getAcquirente();           
            %>
    </head>
    <body>
        <jsp:include page="/common/header.jsp"  flush="true"/>
           <div class="order_warning">
                <h1>Avviso</h1>
                <h2>Non è possibile soddisfare il numero di pezzi richiesti dal cliente. 
                    Si prega di contattare il cliente ai seguenti recapiti:</h2>
                <ul>
                    <li><p>Telefono: <%=contact.getTelefono()%></p></li>
                    <li><p>Email: <%=contact.getEmail()%></p></li>
                </ul>
                <h2>Per poter avviare eventualmente una delle seguenti opzioni;</h2> 
                <ul>
                    <li><p>Rimborso totale dell'ordine</p></li>
                    <li><p>Rimborso dei pezzi mancanti</p></li>
                    <li><p>Sospensione dell'ordine fino a disponibilità dei 
                            prodotti richiesti </p></li>
                </ul>
            </div>       
        <jsp:include page="/common/footer.jsp"  flush="true"/>
    </body>
</html>
