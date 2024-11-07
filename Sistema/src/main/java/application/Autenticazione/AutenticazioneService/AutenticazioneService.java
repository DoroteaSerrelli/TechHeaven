package application.Autenticazione.AutenticazioneService;

import java.sql.SQLException;

import application.Autenticazione.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoPasswordException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.IndirizzoEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.ModificaIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.PasswordEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.ProfiloInesistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.RimozioneIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.TelefonoEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCAPException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCittaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoNumCivicoException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoProvinciaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoViaException;

/**
 * L'interfaccia offre servizi relativi ad un utente autenticato con il ruolo di cliente:
 * autenticazione al sistema, reimpostazione della password e
 * modifica delle informazioni presenti nel suo profilo personale.
 * 
 * @see application.Registrazione.RegistrazioneService.ProxyUtente
 * @see application.Registrazione.RegistrazioneService.Indirizzo
 * 
 * @author Dorotea Serrelli
 * */

public interface AutenticazioneService {
	
	/**
	 * Il metodo permette l'autenticazione di un utente al sistema.
	 * 
	 * @param username : il nome utente
	 * @param password: la password inserita
	 * 
	 * @return un oggetto della classe ProxyUtente che contiene le informazioni username, password, 
	 * 			ruoli associati all'utente autenticato
	 * 
	 * @throws SQLException 
	 * @throws UtenteInesistenteException : lanciata nel caso in cui l'utente non è
	 * 			registrato nel sistema
	 * */
	
	public ProxyUtente login(String username, String password) throws SQLException, UtenteInesistenteException;
	
	/**
	 * Il metodo effettua la reimpostazione della password dell'utente: prima, verifica la corrispondenza 
	 * tra le credenziali inserite e le credenziali dell'utente memorizzate nel database, poi, 
	 * memorizza la nuova password.
	 * 
	 * @param username : l'username fornito dall'utente
	 * @param email : l'email fornita dall'utente
	 * @param newPassword : la password in chiaro fornita dall'utente (senza che sia stato effettuato l'hashing)
	 * 
	 * @throws SQLException 
	 * @throws FormatoPasswordException : lanciata nel caso in cui la nuova password non 
	 * 										rispetti il formato
	 * 
	 * @throws UtenteInesistenteException : lanciata nel caso in cui l'utente non è
	 * 										registrato nel sistema
	 * 
	 * @throws PasswordEsistenteException : lanciata nel caso in cui l'utente inserisce come nuova password,
	 * 										la password che già possiede nel database
	 * @throws FormatoEmailException 
	 * */
	
	public void resetPassword(String username, String email, String newPassword) throws UtenteInesistenteException, FormatoPasswordException, SQLException, PasswordEsistenteException, FormatoEmailException;
	
	/**
	 * Il metodo effettua la modifica del numero di telefono e dell'email 
	 * dell'utente.
	 * @param user : l'utente che richiede la modifica del proprio profilo
	 * @param information : l'informazione che l'utente intende modificare (email o numero di telefono)
	 * @param updatedData : la nuova informazione da memorizzare nel profilo
	 * 
	 * @return un oggetto della classe ProxyUtente che contiene le informazioni username, password, 
	 * 			ruoli associati all'utente, il cui profilo è stato aggiornato in information con
	 * 			updatedData 
	 * 
	 * @throws SQLException 
	 * @throws FormatoEmailException : se si cambia l'indirizzo email, questa eccezione viene lanciata
	 * 									nel caso in cui la nuova email non rispetta il formato indicato
	 * 
	 * @throws ProfiloInesistenteException : lanciata nel caso in cui non è stato definito il profilo
	 * 										dell'utente
	 * 
	 * @throws EmailEsistenteException : se si cambia l'indirizzo email, questa eccezione viene lanciata
	 * 									nel caso in cui la nuova email corrisponde all'email corrente
	 * 
	 * @throws TelefonoEsistenteException : se si cambia il recapito telefonico, questa eccezione viene lanciata
	 * 									nel caso in cui il nuovo numero di telefono coincide con quello corrente
	 * 
	 * @throws FormatoTelefonoException : se si cambia il recapito telefonico, questa eccezione viene lanciata
	 * 									nel caso in cui il nuovo numero di telefono non rispetta il formato indicato
	 * 
	 * @throws InformazioneDaModificareException : viene lanciata nel caso in cui non è stata selezionata alcuna
	 * 												informazione del profilo (email, numero di telefono) da aggiornare
	 * @throws ErroreParametroException 
	 * */
	
	public ProxyUtente aggiornaProfilo(ProxyUtente user, String information, String updatedData) throws SQLException, FormatoEmailException, ProfiloInesistenteException, EmailEsistenteException, TelefonoEsistenteException, FormatoTelefonoException, InformazioneDaModificareException;
	
	/**
	 * Il metodo effettua aggiunta/rimozione/aggiornamento
	 * di un indirizzo di spedizione dell'utente.
	 * 
	 * @param user : l'utente che richiede la modifica del proprio profilo
	 * @param information : l'informazione che l'utente intende modificare (inserimento/
	 * 						rimozione/aggiornamento di un indirizzo)
	 * @param updatedData : l'indirizzo di spedizione da inserire/rimuovere/aggiornato da memorizzare
	 * 
	 * @return un oggetto della classe ProxyUtente che contiene le informazioni username, password, 
	 * 			ruoli associati all'utente, il cui profilo è stato aggiornato nella rubrica degli indirizzi
	 * 
	 * @throws SQLException
	 * 
	 * @throws FormatoIndirizzoException : nel caso di aggiunta o aggiornamento di un indirizzo, questa eccezione
	 * 										viene lanciata nel caso in cui il nuovo indirizzo non rispetta il
	 * 										formato indicato
	 *  
	 * @throws IndirizzoEsistenteException : nel caso di aggiunta o aggiornamento di un indirizzo, questa eccezione viene lanciata quando 
	 * 											il nuovo indirizzo è già presente nella rubrica degli indirizzi dell'utente
	 * 
	 * @throws UtenteInesistenteException : viene lanciata nel caso in cui l'utente non è registrato nel sistema.
	 * 
	 * @throws RimozioneIndirizzoException : gestisce l'assenza di un indirizzo dell'utente da eliminare.
	 * 
	 * @throws ModificaIndirizzoException : gestisce l'assenza di un indirizzo dell'utente da aggiornare.
	 * 
	 * @throws InformazioneDaModificareException : viene lanciata nel caso in cui non è stata selezionata alcuna
	 * 												informazione da modificare
	 * @throws ProfiloInesistenteException 
	 * @throws FormatoProvinciaException 
	 * @throws FormatoCAPException 
	 * @throws FormatoCittaException 
	 * @throws FormatoNumCivicoException 
	 * @throws FormatoViaException 
	 * */
	
	public ProxyUtente aggiornaRubricaIndirizzi(ProxyUtente user, String information, Indirizzo updatedData) throws UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, SQLException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException;

}
