package application.GestioneCatalogoService;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale.Category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import application.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNulloException;
import application.GestioneOrdiniService.OrdineException.IndirizzoSpedizioneNulloException;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.FormatoCodiceException;
import application.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.NavigazioneService.ProdottoException.FormatoModelloException;
import application.NavigazioneService.ProdottoException.FormatoNomeException;
import application.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneCatalogoServiceImplTest {


	private GestioneCatalogoServiceImpl catalogoService;
	private ProdottoDAODataSource productDAO;


	@BeforeEach
	public void setUp() {
		productDAO = Mockito.mock(ProdottoDAODataSource.class);
		catalogoService = new GestioneCatalogoServiceImpl(productDAO);

	}


	/**
	 * TEST CASES PER AGGIUNTA DI UN PRODOTTO NEL CATALOGO
	 * 
	 * TC14.1_1 : il codice del prodotto non è un numero
	 * 
	 * TC14.1_2 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto è presente nel database
	 * 
	 * TC14.1_3 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto non è definito nel formato corretto
	 * 
	 * TC14.1_4 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello non è definito nel formato corretto
	 * 
	 * TC14.1_5 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca non è specificata con il corretto formato
	 * 
	 * TC14.1_6 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo non è > 0.0
	 * 
	 * TC14.1_7 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione è vuota
	 * 
	 * TC14.1_8 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio è vuota
	 * 
	 * TC14.1_9 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto non è > 0
	 * 
	 * TC14.2_0 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria non è specificata nel formato corretto
	 * 
	 * TC14.2_1 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria è specificata nel formato corretto,
	 * 			  non si specifica la sottocategoria
	 * 
	 * TC14.2_2 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria è specificata nel formato corretto,
	 * 			  si specifica la sottocategoria ed è espressa nel formato
	 * 			  non corretto
	 * 
	 * TC14.2_3 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria è specificata nel formato corretto,
	 * 			  si specifica la sottocategoria ed è espressa nel formato
	 * 			  corretto,
	 * 			  si è assegnata una sottocategoria che non appartiene alla 
	 * 			  categoria specificata
	 * 
	 * TC14.2_4 : il codice del prodotto è un numero,
	 * 			  il codice del prodotto non è presente nel database,
	 * 			  il nome del prodotto è definito nel formato corretto,
	 * 			  il modello è definito nel formato corretto,
	 * 			  la marca è specificata con il corretto formato,
	 * 			  il prezzo è > 0.0,
	 * 			  la descrizione di presentazione non è vuota,
	 * 			  la descrizione di dettaglio non è vuota,
	 * 			  la quantità disponibile per un prodotto è > 0,
	 * 			  la categoria è specificata nel formato corretto,
	 * 			  si specifica la sottocategoria ed è espressa nel formato
	 * 			  corretto,
	 * 			  si è assegnata una sottocategoria che appartiene alla 
	 * 			  categoria specificata
	 * 
	 * 
	 * */

	@Test
	public void TC14_1_1() {

		String errorCode = "12A";
		String name = "Samsung Gear S2 Classic";
		String brand = "Samsung";
		String model = "Gear S2";
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		assertThrows(FormatoCodiceException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(errorCode, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});


	}

	@Test
	public void TC14_1_2() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String existCode = "22";
		String name = "Samsung Gear S2 Classic";
		String brand = "Samsung";
		String model = "Gear S2";
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		ProxyProdotto existedProduct = new ProxyProdotto(22, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);


		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(existCode))).thenReturn(existedProduct);

		assertThrows(ProdottoInCatalogoException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(existCode, name, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});

	}

	@Test
	public void TC14_1_3() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String errorName = "Samsung Gear S2 Classic\\";
		String brand = "Samsung";
		String model = "Gear S2";
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoNomeException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, errorName, brand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_1_4() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String errorModel = "Gear\\@S2";
		String brand = "Samsung";
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoModelloException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, errorModel, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_1_5() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String errorBrand = "Samsung123#";
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoMarcaException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, errorBrand, model, topDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_1_6() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		float errorPrice = Float.parseFloat("0.0");
		String topDescription = "Lorem ipsum";
		String details = "Lorem ipsum Lorem";
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(PrezzoProdottoException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, errorPrice,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_1_7() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String errorTopDescription = "";
		String details = "Lorem ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoTopDescrizioneException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, errorTopDescription, details, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_1_8() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String topDescription = "Lorem ipsum";
		String errorDetails = "";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(FormatoDettagliException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, errorDetails, price,
					quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_1_9() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String topDescription = "Lorem ipsum";
		String details = "Lorem Ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int errorQuantity = 0;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(QuantitaProdottoException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					errorQuantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_2_0() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String topDescription = "Lorem ipsum";
		String details = "Lorem Ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String errorCategory = "ERRORE";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(CategoriaProdottoException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, errorCategory, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_2_1() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoInCatalogoException, QuantitaProdottoException, FormatoNomeException, FormatoModelloException, FormatoMarcaException, PrezzoProdottoException, FormatoTopDescrizioneException, FormatoDettagliException, AppartenenzaSottocategoriaException, FormatoCodiceException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String topDescription = "Lorem ipsum";
		String details = "Lorem Ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "TELEFONIA";
		String subCategory = null;
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);

		Collection<ProxyProdotto> newCatalogue = new ArrayList<>();
		newCatalogue.add(product1);
		newCatalogue.add(product2);
		newCatalogue.add(new ProxyProdotto(32, name, topDescription, details, price, Categoria.valueOf(category.toUpperCase()), brand, model, quantity, inCatalogo, inVetrina, productDAO));

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(newCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
				quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);

		assertEquals(newCatalogue, updatedCatalogue);
		Mockito.verify(productDAO).doSave(new Prodotto(32, name, topDescription, details, price, Categoria.valueOf(category.toUpperCase()), brand, model, quantity, inCatalogo, inVetrina));
	}

	@Test
	public void TC14_2_2() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String topDescription = "Lorem ipsum";
		String details = "Lorem Ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "TELEFONIA";
		String errorSubCategory = "ERRORE";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(SottocategoriaProdottoException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, errorSubCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_2_3() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String topDescription = "Lorem ipsum";
		String details = "Lorem Ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "TELEFONIA";
		String errorSubCategory = "PC";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);

		assertThrows(AppartenenzaSottocategoriaException.class, () -> {
			catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
					quantity, category, errorSubCategory, inCatalogo, inVetrina, productDAO, page, perPage);
		});
	}

	@Test
	public void TC14_2_4() throws NumberFormatException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoInCatalogoException, QuantitaProdottoException, FormatoNomeException, FormatoModelloException, FormatoMarcaException, PrezzoProdottoException, FormatoTopDescrizioneException, FormatoDettagliException, AppartenenzaSottocategoriaException, FormatoCodiceException {

		String code = "32";
		String name = "Samsung Gear S2 Classic";
		String model = "Gear-S2";
		String brand = "Samsung";
		String topDescription = "Lorem ipsum";
		String details = "Lorem Ipsum Lorem";
		float price = Float.parseFloat("340.99");
		int quantity = 2;
		String category = "PRODOTTI_ELETTRONICA";
		String subCategory = "SMARTWATCH";
		boolean inCatalogo = true;
		boolean inVetrina = false;

		int page = 1;
		int perPage = 5;

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);

		Collection<ProxyProdotto> newCatalogue = new ArrayList<>();
		newCatalogue.add(product1);
		newCatalogue.add(product2);
		newCatalogue.add(new ProxyProdotto(32, name, topDescription, details, price, Categoria.valueOf(category.toUpperCase()), Sottocategoria.valueOf(subCategory.toUpperCase()), brand, model, quantity, inCatalogo, inVetrina, productDAO));

		Mockito.when(productDAO.doRetrieveProxyByKey(Integer.parseInt(code))).thenReturn(null);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(newCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiuntaProdottoInCatalogo(code, name, brand, model, topDescription, details, price,
				quantity, category, subCategory, inCatalogo, inVetrina, productDAO, page, perPage);

		assertEquals(newCatalogue, updatedCatalogue);
		Mockito.verify(productDAO).doSave(new Prodotto(32, name, topDescription, details, price, Categoria.valueOf(category.toUpperCase()), Sottocategoria.valueOf(subCategory.toUpperCase()), brand, model, quantity, inCatalogo, inVetrina));
	}


	/**
	 * TEST CASES PER LA RIMOZIONE DI UN PRODOTTO DAL CATALOGO
	 * 
	 * TC15.1_1: non viene specificato il prodotto da rimuovere
	 * 
	 * TC15.1_2: viene specificato il prodotto da rimuovere dal catalogo
	 * 			 del negozio
	 * 
	 * */

	public void TC15_1_1() {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);

		int page = 1;
		int perPage = 5;

		ProxyProdotto toRemove = null;

		assertThrows(ProdottoNulloException.class, () -> {
			catalogoService.rimozioneProdottoDaCatalogo(toRemove, page, perPage);
		});

	}

	public void TC15_1_2() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ProdottoNulloException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);

		int page = 1;
		int perPage = 5;

		ProxyProdotto toRemove = product1;
		
		Collection<ProxyProdotto> newCatalogue = catalogue;
		newCatalogue.remove(product1);
		
		Mockito.when(productDAO.doRetrieveProxyByKey(toRemove.getCodiceProdotto())).thenReturn(product1);
		Mockito.when(productDAO.doDelete(toRemove.getCodiceProdotto())).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(newCatalogue);
		
		Collection<ProxyProdotto> updatedCatalogue = catalogoService.rimozioneProdottoDaCatalogo(toRemove, page, perPage);
		
		assertFalse(updatedCatalogue.contains(product1));
		Mockito.verify(productDAO).doDelete(product1.getCodiceProdotto());

	}

}
