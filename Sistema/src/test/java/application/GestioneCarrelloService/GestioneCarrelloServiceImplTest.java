package application.GestioneCarrelloService;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import application.GestioneCarrello.GestioneCarrelloService.Carrello;
import application.GestioneCarrello.GestioneCarrelloService.GestioneCarrelloServiceImpl;
import application.GestioneCarrello.GestioneCarrelloService.ItemCarrello;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.QuantitaProdottoException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneCarrelloServiceImplTest {

	private GestioneCarrelloServiceImpl carrelloService;
	private ProdottoDAODataSource productDAO;

	@BeforeEach
	public void setUp() {
		productDAO = Mockito.mock(ProdottoDAODataSource.class);
		carrelloService = new GestioneCarrelloServiceImpl(productDAO);
	}

	/**
	 * TEST CASES PER AGGIUNTA PRODOTTO NEL CARRELLO
	 * 
	 * TC9_1.1_1: il carrello è vuoto e si vuole inserire un prodotto con n°scorte 
	 * 				in magazzino = 0
	 * 
	 * TC9_1.1_2 : il carrello è vuoto, il prodotto da inserire ha n° scorte > 0 (e il prodotto
	 * 				non è presente nel carrello)
	 * 
	 * TC9_1.1_3 : il carrello non è vuoto e si vuole inserire un prodotto con n° scorte = 0
	 * 
	 * TC9_1.1_4 : il carrello non è vuoto, il prodotto da inserire ha n° scorte > 0 e il 
	 * 				prodotto è presente nel carrello
	 * 
	 * TC9_1.1_5 : il carrello non è vuoto, il prodotto da inserire ha n° scorte > 0 e il
	 * 				prodotto non è presente nel carrello
	 *
	 * 
	 * */

	@Test
	public void TC9_1_1_1() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Carrello cart = new Carrello();

		ProxyProdotto product = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);

		ItemCarrello itemToInsert = new ItemCarrello();
		itemToInsert.setCodiceProdotto(product.getCodiceProdotto());

		Mockito.when(productDAO.doRetrieveProxyByKey(itemToInsert.getCodiceProdotto())).thenReturn(product);

		assertThrows(QuantitaProdottoException.class , () -> {
			carrelloService.aggiungiAlCarrello(cart, itemToInsert);
		});
	}

	@Test
	public void TC9_1_1_2() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException {

		Carrello cart = new Carrello();

		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova",  "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ItemCarrello itemToInsert = new ItemCarrello();
		itemToInsert.setCodiceProdotto(product.getCodiceProdotto());

		Mockito.when(productDAO.doRetrieveProxyByKey(itemToInsert.getCodiceProdotto())).thenReturn(product);

		Carrello updateCart = carrelloService.aggiungiAlCarrello(cart, itemToInsert);

		assertTrue(updateCart.getProducts().contains(itemToInsert));

	}

	@Test
	public void TC9_1_1_3() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException {

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova",  "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(25, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 0, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		cart.addProduct(item1);
		ItemCarrello itemToInsert = new ItemCarrello();
		itemToInsert.setCodiceProdotto(product1.getCodiceProdotto());

		Mockito.when(productDAO.doRetrieveProxyByKey(itemToInsert.getCodiceProdotto())).thenReturn(product2);

		assertThrows(QuantitaProdottoException.class , () -> {
			carrelloService.aggiungiAlCarrello(cart, itemToInsert);
		});

	}

	@Test
	public void TC9_1_1_4() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException {

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		cart.addProduct(item1);
		ItemCarrello itemToInsert = new ItemCarrello();
		itemToInsert.setCodiceProdotto(product1.getCodiceProdotto());

		Mockito.when(productDAO.doRetrieveProxyByKey(itemToInsert.getCodiceProdotto())).thenReturn(product1);

		assertThrows(ProdottoPresenteException.class , () -> {
			carrelloService.aggiungiAlCarrello(cart, itemToInsert);
		});
	}

	@Test
	public void TC9_1_1_5() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoPresenteException, ProdottoNulloException, QuantitaProdottoException {

		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(10, "Bosch lavatrice a carica frontale", "Prova", "Prova", Float.parseFloat("590.50"), 
				Categoria.GRANDI_ELETTRODOMESTICI, "Bosch", "QualcheModello", 112, true, false, productDAO);


		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		cart.addProduct(item1);
		ItemCarrello itemToInsert = new ItemCarrello();
		itemToInsert.setCodiceProdotto(product2.getCodiceProdotto());

		Mockito.when(productDAO.doRetrieveProxyByKey(itemToInsert.getCodiceProdotto())).thenReturn(product2);

		Carrello updatedCart = carrelloService.aggiungiAlCarrello(cart, itemToInsert);

		assertTrue(updatedCart.getProducts().contains(itemToInsert));
	}


	/**
	 * TEST CASES PER RIMOZIONE PRODOTTO DAL CARRELLO
	 * 
	 * TC9_2.1_1: il carrello non è vuoto e il prodotto si trova nel carrello
	 * 
	 * */

	@Test
	public void TC9_2_1_1() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException {
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(13, "Amazfit T-Rex 2", "Prova", "Prova", Float.parseFloat("160.00"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.SMARTWATCH, "Amazfit", "T-Rex2", 10, true, false, productDAO);


		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());

		cart.addProduct(item1);

		ItemCarrello item2 = new ItemCarrello();
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());

		cart.addProduct(item2);

		ItemCarrello itemToDelete = new ItemCarrello();
		itemToDelete.setCodiceProdotto(product2.getCodiceProdotto());

		Carrello updatedCart = carrelloService.rimuoviDalCarrello(cart, itemToDelete);

		assertFalse(updatedCart.getProducts().contains(itemToDelete));
	}


	/**
	 * TEST CASES PER AUMENTO DELLA QUANTITA' DI UN PRODOTTO NEL CARRELLO
	 * 
	 * TC9_3.1_1: il carrello non è vuoto, il prodotto si trova nel carrello
	 * 				e la quantità di aggiornamento non è nel formato corretto
	 * 
	 * TC9_3.1_2: il carrello non è vuoto, il prodotto si trova nel carrello
	 * 				e la quantità di aggiornamento è definita nel formato corretto
	 * 
	 * */

	@Test
	public void TC9_3_1_1() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, QuantitaProdottoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);


		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		

		cart.addProduct(item1);

		ItemCarrello item2 = new ItemCarrello();
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(4);

		cart.addProduct(item2);

		int quantityItem1 = 180;
		
		Mockito.when(productDAO.doRetrieveProxyByKey(item1.getCodiceProdotto())).thenReturn(product1);
		
		assertThrows(QuantitaProdottoException.class , () -> {
			carrelloService.aumentaQuantitaNelCarrello(cart, item1, quantityItem1);
		});
		
	}
	
	@Test
	public void TC9_3_1_2() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, QuantitaProdottoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);


		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setMarca(product1.getMarca());
		item1.setModello(product1.getModello());
		item1.setCategoria(product1.getCategoria());
		

		cart.addProduct(item1);

		ItemCarrello item2 = new ItemCarrello();
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setMarca(product2.getMarca());
		item2.setModello(product2.getModello());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(4);

		cart.addProduct(item2);

		int quantityItem1 = 40;
		
		Mockito.when(productDAO.doRetrieveProxyByKey(item1.getCodiceProdotto())).thenReturn(product1);
		
		Carrello updatedCart = carrelloService.aumentaQuantitaNelCarrello(cart, item1, quantityItem1);

		assertTrue(updatedCart.getProducts().contains(item1));
		ItemCarrello updatedItem1 = new ItemCarrello();
		updatedItem1.setCodiceProdotto(item1.getCodiceProdotto());
		for(ItemCarrello i : updatedCart.getProducts()) {
			if(i.getCodiceProdotto() == updatedItem1.getCodiceProdotto()) {
				updatedItem1 = i;
				break;
			}
		}
			
		assertEquals(40, updatedItem1.getQuantita());
		
	}
	
	/**
	 * TEST CASES PER DIMINUZIONE DELLA QUANTITA' DI UN PRODOTTO NEL CARRELLO
	 * 
	 * TC9_4.1_1: il carrello non è vuoto, il prodotto si trova nel carrello
	 * 				e la quantità di aggiornamento non è nel formato corretto
	 * 
	 * TC9_4.1_2: il carrello non è vuoto, il prodotto si trova nel carrello
	 * 				e la quantità di aggiornamento è definita nel formato corretto
	 * 
	 * */

	@Test
	public void TC9_4_1_1() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, QuantitaProdottoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);


		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setCategoria(product1.getCategoria());
		item1.setQuantita(25);

		cart.addProduct(item1);

		ItemCarrello item2 = new ItemCarrello();
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(4);

		cart.addProduct(item2);

		int quantityItem1 = 40;
		
		Mockito.when(productDAO.doRetrieveProxyByKey(item1.getCodiceProdotto())).thenReturn(product1);
		
		assertThrows(QuantitaProdottoException.class , () -> {
			carrelloService.decrementaQuantitaNelCarrello(cart, item1, quantityItem1);
		});
		
	}
	
	@Test
	public void TC9_4_1_2() throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, ProdottoPresenteException, QuantitaProdottoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {
		Carrello cart = new Carrello();

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova",  Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);

		ProxyProdotto product2 = new ProxyProdotto(14, "Samsung Galaxy Tab A7 Lite", "Prova", "Prova",  Float.parseFloat("110.00"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy Tab A7 Lite", 70, true, false, productDAO);


		ItemCarrello item1 = new ItemCarrello();
		item1.setCodiceProdotto(product1.getCodiceProdotto());
		item1.setNomeProdotto(product1.getNomeProdotto());
		item1.setPrezzo(product1.getPrezzo());
		item1.setMarca(product1.getMarca());
		item1.setModello(product1.getModello());
		item1.setCategoria(product1.getCategoria());
		item1.setQuantita(25);

		cart.addProduct(item1);

		ItemCarrello item2 = new ItemCarrello();
		item2.setCodiceProdotto(product2.getCodiceProdotto());
		item2.setNomeProdotto(product2.getNomeProdotto());
		item2.setMarca(product2.getMarca());
		item2.setModello(product2.getModello());
		item2.setPrezzo(product2.getPrezzo());
		item2.setCategoria(product2.getCategoria());
		item2.setQuantita(4);

		cart.addProduct(item2);

		int quantityItem1 = 12;
		
		Mockito.when(productDAO.doRetrieveProxyByKey(item1.getCodiceProdotto())).thenReturn(product1);
		
		Carrello updatedCart = carrelloService.decrementaQuantitaNelCarrello(cart, item1, quantityItem1);

		assertTrue(updatedCart.getProducts().contains(item1));
		ItemCarrello updatedItem1 = new ItemCarrello();
		updatedItem1.setCodiceProdotto(item1.getCodiceProdotto());
		for(ItemCarrello i : updatedCart.getProducts()) {
			if(i.getCodiceProdotto() == updatedItem1.getCodiceProdotto()) {
				updatedItem1 = i;
				break;
			}
		}
			
		assertEquals(12, updatedItem1.getQuantita());
		
		
	}

}