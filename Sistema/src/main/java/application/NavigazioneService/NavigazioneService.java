package application.NavigazioneService;

import java.util.List;

import application.NavigazioneService.ObjectProdotto.Categoria;

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
	 * */
	public Prodotto visualizzaProdotto(ProxyProdotto pp);
	
	/**
	 * Il metodo rappresenta la ricerca di tutti i prodotti appartenenti ad una categoria.
	 * @param c è la categoria scelta (Telefonia, Prodotti elettronici, Grandi elettrodomestici, 
	 * Piccoli elettrodomestici)
	 * @return i prodotti della categoria c
	 * */
	public List<ProxyProdotto> ricercaProdottoMenu(Categoria c);
	
	/**
	 * Il metodo rappresenta la ricerca di tutti i prodotti che soddisfano una chiave di ricerca.
	 * @param keyword è la parola chiave per effettuare la ricerca nel catalogo di prodotti
	 * @return i prodotti che possiedono keyword nelle proprie specifiche
	 * */
	public List<ProxyProdotto> ricercaProdottoBar(String keyword);
}
