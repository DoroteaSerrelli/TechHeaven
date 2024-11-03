package application.GestioneApprovvigionamentiControl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiControl.GestioneApprovigionamentiController;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.GestioneApprovvigionamentiServiceImpl;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamento;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.FormatoFornitoreException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoDisponibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.Navigazione.NavigazioneControl.PaginationUtils;
import application.Navigazione.NavigazioneService.NavigazioneServiceImpl;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.GestioneApprovvigionamentiDAO.ApprovvigionamentoDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;

public class GestioneApprovvigionamentiControllerTestInt {

	private int perPage;
	private GestioneApprovigionamentiController gaController;
	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;
	private ProdottoDAODataSource pdao;
	private ApprovvigionamentoDAODataSource supplyDAO;
	private GestioneApprovvigionamentiServiceImpl gas;
	private PaginationUtils pu;

	@BeforeEach
	public void setUp() throws ServletException, IOException {

		perPage = 10;

		pdao = mock(ProdottoDAODataSource.class);
		PhotoControl photoControl = mock(PhotoControl.class);
		PagamentoDAODataSource paymentDAO = mock(PagamentoDAODataSource.class);
		UtenteDAODataSource userDAO = mock(UtenteDAODataSource.class);
		OrdineDAODataSource orderDAO = mock(OrdineDAODataSource.class);
		NavigazioneServiceImpl ns = new NavigazioneServiceImpl(pdao);
		supplyDAO = mock(ApprovvigionamentoDAODataSource.class);

		GestioneCatalogoServiceImpl gcs = new GestioneCatalogoServiceImpl(pdao, photoControl);
		GestioneOrdiniServiceImpl gos = new GestioneOrdiniServiceImpl(orderDAO, userDAO, pdao, paymentDAO);
		pu = new PaginationUtils(ns, gcs, gos);
		gas = new GestioneApprovvigionamentiServiceImpl(supplyDAO);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}

	/**
	 * TEST CASES PER CREARE UNA RICHIESTA DI APPROVVIGIONAMENTO
	 * 
	 * TC13.1_1 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  sono presenti scorte del prodotto in magazzino
	 * 
	 * TC13.1_2 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è errata nel formato
	 * 
	 * TC13.1_3 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è corretta nel formato,
	 * 			  il nome del fornitore è espresso non correttamente
	 * 
	 * TC13.1_4 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è corretta nel formato,
	 * 			  il nome del fornitore è espresso correttamente,
	 * 			  l'email del fornitore è scritta non correttamente
	 * 
	 * TC13.1_5 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è corretta nel formato,
	 * 			  il nome del fornitore è espresso correttamente,
	 * 			  l'email del fornitore è scritta correttamente,
	 * 			  la descrizione è vuota
	 * 
	 * TC13.1_6 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è corretta nel formato,
	 * 			  il nome del fornitore è espresso correttamente,
	 * 			  l'email del fornitore è scritta correttamente,
	 * 			  la descrizione è un testo non vuoto
	 * 
	 * */

	@Test
	public void testDoPost_TC13_1_1() throws Exception {

		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false);

		String productIdParam = "12";
		int quantity = 21;
		String supplier = "Esprinet";
		String emailSupplier = "info@esprinet.com";
		String description = "Prova";

