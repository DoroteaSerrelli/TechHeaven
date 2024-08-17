package application.GestioneOrdiniService;

import java.time.LocalDate;
import java.time.LocalTime;

import application.RegistrazioneService.Indirizzo;

/**
 * Questa classe astratta è la classe client del design pattern Proxy
 * utilizzato per l'accesso e la gestione delle operazioni sull'oggetto Ordine.
 * @see application.GestioneOrdiniService.Ordine
 * @see application.GestioneOrdiniService.ProxyOrdine
 * 
 * @author Dorotea Serrelli
 * */

public abstract class ObjectOrdine {
	
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
	 * spedizione : la tipologia di spediziondal negozio
	 * verso un'azienda logistica
	 * */
	private TipoSpedizione spedizione;
	
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
	
	public static boolean checkValidate(Indirizzo indirizzoSpedizione) {
		return Indirizzo.checkValidate(indirizzoSpedizione);
	}
	
	/**
	 * Costruttore di classe di default.
	 * */
	
	protected ObjectOrdine() {
		codiceOrdine = 0;
		stato = null;
		indirizzoSpedizione = null;
	}
	
	/**
	 * Costruttore di classe.
	 * @param codice : l'identificativo numerico dell'ordine;
	 * @param stato : lo stato dell'ordine;
	 * @param indirizzoSpedizione : l'indirizzo di spedizione scelto dal committente;
	 * @param spedizione : la tipologia di spedizione scelta dall'utente.
	 * @return un oggetto di tipo ObjectOrdine inizializzato con i parametri di input
	 * e gli attributi data e ora correnti.
	 * */
	protected ObjectOrdine(int codice, Stato stato, Indirizzo indirizzoSpedizione, TipoSpedizione spedizione) {
		codiceOrdine = codice;
		this.stato = stato;
		this.indirizzoSpedizione = indirizzoSpedizione.toString();
		this.spedizione = spedizione;
		data = LocalDate.now();
		ora = LocalTime.now();
	}
	
	/**
	 * Il metodo fornisce il codice dell'ordine.
	 * @return il codice dell'ordine
	 * */
	public int getCodiceOrdine() {
		return codiceOrdine;
	}
	
	/**
	 * Il metodo imposta il codice dell'ordine.
	 * @param codiceOrdine : l'identificativo numerico da associare 
	 * ad un ordine.
	 * */
	public void setCodiceOrdine(int codiceOrdine) {
		this.codiceOrdine = codiceOrdine;
	}
	
	/**
	 * Il metodo restituisce lo stato dell'ordine.
	 * @return lo stato dell'ordine
	 * */
	public Stato getStato() {
		return stato;
	}
	
	/**
	 * Il metodo imposta lo stato di elaborazione dell'ordine.
	 * @param stato : lo stato dell'ordine
	 * */
	public void setStato(Stato stato) {
		this.stato = stato;
	}
	
	/**
	 * Il metodo fornisce lo stato di elaborazione dell'ordine
	 * sottoforma di stringa.
	 * @return lo stato dell'ordine
	 * */
	public String getStatoAsString() {
		return stato.toString();
	}
	
	/**
	 * Imposta lo stato di elaborazione dell'ordine.
	 * @param stato: lo stato dell'ordine espresso come stringa
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
	 * @return l'indirizzo di spedizione fornito dall'utente per far
	 * recapitare l'ordine
	 * */
	public String getIndirizzoSpedizione() {
		return indirizzoSpedizione;
	}
	
	/**
	 * Il metodo imposta l'indirizzo di spedizione per un ordine.
	 * @param indirizzoSpedizione: l'indirizzo di spedizione fornito dall'utente per far
	 * recapitare l'ordine
	 * */
	public void setIndirizzoSpedizione(Indirizzo indirizzoSpedizione) {
		this.indirizzoSpedizione = indirizzoSpedizione.toString();
	}
	
