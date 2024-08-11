package application.GestioneOrdiniService;

import java.util.ArrayList;

import application.GestioneCarrelloService.ItemCarrello;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;

/**
 * La classe rappresenta il concetto di ordine commissionato dal cliente presso il negozio online.
 * 
 * @author Dorotea Serrelli
 * */

public class Ordine extends ObjectOrdine{
	
	/**
	 * acquirente : è il committente dell'ordine.
	 * 
	 * @see application.RegistrazioneService.Cliente
	 * */
	private Cliente acquirente;
	
	/**
	 * prodotti : i prodotti, e le relative quantità, acquistati
	 * dall'utente, costituenti l'ordine, richiesti
	 * 
	 * @see application.GestioneCarrelloService.ItemCarrello
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
	 * @param codice : l'identificativo numerico dell'ordine;
	 * @param stato : lo stato dell'ordine;
	 * @param indirizzoSpedizione : l'indirizzo di spedizione scelto dal committente;
	 * @param spedizione : la tipologia di spedizione scelta dall'utente.
	 * @param acquirente : il cliente che commissiona l'ordine
	 * @param prodotti : i prodotti acquistati dal cliente
	 * 
	 * @return un oggetto di tipo Ordine.
	 * @throws OrdineVuotoException per gestire un ordine che non ha prodotti al suo interno
	 * */
	
	
	public Ordine(int codice, Stato stato, Indirizzo indirizzoSpedizione, TipoSpedizione spedizione, Cliente acquirente, ArrayList<ItemCarrello> prodotti) throws OrdineVuotoException {
		super(codice, stato, indirizzoSpedizione, spedizione);
		this.acquirente = acquirente;
		if(prodotti == null || prodotti.size() == 0)
			throw new OrdineVuotoException("L'ordine non contiene prodotti!");
		this.prodotti = prodotti;
	}

	/**
	 * Il metodo fornisce le informazioni generali del committente
	 * @return il profilo dell'utente richiedente l'ordine
	 * */
	public Cliente getAcquirente() {
		return acquirente;
	}
	
	/**
	 * Il metodo imposta le informazioni generali del committente
	 * @param client: il profilo del cliente richiedente l'ordine
	 * */
	public void setAcquirente(Cliente client) {
		this.acquirente = client;
	}
	
	/**
	 * Il metodo fornisce i prodotti presenti nell'ordine
	 * 
	 * @return i prodotti dell'ordine
	 * */
	public ArrayList<ItemCarrello> getProdotti() {
		return prodotti;
	}
	
	/**
	 * Il metodo imposta l'insieme di prodotti utili per 
	 * la creazione dell'ordine
	 * 
	 * @param prodotti i prodotti da inserire nell'ordine
	 * @throws OrdineVuotoException 
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
	 * @see application.GestioneOrdiniService.Ordine.toString()
	 * al fine di stampare le referenze dell' ordine.
	 * 
	 * @return le informazioni dei prodotti acquistati
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
	 * @see application.GestioneOrdiniService.Ordine.prodottiOrdiniString().
	 * 
	 * @return le informazioni sull'ordine e sui suoi prodotti 
	 * in formato stringa
	 * */
	@Override
	public String toString() {
		return super.toString() + ", cliente=" + acquirente.getNome() + " " + acquirente.getCognome() + 
				"Prodotti acquistati:\n" + this.prodottiOrdiniString() + "]";
	}
	
	public Ordine clone() {
	    Ordine clone = (Ordine) super.clone();
	    
		// Copia profonda degli attributi
		clone.acquirente = acquirente.clone(); 
		clone.prodotti = new ArrayList<>();
		
		for (ItemCarrello item : prodotti) {
		    clone.prodotti.add(item.clone()); 
		}
		return clone;
	}

}
