<%@page import="model.Prodotto"%>
<%@page import="model.GestioneCarrello"%>
<%@page import="model.Carrello"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html  lang="en">
    <head><title>Header</title>
        <!-- Basta includere uno script nella sezione di file in cui 
        viene usato e basta importarlo nel file contenente la sezione usata 
        senza doverlo importare da altre parti-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js" integrity="sha512-3gJwYpMe3QewGELv8k/BX9vcqhryRdzRMxVfq6ngyWXwo03GFEzjsUm8Q7RZcHPHksttq7/GFoxjCVUjkjvPdw==" crossorigin="anonymous"></script> 
    <script src="https://kit.fontawesome.com/207052c3a9.js" integrity="sha512-BgwIN3PpXLkbg6HyWOm0LO0m1sBZr6gEHLStmyYQ+3WtPcbEJkhC5lH1iISIYI0pWi+L6snpMjPQ99mrWPagew==" crossorigin="anonymous"></script>  
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
       <link rel="stylesheet" href="style.css"> 
       <section id="header">
           <a href="#"> <img src="view\img\logo.png" height="100" width="100" class="logo" alt="alt"/></a>
        <div>              
            <ul id="navbar">
                <li><a class="active" href="index.jsp">Home</a></li> 
                <li><a href="Shop">Shop</a></li> 
                <li><a href="login.jsp">Login</a></li> 
                <li><a href="Registrazione.jsp">Register</a></li> 
                <li><a href="account.jsp"><i class="fa-solid fa-user-gear" style="color: #CCCC66;"></i></a></li>
                <li><a id="lg-bag"><img src="view\img\cart-shopping-solid.svg" height="20" width="20" alt="alt"/></a>
                </li>
                <a href="#" id="close"><img src="view/img/xmark-solid.svg"></a>
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
                        GestioneCarrello gestCart = new GestioneCarrello();
                        
                        for(Prodotto p: gestCart.visualizzaCarrello(cart)){            
                %>
                <%=p.toString()%>
               <% }}%>
        </div>  
        <div id="mobile">
        <a href="cart.jsp"><img src="img\2.png" height="60" width="60" alt="alt"/></a>    
        <img id="bar" src="view/img/outdent-solid.svg"><script src="view/script.js"></script>            
        </div>
        <br>         
        </section>       
</html>