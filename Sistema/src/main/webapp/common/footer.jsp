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
           <img class="logo" src="${pageContext.request.contextPath}/view/img/logo.png" height="100" width="100" alt="alt">
	<h4>Contact</h4>
	<p><strong>Address:</strong> via qualcosa n° 56, San Francisco</p>
	<p><strong>Phone:</strong> +01 2222 365/(91) 01 5432 6987</p>					
	<p><strong>Hours:</strong> 10:00 - 18:00, Mon - Sat </p>
	<div class="follow">
            <h4>Follow us</h4>
            <div class="icon" id="icons">
                 <div class="icon">
                    <img class="static-image" src="${pageContext.request.contextPath}/view/img/logos/facebook.png">
                    <img class="gif-image" src="${pageContext.request.contextPath}/view/img/logos/facebook_animated.gif">
                </div>
                <div class="icon">
                    <img class="static-image" src="${pageContext.request.contextPath}/view/img/logos/twitter.png">
                    <img class="gif-image" src="${pageContext.request.contextPath}/view/img/logos/twitter_animated.gif">
                </div>
                <div class="icon">
                    <img class="static-image" src="${pageContext.request.contextPath}/view/img/logos/instagram.png">
                    <img class="gif-image" src="${pageContext.request.contextPath}/view/img/logos/instagram_animated.gif">
                </div>
                <div class="icon">
                    <img class="static-image" src="${pageContext.request.contextPath}/view/img/logos/youtube.png">
                    <img class="gif-image" src="${pageContext.request.contextPath}/view/img/logos/youtube_animated.gif">
                </div>             
            </div>
	</div>	
            </div>
            <div class="col"> 
		<h4>About</h4>
		<a href="${pageContext.request.contextPath}/about.jsp">About us</a>
		<a href="#">Delivery Information</a>
		<a href="#">Privacy Policy</a>
		<a href="#">Terms & Conditions</a>				
		<a href="#">Contact Us</a>
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
		<h4> Install App </h4>
		<p>From App Store or Google Play</p>
		<div class="row">
                    <img src="${pageContext.request.contextPath}/view/img/logos/app store.png" width="40" height="40" alt="alt">
                    <img src="${pageContext.request.contextPath}/view/img/logos/google play store.png" width="40" height="40" alt="alt">
            	</div>
                <p>Secure Payment Gateways </p>
                <div class="row">               
                    <img src="${pageContext.request.contextPath}/view/img/logos/master card.png" alt="alt">
                    <img src="${pageContext.request.contextPath}/view/img/logos/visa.png" alt="alt">
                    <img src="${pageContext.request.contextPath}/view/img/logos/maestro.png" alt="alt">	
                </div>
	    </div>
            <div class="copyright">
                <p>&copy 2024, TechHeavenSRL etc - Website TechHeaven</p>
            </div>
	</footer>
        <script src="${pageContext.request.contextPath}/view/gif_observer.js"></script>
    </body>
</html>
