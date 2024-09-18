package application.NavigazioneService;

import java.sql.SQLException;
import java.util.Collection;

import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.*;

/**
 * Questa classe fornisce un'implementazione concreta dei servizi per la navigazione 
 * all'interno del negozio online: visualizzazione della pagina di un prodotto,
 * ricerca mediante menu di navigazione, ricerca mediante barra di ricerca.
 * 
 * @see application.NavigazioneService.NavigazioneService
 * @see	application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ProxyProdotto
 * @see application.NavigazioneService.ObjectProdotto
 * @see application.NavigazioneService.ProdottoException
 * 
 * @author Dorotea Serrelli 
 * 
 * */

public class NavigazioneServiceImpl implements NavigazioneService{
	
	/**
	 * Il metodo implementa il servizio di recupero delle specifiche complete 
	 * di un prodotto per poterle visualizzare.
	 * 
	 * @param pp : il prodotto per il quale recuperare le specifiche dal database
	 * 
	 * @return il prodotto pp con le specifiche complete
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	@Override
	public Prodotto visualizzaProdotto(ProxyProdotto pp) throws SottocategoriaProdottoException, CategoriaProdottoException {
		return pp.mostraProdotto();
	}
	
	/**
	 * Il metodo implementa la ricerca di un prodotto mediante menu di navigazione.
	 * Per mostrare i prodotti inerenti alla ricerca viene effettuata la paginazione
	 * degli stessi.
	 * 
	 * @param c : la categoria scelta (Telefonia, Prodotti elettronici, Grandi elettrodomestici, 
	 * 				Piccoli elettrodomestici)
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return una collezione di prodotti appartenenti alla categoria c
	 * 
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * */
	@Override
	public Collection<ProxyProdotto> ricercaProdottoMenu(Categoria c, int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException {
		
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();
		return pdao.searchingByCategory(null, c.toString(), page, perPage);
	}
	
	/**
	 * Il metodo implementa la ricerca di un prodotto mediante barra di ricerca.
	 * Per mostrare i prodotti inerenti alla ricerca viene effettuata la paginazione
	 * degli stessi per nome.
	 * 
	 * @param keyword : la parola chiave per effettuare la ricerca nel catalogo di prodotti
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return una collezione di prodotti che contengono nel nome, nel modello, 
	 * 			nella marca, nella descrizione in evidenza o nella descrizione dettagliata la parola
	 * 			keyword.
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * @throws SQLException 
	 * */
	@Override
	public Collection<ProxyProdotto> ricercaProdottoBar(String keyword, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {
		
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();
		return pdao.searching("NOME", keyword, page, perPage);
	}

}