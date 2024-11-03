package application.GestioneApprovvigionamentiControl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
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
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.CodiceRichiestaException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.FormatoFornitoreException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoDisponibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.Navigazione.NavigazioneControl.PaginationUtils;
import application.Navigazione.NavigazioneService.NavigazioneServiceImpl;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneApprovvigionamentiDAO.ApprovvigionamentoDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneApprovvigionamentiControllerTest {

	private int perPage;
	private GestioneApprovigionamentiController gaController;
	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;
	private ProdottoDAODataSource pdao;
	private GestioneApprovvigionamentiServiceImpl gas;
	private PaginationUtils pu;

	@BeforeEach
	public void setUp() throws ServletException, IOException {

		perPage = 10;

		pdao = mock(ProdottoDAODataSource.class);
		pu = mock(PaginationUtils.class);
		gas = mock(GestioneApprovvigionamentiServiceImpl.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}

	@Test
	public void testDoPost_IDProdottoMancante() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		when(request.getParameter("product_id")).thenReturn(null);

		gaController.doPost(request, response);

		CodiceRichiestaException expectedExc = new CodiceRichiestaException("Errore nella generazione del codice della richiesta (codice = null).\n Riprovare più tardi.");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

	}

	@Test
	public void testDoPost_IDProdottoErratoFormato() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "error";
		when(request.getParameter("product_id")).thenReturn(product_id);

		gaController.doPost(request, response);

		CodiceRichiestaException expectedExc = new CodiceRichiestaException("Errore nella generazione del codice della richiesta (codice non è un numero intero).\n Riprovare più tardi.");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

	}

	@Test
	public void testDoPost_ProdottoNullNonInCatalogo() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "1";
		int productId = 1;
		when(request.getParameter("product_id")).thenReturn(product_id);
		when(pdao.doRetrieveProxyByKey(productId)).thenReturn(null);

		gaController.doPost(request, response);


		ProdottoVendibileException expectedExc = new ProdottoVendibileException("Non è possibile fare l'approvvigionamento di un prodotto non in catalogo.");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

	}

	@Test
	public void testDoPost_ProdottoNonInCatalogo() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "12";
		int productId = 12;
		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, false, false, pdao);

		when(request.getParameter("product_id")).thenReturn(product_id);
		when(pdao.doRetrieveProxyByKey(productId)).thenReturn(product1);

		gaController.doPost(request, response);

		ProdottoVendibileException expectedExc = new ProdottoVendibileException("Non è possibile fare l'approvvigionamento di un prodotto non in catalogo.");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

	}

	@Test
	public void testDoPost_QuantitaProdottoDisponibileException() throws Exception {
		
		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "12";
		int productId = 12;
		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, pdao);

		String quantity = "2";
		String fornitore = "";
		String email_fornitore = "";
		String descrizione = "";

		when(request.getParameter("product_id")).thenReturn(product_id);
		when(pdao.doRetrieveProxyByKey(productId)).thenReturn(product1);
		when(request.getParameter("quantity")).thenReturn(quantity);
		when(request.getParameter("fornitore")).thenReturn(fornitore);
		when(request.getParameter("email_fornitore")).thenReturn(email_fornitore);
		when(request.getParameter("descrizione")).thenReturn(descrizione);

		doThrow(new QuantitaProdottoDisponibileException("In magazzino sono ancora disponibili delle scorte per il prodotto specificato."))
		.when(gas).effettuaRichiestaApprovvigionamento(any(), anyInt(), anyString(), anyString(), anyString());


		gaController.doPost(request, response);

		QuantitaProdottoDisponibileException expectedExc = new QuantitaProdottoDisponibileException("In magazzino sono ancora disponibili delle scorte per il prodotto specificato.");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");

	}

	@Test
	public void testDoPost_QuantitaProdottoException() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "12";
		int productId = 12;
		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, pdao);

		String quantity = "-1";
		int quantityInt = -1;
		String fornitore = "";
		String email_fornitore = "";
		String descrizione = "";

		when(request.getParameter("product_id")).thenReturn(product_id);
		when(pdao.doRetrieveProxyByKey(productId)).thenReturn(product1);
		when(request.getParameter("quantity")).thenReturn(quantity);
		when(request.getParameter("fornitore")).thenReturn(fornitore);
		when(request.getParameter("email_fornitore")).thenReturn(email_fornitore);
		when(request.getParameter("descrizione")).thenReturn(descrizione);

		doThrow(new QuantitaProdottoException("La quantità del prodotto specificata non è valida"))
		.when(gas).effettuaRichiestaApprovvigionamento(product1, quantityInt, fornitore, email_fornitore, descrizione);


		gaController.doPost(request, response);

		QuantitaProdottoException expectedExc = new QuantitaProdottoException("La quantità del prodotto specificata non è valida");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");

	}

	@Test
	public void testDoPost_FormatoFornitoreException() throws Exception {
		
		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "12";
		int productId = 12;
		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, pdao);

		String quantity = "2";
		int quantityInt = 2;
		String fornitore = "%fornerror";
		String email_fornitore = "";
		String descrizione = "";

		when(request.getParameter("product_id")).thenReturn(product_id);
		when(pdao.doRetrieveProxyByKey(productId)).thenReturn(product1);
		when(request.getParameter("quantity")).thenReturn(quantity);
		when(request.getParameter("fornitore")).thenReturn(fornitore);
		when(request.getParameter("email_fornitore")).thenReturn(email_fornitore);
		when(request.getParameter("descrizione")).thenReturn(descrizione);

		doThrow(new FormatoFornitoreException("Il nome del fornitore deve essere una sequenza di lettere, spazi ed, eventualmente, numeri."))
		.when(gas).effettuaRichiestaApprovvigionamento(product1, quantityInt, fornitore, email_fornitore, descrizione);


		gaController.doPost(request, response);

		FormatoFornitoreException expectedExc = new FormatoFornitoreException("Il nome del fornitore deve essere una sequenza di lettere, spazi ed, eventualmente, numeri.");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");
	}

	@Test
	public void testDoPost_FormatoEmailException() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "12";
		int productId = 12;
		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, pdao);

		String quantity = "2";
		int quantityInt = 2;
		String fornitore = "FornitoreProva";
		String email_fornitore = "error@ex";
		String descrizione = "";

		when(request.getParameter("product_id")).thenReturn(product_id);
		when(pdao.doRetrieveProxyByKey(productId)).thenReturn(product1);
		when(request.getParameter("quantity")).thenReturn(quantity);
		when(request.getParameter("fornitore")).thenReturn(fornitore);
		when(request.getParameter("email_fornitore")).thenReturn(email_fornitore);
		when(request.getParameter("descrizione")).thenReturn(descrizione);

		doThrow(new FormatoEmailException("L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com)."))
		.when(gas).effettuaRichiestaApprovvigionamento(product1, quantityInt, fornitore, email_fornitore, descrizione);


		gaController.doPost(request, response);

		FormatoEmailException expectedExc = new FormatoEmailException("L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");

	}

	@Test
	public void testDoPost_DescrizioneDettaglioException() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "12";
		int productId = 12;
		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, pdao);

		String quantity = "2";
		int quantityInt = 2;
		String fornitore = "FornitoreProva";
		String email_fornitore = "fornitore@prova.com";
		String descrizione = "";

		when(request.getParameter("product_id")).thenReturn(product_id);
		when(pdao.doRetrieveProxyByKey(productId)).thenReturn(product1);
		when(request.getParameter("quantity")).thenReturn(quantity);
		when(request.getParameter("fornitore")).thenReturn(fornitore);
		when(request.getParameter("email_fornitore")).thenReturn(email_fornitore);
		when(request.getParameter("descrizione")).thenReturn(descrizione);

		doThrow(new DescrizioneDettaglioException("Questo campo non puo\' essere vuoto.")).when(gas).effettuaRichiestaApprovvigionamento(product1, quantityInt, fornitore, email_fornitore, descrizione);


		gaController.doPost(request, response);

		DescrizioneDettaglioException expectedExc = new DescrizioneDettaglioException("Questo campo non puo\' essere vuoto.");

		verify(request.getSession()).setAttribute("error",expectedExc.getMessage());                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");

	}

	@Test
	public void testDoPost_SuccessoRichiestaApprovvigionamento() throws Exception {

		gaController = new GestioneApprovigionamentiController(pdao, gas, pu);

		String product_id = "12";
		int productId = 12;
		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, pdao);

		String quantity = "2";
		int quantityInt = 2;
		String fornitore = "FornitoreProva";
		String email_fornitore = "fornitore@prova.com";
		String descrizione = "Questo campo è un esempio";

		when(request.getParameter("product_id")).thenReturn(product_id);
		when(pdao.doRetrieveProxyByKey(productId)).thenReturn(product1);
		when(request.getParameter("quantity")).thenReturn(quantity);
		when(request.getParameter("fornitore")).thenReturn(fornitore);
		when(request.getParameter("email_fornitore")).thenReturn(email_fornitore);
		when(request.getParameter("descrizione")).thenReturn(descrizione);

		RichiestaApprovvigionamento supply = new RichiestaApprovvigionamento(3, fornitore, email_fornitore, descrizione, quantityInt, product1);
		when(gas.effettuaRichiestaApprovvigionamento(product1, quantityInt, fornitore, email_fornitore, descrizione)).thenReturn(supply);


		gaController.doPost(request, response);

		verify(request.getSession()).setAttribute("error", "Richiesta Approvigionamento Avvenuta Con Successo!");                               
		verify(response).sendRedirect(request.getContextPath() + "/Approvigionamento");

	}
}
