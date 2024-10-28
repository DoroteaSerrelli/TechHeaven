package application.GestioneCatalogoController;

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

import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.GestioneCatalogoControl.ModificaInfoProdottoController;
import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoAggiornatoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.NavigazioneService.ProdottoException.FormatoModelloException;
import application.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class ModificaInfoProdottoControllerTest {
	
	private ModificaInfoProdottoController modificaController;
	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private GestioneCatalogoServiceImpl gcs;

	@BeforeEach
	public void setUp() throws IOException {
		
		gcs = mock(GestioneCatalogoServiceImpl.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

	}
	

	@Test
	public void testDoPost_ModificaModelloSuccesso() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"productDetails\": {"
		        + "       \"modello\": \"Nuovo RedmiNote 13\""
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

		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:MARCA Nuovo Valorenull\\nField aggiornata con successo:MODELLO Nuovo ValoreNuovo RedmiNote 13\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);

	}
	
	
	
	@Test
	public void testDoPost_ModificaMarcaSuccesso() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"productDetails\": {"
		        + "       \"marca\": \"NuovaMarcaX\""
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

		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:MARCA Nuovo ValoreNuovaMarcaX\\nField aggiornata con successo:MODELLO Nuovo Valorenull\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	
	@Test
	public void testDoPost_ModificaTopDescrizioneSuccesso() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"descriptions\": {"
		        + "       \"topDescrizione\": \"Nuova descrizione top\""
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


		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:DESCRIZIONE_EVIDENZA Nuovo ValoreNuova descrizione top\\nField aggiornata con successo:DESCRIZIONE_DETTAGLIATA Nuovo Valorenull\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	@Test
	public void testDoPost_ModificaDettagliSuccesso() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"descriptions\": {"
		        + "       \"dettagli\": \"Nuova descrizione dettagli\""
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


		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:DESCRIZIONE_EVIDENZA Nuovo Valorenull\\nField aggiornata con successo:DESCRIZIONE_DETTAGLIATA Nuovo ValoreNuova descrizione dettagli\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	@Test
	public void testDoPost_ModificaPrezzoSuccesso() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"pricing\": {"
		        + "       \"price\": 99.99"
		        + "   }"
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

		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Aggiornamento Prezzo Avvenuto con Successo!\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	@Test
	public void testDoPost_ModificaCategoriaSuccesso() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"categoria\": \"Prodotti_Elettronica\""
		        + "   }"
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

		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:CATEGORIA Nuovo ValoreProdotti_Elettronica\\nField aggiornata con successo:SOTTOCATEGORIA Nuovo Valorenull\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	@Test
	public void testDoPost_ModificaSottocategoriaSuccesso() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"category\": {"
		        + "       \"sottocategoria\": \"Tablet\""
		        + "   }"
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

		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Field aggiornata con successo:CATEGORIA Nuovo Valorenull\\nField aggiornata con successo:SOTTOCATEGORIA Nuovo ValoreTablet\\n\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	@Test
	public void testDoPost_ModificaQuantitàSuccesso() throws IOException, ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SQLException, ServletException {
		
		
		Prodotto originalProduct = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);
		
		
		String inputJson = "{"
		        + "\"modifiedData\": {"
		        + "   \"quantita\": {"
		        + "       \"quantita\": 10"
		        + "   }"
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

		
		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(inputJson)));
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		modificaController = new ModificaInfoProdottoController(gcs);
		
		modificaController.doPost(request, response);
		
		String expectedJsonResponse = "{\"redirectUrl\":\"/test/Catalogo\",\"message\":\"Aggiornamento Quantità Avvenuto con Successo!\"}";
		verify(response.getWriter()).write(expectedJsonResponse);
	}
	
	
}
