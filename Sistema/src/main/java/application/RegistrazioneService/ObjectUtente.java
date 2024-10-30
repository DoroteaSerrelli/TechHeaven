package application.RegistrazioneService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import application.RegistrazioneService.RegistrazioneException.FormatoPasswordException;
import application.RegistrazioneService.RegistrazioneException.FormatoUsernameException;

/**
 * Questa classe astratta è la classe client del design pattern Proxy
 * utilizzato per l'accesso e la gestione delle operazioni sull'oggetto Utente.
 * 
 * @see application.RegistrazioneService.Utente
 * @see application.RegistrazioneService.ProxyUtente
 * @see application.RegistrazioneService.Ruolo
 * 
 * @author Dorotea Serrelli
 * */

public abstract class ObjectUtente implements Cloneable{

	/**
	 * Username : il nome utente che identifica univocamente l'utente
	 * */
	protected String username;

	/**
	 * Password : la password dell'utente (crittografata mediante SHA-512)
	 * */
	protected String password;

	/**
	 * Ruoli : l'insieme dei ruoli associati a quell'utente (accesso alle pagine web)
	 * */
	protected ArrayList<Ruolo> ruoli;


	/**
	 * Costruttore di classe di default.
	 * */
	public ObjectUtente() {}

	/**
	 * Questo costruttore inizializza un oggetto Utente con username, 
	 * password e ruoli definiti.
	 * 
	 * @param u : username;
	 * @param p : password;
	 * @param r : ruolo da associare all'utente;
	 * 
	 * */

	public ObjectUtente(String u, String p, Ruolo r) {
		username = u;
		password = hashPassword(p).toString();
		ruoli = new ArrayList<>();
		ruoli.add(r);
	}

	/**
	 * Questo costruttore inizializza un oggetto Utente con username, 
	 * password e ruoli definiti.
	 * 
	 * @param u : username;
	 * @param p : password;
	 * @param r : ruoli da associare all'utente;
	 * 
	 * */

	public ObjectUtente(String u, String p, ArrayList<Ruolo> r) {
		username = u;
		password = hashPassword(p).toString();
		ruoli = r;
	}

	/**
	 * Questo costruttore inizializza un oggetto Utente (con ruolo Cliente) 
	 * con username e password definite.
	 * 
	 * @param u : username;
	 * @param p : password;
	 * 
	 * */
	
	public ObjectUtente(String u, String p) {
		username = u;
		password = hashPassword(p).toString();
		ruoli = new ArrayList<>();
		ruoli.add(new Ruolo("Cliente"));
	}

	/**
	 * In questo metodo si verifica se le credenziali inserite dall'utente sono valide.
	 * 
	 * @param username : il nome utente
	 * @param password : la password dell'utente
	 * 
	 * @return true se le credenziali username e password sono corrette; false altrimenti.
	 * @throws FormatoUsernameException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  l'username non rispettando il formato.
	 * @throws FormatoPasswordException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  la password non rispettando il formato.
	 * */
	public static boolean checkValidate(String username, String password) throws FormatoUsernameException, FormatoPasswordException {

		String usernamePattern = "^[a-zA-Z]{5,}$";
		String passwordPattern = "^(?=.*[a-zA-Z])(?=.*[0-9]).{5,}$";

		if(!username.matches(usernamePattern)) {
			throw new FormatoUsernameException("L'username deve avere almeno lunghezza pari a 5 e contenere solo lettere.");
		}else if(password.length() < 5 || password.isBlank() || !password.matches(passwordPattern))
			throw new FormatoPasswordException("La password deve avere almeno 5 caratteri che siano lettere e numeri.");

		return true;
	}
	
	/**
	 * In questo metodo si verifica se l'username inserita dall'utente è corretta nel formato.
	 * 
	 * @param username : il nome utente
	 * 
	 * @return true se username è corretta; false altrimenti.
	 * @throws FormatoUsernameException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  l'username non rispettando il formato.
	 **/
	
	public static boolean checkUsername(String username) throws FormatoUsernameException, FormatoPasswordException {

		String usernamePattern = "^[a-zA-Z]{5,}$";

		if(!username.matches(usernamePattern))
			throw new FormatoUsernameException("L'username deve avere almeno lunghezza pari a 5 e contenere solo lettere.");

		return true;
	}
	
	/**
	 * In questo metodo si verifica se la password inserita dall'utente è valida nel formato.
	 * 
	 * @param password : la password dell'utente
	 * 
	 * @return true se la password è corretta; false altrimenti.
	 * 
	 * @throws FormatoPasswordException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  la password non rispettando il formato.
	 * */
	public static boolean checkPassword(String password) throws FormatoUsernameException, FormatoPasswordException {

		
		String passwordPattern = "^(?=.*[a-zA-Z])(?=.*[0-9]).{5,}$";

		if(password.length() < 5 || password.isBlank() || !password.matches(passwordPattern))
			throw new FormatoPasswordException("La password deve avere almeno 5 caratteri che siano lettere e numeri.");

		return true;
	}
	
	
	/**
	 * Questo metodo verifica se la nuova password inserita dall'utente è valida.
	 * 
	 * @param password : la password dell'utente
	 * 
	 * @return true se la password è corretta; false altrimenti.
	 * */
	public static boolean checkResetPassword(String password) {

		String passwordPattern = "^(?=.*[a-zA-Z])(?=.*[0-9]).{5,}$";

		return !(password.length() < 5 || password.isBlank() || !password.matches(passwordPattern));
	}


