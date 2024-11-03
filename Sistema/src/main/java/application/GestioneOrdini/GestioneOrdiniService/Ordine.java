package application.GestioneOrdini.GestioneOrdiniService;

import java.util.ArrayList;

import application.GestioneCarrello.GestioneCarrelloService.ItemCarrello;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;

/**
 * La classe rappresenta il concetto di ordine commissionato dal cliente presso il negozio online.
 * 
 * @see application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine
 * @see application.GestioneOrdini.GestioneOrdiniService.ProxyOrdine
 * @see application.Registrazione.RegistrazioneService.Cliente
 * @see application.GestioneCarrello.GestioneCarrelloService.ItemCarrello
 * 
 * @author Dorotea Serrelli
 * */

public class Ordine extends ObjectOrdine implements Cloneable{
	
	/**
	 * acquirente : è il committente dell'ordine.
	 * 
	 * */
	
	private Cliente acquirente;
	
	/**
	 * prodotti : i prodotti, e le relative quantità, acquistati
	 * dall'utente, costituenti l'ordine, richiesti
	 * */
	
	private ArrayList<ItemCarrello> prodotti;
	
	/**
	 * Costruttore della classe di default.
	 * */
	public Ordine() {
		super();
		this.acquirente = null;
		this.prodotti = null;
	}
	
	/**
	 * Costruttore della classe.
	 * Si costruisce un oggetto di tipo Ordine contenente i seguenti attributi : 
	 * codice, stato, indirizzoSpedizione, spedizione, acquirente, prodotti.
	 * 
	 * @param codice : l'identificativo numerico dell'ordine;
	 * @param stato : lo stato dell'ordine;
	 * @param indirizzoSpedizione : l'indirizzo di spedizione scelto dal committente;
	 * @param spedizione : la tipologia di spedizione scelta dall'utente.
	 * @param acquirente : il cliente che commissiona l'ordine
	 * @param prodotti : i prodotti acquistati dal cliente
	 * 
	 * @throws OrdineVuotoException : per gestire un ordine che non ha prodotti al suo interno
	 * */
	
	public Ordine(int codice, Stato stato, Indirizzo indirizzoSpedizione, TipoSpedizione spedizione, TipoConsegna consegna, Cliente acquirente, ArrayList<ItemCarrello> prodotti) throws OrdineVuotoException {
		super(codice, stato, indirizzoSpedizione, spedizione, consegna);
		this.acquirente = acquirente;
		if(prodotti == null || prodotti.size() == 0)
			throw new OrdineVuotoException("L'ordine non contiene prodotti!");
		this.prodotti = prodotti;
	}

	/**
	 * Il metodo fornisce le informazioni generali del committente.
	 * 
	 * @return acquirente : il profilo dell'utente richiedente l'ordine
	 * */
	
	public Cliente getAcquirente() {
		return acquirente;
	}
	
	/**
	 * Il metodo imposta le informazioni generali del committente.
	 * 
	 * @param client: il profilo del cliente richiedente l'ordine
	 * */
	
	public void setAcquirente(Cliente client) {
		this.acquirente = client;
	}
	
	/**
	 * Il metodo fornisce i prodotti presenti nell'ordine.
	 * 
	 * @return prodotti : i prodotti dell'ordine
	 * */
	
	public ArrayList<ItemCarrello> getProdotti() {
		return prodotti;
	}
	
	/**
	 * Il metodo imposta l'insieme di prodotti utili per 
	 * la creazione dell'ordine.
	 * 
	 * @param prodotti : i prodotti da inserire nell'ordine
	 * 
	 * @throws OrdineVuotoException : per gestire un ordine che non ha prodotti al suo interno
	 * */
	
	public void setProdotti(ArrayList<ItemCarrello> prodotti) throws OrdineVuotoException {
		if(prodotti == null || prodotti.size() == 0)
			throw new OrdineVuotoException("L'insieme dei prodotti fornito e\' vuoto.");
		this.prodotti = prodotti;
	}
	
