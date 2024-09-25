package application.RegistrazioneService;

/**
 * Questa classe fornisce metodi per creare, accedere e modificare gli attributi di un indirizzo
 * associato ad un utente.
 * Inoltre, include metodi di validazione per verificare la correttezza dei dati inseriti.
 * 
 * @author Dorotea Serrelli
 */

public class Indirizzo implements Cloneable{
	
	/**
	 * IDIndirizzo : identificativo dell'indirizzo postale.
	 * Un utente può avere più indirizzi postali.
	 * */
	private int IDIndirizzo = 0;
	
	/**
	 * via : la via
	 * */
	private String via = "";
	
	/**
	 * numCivico : il numero civico (si comprende il caso dei numeri civici di 
	 * condomini, formati da numeri e lettere)
	 * */
	private String numCivico = "";
	
	/**
	 * citta : la città
	 * */
	private String citta = "";
	
	/**
	 * cap : CAP
	 * */
	private String cap = "";
	
	/**
	 * provincia : la provincia formata da due lettere maiuscole
	 * */
	private String provincia = "";
	
	/**
	 * Il metodo verifica la validità di un indirizzo postale, ovvero  
	 * controlla che i parametri passati rispettino i seguenti pattern di validazione:
	 * - Via: solo lettere e spazi
	 * - Numero civico: può contenere numeri, lettere e alcuni caratteri speciali
	 * - Città: solo lettere e spazi
	 * - CAP: esattamente 5 cifre numeriche
	 * - Provincia: solo due lettere maiuscole
	 *
	 * @param via: La via dell'indirizzo.
	 * @param numCivico: Il numero civico dell'indirizzo.
	 * @param citta: La città dell'indirizzo.
	 * @param cap: Il CAP dell'indirizzo.
	 * @param provincia: La provincia dell'indirizzo.
	 * 
	 * @return true se l'indirizzo è valido; false altrimenti.
	 */
	
	public static boolean checkValidate(String via, String numCivico, String citta, String cap, String provincia) {
		String viaPattern = "^[A-Za-z\\s]+$" ;
		String numCivicoPattern = "^(([0-9])|(([0-9]+|\\w)(\\w|[0-9]+)))$";
		String cittaPattern = "^[A-Za-z\\s]+$";
		String capPattern = "^\\d{5}$";
		String provinciaPattern = "^[A-Za-z]{2}$";
		
		return (!via.matches(viaPattern) || !numCivico.matches(numCivicoPattern) ||
				!citta.matches(cittaPattern) || !cap.matches(capPattern) || 
				!provincia.matches(provinciaPattern)) ? false : true;
	}
	
	/**
	 * Il metodo verifica la validità di un oggetto Indirizzo, 
	 * delegando la verifica dei singoli campi dell'indirizzo al metodo 
	 * {@link #checkValidate(String, String, String, String, String)} passando i valori
	 * ottenuti dai getter dell'oggetto Indirizzo.
	 *
	 * @param indirizzo: L'oggetto Indirizzo da validare.
	 * 
	 * @return true se tutti i campi di indirizzo sono validi; false altrimenti.
	 */
	
	public static boolean checkValidate(Indirizzo indirizzo) {
		return checkValidate(indirizzo.getVia(), indirizzo.getNumCivico(), 
				indirizzo.getCitta(), indirizzo.getCap(), indirizzo.getProvincia());
	}
	
	/**
	 * Metodo costruttore per creare una nuova istanza di un indirizzo postale.
	 * Si crea, dunque, un oggetto della classe Indirizzo che ha i seguenti 
	 * attributi : iDIndirizzo, via, numCivico, citta, cap, provincia.
	 *
	 * @param iDIndirizzo : L'identificativo univoco dell'indirizzo.
	 * @param via : La via dell'indirizzo.
	 * @param numCivico : Il numero civico dell'indirizzo.
	 * @param citta: La città dell'indirizzo.
	 * @param cap: Il codice postale dell'indirizzo.
	 * @param provincia: La provincia dell'indirizzo.
	 * 
	 */
	
	public Indirizzo(int iDIndirizzo, String via, String numCivico, String citta, String cap, String provincia) {
		IDIndirizzo = iDIndirizzo;
		this.via = via;
		this.numCivico = numCivico;
		this.citta = citta;
		this.cap = cap;
		this.provincia = provincia;
	}
	
