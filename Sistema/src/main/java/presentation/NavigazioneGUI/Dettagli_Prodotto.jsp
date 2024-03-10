<%-- 
    Document   : Dettagli_Prodotto
    Created on : 9-mar-2024, 15.32.03
    Author     : raffy
--%>

<%@page import="model.Prodotto"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Product Details</title>
    </head>
    <body>
         <link rel="stylesheet" href="style.css">
        <jsp:include page="header.jsp"  flush="true"/>
        <h2>Product Detail</h2>
        <%
            if (request.getAttribute("prodotto") == null) {
                out.println("Prodotto non trovato");
            } else {
            // If product is not null, retrieve it from the session
            Prodotto prod = (Prodotto) request.getAttribute("prodotto");
            // Now you can print the properties of the product
        %>
            <h2>Nome: <%= prod.getNome()%></h2>
            <p>Description: <%= prod.getTop_descrizione()%></p>
            <p>Price: <%= prod.getPrezzo()%></p>
           <%
              }
           %> 
    </body>
</html>
