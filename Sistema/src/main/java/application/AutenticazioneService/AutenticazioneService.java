package application.AutenticazioneService;

import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;

/**
 * L'interfaccia offre servizi relativi all' autenticazione dell'utente al sistema,
 * alla reimpostazione della password ed alla modifica delle informazioni
 * presenti nel suo profilo personale.
 * 
 * @author Dorotea Serrelli
 * */

public interface AutenticazioneService {
	
	/**
	 * Il metodo permette l'autenticazione di un utente al sistema
	 * @param username : il nome utente
	 * @param password: la password inserita
	 * */
	public ProxyUtente login(String username, String password);
	
	/**
	 * Il metodo consente la reimpostazione della password di un cliente registrato nel sistema.
	 * @param username : il nome utente
	 * @param email: l'indirizzo di posta elettronica dell'utente
	 * */
	public ProxyUtente resetPassword(String username, String email);
	
	/**
	 * Il metodo effettua la modifica del numero di telefono e dell'email 
	 * dell'utente.
	 * @param user : l'utente che richiede la modifica del proprio profilo
	 * @param information : l'informazione che l'utente intende modificare (email o numero di telefono)
	 * @param updatedData : la nuova informazione da memorizzare nel profilo
	 * */
	
	public ProxyUtente updateProfile(ProxyUtente user, String information, String updatedData);
	
	/**
	 * Il metodo effettua aggiunta/rimozione/aggiornamento
	 * di un indirizzo di spedizione dell'utente.
	 * @param user : l'utente che richiede la modifica del proprio profilo
	 * @param information : l'informazione che l'utente intende modificare (inserimento/
	 * rimozione/aggiornamento di un indirizzo)
	 * @param updatedData : l'indirizzo di spedizione da inserire/rimuovere/aggiornato da memorizzare
	 * */
	
	public ProxyUtente updateAddressBook(ProxyUtente user, String information, Indirizzo updatedData);
}
