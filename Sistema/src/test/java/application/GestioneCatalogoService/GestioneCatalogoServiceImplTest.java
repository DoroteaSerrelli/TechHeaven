package application.GestioneCatalogoService;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoAggiornatoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNulloException;
import application.GestioneOrdiniService.OrdineException.IndirizzoSpedizioneNulloException;
import application.NavigazioneService.ObjectProdotto;
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
import application.NavigazioneService.ProdottoException.FormatoVetrinaException;
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


	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * DESCRIZIONE IN EVIDENZA.
	 * 
	 * TC16_1.1_1 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare non dichiarata correttamente
	 * 
	 * TC16_1.1_2 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di presentazione,
	 * 				la nuova descrizione di presentazione è un testo vuoto
	 * 				
	 * TC16_1.1_3 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di presentazione,
	 * 				la nuova descrizione di presentazione non è un testo vuoto,
	 * 				nuova top descrizione == vecchia top descrizione
	 * 
	 * TC16_1.1_4 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di presentazione,
	 * 				la nuova descrizione di presentazione non è un testo vuoto,
	 * 				nuova top descrizione != vecchia top descrizione
	 * 
	 * */

	@Test
	public void TC16_1_1_1() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String errorInfoToUpdate = "ERRORE"; 

		String updatedData = "";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(ErroreSpecificaAggiornamentoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, errorInfoToUpdate, updatedData, page, perPage);
		});

	}

	@Test
	public void TC16_1_1_2() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "DESCRIZIONE_EVIDENZA"; 

		String updatedData = "";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(FormatoTopDescrizioneException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}

	@Test
	public void TC16_1_1_3() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "DESCRIZIONE_EVIDENZA"; 

		String updatedData = "Prova";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}

	@Test
	public void TC16_1_1_4() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoAggiornatoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto doUpdateProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(doUpdateProduct);


		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "DESCRIZIONE_EVIDENZA"; 

		String updatedData = "Questa descrizione è una prova";
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", updatedData, "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(product1);
		expectedCatalogue.add(updatedProduct);

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);
		Mockito.when(productDAO.updateData(doUpdate.getCodiceProdotto(), "TOPDESCRIZIONE", updatedData)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updateData(doUpdate.getCodiceProdotto(), "TOPDESCRIZIONE", updatedData);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProduct));

	}


	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * DESCRIZIONE DI DETTAGLIO.
	 * 
	 * TC16_1.1_5 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di dettaglio,
	 * 				la nuova descrizione di dettaglio è un testo vuoto
	 * 
	 * TC16_1.1_6 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di dettaglio,
	 * 				la nuova descrizione di dettaglio non è un testo vuoto,
	 * 				nuova descrizione dettaglio == vecchia descrizione dettaglio
	 * 				
	 * TC16_1.1_7 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la descrizione di dettaglio,
	 * 				la nuova descrizione di dettaglio non è un testo vuoto,
	 * 				nuova descrizione dettaglio != vecchia descrizione dettaglio
	 * 
	 * */

	@Test
	public void TC16_1_1_5() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "DESCRIZIONE_DETTAGLIATA"; 

		String updatedData = "";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(FormatoDettagliException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_1_6() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "DESCRIZIONE_DETTAGLIATA"; 

		String updatedData = "Prova";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_1_7() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoAggiornatoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto doUpdateProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(doUpdateProduct);


		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "DESCRIZIONE_DETTAGLIATA"; 

		String updatedData = "Questa descrizione di dettaglio è una prova";
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", updatedData, Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(product1);
		expectedCatalogue.add(updatedProduct);

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);
		Mockito.when(productDAO.updateData(doUpdate.getCodiceProdotto(), "DETTAGLI", updatedData)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updateData(doUpdate.getCodiceProdotto(), "DETTAGLI", updatedData);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProduct));

	}


	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * MODELLO.
	 * 
	 * TC16_1.1_8 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare il modello,
	 * 				il nuovo modello non è espresso nel formato corretto
	 * 
	 * TC16_1.1_9 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare il modello,
	 * 				il nuovo modello è espresso nel formato corretto
	 * 				nuovo modello == vecchio modello
	 * 				
	 * TC16_1.2_0 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare il modello,
	 * 				il nuovo modello è espresso nel formato corretto
	 * 				nuovo modello != vecchio modello
	 * 
	 * */

	@Test
	public void TC16_1_1_8() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "MODELLO"; 

		String updatedData = "%errorModello-&";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(FormatoModelloException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_1_9() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "MODELLO"; 

		String updatedData = "Redmi Note 13";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_2_0() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoAggiornatoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto doUpdateProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(doUpdateProduct);


		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "MODELLO"; 

		String updatedData = "PROVA56-3";
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", updatedData, 180, true, false);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(product1);
		expectedCatalogue.add(updatedProduct);

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);
		Mockito.when(productDAO.updateData(doUpdate.getCodiceProdotto(), "MODELLO", updatedData)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updateData(doUpdate.getCodiceProdotto(), "MODELLO", updatedData);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProduct));

	}

	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * MARCA.
	 * 
	 * TC16_1.2_1 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la marca,
	 * 				la marca non è espressa nel formato corretto
	 * 
	 * TC16_1.2_2 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la marca,
	 * 				la marca è espressa nel formato corretto,
	 * 				nuova marca == vecchia marca
	 * 				
	 * TC16_1.2_3 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la marca,
	 * 				la marca è espressa nel formato corretto,
	 * 				nuova marca != vecchia marca
	 * 
	 * */

	@Test
	public void TC16_1_2_1() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "MARCA"; 

		String updatedData = "errorMARC4-";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(FormatoMarcaException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_2_2() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "MARCA"; 

		String updatedData = "Xiaomi";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_2_3() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoAggiornatoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto doUpdateProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(doUpdateProduct);


		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "MARCA"; 

		String updatedData = "NUOVA MARCA";
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, updatedData, "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(product1);
		expectedCatalogue.add(updatedProduct);

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);
		Mockito.when(productDAO.updateData(doUpdate.getCodiceProdotto(), "MARCA", updatedData)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updateData(doUpdate.getCodiceProdotto(), "MARCA", updatedData);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProduct));

	}

	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * CATEGORIA.
	 * 
	 * TC16_1.2_4 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la categoria,
	 * 				la categoria non appartiene a {TELEFONIA, PRODOTTI_ELETTRONICA, 
	 * 				PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI}
	 * 
	 * TC16_1.2_5 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la categoria,
	 * 				la categoria appartiene a {TELEFONIA, PRODOTTI_ELETTRONICA, 
	 * 				PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI},
	 * 				nuova categoria == vecchia categoria
	 * 				
	 * TC16_1.2_6 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la categoria,
	 * 				la categoria appartiene a {TELEFONIA, PRODOTTI_ELETTRONICA, 
	 * 				PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI},
	 * 				nuova categoria != vecchia categoria
	 * 
	 * */

	@Test
	public void TC16_1_2_4() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "CATEGORIA"; 

		String updatedData = "errorCATEGORIA";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(CategoriaProdottoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_2_5() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "CATEGORIA"; 

		String updatedData = "TELEFONIA";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_2_6() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoAggiornatoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto doUpdateProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(doUpdateProduct);


		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "CATEGORIA"; 

		String updatedData = "PRODOTTI_ELETTRONICA";
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.valueOf(updatedData), Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(product1);
		expectedCatalogue.add(updatedProduct);

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);
		Mockito.when(productDAO.updateData(doUpdate.getCodiceProdotto(), "CATEGORIA", updatedData)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updateData(doUpdate.getCodiceProdotto(), "CATEGORIA", updatedData);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProduct));

	}

	/**
	 * TEST CASES PER LA MODIFICA DELLE SPECIFICHE DI UN PRODOTTO: 
	 * SOTTOCATEGORIA.
	 * 
	 * TC16_1.2_7 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la sottocategoria,
	 * 				la sottocategoria non appartiene a {TABLET, SMARTPHONE, 
	 * 				PC, SMARTWATCH}
	 * 
	 * TC16_1.2_8 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la sottocategoria,
	 * 				la sottocategoria appartiene a {TABLET, SMARTPHONE, 
	 * 				PC, SMARTWATCH},
	 * 				nuova sottocategoria == vecchia sottocategoria
	 * 				
	 * TC16_1.2_9 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la sottocategoria,
	 * 				la sottocategoria appartiene a {TABLET, SMARTPHONE, 
	 * 				PC, SMARTWATCH},
	 * 				nuova categoria != vecchia categoria,
	 * 				sottocategoria non è associata alla categoria del prodotto
	 * 
	 * TC16_1.3_0 : prodotto selezionato dal catalogo, 
	 * 				informazione da modificare dichiarata correttamente,
	 * 				si vuole cambiare la sottocategoria,
	 * 				la sottocategoria appartiene a {TABLET, SMARTPHONE, 
	 * 				PC, SMARTWATCH},
	 * 				nuova categoria != vecchia categoria,
	 * 				sottocategoria è associata alla categoria del prodotto
	 * 
	 * */

	@Test
	public void TC16_1_2_7() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "SOTTOCATEGORIA"; 

		String updatedData = "errorSOTTOCATEGORIA";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(SottocategoriaProdottoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}


	@Test
	public void TC16_1_2_8() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "SOTTOCATEGORIA"; 

		String updatedData = "SMARTPHONE";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}

	@Test
	public void TC16_1_2_9() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);

		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "SOTTOCATEGORIA"; 

		String updatedData = "PC";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);

		assertThrows(AppartenenzaSottocategoriaException.class, () -> {
			catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}

	@Test
	public void TC16_1_3_0() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoAggiornatoException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto doUpdateProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Prodotto doUpdate = new Prodotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(doUpdateProduct);


		//deve essere DESCRIZIONE_EVIDENZA, DESCRIZIONE_DETTAGLIATA, MODELLO, MARCA, 
		//CATEGORIA, SOTTOCATEGORIA
		String infoToUpdate = "SOTTOCATEGORIA"; 

		String updatedData = "TABLET";
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.valueOf(updatedData), "Xiaomi", "Redmi Note 13", 180, true, false);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(product1);
		expectedCatalogue.add(updatedProduct);

		Mockito.when(productDAO.doRetrieveCompleteByKey(doUpdate.getCodiceProdotto())).thenReturn(doUpdate);
		Mockito.when(productDAO.updateData(doUpdate.getCodiceProdotto(), "SOTTOCATEGORIA", updatedData)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoSpecificheProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updateData(doUpdate.getCodiceProdotto(), "SOTTOCATEGORIA", updatedData);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProduct));

	}

	/**
	 * TEST CASES MODIFICA DELLA MESSA IN EVIDENZA DI UN PRODOTTO
	 * 
	 * TC16_2.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la messa in evidenza di un prodotto,
	 * 			   il nuovo valore non appartiene a {0, 1}
	 * 
	 * TC16_2.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la messa in evidenza di un prodotto,
	 * 			   il nuovo valore appartiene a {0, 1},
	 * 			   nuovo valore vetrina == vecchio valore vetrina
	 * 
	 * TC16_2.1_3: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la messa in evidenza di un prodotto,
	 * 			   il nuovo valore appartiene a {0, 1},
	 * 			   nuovo valore vetrina != vecchio valore vetrina
	 * 
	 * */

	@Test
	public void TC16_2_1_1() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		ProxyProdotto doUpdateProxy = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false);


		//deve essere VETRINA
		String infoToUpdate = "VETRINA"; 

		String updatedData = "";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);

		assertThrows(FormatoVetrinaException.class, () -> {
			catalogoService.aggiornamentoProdottoInVetrina(doUpdate, infoToUpdate, updatedData, page, perPage);
		});
	}

	@Test
	public void TC16_2_1_2() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		ProxyProdotto doUpdateProxy = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false);


		//deve essere VETRINA
		String infoToUpdate = "VETRINA"; 

		String updatedData = "FALSE";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoProdottoInVetrina(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}

	@Test
	public void TC16_2_1_3() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoVetrinaException, ProdottoAggiornatoException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto product3 = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);


		ProxyProdotto doUpdateProxy = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false);



		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(product3);


		//deve essere VETRINA
		String infoToUpdate = "VETRINA"; 

		String updatedData = "TRUE";
		int updatedDataInt = 1;
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, Boolean.parseBoolean(updatedData), productDAO);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(product1);
		expectedCatalogue.add(product2);
		expectedCatalogue.add(updatedProduct);

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);
		Mockito.when(productDAO.updateDataView(doUpdate.getCodiceProdotto(), updatedDataInt)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoProdottoInVetrina(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updateDataView(doUpdate.getCodiceProdotto(), updatedDataInt);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProxy));

	}


	/**
	 * TEST CASES MODIFICA DEL PREZZO DI UN PRODOTTO
	 * 
	 * TC16_3.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è il prezzo di un prodotto,
	 * 			   il nuovo valore non è un numero con la virgola arrotondato in centesimi
	 * 
	 * TC16_3.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è il prezzo di un prodotto,
	 * 			   il nuovo valore è un numero con la virgola arrotondato in centesimi,
	 * 			   nuovo prezzo == vecchio prezzo
	 * 
	 * TC16_3.1_3: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è il prezzo di un prodotto,
	 * 			   il nuovo valore è un numero con la virgola arrotondato in centesimi,
	 * 			   nuovo prezzo != vecchio prezzo
	 * 
	 * */

	@Test
	public void TC16_3_1_1() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		ProxyProdotto doUpdateProxy = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);


		//deve essere PREZZO
		String infoToUpdate = "PREZZO"; 

		String updatedData = "";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);

		assertThrows(PrezzoProdottoException.class, () -> {
			catalogoService.aggiornamentoPrezzoProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});
	}

	@Test
	public void TC16_3_1_2() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		ProxyProdotto doUpdateProxy = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);


		//deve essere PREZZO
		String infoToUpdate = "PREZZO"; 

		String updatedData = "454.50";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoPrezzoProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}

	@Test
	public void TC16_3_1_3() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ProdottoAggiornatoException, PrezzoProdottoException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		ProxyProdotto product2 = new ProxyProdotto(3, "Xiaomi Redmi Note 13", "Prova", "Prova", Float.parseFloat("229.90"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Xiaomi", "Redmi Note 13", 180, true, false, productDAO);

		ProxyProdotto product3 = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);


		ProxyProdotto doUpdateProxy = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);


		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(product3);


		//deve essere PREZZO
		String infoToUpdate = "PREZZO"; 

		String updatedData = "359.99";
		float updatedDataFloat = Float.parseFloat(updatedData);
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("359.99"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, Boolean.parseBoolean(updatedData), productDAO);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(updatedProduct);
		expectedCatalogue.add(product2);
		expectedCatalogue.add(product3);

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);
		Mockito.when(productDAO.updatePrice(doUpdate.getCodiceProdotto(), updatedDataFloat)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoPrezzoProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updatePrice(doUpdate.getCodiceProdotto(), updatedDataFloat);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProxy));

	}

	/**
	 * TEST CASES MODIFICA DELLA QUANTITA' DI UN PRODOTTO
	 * 
	 * TC16_4.1_1: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la quantità in magazzino del prodotto,
	 * 			   il nuovo valore non è un intero positivo
	 * 
	 * TC16_4.1_2: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la quantità in magazzino del prodotto,
	 * 			   il nuovo valore è un intero positivo,
	 * 			   nuova quantità == vecchia quantità
	 * 
	 * TC16_4.1_3: prodotto selezionato dal catalogo, 
	 * 			   informazione da modificare è la quantità in magazzino del prodotto,
	 * 			   il nuovo valore è un intero positivo,
	 * 			   nuova quantità != vecchia quantità
	 * 
	 * */

	@Test
	public void TC16_4_1_1() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {
		
		ProxyProdotto doUpdateProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);


		//deve essere QUANTITA
		String infoToUpdate = "QUANTITA"; 

		String updatedData = "-23";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);

		assertThrows(QuantitaProdottoException.class, () -> {
			catalogoService.aggiornamentoDisponibilitàProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});
	}

	@Test
	public void TC16_4_1_2() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {

		ProxyProdotto doUpdateProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);

		//deve essere PREZZO
		String infoToUpdate = "QUANTITA"; 

		String updatedData = "4";
		int page = 1;
		int perPage = 5;

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);

		assertThrows(ProdottoAggiornatoException.class, () -> {
			catalogoService.aggiornamentoDisponibilitàProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);
		});

	}

	@Test
	public void TC16_4_1_3() throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ProdottoAggiornatoException, QuantitaProdottoException {

		ProxyProdotto product1 = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false);
		
		ProxyProdotto product2 = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		ProxyProdotto product3 = new ProxyProdotto(16, "Samsung Galaxy A34 5G", "Prova", "Prova", Float.parseFloat("234.50"), 
				Categoria.TELEFONIA, Sottocategoria.SMARTPHONE, "Samsung", "Galaxy A34", 0, true, false, productDAO);


		ProxyProdotto doUpdateProxy = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false, productDAO);

		Prodotto doUpdate = new Prodotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 4, true, false);


		Collection<ProxyProdotto> catalogue = new ArrayList<>();
		catalogue.add(product1);
		catalogue.add(product2);
		catalogue.add(product3);


		//deve essere QUANTITA
		String infoToUpdate = "QUANTITA"; 

		String updatedData = "2";
		int updatedDataInt = Integer.parseInt(updatedData);
		int page = 1;
		int perPage = 5;

		ProxyProdotto updatedProduct = new ProxyProdotto(0, "Apple AirPods Pro 2", "Prova", "Prova", Float.parseFloat("254.50"), 
				Categoria.PRODOTTI_ELETTRONICA, "Apple", "AirPods Pro 2", 2, true, false, productDAO);


		Collection<ProxyProdotto> expectedCatalogue = new ArrayList<>();
		expectedCatalogue.add(product1);
		expectedCatalogue.add(updatedProduct);
		expectedCatalogue.add(product3);

		Mockito.when(productDAO.doRetrieveProxyByKey(doUpdateProxy.getCodiceProdotto())).thenReturn(doUpdateProxy);
		Mockito.when(productDAO.updateQuantity(doUpdate.getCodiceProdotto(), updatedDataInt)).thenReturn(true);
		Mockito.when(productDAO.doRetrieveAllExistent(null, page, perPage)).thenReturn(expectedCatalogue);

		Collection<ProxyProdotto> updatedCatalogue = catalogoService.aggiornamentoDisponibilitàProdotto(doUpdate, infoToUpdate, updatedData, page, perPage);

		Mockito.verify(productDAO).updateQuantity(doUpdate.getCodiceProdotto(), updatedDataInt);
		assertEquals(updatedCatalogue, expectedCatalogue);
		assertTrue(updatedCatalogue.contains(updatedProduct));
		assertFalse(updatedCatalogue.contains(doUpdateProxy));

	}
	

}
