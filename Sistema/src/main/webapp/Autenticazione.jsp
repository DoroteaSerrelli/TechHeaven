<%-- 
    Document   : Autenticazione
    Created on : 11-mar-2024, 13.13.55
    Author     : raffy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
       <title>Autenticazione</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <link rel="stylesheet" href="common/style.css">
        <jsp:include page="common/header.jsp"  flush="true"/>
        <div class="section-p1">
            <div class="login-container">
                <form name="client" method="post" action="AutenticazioneController">           
                <div class="row">
                    <div class="input-wrapper">
                        <p>  *Username: </p>  
                        <input type="text" name="username" required>
                    </div>
                </div>
                <div class="row">
                    <div class="input-wrapper">
                        <p> *Password: </p>
                        <input type="password" name="password" required>
                    </div>
                </div>  
                <div class="row">     
                     <div class="input-wrapper">             
                        <p>  *Role: </p>
                        <select name="ruolo" required>
                            <option value="Cliente">Cliente</option>
                            <option value="GestoreOrdini">Gestore Ordini</option>
                            <option value="GestoreCatalogo">Gestore Catalogo</option>
                        </select>    
                     </div> 
                </div>  
                    <div class="row">
                        <div class="input-wrapper"> 
                        <input value="Submit" type="submit" class="confirm_button" name="submit" onclick="return validate()">                
                        </div>
                        <div class="input-wrapper">
                            <button class="confirm_button"><a href="resetPassword">Reimposta password</a></button>                           
                        </div>
                    </div>    
                <div class="errormsg">                   
                 <% 
                    String err = (String)request.getSession().getAttribute("error");
                    if (err != null && !err.isEmpty()) {
                 %>
                 <p id="error" class="error invalid"><%=err%></p>               
                <% request.getSession().removeAttribute("error");
                    } %>
            </div> 
                </form>
            </div>    
         </div>
            <jsp:include page="common/footer.jsp"  flush="false"/>  
    </body>      
</html>
