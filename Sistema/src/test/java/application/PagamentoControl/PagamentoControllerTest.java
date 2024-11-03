package application.PagamentoControl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.GestioneCarrello.GestioneCarrelloService.Carrello;
import application.GestioneCarrello.GestioneCarrelloService.ItemCarrello;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine;
import application.GestioneOrdini.GestioneOrdiniService.Ordine;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoConsegnaException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.IndirizzoSpedizioneNulloException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.Pagamento.PagamentoControl.PagamentoController;
import application.Pagamento.PagamentoService.PagamentoCartaCredito;
import application.Pagamento.PagamentoService.PagamentoContrassegno;
import application.Pagamento.PagamentoService.PagamentoPaypal;
import application.Pagamento.PagamentoService.PagamentoServiceImpl;
import application.Pagamento.PagamentoService.PagamentoException.FormatoCVVCartaException;
import application.Pagamento.PagamentoService.PagamentoException.FormatoDataCartaException;
import application.Pagamento.PagamentoService.PagamentoException.FormatoNumeroCartaException;
import application.Pagamento.PagamentoService.PagamentoException.FormatoTitolareCartaException;
import application.Pagamento.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.Utente;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class PagamentoControllerTest {

	private PagamentoController pagamentoController;
	private GestioneOrdiniServiceImpl gos;
	private PagamentoServiceImpl ps;
	private ProdottoDAODataSource productDAO;
	private UtenteDAODataSource userDAO;

	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@BeforeEach
	public void setUp() throws IOException {
		gos = mock(GestioneOrdiniServiceImpl.class);
		productDAO = mock(ProdottoDAODataSource.class);
		userDAO = mock(UtenteDAODataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}



	/**
	 * TEST CASES PER ACQUISTO PRODOTTI NEL CARRELLO (CHECK-OUT CARRELLO) - CON CARTA DI CREDITO
	 * 
	 * TC11_1.1_1 : il carrello non è vuoto, l'indirizzo di spedizione non è stato
	 * 				specificato.
	 * 
	 * TC11_1.1_2 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine non è stata specificata correttamente.
	 * 
	 * TC11_1.1_3 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna non è stata indicata correttamente.
	 * 
	 * TC11_1.1_4 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				non si è specificato il metodo di pagamento.
	 * 
	 * TC11_1.1_5 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				si è specificato il metodo di pagamento == CARTA_CREDITO,
	 * 				si è fornito il titolare della carta nel formato non corretto.
	 * 
	 * TC11_1.1_6 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				si è specificato il metodo di pagamento == CARTA_CREDITO,
	 * 				si è fornito il titolare della carta nel formato corretto,
	 * 				il numero della carta è stato indicato nel formato errato.
	 * 
	 * TC11_1.1_7 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				si è specificato il metodo di pagamento == CARTA_CREDITO,
	 * 				si è fornito il titolare della carta nel formato corretto,
	 * 				il numero della carta è stato indicato nel formato corretto,
	 * 				è stata specificata una data di scadenza non valida.
	 * 
	 * TC11_1.1_8 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				si è specificato il metodo di pagamento == CARTA_CREDITO,
	 * 				si è fornito il titolare della carta nel formato corretto,
	 * 				il numero della carta è stato indicato nel formato corretto,
	 * 				è stata specificata una data di scadenza valida,
	 * 				il numero CVV indicato è espresso nel formato errato.
	 * 
	 * TC11_1.1_9 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				si è specificato il metodo di pagamento == CARTA_CREDITO,
	 * 				si è fornito il titolare della carta nel formato corretto,
	 * 				il numero della carta è stato indicato nel formato corretto,
	 * 				è stata specificata una data di scadenza valida,
	 * 				il numero CVV indicato è espresso nel formato corretto.
	 * 
	 * 
	 * */

	@Test
	public void testDoPost_TC11_1_1_4() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IOException, ServletException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		pagamentoController.doPost(request, response);

		ModalitaAssenteException ex = new ModalitaAssenteException("Specificare la modalità di pagamento: carta di credito, Paypal, contrassegno.");  

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento");  


	}

	@Test
	public void testDoPost_TC11_1_1_5() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IOException, ServletException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabry56";
		String noCard = "0000001111223456";
		String expiredDate = "02-25";
		String CVV = "123";


		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		order.setData(LocalDate.now());
		order.setOra(LocalTime.now());

		when(request.getSession().getAttribute("preview_order")).thenReturn(order);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("titolare")).thenReturn(titolare);
		when(request.getParameter("cc_number")).thenReturn(noCard);
		when(request.getParameter("cc_expiry")).thenReturn(expiredDate);
		when(request.getParameter("cc_cvc")).thenReturn(CVV);

		pagamentoController.doPost(request, response);

		assertThrows(FormatoTitolareCartaException.class, () -> {
			PagamentoCartaCredito.checkValidate(titolare, noCard, expiredDate, CVV);
		});

		FormatoTitolareCartaException ex = new FormatoTitolareCartaException("Il titolare deve essere una sequenza di lettere e spazi.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento");  

	}

	@Test
	public void testDoPost_TC11_1_1_6() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IOException, ServletException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabrina Ferro";
		String noCard = "11122345error6";
		String expiredDate = "02-25";
		String CVV = "123";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		order.setData(LocalDate.now());
		order.setOra(LocalTime.now());

		when(request.getSession().getAttribute("preview_order")).thenReturn(order);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("titolare")).thenReturn(titolare);
		when(request.getParameter("cc_number")).thenReturn(noCard);
		when(request.getParameter("cc_expiry")).thenReturn(expiredDate);
		when(request.getParameter("cc_cvc")).thenReturn(CVV);

		pagamentoController.doPost(request, response);

		assertThrows(FormatoNumeroCartaException.class, () -> {
			PagamentoCartaCredito.checkValidate(titolare, noCard, expiredDate, CVV);
		});

		FormatoNumeroCartaException ex = new FormatoNumeroCartaException("Il numero della carta è formato da 16 numeri.");
		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento");  
	}

	@Test
	public void testDoPost_TC11_1_1_7() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IOException, ServletException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabrina Ferro";
		String noCard = "1112234890999945";
		String expiredDate = "17/29";
		String CVV = "123";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		order.setData(LocalDate.now());
		order.setOra(LocalTime.now());

		when(request.getSession().getAttribute("preview_order")).thenReturn(order);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("titolare")).thenReturn(titolare);
		when(request.getParameter("cc_number")).thenReturn(noCard);
		when(request.getParameter("cc_expiry")).thenReturn(expiredDate);
		when(request.getParameter("cc_cvc")).thenReturn(CVV);

		pagamentoController.doPost(request, response);

		assertThrows(FormatoDataCartaException.class, () -> {
			PagamentoCartaCredito.checkValidate(titolare, noCard, expiredDate, CVV);
		});

		FormatoDataCartaException ex = new FormatoDataCartaException("La data di scadenza della carta non è valida.");
		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento"); 

	}

	@Test
	public void testDoPost_TC11_1_1_8() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IOException, ServletException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabrina Ferro";
		String noCard = "1112234890999945";
		String expiredDate = "12/24";
		String CVV = "3error";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		order.setData(LocalDate.now());
		order.setOra(LocalTime.now());

		when(request.getSession().getAttribute("preview_order")).thenReturn(order);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("titolare")).thenReturn(titolare);
		when(request.getParameter("cc_number")).thenReturn(noCard);
		when(request.getParameter("cc_expiry")).thenReturn(expiredDate);
		when(request.getParameter("cc_cvc")).thenReturn(CVV);

		pagamentoController.doPost(request, response);

		assertThrows(FormatoCVVCartaException.class, () -> {
			PagamentoCartaCredito.checkValidate(titolare, noCard, expiredDate, CVV);
		});

		FormatoCVVCartaException ex = new FormatoCVVCartaException("Il numero CVV è formato da 3 numeri.");
		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento"); 

	}

	@Test
	public void testDoPost_TC11_1_1_9() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, ModalitaAssenteException, FormatoCVVCartaException, FormatoDataCartaException, FormatoTitolareCartaException, FormatoNumeroCartaException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, IOException, ServletException, ProdottoNonPresenteException, CarrelloVuotoException, CloneNotSupportedException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabrina Ferro";
		String noCard = "1112234890999945";
		String expiredDate = "12/24";
		String CVV = "312";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		order.setData(LocalDate.now());
		order.setOra(LocalTime.now());

		when(request.getSession().getAttribute("preview_order")).thenReturn(order);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("titolare")).thenReturn(titolare);
		when(request.getParameter("cc_number")).thenReturn(noCard);
		when(request.getParameter("cc_expiry")).thenReturn(expiredDate);
		when(request.getParameter("cc_cvc")).thenReturn(CVV);

		when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(user);

		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenReturn(order);


		PagamentoCartaCredito resultedPayment = new PagamentoCartaCredito();
		resultedPayment.setCodicePagamento(0);
		resultedPayment.setNumeroCarta(noCard);
		resultedPayment.setTitolare(titolare);
		resultedPayment.setDataPagamento(LocalDate.now());
		resultedPayment.setOraPagamento(LocalTime.now());
		resultedPayment.setImporto((float) cart.totalAmount());

		Carrello cartEmpty = new Carrello();

		when(gos.commissionaOrdine(cart, order, resultedPayment, user)).thenReturn(cartEmpty);


		pagamentoController.doPost(request, response);


		verify(request.getSession()).removeAttribute("usercart");
		verify(request.getSession()).removeAttribute("preview_order"); 
		verify(response).sendRedirect(request.getContextPath()+"/SuccessoPagamento");

	}


	/**
	 * TEST CASES PER ACQUISTO PRODOTTI NEL CARRELLO (CHECK-OUT CARRELLO) - CON PAYPAL
	 * 
	 * TC11_2.1_1 : il carrello non è vuoto, l'indirizzo di spedizione non è stato
	 * 				specificato.
	 * 
	 * TC11_2.1_2 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine non è stata specificata correttamente.
	 * 
	 * TC11_2.1_3 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna non è stata indicata correttamente.
	 * 
	 * TC11_2.1_4 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				non si è specificato il metodo di pagamento.
	 * 
	 * TC11_2.1_5 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				si è specificato il metodo di pagamento == PAYPAL.
	 * 
	 * */

	@Test
	public void testDoPost_TC11_2_1_4() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IOException, ServletException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		pagamentoController.doPost(request, response);

		ModalitaAssenteException ex = new ModalitaAssenteException("Specificare la modalità di pagamento: carta di credito, Paypal, contrassegno.");  

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento");  


	}

	@Test
	public void testDoPost_TC11_2_1_5() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, ModalitaAssenteException, FormatoCVVCartaException, FormatoDataCartaException, FormatoTitolareCartaException, FormatoNumeroCartaException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, IOException, ServletException, ProdottoNonPresenteException, CarrelloVuotoException, CloneNotSupportedException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "PAYPAL";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		order.setData(LocalDate.now());
		order.setOra(LocalTime.now());

		when(request.getSession().getAttribute("preview_order")).thenReturn(order);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(user);

		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenReturn(order);

		PagamentoPaypal resultedPayment = new PagamentoPaypal();
		resultedPayment.setCodicePagamento(0);
		resultedPayment.setDataPagamento(LocalDate.now());
		resultedPayment.setOraPagamento(LocalTime.now());
		resultedPayment.setImporto((float) cart.totalAmount());

		Carrello cartEmpty = new Carrello();

		when(gos.commissionaOrdine(cart, order, resultedPayment, user)).thenReturn(cartEmpty);


		pagamentoController.doPost(request, response);


		verify(request.getSession()).removeAttribute("usercart");
		verify(request.getSession()).removeAttribute("preview_order"); 
		verify(response).sendRedirect(request.getContextPath()+"/SuccessoPagamento");

	}

	/**
	 * TEST CASES PER ACQUISTO PRODOTTI NEL CARRELLO (CHECK-OUT CARRELLO) - CON CONTRASSEGNO
	 * 
	 * TC11_3.1_1 : il carrello non è vuoto, l'indirizzo di spedizione non è stato
	 * 				specificato.
	 * 
	 * TC11_3.1_2 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine non è stata specificata correttamente.
	 * 
	 * TC11_3.1_3 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna non è stata indicata correttamente.
	 * 
	 * TC11_3.1_4 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				non si è specificato il metodo di pagamento.
	 * 
	 * TC11_3.1_5 : il carrello non è vuoto, l'indirizzo di spedizione è stato specificato,
	 * 				la modalità di spedizione dell'ordine è stata indicata,
	 * 				la modalità di consegna è stata fornita,
	 * 				si è specificato il metodo di pagamento == PAYPAL.
	 * 
	 * */


	@Test
	public void testDoPost_TC11_3_1_4() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IOException, ServletException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		pagamentoController.doPost(request, response);

		ModalitaAssenteException ex = new ModalitaAssenteException("Specificare la modalità di pagamento: carta di credito, Paypal, contrassegno.");  

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento");  


	}


	@Test
	public void testDoPost_TC11_3_1_5() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, ModalitaAssenteException, FormatoCVVCartaException, FormatoDataCartaException, FormatoTitolareCartaException, FormatoNumeroCartaException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, IOException, ServletException, ProdottoNonPresenteException, CarrelloVuotoException, CloneNotSupportedException {

		pagamentoController = new PagamentoController(gos, ps);

		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>(), userDAO); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

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
		String action = "confirmPayment";
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CONTRASSEGNO";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		when(request.getParameter("metodoPagamento")).thenReturn(modPagamento);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		order.setData(LocalDate.now());
		order.setOra(LocalTime.now());

		when(request.getSession().getAttribute("preview_order")).thenReturn(order);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);

		when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(user);

		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenReturn(order);

		PagamentoContrassegno resultedPayment = new PagamentoContrassegno();
		resultedPayment.setCodicePagamento(0);
		resultedPayment.setDataPagamento(LocalDate.now());
		resultedPayment.setOraPagamento(LocalTime.now());
		resultedPayment.setImporto((float) cart.totalAmount());

		Carrello cartEmpty = new Carrello();

		when(gos.commissionaOrdine(cart, order, resultedPayment, user)).thenReturn(cartEmpty);

		pagamentoController.doPost(request, response);


		verify(request.getSession()).removeAttribute("usercart");
		verify(request.getSession()).removeAttribute("preview_order"); 
		verify(response).sendRedirect(request.getContextPath()+"/SuccessoPagamento");

	}
}
