<%@page contentType="text/html" pageEncoding="UTF-8"
	import="java.util.Currency,
				  java.text.NumberFormat,
				  application.NavigazioneService.ProxyProdotto,
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
				<a href="#"
					onClick="modifyCart(<%=product.getCodiceProdotto()%>,'aggiungiAlCarrello')">
					<img class="cart"
					src="<%=request.getContextPath()%>/images/site_images/icon_carrello2.png">
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
		<h4>Buy a laptop today</h4>
		<h2>
			Up to <span>70% off</span> - To improve your efficiency at home
		</h2>
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
				<a href="#"
					onClick="modifyCart(<%=product.getCodiceProdotto()%>,'aggiungiAlCarrello')">
					<img class="cart"
					src="<%=request.getContextPath()%>/images/site_images/icon_carrello2.png">
				</a>
			</div>
			<%
			}
			}
			%>
		</div>
	</section>
	<section id="sm-banner" class="section-p1">
		<div class="banner-box">
			<h4>crazy deals</h4>
			<h2>Buy 1 get 1 free</h2>
			<span>The best quality tech is on sale at TechHeaven</span>
			<button class="minbanner">Learn More</button>
		</div>
		<div class="banner-box banner-box2">
			<h4>summer</h4>
			<h2>Upcoming season</h2>
			<span>Fresh offers available on sale at TechHeaven</span>
			<button class="minbanner">Learn More</button>
		</div>
	</section>
	<jsp:include page="/common/footer.jsp" flush="true" />
</body>
</html>