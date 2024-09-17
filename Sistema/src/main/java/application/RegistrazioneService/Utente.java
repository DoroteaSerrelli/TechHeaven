package application.RegistrazioneService;

/**
 * Questa classe rappresenta un utente del sistema, per il quale si conoscono
 * username, password, 
 * ruoli associati e profilo personale.
 * 
 * @see application.RegistrazioneService.ObjectUtente
 * @see application.RegistrazioneService.Cliente
 * @see application.RegistrazioneService.Ruolo
 * 
 * @author Dorotea Serrelli
 * 
 */

public class Utente extends ObjectUtente implements Cloneable{
	
	/**
	 * profile : riferimento al profilo dell'utente, contenente le informazioni
	 * personali.
	 * */
	private Cliente profile;
	
	
	/**
     * Il metodo crea un nuovo oggetto Utente con il ruolo di cliente,
     * date le credenziali di accesso e le informazioni
	 * personali : nome, cognome, sesso, email, numero di telefono, indirizzo..
     *
     * @param username: Il nome utente.
     * @param password: La password.
     * @param profile: Il profilo cliente associato all'utente.
     * 
     * @return un oggetto della classe Utente avente come attributi
     * 			username, password e profile.
     */

	public Utente(String username, String password, Cliente profile) {
		super(username, password);
		this.profile = profile;
	}
	
	/**
     * Il metodo crea un nuovo oggetto Utente con i ruoli di cliente e role,
     * date le credenziali di accesso e le informazioni
	 * personali : nome, cognome, sesso, email, numero di telefono, indirizzo.
     *
     * @param username: Il nome utente.
     * @param password: La password.
     * @param profile: Il profilo cliente associato all'utente.
     * @param role : un ruolo aggiuntivo oltre a quello di cliente
     * 
     * @return un oggetto della classe Utente avente come attributi
     * 			username, password, profile e role.
     * 
     */
	
	public Utente(String username, String password, Cliente profile, Ruolo role) {
		super(username, password, role);
		this.profile = profile;
	}
	
	/**
	 * Il metodo fornisce il profilo dell'utente.
	 * 
	 * @return profile : informazioni personali dell'utente
	 * */
	
	public Cliente getProfile() {
		return profile;
	}
	
	/**
	 * Il metodo imposta il profilo dell'utente.
	 * 
	 * @param profile : informazioni personali dell'utente
	 * */
	public void setProfile(Cliente profile) {
		this.profile = profile;
	}
	
	/**
     * Il metodo restituisce una rappresentazione stringa dell'oggetto Utente.
     * La rappresentazione include il nome utente, la password e le 
     * informazioni del profilo cliente.
     *
     * @return Una stringa contenente le informazioni di un utente.
     */
	@Override
	public String toString() {
		return "Utente [username=" + username + ", password=" + password + ", profile=" + profile.toString() + "]";
	}
	
	/**
	 * Il metodo crea una copia profonda dell'oggetto Utente:
	 * Sia gli attributi primitivi che l'oggetto profilo
	 * vengono copiati in modo profondo, garantendo che le modifiche apportate alla
	 * copia non influenzino l'oggetto originale.
	 *
	 * @return clone : Una copia profonda dell'oggetto Utente.
	 * @throws RuntimeException se si verifica un errore durante la clonazione.
	 */
	@Override
	public Utente clone() throws CloneNotSupportedException{
	    Utente clone = null;
	    clone = (Utente) super.clone();
		clone.profile = profile.clone();
	    return clone;
	}

}
