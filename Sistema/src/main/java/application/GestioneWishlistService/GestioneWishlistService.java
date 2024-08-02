package application.GestioneWishlistService;

import java.sql.SQLException;
import java.util.ArrayList;
import application.GestioneWishlistService.WishlistException.ProdottoNonPresenteException;
import application.GestioneWishlistService.WishlistException.ProdottoNulloException;
import application.GestioneWishlistService.WishlistException.ProdottoPresenteException;
import application.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.NavigazioneService.ProxyProdotto;
import application.RegistrazioneService.ProxyUtente;


/**
 * Interfaccia che si occupa di offrire servizi relativi alla
 * gestione della lista dei desideri dell'utente: visualizzazione della wishlist, 
 * aggiunta di un prodotto nella  wishlist, rimozione di un prodotto dalla wishlist.
 * 
 * @author Dorotea Serrelli
 * */

public interface GestioneWishlistService {
	
	/**
	 * Questo metodo si occupa di fornire la wishlist di un utente.
	 * 
	 * @param user : il proprietario della wishlist
	 * @return la wishlist del proprietario
	 * @throws SQLException 
	 * */
	
	public Wishlist recuperaWishlist(ProxyUtente user) throws SQLException;
	
	
	/**
	 * Questo metodo si occupa di fornire l'elenco dei prodotti
	 * presenti nella wishlist.
	 * @see application.NavigazioneService.ProxyProdotto
	 * 
	 * @param user : il proprietario della wishlist
	 * @param wishes: la wishlist
	 * @return l'insieme dei prodotti nella wishlist
	 * @throws SQLException 
	 * */
	
	public ArrayList<ProxyProdotto> visualizzaWishlist(Wishlist wishes, ProxyUtente user) throws SQLException;
	
	/**
	 * Questo metodo si occupa di aggiungere un prodotto
	 * nella wishlist.
	 * @param wishes : la wishlist
	 * @param user : il proprietario della wishlist
	 * @param prod : il prodotto da aggiungere alla wishlist
	 * @return la wishlist contenente il nuovo prodotto
	 * @throws SQLException 
	 * */
	
	public Wishlist aggiungiProdottoInWishlist(Wishlist wishes, ProxyProdotto prod, ProxyUtente user) throws ProdottoPresenteException, ProdottoNulloException, SQLException;
	
	/**
	 * Questo metodo si occupa di rimuovere un prodotto
	 * dalla wishlist.
	 * @param wishes : la wishlist dell'utente
	 * @param user : il proprietario della wishlist
	 * @param prod : il prodotto da rimuovere dalla wishlist
	 * 
	 * @return la wishlist priva del prodotto rimosso
	 * @throws SQLException 
	 * */
	
	public Wishlist rimuoviDallaWishlist(Wishlist wishes, ProxyUtente user, ProxyProdotto prod) throws ProdottoNonPresenteException, WishlistVuotaException, ProdottoNulloException, SQLException;

}