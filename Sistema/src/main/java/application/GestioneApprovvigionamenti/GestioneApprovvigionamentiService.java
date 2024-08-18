package application.GestioneApprovvigionamenti;

import java.sql.SQLException;
import java.util.Collection;

import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.FornitoreException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.QuantitaProdottoException;

/**
 * Servizi legati all'approvvigionamento dei prodotti dell'e-commerce
 * 
 * L'interfaccia offre i servizi relativi alla gestione delle richieste di approvvigionamento di prodotti: 
 * - creazione di una richiesta di approvvigionamento;
 * - visualizzazione delle richieste di rifornimento effettuate.
 * 
 * @author Dorotea Serrelli
 * 
 * */

public interface GestioneApprovvigionamentiService {
	
	/**
	 * Il metodo rappresenta il servizio di visualizzazione delle richieste 
	 * di approvvigionamento di prodotti dell'e-commerce.
	 * 
	 * @param page : numero della pagina
	 * @param perPage: numero di richieste di approvvigionamento per pagina
	 * 
	 * @return una collezione di richieste di rifornimento dei prodotti non disponibili
	 * 			in magazzino
	 * @throws SQLException 
	 * @throws ProdottoVendibileException 
	 * @throws QuantitaProdottoException 
	 * @throws DescrizioneDettaglioException 
	 * @throws FornitoreException 
	 * */
	
	public Collection<RichiestaApprovvigionamento> visualizzaRichiesteFornitura(int page, int perPage) throws FornitoreException, DescrizioneDettaglioException, QuantitaProdottoException, ProdottoVendibileException, SQLException;
	
	/**
	 * Il metodo rappresenta il servizio di visualizzazione delle richieste 
	 * di approvvigionamento di prodotti dell'e-commerce.
	 * 
	 * @param supply : la richiesta di rifornimento effettuata da memorizzare
	 * @throws SQLException 
	 * 
	 * */
	
	public void effettuaRichiestaApprovvigionamento(RichiestaApprovvigionamento supply) throws SQLException;
}
