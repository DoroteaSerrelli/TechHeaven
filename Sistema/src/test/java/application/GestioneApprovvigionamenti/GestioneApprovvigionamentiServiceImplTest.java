package application.GestioneApprovvigionamenti;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;

import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.GestioneApprovvigionamentiServiceImpl;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamento;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.FormatoFornitoreException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoDisponibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import storage.GestioneApprovvigionamentiDAO.ApprovvigionamentoDAODataSource;
import storage.NavigazioneDAO.ProdottoDAODataSource;

public class GestioneApprovvigionamentiServiceImplTest {
	private GestioneApprovvigionamentiServiceImpl approvvigionamentiService;
	private ApprovvigionamentoDAODataSource supplyDAO;

	@BeforeEach
	public void setUp() {
		supplyDAO = Mockito.mock(ApprovvigionamentoDAODataSource.class);
		approvvigionamentiService = new GestioneApprovvigionamentiServiceImpl(supplyDAO);
	}
	
	/**
	 * TEST CASES PER CREARE UNA RICHIESTA DI APPROVVIGIONAMENTO
	 * 
	 * TC13.1_1 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  sono presenti scorte del prodotto in magazzino
	 * 
	 * TC13.1_2 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è errata nel formato
	 * 
	 * TC13.1_3 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è corretta nel formato,
	 * 			  il nome del fornitore è espresso non correttamente
	 * 
	 * TC13.1_4 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è corretta nel formato,
	 * 			  il nome del fornitore è espresso correttamente,
	 * 			  l'email del fornitore è scritta non correttamente
	 * 
	 * TC13.1_5 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è corretta nel formato,
	 * 			  il nome del fornitore è espresso correttamente,
	 * 			  l'email del fornitore è scritta correttamente,
	 * 			  la descrizione è vuota
	 * 
	 * TC13.1_6 : prodotto presente nel database,
	 * 			  prodotto presente nel catalogo del negozio,
	 * 			  non sono presenti scorte del prodotto in magazzino,
	 * 			  la quantità del prodotto specificata è corretta nel formato,
	 * 			  il nome del fornitore è espresso correttamente,
	 * 			  l'email del fornitore è scritta correttamente,
	 * 			  la descrizione è un testo non vuoto
	 * 
	 * */
	
	@Test
	public void TC13_1_1() {
		
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		
		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 80, true, false, productDAO);
		
		int quantity = 21;
		String supplier = "Esprinet";
		String emailSupplier = "info@esprinet.com";
		String description = "Prova";
		
		
		assertThrows(QuantitaProdottoDisponibileException.class , () -> {
			approvvigionamentiService.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, description);
		});
	}
	
	@Test
	public void TC13_1_2() {
		
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		
		
		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);
		
		int errorQuantity = -2;
		String supplier = "Esprinet";
		String emailSupplier = "info@esprinet.com";
		String description = "Prova";
		
		
		assertThrows(QuantitaProdottoException.class , () -> {
			approvvigionamentiService.effettuaRichiestaApprovvigionamento(product, errorQuantity, supplier, emailSupplier, description);
		});
	}
	
	@Test
	public void TC13_1_3() {
		
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		
		
		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);
		
		int quantity = 2;
		String errorSupplier = "Esprinet\\**";
		String emailSupplier = "info@esprinet.com";
		String description = "Prova";
		
		
		assertThrows(FormatoFornitoreException.class , () -> {
			approvvigionamentiService.effettuaRichiestaApprovvigionamento(product, quantity, errorSupplier, emailSupplier, description);
		});
	}
	
	@Test
	public void TC13_1_4() {
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		
		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);
		
		int quantity = 2;
		String supplier = "Esprinet";
		String errorEmailSupplier = "info@";
		String description = "Prova";
		
		
		assertThrows(FormatoEmailException.class , () -> {
			approvvigionamentiService.effettuaRichiestaApprovvigionamento(product, quantity, supplier, errorEmailSupplier, description);
		});
	}
	
	@Test
	public void TC13_1_5() {
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		
		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);
		
		int quantity = 2;
		String supplier = "Esprinet";
		String emailSupplier = "info@esprinet.com";
		String errorDescription = "";
		
		
		assertThrows(DescrizioneDettaglioException.class , () -> {
			approvvigionamentiService.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, errorDescription);
		});
	}
	
	@Test
	public void TC13_1_6() throws QuantitaProdottoException, DescrizioneDettaglioException, ProdottoVendibileException, QuantitaProdottoDisponibileException, FormatoFornitoreException, FormatoEmailException, SQLException {
		ProdottoDAODataSource productDAO = mock(ProdottoDAODataSource.class);
		
		ProxyProdotto product = new ProxyProdotto(12, "HP 15s-fq5040nl", "Prova", "Prova", Float.parseFloat("454.50"), 
				Categoria.PRODOTTI_ELETTRONICA, Sottocategoria.PC, "HP", "15s-fq5040nl", 0, true, false, productDAO);
		
		int quantity = 2;
		String supplier = "Esprinet";
		String emailSupplier = "info@esprinet.com";
		String description = "Prova";
		
		RichiestaApprovvigionamento request = approvvigionamentiService.effettuaRichiestaApprovvigionamento(product, quantity, supplier, emailSupplier, description);
		
		assertEquals(request.getProdotto(), product);
		assertEquals(request.getQuantitaRifornimento(), quantity);
		assertEquals(request.getFornitore(), supplier);
		assertEquals(request.getEmailFornitore(), emailSupplier);
		assertEquals(request.getDescrizione(), description);
		Mockito.verify(supplyDAO).doSave(request);
		
	}
}
