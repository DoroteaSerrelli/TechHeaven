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
	 * L'identificativo della wishlist dell'utente proprietario.
	 * Per la realizzazione corrente del sistema software, l'utente può creare
	 * una sola wishlist.
	 * Per implementazioni future si può pensare di creare più wishlist per lo stesso
	 * utente.
	 * 
	 * */
	private int id;

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
	 * Costruttore di classe di default per una wishlist vuota
	 * @param user l'utente 
	 * @param id : identificativo della wishlist
	 * */
	public Wishlist(ProxyUtente user, int id) {
		this.utente = user;
		this.id = id;
		this.prodotti = new ArrayList<>();
	}
	
	/**
	 * Costruttore di classe
	 * @param user l'utente
	 * @param id : l'identificativo della wishlist
	 * @param products i prodotti desiderati dall'utente (non è possibile creare una wishlist vuota)
	 * @throws WishlistVuotaException 
	 * */
	public Wishlist(ProxyUtente user, int id, ArrayList<ProxyProdotto> products) throws WishlistVuotaException {
		this.utente = user;
		if(products.isEmpty())
			throw new WishlistVuotaException("La wishlist e\' vuota.");
		this.id = id;
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
	 * Questo metodo imposta il codice identificativo della wishlist
	 * @param id: il codice univoco della wishlist.
	 * */
	public void setId(int id) {
		this.id = id;
	}
	
		
	/**
	 * Questo metodo imposta la lista dei prodotti desiderati dall'utente
	 * @param products i prodotti che l'utente desidera o vorrebbe acquistare in un secondo momento
	 * */
	public void setProdotti(ArrayList<ProxyProdotto> products) {
		this.prodotti = products;
	}
	
	/**
	 * Questo metodo fornisce il codice identificativo della wishlist
	 * @return id della wishlist.
	 * */
	public int getId() {
		return id;
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
