package application.GestioneOrdini.GestioneOrdiniService;

import java.time.LocalDate;
import java.time.LocalTime;

import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCAPException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCittaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoNumCivicoException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoProvinciaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoViaException;

/**
 * Questa classe astratta è la classe client del design pattern Proxy
 * utilizzato per l'accesso e la gestione delle operazioni sull'oggetto Ordine.
 * 
 * @see application.GestioneOrdini.GestioneOrdiniService.Ordine
 * @see application.GestioneOrdini.GestioneOrdiniService.ProxyOrdine
 * 
 * @author Dorotea Serrelli
 * */

public abstract class ObjectOrdine implements Cloneable{
	
	/**
	 * L'enum Stato permette di identificare lo stato di elaborazione dell'ordine.
	*/
	public enum Stato{
		/**
		 * Richiesta_effettuata: il cliente ha effettuato il pagamento dell'ordine ed è 
		 * stata inviata la richiesta di elaborazione dell'ordine al negozio*/
		
		Richiesta_effettuata,
		
		/**In lavorazione : la richiesta di elaborazione dell'ordine è stata presa in carico
		 * da uno dei gestori degli ordini del negozio. In questa fase l'ordine viene preparato
		 * alla spedizione*/
		
		In_lavorazione,
		
		/**
		 *Spedito : l'ordine è stato spedito dal negozio ad un'azienda di logistica convenzionata,
		 *la quale si occuperà della consegna del pacco*/
		
		Spedito,
		
		/**
		 *Preparazione_incompleta: l'ordine è stato preso in carico da uno dei gestori degli ordini
		 *del negozio ma non è stato completato per la spedizione (ad esempio per mancanza dei prodotti richiesti).
		 * */
		
		Preparazione_incompleta
	}
	
	/**
	 * L'enum TipoSpedizione definisce la tipologia di spedizione dell'ordine scelta dall'utente
	 * in fase di check-out del carrello.
	 * */
	public enum TipoSpedizione{
		/**
		 * Spedizione_standard : l'ordine viene elaborato da 3 a 5 giorni lavorativi.
		 * */
		
		Spedizione_standard,
		
		/**
		 * Spedizione_assicurata : l'ordine viene elaborato da 4 a 6 giorni lavorativi.
		 * */
		
		Spedizione_assicurata,
		
		/**
		 * Spedizione_prime : l'ordine viene elaborato in 2 giorni lavorativi.
		 * */
		
		Spedizione_prime
	}
	
	/**
	 * L'enum TipoConsegna definisce la tipologia di consegna dell'ordine scelta dall'utente
	 * in fase di check-out del carrello.
	 * */
	public enum TipoConsegna{
		/**
		 * Domicilio : l'ordine viene spedito presso l'indirizzo di spedizione indicato dall'utente.
		 * */
		
		Domicilio,
		
		/**
		 * Punto_ritiro : l'ordine viene spedito presso un punto di ritiro convenzionato con l'azienda logistica, 
		 * nelle vicinanze dell'indirizzo di spedizione dell'utente.
		 * */
		
		Punto_ritiro,
		
		/**
		 * Priority : l'ordine viene spedito presso l'indirizzo di spedizione specificato dall'utente, scegliendo
		 * una fascia oraria da concordare con il negozio.
		 * */
		
		Priority
	}
	
	/**
	 * codiceOrdine : identificativo numerico dell'ordine
	 * */
	
	private int codiceOrdine;
	
	/**
	 * stato: stato di elaborazione dell'ordine
	 * */
	
	private Stato stato;
	
	/**
	 * indirizzoSpedizione : l'indirizzo di spedizione specificato dall'utente in fase di check-out dell'ordine,
	 * al fine di recapitare il pacco.
	 * */
	
	private String indirizzoSpedizione;
	
	/**
	 * spedizione : la tipologia di spedizione dal negozio
	 * verso un'azienda logistica
	 * */
	
	private TipoSpedizione spedizione;
	
	/**
	 * consegna : la tipologia di consegna dell'ordine
	 * */
	
	private TipoConsegna consegna;
	
	/**
	 * data : la data in cui viene effettuata la richiesta di 
	 * creazione dell'ordine dal cliente
	 * */
	
	private LocalDate data;
	
	/**
	 * ora : l'ora in cui viene effettuata la richiesta di 
	 * creazione dell'ordine dal cliente 
	 * */
	
	private LocalTime ora;
	
