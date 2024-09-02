package application.NavigazioneService;

import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

/**
 * Questa classe astratta è la classe client del design pattern Proxy
 * utilizzato per l'accesso e la gestione delle operazioni sull'oggetto Prodotto.
 * @see application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ProxyProdotto
 * 
 * @author Dorotea Serrelli
 * */

public abstract class ObjectProdotto {
	
	/**
	 * La classe Categoria consente di specificare le categorie
	 * dei prodotti adottate nel negozio.
	 * */
	public enum Categoria{
		TELEFONIA,
		PRODOTTI_ELETTRONICA,
		PICCOLI_ELETTRODOMESTICI,
		GRANDI_ELETTRODOMESTICI
	}
	
	/**
	 * La classe Sottocategoria consente di specificare le sottocategorie
	 * dei prodotti adottate nel negozio.
	 * */
	public enum Sottocategoria{
		TABLET,
		SMARTPHONE,
		PC,
		SMARTWATCH
	}
	
	/**
	 * codiceProdotto : identificativo numerico univoco di 
	 * un prodotto 
	 * */
	private int codiceProdotto;
	
	/**
	 * nomeProdotto : nome del prodotto
	 * */
	private String nomeProdotto;
	
	/**
	 * topDescrizione : testo di presentazione del prodotto, 
	 * contenente le informazioni peculiari, essenziali del prodotto,
	 * al fine di catturare l'attenzione dell'utente.
	 * */
	private String topDescrizione;
	
	/**
	 * prezzo : il costo del prodotto in Euro
	 * */
	private float prezzo;
	
	/**
	 * categoria : la categoria di appartenenza del prodotto.
	 * Il negozio suddivide, finora, i prodotti nelle seguenti categorie:
	 * Telefonia, Prodotti di elettronica (es. pc, ...), Piccoli elettrodomestici
	 * (es. forno a microonde, phon, aspirapolvere, ...) e Grandi elettrodomestici
	 * (es. frigorifero, lavatrice, ...).
	 * */
	private Categoria categoria;
	
	/**
	 * sottocategoria : il sottogruppo della categoria di appartenenza del prodotto
	 * Il negozio ha individuato per le categorie Telefonia e Prodotti di elettronica
	 * le seguenti sottocategorie:
	 * - Telefonia: Tablet, Smartphone;
	 * - Prodotti di elettronica: PC, Smartwatch;  
	 * */
	private Sottocategoria sottocategoria;
	
	/**
	 * marca : il brand o la casa produttrice del prodotto
	 * */
	private String marca;
	
	/**
	 * modello : una specifica versione o variante di un prodotto, definita per 
	 * identificare le caratteristiche distintive del prodotto, come le sue funzioni, le dimensioni, 
	 * il design e le prestazioni.
	 * Il modello viene definito dalla casa produttrice del prodotto.
	 * */
	private String modello;
	
	/**
	 * quantita: la quantità del prodotto disponibile in magazzino.
	 * */
	private int quantita;
	
	/**
	 * inCatalogo : flag utilizzato per marcare i prodotti presenti 
	 * nel catalogo del negozio.
	 * */
	private boolean inCatalogo;
	
	/**
	 * inVetrina : flag utilizzato per marcare i prodotti presenti 
	 * nel catalogo del negozio da mettere in evidenza per una categoria.
	 * */
	private boolean inVetrina;
	
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
	 * 
	 * @return true se i dati sono stati inseriti con il formato corretto; false altrimenti.
	 * */
	public static boolean checkValidate(int codice, String nome, String topDescrizione, float prezzo, 
			String marca, String modello, int quantita) {
		
		String descriptionPattern = "^[a-zA-Z0-9\s]{1,200}$";
		String modelPattern = "^[a-zA-Z0-9]";
		String numbersPattern = "^[0-9]$";
		return !(codice < 0 || nome.isBlank() || nome.matches(numbersPattern) || topDescrizione.matches(numbersPattern) 
				|| !topDescrizione.matches(descriptionPattern) || prezzo <= 0 
				|| marca.matches(numbersPattern) || !modello.matches(modelPattern)
				|| quantita <= 0);
	}
	
	/**
	 * Costruttore di classe di default.
	 * */
	protected ObjectProdotto() {
		this.codiceProdotto = 0;
		this.nomeProdotto = "";
		this.topDescrizione = "";
		this.prezzo = 0;
		this.categoria = null;
		this.sottocategoria = null;
		this.marca = "";
		this.modello = "";
		this.quantita = 0;
		this.inCatalogo = false;
		this.inVetrina = false;
	}
	
	/**
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria.
	 * */
	protected ObjectProdotto(int codiceProdotto, String nomeProdotto, String topDescrizione, float prezzo,
			Categoria categoria, String marca, String modello, int quantita,
			boolean inCatalogo, boolean inVetrina) {
		
		this.codiceProdotto = codiceProdotto;
		this.nomeProdotto = nomeProdotto;
		this.topDescrizione = topDescrizione;
		this.prezzo = prezzo;
		this.categoria = categoria;
		this.sottocategoria = null;
		this.marca = marca;
		this.modello = modello;
		this.quantita = quantita;
		this.inCatalogo = inCatalogo;
		this.inVetrina = inVetrina;
	}
	
