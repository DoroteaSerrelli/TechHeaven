<%-- 
    Document   : pagination
    Created on : 23 ago 2024, 17:38:50
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
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
                        <h2>Pagina Precedente: <%=previous_page%></h2>  
                        <a href="<%= prevpageUrl %>"><img src="${pageContext.request.contextPath}/view/img/arrow_back.png"></a>
                    <%}%>    
                    <% if ((boolean) request.getAttribute("hasNextPage")) { %>
                        <h2>Pagina Successiva: <%=next_page%></h2>
                        <a href="<%= nextpageUrl %>"><img src="${pageContext.request.contextPath}/view/img/arrow_forward.png"></a>
                    <% } else { %>
                        <img src="${pageContext.request.contextPath}/view/img/arrow_forward_disabled.png">
                    <% } %>
                <%  %>
            <%  %>
        </div>
    </body>
</html>
