package application.RegistrazioneService;

public class Utente extends ObjectUtente{
	
	/*
	 * Questa classe detiene le informazioni relative all'utente:
	 * username, password, ruoli associati e profilo personale.
	 * */
	
	private Cliente profile;
	
	/*
	 * Questo metodo crea un nuovo oggetto Utente (con ruolo cliente) date le credenziali di accesso e le informazioni
	 * personali : nome, cognome, sesso, email, numero di telefono, indirizzo.
	 * */
	public Utente(String username, String password, Cliente profile) {
		super(username, password);
		this.profile = profile;
	}
	
	/*
	 * Questo metodo crea un nuovo oggetto Utente (con ruolo cliente e role) date le credenziali di accesso e le informazioni
	 * personali : nome, cognome, sesso, email, numero di telefono, indirizzo.
	 * */
	public Utente(String username, String password, Cliente profile, Ruolo role) {
		super(username, password, role);
		this.profile = profile;
	}
	
	/*
	 * Metodi GETTER E SETTER
	 * */
	public Cliente getProfile() {
		return profile;
	}
	public void setProfile(Cliente profile) {
		this.profile = profile;
	}

	@Override
	public String toString() {
		return "Utente [username=" + username + ", password=" + password + ", profile=" + profile.toString() + "]";
	}
}