	/**
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria, e sottocategoria.
	 * */
	protected ObjectProdotto(int codiceProdotto, String nomeProdotto, String topDescrizione, float prezzo,
			Categoria categoria, Sottocategoria sottocategoria, String marca, String modello, int quantita,
			boolean inCatalogo, boolean inVetrina) {
		this.codiceProdotto = codiceProdotto;
		this.nomeProdotto = nomeProdotto;
		this.topDescrizione = topDescrizione;
		this.prezzo = prezzo;
		this.categoria = categoria;
		this.sottocategoria = sottocategoria;
		this.marca = marca;
		this.modello = modello;
		this.quantita = quantita;
		this.inCatalogo = inCatalogo;
		this.inVetrina = inVetrina;
	}
	
	/**
	 * Restituisce il codice del prodotto.
	 * @return identificativo numerico del prodotto
	 * */
	public int getCodiceProdotto() {
		return codiceProdotto;
	}
	
	/**
	 * Imposta il codice del prodotto.
	 * @param codiceProdotto : il codice del prodotto da inserire
	 * */
	public void setCodiceProdotto(int codiceProdotto) {
		this.codiceProdotto = codiceProdotto;
	}
	
	/**
	 * Restituisce il nome del prodotto.
	 * @return nominativo del prodotto
	 * */
	public String getNomeProdotto() {
		return nomeProdotto;
	}
	
	/**
	 * Imposta il nominativo del prodotto.
	 * @param nomeProdotto : il nome del prodotto da inserire
	 * */
	public void setNomeProdotto(String nomeProdotto) {
		this.nomeProdotto = nomeProdotto;
	}
	
	/**
	 * Fornisce la descrizione di presentazione del prodotto.
	 * @return descrizione in primo piano del prodotto
	 * */
	public String getTopDescrizione() {
		return topDescrizione;
	}
	
	/**
	 * Imposta la descrizione di presentazione del prodotto.
	 * @param topDescrizione: descrizione in primo piano del prodotto
	 * */
	public void setTopDescrizione(String topDescrizione) {
		this.topDescrizione = topDescrizione;
	}
	
	/**
	 * Fornisce il prezzo del prodotto.
	 * @return il costo del prodotto
	 * */
	public float getPrezzo() {
		return prezzo;
	}
	
	/**
	 * Imposta il prezzo del prodotto.
	 * @param prezzo il costo del prodotto
	 * */
	public void setPrezzo(float prezzo) {
		this.prezzo = prezzo;
	}
	
	/**
	 * Fornisce la categoria di appartenenza del prodotto.
	 * @return la categoria del prodotto
	 * */
	public Categoria getCategoria() {
		return categoria;
	}
	
	/**
	 * Imposta la categoria di appartenenza del prodotto
	 * @param categoria la categoria del prodotto
	 * */
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	/**
	 * Fornisce la categoria di appartenenza del prodotto sottoforma di stringa.
	 * @return la categoria del prodotto
	 * */
	public String getCategoriaAsString() {
		return categoria.toString();
	}
	
	/**
	 * Imposta la categoria di appartenenza del prodotto
	 * @param categoria la categoria del prodotto espressa come stringa
	 * @throws CategoriaProdottoException 
	 * */
	public void setCategoria(String category) throws CategoriaProdottoException {
	    switch (category.toUpperCase()) {
	        case "TELEFONIA":
	            this.categoria = Categoria.TELEFONIA;
	            break;
	        case "PRODOTTI_ELETTRONICA", "PRODOTTI ELETTRONICA" :
	            this.categoria = Categoria.PRODOTTI_ELETTRONICA;
	            break;
	        case "PICCOLI_ELETTRODOMESTICI", "PICCOLI ELETTRODOMESTICI":
	            this.categoria = Categoria.PICCOLI_ELETTRODOMESTICI;
	            break;
	        case "GRANDI_ELETTRODOMESTICI", "GRANDI ELETTRODOMESTICI":
	            this.categoria = Categoria.GRANDI_ELETTRODOMESTICI;
	            break;
	        default:
	            throw new CategoriaProdottoException("Le categorie ammissibili sono TELEFONIA, PRODOTTI ELETTRONICA, GRANDI ELETTRODOMESTICI, PICCOLI ELETTRODOMESTICI");
	    }
	}
	
	/**
	 * Fornisce la sottocategoria di appartenenza del prodotto.
	 * @return la categoria del prodotto
	 * */
	public Sottocategoria getSottocategoria() {
		return sottocategoria;
	}
	
	/**
	 * Fornisce la sottocategoria di appartenenza del prodotto sottoforma di stringa.
	 * @return la sottocategoria del prodotto
	 * */
	public String getSottocategoriaAsString() {
		if(this.sottocategoria == null)
			return "";
		return sottocategoria.toString();
	}
	
