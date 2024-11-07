package application.NavigazioneControl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import org.junit.Test;

import application.Navigazione.NavigazioneControl.ProductInfos;
import application.Navigazione.NavigazioneService.NavigazioneServiceImpl;
import application.Navigazione.NavigazioneService.Prodotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class ProductInfosTest {
	private ProductInfos productInfos;
	private NavigazioneServiceImpl ns;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	private ProdottoDAODataSource productDAO;
	private PhotoControl photoControl;
	private DataSource ds;

	@Test
	public void testDoGet_ProdottoNull() throws Exception {
		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);
		ns = new NavigazioneServiceImpl(productDAO);
		
		
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		productInfos = new ProductInfos(ds, photoControl, productDAO, ns);
		
		String pathImg1 = "C://Users//dorot//git//TechHeaven//Sistema//src//main//webapp//images//product_images//lenovo_ideacentre3_top.jpg";
		List<String> galleryImages = new ArrayList<>();
		galleryImages.add(pathImg1);
		
		when(request.getSession().getAttribute("product")).thenReturn(null);
		when(request.getSession().getAttribute("galleryImages")).thenReturn(galleryImages);
		
		productInfos.doGet(request, response);
		
		verify(response).sendRedirect(request.getContextPath() + "/");
		
	}
	
	@Test
	public void testDoGet_GalleriaImmaginiNull() throws Exception {
		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		Prodotto product = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		when(request.getSession().getAttribute("product")).thenReturn(product);
		when(request.getSession().getAttribute("galleryImages")).thenReturn(null);
		
		productInfos = new ProductInfos(ds, photoControl, productDAO, ns);
		
		productInfos.doGet(request, response);

		verify(response).sendRedirect(request.getContextPath() + "/");
		
	}
	
	@Test
	public void testDoGet_Successo() throws Exception {
		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		final ServletConfig mockServletConfig = mock(ServletConfig.class);
		final ServletContext mockServletContext = mock(ServletContext.class);

		productInfos = new ProductInfos(ds, photoControl, productDAO, ns) {
			@Override
			public ServletConfig getServletConfig() {
				return mockServletConfig;
			}
			
			@Override
			public ServletContext getServletContext() {
				return mockServletContext;
			}
		};
		
		Prodotto product = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		
		String pathImg1 = "C://Users//dorot//git//TechHeaven//Sistema//src//main//webapp//images//product_images//xiaomi_RedmiNote13_dett1.jpg";
		List<String> galleryImages = new ArrayList<>();
		galleryImages.add(pathImg1);
		
		when(request.getSession().getAttribute("product")).thenReturn(product);
		when(request.getSession().getAttribute("galleryImages")).thenReturn(galleryImages);
		
		RequestDispatcher dispatcherMock = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("common/productDetails.jsp")).thenReturn(dispatcherMock);
		
		productInfos.doGet(request, response);
		
		verify(request).setAttribute("product", product);
		verify(request).setAttribute("galleryImages", galleryImages);
		verify(dispatcherMock).forward(request, response);
		
	}
	
	@Test
	public void testDoPost_ActionNull() throws IOException, ServletException {
		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		when(request.getParameter("action")).thenReturn(null);
		productInfos = new ProductInfos(ds, photoControl, productDAO, ns);
		
		productInfos.doPost(request, response);
		
		verify(response).sendRedirect(request.getContextPath() + "/ProductInfos");
	}
	
	@Test
	public void testDoPost_Successo() throws IOException, ServletException {
		productDAO = mock(ProdottoDAODataSource.class);
		photoControl = mock(PhotoControl.class);
		ds = mock(DataSource.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		String action = "retrieveInfosForUpdate";
		when(request.getParameter("action")).thenReturn(action);
		
		productInfos = new ProductInfos(ds, photoControl, productDAO, ns);
		
		productInfos.doPost(request, response);
		
	}
}