	/**
	 * Il metodo verifica la correttezza del formato dell'indirizzo di spedizione
	 * fornito dall'utente per la spedizione di un ordine.
	 * 
	 * @param indirizzoSpedizione : l'indirizzo presso cui spedire l'ordine
	 * 
	 * @return true se indirizzoSpedizione è espresso nel formato corretto; false altrimenti.
	 * @throws FormatoProvinciaException  : eccezione che gestisce il caso in cui la provincia
	 * 										non è espressa nel formato corretto
	 * 
	 * @throws FormatoCAPException : eccezione che gestisce il caso in cui il CAP non rispetta
	 * 								 il formato
	 * 
	 * @throws FormatoCittaException : eccezione che gestisce il caso in cui la città non
	 * 									è specificata nel formato corretto
	 * 
	 * @throws FormatoNumCivicoException : eccezione che gestisce il caso in cui il numero civico
	 * 										non è specificato nel formato corretto
	 * 
	 * @throws FormatoViaException : eccezione che gestisce il caso in cui la via non è specificata nel formato
	 * 									corretto 
	 * 
	 * 
	 * */
	
	public static boolean checkValidate(Indirizzo indirizzoSpedizione) throws FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		return Indirizzo.checkValidate(indirizzoSpedizione);
	}
	
	/**
	 * Costruttore di classe di default.
	 * */
	
	protected ObjectOrdine() {
		codiceOrdine = 0;
		stato = null;
		indirizzoSpedizione = null;
		consegna = null;
	}
	
	/**
	 * Costruttore di classe.
	 * 
	 * @param codice : l'identificativo numerico dell'ordine;
	 * @param stato : lo stato dell'ordine;
	 * @param indirizzoSpedizione : l'indirizzo di spedizione scelto dal committente;
	 * @param spedizione : la tipologia di spedizione scelta dall'utente;
	 * @param consegna : la tipologia di consegna scelta dall'utente.
	 * 
	 * @return un oggetto della classe ObjectOrdine inizializzato con i parametri di input
	 * 		   codice, stato, indirizzoSpedizione, spedizione e consegna
	 * 				e gli attributi data e ora correnti.
	 * */
	
	protected ObjectOrdine(int codice, Stato stato, Indirizzo indirizzoSpedizione, TipoSpedizione spedizione, TipoConsegna consegna) {
		codiceOrdine = codice;
		this.stato = stato;
		this.indirizzoSpedizione = indirizzoSpedizione.toString();
		this.spedizione = spedizione;
		this.consegna = consegna;
		data = LocalDate.now();
		ora = LocalTime.now();
	}
	
	/**
	 * Il metodo fornisce il codice dell'ordine.
	 * 
	 * @return codiceOrdine : il codice dell'ordine
	 * */
	
	public int getCodiceOrdine() {
		return codiceOrdine;
	}
	
	/**
	 * Il metodo imposta il codice dell'ordine.
	 * 
	 * @param codiceOrdine : l'identificativo numerico da associare 
	 * 						ad un ordine.
	 * */
	
	public void setCodiceOrdine(int codiceOrdine) {
		this.codiceOrdine = codiceOrdine;
	}
	
	/**
	 * Il metodo restituisce lo stato dell'ordine.
	 * 
	 * @return stato : lo stato dell'ordine
	 * */
	
	public Stato getStato() {
		return stato;
	}
	
	/**
	 * Il metodo imposta lo stato di elaborazione dell'ordine.
	 * 
	 * @param stato : lo stato dell'ordine
	 * */
	
	public void setStato(Stato stato) {
		this.stato = stato;
	}
	
	/**
	 * Il metodo fornisce lo stato di elaborazione dell'ordine
	 * sottoforma di stringa.
	 * 
	 * @return lo stato dell'ordine come oggetto di classe String
	 * */
	
	public String getStatoAsString() {
		return stato.toString();
	}
	
	/**
	 * Imposta lo stato di elaborazione dell'ordine espresso in formato stringa.
	 * 
	 * @param stato: lo stato dell'ordine espresso come oggetto della classe String
	 * */
	
	public void setStatoAsString(String stato) {
		switch (stato.toUpperCase()) {
		case "RICHIESTA_EFFETTUATA", "RICHIESTA EFFETTUATA":
			this.stato = Stato.Richiesta_effettuata;
		break;
		case "IN_LAVORAZIONE", "IN LAVORAZIONE":
			this.stato = Stato.In_lavorazione;
		break;
		case "SPEDITO":
			this.stato = Stato.Spedito;
			break;
		case "PREPARAZIONE_INCOMPLETA", "PREPARAZIONE INCOMPLETA":
			this.stato = Stato.Preparazione_incompleta;
		break;
		default:
			throw new IllegalArgumentException("Lo stato dell'ordine può essere :\n-Richiesta effettuata;"
					+ "\n-In lavorazione;\n-Spedito;\n-Preparazione incompleta.");
		}
	}

	/**
	 * Il metodo fornisce l'indirizzo di spedizione presso cui spedire l'ordine.
	 * 
	 * @return indirizzoSpedizione : l'indirizzo di spedizione fornito dall'utente per far
	 * 								recapitare l'ordine
	 * */
	
	public String getIndirizzoSpedizione() {
		return indirizzoSpedizione;
	}
	
	/**
	 * Il metodo imposta l'indirizzo di spedizione per un ordine.
	 * @param indirizzoSpedizione: l'indirizzo di spedizione fornito dall'utente per far
	 * 								recapitare l'ordine
	 * */
	
	public void setIndirizzoSpedizione(Indirizzo indirizzoSpedizione) {
		this.indirizzoSpedizione = indirizzoSpedizione.toString();
	}
	
	/**
	 * Il metodo imposta l'indirizzo di spedizione per un ordine in formato stringa.
	 * 
	 * @param indirizzoSpedizione: l'indirizzo di spedizione fornito dall'utente per far
	 * 								recapitare l'ordine,espresso come oggetto della classe String
	 * */
	
	public void setIndirizzoSpedizioneString(String indirizzoSpedizione) {
		this.indirizzoSpedizione = indirizzoSpedizione;
	}
	
	
	/**
	 * Il metodo fornisce la tipologia di spedizione da applicare all'ordine.
	 * 
	 * @return spedizione : la tipologia di spedizione scelta dall'utente per il suo ordine.
	 * */
	
	public TipoSpedizione getSpedizione() {
		return spedizione;
	}
	
	/**
	 * Il metodo imposta la tipologia di spedizione ad un ordine.
	 * 
	 * @param spedizione : tipo di spedizione preferita dall'utente per il suo ordine
	 * */
	public void setSpedizione(TipoSpedizione spedizione) {
		this.spedizione = spedizione;
	}
	
	/**
	 * Il metodo fornisce la tipologia di spedizione da applicare all'ordine,
	 * sottoforma di stringa.
	 * 
	 * @return la tipologia di spedizione scelta dall'utente per il suo ordine
	 * 			come oggetto della classe String.
	 * */
	
	public String getSpedizioneAsString() {
		return spedizione.toString();
	}
	
	/**
	 * Il metodo imposta la tipologia di spedizione ad un ordine, fornita sottoforma di stringa.
	 * 
	 * @param spedizione : tipo di spedizione preferita 
	 * 						dall'utente per il suo ordine espressa come oggetto di String.
	 * @throws ErroreTipoSpedizioneException : per gestire l'errata specifica della
	 * 											modalità di spedizione
	 * */
	
	public void setSpedizioneAsString(String spedizione) throws ErroreTipoSpedizioneException {
		switch (spedizione.toUpperCase()) {
		case "SPEDIZIONE_STANDARD", "SPEDIZIONE STANDARD":
			this.spedizione = TipoSpedizione.Spedizione_standard;
		break;
		case "SPEDIZIONE_PRIME", "SPEDIZIONE PRIME":
			this.spedizione = TipoSpedizione.Spedizione_prime;
		break;
		case "SPEDIZIONE_ASSICURATA", "SPEDIZIONE ASSICURATA":
			this.spedizione = TipoSpedizione.Spedizione_assicurata;
			break;
		default:
			throw new ErroreTipoSpedizioneException("La spedizione da associare ad un ordine puo\' essere:\n-Spedizione standard;"
					+ "\n-Spedizione prime;\n-Spedizione assicurata.");
		}
	}
	
	/**
	 * Il metodo fornisce la tipologia di consegna da applicare all'ordine.
	 * 
	 * @return consegna : la tipologia di consegna scelta dall'utente per il suo ordine.
	 * */
	
	public TipoConsegna getConsegna() {
		return consegna;
	}
	
	/**
	 * Il metodo imposta la tipologia di consegna ad un ordine.
	 * 
	 * @param consegna : tipo di consegna preferita dall'utente per il suo ordine
	 * */
	public void setConsegna(TipoConsegna consegna) {
		this.consegna = consegna;
	}
	
	/**
	 * Il metodo fornisce la tipologia di consegna da applicare all'ordine,
	 * sottoforma di stringa.
	 * 
	 * @return la tipologia di consegna scelta dall'utente per il suo ordine
	 * 			come oggetto della classe String.
	 * */
	
	public String getConsegnaAsString() {
		return consegna.toString();
	}
	
	/**
	 * Il metodo imposta la tipologia di consegna ad un ordine, fornita sottoforma di stringa.
	 * 
	 * @param spedizione : tipo di consegna preferita 
	 * 						dall'utente per il suo ordine espressa come oggetto di String.
	 * */
	
	public void setConsegnaAsString(String consegna) {
		switch (consegna.toUpperCase()) {
		case "DOMICILIO":
			this.consegna = TipoConsegna.Domicilio;
		break;
		case "PUNTO_RITIRO", "PUNTO RITIRO":
			this.consegna = TipoConsegna.Punto_ritiro;
		break;
		case "PRIORITY":
			this.consegna = TipoConsegna.Priority;
			break;
		default:
			throw new IllegalArgumentException("La consegna da associare ad un ordine puo\' essere:\n- Presso domicilio;"
					+ "\n-Presso punto di ritiro;\n-Priority/fascia oraria.");
		}
	}
	
	
	/**
	 * Il metodo restituisce la data di creazione dell'ordine fatta dall'utente
	 * dopo il pagamento della merce.
	 * 
	 * @return data : data di commissione dell'ordine
	 * */
	
	public LocalDate getData() {
		return data;
	}
	
	/**
	 * Il metodo imposta la data di creazione dell'ordine fatta dall'utente
	 * dopo il pagamento della merce.
	 * 
	 * @param data: data di commissione dell'ordine
	 * */
	
	public void setData(LocalDate data) {
		this.data = data;
	}
	
	/**
	 * Il metodo restituisce l'ora di creazione dell'ordine fatta dall'utente
	 * dopo il pagamento della merce.
	 * 
	 * @return ora : ora di commissione dell'ordine
	 * */
	
	public LocalTime getOra() {
		return ora;
	}
	
	/**
	 * Il metodo imposta l'ora di creazione dell'ordine fatta dall'utente
	 * dopo il pagamento della merce.
	 * 
	 * @param ora: ora di commissione dell'ordine
	 * */
	
	public void setOra(LocalTime ora) {
		this.ora = ora;
	}
	
	/**
	 * Il metodo fornisce, in formato stringa, le caratteristiche essenziali di
	 * un oggetto di tipo ObjectOrdine.
	 * 
	 * @return una stringa contenente le informazioni associate ad un oggetto
	 * 			di tipo ObjectOrdine 
	 * */
	
	@Override
	public String toString() {
		return "Ordine [CodiceOrdine=" + codiceOrdine + ", stato=" + stato 
				+ ", tipo di spedizione=" + spedizione + ", indirizzo di spedizione: \n" + indirizzoSpedizione.toString()
				+ ", tipo di consegna=" + consegna
				+ "\n Data e ora commissione=" + data + ", " + ora + "]";
	}
	
	/**
	 * Il metodo crea un nuovo oggetto `ObjectOrdine` con gli stessi valori
	 * degli attributi di questo oggetto. Si tratta di una copia profonda, ovvero anche gli
	 * oggetti contenuti all'interno vengono clonati, evitando di condividere riferimenti.
	 *
	 * @return clone : una nuova istanza di `ObjectOrdine` che rappresenta una copia esatta di questo oggetto.
	 * @throws AssertionError Se il metodo `clone()` di `Object` lancia una eccezione inaspettata.
	 */
	
	@Override
	public ObjectOrdine clone() throws CloneNotSupportedException{
	    try {
	        ObjectOrdine clone = (ObjectOrdine) super.clone();
	        // Copia profonda degli attributi
	        
	        clone.stato = this.stato;
	        clone.indirizzoSpedizione = new String(this.indirizzoSpedizione); // Copia della stringa
	        clone.spedizione = this.spedizione;
	        clone.data = this.data;
	        clone.ora = this.ora;
	        clone.consegna=this.consegna;
	        clone.codiceOrdine = this.codiceOrdine;
	        
	        return clone;
	    } catch (CloneNotSupportedException e) {
	    	
	        throw new AssertionError();
	    }
	}
	
}
