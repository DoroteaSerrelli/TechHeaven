package application.NavigazioneService;

import java.util.ArrayList;

/**
 * La classe possiede le informazioni relative ad un prodotto venduto dal negozio.
 * 
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
	 * dettagli : testo dettagliato che elenca le specifiche del prodotto 
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
	 * 
	 * @param codice : il codice del prodotto
	 * @param nome : il nome del prodotto
	 * @param topDescrizione : la descrizione di presentazione del prodotto
	 * @param prezzo : il prezzo del prodotto
	 * @param marca : il produttore del prodotto
	 * @param modello : il modello del prodotto
	 * @param quantita : il numero di scorte in magazzino del prodotto
	 * @param dettagli : la descrizione dettagliata del prodotto
	 * 
	 * @return true se i dati sono stati inseriti con il formato corretto; false altrimenti.
	 * 
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
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria
	 * (con sottocategoria non nota), privo di
	 * immagine in evidenza e di galleria di immagini.
	 * Pertanto, si genera un oggetto della classe ObjectProdotto avente come attributi 
	 * codiceProdotto, nomeProdotto, topDescrizione, dettagli, prezzo, categoria, 
	 * marca, modello, quantita, inCatalogo e inVetrina.
	 * 
	 * @param codiceProdotto : il codice identificativo del prodotto
	 * @param nomeProdotto : il nominativo del prodotto
	 * @param topDescrizione : la descrizione di presentazione del prodotto
	 * @param dettagli : la descrizione dettagliata del prodotto
	 * @param prezzo : il costo del prodotto
	 * @param categoria : la categoria di appartenenza del prodotto
	 * @param marca : il produttore del prodotto
	 * @param modello : il modello del prodotto secondo la casa produttrice
	 * @param quantita : il numero di scorte disponibili in magazzino per quel prodotto
	 * @param inCatalogo : indica se il prodotto è presente nel catalogo
	 * @param inVetrina : indica se il prodotto deve essere presente in una vetrina virtuale del sito
	 * 
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
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria e sottocategoria, privo di
	 * immagine in evidenza e di galleria di immagini.
	 * Si crea un oggetto della classe ObjectProdotto avente come attributi codiceProdotto, nomeProdotto, 
	 * topDescrizione, dettagli, prezzo, categoria, sottocategoria, marca, modello, quantita, 
	 * inCatalogo e inVetrina.
	 * 
	 * @param codiceProdotto : il codice identificativo del prodotto
	 * @param nomeProdotto : il nominativo del prodotto
	 * @param topDescrizione : la descrizione di presentazione del prodotto
	 * @param dettagli : la descrizione dettagliata del prodotto
	 * @param prezzo : il costo del prodotto
	 * @param categoria : la categoria di appartenenza del prodotto
	 * @param sottocategoria : la sottocategoria del prodotto
	 * @param marca : il produttore del prodotto
	 * @param modello : il modello del prodotto secondo la casa produttrice
	 * @param quantita : il numero di scorte disponibili in magazzino per quel prodotto
	 * @param inCatalogo : indica se il prodotto è presente nel catalogo
	 * @param inVetrina : indica se il prodotto deve essere presente in una vetrina virtuale del sito
	 * 
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
	 * Il metodo fornisce l'immagine di presentazione del prodotto in vendita.
	 * 
	 * @return topImmagine : l'immagine in primo piano del prodotto
	 * */
	
	public byte[] getTopImmagine() {
		return topImmagine;
	}
	
	/**
	 * Il metodo imposta l'immagine di presentazione del prodotto in vendita.
	 * 
	 * @param topImmagine : sequenza di byte relativa all'immagine in primo piano del prodotto.
	 * */
	
	public void setTopImmagine(byte[] topImmagine) {
		this.topImmagine = topImmagine;
	}
	
	/**
	 * Il metodo fornisce la descrizione dettagliata del prodotto.
	 * 
	 * @return dettagli : specifica tecnica del prodotto
	 * */
	
	public String getDettagli() {
		return dettagli;
	}
	
	/**
	 * Il metodo imposta la specifica tecnica del prodotto.
	 * 
	 * @param dettagli: descrizione dettagliata del prodotto
	 * */
	
	public void setDettagli(String dettagli) {
		this.dettagli = dettagli;
	}
	
	/**
	 * Il metodo fornisce la galleria di immagini dettagliate associata al prodotto
	 * in vendita.
	 * 
	 * @return galleriaImmagini : lista di immagini dettagliate del prodotto
	 * */
	
	public ArrayList<byte[]> getGalleriaImmagini() {
		return galleriaImmagini;
	}

	/**
	 * Il metodo imposta la galleria di immagini dettagliate associata al prodotto
	 * in vendita.
	 * 
	 * @param galleriaImmagini : lista di immagini dettagliate del prodotto
	 * */
	
	public void setGalleriaImmagini(ArrayList<byte[]> galleriaImmagini) {
		this.galleriaImmagini = galleriaImmagini;
	}
}
