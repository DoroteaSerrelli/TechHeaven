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
	 * */
	@Override
	public int getQuantita() {
		return quantitàAcquisto;
	}
	
	/**
	 * Il metodo imposta il numero di pezzi del prodotto scelto.
	 * */
	@Override
	public void setQuantita(int quantity) {
		this.quantitàAcquisto = quantity;
	}
}
