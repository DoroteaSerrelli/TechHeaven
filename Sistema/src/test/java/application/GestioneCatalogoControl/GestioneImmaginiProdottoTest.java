package application.GestioneCatalogoControl;

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
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import application.GestioneCatalogoControl.GestioneImmaginiProdotto;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.DettagliImmagineNonPresenteException;
import application.NavigazioneService.ProdottoException.ErroreDettagliImmagineException;
import application.NavigazioneService.ProdottoException.ErroreTopImmagineException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneImmaginiProdottoTest {

	private GestioneImmaginiProdotto immaginiController;
	private ProdottoDAODataSource productDAO;
	private PhotoControl photoControl;
	private DataSource ds;
	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private GestioneCatalogoServiceImpl gcs;

	private int perPage;

	@BeforeEach
	public void setUp() throws IOException {
		perPage = 50;
		productDAO = mock(ProdottoDAODataSource.class);
		ds = mock(DataSource.class);
		photoControl = mock(PhotoControl.class);
		gcs = mock(GestioneCatalogoServiceImpl.class);

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


	@Test
	public void testDoPost_RimozioneImmagineSuccesso() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

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

		when(gcs.cancellazioneImmagineInGalleria(prodotto, gallery_photoActions, updatedData, 1, perPage)).thenReturn(catalogo);

		immaginiController.doPost(request, response);

		verify(request.getSession()).setAttribute("originalGallery",  newImages);
		verify(response.getWriter()).write("Image deleted successfully.");                                 
	}

	@Test
	public void testDoPost_RimozioneImmagineNonPresente() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

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

		when(gcs.cancellazioneImmagineInGalleria(prodotto, gallery_photoActions, updatedData, 1, perPage)).thenReturn(catalogo);

		immaginiController.doPost(request, response);

		verify(response.getWriter()).write("Image not successfully deleted.");                                 
	}

	@Test
	public void testDoPost_RimozioneImmagineNull() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

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

		when(gcs.cancellazioneImmagineInGalleria(prodotto, gallery_photoActions, updatedData, 1, perPage)).thenReturn(catalogo);

		immaginiController.doPost(request, response);

		verify(response.getWriter()).write("Image not successfully deleted.");                                 

	}

	@Test
	public void testDoPost_TopImmagineSuccesso() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException, ErroreTopImmagineException {

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

		when(gcs.inserimentoTopImmagine(prodotto, "TOP_IMMAGINE", topImmagine, 1, perPage)).thenReturn(catalogo);
		prodotto.setTopImmagine(inputStreamToByteArray(topImmagine));

		immaginiController.doPost(request, response);

		verify(response.getWriter()).write("TopImage successfully added");                                 
	}

	@Test
	public void testDoPost_TopImmagineNull() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException, ErroreTopImmagineException {

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


		verify(response.getWriter()).write("TopImage not successfully added");                               
	}

	@Test
	public void testDoPost_AggiuntaImmagineDettaglioSuccesso() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

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

		when(gcs.inserimentoImmagineInGalleriaImmagini(prodotto, "AGGIUNTA_DETT_IMMAGINE", updatedData, 1, perPage)).thenReturn(catalogo);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);


		immaginiController.doPost(request, response);

		verify(response.getWriter()).write("Detailed Image successfully added");  

	}

	@Test
	public void testDoPost_AggiuntaImmagineDettaglioNull() throws IOException, ServletException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException, SQLException {

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

		when(gcs.inserimentoImmagineInGalleriaImmagini(prodotto, "AGGIUNTA_DETT_IMMAGINE", updatedData, 1, perPage)).thenReturn(catalogo);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);


		immaginiController.doPost(request, response);

		verify(response.getWriter()).write("Detailed Image not successfully added");  

	}

}