		when(request.getParameter("product_id")).thenReturn(productIdParam);
		when(pdao.doRetrieveProxyByKey(product.getCodiceProdotto())).thenReturn(product);
		when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));

		when(request.getParameter("fornitore")).thenReturn(supplier);
		when(request.getParameter("email_fornitore")).thenReturn(emailSupplier);
		when(request.getParameter("descrizione")).thenReturn(description);

		
		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		gaController.doPost(request, response);
		
		assertThrows(QuantitaProdottoDisponibileException.class , () -> {
			gas.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, description);
		});

		String exMessage = "In magazzino sono ancora disponibili delle scorte per il prodotto specificato";

		verify(request.getSession()).setAttribute("error",exMessage);                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");

	}


	@Test
	public void testDoPost_TC13_1_2() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);

		String productIdParam = "12";
		int quantity = -2;
		String supplier = "Esprinet";
		String emailSupplier = "info@esprinet.com";
		String description = "Prova";

		when(request.getParameter("product_id")).thenReturn(productIdParam);
		when(pdao.doRetrieveProxyByKey(product.getCodiceProdotto())).thenReturn(product);
		when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));

		when(request.getParameter("fornitore")).thenReturn(supplier);
		when(request.getParameter("email_fornitore")).thenReturn(emailSupplier);
		when(request.getParameter("descrizione")).thenReturn(description);


		assertThrows(QuantitaProdottoException.class , () -> {
			gas.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, description);
		});

		gaController.doPost(request, response);

		String exMessage = "La quantità del prodotto specificata non è valida";

		verify(request.getSession()).setAttribute("error",exMessage);                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");

	}
	
	@Test
	public void testDoPost_TC13_1_3() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);

		String productIdParam = "12";
		int quantity = 2;
		String supplier = "Esprinet\\**";
		String emailSupplier = "info@esprinet.com";
		String description = "Prova";

		when(request.getParameter("product_id")).thenReturn(productIdParam);
		when(pdao.doRetrieveProxyByKey(product.getCodiceProdotto())).thenReturn(product);
		when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));

		when(request.getParameter("fornitore")).thenReturn(supplier);
		when(request.getParameter("email_fornitore")).thenReturn(emailSupplier);
		when(request.getParameter("descrizione")).thenReturn(description);


		assertThrows(FormatoFornitoreException.class , () -> {
			gas.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, description);
		});

		gaController.doPost(request, response);

		String exMessage = "Il nome del fornitore deve essere una sequenza di lettere, spazi ed, eventualmente, numeri";

		verify(request.getSession()).setAttribute("error",exMessage);                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");

	}
	
	@Test
	public void testDoPost_TC13_1_4() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);

		String productIdParam = "12";
		int quantity = 2;
		String supplier = "Esprinet";
		String emailSupplier = "info@";
		String description = "Prova";

		when(request.getParameter("product_id")).thenReturn(productIdParam);
		when(pdao.doRetrieveProxyByKey(product.getCodiceProdotto())).thenReturn(product);
		when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));

		when(request.getParameter("fornitore")).thenReturn(supplier);
		when(request.getParameter("email_fornitore")).thenReturn(emailSupplier);
		when(request.getParameter("descrizione")).thenReturn(description);


		assertThrows(FormatoEmailException.class , () -> {
			gas.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, description);
		});

		gaController.doPost(request, response);

		String exMessage = "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com)";

		verify(request.getSession()).setAttribute("error",exMessage);                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");
	}
	
	@Test
	public void testDoPost_TC13_1_5() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);

		String productIdParam = "12";
		int quantity = 2;
		String supplier = "Esprinet";
		String emailSupplier = "info@esprinet.com";
		String description = "";

		when(request.getParameter("product_id")).thenReturn(productIdParam);
		when(pdao.doRetrieveProxyByKey(product.getCodiceProdotto())).thenReturn(product);
		when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));

		when(request.getParameter("fornitore")).thenReturn(supplier);
		when(request.getParameter("email_fornitore")).thenReturn(emailSupplier);
		when(request.getParameter("descrizione")).thenReturn(description);


		assertThrows(DescrizioneDettaglioException.class , () -> {
			gas.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, description);
		});

		gaController.doPost(request, response);

		String exMessage = "Questo campo non puo\' essere vuoto";

		verify(request.getSession()).setAttribute("error",exMessage);                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");
	}
	
	@Test
	public void testDoPost_TC13_1_6() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);

		String productIdParam = "12";
		int quantity = 2;
		String supplier = "Esprinet";
		String emailSupplier = "info@esprinet.com";
		String description = "Prova";

		when(request.getParameter("product_id")).thenReturn(productIdParam);
		when(pdao.doRetrieveProxyByKey(product.getCodiceProdotto())).thenReturn(product);
		when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));

		when(request.getParameter("fornitore")).thenReturn(supplier);
		when(request.getParameter("email_fornitore")).thenReturn(emailSupplier);
		when(request.getParameter("descrizione")).thenReturn(description);

		RichiestaApprovvigionamento expectedRequestSupply = new RichiestaApprovvigionamento(supplier, emailSupplier, description, quantity, product);
		RichiestaApprovvigionamento realRequestSupply = gas.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, description);
		

		gaController.doPost(request, response);
		
		assertEquals(expectedRequestSupply, realRequestSupply);
		verify(request.getSession()).setAttribute("error", "Richiesta Approvigionamento Avvenuta Con Successo!");            
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");
	}
	
}
