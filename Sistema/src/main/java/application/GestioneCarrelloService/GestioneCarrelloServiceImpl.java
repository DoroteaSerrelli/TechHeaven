package application.GestioneCarrelloService;

import java.util.Collection;
import java.util.ArrayList;

import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneCarrelloService.CarrelloException.QuantitaProdottoException;

/**
 * Questa classe fornisce un'implementazione concreta dei servizi per la gestione del carrello.
 * 
 * @see application.GestioneCarrelloService.GestioneCarrelloService
 * @see	application.GestioneCarrelloService.Carrello
 * @see application.GestioneCarrelloService.ItemCarrello
 * @see application.GestioneCarrelloService.CarrelloException
 * 
 * @author Dorotea Serrelli 
 * */

public class GestioneCarrelloServiceImpl implements GestioneCarrelloService{
	
	/**
	 * Il metodo fornisce i prodotti presenti nel carrello.
	 * 
	 * @param cart : il carrello virtuale dell'utente
	 * 
	 * @return collezione di prodotti presenti nel carrello cart
	 * */
	
	@Override
	public Collection<ItemCarrello> visualizzaCarrello(Carrello cart) {
		return cart.getProducts();
	}
	
	/**
	 * Il metodo aggiunge un prodotto nel carrello.
	 * 
	 * @param cart : il carrello virtuale dell'utente
	 * @param item : il prodotto da aggiungere (di quantità unitaria)
	 * 
	 * @return il carrello contenente il nuovo prodotto item
	 * */
	
	@Override
	public Carrello aggiungiAlCarrello(Carrello cart, ItemCarrello item) throws ProdottoPresenteException, ProdottoNulloException {
		cart.addProduct(item);
		return cart;
	}
	
	/**
	 * Il metodo rimuove un prodotto nel carrello.
	 * 
	 * @param cart : il carrello virtuale dell'utente
	 * @param item : il prodotto da rimuovere
	 * 
	 * @return il carrello privo del prodotto item
	 * */
	
	@Override
	public Carrello rimuoviDalCarrello(Carrello cart, ItemCarrello item) throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException {
		cart.deleteProduct(item);
		return cart;
	}
	
	/**
	 * Il metodo incrementa il numero di pezzi di un prodotto del carrello.
	 * 
	 * @param cart : il carrello del cliente
	 * @param item : il prodotto nel carrello
	 * @param quantity : la quantità del prodotto nel carrello da impostare
	 * 						(quantity deve essere maggiore della quantità corrente di item nel carrello)
	 * 
	 * @return il carrello contenente il prodotto item con la quantità quantity
	 * */
	
	@Override
	public Carrello aumentaQuantitaNelCarrello(Carrello cart, ItemCarrello item, int quantity) throws ProdottoNulloException, CarrelloVuotoException, ProdottoNonPresenteException, QuantitaProdottoException {
		if(item.getQuantita() >= quantity)
			throw new QuantitaProdottoException("La quantita\' specificata è minore o uguale rispetto alla quantita\' del prodotto " + item.getNomeProdotto() + " nel carrello.");
		else
			cart.updateProductQuantity(item, quantity);

		return cart;
	}
	
	/**
	 * Il metodo decrementa il numero di pezzi di un prodotto del carrello.
	 * 
	 * @param cart : il carrello del cliente
	 * @param item : il prodotto nel carrello
	 * @param quantity : la quantità del prodotto nel carrello da impostare
	 * 					(quantity deve essere minore della quantità corrente di item nel carrello)
	 * 
	 * @return il carrello contenente il prodotto item con la quantità quantity
	 * */
	
	@Override
	public Carrello decrementaQuantitaNelCarrello(Carrello cart, ItemCarrello item, int quantity) throws ProdottoNulloException, CarrelloVuotoException, ProdottoNonPresenteException, QuantitaProdottoException {
		if(item.getQuantita() <= quantity)
			throw new QuantitaProdottoException("La quantita\' specificata è maggiore o uguale rispetto alla quantita\' del prodotto " + item.getNomeProdotto() + " nel carrello.");
		else
			cart.updateProductQuantity(item, quantity);
		return cart;
	}
	
	/**
	 * Questo metodo implementa il servizio di svuotamento
	 * del carrello.
	 * 
	 * @param cart : il carrello da svuotare
	 * 
	 * @return il carrello vuoto
	 * 
	 * @throws ProdottoNulloException : 
	 * 					@see application.GestioneCarrelloService.CarrelloException.ProdottoNulloException
	 * @throws CarrelloVuotoException :
	 * 					@see application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException
	 * @throws ProdottoNonPresenteException :
	 * 					@see application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException
	 * **/
	
	@Override
	public Carrello svuotaCarrello(Carrello cart) throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException {
		if(cart == null || cart.getNumProdotti() == 0)
			return cart;
		ArrayList<ItemCarrello> products = (ArrayList<ItemCarrello>) cart.getProducts();
		for(ItemCarrello i : products)
			cart.deleteProduct(i);
		return cart;
	}
}