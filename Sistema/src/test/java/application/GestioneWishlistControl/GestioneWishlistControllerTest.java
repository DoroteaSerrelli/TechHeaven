package application.GestioneWishlistControl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import application.GestioneWishlist.GestioneWishlistControl.GestioneWishlistController;
import application.GestioneWishlist.GestioneWishlistService.GestioneWishlistServiceImpl;
import application.GestioneWishlist.GestioneWishlistService.Wishlist;
import application.GestioneWishlist.GestioneWishlistService.WishlistException.ProdottoNonPresenteException;
import application.GestioneWishlist.GestioneWishlistService.WishlistException.ProdottoNulloException;
import application.GestioneWishlist.GestioneWishlistService.WishlistException.ProdottoPresenteException;
import application.GestioneWishlist.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import storage.WishlistDAO.WishlistDAODataSource;

public class GestioneWishlistControllerTest {

	private GestioneWishlistController wishlistController;
	private ProdottoDAODataSource productDAO;
	private WishlistDAODataSource wishlistDAO;
	private GestioneWishlistServiceImpl gws;


	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;

	@BeforeEach
	public void setUp() throws IOException {

		productDAO = mock(ProdottoDAODataSource.class);
		wishlistDAO = mock(WishlistDAODataSource.class);
		gws = mock(GestioneWishlistServiceImpl.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

	}

	/**
	 * TEST CASES PER AGGIUNTA PRODOTTO IN WISHLIST
	 * 
	 * TC10_1.1_1 : l'utente non possiede una wishlist, la wishlist è vuota,
	 * 				 il prodotto scelto non è presente nella wishlist
	 * 
	 * TC10_1.1_2 : l'utente possiede una wishlist, la wishlist non è vuota,
	 * 				 il prodotto scelto è già presente nella wishlist
	 * 
	 * TC10_1.1_3 : l'utente possiede una wishlist, la wishlist non è vuota,
	 * 				 il prodotto scelto non è presente nella wishlist
	 * 
	 * */

	@Test
	public void testDoPost_TC10_1_1_1() throws SQLException, SottocategoriaProdottoException, CategoriaProdottoException, ServletException, IOException, ProdottoPresenteException, ProdottoNulloException{

		wishlistController = new GestioneWishlistController(productDAO, wishlistDAO, gws);

		String username = "saraNa";
		String password = "12sara";

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());
		ProxyProdotto product = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);


		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);

		// Simula il comportamento del wishlistDAO per l'utente che non ha wishlist
		when(wishlistDAO.getWishlistCount(user)).thenReturn(0);

		// Simula il prodotto da aggiungere

		when(productDAO.doRetrieveProxyByKey(product.getCodiceProdotto())).thenReturn(product);
		when(wishlistDAO.doRetrieveAllWishUser(user)).thenReturn(wishlistUser);

		String productId = "3";
		String action = "addtowishlist";

		// Imposta l'azione da eseguire
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(productId);

		Wishlist wishlistUserUpdated = new Wishlist(user);
		wishlistUserUpdated.setId(1);
		Collection<ProxyProdotto> products = new ArrayList<>();
		products.add(product);
		wishlistUserUpdated.setProdotti(products);

		when(gws.aggiungiProdottoInWishlist(wishlistUser, product, user)).thenReturn(wishlistUserUpdated);

		wishlistController.doPost(request, response);

		verify(request.getSession()).setAttribute("Wishlist", wishlistUserUpdated);
		verify(request.getSession()).setAttribute("errormsg", "Prodotto aggiunto nella wishlist con successo");
		verify(request.getSession()).setAttribute("status", "valid");

	}

	@Test
	public void testDoPost_TC10_1_1_2() throws SQLException, SottocategoriaProdottoException, CategoriaProdottoException, ServletException, IOException, ProdottoPresenteException, ProdottoNulloException{

		wishlistController = new GestioneWishlistController(productDAO, wishlistDAO, gws);

		String username = "saraNa";
		String password = "12sara";

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());
		ProxyProdotto product = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);


		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);

		Collection<ProxyProdotto> products = new ArrayList<>();
		products.add(product);
		wishlistUser.setProdotti(products);


		// Simula il comportamento del wishlistDAO per l'utente che non ha wishlist
		when(wishlistDAO.getWishlistCount(user)).thenReturn(0);

		// Simula il prodotto da aggiungere

		when(productDAO.doRetrieveProxyByKey(product.getCodiceProdotto())).thenReturn(product);
		when(wishlistDAO.doRetrieveAllWishUser(user)).thenReturn(wishlistUser);

		String productId = "3";
		String action = "addtowishlist";

		// Imposta l'azione da eseguire
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(productId);

		when(gws.aggiungiProdottoInWishlist(wishlistUser, product, user)).thenThrow(new ProdottoPresenteException("Prodotto già presente nella wishlist"));

		wishlistController.doPost(request, response);

		ProdottoPresenteException ex = new ProdottoPresenteException("Prodotto già presente nella wishlist");

		verify(request.getSession()).setAttribute("errormsg", ex.getMessage());
		verify(request.getSession()).setAttribute("status", "invalid");

		verify(response).sendRedirect(request.getContextPath() + "/Wishlist");

	}

	@Test
	public void testDoPost_TC10_1_1_3() throws SQLException, SottocategoriaProdottoException, CategoriaProdottoException, ServletException, IOException, ProdottoPresenteException, ProdottoNulloException{

		wishlistController = new GestioneWishlistController(productDAO, wishlistDAO, gws);

		String username = "saraNa";
		String password = "12sara";

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());
		ProxyProdotto product = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);


		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);

		Collection<ProxyProdotto> products = new ArrayList<>();
		products.add(product);
		wishlistUser.setProdotti(products);


		// Simula il comportamento del wishlistDAO per l'utente che non ha wishlist
		when(wishlistDAO.getWishlistCount(user)).thenReturn(0);

		// Simula il prodotto da aggiungere

		when(productDAO.doRetrieveProxyByKey(product2.getCodiceProdotto())).thenReturn(product2);
		when(wishlistDAO.doRetrieveAllWishUser(user)).thenReturn(wishlistUser);

		String product2Id = "1";
		String action = "addtowishlist";

		// Imposta l'azione da eseguire
		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(product2Id);

		Wishlist updatedWishlistUser = new Wishlist(user);
		wishlistUser.setId(1);

		Collection<ProxyProdotto> newProducts = new ArrayList<>();
		newProducts.add(product);
		newProducts.add(product2);
		wishlistUser.setProdotti(newProducts);

		when(gws.aggiungiProdottoInWishlist(wishlistUser, product2, user)).thenReturn(updatedWishlistUser);

		wishlistController.doPost(request, response);

		verify(request.getSession()).setAttribute("Wishlist", updatedWishlistUser);
		verify(request.getSession()).setAttribute("errormsg", "Prodotto aggiunto nella wishlist con successo");
		verify(request.getSession()).setAttribute("status", "valid");
		verify(response).sendRedirect(request.getContextPath() + "/Wishlist");

	}

	/**
	 * TEST CASES PER RIMOZIONE PRODOTTO DALLA WISHLIST
	 * 
	 * TC10_2.1_1 : l'utente possiede una wishlist, la wishlist ha un solo prodotto,
	 * 				 il prodotto scelto è presente nella wishlist
	 * 
	 * TC10_2.1_2 : l'utente possiede una wishlist, la wishlist ha più di un prodotto,
	 * 				 il prodotto scelto è presente nella wishlist
	 * 
	 * 
	 * */


	@Test
	public void testDoPost_TC10_2_1_1() throws CategoriaProdottoException, SQLException, ProdottoNulloException, ProdottoNonPresenteException, WishlistVuotaException, IOException, NumberFormatException, SottocategoriaProdottoException, ServletException {
		
		wishlistController = new GestioneWishlistController(productDAO, wishlistDAO, gws);
		
		String username = "saraNa";
		String password = "12sara";

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());

		ProxyProdotto product = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);


		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);

		Collection<ProxyProdotto> products = new ArrayList<>();
		products.add(product);
		wishlistUser.setProdotti(products);

		String action = "removefromwishlist";
		String productId = "9";

		when(wishlistDAO.getWishlistCount(user)).thenReturn(1);
		when(request.getSession().getAttribute("Wishlist")).thenReturn(wishlistUser);
		when(wishlistDAO.doRetrieveAllWishUser(user)).thenReturn(wishlistUser);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(productId))).thenReturn(product);

		Wishlist updatedWishlistUser = new Wishlist(user);

		when(gws.rimuoviProdottoDaWishlist(wishlistUser, user, product)).thenReturn(updatedWishlistUser);
		request.getSession().setAttribute("errormsg", "Prodotto rimosso con successo dalla wishlist");
		request.getSession().setAttribute("status", "valid");
		
		wishlistController.doPost(request, response);
		
		verify(request.getSession()).removeAttribute("Wishlist");
		verify(response).sendRedirect(request.getContextPath() + "/Wishlist");

	}

	@Test
	public void TC10_2_1_2() throws CategoriaProdottoException, SQLException, ProdottoNulloException, ProdottoNonPresenteException, WishlistVuotaException, ServletException, IOException, NumberFormatException, SottocategoriaProdottoException {

		String username = "saraNa";
		String password = "12sara";

		ProdottoDAODataSource productDAO = Mockito.mock(ProdottoDAODataSource.class); 

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());

		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(7, "Dyson Supersonic asciuga capelli", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Dyson", "Supersonic", 45, true, true, productDAO);
		

		wishlistController = new GestioneWishlistController(productDAO, wishlistDAO, gws);
		
		
		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);

		Collection<ProxyProdotto> products = new ArrayList<>();
		products.add(product1);
		products.add(product2);
		wishlistUser.setProdotti(products);

		String action = "removefromwishlist";
		String productId = "7";

		when(wishlistDAO.getWishlistCount(user)).thenReturn(1);
		when(request.getSession().getAttribute("Wishlist")).thenReturn(wishlistUser);
		when(wishlistDAO.doRetrieveAllWishUser(user)).thenReturn(wishlistUser);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("productId")).thenReturn(productId);
		when(productDAO.doRetrieveProxyByKey(Integer.parseInt(productId))).thenReturn(product2);

		Wishlist updatedWishlistUser = new Wishlist(user);
		updatedWishlistUser.setId(1);

		Collection<ProxyProdotto> updatedProducts = new ArrayList<>();
		updatedProducts.add(product1);
		updatedWishlistUser.setProdotti(updatedProducts);
		
		//createWishlistIfNotExists when
		
		when(request.getSession().getAttribute("Wishlist")).thenReturn(wishlistUser);
		when(wishlistDAO.doRetrieveAllWishUser(user)).thenReturn(wishlistUser);
		
		when(gws.rimuoviProdottoDaWishlist(wishlistUser, user, product2)).thenReturn(updatedWishlistUser);
		request.getSession().setAttribute("errormsg", "Prodotto rimosso con successo dalla wishlist");
		request.getSession().setAttribute("status", "valid");
		
		wishlistController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("Wishlist", updatedWishlistUser);
		verify(response).sendRedirect(request.getContextPath() + "/Wishlist");

	}

}