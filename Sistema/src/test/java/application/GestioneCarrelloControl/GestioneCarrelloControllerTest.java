package application.GestioneCarrelloControl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.GestioneCarrelloServiceImpl;
import application.GestioneCarrelloService.ItemCarrello;
import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneCarrelloService.CarrelloException.QuantitaProdottoException;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneCarrelloControllerTest {

	private GestioneCarrelloController carrelloController;
	private GestioneCarrelloServiceImpl gc;
	private ProdottoDAODataSource productDAO;

	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@BeforeEach
	public void setUp() throws ServletException, IOException {

		productDAO = mock(ProdottoDAODataSource.class);
		gc = mock(GestioneCarrelloServiceImpl.class);

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


		String action = "addToCart";
		String productId = "16";
		int productIdInt = 16;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(null);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(productIdInt)).thenReturn(product);
		when(request.getSession().getAttribute("usercart")).thenReturn(null);

		when(gc.aggiungiAlCarrello(any(), any())).thenThrow(new QuantitaProdottoException("Non è disponibile il prodotto per l’acquisto"));

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponse = "{\"updatedQuantity\":0,\"totalAmount\":\"0,00\",\"updatedPrice\":\"0,00\",\"message\":\"Non è disponibile il prodotto per l’acquisto\",\"status\":\"invalid\"}";

		// Verifica che il metodo per preparare la risposta JSON venga chiamato con il messaggio corretto
		verify(response.getWriter()).write(jsonResponse);

	}

	@Test
	public void testDoGet_TC9_1_1_2() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		Carrello cart = mock(Carrello.class);

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
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);



		when(gc.aggiungiAlCarrello(any(), any())).thenReturn(cart);
		when(cart.totalAmount()).thenReturn(454.50);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponse = "{\"updatedQuantity\":1,\"totalAmount\":\"454,50\",\"updatedPrice\":\"454,50\",\"message\":\"Prodotto aggiunto nel carrello con successo\",\"status\":\"valid\"}";

		// Verifica che il metodo per preparare la risposta JSON venga chiamato con il messaggio corretto
		verify(response.getWriter()).write(jsonResponse);
		verify(request.getSession()).setAttribute("error", "Prodotto aggiunto nel carrello con successo");
		verify(request.getSession()).setAttribute("status", "valid");

	}

	@Test
	public void testDoGet_TC9_1_1_3() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		Carrello cart = mock(Carrello.class);

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova",  "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(25, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

		ArrayList<ItemCarrello> itemsCart = new ArrayList<>();
		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		itemsCart.add(item1);

		when(cart.getProducts()).thenReturn(itemsCart);
		when(cart.getNumProdotti()).thenReturn(1);

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

		when(gc.aggiungiAlCarrello(any(), any())).thenThrow(new QuantitaProdottoException("Non è disponibile il prodotto per l’acquisto"));

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponse = "{\"updatedQuantity\":0,\"totalAmount\":\"0,00\",\"updatedPrice\":\"0,00\",\"message\":\"Non è disponibile il prodotto per l’acquisto\",\"status\":\"invalid\"}";

		// Verifica che il metodo per preparare la risposta JSON venga chiamato con il messaggio corretto
		verify(response.getWriter()).write(jsonResponse);

	}

	@Test
	public void testDoGet_TC9_1_1_4() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		Carrello cart = mock(Carrello.class);

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ArrayList<ItemCarrello> itemsCart = new ArrayList<>();
		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		itemsCart.add(item1);

		when(cart.getProducts()).thenReturn(itemsCart);
		when(cart.getNumProdotti()).thenReturn(1);

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

		when(gc.aggiungiAlCarrello(cart, inCart)).thenThrow(new ProdottoPresenteException("Prodotto già presente nel carrello"));

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponse = "{\"updatedQuantity\":0,\"totalAmount\":\"0,00\",\"updatedPrice\":\"0,00\",\"message\":\"Prodotto già presente nel carrello\",\"status\":\"invalid\"}";

		// Verifica che il metodo per preparare la risposta JSON venga chiamato con il messaggio corretto
		verify(response.getWriter()).write(jsonResponse);
		verify(response).sendRedirect(request.getContextPath() + "/cart");

	}

	@Test
	public void testDoGet_TC9_1_1_5() throws IOException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException, ServletException {

		carrelloController = new GestioneCarrelloController(productDAO, gc);

		Carrello cart = mock(Carrello.class);

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(10, "Bosch lavatrice a carica frontale", "Prova", "Prova", Float.parseFloat("590.50"), 
				Categoria.GRANDI_ELETTRODOMESTICI, "Bosch", "QualcheModello", 112, true, false, productDAO);

		ArrayList<ItemCarrello> itemsCart = new ArrayList<>();

		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		itemsCart.add(item1);

		when(cart.getProducts()).thenReturn(itemsCart);
		when(cart.getNumProdotti()).thenReturn(1);

		ItemCarrello inCart = new ItemCarrello();
		inCart.setCodiceProdotto(product1.getCodiceProdotto());
		inCart.setNomeProdotto(product1.getNomeProdotto());
		inCart.setCategoria(product1.getCategoria());
		inCart.setMarca(product1.getMarca());
		inCart.setPrezzo(product1.getPrezzo());
		inCart.setModello(product1.getModello());
		inCart.setDettagli(product1.getTopDescrizione());


		String action = "addToCart";
		String productId = "10";
		int productIdInt = 10;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(null);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(productIdInt)).thenReturn(product2);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		when(gc.aggiungiAlCarrello(any(), any())).thenReturn(cart);
		when(cart.totalAmount()).thenReturn((double) 1045);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponse = "{\"updatedQuantity\":1,\"totalAmount\":\"1045,00\",\"updatedPrice\":\"590,50\",\"message\":\"Prodotto aggiunto nel carrello con successo\",\"status\":\"valid\"}";

		// Verifica che il metodo per preparare la risposta JSON venga chiamato con il messaggio corretto
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
		
		Carrello cart = mock(Carrello.class);

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(13, "Amazfit T-Rex 2", "Prova", "Prova", Float.parseFloat("160.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Amazfit", "T-Rex2", 10, true, false, productDAO);

		ArrayList<ItemCarrello> itemsCart = new ArrayList<>();

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

		itemsCart.add(item1);
		itemsCart.add(item2);


		String action = "removeFromCart";
		String pid = "13";
		int pidInt = 13;
		HashMap<Integer, Integer> hs = new HashMap<>();

		hs.put(13, 10); //id AmazFit, quantità AmazFit

		when(cart.getProducts()).thenReturn(itemsCart);
		when(cart.getNumProdotti()).thenReturn(2);
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getSession().getAttribute("products_available_inStock")).thenReturn(hs);
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product2);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		
		ArrayList<ItemCarrello> itemsCartUpdated = new ArrayList<>();

		itemsCartUpdated.add(item2);

		when(cart.isPresent(item2)).thenReturn(true);
		when(gc.rimuoviDalCarrello(cart, item2)).thenReturn(cart);
		when(cart.totalAmount()).thenReturn(454.50);
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		carrelloController.doGet(request, response);

		String jsonResponseString = "{\"updatedQuantity\":0,\"totalAmount\":\"454,50\",\"updatedPrice\":\"0,00\",\"message\":\"Prodotto rimosso con successo dal carrello\",\"status\":\"valid\"}";

		hs.remove(13);
		
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
		
		Carrello cart = mock(Carrello.class);

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);

		ArrayList<ItemCarrello> itemsCart = new ArrayList<>();

		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setCategoria(product1.getCategoria());
		item1.setMarca(product1.getMarca());
		item1.setPrezzo(product1.getPrezzo());
		item1.setModello(product1.getModello());
		item1.setDettagli(product1.getTopDescrizione());

		ItemCarrello item2 = new ItemCarrello();	//item da rimuovere
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setCategoria(product2.getCategoria());
		item2.setMarca(product2.getMarca());
		item2.setPrezzo(product2.getPrezzo());
		item2.setModello(product2.getModello());
		item2.setDettagli(product2.getTopDescrizione());
		item2.setQuantita(4);

		itemsCart.add(item1);
		itemsCart.add(item2);
		
		int quantityItem1 = 180;


		String action = "increaseQuantity";
		String pid = "12";
		int pidInt = 12;

		when(cart.getProducts()).thenReturn(itemsCart);
		when(cart.getNumProdotti()).thenReturn(5);
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getParameter("prod_quantità")).thenReturn(String.valueOf(quantityItem1));
		
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(cart.isPresent(item1)).thenReturn(true);
		
		when(gc.aumentaQuantitaNelCarrello(cart, item1, quantityItem1)).thenThrow(new QuantitaProdottoException("La quantità specificata supera il numero di scorte possibili del prodotto in magazzino."));
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		when(cart.totalAmount()).thenReturn(894.50);
		
		carrelloController.doGet(request, response);
		
		String jsonResponseString = "{\"updatedQuantity\":0,\"totalAmount\":\"894,50\",\"updatedPrice\":\"0,00\",\"message\":\"La quantità specificata supera il numero di scorte possibili del prodotto in magazzino.\",\"status\":\"invalid\"}";
		
		verify(response.getWriter()).write(jsonResponseString);
		verify(response).sendRedirect(request.getContextPath() + "/cart");
		
	}
	
	@Test
	public void testDoGet_TC9_3_1_2() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, IOException, ServletException, QuantitaProdottoException {
		
		carrelloController = new GestioneCarrelloController(productDAO, gc);
		
		Carrello cart = mock(Carrello.class);

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);

		ArrayList<ItemCarrello> itemsCart = new ArrayList<>();

		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setCategoria(product1.getCategoria());
		item1.setMarca(product1.getMarca());
		item1.setPrezzo(product1.getPrezzo());
		item1.setModello(product1.getModello());
		item1.setDettagli(product1.getTopDescrizione());

		ItemCarrello item2 = new ItemCarrello();	//item da rimuovere
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setCategoria(product2.getCategoria());
		item2.setMarca(product2.getMarca());
		item2.setPrezzo(product2.getPrezzo());
		item2.setModello(product2.getModello());
		item2.setDettagli(product2.getTopDescrizione());
		item2.setQuantita(4);

		itemsCart.add(item1);
		itemsCart.add(item2);
		
		int quantityItem1 = 40;


		String action = "increaseQuantity";
		String pid = "12";
		int pidInt = 12;

		when(cart.getProducts()).thenReturn(itemsCart);
		when(cart.getNumProdotti()).thenReturn(5);
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getParameter("prod_quantità")).thenReturn(String.valueOf(quantityItem1));
		
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(cart.isPresent(item1)).thenReturn(true);
		
		when(gc.aumentaQuantitaNelCarrello(cart, item1, quantityItem1)).thenReturn(cart);
		when(cart.totalAmount()).thenReturn(40*454.50 + 4*110.00);
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		
		carrelloController.doGet(request, response);
		
		String jsonResponseString = "{\"updatedQuantity\":40,\"totalAmount\":\"18620,00\",\"updatedPrice\":\"18180,00\",\"message\":\"Quantità modificata nel carrello con successo\",\"status\":\"valid\"}";
		
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
		
		Carrello cart = mock(Carrello.class);

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);

		ArrayList<ItemCarrello> itemsCart = new ArrayList<>();

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

		itemsCart.add(item1);
		itemsCart.add(item2);
		
		int quantityItem1 = 40;


		String action = "decreaseQuantity";
		String pid = "12";
		int pidInt = 12;

		when(cart.getProducts()).thenReturn(itemsCart);
		when(cart.getNumProdotti()).thenReturn(5);
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getParameter("prod_quantità")).thenReturn(String.valueOf(quantityItem1));
		
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(cart.isPresent(item1)).thenReturn(true);
		
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		when(cart.totalAmount()).thenReturn(110*4+25*454.50);
		
		carrelloController.doGet(request, response);
		
		
		String jsonResponseString = "{\"updatedQuantity\":0,\"totalAmount\":\"11802,50\",\"updatedPrice\":\"0,00\",\"message\":\"La quantità inserita non è minore della quantità del prodotto nel carrello\",\"status\":\"invalid\"}";
		
		verify(response.getWriter()).write(jsonResponseString);
		verify(response).sendRedirect(request.getContextPath() + "/cart");
		
	}
	
	@Test
	public void testDoGet_TC9_4_1_2() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, IOException, ServletException, QuantitaProdottoException {
		
		carrelloController = new GestioneCarrelloController(productDAO, gc);
		
		Carrello cart = mock(Carrello.class);

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);

		ArrayList<ItemCarrello> itemsCart = new ArrayList<>();

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

		itemsCart.add(item1);
		itemsCart.add(item2);
		
		int quantityItem1 = 12;


		String action = "decreaseQuantity";
		String pid = "12";
		int pidInt = 12;

		when(cart.getProducts()).thenReturn(itemsCart);
		when(cart.getNumProdotti()).thenReturn(5);
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(pid);
		when(request.getParameter("prod_quantità")).thenReturn(String.valueOf(quantityItem1));
		
		when(productDAO.doRetrieveProxyByKey(pidInt)).thenReturn(product1);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(cart.isPresent(item1)).thenReturn(true);
		
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(gc.decrementaQuantitaNelCarrello(cart, item1, quantityItem1)).thenReturn(cart);
		
		when(cart.totalAmount()).thenReturn(110*4+12*454.50);
		
		carrelloController.doGet(request, response);
		
		
		String jsonResponseString = "{\"updatedQuantity\":12,\"totalAmount\":\"5894,00\",\"updatedPrice\":\"5454,00\",\"message\":\"Quantità modificata nel carrello con successo\",\"status\":\"valid\"}";
		
		verify(response.getWriter()).write(jsonResponseString);
		verify(request.getSession()).setAttribute("usercart", cart);
		
	}
	
}
