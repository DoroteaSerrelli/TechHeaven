package application.GestioneCarrello.GestioneCarrelloService;

import java.sql.SQLException;
import java.util.Collection;

import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.QuantitaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

/**
 * Interfaccia che si occupa di offrire servizi relativi alla
 * gestione del carrello dell'utente: visualizzazione del carrello, 
 * aggiunta di un prodotto nel carrello, rimozione di un prodotto dal carrello,
 * aumento delle quantità di un prodotto, diminuzione delle quantità di un prodotto
 * nel carrello, svuotamento del carrello.
 * 
 * @see application.GestioneCarrello.GestioneCarrelloService.GestioneCarrelloServiceImpl
 * @see application.GestioneCarrello.GestioneCarrelloService.Carrello
 * @see application.GestioneCarrello.GestioneCarrelloService.ItemCarrello
 * @see application.GestioneCarrello.GestioneCarrelloService.CarrelloException
 * 
 * @author Dorotea Serrelli
 * */

public interface GestioneCarrelloService {
	
	/**
	 * Questo metodo si occupa di fornire l'elenco dei prodotti
	 * presenti nel carrello.
	 * 
	 * @param cart : il carrello virtuale dell'utente
	 * 
	 * @return collezione di prodotti presenti nel carrello cart
	 * */
	
	public Collection<ItemCarrello> visualizzaCarrello(Carrello cart);
	
	/**
	 * Questo metodo si occupa di aggiungere un prodotto
	 * nel carrello.
	 * 
	 * @param cart : il carrello virtuale dell'utente
	 * @param item : il prodotto da aggiungere (di quantità unitaria)
	 * 
	 * @return il carrello contenente il nuovo prodotto item
	 * @throws SQLException : eccezione gestista in caso di problemi di accesso/recupero dati dal database
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws QuantitaProdottoException : eccezione gestita nel caso in cui l'utente inserisce un prodotto 
	 * 										avente numero di scorte pari a 0
	 * */
	
	public Carrello aggiungiAlCarrello(Carrello cart, ItemCarrello item) throws ProdottoPresenteException, ProdottoNulloException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, QuantitaProdottoException;
	
	/**
	 * Questo metodo si occupa di rimuovere un prodotto
	 * dal carrello.
	 * 
	 * @param cart : il carrello virtuale dell'utente
	 * @param item : il prodotto da rimuovere
	 * 
	 * @return il carrello privo del prodotto item
	 * */
	
	public Carrello rimuoviDalCarrello(Carrello cart, ItemCarrello item) throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException;
	
	/**
	 * Questo metodo si occupa di aumentare la quantità di un prodotto
	 * selezionato nel carrello.
	 * 
	 * @param cart : il carrello virtuale dell'utente
	 * @param item : il prodotto per il quale aumentare la quantità
	 * @param quantity : la quantità del prodotto da impostare
	 * 
	 * @return il carrello contenente la quantità del prodotto item aggiornata a quantity
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * */
	
	public Carrello aumentaQuantitaNelCarrello(Carrello cart, ItemCarrello item, int quantity) throws ProdottoNulloException, CarrelloVuotoException, ProdottoNonPresenteException, QuantitaProdottoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException;
	
	/**
	 * Questo metodo si occupa di decrementare la quantità di un prodotto
	 * selezionato nel carrello.
	 * 
	 * @param cart : il carrello virtuale dell'utente
	 * @param item : il prodotto per il quale aumentare la quantità
	 * @param quantity : la quantità del prodotto da impostare
	 * 
	 * @return il carrello contenente la quantità del prodotto item aggiornata a quantity
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * */
	
	public Carrello decrementaQuantitaNelCarrello(Carrello cart, ItemCarrello item, int quantity) throws ProdottoNulloException, CarrelloVuotoException, ProdottoNonPresenteException, QuantitaProdottoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException;
	
	/**
	 * Questo metodo fornisce il servizio di svuotamento
	 * del carrello.
	 * 
	 * @param cart : il carrello da svuotare
	 * 
	 * @return il carrello vuoto
	 * 
	 * @throws ProdottoNulloException : 
	 * 					@see application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNulloException
	 * @throws CarrelloVuotoException :
	 * 					@see application.GestioneCarrello.GestioneCarrelloService.CarrelloException.CarrelloVuotoException
	 * @throws ProdottoNonPresenteException :
	 * 					@see application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException
	 * **/
	
	public Carrello svuotaCarrello(Carrello cart) throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException;
}
