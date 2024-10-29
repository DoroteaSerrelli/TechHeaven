package application.GestioneCatalogoController;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import application.GestioneCatalogoControl.ModificaInfoProdottoController;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoAggiornatoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.FormatoCodiceException;
import application.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.NavigazioneService.ProdottoException.FormatoModelloException;
import application.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class ModificaInfoProdottoControllerIntegrationTest {
	
	private GestioneCatalogoServiceImpl gcs;
	private ModificaInfoProdottoController modificaController;
	private ProdottoDAODataSource productDAO;
	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private int pr_pagina; 
	
	@BeforeEach
	public void setUp() throws IOException {
		pr_pagina = 50; 
		PhotoControl photoControl = mock(PhotoControl.class);
		productDAO = mock(ProdottoDAODataSource.class);
		
		gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

	}
	
	
	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * DESCRIZIONE IN EVIDENZA.
	 * 
	 * TC16_1.1_2 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di presentazione,
	 * 				la nuova descrizione di presentazione è un testo vuoto
	 * 				
	 * TC16_1.1_3 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di presentazione,
	 * 				la nuova descrizione di presentazione non è un testo vuoto,
	 * 				nuova top descrizione == vecchia top descrizione
	 * 
	 * TC16_1.1_4 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di presentazione,
	 * 				la nuova descrizione di presentazione non è un testo vuoto,
	 * 				nuova top descrizione != vecchia top descrizione
	 * 
	 * */
	
	@Test
	public void testDoPost_TC16_1_1_2() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"descriptions\": {"
		        + "       \"topDescrizione\": \"\""
		        + "       }"
		        + "   },"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";


		String field = "DESCRIZIONE_EVIDENZA";
		String modifiedValue = "";
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		assertThrows(FormatoTopDescrizioneException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});
		
	}
	
	@Test
	public void testDoPost_TC16_1_1_3() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"descriptions\": {"
		        + "       \"topDescrizione\": \"Prova\""
		        + "       }"
		        + "   },"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";


		String field = "DESCRIZIONE_EVIDENZA";
		String modifiedValue = "Prova";
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		assertThrows(ProdottoAggiornatoException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});
		
	}
	
	@Test
	public void testDoPost_TC16_1_1_4() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		
		
		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(originalProxyProduct);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"descriptions\": {"
		        + "       \"topDescrizione\": \"Nuova Descrizione in evidenza\""
		        + "       }"
		        + "   },"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";


		String field = "DESCRIZIONE_EVIDENZA";
		String modifiedValue = "Nuova Descrizione in evidenza";
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		ProxyProdotto originalProductModified = new ProxyProdotto(3, "Xiaomi Redmi Note 13", modifiedValue, "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		
		catalogo.remove(originalProxyProduct);
		catalogo.add(originalProductModified);
		
		when(productDAO.updateData(originalProduct.getCodiceProdotto(), "TOPDESCRIZIONE", modifiedValue)).thenReturn(true);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(catalogo);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:DESCRIZIONE_EVIDENZA Nuovo ValoreNuova Descrizione in evidenza\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
		
	}
	
	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * DESCRIZIONE DI DETTAGLIO.
	 * 
	 * TC16_1.1_5 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di dettaglio,
	 * 				la nuova descrizione di dettaglio è un testo vuoto
	 * 
	 * TC16_1.1_6 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di dettaglio,
	 * 				la nuova descrizione di dettaglio non è un testo vuoto,
	 * 				nuova descrizione dettaglio == vecchia descrizione dettaglio
	 * 				
	 * TC16_1.1_7 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di dettaglio,
	 * 				la nuova descrizione di dettaglio non è un testo vuoto,
	 * 				nuova descrizione dettaglio != vecchia descrizione dettaglio
	 * 
	 * */
	
	@Test
	public void testDoPost_TC16_1_1_5() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"descriptions\": {"
		        + "       \"dettagli\": \"\""
		        + "       }"
		        + "   },"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";


		String field = "DESCRIZIONE_DETTAGLIATA";
		String modifiedValue = "";
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		assertThrows(FormatoDettagliException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});
		
	}
	
	@Test
	public void testDoPost_TC16_1_1_6() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"descriptions\": {"
		        + "       \"dettagli\": \"Prova\""
		        + "       }"
		        + "   },"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";


		String field = "DESCRIZIONE_DETTAGLIATA";
		String modifiedValue = "Prova";
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		assertThrows(ProdottoAggiornatoException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});
		
	}
	
	@Test
	public void testDoPost_TC16_1_1_7() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		
		
		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(originalProxyProduct);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"descriptions\": {"
		        + "       \"dettagli\": \"Nuova Descrizione dettagliata\""
		        + "       }"
		        + "   },"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";


		String field = "DESCRIZIONE_DETTAGLIATA";
		String modifiedValue = "Nuova Descrizione dettagliata";
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		ProxyProdotto originalProductModified = new ProxyProdotto(3, "Xiaomi Redmi Note 13", modifiedValue, "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		
		catalogo.remove(originalProxyProduct);
		catalogo.add(originalProductModified);
		
		when(productDAO.updateData(originalProduct.getCodiceProdotto(), "DESCRIZIONE_DETTAGLIATA", modifiedValue)).thenReturn(true);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(catalogo);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:DESCRIZIONE_DETTAGLIATA Nuovo ValoreNuova Descrizione dettagliata\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * MODELLO.
	 * 
	 * TC16_1.1_8 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare il modello,
	 * 				il nuovo modello non è espresso nel formato corretto
	 * 
	 * TC16_1.1_9 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare il modello,
	 * 				il nuovo modello è espresso nel formato corretto
	 * 				nuovo modello == vecchio modello
	 * 				
	 * TC16_1.2_0 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare il modello,
	 * 				il nuovo modello è espresso nel formato corretto
	 * 				nuovo modello != vecchio modello
	 * 
	 * */
	
	@Test
	public void testDoPost_TC16_1_1_8() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"productDetails\": {"
		        + "       \"modello\": \"%errorModello-&\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "MODELLO";
		String modifiedValue = "%errorModello-&";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(FormatoModelloException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_1_9() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"productDetails\": {"
		        + "       \"modello\": \"Redmi Note 13\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "MODELLO";
		String modifiedValue = "Redmi Note 13";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(ProdottoAggiornatoException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_2_0() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		
		
		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(originalProxyProduct);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"productDetails\": {"
		        + "       \"modello\": \"PROVA56-3\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "MODELLO";
		String modifiedValue = "PROVA56-3";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		ProxyProdotto originalProductModified = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", modifiedValue, 180, true, false, productDAO);
		
		catalogo.remove(originalProxyProduct);
		catalogo.add(originalProductModified);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		when(productDAO.updateData(originalProduct.getCodiceProdotto(), field, modifiedValue)).thenReturn(true);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(catalogo);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:MODELLO Nuovo ValorePROVA56-3\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);

	}
	
	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * MARCA.
	 * 
	 * TC16_1.2_1 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la marca,
	 * 				la marca non è espressa nel formato corretto
	 * 
	 * TC16_1.2_2 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la marca,
	 * 				la marca è espressa nel formato corretto,
	 * 				nuova marca == vecchia marca
	 * 				
	 * TC16_1.2_3 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la marca,
	 * 				la marca è espressa nel formato corretto,
	 * 				nuova marca != vecchia marca
	 * 
	 * */
	
	@Test
	public void testDoPost_TC16_1_2_1() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"productDetails\": {"
		        + "       \"marca\": \"errorMARC4-\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "MARCA";
		String modifiedValue = "errorMARC4-";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(FormatoMarcaException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_2_2() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"productDetails\": {"
		        + "       \"marca\": \"Xiaomi\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "MARCA";
		String modifiedValue = "Xiaomi";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(ProdottoAggiornatoException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_2_3() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		
		
		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(originalProxyProduct);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"productDetails\": {"
		        + "       \"marca\": \"NUOVA MARCA\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "MARCA";
		String modifiedValue = "NUOVA MARCA";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		ProxyProdotto originalProductModified = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", modifiedValue, 180, true, false, productDAO);
		
		catalogo.remove(originalProxyProduct);
		catalogo.add(originalProductModified);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		when(productDAO.updateData(originalProduct.getCodiceProdotto(), field, modifiedValue)).thenReturn(true);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(catalogo);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:MARCA Nuovo ValoreNUOVA MARCA\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);

	}
	
	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * CATEGORIA.
	 * 
	 * TC16_1.2_4 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la categoria,
	 * 				la categoria non appartiene a {TELEFONIA, PRODOTTI_ELETTRONICA, 
	 * 				PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI}
	 * 
	 * TC16_1.2_5 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la categoria,
	 * 				la categoria appartiene a {TELEFONIA, PRODOTTI_ELETTRONICA, 
	 * 				PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI},
	 * 				nuova categoria == vecchia categoria
	 * 				
	 * TC16_1.2_6 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la categoria,
	 * 				la categoria appartiene a {TELEFONIA, PRODOTTI_ELETTRONICA, 
	 * 				PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI},
	 * 				nuova categoria != vecchia categoria
	 * 
	 * */
	
	@Test
	public void testDoPost_TC16_1_2_4() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"categoria\": \"Prodotti_Elettronica\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "CATEGORIA";
		String modifiedValue = "errorCATEGORIA";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(CategoriaProdottoException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_2_5() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"categoria\": \"Prodotti_Elettronica\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "CATEGORIA";
		String modifiedValue = "TELEFONIA";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(ProdottoAggiornatoException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_2_6() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		
		
		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(originalProxyProduct);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"categoria\": \"Prodotti_Elettronica\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "CATEGORIA";
		String modifiedValue = "PRODOTTI_ELETTRONICA";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		ProxyProdotto originalProductModified = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTPHONE, "Xiaomi", modifiedValue, 180, true, false, productDAO);
		
		catalogo.remove(originalProxyProduct);
		catalogo.add(originalProductModified);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		when(productDAO.updateData(originalProduct.getCodiceProdotto(), field, modifiedValue)).thenReturn(true);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(catalogo);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:CATEGORIA Nuovo ValoreProdotti_Elettronica\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);

	}
	
	
	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * SOTTOCATEGORIA.
	 * 
	 * TC16_1.2_7 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la sottocategoria,
	 * 				la sottocategoria non appartiene a {TABLET, SMARTPHONE, 
	 * 				PC, SMARTWATCH}
	 * 
	 * TC16_1.2_8 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la sottocategoria,
	 * 				la sottocategoria appartiene a {TABLET, SMARTPHONE, 
	 * 				PC, SMARTWATCH},
	 * 				nuova sottocategoria == vecchia sottocategoria
	 * 				
	 * TC16_1.2_9 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la sottocategoria,
	 * 				la sottocategoria appartiene a {TABLET, SMARTPHONE, 
	 * 				PC, SMARTWATCH},
	 * 				nuova categoria != vecchia categoria,
	 * 				sottocategoria non è associata alla categoria del prodotto
	 * 
	 * TC16_1.3_0 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la sottocategoria,
	 * 				la sottocategoria appartiene a {TABLET, SMARTPHONE, 
	 * 				PC, SMARTWATCH},
	 * 				nuova categoria != vecchia categoria,
	 * 				sottocategoria è associata alla categoria del prodotto
	 * 
	 * */

	@Test
	public void testDoPost_TC16_1_2_7() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"sottocategoria\": \"errorSOTTOCATEGORIA\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "SOTTOCATEGORIA";
		String modifiedValue = "errorSOTTOCATEGORIA";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(SottocategoriaProdottoException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_2_8() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"sottocategoria\": \"SMARTPHONE\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "SOTTOCATEGORIA";
		String modifiedValue = "SMARTPHONE";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(ProdottoAggiornatoException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_2_9() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"sottocategoria\": \"PC\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "SOTTOCATEGORIA";
		String modifiedValue = "PC";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(AppartenenzaSottocategoriaException.class , () -> {
			gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_1_3_0() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		
		
		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(originalProxyProduct);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"sottocategoria\": \"TABLET\""
		        + "}"
		        + "},"
		        + "\"productId\": \"3\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Xiaomi\","
		        + "   \"modello\": \"Redmi Note 13\","
		        + "   \"nomeProdotto\": \"Xiaomi Redmi Note 13\","
		        + "   \"prezzo\": 229.90,"
		        + "   \"quantita\": 180,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"Telefonia\","
		        + "   \"sottocategoria\": \"Smartphone\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "SOTTOCATEGORIA";
		String modifiedValue = "TABLET";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		ProxyProdotto originalProductModified = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.TABLET, "Xiaomi", modifiedValue, 180, true, false, productDAO);
		
		catalogo.remove(originalProxyProduct);
		catalogo.add(originalProductModified);
		
		when(productDAO.doRetrieveCompleteByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProduct);
		when(productDAO.updateData(originalProduct.getCodiceProdotto(), field, modifiedValue)).thenReturn(true);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(catalogo);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:SOTTOCATEGORIA Nuovo ValoreTABLET\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	/**
	 * TEST CASES MODIFICA DELLA MESSA IN EVIDENZA DI UN PRODOTTO
	 * 
	 * TC16_2.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la messa in evidenza di un prodotto,
	 * 			   il nuovo valore non appartiene a {0, 1}
	 * 
	 * TC16_2.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la messa in evidenza di un prodotto,
	 * 			   il nuovo valore appartiene a {0, 1},
	 * 			   nuovo valore vetrina == vecchio valore vetrina
	 * 
	 * TC16_2.1_3: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la messa in evidenza di un prodotto,
	 * 			   il nuovo valore appartiene a {0, 1},
	 * 			   nuovo valore vetrina != vecchio valore vetrina
	 * 
	 * */
	
	/*.....................*/
	
	/**
	 * TEST CASES MODIFICA DEL PREZZO DI UN PRODOTTO
	 * 
	 * TC16_3.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è il prezzo di un prodotto,
	 * 			   il nuovo valore non è un numero con la virgola arrotondato in centesimi
	 * 
	 * TC16_3.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è il prezzo di un prodotto,
	 * 			   il nuovo valore è un numero con la virgola arrotondato in centesimi,
	 * 			   nuovo prezzo == vecchio prezzo
	 * 
	 * TC16_3.1_3: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è il prezzo di un prodotto,
	 * 			   il nuovo valore è un numero con la virgola arrotondato in centesimi,
	 * 			   nuovo prezzo != vecchio prezzo
	 * 
	 * */
	
	@Test
	public void testDoPost_TC16_3_1_1() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		Prodotto originalProduct = new Prodotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);

		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"pricing\": {"
		        + "       \"price\": null"
		        + "   }"
		        + "},"
		        + "\"productId\": \"12\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"HP\","
		        + "   \"modello\": \"15s-fq5040nl\","
		        + "   \"nomeProdotto\": \"HP 15s-fq5040nl\","
		        + "   \"prezzo\": 454.50,"
		        + "   \"quantita\": 0,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"PRODOTTI_ELETTRONICA\","
		        + "   \"sottocategoria\": \"PC\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "PREZZO";
		String modifiedValue = "";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveProxyByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProxyProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(PrezzoProdottoException.class , () -> {
			gcs.aggiornamentoPrezzoProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_3_1_2() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		Prodotto originalProduct = new Prodotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);

		ProxyProdotto originalProxyProduct = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);

		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"pricing\": {"
		        + "       \"price\": 454.50"
		        + "   }"
		        + "},"
		        + "\"productId\": \"12\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"HP\","
		        + "   \"modello\": \"15s-fq5040nl\","
		        + "   \"nomeProdotto\": \"HP 15s-fq5040nl\","
		        + "   \"prezzo\": 454.50,"
		        + "   \"quantita\": 0,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"PRODOTTI_ELETTRONICA\","
		        + "   \"sottocategoria\": \"PC\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "PREZZO";
		String modifiedValue = "454.50";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveProxyByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProxyProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(ProdottoAggiornatoException.class , () -> {
			gcs.aggiornamentoPrezzoProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_3_1_3() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);

		ProxyProdotto originalProxyProduct = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);

		
		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(originalProxyProduct);
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"pricing\": {"
		        + "       \"price\": 359.99"
		        + "   }"
		        + "},"
		        + "\"productId\": \"12\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"HP\","
		        + "   \"modello\": \"15s-fq5040nl\","
		        + "   \"nomeProdotto\": \"HP 15s-fq5040nl\","
		        + "   \"prezzo\": 454.50,"
		        + "   \"quantita\": 0,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"PRODOTTI_ELETTRONICA\","
		        + "   \"sottocategoria\": \"PC\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "PREZZO";
		String modifiedValue = "359.99";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		ProxyProdotto originalProductModified = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("359.99"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);

		
		catalogo.remove(originalProxyProduct);
		catalogo.add(originalProductModified);
		
		when(productDAO.doRetrieveProxyByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProxyProduct);
		when(productDAO.updatePrice(originalProduct.getCodiceProdotto(), Float.parseFloat(modifiedValue))).thenReturn(true);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(catalogo);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Aggiornamento Prezzo Avvenuto con Successo!\"}";
		verify(response.getWriter()).write(expectedJsonResponse);

	}
	
	
	/**
	 * TEST CASES MODIFICA DELLA QUANTITA' DI UN PRODOTTO
	 * 
	 * TC16_4.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la quantità in magazzino del prodotto,
	 * 			   il nuovo valore non è un intero positivo
	 * 
	 * TC16_4.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la quantità in magazzino del prodotto,
	 * 			   il nuovo valore è un intero positivo,
	 * 			   nuova quantità == vecchia quantità
	 * 
	 * TC16_4.1_3: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la quantità in magazzino del prodotto,
	 * 			   il nuovo valore è un intero positivo,
	 * 			   nuova quantità != vecchia quantità
	 * 
	 * */
	
	@Test
	public void testDoPost_TC16_4_1_1() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto originalProduct = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"quantita\": {"
		        + "       \"quantita\": -23"
		        + "   }"
		        + "},"
		        + "\"productId\": \"0\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Apple\","
		        + "   \"modello\": \"AirPods Pro 2\","
		        + "   \"nomeProdotto\": \"Apple AirPods Pro 2\","
		        + "   \"prezzo\": 254.50,"
		        + "   \"quantita\": 4,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"PRODOTTI_ELETTRONICA\","
		        + "   \"sottocategoria\": \"\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "QUANTITA";
		String modifiedValue = "-23";
		
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveProxyByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProxyProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(QuantitaProdottoException.class , () -> {
			gcs.aggiornamentoDisponibilitàProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_4_1_2() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto originalProduct = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"quantita\": {"
		        + "       \"quantita\": 4"
		        + "   }"
		        + "},"
		        + "\"productId\": \"0\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Apple\","
		        + "   \"modello\": \"AirPods Pro 2\","
		        + "   \"nomeProdotto\": \"Apple AirPods Pro 2\","
		        + "   \"prezzo\": 254.50,"
		        + "   \"quantita\": 4,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"PRODOTTI_ELETTRONICA\","
		        + "   \"sottocategoria\": \"\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "QUANTITA";
		String modifiedValue = "4";
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveProxyByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProxyProduct);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);

		assertThrows(ProdottoAggiornatoException.class , () -> {
			gcs.aggiornamentoDisponibilitàProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
		});

	}
	
	@Test
	public void testDoPost_TC16_4_1_3() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		ProxyProdotto originalProxyProduct = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto originalProduct = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"quantita\": {"
		        + "       \"quantita\": 2"
		        + "   }"
		        + "},"
		        + "\"productId\": \"0\","
		        + "\"originalProductDetails\": {"
		        + "   \"marca\": \"Apple\","
		        + "   \"modello\": \"AirPods Pro 2\","
		        + "   \"nomeProdotto\": \"Apple AirPods Pro 2\","
		        + "   \"prezzo\": 254.50,"
		        + "   \"quantita\": 4,"
		        + "   \"topDescrizione\": \"Prova\","
		        + "   \"dettagli\": \"Prova\","
		        + "   \"categoria\": \"PRODOTTI_ELETTRONICA\","
		        + "   \"sottocategoria\": \"\","
		        + "   \"inVetrina\": false,"
		        + "   \"inCatalogo\": true"
		        + "}"
		        + "}";
		
		String field = "QUANTITA";
		String modifiedValue = "2";
		
		
		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(originalProxyProduct);
		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		ProxyProdotto originalProductModified = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 2, true, false, productDAO);

		
		catalogo.remove(originalProxyProduct);
		catalogo.add(originalProductModified);
		
		when(productDAO.doRetrieveProxyByKey(originalProduct.getCodiceProdotto())).thenReturn(originalProxyProduct);
		when(productDAO.updateQuantity(originalProduct.getCodiceProdotto(), Integer.parseInt(modifiedValue))).thenReturn(true);
		when(productDAO.doRetrieveAllExistent(null, 1, pr_pagina)).thenReturn(catalogo);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Aggiornamento Quantità Avvenuto con Successo!\"}";
		verify(response.getWriter()).write(expectedJsonResponse);

	}

}
