package application.GestioneOrdiniControl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.QuantitaProdottoDisponibileException;
import application.GestioneCarrelloService.*;
import application.GestioneOrdiniService.*;
import application.GestioneOrdiniService.ObjectOrdine.Stato;
import application.GestioneOrdiniService.ObjectOrdine.TipoConsegna;
import application.GestioneOrdiniService.ObjectOrdine.TipoSpedizione;
import application.GestioneOrdiniService.OrdineException.FormatoCorriereException;
import application.GestioneOrdiniService.OrdineException.FormatoImballaggioException;
import application.GestioneOrdiniService.OrdineException.FormatoQuantitaException;
import application.GestioneOrdiniService.OrdineException.MancanzaPezziException;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Utente;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class GestioneOrdiniControllerTest {

	private GestioneOrdiniController ordiniController;
	private GestioneOrdiniServiceImpl gos;
	private OrdineDAODataSource orderDAO;
	private ProdottoDAODataSource productDAO;
	private PaginationUtils pu;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	private HttpSession session;
	private PrintWriter writer;


	@Test
	public void testDoPost_PezziRichiestiErroreAcceptOrder() throws Exception {
		productDAO = mock(ProdottoDAODataSource.class);
		gos = mock(GestioneOrdiniServiceImpl.class);
		orderDAO = mock(OrdineDAODataSource.class);
		pu = mock(PaginationUtils.class);

		ordiniController = new GestioneOrdiniController(gos, orderDAO, productDAO, pu);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		/*DATI CLIENTE*/
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));

		String email = "sabrina.30@gmail.com";
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 3, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(10);	//errore: dovrebbe essere minore o uguale a 3

		cart.addProduct(item1);
		cart.addProduct(item2);

		String idIndirizzo = "10";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());

		String action = "accept_order";
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("orderId")).thenReturn("20");

		when(orderDAO.doRetrieveAllOrderProducts(20)).thenReturn(cart.getProducts());
		ProxyOrdine proxyOrdine = new ProxyOrdine(orderDAO, order.getCodiceOrdine(), order.getStato(), selectedAddress, order.getSpedizione(), order.getConsegna());
		when(orderDAO.doRetrieveProxyByKey(20)).thenReturn(proxyOrdine);

		when(productDAO.doRetrieveProxyByKey(item1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(item2.getCodiceProdotto())).thenReturn(product2);


		// Esecuzione del metodo
		ordiniController.doPost(request, response);

		// Verifica che il proxy_ordine sia stato rimosso dalla sessione
		verify(request.getSession()).removeAttribute("proxy_ordine");

		verify(response).sendRedirect(request.getContextPath()+"/error_preparazioneOrdine");
	}


	@Test
	public void testDoPost_SuccessoAcceptOrder() throws Exception {
		productDAO = mock(ProdottoDAODataSource.class);
		gos = mock(GestioneOrdiniServiceImpl.class);
		orderDAO = mock(OrdineDAODataSource.class);
		pu = mock(PaginationUtils.class);

		ordiniController = new GestioneOrdiniController(gos, orderDAO, productDAO, pu);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		/*DATI CLIENTE*/
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));

		String email = "sabrina.30@gmail.com";
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 3, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());

		cart.addProduct(item1);
		cart.addProduct(item2);

		String idIndirizzo = "10";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());

		String action = "accept_order";
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("orderId")).thenReturn("20");

		when(orderDAO.doRetrieveAllOrderProducts(20)).thenReturn(cart.getProducts());
		ProxyOrdine proxyOrdine = new ProxyOrdine(orderDAO, order.getCodiceOrdine(), order.getStato(), selectedAddress, order.getSpedizione(), order.getConsegna());
		when(orderDAO.doRetrieveProxyByKey(20)).thenReturn(proxyOrdine);

		when(productDAO.doRetrieveProxyByKey(item1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(item2.getCodiceProdotto())).thenReturn(product2);
		when(proxyOrdine.mostraOrdine()).thenReturn(order);

		// Esecuzione del metodo
		ordiniController.doPost(request, response);

		// Verifica che il proxy_ordine sia stato impostato nella sessione
		verify(request.getSession()).setAttribute("proxy_ordine", proxyOrdine);
		verify(request.getSession()).setAttribute("selected_ordine", proxyOrdine.mostraOrdine());
		verify(request.getSession()).setAttribute("order_products", cart.getProducts());                  


		verify(response).sendRedirect(request.getContextPath() + "/fill_order_details");
	}

	@Test
	public void testDoPost_MancanzaPezziExceptionCompleteOrder() throws Exception {

		productDAO = mock(ProdottoDAODataSource.class);
		gos = mock(GestioneOrdiniServiceImpl.class);
		orderDAO = mock(OrdineDAODataSource.class);
		pu = mock(PaginationUtils.class);

		ordiniController = new GestioneOrdiniController(gos, orderDAO, productDAO, pu);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		/*DATI CLIENTE*/
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));

		String email = "sabrina.30@gmail.com";
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 3, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(8); //genererà MancanzaPezziException : 8 > 3

		cart.addProduct(item1);
		cart.addProduct(item2);

		String idIndirizzo = "10";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		ProxyOrdine proxyOrdine = new ProxyOrdine(orderDAO, order.getCodiceOrdine(), order.getStato(), selectedAddress, order.getSpedizione(), order.getConsegna());

		String action = "complete_order";
		when(request.getParameter("action")).thenReturn(action);

		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(proxyOrdine);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);

		when(request.getSession().getAttribute("order_products")).thenReturn(order.getProdotti());
		//when(request.getParameterValues("product_id")).thenReturn(new String()[]{"1"});
		when(request.getParameterValues("item_amount")).thenReturn(new String[]{"1", "8"});

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(8);
		String imballaggio = "Esempio di imballaggio";
		String corriere = "Esempio di corriere";

		
		// Mock del comportamento del servizio
		
		doThrow(new MancanzaPezziException("I pezzi del prodotto con codice " + item2.getCodiceProdotto() + " richiesti dal cliente non sono disponibili in magazzino."))
		.when(gos).creaReportSpedizione(any(), any(), any(), any(), any());
		
		ordiniController.doPost(request, response);

		String expectedExc = "Avviso importante!\n"
				+ "I pezzi del prodotto con codice " + item2.getCodiceProdotto() + " richiesti dal cliente non sono disponibili in magazzino." 
				+ "\n\nContatta il cliente " + order.getAcquirente().getNome() + " " + order.getAcquirente().getCognome()
				+ " ai seguenti contatti: \n"
				+ "\n- Email: "+ order.getAcquirente().getEmail()
				+ "\n- Telefono: "+ order.getAcquirente().getTelefono()
				+ "\nPer poter procedere con una delle seguenti opzioni:\n "
				+ "- Ricevere il rimborso totale dell’ordine;\n"
				+ "- Ricevere il rimborso dei pezzi mancanti;\n"
				+ "- Attendere l’arrivo dei prodotti in negozio per poter soddisfare l’ordine.\n";
		
		verify(request.getSession()).setAttribute("error", expectedExc); 
		verify(response).sendRedirect(request.getContextPath()+"/GestioneOrdini");

	}
	
	@Test
	public void testDoPost_FormatoQuantitaExceptionCompleteOrder() throws Exception {

		productDAO = mock(ProdottoDAODataSource.class);
		gos = mock(GestioneOrdiniServiceImpl.class);
		orderDAO = mock(OrdineDAODataSource.class);
		pu = mock(PaginationUtils.class);

		ordiniController = new GestioneOrdiniController(gos, orderDAO, productDAO, pu);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		/*DATI CLIENTE*/
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));

		String email = "sabrina.30@gmail.com";
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 3, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(-1); //genererà FormatoQuantitàException : -1 < 0

		cart.addProduct(item1);
		cart.addProduct(item2);

		String idIndirizzo = "10";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		ProxyOrdine proxyOrdine = new ProxyOrdine(orderDAO, order.getCodiceOrdine(), order.getStato(), selectedAddress, order.getSpedizione(), order.getConsegna());

		String action = "complete_order";
		when(request.getParameter("action")).thenReturn(action);

		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(proxyOrdine);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);

		when(request.getSession().getAttribute("order_products")).thenReturn(order.getProdotti());
		//when(request.getParameterValues("product_id")).thenReturn(new String()[]{"1"});
		when(request.getParameterValues("item_amount")).thenReturn(new String[]{"1", "-1"});

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(-1);
		String imballaggio = "Esempio di imballaggio";
		String corriere = "Esempio di corriere";

		
		// Mock del comportamento del servizio
		
		doThrow(new FormatoQuantitaException("Specificare la quantità del prodotto" + item2.getCodiceProdotto() +" pari a quella richiesta dal cliente."))
		.when(gos).creaReportSpedizione(any(), any(), any(), any(), any());
		
		ordiniController.doPost(request, response);

		FormatoQuantitaException expectedExc = new FormatoQuantitaException("Specificare la quantità del prodotto" + item2.getCodiceProdotto() +" pari a quella richiesta dal cliente.");
		verify(request.getSession()).setAttribute("error", expectedExc.getMessage()); 
		verify(response).sendRedirect(request.getContextPath()+"/GestioneOrdini");

	}
	
	@Test
	public void testDoPost_FormatoImballaggioExceptionCompleteOrder() throws Exception {

		productDAO = mock(ProdottoDAODataSource.class);
		gos = mock(GestioneOrdiniServiceImpl.class);
		orderDAO = mock(OrdineDAODataSource.class);
		pu = mock(PaginationUtils.class);

		ordiniController = new GestioneOrdiniController(gos, orderDAO, productDAO, pu);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		/*DATI CLIENTE*/
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));

		String email = "sabrina.30@gmail.com";
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 3, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(2);

		cart.addProduct(item1);
		cart.addProduct(item2);

		String idIndirizzo = "10";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		ProxyOrdine proxyOrdine = new ProxyOrdine(orderDAO, order.getCodiceOrdine(), order.getStato(), selectedAddress, order.getSpedizione(), order.getConsegna());

		String action = "complete_order";
		when(request.getParameter("action")).thenReturn(action);

		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(proxyOrdine);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);

		when(request.getSession().getAttribute("order_products")).thenReturn(order.getProdotti());
		//when(request.getParameterValues("product_id")).thenReturn(new String()[]{"1"});
		when(request.getParameterValues("item_amount")).thenReturn(new String[]{"1", "2"});

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(2);
		String imballaggio = "error%";
		String corriere = "Esempio di corriere";

		
		// Mock del comportamento del servizio
		
		doThrow(new FormatoImballaggioException("Questo campo non deve essere vuoto"))
		.when(gos).creaReportSpedizione(any(), any(), any(), any(), any());
		
		ordiniController.doPost(request, response);

		FormatoImballaggioException expectedExc = new FormatoImballaggioException("Questo campo non deve essere vuoto");
		
		verify(request.getSession()).setAttribute("error", expectedExc.getMessage()); 
		verify(response).sendRedirect(request.getContextPath()+"/GestioneOrdini");

	}
	
	@Test
	public void testDoPost_FormatoCorriereExceptionCompleteOrder() throws Exception {

		productDAO = mock(ProdottoDAODataSource.class);
		gos = mock(GestioneOrdiniServiceImpl.class);
		orderDAO = mock(OrdineDAODataSource.class);
		pu = mock(PaginationUtils.class);

		ordiniController = new GestioneOrdiniController(gos, orderDAO, productDAO, pu);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		/*DATI CLIENTE*/
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));

		String email = "sabrina.30@gmail.com";
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 3, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(2); 

		cart.addProduct(item1);
		cart.addProduct(item2);

		String idIndirizzo = "10";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		ProxyOrdine proxyOrdine = new ProxyOrdine(orderDAO, order.getCodiceOrdine(), order.getStato(), selectedAddress, order.getSpedizione(), order.getConsegna());

		String action = "complete_order";
		when(request.getParameter("action")).thenReturn(action);

		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(proxyOrdine);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);

		when(request.getSession().getAttribute("order_products")).thenReturn(order.getProdotti());
		//when(request.getParameterValues("product_id")).thenReturn(new String()[]{"1"});
		when(request.getParameterValues("item_amount")).thenReturn(new String[]{"1", "8"});

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(2);
		String imballaggio = "Esempio imballaggio";
		String corriere = "error&corriere";

		
		// Mock del comportamento del servizio
		
		doThrow(new FormatoCorriereException("L’azienda di spedizione deve essere composta da lettere e, eventualmente, spazi."))
		.when(gos).creaReportSpedizione(any(), any(), any(), any(), any());
		
		ordiniController.doPost(request, response);

		FormatoCorriereException expectedExc = new FormatoCorriereException("L’azienda di spedizione deve essere composta da lettere e, eventualmente, spazi.");
		
		
		verify(request.getSession()).setAttribute("error", expectedExc.getMessage()); 
		verify(response).sendRedirect(request.getContextPath()+"/GestioneOrdini");

	}


	@Test
	public void testDoPost_SuccessoCompleteOrder() throws Exception {

		productDAO = mock(ProdottoDAODataSource.class);
		gos = mock(GestioneOrdiniServiceImpl.class);
		orderDAO = mock(OrdineDAODataSource.class);
		pu = mock(PaginationUtils.class);

		ordiniController = new GestioneOrdiniController(gos, orderDAO, productDAO, pu);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		/*DATI CLIENTE*/
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));

		String email = "sabrina.30@gmail.com";
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 3, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());

		cart.addProduct(item1);
		cart.addProduct(item2);

		String idIndirizzo = "10";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		ProxyOrdine proxyOrdine = new ProxyOrdine(orderDAO, order.getCodiceOrdine(), order.getStato(), selectedAddress, order.getSpedizione(), order.getConsegna());

		String action = "complete_order";
		when(request.getParameter("action")).thenReturn(action);

		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(proxyOrdine);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);

		when(request.getSession().getAttribute("order_products")).thenReturn(order.getProdotti());
		//when(request.getParameterValues("product_id")).thenReturn(new String()[]{"1"});
		when(request.getParameterValues("item_amount")).thenReturn(new String[]{"1", "1"});

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(1);
		String imballaggio = "Esempio di imballaggio";
		String corriere = "Esempio di corriere";

		ReportSpedizione report = new ReportSpedizione(1, corriere, imballaggio, order);
		Ordine shippedOrder = new Ordine(20, ObjectOrdine.Stato.Spedito, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());

		// Mock del comportamento del servizio
		when(gos.creaReportSpedizione(order, cart.getProducts(), quantities, imballaggio, corriere)).thenReturn(report);
		when(gos.preparazioneSpedizioneOrdine(order, report)).thenReturn(shippedOrder);

		// Esecuzione del metodo
		ordiniController.doPost(request, response);

		// Verifica che sia stata fatta la chiamata corretta e che l'ordine sia stato aggiornato

		assertEquals(report, gos.creaReportSpedizione(order, cart.getProducts(), quantities, imballaggio, corriere));
		assertEquals(gos.preparazioneSpedizioneOrdine(order, report), shippedOrder);
		verify(request.getSession()).removeAttribute("proxy_ordine");                    
		verify(request.getSession()).removeAttribute("selected_ordine");
		verify(request.getSession()).removeAttribute("order_products");
		verify(request.getSession()).setAttribute("error", "Ordine Spedito Con Successo!");
		verify(response).sendRedirect(request.getContextPath() + "/GestioneOrdini");
	}


}
