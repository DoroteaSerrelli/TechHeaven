<%-- 
    Document   : Unathourized
    Created on : 12 ago 2024, 17:48:14
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TechHeaven - Accesso negato</title>
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/style/style.css">
</head>
<body>
	<jsp:include page="<%=request.getContextPath() %>/common/header.jsp"
		flush="true" />
	<div class="section-p1" style="padding-bottom: 20%;">
		<h1>403 - Non autorizzato</h1>
		<p>Spiacente, non ha i permessi necessari per accedere a questa
			pagina.</p>
		<p>Se credi che questo sia un errore, perfavore contatta
			l'amministratore di sistema.</p>
		<p>
			<a href="<%= request.getContextPath()%>/">Ritorna alla pagina
				iniziale</a> oppure <a href="mailto:support@example.com">Contattaci
				per aiuto</a>
		</p>
	</div>
	<jsp:include page="<%=request.getContextPath() %>/common/footer.jsp"
		flush="true" />
</body>
</html>
