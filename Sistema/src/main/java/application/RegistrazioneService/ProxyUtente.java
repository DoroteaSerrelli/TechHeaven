package application.RegistrazioneService;

import java.sql.SQLException;
import java.util.ArrayList;
import storage.AutenticazioneDAO.UtenteDAODataSource;

/**
 * La classe permette di controllare l'accesso ad un oggetto della classe Utente, 
 * agendo come surrogato o interfaccia di sostituzione a questo oggetto nel design pattern Proxy. 
 * ProxyUtente agisce per conto di Utente, memorizzando un sottoinsieme degli attributi di Prodotto 
 * (quelli offerti da ObjectUtente) e gestisce completamente le richieste che non richiedono la conoscenza delle
 * informazioni del cliente, eccetto ruoli, username e password.
 * ProxyUtente ha un riferimento ad un oggetto Utente, in modo che tutte le richieste legate alla manipolazione dei dati personali
 * (accesso area riservata, modifica dati personali, check-out carrello, ...), vengono delegate a Utente.
 * Dopo la delega, viene creato l'oggetto Utente e caricato in memoria.
 * @see application.RegistrazioneService.ObjectUtente
 * @see application.RegistrazioneService.Utente
 * 
 * @author Dorotea Serrelli
 * */

public class ProxyUtente extends ObjectUtente{
	
	private Utente realUtente;
	
	/**
	 * Costruttore di classe per creare un oggetto ProxyUtente noti username, password e ruoli associati.
	 * @param username : il nome utente
	 * @param password 
	 * @param ruoli i ruoli che possiede l'utente
	 * */
	public ProxyUtente(String username, String password, ArrayList<Ruolo> ruoli) {
        super(username, password, ruoli);
    }
	
	/**
	 * Costruttore di classe per creare un oggetto ProxyUtente noti username e password.
	 * @param username : il nome utente
	 * @param password
	 * */
	public ProxyUtente(String username, String password) {
        super(username, password);
    }
	
	/**
	 * Il metodo fornisce il riferimento all'oggetto Utente.
	 * Se non è presente questo riferimento, allora si crea tale oggetto e se ne mantiene in memoria
	 * il riferimento.
	 * @return l'oggetto Utente che possiede le informazioni personali dell'utente
	 * */
	public Utente mostraUtente() {
		if(realUtente == null) {
			UtenteDAODataSource userDao = new UtenteDAODataSource();
			try {
				Utente real = userDao.doRetrieveFullUserByKey(username);
				realUtente = real;
			} catch (SQLException e) {
				System.out.println("Errore nel recupero del profilo e degli ordini dell'utente");
			}
		}
		return realUtente;
	}
}