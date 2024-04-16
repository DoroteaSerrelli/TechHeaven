
<%@page import="application.GestioneCarrelloService.GestioneCarrelloServiceImpl"%>
<%@page import="application.NavigazioneService.Prodotto"%>
<%@page import="application.GestioneCarrelloService.Carrello"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html  lang="en">
    <head><title>Header</title>
        <!-- Basta includere uno script nella sezione di file in cui 
        viene usato e basta importarlo nel file contenente la sezione usata 
        senza doverlo importare da altre parti-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js" integrity="sha512-3gJwYpMe3QewGELv8k/BX9vcqhryRdzRMxVfq6ngyWXwo03GFEzjsUm8Q7RZcHPHksttq7/GFoxjCVUjkjvPdw==" crossorigin="anonymous"></script> 
    <script src="${pageContext.request.contextPath}/view/navi_script.js"></script>
    </head>    
    <script> 
     function hasClass(element, clsName) {
    return (' ' + element.className + ' ').indexOf(' ' + clsName + ' ') > -1;
    }
    $(document).ready(function(){
         $('#carrello').hide();         
         $('#lg-bag').click(function(){
          if (hasClass(document.getElementById('carrello'),"active")) {     
          document.getElementById('carrello').classList.remove("active");
          $('#carrello').hide();          
        }
        else
        { 
          document.getElementById('carrello').classList.add("active");  
          $('#carrello').show();                  
        }
    });       
    });
    </script>
       <link rel="stylesheet" href="${pageContext.request.contextPath}/common/style.css"> 
       <section id="header">
           <a href="#"> <img src="${pageContext.request.contextPath}\view\img\logo.png" height="100" width="100" class="logo" alt="alt"/></a>
        <div>              
            <ul id="navbar">
                <li><a class="active" href="${pageContext.request.contextPath}/index.jsp">Home</a></li> 
                <li><a href="Shop">Shop</a></li> 
                <li><a href="${pageContext.request.contextPath}/Autenticazione.jsp">Login</a></li> 
                <li><a href="${pageContext.request.contextPath}/Registrazione.jsp">Register</a></li> 
                <li><a href="${pageContext.request.contextPath}/AreaRiservata.jsp"><img src="${pageContext.request.contextPath}/view/img/icon_user.png" height="30" width="30"></a></li>
                
                <li>
                    <form method="post" action="NavigazioneController?search_type=bar"><input type="text" name="keyword">
                        <button><input type="submit"></button></form>
                </li>
                
                <li><a id="lg-bag">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                        <path fill="#2DA0F2" d="M16 6h-1V5c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v1H8c-1.1 0-1.99.9-1.99 2L6 19c0 1.1.89 2 1.99 2H17c1.1 0 2-.9 2-2l.01-11c0-1.1-.89-2-1.99-2zM9 5h6v1H9V5zm8 14H7V9h10v10z"/>
                        <path fill="none" d="M0 0h24v24H0V0z"/>
                    </svg>
                    </a>
                </li>
                <a href="#" id="close" onClick="closeSidebar()">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                        <path fill="#2DA0F2" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z"/>
                    </svg>                
                </a>                     
            </ul>  
            <ul id="categories">            
                <li><a href="#">Telefonia</a></li> 
                <li><a href="#">TV e Video</a></li> 
                <li><a href="#">Informatica</a></li>
                <li><a href="#">Piccoli Elettrodomestici</a></li>
           </ul>   
        </div>
         <div id="carrello" > 
             <a href="cart.jsp">Checkout</a>
             <br>
                <%
                    if(request.getSession().getAttribute("usercart")==null);
                    else{
                        Carrello cart = (Carrello)request.getSession().getAttribute("usercart"); 
                        GestioneCarrelloServiceImpl gestCart = new GestioneCarrelloServiceImpl();                       
                        for(Prodotto p: gestCart.visualizzaCarrello(cart)){            
                %>
                <%=p.toString()%>
               <% }}%>
        </div>  
        <div id="mobile">
            <a href="/GestioneCarrello">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                    <path fill="#2DA0F2" d="M16 6h-1V5c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v1H8c-1.1 0-1.99.9-1.99 2L6 19c0 1.1.89 2 1.99 2H17c1.1 0 2-.9 2-2l.01-11c0-1.1-.89-2-1.99-2zM9 5h6v1H9V5zm8 14H7V9h10v10z"/>
                    <path fill="none" d="M0 0h24v24H0V0z"/>
                </svg>
            </a>
            <button class="openbtn" onclick="openSidebar()">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                <path fill="#2DA0F2" d="M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z"/>
            </button>         
        </div>
        <br>         
        </section>       
</html>