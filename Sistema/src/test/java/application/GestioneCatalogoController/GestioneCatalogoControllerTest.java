package application.GestioneCatalogoController;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.*;

import application.GestioneCatalogoControl.GestioneCatalogoController;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneService.ProxyProdotto;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GestioneCatalogoControllerTest {

	private GestioneCatalogoController controller;
	private ProdottoDAODataSource productDAO;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private GestioneCatalogoServiceImpl mockService;

	@BeforeEach
	public void setUp() throws ServletException {
		controller = new GestioneCatalogoController();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		productDAO = mock(ProdottoDAODataSource.class);
		mockService = mock(GestioneCatalogoServiceImpl.class);

		// Set up the controller's dependencies
		controller.setGestioneCatalogoService(mockService);

		// Initialize the controller (this would set up the other dependencies too)
		controller.init();

		// Mock per il metodo getParameter
		when(request.getParameter("page")).thenReturn("1");

		// Mock per il metodo getSession
		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
	}

	@Test
	public void testDoGet() throws Exception {


		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);
		
		Collection<ProxyProdotto> pageProducts = new ArrayList<>();
		pageProducts.add(product1);
		pageProducts.add(product2);
		
		// Prepara il mock
		// Preparazione per il mock del session
		HttpSession session = request.getSession();
		when(request.getParameter("page")).thenReturn("1");
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));

		// Simula il servizio che restituisce alcuni prodotti
		
		when(controller.paginateProducts(request, 1)).thenReturn(pageProducts);

		// Chiama il metodo doGet del controller
		controller.doGet(request, response);

		// Verifica che i prodotti siano stati aggiunti alla sessione
		verify(request.getSession()).setAttribute(eq("products"), eq(pageProducts));
	}



	/*
    @Test
    public void testDoPostAddProduct() throws Exception {
        when(request.getParameter("action")).thenReturn("addProduct");
        when(request.getParameter("productName")).thenReturn("Test Product");
        when(request.getParameter("price")).thenReturn("10.0");
        when(request.getParameter("quantita")).thenReturn("1");
        when(request.getPart("file")).thenReturn(mock(Part.class));

        // Call the doPost method
        controller.doPost(request, response);

        // Verify that the service was called to add the product
        verify(mockService).aggiuntaProdottoInCatalogo(anyString(), anyString(), anyString(), anyString(), anyString(), 
            anyFloat(), anyInt(), anyString(), anyString(), anyBoolean(), anyBoolean(), 
            any(), anyInt(), anyInt());

        // Verify redirect to GestioneCatalogo
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPostDeleteProduct() throws Exception {
        when(request.getParameter("action")).thenReturn("deleteProduct");
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("productName")).thenReturn("Test Product");

        // Call the doPost method
        controller.doPost(request, response);

        // Verify that the service was called to delete the product
        verify(mockService).rimozioneProdottoDaCatalogo(any(), anyInt(), anyInt());

        // Verify redirect to GestioneCatalogo
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testHandleExceptionInPost() throws Exception {
        when(request.getParameter("action")).thenReturn("addProduct");
        when(request.getParameter("productName")).thenReturn("Test Product");
        when(request.getParameter("price")).thenReturn("10.0");
        when(mockService.aggiuntaProdottoInCatalogo(any(), any(), any(), any(), any(), 
            anyFloat(), anyInt(), any(), any(), anyBoolean(), anyBoolean(), any(), anyInt(), anyInt()))
            .thenThrow(new ProdottoInCatalogoException("Error adding product"));

        controller.doPost(request, response);

        // Verify that the session was updated with the error message
        verify(request.getSession()).setAttribute(eq("error"), anyString());
        verify(response).sendRedirect(anyString());
    }*/
}
