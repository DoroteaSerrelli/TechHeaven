package application.GestioneOrdiniService;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.ItemCarrello;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneOrdiniService.OrdineException.ErroreTipoConsegnaException;
import application.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdiniService.OrdineException.IndirizzoSpedizioneNulloException;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.PagamentoService.PagamentoCartaCredito;
import application.PagamentoService.PagamentoPaypal;
import application.PagamentoService.PagamentoContrassegno;
import application.PagamentoService.PagamentoException.FormatoCVVCartaException;
import application.PagamentoService.PagamentoException.FormatoDataCartaException;
import application.PagamentoService.PagamentoException.FormatoNumeroCartaException;
import application.PagamentoService.PagamentoException.FormatoTitolareCartaException;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Utente;
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

		
		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>()); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);
		
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		
		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>()); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);
		
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		
		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>()); 
		Cliente profile = new Cliente(email, "Sabrina", "Ferro", Cliente.Sesso.F, "000-111-2222", addresses);
		Utente userComplete = new Utente(username, password, profile);
		
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", Float.parseFloat("254.50"), 
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
	
}
