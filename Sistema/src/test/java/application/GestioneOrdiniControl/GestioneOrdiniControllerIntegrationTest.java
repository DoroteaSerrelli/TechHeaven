package application.GestioneOrdiniControl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import application.GestioneCarrello.GestioneCarrelloService.Carrello;
import application.GestioneCarrello.GestioneCarrelloService.ItemCarrello;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneCatalogo.GestioneCatalogoService.*;
import application.GestioneOrdini.GestioneOrdiniControl.GestioneOrdiniController;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.Ordine;
import application.GestioneOrdini.GestioneOrdiniService.ProxyOrdine;
import application.GestioneOrdini.GestioneOrdiniService.ReportSpedizione;
import application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine.Stato;
import application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine.TipoConsegna;
import application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine.TipoSpedizione;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreSpedizioneOrdineException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.FormatoCorriereException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.FormatoImballaggioException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.FormatoQuantitaException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.MancanzaPezziException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.Navigazione.NavigazioneControl.PaginationUtils;
import application.Navigazione.NavigazioneService.*;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.Pagamento.PagamentoService.Pagamento;
import application.Pagamento.PagamentoService.PagamentoContrassegno;
import application.Pagamento.PagamentoService.PagamentoServiceImpl;
import application.Pagamento.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.Utente;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import storage.GestioneOrdiniDAO.*;
import storage.AutenticazioneDAO.*;


public class GestioneOrdiniControllerIntegrationTest {

	private GestioneOrdiniController ordiniController;
	private GestioneOrdiniServiceImpl gos;
	private PaginationUtils pu;
	private OrdineDAODataSource orderDAO; 
	private ProdottoDAODataSource productDAO;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	private PagamentoDAODataSource paymentDAO;
	
	private int pr_pagina;

	@BeforeEach
	public void setUp() throws ServletException, IOException {
		pr_pagina = 4;
		paymentDAO = mock(PagamentoDAODataSource.class);
		PhotoControl photoControl = mock(PhotoControl.class);
		UtenteDAODataSource userDAO = mock(UtenteDAODataSource.class);
		productDAO = mock(ProdottoDAODataSource.class);
		orderDAO = mock(OrdineDAODataSource.class);
		gos = new GestioneOrdiniServiceImpl(orderDAO, userDAO, productDAO, paymentDAO);
		GestioneCatalogoServiceImpl gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);
		NavigazioneServiceImpl ns = new NavigazioneServiceImpl(productDAO);

