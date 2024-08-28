<%@page import="application.NavigazioneService.ProxyProdotto"%>
<%@page import="java.util.Collection"%>
<%
    Collection<ProxyProdotto> telefoni = (Collection<ProxyProdotto>) application.getAttribute("telefoni");
    Collection<ProxyProdotto> gr_elettr = (Collection<ProxyProdotto>) application.getAttribute("gr_elettr");
    
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>TechHeaven</title>
         <link rel="favicon.ico" href="${pageContext.request.contextPath}/view/img/favicon.ico" type="image/x-icon">        
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">         
    </head>
    <body>
        <link rel="stylesheet" href="common/style.css">
        <jsp:include page="common/header.jsp"  flush="true"/>
        <section id="hero" >
            <h4>Spacial offers availables every day</h4>
            <h2>Super value deal</h2>
            <h1>On all products</h1>
            <button> Buy Now </button>
        </section>   
          <section id="feature" class="section-p1">
            <div class="fe-box">
                <img src="view/img/freeshipping.png" alt="alt">
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
                <%if(telefoni!=null && !telefoni.isEmpty()){%>                                        
                    <% for (ProxyProdotto product : telefoni) { 
                        if(product.isInVetrina()){%>       
                    <div class="pro">
                        <img src="image?productId=<%= product.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"
                         style="width: 80px; height: 80px"/>
                        <div class="des">
                            <span><%=product.getMarca()%></span>
                            <h5 id="pr_name"><%=product.getNomeProdotto()%></h5>                           
                            <h4><%=product.getPrezzo()%>€</h4>
                            </div>
                        <a href="#" onClick="modifyCart(<%=product.getCodiceProdotto()%>,'aggiungiAlCarrello')">
                            <img class="cart" src="${pageContext.request.contextPath}/view/img/icon_carrello2.png">
                        </a>
                    </div>
                    <%} 
                    }
                }%>
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
                    <%if (gr_elettr!=null && !gr_elettr.isEmpty()){%>                                      
                        <% for (ProxyProdotto product : gr_elettr) { %>       
                       <div class="pro">
                           <img src="image?productId=<%= product.getCodiceProdotto() %>" alt="alt" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/view/img/placeholder.png';"
                            style="width: 80px; height: 80px"/>
                           <div class="des">
                               <span><%=product.getMarca()%></span>
                               <h5 id="pr_name"><%=product.getNomeProdotto()%></h5>                           
                               <h4><%=product.getPrezzo()%>€</h4>
                               </div>
                           <a href="#" onClick="modifyCart(<%=product.getCodiceProdotto()%>,'aggiungiAlCarrello')">
                               <img class="cart" src="${pageContext.request.contextPath}/view/img/icon_carrello2.png">
                           </a>                                       
                       </div> 
                   <%}
                    } %>
                </div>       
            </section> 
            <section id="sm-banner" class="section-p1">
                <div class= "banner-box">
		<h4>crazy deals</h4>
		<h2> Buy 1 get 1 free</h2>
		<span>The best quality tech is on sale at TechHeaven</span>
		<button class="minbanner"> Learn More </button>
                </div>
                <div class= "banner-box banner-box2">
                    <h4>summer</h4>
                    <h2> Upcoming season</h2>
                    <span>Fresh offers available on sale at TechHeaven</span>
                    <button class="minbanner"> Learn More </button>
                </div>
            </section>	
       <jsp:include page="common/footer.jsp"  flush="true"/>
    </body>
</html>