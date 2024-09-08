<%-- 
    Document   : pagination_research
    Created on : 23 ago 2024, 18:35:50
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
                        String searchType = (String)request.getSession().getAttribute("search_type");
                        String keyword = (String) request.getSession().getAttribute("keyword");
                        int previous_page = pagen-1;
                        int next_page = pagen+1;

                        String contextPath = request.getContextPath();
                        String prevpageUrl = contextPath + "/NavigazioneController?page=" + previous_page + "&search_type=" + searchType + "&keyword=" + keyword;
                        String nextpageUrl = contextPath + "/NavigazioneController?page=" + next_page + "&search_type=" + searchType + "&keyword=" + keyword;

                    %>
                    <% if(pagen>1){%>
                        <h2>Pagina Precedente: <%=previous_page%></h2>  
                        <a href="<%= prevpageUrl %>"><img src="<%= request.getContextPath()%>/images/site_images/arrow_back.png"></a>
                    <%}%>    
                    <% if ((boolean) request.getAttribute("hasNextPage")) { %>
                        <h2>Pagina Successiva: <%=next_page%></h2>
                        <a href="<%= nextpageUrl %>"><img src="<%= request.getContextPath()%>/images/site_images/arrow_forward.png"></a>
                    <% } else { %>
                        <img src="<%= request.getContextPath()%>/images/site_images/arrow_forward_disabled.png">
                    <% } %>
                <%  %>
            <%  %>
        </div>
    </body>
</html>
