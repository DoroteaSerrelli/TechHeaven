<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="it">
    <head>
        <title>TechHeaven</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_options.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/product_table.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style/catalog_form.css">             
        <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
        </script>
    </head>              
    <body>
       <jsp:include page="/common/header.jsp"  flush="true"/>
       <jsp:include page="/protected/cliente/roleSelector.jsp"  flush="true"/>
       <script type="text/javascript">
            // Define the context path as a global variable
            window.contextPath = '<%= request.getContextPath() %>';
            fromAnotherPage = true;
        </script> 
        <script src="${pageContext.request.contextPath}/scripts/indexedDBUtils.js"></script>    
        <div id="error">
            <% String errormsg="";
                errormsg= (String)request.getSession().getAttribute("error");
                if(errormsg==null) errormsg="";                                                       
            %>
            <h2> <%=errormsg%> </h2>
            <%request.getSession().removeAttribute("error");%>
        </div>   
         <jsp:include page="/protected/gestoreCatalogo/catalogo_toolbar.jsp"  flush="true"/>                         
     <div class="section-p1">  
           <section id="" >
            <!-- Your form for adding a new product -->                  
                        <h2>Aggiungi un nuovo prodotto</h2>
                        <form action="${pageContext.request.contextPath}/GestioneCatalogoController" method="post" enctype="multipart/form-data">
                             <input id="action" type = "hidden" value = "addProduct">
                             <div id="error"></div>
                                <div class="form-group">                    
                                    <label for="productID"> ID Prodotto </label>
                                    <input type="number" id="number" name="productId" oninput="validateProductID(this, 'ID')" required>
                                    <div id="prodIDError" class="erromsg" style="display:none;"></div>                           
                                    <label for="productName">Nome Prodotto:</label>
                                    <input type="text" id="productName" name="productName" oninput="validateProductNameorModel(this, 'Nome')" required>
                                    <div id="prodNomeError" class="erromsg" style="display:none;"></div>                                                           
                                </div>
                            <div class="form-group">
                                <label for="TopDescrizione">Top Descrizione:</label>
                                <textarea name="topDescrizione" rows="5" cols="40" oninput="validateDettailsAndDescription(this, 'Descrizione')" required></textarea>
                                <div id="prodDescrizioneError" class="erromsg" style="display:none;"></div>  
                                <label for="Dettagli">Dettagli:</label>
                                <textarea name="dettagli" rows="5" cols="40" oninput="validateDettailsAndDescription(this, 'Dettagli')" required></textarea>
                                <div id="prodDettagliError" class="erromsg" style="display:none;"></div>     
                            </div>   
                        <div class="form-group">
                            <label for="prezzo">Prezzo:</label>                       
                            <input type="text" name="price" oninput="validatePrice(this, 'Prezzo')" required>
                            <div id="prodPrezzoError" class="erromsg" style="display:none;"></div>  
                        </div>
                            <div class="form-group">
                                <select name="categoria">
                                    <option value="GRANDI_ELETTRODOMESTICI">Grandi Elettrodomestici</option>
                                    <option value="PICCOLI_ELETTRODOMESTICI">Piccoli Elettrodomestici</option>
                                    <option value="TELEFONIA">Telefonia</option>
                                    <option value="PRODOTTI_ELETTRONICA">Prodotti Elettronica</option>                            
                                </select>
                                <select name="sottocategoria">
                                    <option value="null">Nessuna Sottocategoria</option>
                                    <option value="TABLET">Tablet</option>
                                    <option value="SMARTPHONE">Smartphone</option>
                                    <option value="PC">PC</option>
                                    <option value="SMARTWATCH">Smartwatch</option>                               
                                </select>
                            </div>    
                            <div class="form-group">
                                <label for="marca">Marca:</label>
                                <input type="text" id="marca" name="marca" oninput="validateBrand(this)" required>
                                <div id="prodBrandError" class="erromsg" style="display:none;"></div> 
                            </div>
                        <div class="form-group">
                            <label for="modello">Modello:</label>
                            <input type="text" id="modello" name="modello" oninput="validateProductNameorModel(this, 'Modello')" required>
                            <div id="prodModelloError" class="erromsg" style="display:none;"></div> 
                        </div>
                        <div class="form-group">
                            <label for="inVetrina">In Vetrina:</label>
                            <input type="checkbox" id="inVetrina" name="inVetrina" value="true">
                        </div>
                        <div class="form-group">
                            <label for="inCatalogo">In Catalogo:</label>
                            <input type="checkbox" id="inCatalogo" name="inCatalogo" value="true">
                        </div>
                        <div class="form-group">
                            <label for="quantità">Quantità:</label>
                            <input type="number" id="quantità" name="quantita" oninput="validateProductID(this, 'Quantità')" required>
                            <div id="prodQuantitàError" class="erromsg" style="display:none;"></div>  
                        </div>                     
                        <div class="form-group">
                            <label for="file">Immagine:</label>
                            <input type="file" id="file" name="file" accept="image/*">
                        </div>
                         <button class="confirm_button" type="submit" id="addPrBtn" onclick="return validateForm()">Aggiungi</button>
                         <a href="<%=request.getContextPath()%>/Catalogo"><button class="cancel_button" type="button">Annulla</button></a>
                    </form>
        </section>
     </div>     
       <script src="${pageContext.request.contextPath}/scripts/shifting_menu_manag_functions_sidebar.js"></script>
       <script src="${pageContext.request.contextPath}/scripts/validateNewProduct.js"></script> 
          <jsp:include page="/common/footer.jsp"  flush="true"/> 
    </body>
</html>