		pu = new PaginationUtils(ns, gcs, gos);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}


	/**
	 * TEST CASES PER PREPARAZIONE DI UN ORDINE ALLA SPEDIZIONE
	 * 
	 * TC12_1.1_1 : ordine da spedire, ordine presente nel database, 
	 * 				prodotti associati all'ordine selezionato,
	 * 				non sono disponibili per almeno un prodotto il
	 * 				numero di pezzi richiesti
	 * 
	 * TC12_1.1_2 : ordine da spedire, ordine presente nel database, 
	 * 				prodotti associati all'ordine selezionato,
	 * 				sono disponibili per tutti i prodotti il
	 * 				numero di pezzi richiesti, per almeno un prodotto
	 * 				la quantità specificata non è pari a quella 
	 * 				richiesta dal cliente
	 * 
	 * TC12_1.1_3 : ordine da spedire, ordine presente nel database, 
	 * 				prodotti associati all'ordine selezionato,
	 * 				sono disponibili per tutti i prodotti il
	 * 				numero di pezzi richiesti, per tutti i prodotti
	 * 				la quantità specificata è pari a quella 
	 * 				richiesta dal cliente, specifica dell'imballaggio
	 * 				non corretta nel formato
	 * 
	 * TC12_1.1_4 : ordine da spedire, ordine presente nel database, 
	 * 				prodotti associati all'ordine selezionato,
	 * 				sono disponibili per tutti i prodotti il
	 * 				numero di pezzi richiesti, per tutti i prodotti
	 * 				la quantità specificata è pari a quella 
	 * 				richiesta dal cliente, specifica dell'imballaggio
	 * 				corretta nel formato, specifica azienda di spedizione
	 * 				non corretta nel formato
	 * 
	 * TC12_1.1_5 : ordine da spedire, ordine presente nel database, 
	 * 				prodotti associati all'ordine selezionato,
	 * 				sono disponibili per tutti i prodotti il
	 * 				numero di pezzi richiesti, per tutti i prodotti
	 * 				la quantità specificata è pari a quella 
	 * 				richiesta dal cliente, specifica dell'imballaggio
	 * 				corretta nel formato, specifica azienda di spedizione
	 * 				corretta nel formato
	 * 
	 * */

	@Test
	public void testDoPost_TC12_1_1_1() throws OrdineVuotoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ServletException, IOException {

		ordiniController = new GestioneOrdiniController(productDAO, orderDAO, gos, pu);

		//dati ordine e prodotti
		int codiceOrdine = 23;
		Indirizzo indirizzoSpedizione = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		Cliente mickeyProfile = new Cliente("pippoemail@example.com", "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzoSpedizione);

		ProxyOrdine orderProxy = new ProxyOrdine(orderDAO, codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority);

		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true, productDAO);

		ItemCarrello p1 = new ItemCarrello();
		p1.setCodiceProdotto(product1.getCodiceProdotto());
		p1.setNomeProdotto(product1.getNomeProdotto());
		p1.setQuantita(80);

		ItemCarrello p2 = new ItemCarrello();
		p2.setCodiceProdotto(product2.getCodiceProdotto());
		p2.setNomeProdotto(product2.getNomeProdotto());
		p2.setQuantita(76);

		ArrayList<ItemCarrello> prodotti = new ArrayList<>();

		prodotti.add(p1);
		prodotti.add(p2);

		Ordine order = new Ordine(codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority, mickeyProfile, prodotti);

		String action = "complete_order";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);
		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(orderProxy);
		when(request.getSession().getAttribute("order_products")).thenReturn(prodotti);

		String[] productIds = {String.valueOf(p1.getCodiceProdotto()), String.valueOf(p2.getCodiceProdotto())};
		String[] itemAmounts = {String.valueOf(p1.getQuantita()), String.valueOf(p2.getQuantita())};
		when(request.getParameterValues("product_id[]")).thenReturn(productIds);
		when(request.getParameterValues("item_amount[]")).thenReturn(itemAmounts);

		String imballaggio = "Cartone, scotch";
		String corriere = "Spedizioni Damato Napoli";

		when(request.getParameter("Imballaggio")).thenReturn(imballaggio);
		when(request.getParameter("Corriere")).thenReturn(corriere);

		//loop 
		when(productDAO.doRetrieveProxyByKey(p1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(p2.getCodiceProdotto())).thenReturn(product2);



		ordiniController.doPost(request, response);

		String message = "Avviso importante!\n"
				+ "I pezzi del prodotto con codice 9 richiesti dal cliente non sono disponibili in magazzino."
				+ "\n\nContatta il cliente " + order.getAcquirente().getNome() + " " + order.getAcquirente().getCognome()
				+ " ai seguenti contatti: \n"
				+ "\n- Email: "+ order.getAcquirente().getEmail()
				+ "\n- Telefono: "+ order.getAcquirente().getTelefono()
				+ "\nPer poter procedere con una delle seguenti opzioni:\n "
				+ "- Ricevere il rimborso totale dell’ordine;\n"
				+ "- Ricevere il rimborso dei pezzi mancanti;\n"
				+ "- Attendere l’arrivo dei prodotti in negozio per poter soddisfare l’ordine.\n";

		verify(request.getSession()).setAttribute("error", message); 
		verify(response).sendRedirect(request.getContextPath()+"/GestioneOrdini");

	}


	@Test
	public void testDoPost_TC12_1_1_2() throws OrdineVuotoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ServletException, IOException {

		ordiniController = new GestioneOrdiniController(productDAO, orderDAO, gos, pu);

		//dati ordine e prodotti
		int codiceOrdine = 23;
		Indirizzo indirizzoSpedizione = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		Cliente mickeyProfile = new Cliente("pippoemail@example.com", "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzoSpedizione);

		ProxyOrdine orderProxy = new ProxyOrdine(orderDAO, codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority);

		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true, productDAO);

		ItemCarrello p1 = new ItemCarrello();
		p1.setCodiceProdotto(product1.getCodiceProdotto());
		p1.setNomeProdotto(product1.getNomeProdotto());
		p1.setQuantita(12);

		ItemCarrello p2 = new ItemCarrello();
		p2.setCodiceProdotto(product2.getCodiceProdotto());
		p2.setNomeProdotto(product2.getNomeProdotto());
		p2.setQuantita(7);

		ArrayList<ItemCarrello> prodotti = new ArrayList<>();

		prodotti.add(p1);
		prodotti.add(p2);

		Ordine order = new Ordine(codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority, mickeyProfile, prodotti);

		String action = "complete_order";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);
		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(orderProxy);
		when(request.getSession().getAttribute("order_products")).thenReturn(prodotti);

		String[] productIds = {String.valueOf(p1.getCodiceProdotto()), String.valueOf(p2.getCodiceProdotto())};
		String[] itemAmounts = {String.valueOf(28), String.valueOf(3)};
		when(request.getParameterValues("product_id[]")).thenReturn(productIds);
		when(request.getParameterValues("item_amount[]")).thenReturn(itemAmounts);

		String imballaggio = "Cartone, scotch";
		String corriere = "Spedizioni Damato Napoli";

		when(request.getParameter("Imballaggio")).thenReturn(imballaggio);
		when(request.getParameter("Corriere")).thenReturn(corriere);

		//loop 
		when(productDAO.doRetrieveProxyByKey(p1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(p2.getCodiceProdotto())).thenReturn(product2);



		ordiniController.doPost(request, response);

		String message = "Specificare la quantità del prodotto 9 pari a quella richiesta dal cliente.";

		verify(request.getSession()).setAttribute("error", message); 
		verify(response).sendRedirect(request.getContextPath()+"/fill_order_details");

	}

	@Test
	public void testDoPost_TC12_1_1_3() throws OrdineVuotoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ServletException, IOException {

		ordiniController = new GestioneOrdiniController(productDAO, orderDAO, gos, pu);

		//dati ordine e prodotti
		int codiceOrdine = 23;
		Indirizzo indirizzoSpedizione = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		Cliente mickeyProfile = new Cliente("pippoemail@example.com", "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzoSpedizione);

		ProxyOrdine orderProxy = new ProxyOrdine(orderDAO, codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority);

		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true, productDAO);

		ItemCarrello p1 = new ItemCarrello();
		p1.setCodiceProdotto(product1.getCodiceProdotto());
		p1.setNomeProdotto(product1.getNomeProdotto());
		p1.setQuantita(12);

		ItemCarrello p2 = new ItemCarrello();
		p2.setCodiceProdotto(product2.getCodiceProdotto());
		p2.setNomeProdotto(product2.getNomeProdotto());
		p2.setQuantita(7);

		ArrayList<ItemCarrello> prodotti = new ArrayList<>();

		prodotti.add(p1);
		prodotti.add(p2);

		Ordine order = new Ordine(codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority, mickeyProfile, prodotti);

		String action = "complete_order";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);
		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(orderProxy);
		when(request.getSession().getAttribute("order_products")).thenReturn(prodotti);

		String[] productIds = {String.valueOf(p1.getCodiceProdotto()), String.valueOf(p2.getCodiceProdotto())};
		String[] itemAmounts = {String.valueOf(12), String.valueOf(7)};
		when(request.getParameterValues("product_id[]")).thenReturn(productIds);
		when(request.getParameterValues("item_amount[]")).thenReturn(itemAmounts);

		String imballaggio = "";
		String corriere = "Spedizioni Damato Napoli";

		when(request.getParameter("Imballaggio")).thenReturn(imballaggio);
		when(request.getParameter("Corriere")).thenReturn(corriere);

		//loop 
		when(productDAO.doRetrieveProxyByKey(p1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(p2.getCodiceProdotto())).thenReturn(product2);



		ordiniController.doPost(request, response);

		String message = "Questo campo non deve essere vuoto";

		verify(request.getSession()).setAttribute("error", message); 
		verify(response).sendRedirect(request.getContextPath()+"/fill_order_details");

	}

	@Test
	public void testDoPost_TC12_1_1_4() throws OrdineVuotoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ServletException, IOException {

		ordiniController = new GestioneOrdiniController(productDAO, orderDAO, gos, pu);

		//dati ordine e prodotti
		int codiceOrdine = 23;
		Indirizzo indirizzoSpedizione = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		Cliente mickeyProfile = new Cliente("pippoemail@example.com", "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzoSpedizione);

		ProxyOrdine orderProxy = new ProxyOrdine(orderDAO, codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority);

		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true, productDAO);

		ItemCarrello p1 = new ItemCarrello();
		p1.setCodiceProdotto(product1.getCodiceProdotto());
		p1.setNomeProdotto(product1.getNomeProdotto());
		p1.setQuantita(12);

		ItemCarrello p2 = new ItemCarrello();
		p2.setCodiceProdotto(product2.getCodiceProdotto());
		p2.setNomeProdotto(product2.getNomeProdotto());
		p2.setQuantita(7);

		ArrayList<ItemCarrello> prodotti = new ArrayList<>();

		prodotti.add(p1);
		prodotti.add(p2);

		Ordine order = new Ordine(codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority, mickeyProfile, prodotti);

		String action = "complete_order";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);
		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(orderProxy);
		when(request.getSession().getAttribute("order_products")).thenReturn(prodotti);

		String[] productIds = {String.valueOf(p1.getCodiceProdotto()), String.valueOf(p2.getCodiceProdotto())};
		String[] itemAmounts = {String.valueOf(12), String.valueOf(7)};
		when(request.getParameterValues("product_id[]")).thenReturn(productIds);
		when(request.getParameterValues("item_amount[]")).thenReturn(itemAmounts);

		String imballaggio = "Cartone, scotch";
		String corriere = "<azienda> di spedizione 1234!";

		when(request.getParameter("Imballaggio")).thenReturn(imballaggio);
		when(request.getParameter("Corriere")).thenReturn(corriere);

		//loop 
		when(productDAO.doRetrieveProxyByKey(p1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(p2.getCodiceProdotto())).thenReturn(product2);



		ordiniController.doPost(request, response);

		String message = "L’azienda di spedizione deve essere composta da lettere e, eventualmente, spazi.";

		verify(request.getSession()).setAttribute("error", message); 
		verify(response).sendRedirect(request.getContextPath()+"/fill_order_details");
	}

	@Test
	public void testDoPost_TC12_1_1_5() throws OrdineVuotoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ServletException, IOException, MancanzaPezziException, FormatoQuantitaException, FormatoImballaggioException, FormatoCorriereException, CloneNotSupportedException, ModalitaAssenteException, ProdottoPresenteException, ProdottoNulloException, ErroreTipoSpedizioneException, ErroreSpedizioneOrdineException {

		ordiniController = new GestioneOrdiniController(productDAO, orderDAO, gos, pu);


		//Dati utente
		String username = "topolino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String errortelefono = "111 234 4444";
		
		Indirizzo indirizzoSpedizione = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 
		Cliente mickeyProfile = new Cliente("pippoemail@example.com", "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzoSpedizione);
		Utente userComplete = new Utente(username, password, mickeyProfile);

		//dati ordine e prodotti
		int codiceOrdine = 23;

		ProxyOrdine orderProxy = new ProxyOrdine(orderDAO, codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority);

		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true, productDAO);

		ItemCarrello p1 = new ItemCarrello();
		p1.setCodiceProdotto(product1.getCodiceProdotto());
		p1.setNomeProdotto(product1.getNomeProdotto());
		p1.setQuantita(12);

		ItemCarrello p2 = new ItemCarrello();
		p2.setCodiceProdotto(product2.getCodiceProdotto());
		p2.setNomeProdotto(product2.getNomeProdotto());
		p2.setQuantita(7);

		ArrayList<ItemCarrello> prodotti = new ArrayList<>();

		prodotti.add(p1);
		prodotti.add(p2);

		Ordine order = new Ordine(codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority, mickeyProfile, prodotti);

		String action = "complete_order";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);
		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(orderProxy);
		when(request.getSession().getAttribute("order_products")).thenReturn(prodotti);

		String[] productIds = {String.valueOf(p1.getCodiceProdotto()), String.valueOf(p2.getCodiceProdotto())};
		String[] itemAmounts = {String.valueOf(12), String.valueOf(7)};
		when(request.getParameterValues("product_id[]")).thenReturn(productIds);
		when(request.getParameterValues("item_amount[]")).thenReturn(itemAmounts);

		String imballaggio = "Cartone, scotch";
		String corriere = "Spedizioni Damato Napoli";

		when(request.getParameter("Imballaggio")).thenReturn(imballaggio);
		when(request.getParameter("Corriere")).thenReturn(corriere);

		ArrayList<Integer> quantities = new ArrayList<>();

		for(String quantity : itemAmounts)
			quantities.add(Integer.parseInt(quantity));


		when(productDAO.doRetrieveProxyByKey(p1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(p2.getCodiceProdotto())).thenReturn(product2);
		
		//Creazione report
		ReportSpedizione report = new ReportSpedizione(orderProxy.getCodiceOrdine(), corriere, imballaggio, order);
		
		ReportSpedizione realReport = gos.creaReportSpedizione(order, prodotti, quantities, imballaggio, corriere);
		
		//Creazione pagamento
		Carrello cart = new Carrello();
		cart.addProduct(p2);
		cart.addProduct(p1);
		String modPagamento = "CONTRASSEGNO";
		
		PagamentoContrassegno resultedPayment = (PagamentoContrassegno) gos.creaPagamento_PaypalContrassegno(cart, order, modPagamento);
		
		UtenteDAODataSource userDAO = mock(UtenteDAODataSource.class);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);
		
		//Si procede con la preparazione alla spedizione
		ArrayList<Prodotto> completeProducts = new ArrayList<>();
		Prodotto prodotto1 = new Prodotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false);

		Prodotto prodotto2 = new Prodotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true);

		completeProducts.add(prodotto2);
		completeProducts.add(prodotto1);
		
		when(orderDAO.doSaveToShip(order, report)).thenReturn(true);
		when(paymentDAO.doRetrieveCashByOrder(codiceOrdine)).thenReturn(resultedPayment);
		when(paymentDAO.doRetrievePaypalByOrder(codiceOrdine)).thenReturn(null);
		when(paymentDAO.doRetrieveCardByOrder(codiceOrdine)).thenReturn(null);
		
		Pagamento expectedPayment = PagamentoServiceImpl.createPagamentoOrdine(codiceOrdine, paymentDAO);

		//si decrementano le quantità dei prodotti richiesti
		int index = 0;
		for(ItemCarrello item : cart.getProducts()) {
			int expectedQuantity = completeProducts.get(index).getQuantita();

			Mockito.when(productDAO.doRetrieveCompleteByKey(item.getCodiceProdotto())).thenReturn(completeProducts.get(index));

			//aggiornamento quantità
			Mockito.when(productDAO.updateQuantity(0, expectedQuantity - item.getQuantita())).thenReturn(true);
			index++;
		}
		
		order.setStatoAsString("SPEDITO");
		
		ordiniController.doPost(request, response);

		assertEquals(realReport, report);
		assertEquals(resultedPayment, expectedPayment);
		verify(productDAO, Mockito.times(cart.getProducts().size())).updateQuantity(anyInt(), anyInt());
		verify(orderDAO).doSaveToShip(order, report);

		
		verify(request.getSession()).setAttribute("error", "Ordine Spedito Con Successo!");
		verify(request.getSession()).removeAttribute("proxy_ordine");                    
		verify(request.getSession()).removeAttribute("selected_ordine");
		verify(request.getSession()).removeAttribute("order_products");

		verify(response).sendRedirect(request.getContextPath() + "/GestioneOrdini"); 
	}

}