	/**
	 * Imposta la sottocategoria di appartenenza del prodotto
	 * @param subCategory la sottocategoria del prodotto espressa come stringa
	 * @throws SottocategoriaProdottoException 
	 * */
	
	public void setSottocategoria(String subcategory) throws SottocategoriaProdottoException {
		if(subcategory == null) {
			this.sottocategoria = null;
			return; 
		}
	    switch (subcategory.toUpperCase()) {
	        case "TABLET":
	        	this.sottocategoria = Sottocategoria.TABLET;
	            break;
	        case "SMARTPHONE":
	        	this.sottocategoria = Sottocategoria.SMARTPHONE;
	            break;
	        case "PC":
	            this.sottocategoria = Sottocategoria.PC;
	            break;
	        case "SMARTWATCH":
	            this.sottocategoria = Sottocategoria.SMARTWATCH;
	            break;
	        case "":
	        	this.sottocategoria = null;
	        	break;
	        default:
	        	throw new SottocategoriaProdottoException("Se specificata, le sottocategorie ammissibili per un prodotto sono:"
		        		+ "\n- TABLET e SMARTPHONE per la categoria TELEFONIA;"
		        		+ "\n- PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.");
	    }
	}
	
	/**
	 * Fornisce la casa produttrice del prodotto.
	 * @return marca del prodotto
	 * */
	public String getMarca() {
		return marca;
	}
	
	/**
	 * Imposta la marca del prodotto.
	 * @param marca l'azienda produttrice del prodotto
	 * */
	public void setMarca(String marca) {
		this.marca = marca;
	}
	
	/**
	 * Fornisce il modello del prodotto.
	 * @return modello del prodotto
	 * */
	public String getModello() {
		return modello;
	}
	
	/**
	 * Imposta il modello del prodotto.
	 * @param modello la versione o variante del prodotto definita dall'azienda produttrice
	 * */
	public void setModello(String modello) {
		this.modello = modello;
	}
	
	/**
	 * Fornisce le quantità del prodotto disponibili in magazzino.
	 * @return scorte nel magazzino del prodotto
	 * */
	public int getQuantita() {
		return quantita;
	}
	
	/**
	 * Imposta la quantità del prodotto in magazzino.
	 * @param quantita il numero di scorte del prodotto in magazzino
	 * */
	
	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}
	
	/**
	 * Il metodo permette di comprendere se un dato prodotto è presente nel catalogo.
	 * @return true se è presente il prodotto nel catalogo; false altrimenti.
	 * */
	public boolean isInCatalogo() {
		return inCatalogo;
	}
	
	/**
	 * Il metodo permette di comprendere se un dato prodotto è presente nel catalogo.
	 * @return 1 se è presente il prodotto nel catalogo; 0 altrimenti.
	 * */
	public int isInCatalogoInt() {
		return inCatalogo? 1 : 0;
	}
	
	/**
	 * Imposta la disponibilità del prodotto nel catalogo del negozio online.
	 * @param inCatalogo : true se il prodotto è presente nel catalogo, false altrimenti.
	 * */
	public void setInCatalogo(boolean inCatalogo) {
		this.inCatalogo = inCatalogo;
	}
	
	/**
	 * Il metodo permette di comprendere se un dato prodotto è presente nella vetrina del negozio
	 * come rappresentante dei prodotti della propria categoria di appartenenza.
	 * @return true se è presente il rappresentante della categoria di appartenenza; false altrimenti.
	 * */
	public boolean isInVetrina() {
		return inVetrina;
	}
	
	/**
	 * Il metodo permette di comprendere se un dato prodotto è presente nella vetrina del negozio
	 * come rappresentante dei prodotti della propria categoria di appartenenza.
	 * @return 1 se è presente il rappresentante della categoria di appartenenza; 0 altrimenti.
	 * */
	public int isInVetrinaInt() {
		return inVetrina? 1 : 0;
	}
	
	/**
	 * Imposta il prodotto come rappresentante della categoria di appartenenza e, pertanto,
	 * sarà nella vetrina del negozio online per l'esposizione dei prodotti della suddetta categoria.
	 * @return true se è il rappresentante della categoria di appartenenza; false altrimenti.
	 * */
	public void setInVetrina(boolean inVetrina) {
		this.inVetrina = inVetrina;
	}

	/**
	 * Il metodo fornisce in formato stringa le caratteristiche associate al prodotto.
	 * */
	@Override
	public String toString() {
		return "Prodotto [CodiceProdotto=" + codiceProdotto + ", NomeProdotto=" + nomeProdotto + ", Prezzo=" + prezzo
				+ ", Categoria=" + categoria + ", Marca=" + marca + ", Modello = "+ modello + "]";
	}
	
	public String ordertoString() {
		return "Prodotto [CodiceProdotto=" + codiceProdotto + ", NomeProdotto=" + nomeProdotto + ", "
				+ "Categoria=" + categoria + ", Marca=" + marca + ", Modello = " + modello + "]";
	}
}
