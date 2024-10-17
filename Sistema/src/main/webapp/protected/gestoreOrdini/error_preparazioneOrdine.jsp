<%-- 
    Document   : error_preparazioneOrdine
    Created on : 17 ott 2024, 19:21:45
    Author     : raffa
--%>


<%@page import="application.RegistrazioneService.Cliente"%>
<%@page import="application.GestioneOrdiniService.Ordine"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>TechHeaven - Errore Ordine</title>
         <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/style.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/catalog_options.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/style/product_table.css">
        <%
            Ordine  order = (Ordine)  request.getSession().getAttribute("selected_ordine");
            Cliente contact = order.getAcquirente();           
            %>
    </head>
    <body>
        <jsp:include page="/common/header.jsp"  flush="true"/>
            <div>
                <h1>Avviso</h1>
                <h2>Non è possibile soddisfare il numero di pezzi richiesti dal cliente. 
                    Si prega di contattare il cliente ai seguenti recapiti</h2>
                <ul><p>Telefono: <%=contact.getTelefono()%></p>
                    <p>Email: <%=contact.getEmail()%></p></ul>
            </div>       
        <jsp:include page="/common/footer.jsp"  flush="true"/>
    </body>
</html>
