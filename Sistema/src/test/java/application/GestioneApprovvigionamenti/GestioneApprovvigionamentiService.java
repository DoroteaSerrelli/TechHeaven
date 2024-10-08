package application.GestioneApprovvigionamenti;

import java.sql.SQLException;
import java.util.Collection;

import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.FornitoreException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

/**
 * Interfaccia che si occupa di offrire operazioni relative alla gestione delle richieste
 * di approvvigionamento di prodotti: creazione di una richiesta di approvvigionamento e 
 * visualizzazione delle richieste di rifornimento effettuate
 * dal negozio.
 * 
 * @see application.GestioneApprovvigionamenti.GestioneApprovvigionamentiServiceImpl
 * @see application.GestioneApprovvigionamenti.RichiestaApprovvigionamento
 * @see application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException
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
	 * @throws FornitoreException : gestisce la specifica del fornitore espressa in formato non corretto
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * */
	
	public Collection<RichiestaApprovvigionamento> visualizzaRichiesteFornitura(int page, int perPage) throws FornitoreException, DescrizioneDettaglioException, QuantitaProdottoException, ProdottoVendibileException, SQLException, SottocategoriaProdottoException, CategoriaProdottoException;
	

	/**
	 * Il metodo implementa il servizio di memorizzazione di una richiesta 
	 * di approvvigionamento di un prodotto dell'e-commerce.
	 * 
	 * @param supply : la richiesta di rifornimento di un prodotto da memorizzare
	 * 
	 * @throws SQLException 
	 * */
	
	public void effettuaRichiestaApprovvigionamento(RichiestaApprovvigionamento supply) throws SQLException;
}
