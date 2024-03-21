<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <link rel="stylesheet" href="style.css">
        <jsp:include page="header.jsp"  flush="true"/>
        <section id="hero" >
            <h4>Spacial offers availables every day</h4>
            <h2>Super value deal</h2>
            <h1>On all products</h1>
            <button> Buy Now </button>
        </section>   
          <section id="feature" class="section-p1">
            <div class="fe-box">
                <img src="view/img/freechockbird2.png" alt="alt">
                <h6>Free Shipping</h6>
                
            </div>
             <div class="fe-box">
                <img src="view/img/spedizionionline.png" alt="alt">
                <h6>Online Order</h6>
                
            </div>
            <div class="fe-box">
                <img src="view/img/savemoneyups.png" alt="alt">
                <h6>Save Money</h6>
                
            </div>           
            <div class="fe-box">
                <img src="view/img/promozioni.png" alt="alt">
                <h6>Promotions</h6>                
            </div>           
            <div class="fe-box">
                <img src="view/img/supportoh24.png" alt="alt">
                <h6>F24/7 Support</h6>               
            </div> 
        </section>
        <section id="product1" class="section-p1">
                <h2>Featured Product</h2>
                <p>Some of our featured products include:</p>
                <div class="pro-container">
                    <div class="pro">
                        <img src="alt" alt="alt">
                        <div class="des">
                            <span>Lindt</span>
                            <h5 id="pr_name">Gold Bunny Latte 200g</h5>
                            <div class="star">
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>                                
                                <i class="fa-solid fa-star" ></i>
                            </div>
                            <h4>4,95€</h4>
                            </div>
                        <a href="Shop">                           
                            <i class="fa-solid fa-cart-plus cart">                                 
                            </i>
                        </a>
                    </div>                                        
            </section> 
            <section id="banner">
                <h4>Buy a laptop today</h4>
                <h2>Up to <span>70% off</span> - To improve your efficiency at home</h2>
                <button>Explore More</button>
            </section>			
	    <section id="product1" class="section-p1">
                <h2>Summer Arrival</h2>
                <p>Keep up with our summer products:</p>
                <div class="pro-container">
                    <div class="pro">
                        <img src="img/prodotti/pralineLindt.jpg" alt="alt">
                        <div class="des">
                            <span>Lindt</span>
                            <h5>Praline assortite 316gr</h5>
                            <div class="star">
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>                                
                                <i class="fa-solid fa-star" ></i>
                            </div>
                            <h4>10,38€</h4>
                            </div>
                        <a href="Shop">
                            <i class="fa-solid fa-cart-plus cart"></i>
                        </a>
                    </div>                   
                <div class="pro">
                        <img src="img/prodotti/pralineMilka.jpg" alt="alt">
                        <div class="des">
                            <span>Milka</span>
                            <h5>Praline al cioccolato 92gr</h5>
                            <div class="star">
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>                                
                                <i class="fa-solid fa-star" ></i>
                            </div>
                            <h4>3,29€</h4>
                            </div>
                        <a href="Shop" >
                            <i class="fa-solid fa-cart-plus cart"></i>
                        </a>
                    </div>    
                <div class="pro">
                        <img src="img/prodotti/gelatokinderpinguino.png" alt="alt">
                        <div class="des">
                            <span>Kinder</span>
                            <h5>Pinguì gelato cioccolato 120gr</h5>
                            <div class="star">
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>
                                <i class="fa-solid fa-star" ></i>                                
                                <i class="fa-solid fa-star" ></i>
                            </div>
                            <h4>2,15€ (17,27 €/kg)</h4>
                            </div>
                        <a href="Shop" >
                            <i class="fa-solid fa-cart-plus cart"></i>
                        </a>
                    </div>                   
                </div>                             
            </section> 
            <section id="sm-banner" class="section-p1">
                <div class= "banner-box">
		<h4>crazy deals</h4>
		<h2> Buy 1 get 1 free</h2>
		<span>The best quality chocolate is on sale at ChockyMaker</span>
		<button class="minbanner"> Learn More </button>
                </div>
                <div class= "banner-box banner-box2">
                    <h4>summer</h4>
                    <h2> Upcoming season</h2>
                    <span>Fresh offers available on sale at ChockyMaker</span>
                    <button class="minbanner"> Learn More </button>
                </div>
            </section>	
       <jsp:include page="footer.jsp"  flush="true"/>
    </body>
</html>