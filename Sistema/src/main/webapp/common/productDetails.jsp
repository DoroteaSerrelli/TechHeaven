<%-- 
    Document   : productDetails
    Created on : 31 ago 2024, 17:53:50
    Author     : raffa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
	import="java.util.List, 
				  java.util.Currency,
				  java.text.NumberFormat,
				  application.NavigazioneService.Prodotto"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1.0">
<title>TechHeaven - Dettagli prodotto</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/style/style.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/style/product_table.css">
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
	<div class="product-page">
		<div class="pro">
			<div class="product-image">
				<img src="image?productId=<%= product.getCodiceProdotto() %>"
					alt="Prodotto"
					onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/images/site_images/placeholder.png';" />
				<div class="product-gallery">
					<% if(galleryImages!=null && !galleryImages.isEmpty()){%>
					<div class="main-image">
						<!-- Display the first image as the main image -->
						<img id="currentImage" src="<%= galleryImages.get(0) %>"
							alt="<%= product.getNomeProdotto()%>" />
					</div>
					<div class="thumbnails">
						<!-- Loop through the galleryImages list to create thumbnails -->
						<%
                            for (int i = 0; i < galleryImages.size(); i++) {
                                String img = galleryImages.get(i);
                        %>
						<img src="<%= img %>" alt="<%= product.getNomeProdotto()%>"
							onclick="changeImage('<%= img %>')" />
						<%
                            }
                        }    
                        %>

						<%  %>
					</div>
				</div>
			</div>
			<div class="product-actions">
				<h2 class="prezzo"><%=prezzoFormattato%></h2>
				<a class="add-to-cart" href="#"
					onClick="modifyCart(<%=product.getCodiceProdotto()%>,'aggiungiAlCarrello')">
					<img class="cart"
					src="<%= request.getContextPath()%>/images/site_images/icon_carrello2.png">
				</a> <a class="add-to-wishlist"
					href="GestioneWishlistController?action=addtowishlist&productId=<%= product.getCodiceProdotto() %>">
					<img
					src="<%= request.getContextPath()%>/images/site_images/icon_wishlist.png"
					style="margin: 1px; width: 25px; height: 25px">
				</a>
			</div>
		</div>

	</div>
	<div class="errormsg">
		<p id="error" class="error"></p>
	</div>
	<div class="product-details">
		<h1><%=product.getNomeProdotto()%></h1>
		<span><strong><%=product.getMarca()%></strong></span>
		<h3><%=product.getTopDescrizione()%></h3>
		<h2><%=product.getDettagli()%></h2>
		<div class="star"></div>
	</div>

	<script>
            function changeImage(src) {
                document.getElementById('currentImage').src = src;
            }
            </script>
	<jsp:include page="/common/footer.jsp" flush="true" />
</body>
</html>
