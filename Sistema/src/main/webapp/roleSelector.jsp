<%-- 
    Document   : roleSelector
    Created on : 21-mar-2024, 17.48.29
    Author     : raffy
--%>

<%@page import="application.RegistrazioneService.Utente"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.RegistrazioneService.Ruolo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%
        if (request.getSession().getAttribute("user") == null) {
            out.println("");
        } else {
            Utente u = (Utente) request.getSession().getAttribute("user");
            ArrayList<Ruolo> ruoli = u.getRuoli();
            boolean checkAllowed = false;
             String requiredRole = (String) request.getAttribute("requiredRole");
                for(Ruolo r : ruoli){
                %>
                <% if (r.getNomeRuolo().equals(requiredRole)) {
                        checkAllowed = true;
                        break;
                    }
                }    
            if(!checkAllowed){
                response.sendRedirect("Autenticazione.jsp");
                 return; // Stop further execution
            }
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
</html>
