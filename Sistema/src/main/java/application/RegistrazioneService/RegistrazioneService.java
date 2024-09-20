package application.RegistrazioneService;

import application.RegistrazioneService.Cliente.Sesso;

/**
 * Interfaccia che si occupa di offrire servizi relativi alla
 * registrazione di un nuovo utente: cliente, gestore degli ordini e 
 * gestore del catalogo.
 * 
 * @see application.RegistrazioneService.RegistrazioneServiceImpl
 * @see application.RegistrazioneService.ProxyUtente
 * @see application.RegistrazioneService.Ruolo
 * @see application.RegistrazioneService.Indirizzo
 * @see application.RegistrazioneService.Cliente
 * 
 * @author Dorotea Serrelli
 * */

public interface RegistrazioneService {

	/**
	 * Questo metodo permette di registrare un nuovo cliente al sistema.
	 * 
	 * @param username: il nome utente scelto dal cliente
	 * @param password: la password scelta dal cliente
	 * @param email: l'indirizzo di posta elettronica del cliente
	 * @param nome: il nome del cliente
	 * @param cognome: il cognome dell'utente
	 * @param sex: il genere del cliente
	 * @param telefono: il numero di telefono del cliente
	 * @param indirizzo: l'indirizzo di spedizione del cliente
	 * 
	 * @return un oggetto ProxyUtente che contiene le seguenti informazioni del nuovo cliente: username, password e 
	 * 			ruoli (in questo caso possiede solo il ruolo Cliente).
	 * */
	ProxyUtente registraCliente(String username, String password, String email, String nome, String cognome, 
			Sesso sex, String telefono, Indirizzo indirizzo);
	
	/**
	 * Il metodo permette di registrare un nuovo utente come gestore degli ordini al sistema.
	 * 
	 * @param username: il nome utente scelto dal gestore
	 * @param password: la password scelta dal gestore
	 * @param email: l'indirizzo di posta elettronica del gestore
	 * @param nome: il nome del gestore
	 * @param cognome: il cognome del gestore
	 * @param sex: il genere del gestore
	 * @param telefono: il numero di telefono del gestore
	 * @param indirizzo: l'indirizzo di spedizione del gestore
	 * @param isOrderManager: è il ruolo "GestoreOrdini" da associare all'utente
	 * 
	 * @return un oggetto ProxyUtente che contiene le seguenti informazioni del nuovo cliente: username, password e 
	 * 			ruoli (in questo caso possiede i ruoli GestoreOrdini e Cliente).
	 * */
	
	ProxyUtente registraGestoreOrdini(String username, String password, String email, String nome, String cognome, 
			Sesso sex, String telefono, Indirizzo indirizzo, Ruolo isOrderManager);
	
	/**
	 * Questo metodo permette di registrare un nuovo utente come gestore del catalogo al sistema.
	 * 
	 * @param username: il nome utente scelto dal gestore
	 * @param password: la password scelta dal gestore
	 * @param email: l'indirizzo di posta elettronica del gestore
	 * @param nome: il nome del gestore
	 * @param cognome: il cognome del gestore
	 * @param sex: il genere del gestore
	 * @param telefono: il numero di telefono del gestore
	 * @param indirizzo: l'indirizzo di spedizione del gestore
	 * @param isCatalogManager: è il ruolo "GestoreCatalogo" da associare all'utente
	 * 
	 * @return un oggetto ProxyUtente che contiene le seguenti informazioni del nuovo cliente: username, password e 
	 * 			ruoli (in questo caso possiede i ruoli GestoreCatalogo e Cliente).
	 * */
	
	ProxyUtente registraGestoreCatalogo(String username, String password,String email, String nome, String cognome, 
			Sesso sex, String telefono, Indirizzo indirizzo, Ruolo isCatalogManager);
}