	/**
	 * Il metodo restituisce una stringa contenente le informazioni 
	 * essenziali sui prodotti presenti nell'ordine.
	 * Tale metodo viene utilizzato per il metodo 
	 * @see application.GestioneOrdini.GestioneOrdiniService.Ordine.toString()
	 * al fine di stampare le referenze dell' ordine.
	 * 
	 * @return str : le informazioni dei prodotti acquistati
	 * */
	
	private String prodottiOrdiniString() {
		String str = "";
		for(ItemCarrello i : prodotti) 
			str = str.concat(i.toString().concat("\n"));
		
		return str;
	}
	
	/**
	 * Il metodo restituisce una stringa contenente le referenze sull'ordine
	 * del cliente e le informazioni 
	 * essenziali sui prodotti presenti nell'ordine, facendo uso del un metodo helper
	 * prodottiOrdiniString() nella classe Ordine.
	 * 
	 * @see application.GestioneOrdini.GestioneOrdiniService.Ordine
	 * 
	 * @return le informazioni sull'ordine e sui suoi prodotti 
	 * 			in formato stringa
	 * */
	
	@Override
	public String toString() {
		return super.toString() + ", cliente=" + acquirente.getNome() + " " + acquirente.getCognome() + 
				"Prodotti acquistati:\n" + this.prodottiOrdiniString() + "]";
	}
	
	/**
	 * Clona l'oggetto Ordine creando una copia profonda.
	 * 
	 * Questa implementazione garantisce che tutti gli attributi dell'oggetto,
	 * inclusi gli oggetti annidati come `acquirente` e gli elementi della lista `prodotti`,
	 * vengano copiati in modo indipendente, evitando così di condividere riferimenti
	 * con l'oggetto originale.
	 * 
	 * @return clone : una nuova istanza di `Ordine` che rappresenta una copia esatta dell'oggetto originale.
	 * @throws CloneNotSupportedException Se la classe o una delle classi degli attributi
	 *         non supporta l'operazione di clonazione.
	 */
	
	@Override
	public Ordine clone() throws CloneNotSupportedException{
	    Ordine clone = (Ordine) super.clone();
	    
		// Copia profonda degli attributi
		clone.acquirente = acquirente.clone(); 
		clone.prodotti = new ArrayList<>();
		
		for (ItemCarrello item : prodotti) {
		    clone.prodotti.add(item.clone()); 
		}
		return clone;
	}
	
	/**
	 * Confronta l'oggetto corrente con un altro oggetto per verificare
	 * se sono equivalenti. Due ordini sono considerati equivalenti se
	 * hanno lo stesso codice, lo stesso acquirente e lo stesso insieme
	 * di prodotti, indipendentemente dall'ordine in cui appaiono.
	 *
	 * @param obj l'oggetto da confrontare con l'oggetto corrente
	 * @return true se gli oggetti sono equivalenti, false altrimenti
	 */
	@Override
	public boolean equals(Object obj) {
	    // Verifica se l'oggetto confrontato è la stessa istanza
	    if (this == obj) {
	        return true;
	    }
	    
	    // Controlla se l'oggetto è un'istanza di Ordine
	    if (!(obj instanceof Ordine)) {
	        return false;
	    }
	    
	    // Cast dell'oggetto a Ordine
	    Ordine other = (Ordine) obj;
	    
	    // Confronto dei codici degli ordini
	    if (this.getCodiceOrdine() != other.getCodiceOrdine()) {
	        return false;
	    }
	    
	    // Confronto degli acquirenti
	    if (!this.acquirente.equals(other.acquirente)) {
	        return false;
	    }
	    
	    // Confronto della lista di prodotti
	    if (this.prodotti.size() != other.prodotti.size()) {
	        return false;
	    }
	    
	    // Verifica che tutti i prodotti siano equivalenti
	    for (ItemCarrello item : this.prodotti) {
	        if (!other.prodotti.contains(item)) {
	            return false;
	        }
	    }
	    
	    return true; // Gli ordini sono equivalenti
	}


}
