package application.GestioneWishlistService;

import java.sql.SQLException;
import java.util.ArrayList;

import application.GestioneWishlistService.WishlistException.ProdottoNonPresenteException;
import application.GestioneWishlistService.WishlistException.ProdottoPresenteException;
import application.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.NavigazioneService.ProxyProdotto;
import application.RegistrazioneService.ProxyUtente;
import storage.WishlistDAO.WishlistDAODataSource;

/**
 * Questa classe fornisce un'implementazione concreta dei servizi per la gestione della wishlist.
 * @see application.GestioneWishlistService.GestioneWishlistService
 * @see	application.GestioneWishlistService.Wishlist
 * @see application.NavigazioneService.ProxyProdotto
 * 
 * @author Dorotea Serrelli 
 * */

public class GestioneWishlistServiceImpl implements GestioneWishlistService{
	
	/**
	 * Il metodo si occupa di fornire la wishlist di un utente.
	 * 
	 * @param user : il proprietario della wishlist
	 * @return la wishlist del proprietario
	 * 
	 * @throws SQLException relativa al recupero dei dati dal database per 
	 * 		   costruire la wishlist dell'utente user
	 * */
	
	@Override
	public Wishlist recuperaWishlist(ProxyUtente user) throws SQLException {
		WishlistDAODataSource dao = new WishlistDAODataSource();
		Wishlist ws = new Wishlist(user);
		if(dao.doRetrieveWishlistByKey(user) != null) {
			ArrayList<ProxyProdotto> products = new ArrayList<>(dao.doRetrieveAll("", ws));
			ws.setProdotti(products);
			return ws;	
		}
		return null;
	}
	
	/**
	 * Il metodo si occupa di visualizzare i prodotti contenuti
	 * nella wishlist di un utente.
	 * 
	 * @param user : il proprietario della wishlist
	 * @param wishes : la wishlist dell'utente user
	 * @return il contenuto della wishlist del proprietario
	 * 
	 * @throws SQLException relativa al recupero dei dati dal database per 
	 * 		   costruire la wishlist dell'utente user
	 * */
	@Override
	public ArrayList<ProxyProdotto> visualizzaWishlist(Wishlist wishes, ProxyUtente user) throws SQLException {
		WishlistDAODataSource dao = new WishlistDAODataSource();
		Wishlist ws;
		if((ws = dao.doRetrieveWishlistByKey(user)) != null) {
			ArrayList<ProxyProdotto> products = new ArrayList<>(dao.doRetrieveAll("", ws));
			wishes.setProdotti(products);
			return wishes.getProdotti();	
		}
		System.out.println("Wishlist vuota");
		return null;
	}
	
	/**
	 * Il metodo si occupa di aggiungere un prodotto selezionato dall'utente
	 * nella wishlist.
	 * @param wishes : la wishlist
	 * @param user : il proprietario della wishlist
	 * @param prod : il prodotto da aggiungere alla wishlist
	 * @return la wishlist contenente il nuovo prodotto
	 * 
	 * @throws SQLException per gestire eccezione dovuta al recupero del prodotto prod nel database,
	 * 						utile per verificare se prod è già nella wishlist wishes.
	 * */
	@Override
	public Wishlist aggiungiProdottoInWishlist(Wishlist wishes, ProxyProdotto prod, ProxyUtente user)
			throws ProdottoPresenteException,
			application.GestioneWishlistService.WishlistException.ProdottoNulloException, SQLException {
		
		WishlistDAODataSource dao = new WishlistDAODataSource();
		if(dao.doRetrieveProductByKey(prod.getCodiceProdotto(), wishes) != null)
			throw new ProdottoPresenteException("Il prodotto selezionato e\' gia\' presente nella wishlist!");
		else {
			dao.doSaveProduct(prod, wishes);
			Wishlist newWishes = dao.doRetrieveWishlistByKey(user);
			ArrayList<ProxyProdotto> products = new ArrayList<>(dao.doRetrieveAll("", newWishes));
			newWishes.setProdotti(products);
			return newWishes;
		}
	}
	
	/**
	 * Il metodo si occupa di rimuovere un prodotto, selezionato
	 * dall'utente, dalla wishlist.
	 * 
	 * @param wishes : la wishlist dell'utente
	 * @param user : il proprietario della wishlist
	 * @param prod : il prodotto da rimuovere dalla wishlist
	 * 
	 * @return la wishlist priva del prodotto rimosso
	 * @throws SQLException per gestire eccezione dovuta al recupero del prodotto prod nel database,
	 * 						utile per verificare se prod è presente nella wishlist wishes. 
	 * */

	@Override
	public Wishlist rimuoviDallaWishlist(Wishlist wishes, ProxyUtente user, ProxyProdotto prod)
			throws application.GestioneWishlistService.WishlistException.ProdottoNonPresenteException,
			WishlistVuotaException, application.GestioneWishlistService.WishlistException.ProdottoNulloException, SQLException {
		
		WishlistDAODataSource dao = new WishlistDAODataSource();
		if(dao.doRetrieveProductByKey(prod.getCodiceProdotto(), wishes) == null)
			throw new ProdottoNonPresenteException("Il prodotto selezionato non e\' presente nella wishlist!");
		else {
			dao.doDeleteProduct(prod.getCodiceProdotto(), wishes);
			Wishlist newWishes = dao.doRetrieveWishlistByKey(user);
			ArrayList<ProxyProdotto> products = new ArrayList<>(dao.doRetrieveAll("", newWishes));
			newWishes.setProdotti(products);
			return newWishes;
		}
	}

}
