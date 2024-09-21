<%-- 
    Document   : Pagamento
    Created on : 21 set 2024, 15:59:50
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta charset="UTF-8">
        <link rel="stylesheet"
                href="<%= request.getContextPath()%>/style/style.css">
        <link rel="stylesheet"
                href="<%= request.getContextPath()%>/style/cart.css">
        <title>TechHeaven - Completa l'acquisto</title>    
        <jsp:include page="common/header.jsp"
		flush="true" />
        <script src="<%= request.getContextPath()%>/scripts/paymemt_scripts.js"></script>
    </head>
    </head>
    <body>
    <div class="section-p1">
        <h1>Seleziona Metodo di Pagamento:</h1>
       <form action="/CheckoutCarrello" method="POST" onsubmit="return validatePaymentForm()">
           <input type="hidden" name="action" value="confirmPayment">
            <div class="payment_methods" onchange="toggleCreditCardFields()">
                <p>
                    <input type="radio" name="metodoPagamento" value="CreditCard" id="creditCardRadio">
                    Carta di Credito 
                </p>
                <p>
                    <input type="radio" name="metodoPagamento" value="Paypal" id="paypalRadio">
                    Paypal
                </p>
                <p>
                    <input type="radio" name="metodoPagamento" value="Contrassegno" id="contrassegnoRadio">
                    Contrassegno
                </p>
                
                
            </div>        
            <!-- Hidden fields for Credit Card payment -->
            <div id="creditCardFields" style="display: none;">
                <p>
                    <label for="titolare">Titolare</label>
                    <input type="text" id="titolare" name="titolare">
                </p>
                <p>
                    <label for="cc_number">Numero Carta di Credito</label>
                    <input type="text" id="cc_number" name="cc_number">
                </p>
                <p>
                    <label for="cc_expiry">Data di Scadenza</label>
                    <input type="text" id="cc_expiry" name="cc_expiry">
                </p>
                <p>
                    <label for="cc_cvc">CVC</label>
                    <input type="text" id="cc_cvc" name="cc_cvc">
                </p>
            </div>
            <button class="delete_button"><a href="/CheckoutCarrello?action=annullaPagamento">Annulla Pagamento</a></button>               
       </form>
    </div>         
    </body>
</html>
