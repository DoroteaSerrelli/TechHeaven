package application.GestioneCarrelloService;
import application.NavigazioneService.*;

/**
 * La classe rappresenta il concetto di prodotto presente nel carrello, con una certa quantità.
 * 
 * @see application.NavigazioneService.Prodotto;
 * 
 * @author Dorotea Serrelli
 * */
public class ItemCarrello extends Prodotto{
	
	/**
	 * Il numero di pezzi di un prodotto da acquistare.
	 * */
	
	private int quantitàAcquisto = 0;

	/**
	 * Costruttore di classe di default.
	 * Si crea un oggetto ItemCarrello con quantità di acquisto pari a 1.
	 * */
	public ItemCarrello() {
		super();
		quantitàAcquisto = 1;
	}
	
	/**
	 * Il metodo fornisce il numero di pezzi del prodotto scelto.
	 * @return la quantità di un prodotto
	 * */
	@Override
	public int getQuantita() {
		return quantitàAcquisto;
	}
	
	/**
	 * Il metodo imposta il numero di pezzi del prodotto scelto.
	 * @param quantity : quantità di un prodotto
	 * */
	@Override
	public void setQuantita(int quantity) {
		this.quantitàAcquisto = quantity;
	}
	
	/**
	 * Il metodo crea una copia dell'oggetto ItemCarrello.
	 *
	 * @return Una copia dell'oggetto ItemCarrello.
	 * @throws RuntimeException se si verifica un errore durante la clonazione.
	 */
	
	public ItemCarrello clone() {
	    ItemCarrello clone = null;
	    try {
	        clone = (ItemCarrello) super.clone();
	    } catch (CloneNotSupportedException e) {
	        throw new RuntimeException("Clonazione non supportata per ItemCarrello", e);
	    }
	    return clone;
	}
}
