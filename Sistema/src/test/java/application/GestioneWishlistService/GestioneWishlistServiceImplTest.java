package application.GestioneWishlistService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import application.GestioneWishlistService.WishlistException.ProdottoNonPresenteException;
import application.GestioneWishlistService.WishlistException.ProdottoNulloException;
import application.GestioneWishlistService.WishlistException.ProdottoPresenteException;
import application.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.RegistrazioneService.ProxyUtente;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import storage.WishlistDAO.WishlistDAODataSource;

public class GestioneWishlistServiceImplTest {

	private GestioneWishlistServiceImpl wishlistService;
	private WishlistDAODataSource wishlistDAO;

	@BeforeEach
	public void setUp() {
		wishlistDAO = Mockito.mock(WishlistDAODataSource.class);
		wishlistService = new GestioneWishlistServiceImpl(wishlistDAO);
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
	public void TC10_1_1_1() throws CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException {
		String username = "saraNa";
		String password = "12sara";
		ProdottoDAODataSource productDAO = Mockito.mock(ProdottoDAODataSource.class); 

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());
		ProxyProdotto product = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);

		Mockito.when(wishlistDAO.doRetrieveProductByKey(product.getCodiceProdotto(), wishlistUser)).thenReturn(null);
		Mockito.when(wishlistDAO.doSaveProduct(product, wishlistUser)).thenReturn(true);

		Wishlist updatedWishlistUser = new Wishlist(user);
		Collection<ProxyProdotto> productsWish = new ArrayList<>();
		productsWish.add(product);
		updatedWishlistUser.setId(1);
		updatedWishlistUser.setProdotti(productsWish);

		Mockito.when(wishlistDAO.doRetrieveWishlistByKey(user, wishlistUser.getId())).thenReturn(updatedWishlistUser);

		Wishlist result = wishlistService.aggiungiProdottoInWishlist(wishlistUser, product, user);

		//assertEquals(1, result.getProdotti().size());
		assertTrue(result.getProdotti().contains(product));

	}

	@Test
	public void TC10_1_1_2() throws CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException {
		String username = "saraNa";
		String password = "12sara";
		ProdottoDAODataSource productDAO = Mockito.mock(ProdottoDAODataSource.class); 

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());
		ProxyProdotto product = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);
		Collection<ProxyProdotto> productsWish = new ArrayList<>();
		productsWish.add(product);
		wishlistUser.setId(1);
		wishlistUser.setProdotti(productsWish);

		Mockito.when(wishlistDAO.doRetrieveProductByKey(product.getCodiceProdotto(), wishlistUser)).thenReturn(product);

		assertThrows(ProdottoPresenteException.class , () -> {
			wishlistService.aggiungiProdottoInWishlist(wishlistUser, product, user);

		});
	}

	@Test
	public void TC10_1_1_3() throws CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException {
		String username = "saraNa";
		String password = "12sara";

		ProdottoDAODataSource productDAO = Mockito.mock(ProdottoDAODataSource.class); 

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());

		ProxyProdotto product1 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(1, "Apple Watch SE", "Prova", "Prova", Float.parseFloat("240.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Apple", "Watch SE", 110, true, false, productDAO);



		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);
		Collection<ProxyProdotto> productsWish = new ArrayList<>();
		productsWish.add(product1);
		wishlistUser.setId(1);
		wishlistUser.setProdotti(productsWish);

		Mockito.when(wishlistDAO.doRetrieveProductByKey(product2.getCodiceProdotto(), wishlistUser)).thenReturn(null);

		Mockito.when(wishlistDAO.doSaveProduct(product2, wishlistUser)).thenReturn(true);

		Wishlist updatedWishlistUser = new Wishlist(user);
		Collection<ProxyProdotto> productsWishes = new ArrayList<>();
		productsWishes.add(product1);
		productsWishes.add(product2);
		updatedWishlistUser.setId(1);
		updatedWishlistUser.setProdotti(productsWishes);

		Mockito.when(wishlistDAO.doRetrieveWishlistByKey(user, wishlistUser.getId())).thenReturn(updatedWishlistUser);

		Wishlist result = wishlistService.aggiungiProdottoInWishlist(wishlistUser, product2, user);

		//assertEquals(2, result.getProdotti().size());
		assertTrue(result.getProdotti().contains(product2));
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
	 * */

	@Test
	public void TC10_2_1_1() throws CategoriaProdottoException, SQLException, ProdottoNulloException, ProdottoNonPresenteException, WishlistVuotaException {

		String username = "saraNa";
		String password = "12sara";

		ProdottoDAODataSource productDAO = Mockito.mock(ProdottoDAODataSource.class); 

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());

		ProxyProdotto product = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);


		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);
		Collection<ProxyProdotto> productsWish = new ArrayList<>();
		productsWish.add(product);
		wishlistUser.setId(1);
		wishlistUser.setProdotti(productsWish);

		Mockito.when(wishlistDAO.doRetrieveProductByKey(product.getCodiceProdotto(), wishlistUser)).thenReturn(product);
		Mockito.when(wishlistDAO.doDeleteProduct(product.getCodiceProdotto(), wishlistUser)).thenReturn(null);

		Mockito.when(wishlistDAO.doRetrieveWishlistByKey(user, wishlistUser.getId())).thenReturn(null);

		Wishlist result = wishlistService.rimuoviProdottoDaWishlist(wishlistUser, user, product);

		assertEquals(result, null);

	}

	@Test
	public void TC10_2_1_2() throws CategoriaProdottoException, SQLException, ProdottoNulloException, ProdottoNonPresenteException, WishlistVuotaException {

		String username = "saraNa";
		String password = "12sara";

		ProdottoDAODataSource productDAO = Mockito.mock(ProdottoDAODataSource.class); 

		ProxyUtente user = new ProxyUtente(username, password, new ArrayList<>());

		ProxyProdotto product1 = new ProxyProdotto(9, "Ariete Handy Force - Scopa elettrica con Filo", "Prova", "Prova", Float.parseFloat("46.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Ariete", "Handy Force", 68, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(7, "Dyson Supersonic asciuga capelli", "Prova", "Prova", Float.parseFloat("1399.00"), 
				Categoria.PICCOLI_ELETTRODOMESTICI, "Dyson", "Supersonic", 45, true, true, productDAO);
		
		
		Wishlist wishlistUser = new Wishlist(user);
		wishlistUser.setId(1);
		Collection<ProxyProdotto> productsWish = new ArrayList<>();
		productsWish.add(product1);
		productsWish.add(product2);
		wishlistUser.setId(1);
		wishlistUser.setProdotti(productsWish);
		
		Wishlist updatedWishlistUser = new Wishlist(user);
		updatedWishlistUser.setId(1);
		Collection<ProxyProdotto> productsWishes = new ArrayList<>();
		productsWishes.add(product1);
		updatedWishlistUser.setId(1);
		updatedWishlistUser.setProdotti(productsWishes);

		Mockito.when(wishlistDAO.doRetrieveProductByKey(product2.getCodiceProdotto(), wishlistUser)).thenReturn(product2);
		Mockito.when(wishlistDAO.doDeleteProduct(product2.getCodiceProdotto(), wishlistUser)).thenReturn(updatedWishlistUser);

		Mockito.when(wishlistDAO.doRetrieveWishlistByKey(user, wishlistUser.getId())).thenReturn(updatedWishlistUser);

		Wishlist result = wishlistService.rimuoviProdottoDaWishlist(wishlistUser, user, product2);
		
		assertEquals(result.getProdotti().size(), 1);
		assertFalse(result.getProdotti().contains(product2));

	}


}
