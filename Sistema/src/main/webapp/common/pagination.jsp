<%-- 
    Document   : pagination
    Created on : 23 ago 2024, 17:38:50
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>JSP Page</title>
<%int pagen = (int) request.getAttribute("page");%>
</head>
<body>
	<div id="pagination">
		<%// if (totalPages > 1) { %>
		<%// for (int pager = 1; pager <= totalPages; pager++) { %>
		<%
                        int previous_page = pagen-1;
                        int next_page = pagen+1;
                        
                        String prevpageUrl = "GestioneApprovigionamentiController?page=" + previous_page + "&action=viewProductList";
                        String nextpageUrl = "GestioneApprovigionamentiController?page=" + next_page + "&action=viewProductList";        
                    %>
		<% if(pagen>1){%>
		<h2>
			Pagina Precedente:
			<%=previous_page%></h2>
		<a href="<%= prevpageUrl %>"><img
			src="<%= request.getContextPath()%>/images/site_images/arrow_back.png" alt = "ArrowBack"></a>
		<%}%>
		<% if ((boolean) request.getAttribute("hasNextPage")) { %>
		<h2>
			Pagina Successiva:
			<%=next_page%></h2>
		<a href="<%= nextpageUrl %>"><img
			src="<%= request.getContextPath()%>/images/site_images/arrow_forward.png" alt = "ArrowForward"></a>
		<% } else { %>
		<img
			src="<%= request.getContextPath()%>/images/site_images/arrow_forward_disabled.png" alt = "ArrowDisabled">
		<% } %>
		<%  %>
		<%  %>
	</div>
</body>
</html>
