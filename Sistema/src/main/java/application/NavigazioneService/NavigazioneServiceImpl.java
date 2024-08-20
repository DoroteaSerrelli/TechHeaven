package application.NavigazioneService;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.*;

/**
 * Questa classe fornisce un'implementazione concreta dei servizi per la navigazione all'interno del
 * negozio online.
 * @see application.NavigazioneService.NavigazioneService
 * @see	application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ProxyProdotto
 * @see application.NavigazioneService.ObjectProdotto
 * 
 * @author Dorotea Serrelli 
 * */

public class NavigazioneServiceImpl implements NavigazioneService{
	
	/**
	 * Il metodo implementa un servizio dell'interfaccia NavigazioneService: 
	 * il recupero delle specifiche complete di un prodotto per poterle visualizzare.
	 * @param pp è il prodotto da far visualizzare
	 * @return le specifiche complete del prodotto
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * */
	@Override
	public Prodotto visualizzaProdotto(ProxyProdotto pp) throws SottocategoriaProdottoException, CategoriaProdottoException {
		return pp.mostraProdotto();
	}
	
	/**
	 * Il metodo implementa un servizio dell'interfaccia NavigazioneService:
	 * la ricerca di un prodotto mediante menu di navigazione.
	 * @param c la categoria selezionata dal cliente per la quale ricercare i prodotti
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return i prodotti appartenenti alla categoria c, ordinati per nome ed impaginati.
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException 
	 * @throws CategoriaProdottoException 
	 * */
	@Override
	public List<ProxyProdotto> ricercaProdottoMenu(Categoria c, int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException {
		
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();
		List<ProxyProdotto> productsCategory = (List<ProxyProdotto>) pdao.searchingByCategory(null, c.toString(), page, perPage);
		
		return productsCategory;
	}
	
	/**
	 * Il metodo implementa un servizio dell'interfaccia NavigazioneService:
	 * la ricerca di un prodotto mediante barra di ricerca.
	 * @param keyword la parola chiave sulla quale fare la ricerca nel catalogo dei prodotti
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return i prodotti, ordinati per nome ed impaginati, che contengono nel nome, nel modello, 
	 * nel brand, nella descrizione in evidenza o nella descrizione dettagliata la parola
	 * keyword.
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * */
	@Override
	public List<ProxyProdotto> ricercaProdottoBar(String keyword, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException {
		
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();
		List<ProxyProdotto> productsSearchBar = new ArrayList<>();
		
		try {
			productsSearchBar = (List<ProxyProdotto>) pdao.searching("NOME", keyword, page, perPage);		

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productsSearchBar;
	}

}