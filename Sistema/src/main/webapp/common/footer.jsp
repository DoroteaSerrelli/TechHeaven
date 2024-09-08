<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Footer</title>
    </head>
    <body>
    <footer class="section-p1">
       <div class="col">
           <img class="logo" src="<%= request.getContextPath()%>/images/site_images/logo.png" height="100" width="100" alt="alt">
	<h4>Contatti</h4>
	<p><strong>Indirizzo:</strong> Via Giovanni Paolo II 00, 84084 Fisciano (SA)</p>
	<p><strong>Cellulare:</strong> +39 123 456 7890</p>					
	<p><strong>Orari di apertura:</strong> 10:00 - 18:00, Lun - Sab </p>
	<div class="follow">
            <h4>Seguici su</h4>
            <div class="icon" id="icons">
                 <div class="icon">
                    <img class="static-image" src="<%= request.getContextPath()%>/images/site_images/logos/facebook.png">
                    <img class="gif-image" src="<%= request.getContextPath()%>/images/site_images/logos/facebook_animated.gif">
                </div>
                <div class="icon">
                    <img class="static-image" src="<%= request.getContextPath()%>/images/site_images/logos/twitter.png">
                    <img class="gif-image" src="<%= request.getContextPath()%>/images/site_images/logos/twitter_animated.gif">
                </div>
                <div class="icon">
                    <img class="static-image" src="<%= request.getContextPath()%>/images/site_images/logos/instagram.png">
                    <img class="gif-image" src="<%= request.getContextPath()%>/images/site_images/logos/instagram_animated.gif">
                </div>
                <div class="icon">
                    <img class="static-image" src="<%= request.getContextPath()%>/images/site_images/logos/youtube.png">
                    <img class="gif-image" src="<%= request.getContextPath()%>/images/site_images/logos/youtube_animated.gif">
                </div>             
            </div>
	</div>	
            </div>
            <div class="col"> 
		<h4>F.A.Q.</h4>
		<a href="${pageContext.request.contextPath}/about.jsp">Chi siamo</a>
		<a href="#">Informazioni di spedizione</a>
		<a href="#">Privacy Policy</a>
		<a href="#">Termini & Condizioni</a>				
		<a href="#">Servizio Clienti</a>
            </div>				
            <div class="col"> 
                <h4>My Account</h4>
                <a href="#">Sign In</a>
		<a href="#">View Cart</a>
		<a href="#">My Wishlist</a>
		<a href="#">Track My Order</a>				
		<a href="#">Help</a>
            </div>				
	    <div class="col install">
		
                <p>Gateway di pagamento accettati </p>
                <div class="row">               
                    <img src="<%= request.getContextPath()%>/images/site_images/logos/master_card.png" alt="MasterCard">
                    <img src="<%= request.getContextPath()%>/images/site_images/logos/visa.png" alt="Visa">
                    <img src="<%= request.getContextPath()%>/images/site_images/logos/maestro.png" alt="Maestro">	
                </div>
	    </div>
            <div class="copyright">
                <p>&copy 2024, TechHeavenSRL etc - Website TechHeaven</p>
            </div>
	</footer>
        <script src="${pageContext.request.contextPath}/view/gif_observer.js"></script>
    </body>
</html>
