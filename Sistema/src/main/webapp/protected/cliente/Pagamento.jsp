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
         <div class="errormsg">        
                <% 
                String err = (String)request.getSession().getAttribute("error");
                if (err != null && !err.isEmpty()) {
                %>
              <p id="error">  <%=err%></p>
                <% }
                request.getSession().removeAttribute("error");
                    %>
            </div>   
        <h1>Seleziona Metodo di Pagamento:</h1>
       <form action="<%=request.getContextPath()%>/PagamentoController" method="POST" onsubmit="return validatePaymentForm()">
           <input type="hidden" name="action" value="confirmPayment">
            <div class="payment_methods" onchange="toggleCreditCardFields()">
                <p>
                    <input type="radio" name="metodoPagamento" value="Carta_credito" id="creditCardRadio">
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
                    <input type="text" id="titolare" name="titolare" oninput="validateTitolare(this.value)">
                </p>
                    <div id="errorTitolare"></div>
                <p>
                    <label for="cc_number">Numero Carta di Credito</label>
                    <input type="text" id="cc_number" name="cc_number" oninput="validateCCNumber(this.value)">
                </p>
                    <div id="errorcc_number"></div>
                <p>
                    <label for="cc_expiry">Data di Scadenza</label>
                    <input type="text" id="cc_expiry" name="cc_expiry">
                </p>              
                <p>
                    <label for="cc_cvc">CVC</label>
                    <input type="text" id="cc_cvc" name="cc_cvc" oninput="validateCVV(this.value)">
                </p>
                    <div id="errorcc_cvc"></div>
            </div>
            <input value="Conferma Pagamento" type="submit" class="confirm_button" name="submit">
            <button class="delete_button"><a href="/PagamentoController?action=annullaPagamento">Annulla Pagamento</a></button>               
       </form>
    </div>         
    </body>
</html>
