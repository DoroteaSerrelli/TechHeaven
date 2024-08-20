package application.NavigazioneService;

import java.sql.SQLException;
import java.util.List;

import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

/**
 * Interfaccia che si occupa di offrire servizi relativi alla
 * navigazione del negozio online: visualizzazione della pagina di un prodotto,
 * ricerca mediante menu di navigazione, ricerca mediante barra di ricerca.
 * 
 * @author Dorotea Serrelli
 * */

public interface NavigazioneService {
	
	/**
	 * Il metodo fornisce le informazioni dettagliate di un prodotto.
	 * @param pp è il prodotto per il quale recuperare le specifiche dal database
	 * @return il prodotto con le specifiche complete
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * */
	public Prodotto visualizzaProdotto(ProxyProdotto pp) throws SottocategoriaProdottoException, CategoriaProdottoException;
	
	/**
	 * Il metodo rappresenta la ricerca di tutti i prodotti appartenenti ad una categoria.
	 * @param c è la categoria scelta (Telefonia, Prodotti elettronici, Grandi elettrodomestici, 
	 * Piccoli elettrodomestici)
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return i prodotti della categoria c
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException 
	 * @throws CategoriaProdottoException 
	 * */
	public List<ProxyProdotto> ricercaProdottoMenu(Categoria c, int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException;
	
	/**
	 * Il metodo rappresenta la ricerca di tutti i prodotti che soddisfano una chiave di ricerca.
	 * @param keyword è la parola chiave per effettuare la ricerca nel catalogo di prodotti
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return i prodotti che possiedono keyword nelle proprie specifiche
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * */
	public List<ProxyProdotto> ricercaProdottoBar(String keyword, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException;
}
