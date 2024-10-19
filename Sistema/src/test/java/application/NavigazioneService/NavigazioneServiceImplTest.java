package application.NavigazioneService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class NavigazioneServiceImplTest {
	private NavigazioneServiceImpl navigazioneService;
	private ProdottoDAODataSource productDAO;

	@BeforeEach
	public void setUp() {
		productDAO = Mockito.mock(ProdottoDAODataSource.class);
		navigazioneService = new NavigazioneServiceImpl(productDAO);
	}
	
	
	/**
	 * TEST CASES PER RICERCA DI PRODOTTI PER MENU DI NAVIGAZIONE
	 * 
	 * TC7.1_1: la categoria non è specificata nel formato corretto
	 * TC7.1_2: la categoria è specificata nel formato corretto
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException 
	 * @throws CategoriaProdottoException 
	 * 
	 * */
	
	@Test
	public void TC7_1_1() throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException {
		
		Collection<ProxyProdotto> smallCatalogue = new ArrayList<>();
		ProxyProdotto p1 = new ProxyProdotto(1, "Tablet", "Prova", "Prova", Float.valueOf("129.99"), Categoria.TELEFONIA, "Lenovo", "TabM11", 20, true, true);
		ProxyProdotto p2 = new ProxyProdotto(2, "Lavatrice", "Prova", "Prova", Float.valueOf("439.99"), Categoria.GRANDI_ELETTRODOMESTICI, "Candy", "White-X", 5, true, false);
		
		smallCatalogue.add(p1);
		smallCatalogue.add(p2);
		
		int page = 1, perPage = 3;
		String errorCategory = "errorCategory";
		Mockito.when(productDAO.searchingByCategory(null, errorCategory, page, perPage)).thenReturn(null);
		
		Collection<ProxyProdotto> results = navigazioneService.ricercaProdottoMenu(errorCategory, page, perPage);
		
		assertEquals(null, results);
		verify(productDAO, times(0)).searchingByCategory(null, errorCategory, page, perPage);
	}
	
	@Test
	public void TC7_1_2() throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException {
		
		Collection<ProxyProdotto> smallCatalogue = new ArrayList<>();
		ProxyProdotto p1 = new ProxyProdotto(1, "Tablet", "Prova", "Prova", Float.valueOf("129.99"), Categoria.TELEFONIA, "Lenovo", "TabM11", 20, true, true);
		ProxyProdotto p2 = new ProxyProdotto(2, "Smartphone", "Prova", "Prova", Float.valueOf("439.99"), Categoria.TELEFONIA, "Samsung", "S9Plus", 5, true, false);
		
		smallCatalogue.add(p1);
		smallCatalogue.add(p2);
		
		int page = 1, perPage = 3;
		String category = "TELEFONIA";
		Mockito.when(productDAO.searchingByCategory(null, category, page, perPage)).thenReturn(smallCatalogue);
		
		Collection<ProxyProdotto> results = navigazioneService.ricercaProdottoMenu(category, page, perPage);
		
		assertEquals(2, results.size());
		assertEquals(results, smallCatalogue);
		verify(productDAO).searchingByCategory(null, category, page, perPage);
	}
	
	
	/**
	 * TEST CASES PER RICERCA DI PRODOTTI PER BARRA DI RICERCA
	 * 
	 * TC8.1_1: la parola-chiave inserita ha lunghezza 0
	 * TC8.1_2: la parola-chiave ha lunghezza > 0 ma non è presente
	 * 			in alcuna specifica di qualche prodotto
	 * TC8.1_2: la parola-chiave ha lunghezza > 0 ed è presente
	 * 			nelle specifiche di almeno un prodotto
	 * */
	
	
	@Test
	public void TC8_1_1() throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException {
		
		Collection<ProxyProdotto> smallCatalogue = new ArrayList<>();
		ProxyProdotto p1 = new ProxyProdotto(1, "Tablet", "Prova", "Prova", Float.valueOf("129.99"), Categoria.TELEFONIA, "Lenovo", "TabM11", 20, true, true);
		ProxyProdotto p2 = new ProxyProdotto(2, "Smartphone", "Prova", "Prova", Float.valueOf("439.99"), Categoria.TELEFONIA, "Samsung", "S9Plus", 5, true, false);
		
		smallCatalogue.add(p1);
		smallCatalogue.add(p2);
		
		int page = 1, perPage = 3;
		String keyword = "";
		Mockito.when(productDAO.searching("NOME", keyword, page, perPage)).thenReturn(null);
		
		Collection<ProxyProdotto> results = navigazioneService.ricercaProdottoBar(keyword, page, perPage);
		
		assertEquals(null, results);
		verify(productDAO, times(0)).searching("NOME", keyword, page, perPage);
	}
	
	@Test
	public void TC8_1_2() throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException {
		
		Collection<ProxyProdotto> smallCatalogue = new ArrayList<>();
		ProxyProdotto p1 = new ProxyProdotto(1, "Tablet", "Prova", "Prova", Float.valueOf("129.99"), Categoria.TELEFONIA, "Lenovo", "TabM11", 20, true, true);
		ProxyProdotto p2 = new ProxyProdotto(2, "Smartphone", "Prova", "Prova", Float.valueOf("439.99"), Categoria.TELEFONIA, "Samsung", "S9Plus", 5, true, false);
		
		smallCatalogue.add(p1);
		smallCatalogue.add(p2);
		
		int page = 1, perPage = 3;
		String keyword = "Nokia";
		Mockito.when(productDAO.searching("NOME", keyword, page, perPage)).thenReturn(null);
		
		Collection<ProxyProdotto> results = navigazioneService.ricercaProdottoBar(keyword, page, perPage);
		
		assertEquals(null, results);
		verify(productDAO).searching("NOME", keyword, page, perPage);
	}
	
	@Test
	public void TC8_1_3() throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException {
		
		Collection<ProxyProdotto> smallCatalogue = new ArrayList<>();
		ProxyProdotto p1 = new ProxyProdotto(1, "Tablet", "Prova", "Prova", Float.valueOf("129.99"), Categoria.TELEFONIA, "Lenovo", "TabM11", 20, true, true);
		ProxyProdotto p2 = new ProxyProdotto(2, "Smartphone", "Prova", "Prova", Float.valueOf("439.99"), Categoria.TELEFONIA, "Samsung", "S9Plus", 5, true, false);
		
		smallCatalogue.add(p1);
		smallCatalogue.add(p2);
		
		int page = 1, perPage = 3;
		String keyword = "Prova";
		Mockito.when(productDAO.searching("NOME", keyword, page, perPage)).thenReturn(smallCatalogue);
		
		Collection<ProxyProdotto> results = navigazioneService.ricercaProdottoBar(keyword, page, perPage);
		
		assertEquals(2, results.size());
		assertEquals(results, smallCatalogue);
		verify(productDAO).searching("NOME", keyword, page, perPage);
	}
}