	/**
	 * Il metodo imposta l'indirizzo di spedizione per un ordine.
	 * @param indirizzoSpedizione: l'indirizzo di spedizione fornito dall'utente per far
	 * recapitare l'ordine, in formato stringa
	 * */
	public void setIndirizzoSpedizioneString(String indirizzoSpedizione) {
		this.indirizzoSpedizione = indirizzoSpedizione;
	}
	
	
	/**
	 * Il metodo fornisce la tipologia di spedizione da applicare all'ordine.
	 * @return la tipologia di spedizione scelta dall'utente per il suo ordine.
	 * */
	public TipoSpedizione getSpedizione() {
		return spedizione;
	}
	
	/**
	 * Il metodo imposta la tipologia di spedizione ad un ordine.
	 * @param spedizione : tipo di spedizione preferita dall'utente per il suo ordine
	 * */
	public void setSpedizione(TipoSpedizione spedizione) {
		this.spedizione = spedizione;
	}
	
	/**
	 * Il metodo fornisce la tipologia di spedizione da applicare all'ordine,
	 * sottoforma di stringa.
	 * @return la tipologia di spedizione scelta dall'utente per il suo ordine.
	 * */
	public String getSpedizioneAsString() {
		return spedizione.toString();
	}
	
	/**
	 * Il metodo imposta la tipologia di spedizione ad un ordine.
	 * @param spedizione : tipo di spedizione preferita 
	 * dall'utente per il suo ordine, fornita sottoforma di stringa.
	 * */
	public void setSpedizioneAsString(String spedizione) {
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
			throw new IllegalArgumentException("La spedizione da associare ad un ordine puo\' essere:\n-Spedizione standard;"
					+ "\n-Spedizione prime;\n-Spedizione assicurata.");
		}
	}
	
	/**
	 * Il metodo restituisce la data di creazione dell'ordine fatta dall'utente
	 * dopo il pagamento della merce.
	 * @return data di commissione dell'ordine
	 * */
	public LocalDate getData() {
		return data;
	}
	
	/**
	 * Il metodo imposta la data di creazione dell'ordine fatta dall'utente
	 * dopo il pagamento della merce.
	 * @param data: data di commissione dell'ordine
	 * */
	public void setData(LocalDate data) {
		this.data = data;
	}
	
	/**
	 * Il metodo restituisce l'ora di creazione dell'ordine fatta dall'utente
	 * dopo il pagamento della merce.
	 * @return ora di commissione dell'ordine
	 * */
	public LocalTime getOra() {
		return ora;
	}
	
	/**
	 * Il metodo imposta l'ora di creazione dell'ordine fatta dall'utente
	 * dopo il pagamento della merce.
	 * @param ora: ora di commissione dell'ordine
	 * */
	public void setOra(LocalTime ora) {
		this.ora = ora;
	}
	
	/**
	 * Il metodo che fornisce lo stato di un oggetto di tipo ObjectOrdine.
	 * @return una stringa contenente le informazioni associate ad un oggetto
	 * di tipo ObjectOrdine 
	 * */
	@Override
	public String toString() {
		return "Ordine [CodiceOrdine=" + codiceOrdine + ", stato=" + stato 
				+ ", tipo di spedizione=" + spedizione + ", indirizzo di spedizione: \n" + indirizzoSpedizione.toString()
				+ "\n Data e ora commissione=" + data + ", " + ora + "]";
	}
	
	/**
	 * Il metodo crea un nuovo oggetto `ObjectOrdine` con gli stessi valori
	 * degli attributi di questo oggetto. Si tratta di una copia profonda, ovvero anche gli
	 * oggetti contenuti all'interno vengono clonati, evitando di condividere riferimenti.
	 *
	 * @return Una nuova istanza di `ObjectOrdine` che rappresenta una copia esatta di questo oggetto.
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
	        clone.codiceOrdine = this.codiceOrdine;
	        
	        return clone;
	    } catch (CloneNotSupportedException e) {
	    	
	        throw new AssertionError();
	    }
	}
	
}
