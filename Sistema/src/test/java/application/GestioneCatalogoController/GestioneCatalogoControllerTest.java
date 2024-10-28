package application.GestioneCatalogoController;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import application.GestioneCatalogoControl.GestioneCatalogoController;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneService.ProxyProdotto;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class GestioneCatalogoControllerTest {

	private GestioneCatalogoController catalogoController;
	private ProdottoDAODataSource productDAO;
	private ServletOutputStream outputStream;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private GestioneCatalogoServiceImpl gcs;
	private PaginationUtils pu;

	@BeforeEach
	public void setUp() throws ServletException, IOException {
		pu = mock(PaginationUtils.class);
		gcs = mock(GestioneCatalogoServiceImpl.class);
		productDAO = mock(ProdottoDAODataSource.class);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}

	@Test
	public void testDoPost_AggiuntaProdottoSuccesso() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);
		
		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);
		
		
		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat("340.99");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;
		
		Part filePart = null; 
		String action = "addProduct";
		
		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(filePart);	//senza topImmagine
		when(request.getParameter("productId")).thenReturn(code);
		
		when(request.getParameter("action")).thenReturn(action);
		
		catalogoController = new GestioneCatalogoController(productDAO, gcs, null);
		
		catalogoController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("error", "Prodotto Aggiunto con Successo!");
		verify(response).sendRedirect(request.getContextPath() + "/GestioneCatalogo");
		
	}


	@Test
	public void testDoPost_AggiuntaProdottoConTopImmagineSuccesso() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);
		
		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);
		
		
		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat("340.99");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;
		
		InputStream topImmagine = new ByteArrayInputStream(new byte[]{
	            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, // Header PNG
	            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, // IHDR
	            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1x1 pixel
	            (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xB3, (byte) 0x51, (byte) 0x22, // Bit depth and color type
	            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IDAT chunk
	            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // IEND
	            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
			});
		
		Part filepart = mock(Part.class);

		when(request.getPart("file")).thenReturn(filepart); 
		when(filepart.getInputStream()).thenReturn(topImmagine);
		
		String action = "addProduct";
		
		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		when(request.getPart("file")).thenReturn(filepart);	//con topImmagine
		when(request.getParameter("productId")).thenReturn(code);
		
		when(request.getParameter("action")).thenReturn(action);
		
		catalogoController = new GestioneCatalogoController(productDAO, gcs, null);
		
		catalogoController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("error", "Prodotto Con Top immagine Aggiunto con Successo!");
		verify(response).sendRedirect(request.getContextPath() + "/GestioneCatalogo");
		
	}	
	
	@Test
	public void testDoPost_RimozioneProdottoSuccesso() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);
		
		ProxyProdotto product3 = new ProxyProdotto(22, "Samsung Gear S2 Classic", "Lorem ipsum", "Lorem ipsum Lorem", Float.parseFloat("340.99"), 
				Categoria.PRODOTTI_ELETTRONICA, "Samsung", "Gear-S2", 2, true, false, productDAO);

		
		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);
		pageProducts.add(product3);
		
		
		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String priceStr = "340.99";
		float price = Float.parseFloat("340.99");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		String quantityStr = "2";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		String inVetrinaStr = "false";
		String inCatalogoStr = "true";
		boolean inCatalogo = true;
		boolean inVetrina = false;
		
		
		
		String action = "deleteProduct";
		
		when(request.getParameter("productName")).thenReturn(name);
		when(request.getParameter("topDescrizione")).thenReturn(topDescription);
		when(request.getParameter("dettagli")).thenReturn(details);
		when(request.getParameter("price")).thenReturn(priceStr);
		when(request.getParameter("categoria")).thenReturn(category);
		when(request.getParameter("marca")).thenReturn(brand);
		when(request.getParameter("modello")).thenReturn(model);
		when(request.getParameter("quantita")).thenReturn(quantityStr);
		when(request.getParameter("sottocategoria")).thenReturn(subCategory);
		when(request.getParameter("inVetrina")).thenReturn(inVetrinaStr);
		when(request.getParameter("inCatalogo")).thenReturn(inCatalogoStr);
		
		when(request.getParameter("productId")).thenReturn(code);
		
		when(request.getParameter("action")).thenReturn(action);
		
		catalogoController = new GestioneCatalogoController(productDAO, gcs, null);
		
		catalogoController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("error", "Prodotto Eliminato con Successo!");
		verify(response).sendRedirect(request.getContextPath() + "/GestioneCatalogo");
		
	}	
	
}
