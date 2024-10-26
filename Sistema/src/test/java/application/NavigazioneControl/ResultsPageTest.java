package application.NavigazioneControl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;

public class ResultsPageTest {
	private ResultsPage resultsPage;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;

	@Test
	public void testDoGet_risultatiRicercaNull() throws Exception {

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

		when(request.getSession().getAttribute("products")).thenReturn(null);

		resultsPage = new ResultsPage();

		String keyword = "prova";

		when(request.getParameter("keyword")).thenReturn(keyword);

		resultsPage.doGet(request, response);

		verify(response).sendRedirect(request.getContextPath() + "/NavigazioneController?keyword="+keyword);
	}
	
	@Test
	public void testDoGet_risultatiRicerca_Successo() throws Exception {

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		ProxyProdotto product = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false);

		Collection<ProxyProdotto> searchResult = new ArrayList<>();
		searchResult.add(product);
		
		when(request.getSession().getAttribute("products")).thenReturn(searchResult);
		
		final ServletConfig mockServletConfig = mock(ServletConfig.class);
		final ServletContext mockServletContext = mock(ServletContext.class);

		resultsPage = new ResultsPage() {
			@Override
			public ServletConfig getServletConfig() {
				return mockServletConfig;
			}
			
			@Override
			public ServletContext getServletContext() {
				return mockServletContext;
			}
		};

		String keyword = "Samsung";
		int page = 1;
		String hasNextPage = "true";
		
		when(request.getSession().getAttribute("keyword")).thenReturn(keyword);
		when(request.getSession().getAttribute("page")).thenReturn(page);
		when(request.getSession().getAttribute("hasNextPage")).thenReturn(hasNextPage);
		
		RequestDispatcher dispatcherMock = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("common/searchResults.jsp")).thenReturn(dispatcherMock);
		
		resultsPage.doGet(request, response);
		
		verify(dispatcherMock).forward(request, response);
	}
}
