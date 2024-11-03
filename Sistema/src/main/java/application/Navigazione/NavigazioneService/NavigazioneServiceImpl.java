package application.Navigazione.NavigazioneService;

import java.sql.SQLException;
import java.util.Collection;

import application.Navigazione.NavigazioneService.NavigazioneException.ErroreRicercaCategoriaException;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.*;

/**
 * Questa classe fornisce un'implementazione concreta dei servizi per la navigazione 
 * all'interno del negozio online: visualizzazione della pagina di un prodotto,
 * ricerca mediante menu di navigazione, ricerca mediante barra di ricerca.
 * 
 * @see application.Navigazione.NavigazioneService.NavigazioneService
 * @see	application.Navigazione.NavigazioneService.Prodotto
 * @see application.Navigazione.NavigazioneService.ProxyProdotto
 * @see application.Navigazione.NavigazioneService.ObjectProdotto
 * @see application.Navigazione.NavigazioneService.ProdottoException
 * 
 * @author Dorotea Serrelli 
 * 
 * */

public class NavigazioneServiceImpl implements NavigazioneService{
	
	private ProdottoDAODataSource productDAO;

	public NavigazioneServiceImpl(ProdottoDAODataSource productDAO) {
		this.productDAO = productDAO;
	}
	
	/**
	 * Il metodo implementa il servizio di recupero delle specifiche complete 
	 * di un prodotto per poterle visualizzare.
	 * 
	 * @param pp : il prodotto per il quale recuperare le specifiche dal database
	 * 
	 * @return il prodotto pp con le specifiche complete
	 * 
	 * @throws CategoriaProdottoException : @see application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException
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
	 * @throws SottocategoriaProdottoException : @see application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * @throws CategoriaProdottoException : @see application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws ErroreRicercaCategoriaException 
	 * */
	@Override
	public Collection<ProxyProdotto> ricercaProdottoMenu(String c, int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException, ErroreRicercaCategoriaException {
		
		if(!c.equalsIgnoreCase(Categoria.GRANDI_ELETTRODOMESTICI.toString()) &&
				!c.equalsIgnoreCase(Categoria.PICCOLI_ELETTRODOMESTICI.toString()) &&
				!c.equalsIgnoreCase(Categoria.PRODOTTI_ELETTRONICA.toString()) &&
				!c.equalsIgnoreCase(Categoria.TELEFONIA.toString()))
			throw new ErroreRicercaCategoriaException("Non esiste la categoria scelta per la ricerca");
		
		return productDAO.searchingByCategory(null, c, page, perPage);
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
	 * @throws CategoriaProdottoException : @see application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * @throws SQLException 
	 * */
	@Override
	public Collection<ProxyProdotto> ricercaProdottoBar(String keyword, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException {
		
		if(keyword.isBlank())
			return null;
		return productDAO.searching("NOME", keyword, page, perPage);
	}

}