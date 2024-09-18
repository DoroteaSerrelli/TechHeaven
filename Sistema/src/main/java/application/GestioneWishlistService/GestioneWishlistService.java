package application.GestioneWishlistService;

import java.sql.SQLException;
import java.util.ArrayList;
import application.GestioneWishlistService.WishlistException.ProdottoNonPresenteException;
import application.GestioneWishlistService.WishlistException.ProdottoNulloException;
import application.GestioneWishlistService.WishlistException.ProdottoPresenteException;
import application.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProxyProdotto;
import application.RegistrazioneService.ProxyUtente;


/**
 * Interfaccia che si occupa di offrire servizi relativi alla
 * gestione della lista dei desideri dell'utente: visualizzazione della wishlist, 
 * aggiunta di un prodotto nella  wishlist, rimozione di un prodotto dalla wishlist.
 * 
 * @see application.GestioneWishlistService.GestioneWishlistServiceImpl
 * @see application.GestioneWishlistService.Wishlist
 * @see application.GestioneWishlistService.WishlistException
 * @see application.NavigazioneService.ProxyProdotto
 * 
 * @author Dorotea Serrelli
 * 
 * */

public interface GestioneWishlistService {
	
	
	/**
	 * Questo metodo si occupa di fornire la wishlist di un utente.
	 * Per la realizzazione corrente del sistema software, l'utente può creare
	 * una sola wishlist.
	 * Per implementazioni future si può pensare di creare più wishlist per lo stesso
	 * utente.
	 * 
	 * @param user : il proprietario della wishlist
	 * @param id : l'identificativo della wishlist
	 * 
	 * @return la wishlist con identificativo id del proprietario user
	 * 
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * */
	
	public Wishlist recuperaWishlist(ProxyUtente user, int id) throws SQLException, CategoriaProdottoException;
	
	/**
	 * Questo metodo si occupa di fornire l'elenco dei prodotti
	 * presenti nella wishlist.
	 * 
	 * @param user : il proprietario della wishlist
	 * @param wishes: la wishlist
	 * 
	 * @return l'insieme dei prodotti nella wishlist
	 * 
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * */
	
	public ArrayList<ProxyProdotto> visualizzaWishlist(Wishlist wishes, ProxyUtente user) throws SQLException, CategoriaProdottoException;
	
	/**
	 * Questo metodo si occupa di aggiungere un prodotto
	 * nella wishlist.
	 * 
	 * @param wishes : la wishlist
	 * @param user : il proprietario della wishlist
	 * @param prod : il prodotto da aggiungere alla wishlist
	 * 
	 * @return la wishlist contenente il nuovo prodotto
	 * 
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * */
	
	public Wishlist aggiungiProdottoInWishlist(Wishlist wishes, ProxyProdotto prod, ProxyUtente user) throws ProdottoPresenteException, ProdottoNulloException, SQLException, CategoriaProdottoException;
	
	/**
	 * Questo metodo si occupa di rimuovere un prodotto
	 * dalla wishlist.
	 * 
	 * @param wishes : la wishlist dell'utente
	 * @param user : il proprietario della wishlist
	 * @param prod : il prodotto da rimuovere dalla wishlist
	 * 
	 * @return la wishlist priva del prodotto rimosso
	 * 
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * */
	
	public Wishlist rimuoviProdottoDaWishlist(Wishlist wishes, ProxyUtente user, ProxyProdotto prod) throws ProdottoNonPresenteException, WishlistVuotaException, ProdottoNulloException, SQLException, CategoriaProdottoException;

}