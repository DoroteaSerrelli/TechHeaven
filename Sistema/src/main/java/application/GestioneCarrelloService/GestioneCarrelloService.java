package application.GestioneCarrelloService;

import java.util.Collection;

import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneCarrelloService.CarrelloException.QuantitaProdottoException;

/**
 * Interfaccia che si occupa di offrire servizi relativi alla
 * gestione del carrello dell'utente: visualizzazione del carrello, 
 * aggiunta di un prodotto nel carrello, rimozione di un prodotto dal carrello,
 * aumento delle quantità di un prodotto, diminuzione delle quantità di un prodotto
 * nel carrello.
 * 
 * @author Dorotea Serrelli
 * */

public interface GestioneCarrelloService {
	
	/**
	 * Questo metodo si occupa di fornire l'elenco dei prodotti
	 * presenti nel carrello.
	 * @param cart : il carrello dell'utente
	 * @return l'insieme dei prodotti nel carrello
	 * */
	
	public Collection<ItemCarrello> visualizzaCarrello(Carrello cart);
	
	/**
	 * Questo metodo si occupa di aggiungere un prodotto
	 * nel carrello.
	 * @param cart : il carrello dell'utente
	 * @param item : il prodotto da aggiungere (di quantità unitaria)
	 * @return il carrello contenente il nuovo prodotto
	 * */
	
	public Carrello aggiungiAlCarrello(Carrello cart, ItemCarrello item) throws ProdottoPresenteException, ProdottoNulloException;
	
	/**
	 * Questo metodo si occupa di rimuovere un prodotto
	 * nel carrello.
	 * @param cart : il carrello dell'utente
	 * @param item : il prodotto da rimuovere
	 * @return il carrello privo del prodotto rimosso
	 * */
	
	public Carrello rimuoviDalCarrello(Carrello cart, ItemCarrello item) throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException;
	
	/**
	 * Questo metodo si occupa di aumentare la quantità di un prodotto
	 * selezionato nel carrello.
	 * @param cart : il carrello dell'utente
	 * @param item : il prodotto
	 * @param quantity : la quantità del prodotto da impostare
	 * @return il carrello contenente la quantità del prodotto scelto aggiornata
	 * */
	
	public Carrello aumentaQuantitaNelCarrello(Carrello cart, ItemCarrello item, int quantity) throws ProdottoNulloException, CarrelloVuotoException, ProdottoNonPresenteException, QuantitaProdottoException;
	
	/**
	 * Questo metodo si occupa di decrementare la quantità di un prodotto
	 * selezionato nel carrello.
	 * @param cart : il carrello dell'utente
	 * @param item : il prodotto
	 * @param quantity : la quantità del prodotto da impostare
	 * @return il carrello contenente la quantità del prodotto scelto aggiornata
	 * */
	
	public Carrello decrementaQuantitaNelCarrello(Carrello cart, ItemCarrello item, int quantity) throws ProdottoNulloException, CarrelloVuotoException, ProdottoNonPresenteException, QuantitaProdottoException;
	
}
