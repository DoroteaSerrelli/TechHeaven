<%-- 
    Document   : GestioneCatalogo
    Created on : 21-mar-2024, 17.36.51
    Author     : raffy
--%>

<%@page import="application.RegistrazioneService.Ruolo"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.RegistrazioneService.Utente"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="view/roleFunctions.js"></script>
    </head>    
   <body>
        <link rel="stylesheet" href="style.css">
       <jsp:include page="header.jsp"  flush="true"/>
       <jsp:include page="roleSelector.jsp"  flush="true"/>
       <section id="feature" class="section-p1">
            <div class="fe-box">
                <a href="/GestioneCatalogoController">               
                    <img src="view/img/listaprodotto.png" alt="alt">
                </a>    
                <h6>Visualizza Prodotti</h6>               
            </div>
             <div class="fe-box">
                <a href="/GestioneCatalogoController">                
                    <img src="view/img/addprodotto.png" alt="alt">
                </a>    
                <h6>Aggiungi un nuovo prodotto</h6>
                
            </div>
            <div class="fe-box">
                <a href="/GestioneCatalogoController">               
                    <img src="view/img/removeprodotto.png" alt="alt">
                </a>    
                <h6>Elimina un prodotto</h6>
                
            </div>           
            <div class="fe-box">
                <a href="/GestioneCatalogoController">
                    <img src="view/img/modproperties.png" alt="alt">
                </a>    
                <h6>Modifica caratteristiche prodotto</h6>                
            </div>           
        </section>
    </body>
</html>
