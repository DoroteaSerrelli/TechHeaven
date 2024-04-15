package application.NavigazioneService;

import java.util.ArrayList;

/**
 * La classe possiede le informazioni relative ad un prodotto venduto dal negozio.
 * @see application.NavigazioneService.ObjectProdotto
 * @see application.NavigazioneService.ProxyProdotto
 * 
 * @author Dorotea Serrelli
 * */

public class Prodotto extends ObjectProdotto{

	/**
	 * topImmagine : contiene l'immagine del prodotto da porre
	 * in primo piano.
	 * */
	
	private byte[] topImmagine;
	
	/**
	 * dettagli : testo che elenca le specifiche del prodotto 
	 * (es. componenti, finalità del prodotto, corretto utilizzo, ...)
	 * */
	private String dettagli;
	
	/**
	 * galleriaImmagini : le immagini che forniscono un particolare
	 * del prodotto in vendita
	 * */
	private ArrayList<byte[]> galleriaImmagini;
	
	/**
	 * Il metodo verifica se i dati inseriti, relativi ad un prodotto, rispettano
	 * il relativo formato.
	 * @param codice il codice del prodotto
	 * @param nome il nome del prodotto
	 * @param topDescrizione la descrizione di presentazione del prodotto
	 * @param prezzo il prezzo del prodotto
	 * @param marca il produttore del prodotto
	 * @param modello il modello del prodotto
	 * @param quantita il numero di scorte in magazzino del prodotto
	 * @param dettagli la descrizione dettagliata del prodotto
	 * 
	 * @return true se i dati sono stati inseriti con il formato corretto; false altrimenti.
	 * */
	public static boolean checkValidateDetails(int codice, String nome, String topDescrizione, float prezzo, 
			String marca, String modello, int quantita, String dettagli) {
		checkValidate(codice, nome, topDescrizione, prezzo, marca, modello, quantita);
		String descriptionPattern = "^[a-zA-Z0-9\s]{1,200}$";
		String numbersPattern = "^[0-9]$";
		return !(dettagli.isBlank() || dettagli.matches(numbersPattern) 
				|| !dettagli.matches(descriptionPattern));
	}
	
	/**
	 * Costruttore di classe di default.
	 * */
	public Prodotto() {
		super();
	}
	
	/**
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria privo di
	 * immagine in evidenza e di galleria di immagini.
	 * */
	public Prodotto(int codiceProdotto, String nomeProdotto, String topDescrizione, String dettagli, float prezzo,
			Categoria categoria, String marca, String modello, int quantita,
			boolean inCatalogo, boolean inVetrina) {
		
		super(codiceProdotto, nomeProdotto, topDescrizione, prezzo,
			categoria, marca, modello, quantita,
			inCatalogo, inVetrina);
		this.dettagli = dettagli;
		this.topImmagine = null;
		this.galleriaImmagini = null;
	}
	
	/**
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria, e sottocategoria, privo di
	 * immagine in evidenza e di galleria di immagini.
	 * */
	public Prodotto(int codiceProdotto, String nomeProdotto, String topDescrizione, String dettagli, float prezzo,
			Categoria categoria, Sottocategoria sottocategoria, String marca, String modello, int quantita,
			boolean inCatalogo, boolean inVetrina) {
		
		super(codiceProdotto, nomeProdotto, topDescrizione, prezzo,
				categoria, sottocategoria, marca, modello, quantita,
				inCatalogo, inVetrina);
		this.dettagli = dettagli;
		this.topImmagine = null;
		this.galleriaImmagini = null;
	}
	
	/**
	 * Fornisce l'immagine di presentazione del prodotto in vendita.
	 * @return l'immagine in primo piano del prodotto
	 * */
	public byte[] getTopImmagine() {
		return topImmagine;
	}
	
	/**
	 * Imposta l'immagine di presentazione del prodotto in vendita.
	 * @param topImmagine : sequenza di byte relativa all'immagine in primo piano del prodotto.
	 * */
	public void setTopImmagine(byte[] topImmagine) {
		this.topImmagine = topImmagine;
	}
	
	/**
	 * Fornisce la descrizione dettagliata del prodotto.
	 * @return specifica tecnica del prodotto
	 * */
	public String getDettagli() {
		return dettagli;
	}
	
	/**
	 * Imposta la specifica tecnica del prodotto.
	 * @param dettagli: descrizione dettagliata del prodotto
	 * */
	public void setDettagli(String dettagli) {
		this.dettagli = dettagli;
	}
	
	/**
	 * Fornisce la galleria di immagini dettagliate associata al prodotto
	 * in vendita.
	 * @return immagini dettagliate del prodotto
	 * */
	public ArrayList<byte[]> getGalleriaImmagini() {
		return galleriaImmagini;
	}

	/**
	 * Imposta la galleria di immagini dettagliate associata al prodotto
	 * in vendita.
	 * @param galleriaImmagini
	 * */
	public void setGalleriaImmagini(ArrayList<byte[]> galleriaImmagini) {
		this.galleriaImmagini = galleriaImmagini;
	}
}
