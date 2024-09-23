<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"
	integrity="sha512-3gJwYpMe3QewGELv8k/BX9vcqhryRdzRMxVfq6ngyWXwo03GFEzjsUm8Q7RZcHPHksttq7/GFoxjCVUjkjvPdw=="
	crossorigin="anonymous"></script>

<script src="<%=request.getContextPath()%>/scripts/navi_script.js"></script>
<script src="<%=request.getContextPath()%>/scripts/cartAndSearch_functions.js?ts=<%=System.currentTimeMillis()%>"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/style/style.css">

<link rel="shortcut icon" type="image/ico" href="<%=request.getContextPath()%>/images/site_images/favicon.ico">
<section id="header">
	<a href="#"> <img
		src="<%=request.getContextPath()%>/images/site_images/logo.png"
		height="120" width="120" class="logo" alt="Logo" /></a>
	<div>
		<ul id="navbar">
			<li><a class="active" href="${pageContext.request.contextPath}/">Home</a></li>
			<li><a href="${pageContext.request.contextPath}/Autenticazione">Autenticazione</a></li>
			<li><a href="${pageContext.request.contextPath}/Registrazione">Registrazione</a></li>
			<li><a
				href="${pageContext.request.contextPath}/GestioneWishlistController?action=viewwishlist"><img
					id="iconw"
					src="<%=request.getContextPath()%>/images/site_images/icon_wishlist2.png"
					height="30" width="30" alt="Wishlist"
					onmouseout="document.getElementById('iconw').src='<%= request.getContextPath()%>/images/site_images/icon_wishlist2.png'"
					onmouseover="document.getElementById('iconw').src='<%= request.getContextPath()%>/images/site_images/icon_wishlist.png'"></a></li>

			<li><a href="${pageContext.request.contextPath}/AreaRiservata"><img
					src="<%=request.getContextPath()%>/images/site_images/icon_user.png"
					height="30" width="30" alt="AreaRiservata" id="iconp"
					onmouseout="document.getElementById('iconp').src='<%= request.getContextPath()%>/images/site_images/icon_user.png'"
					onmouseover="document.getElementById('iconp').src='<%= request.getContextPath()%>/images/site_images/icon_user2.png'"></a></li>

			<li><a href="${pageContext.request.contextPath}/cart"><img
					src="<%=request.getContextPath()%>/images/site_images/icon_carrello2.png"
					height="30" width="30" alt="Carrello" id="iconc"
					onmouseout="document.getElementById('iconc').src='<%= request.getContextPath()%>/images/site_images/icon_carrello2.png'"
					onmouseover="document.getElementById('iconc').src='<%= request.getContextPath()%>/images/site_images/icon_carrello.png'"></a></li>


			<a href="#" id="close" onClick="closeSidebar()"> <svg
					xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24"
					height="24">
                        <path fill="#2DA0F2"
						d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
                    </svg>
			</a>
		</ul>
		<ul id="categories">
			<li><a
				href="${pageContext.request.contextPath}/NavigazioneController?search_type=menu&keyword=TELEFONIA">Telefonia</a></li>
			<li><a
				href="${pageContext.request.contextPath}/NavigazioneController?search_type=menu&keyword=GRANDI_ELETTRODOMESTICI">Grandi
					Elettrodomestici</a></li>
			<li><a
				href="${pageContext.request.contextPath}/NavigazioneController?search_type=menu&keyword=PRODOTTI_ELETTRONICA">Prodotti
					Elettronica</a></li>
			<li><a
				href="${pageContext.request.contextPath}/NavigazioneController?search_type=menu&keyword=PICCOLI_ELETTRODOMESTICI">Piccoli
					Elettrodomestici</a></li>
		</ul>
		<ul id="search_section">
			<li>
				<form style = "background-color:#5b44f2" id="searchForm" method="post"
					action="${pageContext.request.contextPath}/NavigazioneController?search_type=bar"
					onsubmit="return validateSearch()">
					<button class="search" type="submit">
						<img
							src="<%=request.getContextPath()%>/images/site_images/search.png"
							width="30" height="30" alt="Search">
					</button>
					<input id="searchInput" type="text" name="keyword" required="">
				</form>
			</li>
		</ul>
	</div>

	<div id="mobile">
		<a href="${pageContext.request.contextPath}/cart"> <svg
				xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24"
				height="24">
                    <path fill="#2DA0F2"
					d="M16 6h-1V5c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v1H8c-1.1 0-1.99.9-1.99 2L6 19c0 1.1.89 2 1.99 2H17c1.1 0 2-.9 2-2l.01-11c0-1.1-.89-2-1.99-2zM9 5h6v1H9V5zm8 14H7V9h10v10z" />
                    <path fill="none" d="M0 0h24v24H0V0z" />
                </svg>
		</a>
		<button class="openbtn" onclick="openSidebar()" type="button">
			<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
				width="24" height="24">
                <path fill="#2DA0F2"
					d="M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z" />
            </svg>



		</button>
	</div>
	<br>
</section>
<script>
	// Add event listener for close button
	document.getElementById('close').addEventListener('click', function() {
		closeSidebar();
	});

	// Add event listener for search form submission
	document.getElementById('searchForm').addEventListener('submit',
			function(event) {
				event.preventDefault(); // Prevent default form submission
				if (validateSearch()) {
					this.submit(); // Submit the form
				} else {
					// Handle invalid search input
					displayNotification("Please enter a valid search query.");
				}
			});
</script>