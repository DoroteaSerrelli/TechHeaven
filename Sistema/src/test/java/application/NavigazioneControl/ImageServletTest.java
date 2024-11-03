package application.NavigazioneControl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ByteArrayOutputStream;

import application.GestioneCatalogo.GestioneCatalogoControl.ImageResizer;
import application.Navigazione.NavigazioneControl.ImageServlet;
import application.Navigazione.NavigazioneService.Prodotto;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class ImageServletTest {

	private ImageServlet imageServlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	private ProdottoDAODataSource productDAO;
	private PhotoControl photoControl;
	private DataSource ds;

	@Test
	public void testProcessRequest_ProductIdMancante() throws Exception {
		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		when(request.getParameter("productId")).thenReturn(null);

		imageServlet = new ImageServlet(ds, photoControl, productDAO);
		imageServlet.processRequest(request, response);

		verify(request.getSession()).setAttribute("errorMessage", "Non è possibile visualizzare le immagini del prodotto richiesto perché il codice del prodotto non è specificato");
		verify(response).sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
	}

	@Test
	public void testProcessRequest_ProdottoInesistente() throws Exception {
		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		imageServlet = new ImageServlet(ds, photoControl, productDAO);

		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		when(request.getParameter("productId")).thenReturn("1");
		when(imageServlet.productDAO.doRetrieveProxyByKey(1)).thenReturn(null);

		imageServlet.processRequest(request, response);

		verify(request.getSession()).setAttribute("errorMessage", "Non è possibile visualizzare le immagini del prodotto richiesto perché il prodotto specificato non esiste in catalogo.");
		verify(response).sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
	}

	@Test
	public void testProcessRequest_ProdottoSenzaTopImmagine() throws Exception {
		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);

		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		final ServletContext mockServletContext = mock(ServletContext.class);

		imageServlet = new ImageServlet(ds, photoControl, productDAO) {
			@Override
			public ServletContext getServletContext() {
				return mockServletContext;
			}
		};

		// Mock prodotto senza immagine di presentazione
		Prodotto prodottoMock = mock(Prodotto.class);
		when(prodottoMock.getTopImmagine()).thenReturn(null);

		ProxyProdotto proxyMock = mock(ProxyProdotto.class);
		when(proxyMock.mostraProdotto()).thenReturn(prodottoMock);
		when(imageServlet.productDAO.doRetrieveProxyByKey(1)).thenReturn(proxyMock);
		when(request.getParameter("productId")).thenReturn("1");

		// Percorso per l'immagine di placeholder PNG
		String placeholderPath = "C://Users//dorot//git//TechHeaven//Sistema//src//main//webapp//images//site_images//placeholder.png";
		when(mockServletContext.getRealPath("/images/site_images/placeholder.png")).thenReturn(placeholderPath);
		when(imageServlet.getServletContext().getRealPath("/images/site_images/placeholder.png")).thenReturn(placeholderPath);

		// Simula la scrittura dell'immagine PNG
		BufferedImage placeholderImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
		ImageIO.write(placeholderImage, "png", new File(placeholderPath)); // Salva come PNG

		imageServlet.processRequest(request, response);

		// Cattura l'output
		ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
		verify(outputStream).write(captor.capture());
		byte[] imageBytesWritten = captor.getValue();

		// Verifica che l'immagine JPEG sia stata scritta
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(placeholderImage, "jpeg", baos);
		byte[] expectedBytes = baos.toByteArray();

		assertEquals(expectedBytes.length, imageBytesWritten.length); // Confronta le lunghezze
	}


	@Test
	public void testProcessRequest_DimensioniNonValide() throws Exception {

		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);

		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		final ServletContext mockServletContext = mock(ServletContext.class);

		imageServlet = new ImageServlet(ds, photoControl, productDAO) {
			@Override
			public ServletContext getServletContext() {
				return mockServletContext;
			}
		};

		// Mock prodotto con immagine di presentazione
		Prodotto prodottoMock = mock(Prodotto.class);
		byte[] topImmagine = new byte[] {(byte) 255, (byte) 0, (byte) 0 };
		when(prodottoMock.getTopImmagine()).thenReturn(topImmagine);

		ProxyProdotto proxyMock = mock(ProxyProdotto.class);
		when(proxyMock.mostraProdotto()).thenReturn(prodottoMock);
		when(imageServlet.productDAO.doRetrieveProxyByKey(1)).thenReturn(proxyMock);
		when(request.getParameter("productId")).thenReturn("1");

		// Dimensioni immagine non valide
		when(request.getParameter("width")).thenReturn("invalid");
		when(request.getParameter("height")).thenReturn("200");

		imageServlet.processRequest(request, response);

		verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Dimensioni dell'immagine fornite non valide");
	}

	@Test
	public void testProcessRequest_Successo() throws Exception {
	    productDAO = mock(ProdottoDAODataSource.class);
	    photoControl = mock(PhotoControl.class);
	    ds = mock(DataSource.class);

	    outputStream = mock(ServletOutputStream.class);
	    request = mock(HttpServletRequest.class);
	    response = mock(HttpServletResponse.class);

	    when(response.getOutputStream()).thenReturn(outputStream);
	    when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
	    when(request.getContextPath()).thenReturn("/test");

	    final ServletContext mockServletContext = mock(ServletContext.class);

	    imageServlet = new ImageServlet(ds, photoControl, productDAO) {
	        @Override
	        public ServletContext getServletContext() {
	            return mockServletContext;
	        }
	    };

	    // Mock prodotto con immagine di presentazione
	    Prodotto prodottoMock = mock(Prodotto.class);
	    
	    // Carica un'immagine valida dal file system
	    BufferedImage image = ImageIO.read(new File("C://Users//dorot//git//TechHeaven//Sistema//src//main//webapp//images//product_images//appleWatchSE_top.jpg"));
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(image, "jpg", baos);
	    byte[] topImmagine = baos.toByteArray();

	    when(prodottoMock.getTopImmagine()).thenReturn(topImmagine);

	    ProxyProdotto proxyMock = mock(ProxyProdotto.class);
	    when(proxyMock.mostraProdotto()).thenReturn(prodottoMock);
	    when(imageServlet.productDAO.doRetrieveProxyByKey(1)).thenReturn(proxyMock);
	    when(request.getParameter("productId")).thenReturn("1");

	    // Dimensioni immagine valide
	    when(request.getParameter("width")).thenReturn("300");
	    when(request.getParameter("height")).thenReturn("200");

	    // Invocazione metodo da testare
	    imageServlet.processRequest(request, response);
	    
	    
	    // Cattura l'output
	    ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
	    verify(outputStream).write(captor.capture());
	    byte[] imageBytesWritten = captor.getValue();
	    
	    
	   //Output atteso
		BufferedImage originalImage = ImageResizer.byteArrayToImage(topImmagine);
		BufferedImage imageToServe = ImageResizer.resizeImage(originalImage, 300, 200);
		// Convert BufferedImage back to byte array
		byte[] imageBytes = ImageResizer.imageToByteArray(imageToServe, "jpg");

		//Verifica
	    assertEquals(imageBytes.length, imageBytesWritten.length);
	}

}
