package application.NavigazioneService;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import application.NavigazioneService.ObjectProdotto.Categoria;
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
	 * */
	@Override
	public Prodotto visualizzaProdotto(ProxyProdotto pp) {
		return pp.mostraProdotto();
	}
	
	/**
	 * Il metodo implementa un servizio dell'interfaccia NavigazioneService:
	 * la ricerca di un prodotto mediante menu di navigazione.
	 * @param c la categoria selezionata dal cliente per la quale ricercare i prodotti
	 * @return i prodotti appartenenti alla categoria c, ordinati per nome ed impaginati.
	 * */
	@Override
	public List<ProxyProdotto> ricercaProdottoMenu(Categoria c) {
		
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();
		List<ProxyProdotto> productsCategory = new ArrayList<>();
		try {
			Collection<ProxyProdotto> products = pdao.doRetrieveAllExistent("NOME", 1, 10);
			for(ProxyProdotto p : products) {
				if(p.getCategoriaAsString().equals(c.toString()))
					productsCategory.add(p);
			}				
					
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return productsCategory;
	}
	
	/**
	 * Il metodo implementa un servizio dell'interfaccia NavigazioneService:
	 * la ricerca di un prodotto mediante barra di ricerca.
	 * @param keyword la parola chiave sulla quale fare la ricerca nel catalogo dei prodotti
	 * @return i prodotti, ordinati per nome ed impaginati, che contengono nel nome, nel modello, 
	 * nel brand, nella descrizione in evidenza o nella descrizione dettagliata la parola
	 * keyword.
	 * */
	@Override
	public List<ProxyProdotto> ricercaProdottoBar(String keyword) {
		
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();
		List<ProxyProdotto> productsSearchBar = new ArrayList<>();
		
		try {
			productsSearchBar = (List<ProxyProdotto>) pdao.searching("NOME", keyword, 1, 10);		

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productsSearchBar;
	}

}