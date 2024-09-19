<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>TechHeaven - Pagamento confermato</title>
<meta name="viewport" content="width:device-width, initial-scale = 1.0">
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
</head>
<body>
	<%@include file="../../common/header.jsp"%>
	<h2>Pagamento confermato</h2>
	<p>Il pagamento è stato confermato con successo. Grazie per averci
		scelto. Controlla nella tua area riservata lo stato di avanzamento del
		tuo ordine.</p>
	<%@include file="../../common/footer.jsp"%>
</body>
</html>