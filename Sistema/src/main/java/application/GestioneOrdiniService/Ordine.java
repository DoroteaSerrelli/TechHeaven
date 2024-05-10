package application.GestioneOrdiniService;

import java.util.ArrayList;

import application.GestioneCarrelloService.ItemCarrello;
import application.RegistrazioneService.Cliente;

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
	 * */
	public void setProdotti(ArrayList<ItemCarrello> prodotti) {
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
	
}
