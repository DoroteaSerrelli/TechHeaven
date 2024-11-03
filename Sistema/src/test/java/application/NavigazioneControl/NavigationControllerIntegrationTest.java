package application.NavigazioneControl;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;

import application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.Navigazione.NavigazioneControl.NavigazioneController;
import application.Navigazione.NavigazioneControl.PaginationUtils;
import application.Navigazione.NavigazioneService.NavigazioneServiceImpl;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.NavigazioneException.ErroreRicercaCategoriaException;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class NavigationControllerIntegrationTest {

	private NavigazioneController navController;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	private PaginationUtils pu;
	private OrdineDAODataSource orderDAO; 
	private ProdottoDAODataSource productDAO;
	private NavigazioneServiceImpl navi_service;

	@BeforeEach
	public void setUp() throws IOException {

		PagamentoDAODataSource paymentDAO = mock(PagamentoDAODataSource.class);
		PhotoControl photoControl = mock(PhotoControl.class);
		UtenteDAODataSource userDAO = mock(UtenteDAODataSource.class);
		productDAO = mock(ProdottoDAODataSource.class);
		orderDAO = mock(OrdineDAODataSource.class);
		GestioneOrdiniServiceImpl gos = new GestioneOrdiniServiceImpl(orderDAO, userDAO, productDAO, paymentDAO);
		GestioneCatalogoServiceImpl gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);
		navi_service = new NavigazioneServiceImpl(productDAO);

		pu = new PaginationUtils(navi_service, gcs, gos);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}

	@Test
	public void testRedirectsToHomeIfSearchTypeNull() throws Exception {
		String currentPage = request.getContextPath() + "/index.jsp";

		when(request.getRequestURI()).thenReturn(currentPage);
		navController = new NavigazioneController(10, pu);
		when(request.getParameter("keyword")).thenReturn("example");
		when(request.getParameter("search_type")).thenReturn(null);

		navController.doPost(request, response);

		verify(response).sendRedirect(request.getContextPath() + "/");
	}

	/**
	 * TEST CASES PER RICERCA DI PRODOTTI PER BARRA DI RICERCA
	 * 
	 * TC8.1_1: la parola-chiave inserita ha lunghezza 0
	 * TC8.1_2: la parola-chiave ha lunghezza > 0 ma non è presente
	 * 			in alcuna specifica di qualche prodotto
	 * TC8.1_2: la parola-chiave ha lunghezza > 0 ed è presente
	 * 			nelle specifiche di almeno un prodotto
	 * */

	@Test
	public void testProcessRequest_TC8_1_1() throws Exception {
		String currentPage = request.getContextPath() + "/index.jsp";

		when(request.getRequestURI()).thenReturn(currentPage);

		navController = new NavigazioneController(10, pu);
		when(request.getParameter("keyword")).thenReturn("");

		navController.doPost(request, response);

		verify(request.getSession()).setAttribute("empty_search", "Compila questo campo.");
		verify(response).sendRedirect(request.getContextPath() + "/index.jsp");
	}

	@Test
	public void testProcessRequest_TC8_1_2() throws Exception {

		String currentPage = request.getContextPath() + "/index.jsp";

		when(request.getRequestURI()).thenReturn(currentPage);

		navController = new NavigazioneController(10, pu);
		String keyword = "Nokia";
		String searchType = "bar";

		when(request.getParameter("keyword")).thenReturn(keyword);
		when(request.getParameter("search_type")).thenReturn(searchType);
		when(request.getParameter("page")).thenReturn("1");
		when(productDAO.searching("NOME", keyword, 1, 4)).thenReturn(new ArrayList<>());
		when(request.getSession().getAttribute("products")).thenReturn(new ArrayList<>());
		// Act
		navController.doPost(request, response);

		// Assert
		verify(request.getSession(), times(2)).setAttribute("keyword", keyword);
		verify(request.getSession()).setAttribute("search_type", searchType);
		verify(response).sendRedirect(request.getContextPath() + "/ResultsPage");

		// Verifica che non ci siano prodotti nella sessione
		Collection<ProxyProdotto> products = (Collection<ProxyProdotto>) request.getSession().getAttribute("products");
		assertNotNull(products);
		assertTrue(products.isEmpty(), "La lista dei prodotti dovrebbe essere vuota.");
	}


	@Test
	public void testProcessRequest_TC8_1_3() throws Exception {

		String currentPage = request.getContextPath() + "/index.jsp";

		when(request.getRequestURI()).thenReturn(currentPage);

		ProxyProdotto p1 = new ProxyProdotto(1, "Prova", "Prova", "Prova", Float.parseFloat("100.00"),
				Categoria.TELEFONIA, "Marca", "Modello", 12, true, true);

		ProxyProdotto p2 = new ProxyProdotto(10, "Telefono 13 S", "Descrizione di presentazione", "Prova", Float.parseFloat("100.00"),
				Categoria.TELEFONIA, "Marca", "Modello", 2, true, true);

		Collection<ProxyProdotto> results = new ArrayList<>();
		results.add(p1);
		results.add(p2);

		navController = new NavigazioneController(10, pu);
		String keyword = "Prova";
		String searchType = "bar";

		when(request.getParameter("keyword")).thenReturn(keyword);
		when(request.getParameter("search_type")).thenReturn(searchType);
		when(request.getParameter("page")).thenReturn("1");

		when(productDAO.searching("NOME", keyword, 1, 4)).thenReturn(results);
		when(request.getSession().getAttribute("products")).thenReturn(results);

		navController.doPost(request, response);

		verify(request.getSession(), times(2)).setAttribute("keyword", keyword);
		verify(request.getSession()).setAttribute("search_type", searchType);
		verify(response).sendRedirect(request.getContextPath() + "/ResultsPage");
	}



	/**
	 * TEST CASES PER RICERCA DI PRODOTTI PER MENU DI NAVIGAZIONE
	 * 
	 * TC7.1_1: la categoria non è specificata nel formato corretto
	 * TC7.1_2: la categoria è specificata nel formato corretto
	 * 
	 * */

	@Test
	public void testProcessRequest_TC7_1_1() throws Exception {

		String currentPage = request.getContextPath() + "/index.jsp";

		when(request.getRequestURI()).thenReturn(currentPage);

		navController = new NavigazioneController(10, pu);
		String keyword = "errorCategory";
		String searchType = "menu";

		when(request.getParameter("keyword")).thenReturn(keyword);
		when(request.getParameter("search_type")).thenReturn(searchType);
		when(request.getParameter("page")).thenReturn("1");
		when(request.getSession().getAttribute("previous_page")).thenReturn(currentPage);

		assertThrows(ErroreRicercaCategoriaException.class , () -> {
			navi_service.ricercaProdottoMenu(keyword, 1, 4);
		});

		navController.doPost(request, response);

		// Assert
		verify(response).sendRedirect(currentPage);
	}


	@Test
	public void testProcessRequest_TC7_1_2() throws Exception {

		ProxyProdotto p1 = new ProxyProdotto(1, "Prova", "Prova", "Prova", Float.parseFloat("100.00"),
				Categoria.TELEFONIA, "Marca", "Modello", 12, true, true);

		ProxyProdotto p2 = new ProxyProdotto(10, "Telefono 13 S", "Descrizione di presentazione", "Prova", Float.parseFloat("100.00"),
				Categoria.TELEFONIA, "Marca", "Modello", 2, true, true);

		Collection<ProxyProdotto> results = new ArrayList<>();
		results.add(p1);
		results.add(p2);

		navController = new NavigazioneController(10, pu);
		String keyword = "TELEFONIA";
		String searchType = "menu";

		when(request.getParameter("keyword")).thenReturn(keyword);
		when(request.getParameter("search_type")).thenReturn(searchType);
		when(request.getParameter("page")).thenReturn("1");
		
		when(productDAO.searchingByCategory(null, keyword, 1, 4)).thenReturn(results);
		when(request.getSession().getAttribute("products")).thenReturn(results);

		navController.doPost(request, response);

		verify(request.getSession(), times(2)).setAttribute("keyword", keyword);
		verify(request.getSession()).setAttribute("search_type", searchType);
		verify(response).sendRedirect(request.getContextPath() + "/ResultsPage");
	}

}