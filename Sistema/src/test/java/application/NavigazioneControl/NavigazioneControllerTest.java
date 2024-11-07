package application.NavigazioneControl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

import application.Navigazione.NavigazioneControl.NavigazioneController;
import application.Navigazione.NavigazioneControl.PaginationUtils;

public class NavigazioneControllerTest {
	
	private NavigazioneController navigazioneController;
	private PaginationUtils pu;
	private int perPage = 10;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	
	
	@Test
	public void testProcessRequest_keywordNull() throws IOException, ServletException {

		pu = mock(PaginationUtils.class);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		when(request.getParameter("keyword")).thenReturn(null);
		
		navigazioneController = new NavigazioneController(perPage, pu);
		
		navigazioneController.processRequest(request, response);
		
		verify(request.getSession()).setAttribute("empty_search", "Compila questo campo.");
		verify(response).sendRedirect(request.getContextPath() + "/index.jsp");
	}
	
	@Test
	public void testProcessRequest_keywordEmpty() throws IOException, ServletException {

		pu = mock(PaginationUtils.class);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
		when(request.getParameter("keyword")).thenReturn("");
		
		navigazioneController = new NavigazioneController(perPage, pu);
		
		navigazioneController.processRequest(request, response);
		
		verify(request.getSession()).setAttribute("empty_search", "Compila questo campo.");
		verify(response).sendRedirect(request.getContextPath() + "/index.jsp");
	}
	
	
	@Test
	public void testProcessRequest_searchTypeNull() throws IOException, ServletException {

		pu = mock(PaginationUtils.class);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		when(request.getParameter("page")).thenReturn("10");
		
		String keyword = "testKeyword";
		
		when(request.getParameter("keyword")).thenReturn(keyword);
		when(request.getParameter("search_type")).thenReturn(null);
		navigazioneController = new NavigazioneController(perPage, pu);
		
		navigazioneController.processRequest(request, response);
		
		verify(request.getSession()).setAttribute("keyword", keyword);
		verify(response).sendRedirect(request.getContextPath() + "/");
		
	}
	
	@Test
	public void testProcessRequest_Successo() throws IOException, ServletException {

		pu = mock(PaginationUtils.class);
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		when(request.getParameter("page")).thenReturn("1");
		
		String keyword = "testKeyword";
		String searchType = "bar";
		
		when(request.getParameter("keyword")).thenReturn(keyword);
		when(request.getParameter("search_type")).thenReturn(searchType);
		navigazioneController = new NavigazioneController(perPage, pu);
		
		navigazioneController.processRequest(request, response);
		
		verify(request.getSession()).setAttribute("keyword", keyword);
		verify(pu).detectActionChanges(request, searchType);
		verify(pu).paginateSearchedProducts(request, response, 1, perPage, keyword, searchType);
		verify(response).sendRedirect(request.getContextPath() + "/ResultsPage");
		
	}
}
