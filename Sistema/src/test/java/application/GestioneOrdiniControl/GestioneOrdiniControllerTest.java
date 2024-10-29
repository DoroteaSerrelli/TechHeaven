package application.GestioneOrdiniControl;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import application.GestioneCarrelloService.*;
import application.GestioneOrdiniService.*;
import application.GestioneOrdiniService.ObjectOrdine.Stato;
import application.GestioneOrdiniService.ObjectOrdine.TipoConsegna;
import application.GestioneOrdiniService.ObjectOrdine.TipoSpedizione;
import application.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdiniService.OrdineException.FormatoCorriereException;
import application.GestioneOrdiniService.OrdineException.FormatoImballaggioException;
import application.GestioneOrdiniService.OrdineException.FormatoQuantitaException;
import application.GestioneOrdiniService.OrdineException.MancanzaPezziException;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.NavigazioneDAO.ProdottoDAODataSource;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class GestioneOrdiniControllerTest {

	private GestioneOrdiniController ordiniController;
	private GestioneOrdiniServiceImpl gos;
	private PaginationUtils pu;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	
	@BeforeEach
	public void setUp() throws ServletException, IOException {
		pu = mock(PaginationUtils.class);
		gos = mock(GestioneOrdiniServiceImpl.class);
		
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
	}
	
	
	@Test
	public void testDoPost_AccettazioneOrdineDaSpedireErroreQuantità() throws IOException, OrdineVuotoException, ErroreTipoSpedizioneException, SQLException, CategoriaProdottoException, SottocategoriaProdottoException, ServletException {
		
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		OrdineDAODataSource orderDAO = mock(OrdineDAODataSource.class);
		
		ordiniController = new GestioneOrdiniController(productDAO, orderDAO, gos, pu);
		
		
		//dati ordine e prodotti
		int codiceOrdine = 23;
		Indirizzo indirizzoSpedizione = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 
		
		Cliente mickeyProfile = new Cliente("pippoemail@example.com", "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzoSpedizione);
		
		ProxyOrdine orderProxy = new ProxyOrdine(orderDAO, codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority);
		
		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);
		
		ProxyProdotto product2 = new ProxyProdotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true, productDAO);
		
		ItemCarrello p1 = new ItemCarrello();
		p1.setCodiceProdotto(product1.getCodiceProdotto());
		p1.setNomeProdotto(product1.getNomeProdotto());
		p1.setQuantita(80);
		
		ItemCarrello p2 = new ItemCarrello();
		p1.setCodiceProdotto(product2.getCodiceProdotto());
		p1.setNomeProdotto(product2.getNomeProdotto());
		p1.setQuantita(76);
		
		ArrayList<ItemCarrello> prodotti = new ArrayList<>();
		
		prodotti.add(p1);
		prodotti.add(p2);
		
		Ordine order = new Ordine(codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority, mickeyProfile, prodotti);
		
		String action = "accept_order";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("orderId")).thenReturn(String.valueOf(orderProxy.getCodiceOrdine()));
		when(orderDAO.doRetrieveAllOrderProducts(codiceOrdine)).thenReturn(prodotti);
		when(orderDAO.doRetrieveProxyByKey(codiceOrdine)).thenReturn(orderProxy);
		when(orderDAO.doRetrieveFullOrderByKey(codiceOrdine)).thenReturn(order);
		
		//loop for
		when(productDAO.doRetrieveProxyByKey(p1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(p2.getCodiceProdotto())).thenReturn(product2);
		
		ordiniController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("proxy_ordine", orderProxy);
		verify(request.getSession()).setAttribute("selected_ordine", order);

		verify(request.getSession()).removeAttribute("proxy_ordine");                                         
		verify(response).sendRedirect(request.getContextPath()+"/error_preparazioneOrdine");
	}
	
	@Test
	public void testDoPost_AccettazioneOrdineDaSpedireSuccesso() throws IOException, OrdineVuotoException, ErroreTipoSpedizioneException, SQLException, CategoriaProdottoException, SottocategoriaProdottoException, ServletException {
		
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		OrdineDAODataSource orderDAO = mock(OrdineDAODataSource.class);
		
		ordiniController = new GestioneOrdiniController(productDAO, orderDAO, gos, pu);
		
		
		//dati ordine e prodotti
		int codiceOrdine = 23;
		Indirizzo indirizzoSpedizione = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 
		
		Cliente mickeyProfile = new Cliente("pippoemail@example.com", "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzoSpedizione);
		
		ProxyOrdine orderProxy = new ProxyOrdine(orderDAO, codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority);
		
		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);
		
		ProxyProdotto product2 = new ProxyProdotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true, productDAO);
		
		ItemCarrello p1 = new ItemCarrello();
		p1.setCodiceProdotto(product1.getCodiceProdotto());
		p1.setNomeProdotto(product1.getNomeProdotto());
		p1.setQuantita(12);
		
		ItemCarrello p2 = new ItemCarrello();
		p1.setCodiceProdotto(product2.getCodiceProdotto());
		p1.setNomeProdotto(product2.getNomeProdotto());
		p1.setQuantita(7);
		
		ArrayList<ItemCarrello> prodotti = new ArrayList<>();
		
		prodotti.add(p1);
		prodotti.add(p2);
		
		Ordine order = new Ordine(codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority, mickeyProfile, prodotti);
		
		String action = "accept_order";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("orderId")).thenReturn(String.valueOf(orderProxy.getCodiceOrdine()));
		when(orderDAO.doRetrieveAllOrderProducts(codiceOrdine)).thenReturn(prodotti);
		when(orderDAO.doRetrieveProxyByKey(codiceOrdine)).thenReturn(orderProxy);
		when(orderDAO.doRetrieveFullOrderByKey(codiceOrdine)).thenReturn(order);
		
		//loop for
		when(productDAO.doRetrieveProxyByKey(p1.getCodiceProdotto())).thenReturn(product1);
		when(productDAO.doRetrieveProxyByKey(p2.getCodiceProdotto())).thenReturn(product2);
		
		ordiniController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("proxy_ordine", orderProxy);
		verify(request.getSession()).setAttribute("selected_ordine", order);

		verify(request.getSession()).setAttribute("order_products", prodotti);                                    
		verify(response).sendRedirect(request.getContextPath() + "/fill_order_details");
	}
	
	@Test
	public void testDoPost_CompletaSpedizioneOrdineSuccesso() throws IOException, OrdineVuotoException, ErroreTipoSpedizioneException, SQLException, CategoriaProdottoException, SottocategoriaProdottoException, ServletException, MancanzaPezziException, FormatoQuantitaException, FormatoImballaggioException, FormatoCorriereException, CloneNotSupportedException {
		
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		OrdineDAODataSource orderDAO = mock(OrdineDAODataSource.class);
		
		ordiniController = new GestioneOrdiniController(productDAO, orderDAO, gos, pu);
		
		
		//dati ordine e prodotti
		int codiceOrdine = 23;
		Indirizzo indirizzoSpedizione = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 
		
		Cliente mickeyProfile = new Cliente("pippoemail@example.com", "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzoSpedizione);
		
		ProxyOrdine orderProxy = new ProxyOrdine(orderDAO, codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority);
		
		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);
		
		ProxyProdotto product2 = new ProxyProdotto(6, "Acer NITRO 50 N50-650 Tower", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PRODOTTI_ELETTRONICA, "Acer", "NITRO 50 N50-650 Tower", 73, true, true, productDAO);
		
		ItemCarrello p1 = new ItemCarrello();
		p1.setCodiceProdotto(product1.getCodiceProdotto());
		p1.setNomeProdotto(product1.getNomeProdotto());
		p1.setQuantita(12);
		
		ItemCarrello p2 = new ItemCarrello();
		p2.setCodiceProdotto(product2.getCodiceProdotto());
		p2.setNomeProdotto(product2.getNomeProdotto());
		p2.setQuantita(7);
		
		ArrayList<ItemCarrello> prodotti = new ArrayList<>();
		
		prodotti.add(p1);
		prodotti.add(p2);
		
		Ordine order = new Ordine(codiceOrdine, Stato.Richiesta_effettuata, indirizzoSpedizione, TipoSpedizione.Spedizione_prime, TipoConsegna.Priority, mickeyProfile, prodotti);
		
		String action = "complete_order";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("selected_ordine")).thenReturn(order);
		when(request.getSession().getAttribute("proxy_ordine")).thenReturn(orderProxy);
		when(request.getSession().getAttribute("order_products")).thenReturn(prodotti);
		
		String[] productIds = {String.valueOf(p1.getCodiceProdotto()), String.valueOf(p2.getCodiceProdotto())};
		String[] itemAmounts = {String.valueOf(p1.getQuantita()), String.valueOf(p2.getQuantita())};
		
		ArrayList<Integer> quantities = new ArrayList<>();
		
		for(String quantity : itemAmounts)
			quantities.add(Integer.parseInt(quantity));
		
		when(request.getParameterValues("product_id")).thenReturn(productIds);
		when(request.getParameterValues("item_amount")).thenReturn(itemAmounts);
		
		String imballaggio = "Cartone, scotch";
		String corriere = "Spedizioni Damato Napoli";
		
		when(request.getParameter("Imballaggio")).thenReturn(imballaggio);
		when(request.getParameter("Corriere")).thenReturn(corriere);
		
		ReportSpedizione report = new ReportSpedizione(orderProxy.getCodiceOrdine(), corriere, imballaggio, orderProxy);
		
		when(gos.creaReportSpedizione(order, prodotti, quantities, imballaggio, corriere)).thenReturn(report);
		ordiniController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("error", "Ordine Spedito Con Successo!");
		verify(request.getSession()).removeAttribute("proxy_ordine");                    
		verify(request.getSession()).removeAttribute("selected_ordine");
		verify(request.getSession()).removeAttribute("order_products");

		verify(response).sendRedirect(request.getContextPath() + "/GestioneOrdini"); 
		
	}
}
