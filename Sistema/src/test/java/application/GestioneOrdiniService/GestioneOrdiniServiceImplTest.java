package application.GestioneOrdiniService;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import application.GestioneCarrello.GestioneCarrelloService.Carrello;
import application.GestioneCarrello.GestioneCarrelloService.ItemCarrello;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine;
import application.GestioneOrdini.GestioneOrdiniService.Ordine;
import application.GestioneOrdini.GestioneOrdiniService.ReportSpedizione;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreSpedizioneOrdineException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoConsegnaException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.FormatoCorriereException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.FormatoImballaggioException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.FormatoQuantitaException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.IndirizzoSpedizioneNulloException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.MancanzaPezziException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.Navigazione.NavigazioneService.Prodotto;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.Pagamento.PagamentoService.Pagamento;
import application.Pagamento.PagamentoService.PagamentoCartaCredito;
import application.Pagamento.PagamentoService.PagamentoContrassegno;
import application.Pagamento.PagamentoService.PagamentoPaypal;
import application.Pagamento.PagamentoService.PagamentoService;
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
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneOrdiniServiceImplTest {

	private GestioneOrdiniServiceImpl ordiniService;
	private OrdineDAODataSource orderDAO;
	private UtenteDAODataSource userDAO;
	private ProdottoDAODataSource productDAO;
	private PagamentoDAODataSource paymentDAO;

	@BeforeEach
	public void setUp() {

		productDAO = Mockito.mock(ProdottoDAODataSource.class);
		orderDAO = Mockito.mock(OrdineDAODataSource.class);
		userDAO = Mockito.mock(UtenteDAODataSource.class);
		paymentDAO = Mockito.mock(PagamentoDAODataSource.class);

		ordiniService = new GestioneOrdiniServiceImpl(orderDAO, userDAO, productDAO, paymentDAO);
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
	 * */

	@Test
	public void TC11_1_1_1() throws ProdottoPresenteException, ProdottoNulloException {

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

		String idIndirizzo = "";
		String modSpedizione = "SPEDIZIONE_PRIME";
		String modConsegna = "DOMICILIO";

		assertThrows(IndirizzoSpedizioneNulloException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_1_1_2() throws ProdottoPresenteException, ProdottoNulloException, SQLException {

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
		String modSpedizione = "SPEDIZIONE_ERRATA";
		String modConsegna = "DOMICILIO";

		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ErroreTipoSpedizioneException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_1_1_3() throws ProdottoPresenteException, ProdottoNulloException, SQLException {

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
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "ERRATA_CONSEGNA";

		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ErroreTipoConsegnaException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_1_1_4() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ModalitaAssenteException.class, () -> {
			ordiniService.creaPagamento_cartaCredito(cart, order, modPagamento, "", "", "", "");
		});

	}

	@Test
	public void TC11_1_1_5() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabry56";
		String noCard = "0000001111223456";
		String expiredDate = "02-25";
		String CVV = "123";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(FormatoTitolareCartaException.class, () -> {
			ordiniService.creaPagamento_cartaCredito(cart, order, modPagamento, titolare, noCard, expiredDate, CVV);
		});

	}

	@Test
	public void TC11_1_1_6() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabrina Ferro";
		String noCard = "11122345error6";
		String expiredDate = "02-25";
		String CVV = "123";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(FormatoNumeroCartaException.class, () -> {
			ordiniService.creaPagamento_cartaCredito(cart, order, modPagamento, titolare, noCard, expiredDate, CVV);
		});

	}

	@Test
	public void TC11_1_1_7() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabrina Ferro";
		String noCard = "1112234890999945";
		String expiredDate = "17/29";
		String CVV = "123";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(FormatoDataCartaException.class, () -> {
			ordiniService.creaPagamento_cartaCredito(cart, order, modPagamento, titolare, noCard, expiredDate, CVV);
		});

	}

	@Test
	public void TC11_1_1_8() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabrina Ferro";
		String noCard = "1112234890999945";
		String expiredDate = "12/24";
		String CVV = "3error";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(FormatoCVVCartaException.class, () -> {
			ordiniService.creaPagamento_cartaCredito(cart, order, modPagamento, titolare, noCard, expiredDate, CVV);
		});

	}

	@Test
	public void TC11_1_1_9() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, ModalitaAssenteException, FormatoCVVCartaException, FormatoDataCartaException, FormatoTitolareCartaException, FormatoNumeroCartaException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CARTA_CREDITO";
		String titolare = "Sabrina Ferro";
		String noCard = "1112234890999945";
		String expiredDate = "12/24";
		String CVV = "312";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());

		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);


		Ordine resultedOrder = ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		order.setCodiceOrdine(resultedOrder.getCodiceOrdine());

		assertEquals(resultedOrder.getAcquirente(), order.getAcquirente());
		assertEquals(resultedOrder.getIndirizzoSpedizione(), order.getIndirizzoSpedizione());
		assertEquals(resultedOrder.getConsegna(), order.getConsegna());
		assertEquals(resultedOrder.getSpedizione(), order.getSpedizione());


		PagamentoCartaCredito resultedPayment = (PagamentoCartaCredito) ordiniService.creaPagamento_cartaCredito(cart, order, modPagamento, titolare, noCard, expiredDate, CVV);
		assertEquals(resultedPayment.getNumeroCarta(), noCard);
		assertEquals(resultedPayment.getTitolare(), titolare);
		assertEquals(resultedPayment.getOrdine(), order);

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
	public void TC11_2_1_1() throws ProdottoPresenteException, ProdottoNulloException {

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

		String idIndirizzo = "";
		String modSpedizione = "SPEDIZIONE_PRIME";
		String modConsegna = "DOMICILIO";

		assertThrows(IndirizzoSpedizioneNulloException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_2_1_2() throws ProdottoPresenteException, ProdottoNulloException, SQLException {

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
		String modSpedizione = "SPEDIZIONE_ERRATA";
		String modConsegna = "DOMICILIO";

		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ErroreTipoSpedizioneException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_2_1_3() throws ProdottoPresenteException, ProdottoNulloException, SQLException {

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
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "ERRATA_CONSEGNA";

		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ErroreTipoConsegnaException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_2_1_4() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ModalitaAssenteException.class, () -> {
			ordiniService.creaPagamento_PaypalContrassegno(cart, order, modPagamento);
		});

	}

	@Test
	public void TC11_2_1_5() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, ModalitaAssenteException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "PAYPAL";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine resultedOrder = ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		order.setCodiceOrdine(resultedOrder.getCodiceOrdine());

		assertEquals(resultedOrder.getAcquirente(), order.getAcquirente());
		assertEquals(resultedOrder.getIndirizzoSpedizione(), order.getIndirizzoSpedizione());
		assertEquals(resultedOrder.getConsegna(), order.getConsegna());
		assertEquals(resultedOrder.getSpedizione(), order.getSpedizione());


		PagamentoPaypal resultedPayment = (PagamentoPaypal) ordiniService.creaPagamento_PaypalContrassegno(cart, order, modPagamento);
		assertEquals(resultedPayment.getOrdine(), order);

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
	public void TC11_3_1_1() throws ProdottoPresenteException, ProdottoNulloException {

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

		String idIndirizzo = "";
		String modSpedizione = "SPEDIZIONE_PRIME";
		String modConsegna = "DOMICILIO";

		assertThrows(IndirizzoSpedizioneNulloException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_3_1_2() throws ProdottoPresenteException, ProdottoNulloException, SQLException {

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
		String modSpedizione = "SPEDIZIONE_ERRATA";
		String modConsegna = "DOMICILIO";

		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ErroreTipoSpedizioneException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_3_1_3() throws ProdottoPresenteException, ProdottoNulloException, SQLException {

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
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "ERRATA_CONSEGNA";

		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ErroreTipoConsegnaException.class, () -> {
			ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		});

	}

	@Test
	public void TC11_3_1_4() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		assertThrows(ModalitaAssenteException.class, () -> {
			ordiniService.creaPagamento_PaypalContrassegno(cart, order, modPagamento);
		});

	}

	@Test
	public void TC11_3_1_5() throws ProdottoPresenteException, ProdottoNulloException, SQLException, OrdineVuotoException, IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, ErroreTipoConsegnaException, ModalitaAssenteException {

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
		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CONTRASSEGNO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		Ordine resultedOrder = ordiniService.creaOrdine(cart, user, idIndirizzo, modSpedizione, modConsegna);
		order.setCodiceOrdine(resultedOrder.getCodiceOrdine());

		assertEquals(resultedOrder.getAcquirente(), order.getAcquirente());
		assertEquals(resultedOrder.getIndirizzoSpedizione(), order.getIndirizzoSpedizione());
		assertEquals(resultedOrder.getConsegna(), order.getConsegna());
		assertEquals(resultedOrder.getSpedizione(), order.getSpedizione());


		PagamentoContrassegno resultedPayment = (PagamentoContrassegno) ordiniService.creaPagamento_PaypalContrassegno(cart, order, modPagamento);
		assertEquals(resultedPayment.getOrdine(), order);

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
	public void TC12_1_1_1() throws ModalitaAssenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, OrdineVuotoException, ProdottoPresenteException, ProdottoNulloException {

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
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 2, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		ArrayList<ProxyProdotto> proxyProducts = new ArrayList<>();
		proxyProducts.add(product1);
		proxyProducts.add(product2);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item1.setQuantita(3);
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(2);

		cart.addProduct(item1);
		cart.addProduct(item2);

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(3);
		quantities.add(2);

		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CONTRASSEGNO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		// Simulazione del comportamento di doRetrieveProxyByKey
		int i = 0;
		for (ItemCarrello item : cart.getProducts()) {
			Mockito.when(productDAO.doRetrieveProxyByKey(item.getCodiceProdotto())).thenReturn(proxyProducts.get(i));
			i++;
		}

		assertThrows(MancanzaPezziException.class, () -> {
			ordiniService.creaReportSpedizione(order, cart.getProducts(), quantities, "imballaggio", "corriere");
		});


	}


	@Test
	public void TC12_1_1_2() throws ModalitaAssenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, OrdineVuotoException, ProdottoPresenteException, ProdottoNulloException {

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
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 2, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		ArrayList<ProxyProdotto> proxyProducts = new ArrayList<>();
		proxyProducts.add(product1);
		proxyProducts.add(product2);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item1.setQuantita(1);
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(2);

		cart.addProduct(item1);
		cart.addProduct(item2);

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(1);

		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CONTRASSEGNO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		// Simulazione del comportamento di doRetrieveProxyByKey
		int i = 0;
		for (ItemCarrello item : cart.getProducts()) {
			Mockito.when(productDAO.doRetrieveProxyByKey(item.getCodiceProdotto())).thenReturn(proxyProducts.get(i));
			i++;
		}

		assertThrows(FormatoQuantitaException.class, () -> {
			ordiniService.creaReportSpedizione(order, cart.getProducts(), quantities, "imballaggio", "corriere");
		});


	}

	@Test
	public void TC12_1_1_3() throws ModalitaAssenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, OrdineVuotoException, ProdottoPresenteException, ProdottoNulloException {

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
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 2, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		ArrayList<ProxyProdotto> proxyProducts = new ArrayList<>();
		proxyProducts.add(product1);
		proxyProducts.add(product2);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item1.setQuantita(1);
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(2);

		cart.addProduct(item1);
		cart.addProduct(item2);

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(2);

		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CONTRASSEGNO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		// Simulazione del comportamento di doRetrieveProxyByKey
		int i = 0;
		for (ItemCarrello item : cart.getProducts()) {
			Mockito.when(productDAO.doRetrieveProxyByKey(item.getCodiceProdotto())).thenReturn(proxyProducts.get(i));
			i++;
		}

		String errorPackaging = "";
		assertThrows(FormatoImballaggioException.class, () -> {
			ordiniService.creaReportSpedizione(order, cart.getProducts(), quantities, errorPackaging, "corriere");
		});

	}


	@Test
	public void TC12_1_1_4() throws ModalitaAssenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, OrdineVuotoException, ProdottoPresenteException, ProdottoNulloException {

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
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 2, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		ArrayList<ProxyProdotto> proxyProducts = new ArrayList<>();
		proxyProducts.add(product1);
		proxyProducts.add(product2);

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item1.setQuantita(1);
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(2);

		cart.addProduct(item1);
		cart.addProduct(item2);

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(2);

		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CONTRASSEGNO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		// Simulazione del comportamento di doRetrieveProxyByKey
		int i = 0;
		for (ItemCarrello item : cart.getProducts()) {
			Mockito.when(productDAO.doRetrieveProxyByKey(item.getCodiceProdotto())).thenReturn(proxyProducts.get(i));
			i++;
		}

		String packaging = "Scatola di cartone, polistirolo e nastro adesivo";
		String errorCourier = "33SError";

		assertThrows(FormatoCorriereException.class, () -> {
			ordiniService.creaReportSpedizione(order, cart.getProducts(), quantities, packaging, errorCourier);
		});
	}

	@Test
	public void TC12_1_1_5() throws ModalitaAssenteException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, OrdineVuotoException, ProdottoPresenteException, ProdottoNulloException, ErroreSpedizioneOrdineException, ErroreTipoSpedizioneException, CloneNotSupportedException, MancanzaPezziException, FormatoQuantitaException, FormatoImballaggioException, FormatoCorriereException {

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
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 2, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		ArrayList<ProxyProdotto> proxyProducts = new ArrayList<>();
		proxyProducts.add(product1);
		proxyProducts.add(product2);

		ArrayList<Prodotto> completeProducts = new ArrayList<>();
		completeProducts.add(new Prodotto(12, "HP 15s-fq5040nl", "Prova", "ProvaDettagli", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 2, true, false));

		completeProducts.add(new Prodotto(0, "Apple AirPods Pro 2", "Prova", "ProvaDettagli", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false));

		ItemCarrello item1 = new ItemCarrello();
		ItemCarrello item2 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item1.setQuantita(1);
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(2);

		cart.addProduct(item1);
		cart.addProduct(item2);

		ArrayList<Integer> quantities = new ArrayList<>();
		quantities.add(1);
		quantities.add(2);

		Indirizzo selectedAddress = new Indirizzo(10, "Giuseppe Garibaldi", "56", "Parma", "43121", "PR");
		String modSpedizione = "SPEDIZIONE_STANDARD";
		String modConsegna = "PUNTO_RITIRO";
		String modPagamento = "CONTRASSEGNO";

		Ordine order = new Ordine(20, ObjectOrdine.Stato.Richiesta_effettuata, selectedAddress, ObjectOrdine.TipoSpedizione.Spedizione_standard, ObjectOrdine.TipoConsegna.Punto_ritiro, profile, cart.getProducts());


		Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(userComplete);

		int i = 0;
		for (ItemCarrello item : cart.getProducts()) {
			Mockito.when(productDAO.doRetrieveProxyByKey(item.getCodiceProdotto())).thenReturn(proxyProducts.get(i));
			i++;
		}

		String packaging = "Scatola di cartone, polistirolo e nastro adesivo";
		String courier = "BRT";

		PagamentoContrassegno resultedPayment = (PagamentoContrassegno) ordiniService.creaPagamento_PaypalContrassegno(cart, order, modPagamento);
		ReportSpedizione report = ordiniService.creaReportSpedizione(order, cart.getProducts(), quantities, packaging, courier);

		Mockito.when(orderDAO.doSaveToShip(order, report)).thenReturn(true);

		int index = 0;
		for(ItemCarrello item : cart.getProducts()) {
			int expectedQuantity = completeProducts.get(index).getQuantita();

			Mockito.when(productDAO.doRetrieveCompleteByKey(item.getCodiceProdotto())).thenReturn(completeProducts.get(index));

			//aggiornamento quantità
			Mockito.when(productDAO.updateQuantity(0, expectedQuantity - item.getQuantita())).thenReturn(true);
			index++;
		}

		Mockito.when(paymentDAO.doRetrieveCashByOrder(order.getCodiceOrdine())).thenReturn(resultedPayment);
		Ordine shippedOrder = ordiniService.preparazioneSpedizioneOrdine(order, report);
		order.setStatoAsString("SPEDITO");

		assertEquals(order.getStato(), shippedOrder.getStato());

		Mockito.verify(productDAO, Mockito.times(cart.getProducts().size())).updateQuantity(anyInt(), anyInt());
		Mockito.verify(orderDAO).doSaveToShip(order, report);

	}
}
