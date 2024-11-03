package application.GestioneWishlist.GestioneWishlistService;

import java.util.ArrayList;
import java.util.Collection;

import application.GestioneWishlist.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Registrazione.RegistrazioneService.ProxyUtente;

/**
 * Classe che esprime il concetto di wishlist o "lista di desideri".
 * Essa contiene le informazioni relative alla wishlist: identificativo, 
 * riferimento all'utente proprietario, i prodotti che l'utente 
 * vorrebbe acquistare in un secondo momento presso l'e-commerce.
 * 
 * @see application.GestioneWishlist.GestioneWishlistService.GestioneWishlistService
 * @see application.GestioneWishlist.GestioneWishlistService.GestioneWishlistServiceImpl
 * @see application.GestioneWishlist.GestioneWishlistService.WishlistException
 * @see application.Registrazione.RegistrazioneService.ProxyUtente
 * @see application.Navigazione.NavigazioneService.ProxyProdotto
 * 
 * @author Dorotea Serrelli
 * */

public class Wishlist {
	
	/**
	 * utente : l'utente proprietario della lista dei desideri
	 * */
	
	private ProxyUtente utente;
	
	/**
	 * id : l'identificativo della wishlist dell'utente proprietario.
	 * Per la realizzazione corrente del sistema software, l'utente può creare
	 * una sola wishlist.
	 * Per implementazioni future si può pensare di creare più wishlist per lo stesso
	 * utente.
	 * 
	 * */
	
	private int id;

	/**
	 * prodotti : i prodotti che l'utente vorrebbe acquistare in un
	 * secondo momento.
	 * */
	
	private Collection<ProxyProdotto> prodotti;
	
	/**
	 * Costruttore di classe di default per una wishlist vuota.
	 * Si costruisce un oggetto della classe Wishlist rappresentante la wishlist 
	 * dell'utente user, priva di prodotti
	 * 
	 * @param user : l'utente per il quale si costruisce
	 * 				 la wishlist
	 * 
	 * */
	
	public Wishlist(ProxyUtente user) {
		this.utente = user;
		this.prodotti = new ArrayList<>();
	}
	
	/**
	 * Costruttore di classe di default per una wishlist vuota.
	 * Si costruisce un oggetto della classe Wishlist rappresentatnte la wishlist 
	 * con identificativo id dell'utente user, priva di prodotti.
	 * 
	 * @param user : l'utente 
	 * @param id : identificativo della wishlist
	 * 
	 * */
	
	public Wishlist(ProxyUtente user, int id) {
		this.utente = user;
		this.id = id;
		this.prodotti = new ArrayList<>();
	}
	
	/**
	 * Costruttore di classe per una wishlist non vuota.
	 * 
	 * Si costruisce un oggetto della classe Wishlist rappresentante la wishlist 
	 * con identificativo id dell'utente user, avente i prodotti products
	 * 
	 * @param user : l'utente
	 * @param id : l'identificativo della wishlist
	 * @param products : i prodotti desiderati dall'utente
	 * 
	 * @throws WishlistVuotaException : la wishlist deve avere attributo products non vuoto
	 * */
	
	public Wishlist(ProxyUtente user, int id, ArrayList<ProxyProdotto> products) throws WishlistVuotaException {
		this.utente = user;
		if(products.isEmpty())
			throw new WishlistVuotaException("La wishlist e\' vuota.");
		this.id = id;
		this.prodotti = products;
	}
	
	/**
	 * Questo metodo imposta il proprietario della wishlist.
	 * 
	 * @param user : l'utente possessore della lista dei desideri
	 * */
	
	public void setUtente(ProxyUtente user) {
		this.utente = user;
	}
	
	/**
	 * Questo metodo imposta il codice identificativo della wishlist.
	 * 
	 * @param id: il codice univoco della wishlist.
	 * */
	
	public void setId(int id) {
		this.id = id;
	}
	
		
	/**
	 * Questo metodo imposta la lista dei prodotti desiderati dall'utente.
	 * 
	 * @param products : i prodotti che l'utente desidera o vorrebbe acquistare in un secondo momento
	 * */
	
	public void setProdotti(Collection<ProxyProdotto> products) {
		this.prodotti = products;
	}
	
	/**
	 * Questo metodo fornisce il codice identificativo della wishlist.
	 * 
	 * @return id : identificativo della wishlist.
	 * */
	
	public int getId() {
		return id;
	}
	
	/**
	 * Questo metodo restituisce le caratteristiche peculiari del proprietario della wishlist.
	 * 
	 * @return utente : l'utente proprietario della wishlist
	 * */
	public ProxyUtente getUtente() {
		return utente;
	}
	
	/**
	 * Questo metodo restituisce i prodotti presenti nella lista dei desideri.
	 * 
	 * @return products : i prodotti della wishlist
	 * */
	public Collection<ProxyProdotto> getProdotti() {
		return prodotti;
	}
}
