<%-- 
    Document   : Dettagli_Prodotto
    Created on : 9-mar-2024, 15.32.03
    Author     : raffy
--%>

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
        <h1>Product Detail</h1>
        <c:if test="${not empty Prodotto}">
            <h2>${Prodotto.name}</h2>
            <p>Description: ${Prodotto.description}</p>
            <p>Price: ${Prodotto.price}</p>
        </c:if>
        <c:if test="${empty Prodotto}">
            <p>Product not found</p>
        </c:if>
    </body>
</html>
