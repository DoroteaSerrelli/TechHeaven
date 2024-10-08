package application.GestioneWishlistService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import application.GestioneWishlistService.WishlistException.ProdottoNonPresenteException;
import application.GestioneWishlistService.WishlistException.ProdottoPresenteException;
import application.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProxyProdotto;
import application.RegistrazioneService.ProxyUtente;
import storage.WishlistDAO.WishlistDAODataSource;

/**
 * Questa classe fornisce un'implementazione concreta dei servizi per la gestione della wishlist.
 * 
 * @see application.GestioneWishlistService.GestioneWishlistService
 * @see application.GestioneWishlistService.Wishlist
 * @see application.GestioneWishlistService.WishlistException
 * @see application.NavigazioneService.ProxyProdotto
 * 
 * @author Dorotea Serrelli 
 * */

public class GestioneWishlistServiceImpl implements GestioneWishlistService{
		
	/**
	 * Il metodo si occupa di fornire la wishlist, identificata da un codice,
	 * di un utente.
	 * 
	 * @param user : il proprietario della wishlist
	 * @param id : l'identificativo della wishlist
	 * 
	 * @return ws : la wishlist del proprietario
	 * 
	 * @throws SQLException relativa al recupero dei dati dal database per 
	 * 		   costruire la wishlist dell'utente user
	 * 
	 * @throws CategoriaProdottoException 
	 * */
	
	@Override
	public Wishlist recuperaWishlist(ProxyUtente user, int id) throws SQLException, CategoriaProdottoException {
		WishlistDAODataSource dao = new WishlistDAODataSource();
		Wishlist ws = new Wishlist(user, id);
		if(dao.doRetrieveWishlistByKey(user, id) != null) {
			Collection<ProxyProdotto> products = new ArrayList<>(dao.doRetrieveAllWishes("", ws));
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
	 * 
	 * @return i prodotti preferiti dell'utente user, memorizzati in wishes
	 * 
	 * @throws SQLException relativa al recupero dei dati dal database per 
	 * 		   costruire la wishlist dell'utente user
	 * @throws CategoriaProdottoException 
	 * */
	
	@Override
	public Collection<ProxyProdotto> visualizzaWishlist(Wishlist wishes, ProxyUtente user) throws SQLException, CategoriaProdottoException {
		WishlistDAODataSource dao = new WishlistDAODataSource();
		Wishlist ws;
		if((ws = dao.doRetrieveWishlistByKey(user, wishes.getId())) != null) {
			ArrayList<ProxyProdotto> products = new ArrayList<>(dao.doRetrieveAllWishes("", ws));
			wishes.setProdotti(products);
			return wishes.getProdotti();	
		}
		System.out.println("Wishlist vuota");
		return null;
	}
	
	/**
	 * Il metodo si occupa di aggiungere un prodotto selezionato dall'utente
	 * nella wishlist.
	 * 
	 * @param wishes : la wishlist
	 * @param user : il proprietario della wishlist
	 * @param prod : il prodotto da aggiungere alla wishlist
	 * 
	 * @return newWishes : la wishlist contenente il prodotto prod
	 * 
	 * @throws SQLException per gestire eccezione dovuta al recupero del prodotto prod nel database,
	 * 						utile per verificare se prod è già nella wishlist wishes.
	 * @throws CategoriaProdottoException 
	 * */
	
	@Override
	public Wishlist aggiungiProdottoInWishlist(Wishlist wishes, ProxyProdotto prod, ProxyUtente user)
			throws ProdottoPresenteException,
			application.GestioneWishlistService.WishlistException.ProdottoNulloException, SQLException, CategoriaProdottoException {
		
		WishlistDAODataSource dao = new WishlistDAODataSource();
		if(dao.doRetrieveProductByKey(prod.getCodiceProdotto(), wishes) != null)
			throw new ProdottoPresenteException("Il prodotto selezionato e\' gia\' presente nella wishlist!");
		else {
			dao.doSaveProduct(prod, wishes);
			Wishlist newWishes = dao.doRetrieveWishlistByKey(user, wishes.getId());
			ArrayList<ProxyProdotto> products = new ArrayList<>(dao.doRetrieveAllWishes("", newWishes));
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
	 * @return newWishes : la wishlist priva del prodotto prod
	 * 
	 * @throws SQLException per gestire eccezione dovuta al recupero del prodotto prod nel database,
	 * 						utile per verificare se prod è presente nella wishlist wishes. 
	 * @throws CategoriaProdottoException 
	 * */

	@Override
	public Wishlist rimuoviProdottoDaWishlist(Wishlist wishes, ProxyUtente user, ProxyProdotto prod)
			throws application.GestioneWishlistService.WishlistException.ProdottoNonPresenteException,
			WishlistVuotaException, application.GestioneWishlistService.WishlistException.ProdottoNulloException, SQLException, CategoriaProdottoException {
		
		WishlistDAODataSource dao = new WishlistDAODataSource();
		if(dao.doRetrieveProductByKey(prod.getCodiceProdotto(), wishes) == null)
			throw new ProdottoNonPresenteException("Il prodotto selezionato non e\' presente nella wishlist!");
		else {
			dao.doDeleteProduct(prod.getCodiceProdotto(), wishes);
			Wishlist newWishes = dao.doRetrieveWishlistByKey(user, wishes.getId());
			ArrayList<ProxyProdotto> products = new ArrayList<>(dao.doRetrieveAllWishes("", newWishes));
			newWishes.setProdotti(products);
			return newWishes;
		}
	}

}
