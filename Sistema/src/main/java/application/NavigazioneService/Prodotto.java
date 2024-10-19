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
	 * galleriaImmagini : le immagini che forniscono un particolare
	 * del prodotto in vendita
	 * */
	
	private ArrayList<byte[]> galleriaImmagini;
	
	
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
		
		super(codiceProdotto, nomeProdotto, topDescrizione, dettagli, prezzo,
			categoria, marca, modello, quantita,
			inCatalogo, inVetrina);
		
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
		
		super(codiceProdotto, nomeProdotto, topDescrizione, dettagli, prezzo,
				categoria, sottocategoria, marca, modello, quantita,
				inCatalogo, inVetrina);
		
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
