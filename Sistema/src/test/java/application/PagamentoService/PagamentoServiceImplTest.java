package application.PagamentoService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import application.GestioneCarrello.GestioneCarrelloService.Carrello;
import application.GestioneCarrello.GestioneCarrelloService.ItemCarrello;
import application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine;
import application.GestioneOrdini.GestioneOrdiniService.Ordine;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.Pagamento.PagamentoService.Pagamento;
import application.Pagamento.PagamentoService.PagamentoCartaCredito;
import application.Pagamento.PagamentoService.PagamentoContrassegno;
import application.Pagamento.PagamentoService.PagamentoPaypal;
import application.Pagamento.PagamentoService.PagamentoServiceImpl;
import application.Pagamento.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.Utente;

public class PagamentoServiceImplTest {

	private PagamentoDAODataSource paymentDAO;
	private PagamentoServiceImpl pagamentoService;
	private UtenteDAODataSource userDAO;
	private ProdottoDAODataSource productDAO;

	@BeforeEach
	public void setUp() {
		productDAO = Mockito.mock(ProdottoDAODataSource.class);
		userDAO = Mockito.mock(UtenteDAODataSource.class);
		paymentDAO = mock(PagamentoDAODataSource.class);
		pagamentoService = new PagamentoServiceImpl(paymentDAO);
	}


	@Test
	public void testEffettuaPagamentoContrassegno() throws Exception {
		
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

		float price = (float) 0.0;

		for(ItemCarrello p : order.getProdotti()) {
			price += p.getPrezzo()*p.getQuantita();
		}
		
		PagamentoContrassegno pagamento = new PagamentoContrassegno(order.getCodiceOrdine(), order, price);
		PagamentoContrassegno clonedPagamento = (PagamentoContrassegno) pagamento.clone();

		// Simuliamo il salvataggio
		doNothing().when(paymentDAO).doSaveCash(clonedPagamento);

		// Esegui il pagamento
		Pagamento result = pagamentoService.effettuaPagamento(pagamento);

		// Verifica che il pagamento sia stato salvato
		verify(paymentDAO).doSaveCash(clonedPagamento);
		assertEquals(clonedPagamento, result);
	}

	@Test
	public void testEffettuaPagamentoPaypal() throws Exception {

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

		float price = (float) 0.0;

		for(ItemCarrello p : order.getProdotti()) {
			price += p.getPrezzo()*p.getQuantita();
		}

		PagamentoPaypal pagamento = new PagamentoPaypal(order.getCodiceOrdine(), order, price);

		doNothing().when(paymentDAO).doSavePaypal(pagamento);

		// Esegui il pagamento
		PagamentoPaypal result = (PagamentoPaypal) pagamentoService.effettuaPagamento(pagamento);
		
		assertEquals(pagamento.toString(), result.toString());
		
		
	}

	@Test
	public void testEffettuaPagamentoCartaCredito() throws Exception {

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

		float price = (float) 0.0;

		for(ItemCarrello p : order.getProdotti()) {
			price += p.getPrezzo()*p.getQuantita();
		}

		PagamentoCartaCredito pagamento = new PagamentoCartaCredito(order.getCodiceOrdine(), order, price, titolare, noCard);
		
		// Simuliamo il salvataggio
		doNothing().when(paymentDAO).doSaveCard(pagamento);

		// Esegui il pagamento
		PagamentoCartaCredito result = (PagamentoCartaCredito) pagamentoService.effettuaPagamento(pagamento);

		// Verifica che il pagamento sia stato salvato
		
		assertEquals(pagamento, result);
	}

	@Test
	public void testEffettuaPagamentoModalitaNonSupportata() throws Exception {
		Pagamento pagamento = mock(Pagamento.class);

		assertThrows(ModalitaAssenteException.class, () -> {

			pagamentoService.effettuaPagamento(pagamento);

		});

	}

}