	/**
	 * Metodo costruttore per creare una nuova istanza di un indirizzo postale.
	 * Si crea, dunque, un oggetto della classe Indirizzo che ha i seguenti 
	 * attributi : via, numCivico, citta, cap, provincia.
	 *
	 * @param via: La via dell'indirizzo.
	 * @param numCivico: Il numero civico dell'indirizzo.
	 * @param citta: La città dell'indirizzo.
	 * @param cap: Il codice postale dell'indirizzo.
	 * @param provincia: La provincia dell'indirizzo.
	 * 
	 */
	
	public Indirizzo(String via, String numCivico, String citta, String cap, String provincia) {
		this.via = via;
		this.numCivico = numCivico;
		this.citta = citta;
		this.cap = cap;
		this.provincia = provincia;
	}
	
	/**
	 * Il metodo fornisce l'identificativo univoco dell'indirizzo.
	 * 
	 * @return IDIndirizzo : ID dell'indirizzo
	 * */

	public int getIDIndirizzo() {
		return IDIndirizzo;
	}
	
	/**
	 * Il metodo imposta l'identificativo univoco dell'indirizzo.
	 * 
	 * @param iDIndirizzo : codice univoco dell'indirizzo
	 * */
	
	public void setIDIndirizzo(int iDIndirizzo) {
		IDIndirizzo = iDIndirizzo;
	}
	
	/**
	 * Il metodo fornisce la via dell'indirizzo.
	 * 
	 * @return via : la via dell'indirizzo postale
	 * */
	
	public String getVia() {
		return via;
	}
	
	/**
	 * Il metodo imposta la via dell'indirizzo.
	 * 
	 * @param via : via dell'indirizzo postale
	 * */
	
	public void setVia(String via) {
		this.via = via;
	}
	
	/**
	 * Il metodo fornisce il numero civico dell'indirizzo.
	 * 
	 * @return numCivico : il numero civico dell'indirizzo postale
	 * */
	
	public String getNumCivico() {
		return numCivico;
	}
	
	/**
	 * Il metodo imposta il numero civico dell'indirizzo.
	 * 
	 * @param numCivico : il numero civico dell'indirizzo postale
	 * */
	
	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico;
	}
	
	/**
	 * Il metodo fornisce la città dell'indirizzo.
	 * 
	 * @return citta : la città dell'indirizzo postale
	 * */
	
	public String getCitta() {
		return citta;
	}
	
	/**
	 * Il metodo imposta la città dell'indirizzo.
	 * 
	 * @param citta : la città dell'indirizzo postale
	 * */
	
	public void setCitta(String citta) {
		this.citta = citta;
	}
	
	/**
	 * Il metodo fornisce il CAP dell'indirizzo.
	 * 
	 * @return cap : il CAP dell'indirizzo postale
	 * */
	
	public String getCap() {
		return cap;
	}
	
	/**
	 * Il metodo imposta il CAP dell'indirizzo.
	 * 
	 * @param CAP : il CAP dell'indirizzo postale
	 * */
	
	public void setCap(String CAP) {
		this.cap = CAP;
	}
	
	/**
	 * Il metodo fornisce la provincia presente nell'indirizzo.
	 * 
	 * @return provincia : la provincia dell'indirizzo postale
	 * */
	
	public String getProvincia() {
		return provincia;
	}
	
	/**
	 * Il metodo imposta la provincia presente nell'indirizzo.
	 * 
	 * @param provincia : la provincia dell'indirizzo postale
	 * */
	
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	/**
	 * Il metodo fornisce, in formato stringa, le informazioni peculiari
	 * di un indirizzo.
	 *
	 * @return Una stringa che rappresenta l'indirizzo nel formato
	 *         "Indirizzo [via=..., numCivico=..., città=..., cap=..., provincia=...]".
	 */
	
	@Override
	public String toString() {
		return "Indirizzo [via=" + via + ", numCivico=" + numCivico + ", città=" + citta + ", cap=" + cap
				+ ", provincia=" + provincia + "]";
	}
	
	/**
	 * Il metodo crea una copia dell'oggetto Indirizzo.
	 * 
	 * @return Una copia dell'oggetto Indirizzo.
	 * @throws AssertionError Se si verifica un'eccezione CloneNotSupportedException
	 */
	@Override
	public Indirizzo clone() throws CloneNotSupportedException{
	    try {
	        return (Indirizzo) super.clone();
	    } catch (CloneNotSupportedException e) {
	        throw new AssertionError();
	    }
	}

}
