<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="ISO-8859-1">
<title>TechHeaven - Pagina errore</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/style/style.css">
</head>
<body>
	<jsp:include page="/common/header.jsp" flush="true" />
	<h1>Pagina di errore</h1>
	<br>
	<br>
	<h3>
		Si è verificato un problema: <br /> <br> <br> <span
			style="color: red;">${sessionScope.errorMessage}</span>
	</h3>

	<jsp:include page="/common/footer.jsp" flush="true" />
</body>
</html>