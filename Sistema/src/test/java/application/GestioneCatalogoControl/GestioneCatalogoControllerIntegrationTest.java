package application.GestioneCatalogoControl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.GestioneCatalogoControl.GestioneCatalogoController;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.ProdottoException.FormatoNomeException;
import application.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.NavigazioneService.ProdottoException.FormatoModelloException;
import application.NavigazioneService.ProdottoException.FormatoCodiceException;
import application.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.NavigazioneService.ProxyProdotto;
import application.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNulloException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneCatalogoControllerIntegrationTest {
	private GestioneCatalogoController catalogoController;
	private ProdottoDAODataSource productDAO;
	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private GestioneCatalogoServiceImpl gcs;
	private PaginationUtils pu;
	private int pr_pagina;

	@BeforeEach
	public void setUp() throws ServletException, IOException {

		pr_pagina = 50;

		productDAO = mock(ProdottoDAODataSource.class);
		PhotoControl photoControl = mock(PhotoControl.class);
		PagamentoDAODataSource paymentDAO = mock(PagamentoDAODataSource.class);
		UtenteDAODataSource userDAO = mock(UtenteDAODataSource.class);
		OrdineDAODataSource orderDAO = mock(OrdineDAODataSource.class);
		NavigazioneServiceImpl ns = new NavigazioneServiceImpl(productDAO);
		gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);
		GestioneOrdiniServiceImpl gos = new GestioneOrdiniServiceImpl(orderDAO, userDAO, productDAO, paymentDAO);
		pu = new PaginationUtils(ns, gcs, gos);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}

	/**
	 * TEST CASES PER AGGIUNTA DI UN PRODOTTO NEL CATALOGO
	 * 
	 * TC14.1_1 : il codice del prodotto non è un numero
	 * 
	 * TC14.1_2 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto è presente nel database
	 * 
	 * TC14.1_3 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto non è definito nel formato corretto
	 * 
	 * TC14.1_4 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello non è definito nel formato corretto
	 * 
	 * TC14.1_5 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca non è specificata con il corretto formato
	 * 
	 * TC14.1_6 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo non è > 0.0
	 * 
	 * TC14.1_7 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione è vuota
	 * 
	 * TC14.1_8 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio è vuota
	 * 
	 * TC14.1_9 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto non è > 0
	 * 
	 * TC14.2_0 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria non è specificata nel formato corretto
	 * 
	 * TC14.2_1 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria è specificata nel formato corretto,
	 * 			  non si specifica la sottocategoria
	 * 
	 * TC14.2_2 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria è specificata nel formato corretto,
	 * 			  si specifica la sottocategoria ed è espressa nel formato
	 * 			  non corretto
	 * 
	 * TC14.2_3 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria è specificata nel formato corretto,
	 * 			  si specifica la sottocategoria ed è espressa nel formato
	 * 			  corretto,
	 * 			  si è assegnata una sottocategoria che non appartiene alla 
	 * 			  categoria specificata
	 * 
	 * TC14.2_4 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria è specificata nel formato corretto,
	 * 			  si specifica la sottocategoria ed è espressa nel formato
	 * 			  corretto,
	 * 			  si è assegnata una sottocategoria che appartiene alla 
	 * 			  categoria specificata
	 * 
	 * 
	 * */

	@Test
	public void testDoPost_TC14_1_1() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "12A";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat("340.99");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		assertThrows(FormatoCodiceException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}	

	@Test
	public void testDoPost_TC14_1_2() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(22, "Samsung Gear S2 Classic", "Lorem ipsum", "Lorem ipsum Lorem", Float.parseFloat("340.99"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Samsung", "Gear-S2", 2, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat("340.99");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(product2);

		assertThrows(ProdottoInCatalogoException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}	

	@Test
	public void testDoPost_TC14_1_3() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic//";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat("340.99");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoNomeException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}
	
	@Test
	public void testDoPost_TC14_1_4() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear\\\\@S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat("340.99");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoModelloException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}
	
	@Test
	public void testDoPost_TC14_1_5() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung123#";
		String priceStr = "340.99";
		float price = Float.parseFloat("340.99");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoMarcaException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}
	
	@Test
	public void testDoPost_TC14_1_6() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "0.0";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(PrezzoProdottoException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}
	
	@Test
	public void testDoPost_TC14_1_7() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat(priceStr);
		String topDescription = "";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoTopDescrizioneException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}
	
	@Test
	public void testDoPost_TC14_1_8() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Lorem ipsum";
		String details = "";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoDettagliException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}
	
	@Test
	public void testDoPost_TC14_1_9() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "0";
		int quantity = Integer.parseInt(quantityStr);
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(QuantitaProdottoException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}
	
	@Test
	public void testDoPost_TC14_2_0() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = Integer.parseInt(quantityStr);
		String category = "ERRORE";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);

		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(CategoriaProdottoException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		catalogoController.doPost(request, response);

	}
	
	@Test
	public void testDoPost_TC14_2_1() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = Integer.parseInt(quantityStr);
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = null;
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);
		
		ProxyProdotto product3 = new ProxyProdotto(22, "Samsung Gear S2 Classic", "Lorem ipsum", "Lorem ipsum Lorem", Float.parseFloat("340.99"), 
				Categoria.PRODOTTI_ELETTRONICA, "Samsung", "Gear-S2", 2, true, false, productDAO);

		pageProducts.add(product3);
		
		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(pageProducts);
		
		catalogoController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("error", "Prodotto Aggiunto con Successo!");
		verify(response).sendRedirect(request.getContextPath() + "/Catalogo");
		
	}
	
	@Test
	public void testDoPost_TC14_2_2() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = Integer.parseInt(quantityStr);
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "ERRORE";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);
		
		ProxyProdotto product3 = new ProxyProdotto(22, "Samsung Gear S2 Classic", "Lorem ipsum", "Lorem ipsum Lorem", Float.parseFloat("340.99"), 
				Categoria.PRODOTTI_ELETTRONICA, "Samsung", "Gear-S2", 2, true, false, productDAO);

		pageProducts.add(product3);
		
		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(pageProducts);
		
		assertThrows(SottocategoriaProdottoException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		
		catalogoController.doPost(request, response);
		
	}
	
	@Test
	public void testDoPost_TC14_2_3() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = Integer.parseInt(quantityStr);
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTPHONE";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);
		
		ProxyProdotto product3 = new ProxyProdotto(22, "Samsung Gear S2 Classic", "Lorem ipsum", "Lorem ipsum Lorem", Float.parseFloat("340.99"), 
				Categoria.PRODOTTI_ELETTRONICA, "Samsung", "Gear-S2", 2, true, false, productDAO);

		pageProducts.add(product3);
		
		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(pageProducts);
		
		assertThrows(AppartenenzaSottocategoriaException.class , () -> {
			gcs.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

		});
		
		catalogoController.doPost(request, response);
		
	}
	
	
	@Test
	public void testDoPost_TC14_2_4() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);

		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);


		String code = "22";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = Integer.parseInt(quantityStr);
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "addProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);
		
		ProxyProdotto product3 = new ProxyProdotto(22, "Samsung Gear S2 Classic", "Lorem ipsum", "Lorem ipsum Lorem", Float.parseFloat("340.99"), 
				Categoria.PRODOTTI_ELETTRONICA, "Samsung", "Gear-S2", 2, true, false, productDAO);

		pageProducts.add(product3);
		
		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(pageProducts);
		
		
		catalogoController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("error", "Prodotto Aggiunto con Successo!");
		verify(response).sendRedirect(request.getContextPath() + "/Catalogo");
		
	}

	
	/**
	 * TEST CASES PER LA RIMOZIONE DI UN PRODOTTO DAL CATALOGO
	 * 
	 * TC15.1_1: non viene specificato il prodotto da rimuovere
	 * 
	 * TC15.1_2: viene specificato il prodotto da rimuovere dal catalogo
	 * 			 del negozio
	 * 
	 * */
	
	@Test
	public void testDoPost_TC15_1_1() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);


		String code = null;
		String name = "HP 15s-fq5040nl";
		String model = "15s-fq5040nl";
		String brand =  "HP";
		String priceStr = "454.50";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Prova";
		String details = "Prova";
		String quantityStr = "0";
		int quantity = Integer.parseInt(quantityStr);
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "PC";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "deleteProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);
		
		assertThrows(ProdottoNulloException.class , () -> {
			gcs.rimozioneProdottoDaCatalogo(null, 1, pr_pagina);

		});
		
		catalogoController.doPost(request, response);
	}
	
	@Test
	public void testDoPost_TC15_1_2() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);


		String code = "12";
		String name = "HP 15s-fq5040nl";
		String model = "15s-fq5040nl";
		String brand =  "HP";
		String priceStr = "454.50";
		float price = Float.parseFloat(priceStr);
		String topDescription = "Prova";
		String details = "Prova";
		String quantityStr = "0";
		int quantity = Integer.parseInt(quantityStr);
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "PC";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;


		String action = "deleteProduct";

		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(null);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);

		when(request.getParameter("action")).thenReturn(action);
		
		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(product2);

		catalogoController = new GestioneCatalogoController(productDAO, gcs, pu);
		
		catalogoController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("error", "Prodotto Eliminato con Successo!");
		verify(response).sendRedirect(request.getContextPath() + "/Catalogo");
		
	}
}
