package application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService;

import java.sql.SQLException;
import java.util.Collection;

import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.FormatoFornitoreException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoDisponibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

/**
 * Interfaccia che si occupa di offrire operazioni relative alla gestione delle richieste
 * di approvvigionamento di prodotti: creazione di una richiesta di approvvigionamento e 
 * visualizzazione delle richieste di rifornimento effettuate
 * dal negozio.
 * 
 * @see application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.GestioneApprovvigionamentiServiceImpl
 * @see application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamento
 * @see application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException
 * 
 * @author Dorotea Serrelli
 * 
 * */

public interface GestioneApprovvigionamentiService {
	
	/**
	 * Il metodo rappresenta il servizio di visualizzazione delle richieste 
	 * di approvvigionamento di prodotti dell'e-commerce.
	 * Si visualizzano le richieste di approvvigionamento mediante il meccanismo della paginazione.
	 * 
	 * @param page : numero della pagina
	 * @param perPage: numero di richieste di approvvigionamento per pagina
	 * 
	 * @return una collezione di richieste di rifornimento dei prodotti non disponibili
	 * 			in magazzino
	 * 
	 * @throws SQLException 
	 * @throws ProdottoVendibileException : gestisce la richiesta di approvvigionamento di un prodotto
	 * 										non in vendita nell'e-commerce.
	 * @throws QuantitaProdottoException : gestisce l'inserimento di quantità di rifornimento errata
	 * @throws DescrizioneDettaglioException : gestisce il caso in cui manca una descrizione di dettaglio
	 * 											da allegare alla richiesta di approvvigionamento
	 * @throws FormatoFornitoreException : gestisce la specifica del fornitore espressa in formato non corretto
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 *   
	 * */
	
	public Collection<RichiestaApprovvigionamento> visualizzaRichiesteFornitura(int page, int perPage) throws FormatoFornitoreException, DescrizioneDettaglioException, QuantitaProdottoException, ProdottoVendibileException, SQLException, SottocategoriaProdottoException, CategoriaProdottoException;
	

	/**
	 * Il metodo implementa il servizio di memorizzazione di una richiesta 
	 * di approvvigionamento di un prodotto dell'e-commerce.
	 * 
	 * @param fornitore : il nominativo del fornitore
	 * @param emailFornitore : l'email del fornitore
	 * @param descrizione : la descrizione da corredo per la 
	 * 						richiesta di rifornimento del prodotto.
	 * @param quantità : la quantità di prodotto che si 
	 * 					 richiede per il rifornimento.
	 * @param prodotto : il prodotto per cui si effettua la
	 * 					 richiesta di approvvigionamento.
	 * 
	 * @return la richiesta di approvvigionamento
	 * 
	 * @throws QuantitaProdottoDisponibileException : eccezione lanciata nel momento in cui il prodotto non è esaurito in
	 * 													magazzino. 
	 * 
	 * @throws FormatoFornitoreException : eccezione lanciata nel caso di formato non corretto delle informazioni 
	 * 								relative ad un fornitore.
	 * 
	 * @throws QuantitaProdottoException : eccezione lanciata nel caso di quantità di un prodotto non valida
	 * 
	 * @throws DescrizioneDettaglioException : eccezione lanciata nel caso in cui la descrizione da allegare è vuota
	 * 
	 * @throws ProdottoVendibileException : eccezione lanciata nel caso in cui si fa richiesta di rifornimento 
	 * 										di un prodotto non venduto dal negozio online
	 * 
	 * @throws FormatoEmailException : eccezione che gestisce il caso in cui l'email del fornitore è espressa nel
	 * 									formato non corretto
	 * 
	 * */

	public RichiestaApprovvigionamento effettuaRichiestaApprovvigionamento(ProxyProdotto product, int quantity, String supplier, String emailSupplier, String description) throws QuantitaProdottoException, DescrizioneDettaglioException, ProdottoVendibileException, QuantitaProdottoDisponibileException, FormatoFornitoreException, FormatoEmailException, SQLException;
}
