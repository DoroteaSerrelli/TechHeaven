package application.GestioneCatalogoControl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import application.GestioneCatalogo.GestioneCatalogoControl.GestioneImmaginiProdotto;
import application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.Navigazione.NavigazioneService.Prodotto;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.DettagliImmagineNonPresenteException;
import application.Navigazione.NavigazioneService.ProdottoException.ErroreDettagliImmagineException;
import application.Navigazione.NavigazioneService.ProdottoException.ErroreTopImmagineException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneImmaginiProdottoIntegrationTest {

	private GestioneImmaginiProdotto immaginiController;
	private ProdottoDAODataSource productDAO;
	private PhotoControl photoControl;
	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private GestioneCatalogoServiceImpl gcs;

	private int perPage;

	@BeforeEach
	public void setUp() throws IOException {
		perPage = 50;

		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

	}


	private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, length);
		}
		return byteArrayOutputStream.toByteArray();
	}



	/**
	 * TEST CASES MODIFICA DELL'IMMAGINE DI PRESENTAZIONE
	 * DI UN PRODOTTO
	 * 
	 * TC16_5.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è l'immagine di presentazione del prodotto,
	 * 			   la nuova immagine di presentazione non è stata specificata
	 * 
	 * TC16_5.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è l'immagine di presentazione del prodotto,
	 * 			   la nuova immagine di presentazione è stata specificata
	 * 
	 * */


	@Test
	public void testDoPost_TC16_5_1_1() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException, ErroreTopImmagineException {

		immaginiController = new GestioneImmaginiProdotto(gcs);

		ProxyProdotto prodottoProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto prodotto = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		InputStream topImmagine = null;


		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(prodottoProxy);

		Gson gson = new Gson();
		String productJson = gson.toJson(prodotto);
		String main_photoAction = "TOP_IMMAGINE";
		String gallery_photoActions = null;

		when(request.getParameter("product")).thenReturn(productJson);
		when(request.getParameter("main_photoAction")).thenReturn(main_photoAction);
		when(request.getSession().getAttribute("originalGallery")).thenReturn(null);
		when(request.getParameter("gallery_photoActions")).thenReturn(gallery_photoActions);


		// Crea un'istanza di MockPart
		Part part = mock(Part.class);

		when(request.getPart("presentazione")).thenReturn(part); 
		when(part.getInputStream()).thenReturn(topImmagine);


		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		immaginiController.doPost(request, response);

		String message = "Inserire un'immagine di presentazione del prodotto.";
		String jsonResponse = gson.toJson(message);
		verify(response.getWriter()).print(jsonResponse);   
	}

	@Test
	public void testDoPost_TC16_5_1_2() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException, ErroreTopImmagineException {

		immaginiController = new GestioneImmaginiProdotto(gcs);

		ProxyProdotto prodottoProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto prodotto = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		InputStream topImmagine = new ByteArrayInputStream(new byte[]{
				(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});


		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(prodottoProxy);

		Gson gson = new Gson();
		String productJson = gson.toJson(prodotto);
		String main_photoAction = "TOP_IMMAGINE";
		String gallery_photoActions = null;

		when(request.getParameter("product")).thenReturn(productJson);
		when(request.getParameter("main_photoAction")).thenReturn(main_photoAction);
		when(request.getSession().getAttribute("originalGallery")).thenReturn(null);
		when(request.getParameter("gallery_photoActions")).thenReturn(gallery_photoActions);


		// Crea un'istanza di MockPart
		Part part = mock(Part.class);

		when(request.getPart("presentazione")).thenReturn(part); 
		when(part.getInputStream()).thenReturn(topImmagine);


		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		when(productDAO.doRetrieveProxyByKey(prodotto.getCodiceProdotto())).thenReturn(prodottoProxy);

		when(gcs.inserimentoTopImmagine(prodotto, "TOP_IMMAGINE", topImmagine, 1, perPage)).thenReturn(catalogo);
		prodotto.setTopImmagine(inputStreamToByteArray(topImmagine));

		immaginiController.doPost(request, response);

		String message = "Immagine di presentazion inserita con successo.";
		String jsonResponse = gson.toJson(message);
		verify(response.getWriter()).print(jsonResponse);                                  
	}
	
	/**
	 * TEST CASES AGGIUNTA IMMAGINE DI DETTAGLIO PER UN PRODOTTO
	 * 
	 * TC16_6.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è aggiunta immagine di dettaglio per il prodotto,
	 * 			   la nuova immagine di dettaglio non è stata specificata
	 * 
	 * TC16_6.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è aggiunta immagine di dettaglio per il prodotto,
	 * 			   la nuova immagine di dettaglio è stata specificata
	 * 
	 * */
	
	
	@Test
	public void testDoPost_TC16_6_1_1() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

		immaginiController = new GestioneImmaginiProdotto(gcs);

		ProxyProdotto prodottoProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto prodotto = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		InputStream image1 = new ByteArrayInputStream(new byte[]{
				(byte) 0x00, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		InputStream updatedData = null;

		byte[] imageByte1 = inputStreamToByteArray(image1);

		ArrayList<byte[]> images = new ArrayList<>();
		images.add(imageByte1);
		prodotto.setGalleriaImmagini(images);

		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(prodottoProxy);

		Gson gson = new Gson();
		String productJson = gson.toJson(prodotto);
		String main_photoAction = null;
		String gallery_photoActions = "AGGIUNTA_DETT_IMMAGINE";

		when(request.getParameter("product")).thenReturn(productJson);
		when(request.getParameter("main_photoAction")).thenReturn(main_photoAction);
		when(request.getSession().getAttribute("originalGallery")).thenReturn(images);
		when(request.getParameter("gallery_photoActions")).thenReturn(gallery_photoActions);

		// Crea un'istanza di MockPart
		Part part = mock(Part.class);

		when(request.getPart("presentazione")).thenReturn(part); 
		when(part.getInputStream()).thenReturn(updatedData);
		
		when(productDAO.doRetrieveProxyByKey(prodotto.getCodiceProdotto())).thenReturn(prodottoProxy);
		
		assertThrows(ErroreDettagliImmagineException.class, () -> {
				gcs.inserimentoImmagineInGalleriaImmagini(prodotto, "AGGIUNTA_DETT_IMMAGINE", updatedData, 1, perPage);
		});
		
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);


		immaginiController.doPost(request, response);

		String message = "Inserire un'immagine di dettaglio del prodotto.";
		String jsonResponse = gson.toJson(message);
		verify(response.getWriter()).print(jsonResponse);    

	}
	
	
	@Test
	public void testDoPost_TC16_6_1_2() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

		immaginiController = new GestioneImmaginiProdotto(gcs);

		ProxyProdotto prodottoProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto prodotto = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		InputStream image1 = new ByteArrayInputStream(new byte[]{
				(byte) 0x00, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		InputStream updatedData = new ByteArrayInputStream(new byte[]{
				(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		byte[] imageByte1 = inputStreamToByteArray(image1);

		ArrayList<byte[]> images = new ArrayList<>();
		images.add(imageByte1);
		prodotto.setGalleriaImmagini(images);

		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(prodottoProxy);

		Gson gson = new Gson();
		String productJson = gson.toJson(prodotto);
		String main_photoAction = null;
		String gallery_photoActions = "AGGIUNTA_DETT_IMMAGINE";

		when(request.getParameter("product")).thenReturn(productJson);
		when(request.getParameter("main_photoAction")).thenReturn(main_photoAction);
		when(request.getSession().getAttribute("originalGallery")).thenReturn(images);
		when(request.getParameter("gallery_photoActions")).thenReturn(gallery_photoActions);

		// Crea un'istanza di MockPart
		Part part = mock(Part.class);

		when(request.getPart("presentazione")).thenReturn(part); 
		when(part.getInputStream()).thenReturn(updatedData);
		
		when(productDAO.doRetrieveProxyByKey(prodotto.getCodiceProdotto())).thenReturn(prodottoProxy);
		when(gcs.inserimentoImmagineInGalleriaImmagini(prodotto, "AGGIUNTA_DETT_IMMAGINE", updatedData, 1, perPage)).thenReturn(catalogo);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);


		immaginiController.doPost(request, response);

		String message = "L'immagine inserita è stata aggiunta correttamente alla galleria";
		String jsonResponse = gson.toJson(message);
		verify(response.getWriter()).print(jsonResponse); 

	}

	
	/**
	 * TEST CASES RIMOZIONE IMMAGINE DI DETTAGLIO PER UN PRODOTTO
	 * 
	 * TC16_7.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è rimozione immagine di dettaglio per il prodotto,
	 * 			   l'immagine di dettaglio non è stata specificata
	 * 
	 * TC16_7.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è rimozione immagine di dettaglio per il prodotto,
	 * 			   l'immagine di dettaglio è stata specificata, ma non appartiene alla
	 * 			   galleria di immagini del prodotto
	 * 
	 * TC16_7.1_3: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è rimozione immagine di dettaglio per il prodotto,
	 * 			   l'immagine di dettaglio è stata specificata ed appartiene alla
	 * 			   galleria di immagini del prodotto
	 * 
	 * */
	
	@Test
	public void testDoPost_TC16_7_1_1() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

		immaginiController = new GestioneImmaginiProdotto(gcs);

		ProxyProdotto prodottoProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto prodotto = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		InputStream image1 = new ByteArrayInputStream(new byte[]{
				(byte) 0x00, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		InputStream updatedData = new ByteArrayInputStream(new byte[]{
				(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		byte[] imageByte1 = inputStreamToByteArray(image1);
		byte[] imageByte2 = inputStreamToByteArray(updatedData);
		ArrayList<byte[]> images = new ArrayList<>();
		images.add(imageByte1);
		images.add(imageByte2);
		prodotto.setGalleriaImmagini(images);

		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(prodottoProxy);

		Gson gson = new Gson();
		String productJson = gson.toJson(prodotto);
		String main_photoAction = null;
		String gallery_photoActions = "RIMOZIONE_DETT_IMMAGINE";

		when(request.getParameter("product")).thenReturn(productJson);
		when(request.getParameter("main_photoAction")).thenReturn(main_photoAction);
		when(request.getSession().getAttribute("originalGallery")).thenReturn(images);
		when(request.getParameter("gallery_photoActions")).thenReturn(gallery_photoActions);

		when(request.getParameter("imageIndex")).thenReturn(null);

		Prodotto prodottoAggiornato = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		ArrayList<byte[]> newImages = new ArrayList<>();
		newImages.add(imageByte1);
		prodottoAggiornato.setGalleriaImmagini(newImages);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		immaginiController.doPost(request, response);

		String message = "Inserire un'immagine di dettaglio del prodotto.";
		String jsonResponse = gson.toJson(message);
		verify(response.getWriter()).print(jsonResponse);                                  

	}
	
	
	@Test
	public void testDoPost_TC16_7_1_2() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

		immaginiController = new GestioneImmaginiProdotto(gcs);

		ProxyProdotto prodottoProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto prodotto = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		InputStream image1 = new ByteArrayInputStream(new byte[]{
				(byte) 0x00, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		InputStream updatedData = new ByteArrayInputStream(new byte[]{
				(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		byte[] imageByte1 = inputStreamToByteArray(image1);
		byte[] imageByte2 = inputStreamToByteArray(updatedData);
		ArrayList<byte[]> images = new ArrayList<>();
		images.add(imageByte1);
		images.add(imageByte2);
		prodotto.setGalleriaImmagini(images);

		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(prodottoProxy);

		Gson gson = new Gson();
		String productJson = gson.toJson(prodotto);
		String main_photoAction = null;
		String gallery_photoActions = "RIMOZIONE_DETT_IMMAGINE";

		when(request.getParameter("product")).thenReturn(productJson);
		when(request.getParameter("main_photoAction")).thenReturn(main_photoAction);
		when(request.getSession().getAttribute("originalGallery")).thenReturn(images);
		when(request.getParameter("gallery_photoActions")).thenReturn(gallery_photoActions);

		int imageToRemoveIndex = 4; //errore: immagine non associata
		when(request.getParameter("imageIndex")).thenReturn(String.valueOf(imageToRemoveIndex));

		Prodotto prodottoAggiornato = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		ArrayList<byte[]> newImages = new ArrayList<>();
		newImages.add(imageByte1);
		prodottoAggiornato.setGalleriaImmagini(newImages);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		immaginiController.doPost(request, response);

		String message = "L'immagine di dettaglio specificata non è associata al prodotto.\n"
				+ "Scegliere un'altra immagine di dettaglio.";
		String jsonResponse = gson.toJson(message);
		verify(response.getWriter()).print(jsonResponse);  
	}

	@Test
	public void testDoPost_TC16_7_1_3() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

		immaginiController = new GestioneImmaginiProdotto(gcs);

		ProxyProdotto prodottoProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto prodotto = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		InputStream image1 = new ByteArrayInputStream(new byte[]{
				(byte) 0x00, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		InputStream updatedData = new ByteArrayInputStream(new byte[]{
				(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
				(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
				(byte) 0x08, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
				(byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
		});

		byte[] imageByte1 = inputStreamToByteArray(image1);
		byte[] imageByte2 = inputStreamToByteArray(updatedData);
		ArrayList<byte[]> images = new ArrayList<>();
		images.add(imageByte1);
		images.add(imageByte2);
		prodotto.setGalleriaImmagini(images);

		Collection<ProxyProdotto> catalogo = new ArrayList<>();
		catalogo.add(prodottoProxy);

		Gson gson = new Gson();
		String productJson = gson.toJson(prodotto);
		String main_photoAction = null;
		String gallery_photoActions = "RIMOZIONE_DETT_IMMAGINE";

		when(request.getParameter("product")).thenReturn(productJson);
		when(request.getParameter("main_photoAction")).thenReturn(main_photoAction);
		when(request.getSession().getAttribute("originalGallery")).thenReturn(images);
		when(request.getParameter("gallery_photoActions")).thenReturn(gallery_photoActions);

		int imageToRemoveIndex = 1; //la seconda immagine da rimuovere
		when(request.getParameter("imageIndex")).thenReturn(String.valueOf(imageToRemoveIndex));

		Prodotto prodottoAggiornato = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		ArrayList<byte[]> newImages = new ArrayList<>();
		newImages.add(imageByte1);
		prodottoAggiornato.setGalleriaImmagini(newImages);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		when(productDAO.doRetrieveProxyByKey(prodotto.getCodiceProdotto())).thenReturn(prodottoProxy);
		when(gcs.cancellazioneImmagineInGalleria(prodotto, gallery_photoActions, updatedData, 1, perPage)).thenReturn(catalogo);
		immaginiController.doPost(request, response);

		verify(request.getSession()).setAttribute("originalGallery",  newImages);
		String message ="L'immagine selezionata è stata rimossa con successo dalla galleria";
		String jsonResponse = gson.toJson(message);
		verify(response.getWriter()).print(jsonResponse);                             
	}
	
}
