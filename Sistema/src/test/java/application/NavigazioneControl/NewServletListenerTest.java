package application.NavigazioneControl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import java.util.Collection;
import java.io.IOException;
import java.util.ArrayList;

import application.Navigazione.NavigazioneControl.NewServletListener;
import application.Navigazione.NavigazioneControl.PaginationUtils;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.NavigazioneException.ErroreRicercaCategoriaException;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class NewServletListenerTest {
	
	private NewServletListener servletListener;
	private PaginationUtils pu;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	private ProdottoDAODataSource productDAO;
	
	@Test
	public void testContextInitialized() throws IOException, ErroreRicercaCategoriaException {
		productDAO = mock(ProdottoDAODataSource.class);
		pu = mock(PaginationUtils.class);
		ServletContextEvent sce = mock(ServletContextEvent.class);
		
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		Collection<ProxyProdotto> telefoni = new ArrayList<>();
		ProxyProdotto product1 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);
		telefoni.add(product1);
		
		String categoria = "TELEFONIA";
		when(pu.performPagination(categoria, 1, 6, "menu")).thenReturn(telefoni);
		
		Collection<ProxyProdotto> grandi_elettrodomestici = new ArrayList<>();
		ProxyProdotto product2 = new ProxyProdotto(10, "Bosch lavatrice a carica frontale", "Prova", "Prova", Float.parseFloat("590.50"), 
				Categoria.GRANDI_ELETTRODOMESTICI, "Bosch", "QualcheModello", 112, true, false, productDAO);
		grandi_elettrodomestici.add(product2);
		String categoria2 = "GRANDI_ELETTRODOMESTICI"; 
		when(pu.performPagination(categoria2, 1, 6, "menu")).thenReturn(grandi_elettrodomestici);
		
		final ServletContext mockServletContext = mock(ServletContext.class);
		
		when(sce.getServletContext()).thenReturn(mockServletContext);
		servletListener = new NewServletListener(pu);
		
		servletListener.contextInitialized(sce);
		
		verify(sce.getServletContext()).setAttribute("telefoni", telefoni);
		verify(sce.getServletContext()).setAttribute("gr_elettr", grandi_elettrodomestici);

	}
	
	@Test
	public void testContextDestroyed() throws IOException {
		
		pu = mock(PaginationUtils.class);
		ServletContextEvent sce = mock(ServletContextEvent.class);
		
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		
		final ServletContext mockServletContext = mock(ServletContext.class);
		
		when(sce.getServletContext()).thenReturn(mockServletContext);
		servletListener = new NewServletListener(pu);
		
		assertThrows(UnsupportedOperationException.class , () -> {
			servletListener.contextDestroyed(sce);
		});

	}
}