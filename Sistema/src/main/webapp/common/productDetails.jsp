<%-- 
    Document   : productDetails
    Created on : 31 ago 2024, 17:53:50
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
	import="java.util.List, 
				  java.util.Currency,
				  java.text.NumberFormat,application.Navigazione.NavigazioneService.Prodotto"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1.0">
<title>TechHeaven - Dettagli prodotto</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/style/style.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/style/product_details.css">
<jsp:include page="/common/header.jsp" flush="true" />
<%
           Prodotto product = (Prodotto)request.getAttribute("product");
           if(product==null){
                response.sendRedirect(request.getContextPath() + "/index");
                return;
           }         
           List<String> galleryImages = (List<String>)request.getAttribute("galleryImages");          
       %>
<%
            double prezzo = product.getPrezzo();
            // Optionally, specify a particular currency
             // Create a NumberFormat instance for currency formatting
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

            // Optionally, specify a particular currency
            currencyFormatter.setCurrency(Currency.getInstance("EUR")); // Use "USD" for US dollars, "EUR" for Euros, etc.

            String prezzoFormattato = currencyFormatter.format(prezzo);
        %>
        
</head>
<body>
	<div class="errormsg">
		<p id="error" class="error"></p>
	</div>
	<div class="product-page">
		<!-- Product image and gallery section -->
		<div class="pro">
			<div class="product-gallery">
				<% if(galleryImages != null && !galleryImages.isEmpty()) { %>
				<div class="main-image">
					<!-- Display the first image as the main image -->
					<img id="currentImage" src="<%= galleryImages.get(0) %>"
						alt="<%= product.getNomeProdotto() %>" />
				</div>
				<div class="thumbnails">
					<!-- Loop through the galleryImages list to create thumbnails -->
					<%
                        for (int i = 0; i < galleryImages.size(); i++) {
                            String img = galleryImages.get(i);
                        %>
					<img class="gallery-thumbnails" src="<%= img %>"
						alt="<%= product.getNomeProdotto() %>"
						onclick="changeImage('<%= img %>', this)" />
					<%
                        } 
                        %>
				</div>
				<% } %>
			</div>
		</div>

		<!-- Product actions (Add to Cart, Wishlist) section on the side -->
		<div class="product-actions">
			<h2 class="prezzo">Prezzo : <%= prezzoFormattato %></h2>

			<% if(product.getQuantita() > 0) {%>
			<h2 class="suggerimenti">Disponibilità pezzi : <%= product.getQuantita() %></h2>
			
			<a href="#"
				onClick="addToCartAndRedirect(<%= product.getCodiceProdotto() %>, 'addToCart', '<%= request.getContextPath() %>/cart');">
				<img class="cart"
				style = "weight: 50px; height:50px;"
				src="<%=request.getContextPath()%>/images/site_images/icon_carrello2.png" id="iconcart"
					onmouseout="document.getElementById('iconcart').src='<%= request.getContextPath()%>/images/site_images/icon_carrello2.png'"
					onmouseover="document.getElementById('iconcart').src='<%= request.getContextPath()%>/images/site_images/icon_carrello.png'" alt = "Aggiungi al carrello">
			</a>
			<%}else{ %>
			<p>Non è disponibile il prodotto per l'acquisto.</p>
			<%} %>
			
			<a class="add-to-wishlist"
				href="GestioneWishlistController?action=addtowishlist&productId=<%= product.getCodiceProdotto() %>">
				<img
				src="<%= request.getContextPath() %>/images/site_images/icon_wishlist.png" id="iconwishlist"
				style="margin: 1px;" alt="Aggiungi alla Wishlist" onmouseout="document.getElementById('iconwishlist').src='<%= request.getContextPath()%>/images/site_images/icon_wishlist.png'"
					onmouseover="document.getElementById('iconwishlist').src='<%= request.getContextPath()%>/images/site_images/icon_wishlist2.png'">
			</a>
		</div>
	</div>

	<!-- Product details below -->
	
	<h1>Specifiche</h1>
	<div class="product-details">
		
		<h3>Nome : <h3 class = "specifica"><%= product.getNomeProdotto() %> </h3> </h3>
		<h3>Marca : <h3 class = "specifica"><%= product.getMarca() %></h3></h3>
		<h3>Modello : <h3 class = "specifica"><%= product.getModello() %></h3></h3>
		<br>
		<h3>Descrizione</h3>
		
		<p class = "specifica"><b><%= product.getTopDescrizione().replaceAll("\n", "<br>") %></b></p>
		<br>
		<hr>
		<h3>Dettagli</h3>
		<p class = "specifica"><b><%= product.getDettagli().replaceAll("\n", "<br>") %></b></p>
		<div class="star"></div>
	</div>
	<script>
             function changeImage(imageSrc, imgElement) {
                    const currentImage = document.getElementById('currentImage');
                    const thumbnails = document.querySelectorAll('.gallery-thumbnails');

                    // Update the main image's source
                    currentImage.src = imageSrc;

                    // Remove 'selected' class from all thumbnails
                    thumbnails.forEach(thumbnail => thumbnail.classList.remove('selected'));

                    // Add 'selected' class to the clicked thumbnail
                    if (imgElement && imgElement.classList) {
                        imgElement.classList.add('selected');
                    }
                }
        </script>
	<jsp:include page="/common/footer.jsp" flush="true" />
</body>
</html>
