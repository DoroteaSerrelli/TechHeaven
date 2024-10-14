package application.NavigazioneService;

import java.sql.SQLException;
import java.util.Collection;

import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

/**
 * Interfaccia che si occupa di offrire servizi relativi alla
 * navigazione del negozio online: visualizzazione della pagina di un prodotto,
 * ricerca mediante menu di navigazione, ricerca mediante barra di ricerca.
 * 
 * @see application.NavigazioneService.NavigazioneServiceImpl
 * @see application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ProxyProdotto
 * @see application.NavigazioneService.ProdottoException
 * 
 * @author Dorotea Serrelli
 * 
 * */

public interface NavigazioneService {
	
	/**
	 * Il metodo fornisce le informazioni dettagliate di un prodotto.
	 * 
	 * @param pp : il prodotto per il quale recuperare le specifiche dal database
	 * 
	 * @return il prodotto pp con le specifiche complete
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	public Prodotto visualizzaProdotto(ProxyProdotto pp) throws SottocategoriaProdottoException, CategoriaProdottoException;
	
	/**
	 * Il metodo rappresenta la ricerca di tutti i prodotti appartenenti ad una categoria.
	 * Per mostrare i prodotti inerenti alla ricerca viene effettuata la paginazione
	 * degli stessi.
	 * 
	 * @param c : la categoria scelta (Telefonia, Prodotti elettronici, Grandi elettrodomestici, 
	 * 			Piccoli elettrodomestici)
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return una collezione di prodotti appartenenti alla categoria c
	 * 
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * */
	
	public Collection<ProxyProdotto> ricercaProdottoMenu(String c, int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException;
	
	/**
	 * Il metodo rappresenta la ricerca di tutti i prodotti che soddisfano una chiave di ricerca.
	 * Per mostrare i prodotti inerenti alla ricerca viene effettuata la paginazione
	 * degli stessi.
	 * 
	 * @param keyword : la parola chiave per effettuare la ricerca nel catalogo di prodotti
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return una collezione di prodotti che possiedono keyword nelle proprie specifiche (nome, descrizione in primo piano, 
	 * 			descrizione dettagliata, modello, marca)
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * @throws SQLException 
	 * */
	
	public Collection<ProxyProdotto> ricercaProdottoBar(String keyword, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException;
}
