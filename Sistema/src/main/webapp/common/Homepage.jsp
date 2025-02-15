<%@page contentType="text/html" pageEncoding="UTF-8"
	import="java.util.Currency,
				  java.text.NumberFormat,application.Navigazione.NavigazioneService.ProxyProdotto,
				  java.util.Collection"%>
<%
Collection<ProxyProdotto> telefoni = (Collection<ProxyProdotto>) application.getAttribute("telefoni");
Collection<ProxyProdotto> gr_elettr = (Collection<ProxyProdotto>) application.getAttribute("gr_elettr");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>TechHeaven - Homepage</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/style/style.css">
</head>
<body>
	<jsp:include page="/common/header.jsp" flush="true" />
	<section id="hero"></section>
	<section id="feature" class="section-p1">
		<div class="fe-box">
			<img
				src="<%=request.getContextPath()%>/images/site_images/freeshipping.png"
				alt="alt">
			<h6>Free Shipping</h6>

		</div>
		<div class="fe-box">
			<img
				src="<%=request.getContextPath()%>/images/site_images/spedizionionline.png"
				alt="alt">
			<h6>Online Order</h6>

		</div>
		<div class="fe-box">
			<img
				src="<%=request.getContextPath()%>/images/site_images/savemoneyups.png"
				alt="alt">
			<h6>Save Money</h6>

		</div>
		<div class="fe-box">
			<img
				src="<%=request.getContextPath()%>/images/site_images/promozioni.png"
				alt="alt">
			<h6>Promotions</h6>
		</div>
		<div class="fe-box">
			<img
				src="<%=request.getContextPath()%>/images/site_images/supportoh24.png"
				alt="alt">
			<h6>F24/7 Support</h6>
		</div>
	</section>

	<section id="product1" class="section-p1">
		<h2>Featured Product</h2>
		<p>Some of our featured products include:</p>
		<div class="pro-container">
			<%
			if (telefoni != null && !telefoni.isEmpty()) {
			%>
			<%
			for (ProxyProdotto product : telefoni) {
				if (product.isInVetrina()) {
			%>
			<div class="pro">
				<img src="image?productId=<%=product.getCodiceProdotto()%>"
					alt="alt"
					onerror="this.onerror=null;this.src='<%=request.getContextPath()%>/images/site_images/placeholder.png';"
					style="width: 80px; height: 80px" />
				<div class="des">
					<span><%=product.getMarca()%></span>
					<h5 id="pr_name"><%=product.getNomeProdotto()%></h5>
					<%
					double prezzo = product.getPrezzo();
					NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
					currencyFormatter.setCurrency(Currency.getInstance("EUR"));
					String prezzoFormattato = currencyFormatter.format(prezzo);
					%>

					<h4 class="prezzo"><%=prezzoFormattato%></h4>
				</div>
				<%if(product.getQuantita() > 0){ %>
				<a href="#"
					onClick="addToCartAndRedirect(<%= product.getCodiceProdotto() %>, 'addToCart', '<%= request.getContextPath() %>/cart');">
					<img class="cart"
					src="<%=request.getContextPath()%>/images/site_images/icon_carrello2.png">
				</a>
				<%} %>
				<a
					href="GestioneWishlistController?action=addtowishlist&productId=<%= product.getCodiceProdotto() %>">
					<img class="wishlist"
					src="<%= request.getContextPath()%>/images/site_images/icon_wishlist.png"
					alt="wishlist">
				</a>
			</div>
			<%
			}
			}
			}
			%>
		</div>
	</section>
	<section id="banner">
		<!--<h4>Non Restare Indietro!</h4>
		<h2>
			Vieni A Scoprire I <span>Nostri</span> Smartphone!
		</h2>-->
		<button>Explore More</button>
	</section>
	<section id="product1" class="section-p1">
		<h2>Summer Arrival</h2>
		<p>Keep up with our summer products:</p>
		<div class="pro-container">
			<%
			if (gr_elettr != null && !gr_elettr.isEmpty()) {
			%>
			<%
			for (ProxyProdotto product : gr_elettr) {
			%>
			<div class="pro">
				<img src="image?productId=<%=product.getCodiceProdotto()%>"
					alt="alt"
					onerror="this.onerror=null;this.src='<%=request.getContextPath()%>/images/site_images/placeholder.png';"
					style="width: 80px; height: 80px" />
				<div class="des">
					<span><%=product.getMarca()%></span>
					<h5 id="pr_name"><%=product.getNomeProdotto()%></h5>
					<%
					double prezzo = product.getPrezzo();
					NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
					currencyFormatter.setCurrency(Currency.getInstance("EUR"));
					String prezzoFormattato = currencyFormatter.format(prezzo);
					%>

					<h4 class="prezzo"><%=prezzoFormattato%></h4>
				</div>
				<%if(product.getQuantita() > 0){ %>
				<a href="#"
					onClick="addToCartAndRedirect(<%= product.getCodiceProdotto() %>, 'addToCart', '<%= request.getContextPath() %>/cart');">
					<img class="cart"
					src="<%=request.getContextPath()%>/images/site_images/icon_carrello2.png">
				</a>
				<%} %>
				<a
					href="GestioneWishlistController?action=addtowishlist&productId=<%= product.getCodiceProdotto() %>">
					<img class="wishlist"
					src="<%= request.getContextPath()%>/images/site_images/icon_wishlist.png"
					alt="wishlist">
				</a>
			</div>
			<%
			}
			}
			%>
		</div>
		<div id="error" style="display: none"></div>
	</section>
	<section id="sm-banner" class="section-p1">
		<div class="banner-box">
			<button class="minbanner">Learn More</button>
		</div>
		<div class="banner-box banner-box2">
			<button class="minbanner">Learn More</button>
		</div>
	</section>
	<jsp:include page="/common/footer.jsp" flush="true" />
</body>
</html>