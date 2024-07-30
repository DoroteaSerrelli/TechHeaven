package application.GestioneWishlistService;

import java.util.ArrayList;

import application.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.NavigazioneService.ProxyProdotto;
import application.RegistrazioneService.ProxyUtente;

/**
 * Classe che esprime il concetto 'wishlist': si occupa di operazioni volte 
 * a manipolare la lista dei desideri ed i suoi prodotti.
 * 
 * @see java.application.GestioneWishlistService.GestioneWishlistService
 * @see java.application.GestioneWishlistService.GestioneWishlistServiceImpl
 * @see java.application.GestioneWishlistService.WishlistException
 * 
 * @author Dorotea Serrelli
 * */

public class Wishlist {
	
	/**
	 * L'utente proprietario della lista dei desideri
	 * @see application.RegistrazioneService.ProxyUtente
	 * */
	private ProxyUtente utente;
	
	/**
	 * I prodotti che l'utente vorrebbe acquistare in un
	 * secondo momento.
	 * @see application.NavigazioneService.ProxyProdotto
	 * */
	private ArrayList<ProxyProdotto> prodotti;
	
	/**
	 * Costruttore di classe di default per una wishlist vuota
	 * @param user l'utente 
	 * */
	public Wishlist(ProxyUtente user) {
		this.utente = user;
		this.prodotti = new ArrayList<>();
	}
	
	/**
	 * Costruttore di classe
	 * @param user l'utente
	 * @param products i prodotti desiderati dall'utente (non è possibile creare una wishlist vuota)
	 * @throws WishlistVuotaException 
	 * */
	public Wishlist(ProxyUtente user, ArrayList<ProxyProdotto> products) throws WishlistVuotaException {
		this.utente = user;
		if(products.isEmpty())
			throw new WishlistVuotaException("La wishlist e\' vuota.");
		this.prodotti = products;
	}
	
	/**
	 * Questo metodo imposta il proprietario della wishlist
	 * @param user l'utente possessore della lista dei desideri
	 * */
	public void setUtente(ProxyUtente user) {
		this.utente = user;
	}
	
	/**
	 * Questo metodo imposta la lista dei prodotti desiderati dall'utente
	 * @param products i prodotti che l'utente desidera o vorrebbe acquistare in un secondo momento
	 * */
	public void setProdotti(ArrayList<ProxyProdotto> products) {
		this.prodotti = products;
	}
	
	/**
	 * Questo metodo restituisce le caratteristiche peculiari del proprietario della wishlist
	 * @return l'utente proprietario della wishlist
	 * */
	public ProxyUtente getUtente() {
		return utente;
	}
	
	/**
	 * Questo metodo restituisce i prodotti presenti nella lista dei desideri
	 * @return products i prodotti della wishlist
	 * */
	public ArrayList<ProxyProdotto> getProdotti() {
		return prodotti;
	}
}
