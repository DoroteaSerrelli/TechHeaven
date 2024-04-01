package application.AutenticazioneService;

import java.sql.SQLException;

import application.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.AutenticazioneService.AutenticazioneException.FormatoIndirizzoException;
import application.AutenticazioneService.AutenticazioneException.FormatoPasswordException;
import application.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.AutenticazioneService.AutenticazioneException.IndirizzoEsistenteException;
import application.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.AutenticazioneService.AutenticazioneException.ModificaIndirizzoException;
import application.AutenticazioneService.AutenticazioneException.ProfiloInesistenteException;
import application.AutenticazioneService.AutenticazioneException.TelefonoEsistenteException;
import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
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
	 * @throws SQLException 
	 * @throws UtenteInesistenteException 
	 * */
	public ProxyUtente login(String username, String password) throws SQLException, UtenteInesistenteException;
	
	/**
	 * Il metodo effettua la reimpostazione della password dell'utente: prima, verifica la corrispondenza 
	 * tra le credenziali inserite e le credenziali dell'utente memorizzate nel database, poi, 
	 * memorizza la nuova password.
	 * @param username : l'username fornito dall'utente
	 * @param email : l'email fornita dall'utente
	 * @param newPassword : la password fornita dall'utente (senza che sia stato effettuato l'hashing)
	 * @throws SQLException 
	 * @throws FormatoPasswordException 
	 * @throws UtenteInesistenteException 
	 * */
	public void resetPassword(String username, String email, String newPassword) throws UtenteInesistenteException, FormatoPasswordException, SQLException;
	
	/**
	 * Il metodo effettua la modifica del numero di telefono e dell'email 
	 * dell'utente.
	 * @param user : l'utente che richiede la modifica del proprio profilo
	 * @param information : l'informazione che l'utente intende modificare (email o numero di telefono)
	 * @param updatedData : la nuova informazione da memorizzare nel profilo
	 * @throws SQLException 
	 * @throws FormatoEmailException 
	 * @throws ProfiloInesistenteException 
	 * @throws EmailEsistenteException 
	 * @throws TelefonoEsistenteException 
	 * @throws FormatoTelefonoException 
	 * @throws InformazioneDaModificareException 
	 * */
	
	public ProxyUtente updateProfile(ProxyUtente user, String information, String updatedData) throws SQLException, FormatoEmailException, ProfiloInesistenteException, EmailEsistenteException, TelefonoEsistenteException, FormatoTelefonoException, InformazioneDaModificareException;
	
	/**
	 * Il metodo effettua aggiunta/rimozione/aggiornamento
	 * di un indirizzo di spedizione dell'utente.
	 * @param user : l'utente che richiede la modifica del proprio profilo
	 * @param information : l'informazione che l'utente intende modificare (inserimento/
	 * rimozione/aggiornamento di un indirizzo)
	 * @param updatedData : l'indirizzo di spedizione da inserire/rimuovere/aggiornato da memorizzare
	 * @throws SQLException 
	 * @throws FormatoIndirizzoException 
	 * @throws IndirizzoEsistenteException 
	 * @throws UtenteInesistenteException 
	 * @throws ModificaIndirizzoException 
	 * @throws InformazioneDaModificareException 
	 * */
	
	public ProxyUtente updateAddressBook(ProxyUtente user, String information, Indirizzo updatedData) throws UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, SQLException, ModificaIndirizzoException, InformazioneDaModificareException;

}
