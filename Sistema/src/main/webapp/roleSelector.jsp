<%-- 
    Document   : roleSelector
    Created on : 21-mar-2024, 17.48.29
    Author     : raffy
--%>

<%@page import="application.RegistrazioneService.ProxyUtente"%>
<%@page import="application.RegistrazioneService.Utente"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.RegistrazioneService.Ruolo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
    <meta charset="UTF-8">
    <title>Role Selector</title>
    <!-- Include the external JavaScript file using the context path -->
    <script src="<%= request.getContextPath() %>/scripts/roleFunctions.js?ts=<%= System.currentTimeMillis() %>"></script>
    </head>
    <body>
    <%
        if (request.getSession().getAttribute("user") == null) {
            out.println("");
        } else {
            ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
            ArrayList<Ruolo> ruoli = u.getRuoli();          
    %>

    <% if (ruoli != null && !ruoli.isEmpty()) { %>
        <form action="/RoleChanger" method="post">
            <select name="ruolo" onchange="redirectToRolePage(this)">
                <option value="" disabled selected>Please select a role</option>
                <% for (Ruolo ruolo : ruoli) { %>
                    <option value="<%= ruolo.getNomeRuolo() %>"><%= ruolo.getNomeRuolo() %></option>
                <% } %>
            </select>
        </form>
    <% } else { %>
        <p>No roles available.</p>
    <% } %>

    <% } %>
    </body>
</html>
