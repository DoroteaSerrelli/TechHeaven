package application.GestioneCarrelloControl;

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

import application.GestioneCarrello.GestioneCarrelloControl.CheckoutCarrello;
import application.GestioneCarrello.GestioneCarrelloService.Carrello;
import application.GestioneCarrello.GestioneCarrelloService.ItemCarrello;
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
import application.Pagamento.PagamentoService.PagamentoServiceImpl;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.Utente;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class CheckoutCarrelloTest {
	
	private CheckoutCarrello checkoutController;
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
	public void testDoPost_TC11_1_1_1() throws ProdottoPresenteException, ProdottoNulloException, IOException, ServletException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>()); 
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

		String idIndirizzo = "";
		String action = "confirmOrder";
				
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		
		checkoutController.doPost(request, response);
		
		IndirizzoSpedizioneNulloException ex = new IndirizzoSpedizioneNulloException("Specificare l’indirizzo di spedizione per l’ordine. Per aggiungere un altro indirizzo, annulla l’acquisto e vai nell’area riservata.");
		
		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");
	}

	
	@Test
	public void testDoPost_TC11_1_1_2() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IOException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		
		String action = "confirmOrder";
		String idIndirizzo = "10";
		String modSpedizione = "SPEDIZIONE_ERRATA";
		String modConsegna = "DOMICILIO";
				
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenThrow(new ErroreTipoSpedizioneException("Specificare la modalità di spedizione per l'ordine: standard, prime, assicurata."));
		
		checkoutController.doPost(request, response);
		
		ErroreTipoSpedizioneException ex = new ErroreTipoSpedizioneException("Specificare la modalità di spedizione per l'ordine: standard, prime, assicurata.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");
	}

	@Test
	public void testDoPost_TC11_1_1_3() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IOException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		
		String action = "confirmOrder";
		String idIndirizzo = "10";
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "ERRATA_CONSEGNA";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenThrow(new ErroreTipoConsegnaException("Specificare la modalità di consegna per l'ordine: domicilio, punto di ritiro, priority/fascia oraria."));
		
		checkoutController.doPost(request, response);
		
		ErroreTipoConsegnaException ex = new ErroreTipoConsegnaException("Specificare la modalità di consegna per l'ordine: domicilio, punto di ritiro, priority/fascia oraria.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");

	}

	@Test
	public void testDoPost_TC11_1_1_4_DatiOrdineCorretti() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IOException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		
		String action = "confirmOrder";
		String idIndirizzo = "10";
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenReturn(order);
		
		checkoutController.doPost(request, response);
		
		
		verify(request.getSession()).setAttribute("preview_order", order);
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento");

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
	 * 
	 * */

	@Test
	public void testDoPost_TC11_2_1_1() throws ProdottoPresenteException, ProdottoNulloException, ServletException, IOException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>()); 
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

		String idIndirizzo = "";
		String action = "confirmOrder";
				
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		
		checkoutController.doPost(request, response);
		
		IndirizzoSpedizioneNulloException ex = new IndirizzoSpedizioneNulloException("Specificare l’indirizzo di spedizione per l’ordine. Per aggiungere un altro indirizzo, annulla l’acquisto e vai nell’area riservata.");
		
		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");

	}

	@Test
	public void testDoPost_TC11_2_1_2() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException, IOException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		String action = "confirmOrder";
		String modSpedizione = "SPEDIZIONE_ERRATA";
		String modConsegna = "DOMICILIO";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenThrow(new ErroreTipoSpedizioneException("Specificare la modalità di spedizione per l'ordine: standard, prime, assicurata."));
		
		checkoutController.doPost(request, response);
		
		ErroreTipoSpedizioneException ex = new ErroreTipoSpedizioneException("Specificare la modalità di spedizione per l'ordine: standard, prime, assicurata.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");

	}

	@Test
	public void testDoPost_TC11_2_1_3() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException, IOException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		
		String action = "confirmOrder";
		String idIndirizzo = "10";
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "ERRATA_CONSEGNA";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenThrow(new ErroreTipoConsegnaException("Specificare la modalità di consegna per l'ordine: domicilio, punto di ritiro, priority/fascia oraria."));
		
		checkoutController.doPost(request, response);
		
		ErroreTipoConsegnaException ex = new ErroreTipoConsegnaException("Specificare la modalità di consegna per l'ordine: domicilio, punto di ritiro, priority/fascia oraria.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");

	}
	
	@Test
	public void testDoPost_TC11_2_1_4_DatiOrdineCorretti() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IOException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		
		String action = "confirmOrder";
		String idIndirizzo = "10";
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenReturn(order);
		
		checkoutController.doPost(request, response);
		
		
		verify(request.getSession()).setAttribute("preview_order", order);
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento");

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
	public void testDoPost_TC11_3_1_1() throws ProdottoPresenteException, ProdottoNulloException, ServletException, IOException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
		String username = "sabrina";
		String password = "30sabriNa02";
		String email = "sabrina.30@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR"));
		addresses.add(new Indirizzo(13, "Giuseppe Mazzini", "98", "Verona", "37100", "VR"));


		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>()); 
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

		String idIndirizzo = "";
		String action = "confirmOrder";
				
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		
		checkoutController.doPost(request, response);
		
		IndirizzoSpedizioneNulloException ex = new IndirizzoSpedizioneNulloException("Specificare l’indirizzo di spedizione per l’ordine. Per aggiungere un altro indirizzo, annulla l’acquisto e vai nell’area riservata.");
		
		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");

	}

	@Test
	public void testDoPost_TC11_3_1_2() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException, IOException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		String action = "confirmOrder";
		String modSpedizione = "SPEDIZIONE_ERRATA";
		String modConsegna = "DOMICILIO";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenThrow(new ErroreTipoSpedizioneException("Specificare la modalità di spedizione per l'ordine: standard, prime, assicurata."));
		
		checkoutController.doPost(request, response);
		
		ErroreTipoSpedizioneException ex = new ErroreTipoSpedizioneException("Specificare la modalità di spedizione per l'ordine: standard, prime, assicurata.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");

	}

	@Test
	public void testDoPost_TC11_3_1_3() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException, IOException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		
		String action = "confirmOrder";
		String idIndirizzo = "10";
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "ERRATA_CONSEGNA";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenThrow(new ErroreTipoConsegnaException("Specificare la modalità di consegna per l'ordine: domicilio, punto di ritiro, priority/fascia oraria."));
		
		checkoutController.doPost(request, response);
		
		ErroreTipoConsegnaException ex = new ErroreTipoConsegnaException("Specificare la modalità di consegna per l'ordine: domicilio, punto di ritiro, priority/fascia oraria.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());            
		verify(response).sendRedirect(request.getContextPath()+"/CheckoutCarrello");

	}
	
	@Test
	public void testDoPost_TC11_3_1_4_DatiOrdineCorretti() throws ProdottoPresenteException, ProdottoNulloException, SQLException, IOException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, OrdineVuotoException, ServletException {
		
		checkoutController = new CheckoutCarrello(gos, ps);
		
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
		
		String action = "confirmOrder";
		String idIndirizzo = "10";
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("selectedAddress")).thenReturn(idIndirizzo);
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getSession().getAttribute("usercart")).thenReturn(cart);
		when(request.getParameter("tipoSpedizione")).thenReturn(modSpedizione);
		when(request.getParameter("modalitaConsegna")).thenReturn(modConsegna);
		
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		Ordine order = new Ordine(0, null, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());
		
		when(gos.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna)).thenReturn(order);
		
		checkoutController.doPost(request, response);
		
		
		verify(request.getSession()).setAttribute("preview_order", order);
		verify(response).sendRedirect(request.getContextPath()+"/Pagamento");

	}

}
