<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>TechHeaven - Pagamento non confermato</title>
<meta name="viewport" content="width:device-width, initial-scale = 1.0">
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
</head>
<body>
	<%@include file="../../common/header.jsp"%>
	<h2>Errore operazione pagamento</h2>
	<p>Il pagamento della merce non è stato confermato. In caso di
		pagamento con carta di credito o Paypal, ti invitiamo a verificare il
		tuo account bancario con la tua banca. In caso contario, contatta il
		nostro servizio clienti.</p>
	<%@include file="../../common/footer.jsp"%>
</body>
</html>