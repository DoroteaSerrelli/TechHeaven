package application.Navigazione.NavigazioneService;

import java.sql.SQLException;
import java.util.Objects;

import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.Navigazione.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoCodiceException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoModelloException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoNomeException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoVetrinaException;
import application.Navigazione.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 * Questa classe astratta è la classe client del design pattern Proxy
 * utilizzato per l'accesso e la gestione delle operazioni sull'oggetto Prodotto.
 * 
 * @see application.Navigazione.NavigazioneService.Prodotto
 * @see application.Navigazione.NavigazioneService.ProxyProdotto
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
	 * dettagli : testo dettagliato che elenca le specifiche del prodotto 
	 * (es. componenti, finalità del prodotto, corretto utilizzo, ...)
	 * */

	private String dettagli;

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
	 * @param dettagli : la descrizione di dettaglio del prodotto
	 * @param prezzo: il prezzo del prodotto
	 * @param marca: il produttore del prodotto
	 * @param modello: il modello del prodotto
	 * @param quantita: il numero di scorte in magazzino del prodotto
	 * @param categoria : la categoria di appartenenza del prodotto
	 * 
	 * @return true se i dati sono stati inseriti con il formato corretto; false altrimenti.
	 * 
	 * @throws ProdottoInCatalogoException 
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws QuantitaProdottoException 
	 * @throws FormatoNomeException 
	 * @throws FormatoModelloException 
	 * @throws FormatoMarcaException 
	 * @throws PrezzoProdottoException 
	 * @throws FormatoTopDescrizioneException 
	 * @throws FormatoDettagliException 
	 * @throws FormatoCodiceException 
	 * */

	public static boolean checkValidate(String codice, String nome, String marca, String modello, String topDescrizione, String dettagli, float prezzo, 
			int quantita, String categoria, ProdottoDAODataSource productDAO) throws ProdottoInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, QuantitaProdottoException, FormatoNomeException, FormatoModelloException, FormatoMarcaException, PrezzoProdottoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoCodiceException {

		String namePattern = "^[a-zA-Z0-9 ]+$";
		String modelPattern = "^[A-Za-z0-9]+([-]?[A-Za-z0-9]+)*(\s?[A-Za-z0-9]+)*$";
		String brandPattern = "^[A-Za-z ]+$";
		ProxyProdotto retrieved = null;
		int codiceProdotto;

		try {
			codiceProdotto = Integer.parseInt(codice);
		}catch(NumberFormatException ex) {

			throw new FormatoCodiceException("Il codice del prodotto deve contenere numeri");
		}
		if((retrieved = productDAO.doRetrieveProxyByKey(codiceProdotto)) != null && retrieved.isInCatalogo())
			throw new ProdottoInCatalogoException("Il codice inserito è già associato ad un prodotto nel catalogo");

		if(!nome.matches(namePattern))
			throw new FormatoNomeException("Il nome del prodotto deve contenere lettere e, eventualmente, numeri e spazi.");

		if(!modello.matches(modelPattern))
			throw new FormatoModelloException("Il modello deve contenere lettere e, eventualmente, numeri, spazi e trattini");

		if(!marca.matches(brandPattern))
			throw new FormatoMarcaException("La marca del prodotto deve contenere lettere e, eventualmente, spazi");

		if(prezzo <= 0.0)
			throw new PrezzoProdottoException("Il prezzo deve essere un numero con la virgola arrotondato in centesimi");

		if(topDescrizione.isBlank())
			throw new FormatoTopDescrizioneException("La descrizione di presentazione non può essere vuota");

		if(dettagli.isBlank())
			throw new FormatoDettagliException("La descrizione di dettaglio non può essere vuota");

		if(quantita < 1)
			throw new QuantitaProdottoException("La quantità di un prodotto disponibile deve essere almeno 1");

		if(!categoria.equalsIgnoreCase("TELEFONIA") && !categoria.equalsIgnoreCase("PRODOTTI_ELETTRONICA")
				&& !categoria.equalsIgnoreCase("PICCOLI_ELETTRODOMESTICI") 
				&& !categoria.equalsIgnoreCase("GRANDI_ELETTRODOMESTICI"))
			throw new CategoriaProdottoException("La categoria inserita non esiste. Sono ammesse come categorie : TELEFONIA, PRODOTTI_ELETTRONICA, PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI.");

		return true;
	}

	/**
	 * Il metodo verifica se i dati inseriti, relativi ad un prodotto, rispettano
	 * il relativo formato.
	 * 
	 * @param codice: il codice del prodotto
	 * @param nome: il nome del prodotto
	 * @param topDescrizione: la descrizione di presentazione del prodotto
	 * @param dettagli : la descrizione di dettaglio del prodotto
	 * @param prezzo: il prezzo del prodotto
	 * @param marca: il produttore del prodotto
	 * @param modello: il modello del prodotto
	 * @param quantita: il numero di scorte in magazzino del prodotto
	 * @param categoria : la categoria di appartenenza del prodotto
	 * @param sottocategoria : la sottocategoria di appartenenza del prodotto
	 * 
	 * @return true se i dati sono stati inseriti con il formato corretto; false altrimenti.
	 * 
	 * @throws ProdottoInCatalogoException 
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws QuantitaProdottoException 
	 * @throws FormatoTopDescrizioneException 
	 * @throws FormatoNomeException 
	 * @throws FormatoModelloException 
	 * @throws FormatoMarcaException 
	 * @throws PrezzoProdottoException 
	 * @throws FormatoDettagliException 
	 * @throws AppartenenzaSottocategoriaException 
	 * @throws FormatoCodiceException 
	 * */

	public static boolean checkValidate(String codice, String nome, String marca, String modello, String topDescrizione, String dettagli, float prezzo, 
			int quantita, String categoria, String sottocategoria, ProdottoDAODataSource productDAO) throws ProdottoInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, QuantitaProdottoException, FormatoTopDescrizioneException, FormatoNomeException, FormatoModelloException, FormatoMarcaException, PrezzoProdottoException, FormatoDettagliException, AppartenenzaSottocategoriaException, FormatoCodiceException {

		String namePattern = "^[a-zA-Z0-9 ]+$";
		String modelPattern = "^[A-Za-z0-9]+([-]?[A-Za-z0-9]+)*(\s?[A-Za-z0-9]+)*$";
		String brandPattern = "^[A-Za-z ]+$";

		ProxyProdotto retrieved = null;
		int codiceProdotto;

		try {
			codiceProdotto = Integer.parseInt(codice);
		}catch(NumberFormatException ex) {

			throw new FormatoCodiceException("Il codice del prodotto deve contenere numeri");
		}

		if((retrieved = productDAO.doRetrieveProxyByKey(codiceProdotto)) != null && retrieved.isInCatalogo())
			throw new ProdottoInCatalogoException("Il prodotto con il codice inserito esiste nel catalogo");

		if(!nome.matches(namePattern))
			throw new FormatoNomeException("Il nome del prodotto deve contenere lettere e, eventualmente, numeri e spazi.");

		if(!modello.matches(modelPattern))
			throw new FormatoModelloException("Il modello deve contenere lettere e, eventualmente, numeri, spazi e trattini");

		if(!marca.matches(brandPattern))
			throw new FormatoMarcaException("La marca del prodotto deve contenere lettere e, eventualmente, spazi");

		if(prezzo <= 0.0)
			throw new PrezzoProdottoException("Il prezzo deve essere un numero con la virgola arrotondato in centesimi");

		if(topDescrizione.isBlank())
			throw new FormatoTopDescrizioneException("La descrizione di presentazione non può essere vuota");

		if(dettagli.isBlank())
			throw new FormatoDettagliException("La descrizione di dettaglio non può essere vuota");


		if(quantita < 1)
			throw new QuantitaProdottoException("La quantità di un prodotto disponibile deve essere almeno 1");

		if(!categoria.equalsIgnoreCase("TELEFONIA") && !categoria.equalsIgnoreCase("PRODOTTI_ELETTRONICA")
				&& !categoria.equalsIgnoreCase("PICCOLI_ELETTRODOMESTICI") 
				&& !categoria.equalsIgnoreCase("GRANDI_ELETTRODOMESTICI"))
			throw new CategoriaProdottoException("La categoria inserita non esiste. Sono ammesse come categorie : TELEFONIA, PRODOTTI_ELETTRONICA, PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI.");

		if(!sottocategoria.equalsIgnoreCase("SMARTWATCH") && !sottocategoria.equalsIgnoreCase("PC")
				&& !sottocategoria.equalsIgnoreCase("SMARTPHONE")
				&& !sottocategoria.equalsIgnoreCase("TABLET"))
			throw new SottocategoriaProdottoException("La sottocategoria specificata non esiste. Sono ammesse le seguenti sottocategorie: TABLET, SMARTPHONE, PC, SMARTWATCH.");

		if(categoria.equalsIgnoreCase("TELEFONIA")) {
			if(!sottocategoria.equalsIgnoreCase("TABLET") && !sottocategoria.equalsIgnoreCase("SMARTPHONE"))
				throw new AppartenenzaSottocategoriaException("Errata sottocategoria.\n Se specificata, le sottocategorie ammissibili per un prodotto sono:\r\n"
						+ "-	TABLET e SMARTPHONE per la categoria TELEFONIA;\r\n"
						+ "-	PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.\r\n"
						+ "");
		}

		if(categoria.equalsIgnoreCase("PRODOTTI_ELETTRONICA")) {
			if(!sottocategoria.equalsIgnoreCase("PC") && !sottocategoria.equalsIgnoreCase("SMARTWATCH"))
				throw new AppartenenzaSottocategoriaException("Errata sottocategoria.\n Se specificata, le sottocategorie ammissibili per un prodotto sono:\r\n"
						+ "-	TABLET e SMARTPHONE per la categoria TELEFONIA;\r\n"
						+ "-	PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.\r\n"
						+ "");
		}

		return true;
	}


	/**
	 * Verifica correttezza descrizione in evidenza
	 * 
	 * */

	public static boolean checkValidateTopDescrizione(String topDescrizione) throws FormatoTopDescrizioneException {

		if(topDescrizione.isBlank())
			throw new FormatoTopDescrizioneException("La descrizione di presentazione non può essere vuota");

		return true;
	}

	/**
	 * Verifica correttezza descrizione in dettaglio
	 * 
	 * */

	public static boolean checkValidateDettagli(String dettagli) throws FormatoDettagliException {

		if(dettagli.isBlank())
			throw new FormatoDettagliException("La descrizione di dettaglio non può essere vuota");

		return true;
	}

	/**
	 * Verifica correttezza modello
	 * 
	 * */

	public static boolean checkValidateModello(String modello) throws FormatoModelloException {
		String modelPattern = "^[A-Za-z0-9]+([-]?[A-Za-z0-9]+)*(\s?[A-Za-z0-9]+)*$";

		if(!modello.matches(modelPattern))
			throw new FormatoModelloException("Il modello deve contenere lettere e, eventualmente, numeri, spazi e trattini");

		return true;
	}

	/**
	 * Verifica correttezza marca
	 * 
	 * */

	public static boolean checkValidateMarca(String marca) throws FormatoMarcaException {
		String brandPattern = "^[A-Za-z ]+$";

		if(!marca.matches(brandPattern))
			throw new FormatoMarcaException("La marca del prodotto deve contenere lettere e, eventualmente, spazi");

		return true;
	}

	/**
	 * Verifica correttezza categoria
	 * @throws CategoriaProdottoException 
	 * 
	 * */

	public static boolean checkValidateCategoria(String categoria) throws CategoriaProdottoException {

		if(!categoria.equalsIgnoreCase("TELEFONIA") && !categoria.equalsIgnoreCase("PRODOTTI_ELETTRONICA")
				&& !categoria.equalsIgnoreCase("PICCOLI_ELETTRODOMESTICI") 
				&& !categoria.equalsIgnoreCase("GRANDI_ELETTRODOMESTICI"))
			throw new CategoriaProdottoException("La categoria inserita non esiste. Sono ammesse come categorie : TELEFONIA, PRODOTTI_ELETTRONICA, PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI.");

		return true;
	}


	/**
	 * Verifica correttezza sottocategoria
	 * 
	 * */


	public static boolean checkValidateSottocategoria(String sottocategoria) throws  SottocategoriaProdottoException{

		if(!sottocategoria.equalsIgnoreCase("SMARTWATCH") && !sottocategoria.equalsIgnoreCase("PC")
				&& !sottocategoria.equalsIgnoreCase("SMARTPHONE")
				&& !sottocategoria.equalsIgnoreCase("TABLET"))
			throw new SottocategoriaProdottoException("La sottocategoria specificata non esiste. Sono ammesse le seguenti sottocategorie: TABLET, SMARTPHONE, PC, SMARTWATCH.");
		return true;
	}

	/**
	 * Verifica correttezza dell'appartenenza della 
	 * sottocategoria alla categoria specificata per il prodotto.
	 * @throws SottocategoriaProdottoException 
	 *  
	 * 
	 * */

	public static boolean checkValidateAppartenenzaSottocategoria(String categoria, String sottocategoria) throws  AppartenenzaSottocategoriaException{

		if(categoria.equalsIgnoreCase("TELEFONIA")) {
			if(!sottocategoria.equalsIgnoreCase("TABLET") && !sottocategoria.equalsIgnoreCase("SMARTPHONE"))
				throw new AppartenenzaSottocategoriaException("Errata sottocategoria.\n Se specificata, le sottocategorie ammissibili per un prodotto sono:\r\n"
						+ "-	TABLET e SMARTPHONE per la categoria TELEFONIA;\r\n"
						+ "-	PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.\r\n"
						+ "");
		}

		if(categoria.equalsIgnoreCase("PRODOTTI_ELETTRONICA")) {
			if(!sottocategoria.equalsIgnoreCase("PC") && !sottocategoria.equalsIgnoreCase("SMARTWATCH"))
				throw new AppartenenzaSottocategoriaException("Errata sottocategoria.\n Se specificata, le sottocategorie ammissibili per un prodotto sono:\r\n"
						+ "-	TABLET e SMARTPHONE per la categoria TELEFONIA;\r\n"
						+ "-	PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.\r\n"
						+ "");
		}
		return true;
	}

	/**
	 * Verifica correttezza messa in evidenza del prodotto
	 * @throws FormatoVetrinaException 
	 * 
	 * */


	public static boolean checkValidateVetrina(String vetrina) throws  SottocategoriaProdottoException, FormatoVetrinaException{

		/*
		if(!vetrina.equalsIgnoreCase("TRUE") && !vetrina.equalsIgnoreCase("FALSE"))
			throw new FormatoVetrinaException("Per aggiungere un prodotto in vetrina inserire TRUE,\nmentre per rimuovere un prodotto in vetrina inserire FALSE");
		return true;*/

		try {
			double viewDouble = Double.parseDouble(vetrina);
			int updatedDataInt = (int) viewDouble;
			vetrina = String.valueOf(updatedDataInt);

			if(!vetrina.equals("0") && !vetrina.equals("1"))
				throw new FormatoVetrinaException("Per aggiungere un prodotto in vetrina inserire 1,\nmentre per rimuovere un prodotto in vetrina inserire 0");
		
		}catch(NumberFormatException e) {
			
			throw new FormatoVetrinaException("Per aggiungere un prodotto in vetrina inserire 1,\nmentre per rimuovere un prodotto in vetrina inserire 0");
		}
		
		return true;
	}

	/**
	 * Costruttore di classe di default.
	 * */

	protected ObjectProdotto() {
		this.codiceProdotto = 0;
		this.nomeProdotto = "";
		this.topDescrizione = "";
		this.dettagli = "";
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
	 * @param dettagli : descrizione di dettaglio per un prodotto
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

	protected ObjectProdotto(int codiceProdotto, String nomeProdotto, String topDescrizione, String dettagli, float prezzo,
			Categoria categoria, String marca, String modello, int quantita,
			boolean inCatalogo, boolean inVetrina) {

		this.codiceProdotto = codiceProdotto;
		this.nomeProdotto = nomeProdotto;
		this.topDescrizione = topDescrizione;
		this.dettagli = dettagli;
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
	 * @param dettagli : la descrizione di dettaglio del prodotto
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

	protected ObjectProdotto(int codiceProdotto, String nomeProdotto, String topDescrizione, String dettagli, float prezzo,
			Categoria categoria, Sottocategoria sottocategoria, String marca, String modello, int quantita,
			boolean inCatalogo, boolean inVetrina) {
		this.codiceProdotto = codiceProdotto;
		this.nomeProdotto = nomeProdotto;
		this.topDescrizione = topDescrizione;
		this.dettagli = dettagli;
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
	 * 										enum Categoria @see application.Navigazione.NavigazioneService.ObjectProdotto.Categoria
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
	 * 											enum Sottocategoria @see application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria
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
				&& prezzo == other.prezzo
				&& topDescrizione.equals(other.topDescrizione)
				&& dettagli.equals(other.dettagli)
				&& quantita == other.quantita
				&& categoria.toString().equals(other.categoria.toString())
				&& equalsSottocategoria(other.sottocategoria)
				&& inVetrina == other.inVetrina;


	}

	public boolean equalsSottocategoria(Sottocategoria otherSubcategory) {
		if(sottocategoria == null && otherSubcategory == null)
			return true;
		if(sottocategoria != null && otherSubcategory != null) {
			if(sottocategoria.toString().equals(otherSubcategory.toString()))
				return true;
		}

		return false;
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