	/**
	 * Il metodo effettua l'hashing SHA-512 della password dell'utente.
	 * In tal modo le password non vengono memorizzate in chiaro nel Database.
	 * 
	 * @param password : la password dell'utente
	 * 
	 * @return hashString: l'output della funzione hash SHA-512 fornito in 
	 * 			input la password password dell'utente.
	 * 
	 * */
	
	private StringBuilder hashPassword(String password) {
		StringBuilder hashString = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			for (int i = 0; i < bytes.length; i++) {
				hashString.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toLowerCase(), 1, 3);
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}
		return hashString;
	}

	/**
	 * Il metodo restituisce l'username dell'utente.
	 * 
	 * @return username : il nome utente
	 * */
	
	public String getUsername() {
		return username;
	}

	/**
	 * Il metodo imposta l'username dell'utente.
	 * 
	 * @param username : il nome utente
	 * */

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Il metodo restituisce la password dell'utente.
	 * 
	 * @return password : la password dell'utente
	 * */

	public String getPassword() {
		return password;
	}

	/**
	 * Il metodo imposta la password dell'utente già cifrata.
	 * Vedasi metodo hashPassword della classe ObjectUtente.
	 * 
	 * @see application.RegistrazioneService.ObjectUtente
	 * 
	 * @param password : la password cifrata in SHA-512 
	 * */

	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Il metodo imposta la password dell'utente non ancora cifrata.
	 * Si utilizza per la cifratura della password il metodo hashPassword
	 * in presente nella classe ObjectUtente.
	 * 
	 * @see application.RegistrazioneService.ObjectUtente
	 * 
	 * @param p : la password dell'utente in chiaro, da cifrare in SHA-512 
	 * */

	public void setPasswordToHash(String p) {
		this.password = hashPassword(p).toString();
	}

	/**
	 * Il metodo restituisce i ruoli associati all'utente.
	 * 
	 * @return ruoli: i ruoli associati all'utente
	 * */
	public ArrayList<Ruolo> getRuoli() {
		return ruoli;
	}

	/**
	 * Il metodo imposta i ruoli di un utente.
	 * 
	 * @param ruoli : i ruoli dell'utente
	 * 
	 * */

	public void setRuoli(ArrayList<Ruolo> ruoli) {
		this.ruoli = ruoli;
	}

	/**
	 * Questo metodo aggiunge un nuovo ruolo all'utente.
	 * 
	 * @param r : il nuovo ruolo da aggiungere all'utente
	 * 
	 * @return ruoli : una lista dei ruoli associati all'utente, comprendente il ruolo r
	 * */
	public ArrayList<Ruolo> addRuolo(Ruolo r) {
		ruoli.add(r);
		return ruoli;
	}
	
	/**
	 * Il metodo crea una copia esatta di questo oggetto ObjectUtente: crea un nuovo oggetto ObjectUtente 
	 * con gli stessi valori degli attributi di questo oggetto. 
	 * Si tratta di una copia profonda, ovvero anche
	 * l'elenco dei ruoli viene clonato per evitare di condividere riferimenti con l'oggetto originale.
	 *
	 * @return Una nuova istanza di ObjectUtente che rappresenta una copia esatta di questo oggetto.
	 * @throws AssertionError Se il metodo `clone()` di `Object` lancia un'eccezione inaspettata.
	 * 
	 */
	@Override
	public ObjectUtente clone() throws CloneNotSupportedException{
	    try {
	        ObjectUtente clone = (ObjectUtente) super.clone();
	        clone.username = this.username;
	        clone.password = this.password;
	        clone.ruoli = new ArrayList<>();
	        for (Ruolo ruolo : ruoli) {
	            clone.ruoli.add(ruolo);
	        }
	        return clone;
	    } catch (CloneNotSupportedException e) {
	        throw new AssertionError();
	    }
	}
	
	/**
	 * Il metodo confronta due oggetti ObjectUtente per verificare se sono considerati
	 * equivalenti. Due oggetti sono considerati uguali se hanno lo stesso username e
	 * la stessa password. Inoltre, vengono confrontati anche i ruoli associati all'utente.
	 *
	 * @param obj L'oggetto da confrontare con l'istanza corrente.
	 * @return true se gli oggetti sono considerati uguali, false altrimenti.
	 */
	@Override
	public boolean equals(Object obj) {
	    
	    if (this == obj) {
	        return true;
	    }
	    
	    if (obj == null || getClass() != obj.getClass()) {
	        return false;
	    }
	    
	    ObjectUtente other = (ObjectUtente) obj;

	    // Confronto degli attributi username e password
	    boolean usernameEquals = (username != null) ? username.equals(other.username) : other.username == null;
	    boolean passwordEquals = (password != null) ? password.equals(other.password) : other.password == null;

	    // Confronto dei ruoli
	    boolean ruoliEquals = (ruoli != null) ? ruoli.equals(other.ruoli) : other.ruoli == null;

	    // Ritorna true solo se tutti i confronti sono veri
	    return usernameEquals && passwordEquals && ruoliEquals;
	}

}
