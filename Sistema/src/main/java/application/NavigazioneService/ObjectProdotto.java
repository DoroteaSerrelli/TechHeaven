package application.NavigazioneService;

import java.util.Objects;

import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

/**
 * Questa classe astratta è la classe client del design pattern Proxy
 * utilizzato per l'accesso e la gestione delle operazioni sull'oggetto Prodotto.
 * 
 * @see application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ProxyProdotto
 * 
 * @author Dorotea Serrelli
 * 
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
	 * 
	 * @param codice: il codice del prodotto
	 * @param nome: il nome del prodotto
	 * @param topDescrizione: la descrizione di presentazione del prodotto
	 * @param prezzo: il prezzo del prodotto
	 * @param marca: il produttore del prodotto
	 * @param modello: il modello del prodotto
	 * @param quantita: il numero di scorte in magazzino del prodotto
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
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria
	 * (con sottocategoria non nota).
	 * 
	 * @param codiceProdotto : il codice identificativo del prodotto
	 * @param nomeProdotto : il nominativo del prodotto
	 * @param topDescrizione : la descrizione di presentazione del prodotto
	 * @param prezzo : il costo del prodotto
	 * @param categoria : la categoria di appartenenza del prodotto
	 * @param marca : il produttore del prodotto
	 * @param modello : il modello del prodotto secondo la casa produttrice
	 * @param quantita : il numero di scorte disponibili in magazzino per quel prodotto
	 * @param inCatalogo : indica se il prodotto è presente nel catalogo
	 * @param inVetrina : indica se il prodotto deve essere presente in una vetrina virtuale del sito
	 * 
	 * @return un oggetto della classe ObjectProdotto avente come attributi codiceProdotto, nomeProdotto, topDescrizione,
	 * 			prezzo, categoria, marca, modello, quantita, inCatalogo e inVetrina.
	 * 
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
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria, e con 
	 * sottocategoria nota.
	 * 
	 * @param codiceProdotto : il codice identificativo del prodotto
	 * @param nomeProdotto : il nominativo del prodotto
	 * @param topDescrizione : la descrizione di presentazione del prodotto
	 * @param prezzo : il costo del prodotto
	 * @param categoria : la categoria di appartenenza del prodotto
	 * @param sottocategoria : la sottocategoria di appartenenza del prodotto (rientrante nella categoria scelta)
	 * @param marca : il produttore del prodotto
	 * @param modello : il modello del prodotto secondo la casa produttrice
	 * @param quantita : il numero di scorte disponibili in magazzino per quel prodotto
	 * @param inCatalogo : indica se il prodotto è presente nel catalogo
	 * @param inVetrina : indica se il prodotto deve essere presente in una vetrina virtuale del sito
	 * 
	 * @return un oggetto della classe ObjectProdotto avente come attributi codiceProdotto, nomeProdotto, topDescrizione,
	 * 			prezzo, categoria, sottocategoria, marca, modello, quantita, inCatalogo e inVetrina.
	 * 
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
	 * Il metodo restituisce il codice del prodotto.
	 * 
	 * @return codiceProdotto: identificativo numerico del prodotto
	 * */
	
	public int getCodiceProdotto() {
		return codiceProdotto;
	}
	
	/**
	 * Il metodo imposta il codice del prodotto.
	 * 
	 * @param codiceProdotto : il codice del prodotto da inserire
	 * */
	
	public void setCodiceProdotto(int codiceProdotto) {
		this.codiceProdotto = codiceProdotto;
	}
	
	/**
	 * Il metodo restituisce il nome del prodotto.
	 * 
	 * @return nomeProdotto: nominativo del prodotto
	 * */
	
	public String getNomeProdotto() {
		return nomeProdotto;
	}
	
	/**
	 * Il metodo imposta il nominativo del prodotto.
	 * 
	 * @param nomeProdotto : il nome del prodotto da inserire
	 * */
	
	public void setNomeProdotto(String nomeProdotto) {
		this.nomeProdotto = nomeProdotto;
	}
	
	/**
	 * Il metodo fornisce la descrizione di presentazione del prodotto.
	 * 
	 * @return topDescrizione : descrizione in primo piano del prodotto
	 * */
	
	public String getTopDescrizione() {
		return topDescrizione;
	}
	
	/**
	 * Il metodo imposta la descrizione di presentazione del prodotto.
	 * 
	 * @param topDescrizione: descrizione in primo piano del prodotto
	 * */
	
	public void setTopDescrizione(String topDescrizione) {
		this.topDescrizione = topDescrizione;
	}
	
	/**
	 * Il metodo fornisce il prezzo del prodotto.
	 * 
	 * @return prezzo : il costo del prodotto
	 * */
	
	public float getPrezzo() {
		return prezzo;
	}
	
	/**
	 * Il metodo imposta il prezzo del prodotto.
	 * 
	 * @param prezzo : il costo del prodotto
	 * */
	
	public void setPrezzo(float prezzo) {
		this.prezzo = prezzo;
	}
	
	/**
	 * Il metodo fornisce la categoria di appartenenza del prodotto.
	 * 
	 * @return categoria : la categoria del prodotto
	 * */
	
	public Categoria getCategoria() {
		return categoria;
	}
	
	/**
	 * Il metodo imposta la categoria di appartenenza del prodotto
	 * 
	 * @param categoria : la categoria del prodotto
	 * */
	
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	/**
	 * Il metodo fornisce la categoria di appartenenza del prodotto sottoforma di stringa.
	 * 
	 * @return la categoria del prodotto come oggetto della classe String
	 * */
	
	public String getCategoriaAsString() {
		return categoria.toString();
	}
	
	/**
	 * Il metodo imposta la categoria di appartenenza del prodotto.
	 * 
	 * @param category : la categoria del prodotto espressa come oggetto della classe String
	 * 
	 * @throws CategoriaProdottoException : gestisce l'inserimento di una categoria non ammessa dalla classe
	 * 										enum Categoria @see application.NavigazioneService.ObjectProdotto.Categoria
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
	 * Il metodo fornisce la sottocategoria di appartenenza del prodotto.
	 * 
	 * @return sottocategoria : la sottocategoria del prodotto
	 * */
	public Sottocategoria getSottocategoria() {
		return sottocategoria;
	}
	
	/**
	 * Il metodo fornisce la sottocategoria di appartenenza del prodotto sottoforma di stringa.
	 * 
	 * @return la sottocategoria del prodotto come oggetto della classe String
	 * */
	public String getSottocategoriaAsString() {
		if(this.sottocategoria == null)
			return "";
		return sottocategoria.toString();
	}
	
	/**
	 * Il metodo imposta la sottocategoria di appartenenza del prodotto.
	 * 
	 * @param subcategory : la sottocategoria del prodotto espressa come oggetto della classe String
	 * 
	 * @throws SottocategoriaProdottoException : gestisce l'inserimento di una sottocategoria non ammessa dalla classe
	 * 											enum Sottocategoria @see application.NavigazioneService.ObjectProdotto.Sottocategoria
	 * */
	
	public void setSottocategoria(String subcategory) throws SottocategoriaProdottoException {
		if(subcategory == null) {
			this.sottocategoria = null;
			return; 
		}
	    switch (subcategory.toUpperCase()) {
	        case "TABLET":
	        	if(this.categoria.toString().equalsIgnoreCase(Categoria.TELEFONIA.toString())) {
	        		this.sottocategoria = Sottocategoria.TABLET;
	        		break;
	        	}
	        	throw new SottocategoriaProdottoException("Errata sottocategoria.\nSe specificata, le sottocategorie ammissibili per un prodotto sono:"
		        		+ "\n- TABLET e SMARTPHONE per la categoria TELEFONIA;"
		        		+ "\n- PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.");
	        	
	        case "SMARTPHONE":
	        	if(this.categoria.toString().equalsIgnoreCase(Categoria.TELEFONIA.toString())) {
	        		this.sottocategoria = Sottocategoria.SMARTPHONE;
	            	break;
	        	}
	        	throw new SottocategoriaProdottoException("Errata sottocategoria.\nSe specificata, le sottocategorie ammissibili per un prodotto sono:"
		        		+ "\n- TABLET e SMARTPHONE per la categoria TELEFONIA;"
		        		+ "\n- PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.");
	        case "PC":
	        	if(this.categoria.toString().equalsIgnoreCase(Categoria.PRODOTTI_ELETTRONICA.toString())) {
	        		this.sottocategoria = Sottocategoria.PC;
	        		break;
	        	}
	        	throw new SottocategoriaProdottoException("Errata sottocategoria.\nSe specificata, le sottocategorie ammissibili per un prodotto sono:"
		        		+ "\n- TABLET e SMARTPHONE per la categoria TELEFONIA;"
		        		+ "\n- PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.");
	        case "SMARTWATCH":
	        	if(this.categoria.toString().equalsIgnoreCase(Categoria.PRODOTTI_ELETTRONICA.toString())) {
	        		this.sottocategoria = Sottocategoria.SMARTWATCH;
	        		break;
	        	}
	        	throw new SottocategoriaProdottoException("Errata sottocategoria.\nSe specificata, le sottocategorie ammissibili per un prodotto sono:"
		        		+ "\n- TABLET e SMARTPHONE per la categoria TELEFONIA;"
		        		+ "\n- PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.");
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
	 * Il metodo fornisce la casa produttrice del prodotto.
	 * 
	 * @return marca : la marca del prodotto
	 * */
	
	public String getMarca() {
		return marca;
	}
	
	/**
	 * Il metodo imposta la marca del prodotto.
	 * 
	 * @param marca : l'azienda produttrice del prodotto
	 * */
	
	public void setMarca(String marca) {
		this.marca = marca;
	}
	
	/**
	 * Il metodo fornisce il modello del prodotto.
	 * 
	 * @return modello : il modello del prodotto
	 * */
	
	public String getModello() {
		return modello;
	}
	
	/**
	 * Il metodo imposta il modello del prodotto.
	 * 
	 * @param modello : la versione o variante del prodotto definita dall'azienda produttrice
	 * */
	
	public void setModello(String modello) {
		this.modello = modello;
	}
	
	/**
	 * Il metodo fornisce le quantità del prodotto disponibili in magazzino.
	 * 
	 * @return quantita : scorte nel magazzino del prodotto
	 * */
	
	public int getQuantita() {
		return quantita;
	}
	
	/**
	 * Il metodo imposta la quantità del prodotto in magazzino.
	 * 
	 * @param quantita : il numero di scorte del prodotto in magazzino
	 * */
	
	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}
	
	/**
	 * Il metodo permette di comprendere se un dato prodotto è presente nel catalogo.
	 * 
	 * @return true se è presente il prodotto nel catalogo; false altrimenti.
	 * */
	
	public boolean isInCatalogo() {
		return inCatalogo;
	}
	
	/**
	 * Il metodo permette di comprendere se un dato prodotto è presente nel catalogo.
	 * 
	 * @return 1 se è presente il prodotto nel catalogo; 0 altrimenti.
	 * */
	
	public int isInCatalogoInt() {
		return inCatalogo? 1 : 0;
	}
	
	/**
	 * Il metodo imposta la disponibilità del prodotto nel catalogo del negozio online.
	 * 
	 * @param inCatalogo : true se il prodotto è presente nel catalogo, false altrimenti.
	 * */
	
	public void setInCatalogo(boolean inCatalogo) {
		this.inCatalogo = inCatalogo;
	}
	
	/**
	 * Il metodo permette di comprendere se un dato prodotto è presente nella vetrina del negozio
	 * come rappresentante dei prodotti della propria categoria di appartenenza.
	 * 
	 * @return true se è presente il rappresentante della categoria di appartenenza; false altrimenti.
	 * */
	
	public boolean isInVetrina() {
		return inVetrina;
	}
	
	/**
	 * Il metodo permette di comprendere se un dato prodotto è presente nella vetrina del negozio
	 * come rappresentante dei prodotti della propria categoria di appartenenza.
	 * 
	 * @return 1 se è presente il rappresentante della categoria di appartenenza; 0 altrimenti.
	 * */
	
	public int isInVetrinaInt() {
		return inVetrina? 1 : 0;
	}
	
	/**
	 * Il metodo imposta il prodotto come rappresentante della categoria di appartenenza 
	 * e, pertanto, sarà nella vetrina del negozio online per l'esposizione 
	 * dei prodotti della suddetta categoria se inVetrina == true.
	 * In caso contrario, il prodotto non sarà in vetrina.
	 * 
	 * */
	
	public void setInVetrina(boolean inVetrina) {
		this.inVetrina = inVetrina;
	}
	
	/**
	 * Calcola e restituisce un valore hash per l'oggetto corrente.
	 * 
	 * Il valore hash è calcolato utilizzando i campi significativi
	 * dell'oggetto, inclusi codiceProdotto, nomeProdotto, marca,
	 * prezzo e quantita. Questo metodo deve essere sovrascritto
	 * per garantire la coerenza con il metodo equals.
	 * 
	 * @return un valore hash per l'oggetto corrente.
	 */
	
	@Override
	public int hashCode() {
	    return Objects.hash(codiceProdotto, nomeProdotto, marca, modello, prezzo);
	}

	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
	        return true; // Riferimento identico
	    }
	    if (obj == null || getClass() != obj.getClass()) {
	        return false; // Oggetto nullo o classi diverse
	    }
	    ObjectProdotto other = (ObjectProdotto) obj; // Cast dell'oggetto
	    return codiceProdotto == other.codiceProdotto 
	            && nomeProdotto.equals(other.nomeProdotto)
	            && marca.equals(other.marca)
	            && modello.equals(other.modello)
	            && prezzo == other.prezzo;
	}

	

	/**
	 * Il metodo fornisce in formato stringa le caratteristiche associate al prodotto.
	 * */
	
	@Override
	public String toString() {
		return "Prodotto [CodiceProdotto=" + codiceProdotto + ", NomeProdotto=" + nomeProdotto + ", Prezzo=" + prezzo
				+ ", Categoria=" + categoria + ", Marca=" + marca + ", Modello = "+ modello + "]";
	}
	
	/**
	 * Il metodo fornisce in formato stringa le caratteristiche associate al prodotto.
	 * Tale metodo viene utilizzato per descrivere i prodotti presenti in un ordine.
	 * */
	
	public String ordertoString() {
		return "Prodotto [CodiceProdotto=" + codiceProdotto + ", NomeProdotto=" + nomeProdotto + ", "
				+ "Categoria=" + categoria + ", Marca=" + marca + ", Modello = " + modello + "]";
	}
}
