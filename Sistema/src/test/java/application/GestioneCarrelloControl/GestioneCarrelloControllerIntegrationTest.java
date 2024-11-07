package application.GestioneCarrelloControl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.GestioneCarrello.GestioneCarrelloControl.GestioneCarrelloController;
import application.GestioneCarrello.GestioneCarrelloService.Carrello;
import application.GestioneCarrello.GestioneCarrelloService.GestioneCarrelloServiceImpl;
import application.GestioneCarrello.GestioneCarrelloService.ItemCarrello;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.QuantitaProdottoException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneCarrelloControllerIntegrationTest {
	private GestioneCarrelloController carrelloController;
	private GestioneCarrelloServiceImpl gc;
	private ProdottoDAODataSource productDAO;

	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@BeforeEach
	public void setUp() throws ServletException, IOException {

		productDAO = mock(ProdottoDAODataSource.class);
		gc = new GestioneCarrelloServiceImpl(productDAO);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}

	/**
	 * TEST CASES PER AGGIUNTA PRODOTTO NEL CARRELLO
	 * 
	 * TC9_1.1_1: il carrello è vuoto e si vuole inserire un prodotto con n°scorte 
	 * 				in magazzino = 0
	 * 
	 * TC9_1.1_2 : il carrello è vuoto, il prodotto da inserire ha n° scorte > 0 (e il prodotto
	 * 				non è presente nel carrello)
	 * 
	 * TC9_1.1_3 : il carrello non è vuoto e si vuole inserire un prodotto con n° scorte = 0
	 * 
	 * TC9_1.1_4 : il carrello non è vuoto, il prodotto da inserire ha n° scorte > 0 e il 
	 * 				prodotto è presente nel carrello
	 * 
	 * TC9_1.1_5 : il carrello non è vuoto, il prodotto da inserire ha n° scorte > 0 e il
	 * 				prodotto non è presente nel carrello
	 * 
	 * 
	 * */

	@Test
	public void testDoGet_TC9_1_1_1() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		ProxyProdotto product = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);

		ItemCarrello inCart = new ItemCarrello();
		inCart.setCodiceProdotto(product.getCodiceProdotto());
		inCart.setNomeProdotto(product.getNomeProdotto());
		inCart.setCategoria(product.getCategoria());
		inCart.setMarca(product.getMarca());
		inCart.setPrezzo(product.getPrezzo());
		inCart.setModello(product.getModello());
		inCart.setDettagli(product.getTopDescrizione());
		
		Carrello cart = new Carrello();

		String action = "addToCart";
		String productId = "16";
		int productIdInt = 16;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(null);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(productIdInt)).thenReturn(product);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);
		
		assertThrows(QuantitaProdottoException.class, () -> {
			gc.aggiungiAlCarrello(cart, inCart);
		});

		String jsonResponse = "{\"updatedQuantity\":0,\"totalAmount\":\"0,00\",\"updatedPrice\":\"0,00\",\"message\":\"Non è disponibile il prodotto per l’acquisto\",\"status\":\"invalid\"}";

		 
		verify(response.getWriter()).write(jsonResponse);
		verify(response).sendRedirect(request.getContextPath() + "/cart");

	}

	@Test
	public void testDoGet_TC9_1_1_2() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		Carrello cart = new Carrello();

		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova",  "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ItemCarrello inCart = new ItemCarrello();
		inCart.setCodiceProdotto(product.getCodiceProdotto());
		inCart.setNomeProdotto(product.getNomeProdotto());
		inCart.setCategoria(product.getCategoria());
		inCart.setMarca(product.getMarca());
		inCart.setPrezzo(product.getPrezzo());
		inCart.setModello(product.getModello());
		inCart.setDettagli(product.getTopDescrizione());


		String action = "addToCart";
		String productId = "12";
		int productIdInt = 12;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(null);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(productIdInt)).thenReturn(product);
		when(request.getSession().getAttribute("usercart")).thenReturn(new Carrello());
		
		Carrello expectedCart = new Carrello();
		expectedCart.addProduct(inCart);
		
		Carrello realCart = gc.aggiungiAlCarrello(cart, inCart);
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponse = "{\"updatedQuantity\":1,\"totalAmount\":\"454,50\",\"updatedPrice\":\"454,50\",\"message\":\"Prodotto aggiunto nel carrello con successo\",\"status\":\"valid\"}";

		 
		assertEquals(expectedCart, realCart);
		verify(response.getWriter()).write(jsonResponse);
		verify(request.getSession()).setAttribute("error", "Prodotto aggiunto nel carrello con successo");
		verify(request.getSession()).setAttribute("status", "valid");

	}

	@Test
	public void testDoGet_TC9_1_1_3() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova",  "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(25, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

		
		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		cart.addProduct(item1);

		ItemCarrello inCart = new ItemCarrello();
		inCart.setCodiceProdotto(product2.getCodiceProdotto());
		inCart.setNomeProdotto(product2.getNomeProdotto());
		inCart.setCategoria(product2.getCategoria());
		inCart.setMarca(product2.getMarca());
		inCart.setPrezzo(product2.getPrezzo());
		inCart.setModello(product2.getModello());
		inCart.setDettagli(product2.getTopDescrizione());


		String action = "addToCart";
		String productId = "25";
		int productIdInt = 25;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(null);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(productIdInt)).thenReturn(product2);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);
		
		assertThrows(QuantitaProdottoException.class, () ->{
			gc.aggiungiAlCarrello(cart, inCart);
		});

		String jsonResponse = "{\"updatedQuantity\":0,\"totalAmount\":\"0,00\",\"updatedPrice\":\"0,00\",\"message\":\"Non è disponibile il prodotto per l’acquisto\",\"status\":\"invalid\"}";

		
		verify(response.getWriter()).write(jsonResponse);
		verify(response).sendRedirect(request.getContextPath() + "/cart");

	}

	@Test
	public void testDoGet_TC9_1_1_4() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		cart.addProduct(item1);


		ItemCarrello inCart = new ItemCarrello();
		inCart.setCodiceProdotto(product1.getCodiceProdotto());
		inCart.setNomeProdotto(product1.getNomeProdotto());
		inCart.setCategoria(product1.getCategoria());
		inCart.setMarca(product1.getMarca());
		inCart.setPrezzo(product1.getPrezzo());
		inCart.setModello(product1.getModello());
		inCart.setDettagli(product1.getTopDescrizione());


		String action = "addToCart";
		String productId = "12";
		int productIdInt = 12;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(null);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(productIdInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);
		
		assertThrows(ProdottoPresenteException.class, () -> {
			gc.aggiungiAlCarrello(cart, inCart);
			
		});

		String jsonResponse = "{\"updatedQuantity\":0,\"totalAmount\":\"454,50\",\"updatedPrice\":\"0,00\",\"message\":\"Prodotto già presente nel carrello\",\"status\":\"invalid\"}";

		 
		verify(response.getWriter()).write(jsonResponse);
		verify(response).sendRedirect(request.getContextPath() + "/cart");

	}

	@Test
	public void testDoGet_TC9_1_1_5() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(10, "Bosch lavatrice a carica frontale", "Prova", "Prova", Float.parseFloat("590.50"), 
				Categoria.GRANDI_ELETTRODOMESTICI, "Bosch", "QualcheModello", 112, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		cart.addProduct(item1);

		ItemCarrello inCart = new ItemCarrello();
		inCart.setCodiceProdotto(product2.getCodiceProdotto());
		inCart.setNomeProdotto(product2.getNomeProdotto());
		inCart.setCategoria(product2.getCategoria());
		inCart.setMarca(product2.getMarca());
		inCart.setPrezzo(product2.getPrezzo());
		inCart.setModello(product2.getModello());
		inCart.setDettagli(product2.getTopDescrizione());


		String action = "addToCart";
		String productId = "10";
		int productIdInt = 10;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(null);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(productIdInt)).thenReturn(product2);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		Carrello expectedCart = new Carrello();
		expectedCart.addProduct(item1);
		expectedCart.addProduct(inCart);
		
		Carrello cart2 = new Carrello();
		cart2.addProduct(item1);
		Carrello realCart = gc.aggiungiAlCarrello(cart2, inCart);
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponse = "{\"updatedQuantity\":1,\"totalAmount\":\"1045,00\",\"updatedPrice\":\"590,50\",\"message\":\"Prodotto aggiunto nel carrello con successo\",\"status\":\"valid\"}";

		assertEquals(expectedCart, realCart);
		verify(response.getWriter()).write(jsonResponse);
		verify(request.getSession()).setAttribute("error", "Prodotto aggiunto nel carrello con successo");
		verify(request.getSession()).setAttribute("status", "valid");

	}

	/**
	 * TEST CASES PER RIMOZIONE PRODOTTO DAL CARRELLO
	 * 
	 * TC9_2.1_1: il carrello non è vuoto e il prodotto si trova nel carrello
	 * 
	 * 
	 * */

	@Test
	public void testDoGet_TC9_2_1_1() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, IOException, ServletException {
		
		carrelloController = new GestioneCarrelloController(productDAO, gc);
		
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(13, "Amazfit T-Rex 2", "Prova", "Prova", Float.parseFloat("160.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Amazfit", "T-Rex2", 10, true, false, productDAO);

		
		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		ItemCarrello item2 = new ItemCarrello();	//item da rimuovere
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setCategoria(product2.getCategoria());
		item2.setMarca(product2.getMarca());
		item2.setPrezzo(product2.getPrezzo());
		item2.setModello(product2.getModello());
		item2.setDettagli(product2.getTopDescrizione());

		cart.addProduct(item1);
		cart.addProduct(item2);


		String action = "removeFromCart";
		String pid = "13";
		int pidInt = 13;
		HashMap<Integer, Integer> hs = new HashMap<>();

		hs.put(13, 10); //id AmazFit, quantità AmazFit

		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(hs);
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product2);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		
		Carrello expectedCart = new Carrello();
		expectedCart.addProduct(item1);
		
		Carrello temp = new Carrello();
		temp.addProduct(item2);
		temp.addProduct(item1);
		Carrello realCart = gc.rimuoviDalCarrello(temp, item2);
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponseString = "{\"updatedQuantity\":0,\"totalAmount\":\"454,50\",\"updatedPrice\":\"0,00\",\"message\":\"Prodotto rimosso con successo dal carrello\",\"status\":\"valid\"}";

		hs.remove(13);
		
		assertEquals(realCart, expectedCart);
		verify(response.getWriter()).write(jsonResponseString);
		verify(request.getSession()).setAttribute("products_available_inStock", hs);
		verify(request.getSession()).setAttribute("usercart", cart);
	}
	
	
	/**
	 * TEST CASES PER AUMENTO DELLA QUANTITA' DI UN PRODOTTO NEL CARRELLO
	 * 
	 * TC9_3.1_1: il carrello non è vuoto, il prodotto si trova nel carrello
	 * 				e la quantità di aggiornamento non è nel formato corretto
	 * 
	 * TC9_3.1_2: il carrello non è vuoto, il prodotto si trova nel carrello
	 * 				e la quantità di aggiornamento è definita nel formato corretto
	 * 
	 * 
	 * */

	
	@Test
	public void testDoGet_TC9_3_1_1() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, IOException, ServletException, QuantitaProdottoException {
		
		carrelloController = new GestioneCarrelloController(productDAO, gc);
		
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);

		
		ItemCarrello item1 = new ItemCarrello(); //item da modificare
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setCategoria(product1.getCategoria());
		item1.setMarca(product1.getMarca());
		item1.setPrezzo(product1.getPrezzo());
		item1.setModello(product1.getModello());
		item1.setDettagli(product1.getTopDescrizione());

		ItemCarrello item2 = new ItemCarrello();
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setCategoria(product2.getCategoria());
		item2.setMarca(product2.getMarca());
		item2.setPrezzo(product2.getPrezzo());
		item2.setModello(product2.getModello());
		item2.setDettagli(product2.getTopDescrizione());
		item2.setQuantita(4);

		cart.addProduct(item1);
		cart.addProduct(item2);
		
		int quantityItem1 = 180;

		String action = "increaseQuantity";
		String pid = "12";
		int pidInt = 12;

		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getParameter("prod_quantità")).thenReturn(String.valueOf(quantityItem1));
		
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		carrelloController.doGet(request, response);
		
		assertThrows(QuantitaProdottoException.class, () -> {
			gc.aumentaQuantitaNelCarrello(cart, item1, quantityItem1);
			
		});
		
		String jsonResponseString = "{\"updatedQuantity\":0,\"totalAmount\":\"894,50\",\"updatedPrice\":\"0,00\",\"message\":\"La quantità specificata supera il numero di scorte possibili del prodotto in magazzino.\",\"status\":\"invalid\"}";
		
		verify(response.getWriter()).write(jsonResponseString);
		verify(response).sendRedirect(request.getContextPath() + "/cart");
		
	}
	
	@Test
	public void testDoGet_TC9_3_1_2() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, IOException, ServletException, QuantitaProdottoException {
		
		carrelloController = new GestioneCarrelloController(productDAO, gc);
		
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);

		
		ItemCarrello item1 = new ItemCarrello();	//item da modificare
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setCategoria(product1.getCategoria());
		item1.setMarca(product1.getMarca());
		item1.setPrezzo(product1.getPrezzo());
		item1.setModello(product1.getModello());
		item1.setDettagli(product1.getTopDescrizione());

		ItemCarrello item2 = new ItemCarrello();	
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setCategoria(product2.getCategoria());
		item2.setMarca(product2.getMarca());
		item2.setPrezzo(product2.getPrezzo());
		item2.setModello(product2.getModello());
		item2.setDettagli(product2.getTopDescrizione());
		item2.setQuantita(4);

		cart.addProduct(item1);
		cart.addProduct(item2);
		
		int quantityItem1 = 40;


		String action = "increaseQuantity";
		String pid = "12";
		int pidInt = 12;


		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getParameter("prod_quantità")).thenReturn(String.valueOf(quantityItem1));
		
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		
		Carrello expectedCart = new Carrello();
		
		ItemCarrello updatedItem1 = new ItemCarrello();
		updatedItem1.setCodiceProdotto(product1.getCodiceProdotto());
		updatedItem1.setNomeProdotto(product1.getNomeProdotto());
		updatedItem1.setCategoria(product1.getCategoria());
		updatedItem1.setMarca(product1.getMarca());
		updatedItem1.setPrezzo(product1.getPrezzo());
		updatedItem1.setModello(product1.getModello());
		updatedItem1.setDettagli(product1.getTopDescrizione());
		updatedItem1.setQuantita(40);
		
		expectedCart.addProduct(updatedItem1);
		expectedCart.addProduct(item2);
		
		Carrello realCart = gc.aumentaQuantitaNelCarrello(cart, item1, quantityItem1);
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		
		carrelloController.doGet(request, response);
		
		String jsonResponseString = "{\"updatedQuantity\":40,\"totalAmount\":\"18620,00\",\"updatedPrice\":\"18180,00\",\"message\":\"Quantità modificata nel carrello con successo\",\"status\":\"valid\"}";
		
		assertEquals(realCart, expectedCart);
		verify(response.getWriter()).write(jsonResponseString);
		verify(request.getSession()).setAttribute("usercart", cart); 
		
	}
	
	/**
	 * TEST CASES PER DIMINUZIONE DELLA QUANTITA' DI UN PRODOTTO NEL CARRELLO
	 * 
	 * TC9_4.1_1: il carrello non è vuoto, il prodotto si trova nel carrello
	 * 				e la quantità di aggiornamento non è nel formato corretto
	 * 
	 * TC9_4.1_2: il carrello non è vuoto, il prodotto si trova nel carrello
	 * 				e la quantità di aggiornamento è definita nel formato corretto
	 * 
	 * */
	
	@Test
	public void testDoGet_TC9_4_1_1() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, IOException, ServletException, QuantitaProdottoException {
		
		carrelloController = new GestioneCarrelloController(productDAO, gc);
		
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setCategoria(product1.getCategoria());
		item1.setMarca(product1.getMarca());
		item1.setPrezzo(product1.getPrezzo());
		item1.setModello(product1.getModello());
		item1.setDettagli(product1.getTopDescrizione());
		item1.setQuantita(25);

		ItemCarrello item2 = new ItemCarrello();	//item da rimuovere
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setCategoria(product2.getCategoria());
		item2.setMarca(product2.getMarca());
		item2.setPrezzo(product2.getPrezzo());
		item2.setModello(product2.getModello());
		item2.setDettagli(product2.getTopDescrizione());
		item2.setQuantita(4);

		cart.addProduct(item1);
		cart.addProduct(item2);
		
		int quantityItem1 = 40;


		String action = "decreaseQuantity";
		String pid = "12";
		int pidInt = 12;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getParameter("prod_quantità")).thenReturn(String.valueOf(quantityItem1));
		
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);
		
		String jsonResponseString = "{\"updatedQuantity\":0,\"totalAmount\":\"11802,50\",\"updatedPrice\":\"0,00\",\"message\":\"La quantità inserita non è minore della quantità del prodotto nel carrello\",\"status\":\"invalid\"}";
		
		verify(response.getWriter()).write(jsonResponseString);
		verify(response).sendRedirect(request.getContextPath() + "/cart");
		
	}
	
	@Test
	public void testDoGet_TC9_4_1_2() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, IOException, ServletException, QuantitaProdottoException {
		
		carrelloController = new GestioneCarrelloController(productDAO, gc);
		
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);

		
		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setCategoria(product1.getCategoria());
		item1.setMarca(product1.getMarca());
		item1.setPrezzo(product1.getPrezzo());
		item1.setModello(product1.getModello());
		item1.setDettagli(product1.getTopDescrizione());
		item1.setQuantita(25);

		ItemCarrello item2 = new ItemCarrello();	//item da rimuovere
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setCategoria(product2.getCategoria());
		item2.setMarca(product2.getMarca());
		item2.setPrezzo(product2.getPrezzo());
		item2.setModello(product2.getModello());
		item2.setDettagli(product2.getTopDescrizione());
		item2.setQuantita(4);

		cart.addProduct(item1);
		cart.addProduct(item2);
		
		int quantityItem1 = 12;

		String action = "decreaseQuantity";
		String pid = "12";
		int pidInt = 12;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getParameter("prod_quantità")).thenReturn(String.valueOf(quantityItem1));
		
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		Carrello expectedCart = new Carrello();
		
		ItemCarrello updatedItem1 = new ItemCarrello();
		updatedItem1.setCodiceProdotto(product1.getCodiceProdotto());
		updatedItem1.setNomeProdotto(product1.getNomeProdotto());
		updatedItem1.setCategoria(product1.getCategoria());
		updatedItem1.setMarca(product1.getMarca());
		updatedItem1.setPrezzo(product1.getPrezzo());
		updatedItem1.setModello(product1.getModello());
		updatedItem1.setDettagli(product1.getTopDescrizione());
		updatedItem1.setQuantita(12);
		
		expectedCart.addProduct(updatedItem1);
		expectedCart.addProduct(item2);
		
		Carrello temp = new Carrello();

		ItemCarrello item1Temp = new ItemCarrello();
		item1Temp.setCodiceProdotto(product1.getCodiceProdotto());
		item1Temp.setNomeProdotto(product1.getNomeProdotto());
		item1Temp.setCategoria(product1.getCategoria());
		item1Temp.setMarca(product1.getMarca());
		item1Temp.setPrezzo(product1.getPrezzo());
		item1Temp.setModello(product1.getModello());
		item1Temp.setDettagli(product1.getTopDescrizione());
		item1Temp.setQuantita(25);
		
		temp.addProduct(item1Temp);
		temp.addProduct(item2);
		
		
		Carrello realCart = gc.decrementaQuantitaNelCarrello(temp, item1, quantityItem1);
		
		carrelloController.doGet(request, response);
		
		String jsonResponseString = "{\"updatedQuantity\":12,\"totalAmount\":\"5894,00\",\"updatedPrice\":\"5454,00\",\"message\":\"Quantità modificata nel carrello con successo\",\"status\":\"valid\"}";
		
		assertEquals(realCart, expectedCart);
		verify(response.getWriter()).write(jsonResponseString);
		verify(request.getSession()).setAttribute("usercart", cart);
		
	}
	
}
