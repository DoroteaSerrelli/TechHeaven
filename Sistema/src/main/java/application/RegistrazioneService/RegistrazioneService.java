package application.RegistrazioneService;

import java.sql.SQLException;

import application.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.RegistrazioneService.Cliente.Sesso;
import application.RegistrazioneService.RegistrazioneException.EmailPresenteException;
import application.RegistrazioneService.RegistrazioneException.FormatoCAPException;
import application.RegistrazioneService.RegistrazioneException.FormatoCittaException;
import application.RegistrazioneService.RegistrazioneException.FormatoCognomeException;
import application.RegistrazioneService.RegistrazioneException.FormatoGenereException;
import application.RegistrazioneService.RegistrazioneException.FormatoNomeException;
import application.RegistrazioneService.RegistrazioneException.FormatoNumCivicoException;
import application.RegistrazioneService.RegistrazioneException.FormatoPasswordException;
import application.RegistrazioneService.RegistrazioneException.FormatoProvinciaException;
import application.RegistrazioneService.RegistrazioneException.FormatoUsernameException;
import application.RegistrazioneService.RegistrazioneException.FormatoViaException;
import application.RegistrazioneService.RegistrazioneException.UtentePresenteException;

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
	 * @throws SQLException 
	 * @throws UtentePresenteException : gestisce il caso in cui un visitatore si registra con un username
	 * 									 associata ad un utente presente già nel database.
	 * 
	 * @throws EmailPresenteException : gestisce il caso in cui un visitatore si registra con un'email
	 * 									associata ad un utente già presente nel database.
	 * @throws FormatoProvinciaException  : eccezione che gestisce il caso in cui la provincia
	 * 										non è espressa nel formato corretto
	 * 
	 * @throws FormatoCAPException : eccezione che gestisce il caso in cui il CAP non rispetta
	 * 								 il formato
	 * 
	 * @throws FormatoCittaException : eccezione che gestisce il caso in cui la città non
	 * 									è specificata nel formato corretto
	 * 
	 * @throws FormatoNumCivicoException : eccezione che gestisce il caso in cui il numero civico
	 * 										non è specificato nel formato corretto
	 * 
	 * @throws FormatoViaException : eccezione che gestisce il caso in cui la via non è specificata nel formato
	 * 									corretto
	 * 
	 * @throws FormatoUsernameException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  l'username non rispettando il formato.
	 * @throws FormatoPasswordException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  la password non rispettando il formato.
	 * 
	 * @throws UtentePresenteException : eccezione che gestisce il caso in cui l'utente
	 * 									 ha inserito un'username già presente nel database
	 * @throws FormatoEmailException : eccezione che gestisce il caso in cui l'email non è specificata nel formato
	 * 									corretto
	 * @throws FormatoNomeException : eccezione che gestisce il caso in cui l'utente specifica il nome
	 * 									non rispettando il formato.
	 * @throws FormatoCognomeException  : eccezione che gestisce il caso in cui l'utente specifica il cognome
	 * 									non rispettando il formato.
	 * @throws FormatoGenereException : eccezione che gestisce il caso in cui l'utente non specifica il genere.
	 * @throws FormatoTelefonoException : eccezione che gestisce il caso in cui l'utente non specifica il 
	 * 									numero di telefono con il formato corretto.
	 * @throws EmailEsistenteException : eccezione che gestisce il caso in cui l'email è già presente nel database.
	 * 
	 * 
	 * */
	
	ProxyUtente registraCliente(String username, String password, String email, String nome, String cognome, 
			String sex, String telefono, Indirizzo indirizzo) throws UtentePresenteException, SQLException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException;
	
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
	 * @throws FormatoProvinciaException  : eccezione che gestisce il caso in cui la provincia
	 * 										non è espressa nel formato corretto
	 * 
	 * @throws FormatoCAPException : eccezione che gestisce il caso in cui il CAP non rispetta
	 * 								 il formato
	 * 
	 * @throws FormatoCittaException : eccezione che gestisce il caso in cui la città non
	 * 									è specificata nel formato corretto
	 * 
	 * @throws FormatoNumCivicoException : eccezione che gestisce il caso in cui il numero civico
	 * 										non è specificato nel formato corretto
	 * 
	 * @throws FormatoViaException : eccezione che gestisce il caso in cui la via non è specificata nel formato
	 * 									corretto
	 * @throws FormatoUsernameException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  l'username non rispettando il formato.
	 * @throws FormatoPasswordException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  la password non rispettando il formato.
	 * @throws UtentePresenteException : eccezione che gestisce il caso in cui l'utente
	 * 									 ha inserito un'username già presente nel database
	 * @throws SQLException 
	 * @throws FormatoEmailException : eccezione che gestisce il caso in cui l'email non è specificata nel formato
	 * 									corretto
	 * @throws FormatoNomeException : eccezione che gestisce il caso in cui l'utente specifica il nome
	 * 									non rispettando il formato.
	 * @throws FormatoCognomeException  : eccezione che gestisce il caso in cui l'utente specifica il cognome
	 * 									non rispettando il formato.
	 * @throws FormatoGenereException : eccezione che gestisce il caso in cui l'utente non specifica il genere.
	 * @throws FormatoTelefonoException : eccezione che gestisce il caso in cui l'utente non specifica il 
	 * 									numero di telefono con il formato corretto.
	 * @throws EmailEsistenteException : eccezione che gestisce il caso in cui l'email è già presente nel database.
	 * 
	 * 
	 * 
	 * */
	
	ProxyUtente registraGestoreOrdini(String username, String password, String email, String nome, String cognome, 
			String sex, String telefono, Indirizzo indirizzo, Ruolo isOrderManager) throws FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, UtentePresenteException, SQLException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException;
	
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
	 * @throws FormatoProvinciaException  : eccezione che gestisce il caso in cui la provincia
	 * 										non è espressa nel formato corretto
	 * 
	 * @throws FormatoCAPException : eccezione che gestisce il caso in cui il CAP non rispetta
	 * 								 il formato
	 * 
	 * @throws FormatoCittaException : eccezione che gestisce il caso in cui la città non
	 * 									è specificata nel formato corretto
	 * 
	 * @throws FormatoNumCivicoException : eccezione che gestisce il caso in cui il numero civico
	 * 										non è specificato nel formato corretto
	 * 
	 * @throws FormatoViaException : eccezione che gestisce il caso in cui la via non è specificata nel formato
	 * 									corretto
	 * 
	 * @throws FormatoUsernameException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  l'username non rispettando il formato.
	 * 
	 * @throws FormatoPasswordException : eccezione che gestisce il caso in cui l'utente specifica 
	 * 									  la password non rispettando il formato.
	 * 
	 * @throws UtentePresenteException : eccezione che gestisce il caso in cui l'utente
	 * 									 ha inserito un'username già presente nel database
	 * @throws SQLException 
	 * @throws FormatoEmailException : eccezione che gestisce il caso in cui l'email non è specificata nel formato
	 * 									corretto
	 * @throws FormatoNomeException : eccezione che gestisce il caso in cui l'utente specifica il nome
	 * 									non rispettando il formato.
	 * @throws FormatoCognomeException  : eccezione che gestisce il caso in cui l'utente specifica il cognome
	 * 									non rispettando il formato.
	 * @throws FormatoGenereException : eccezione che gestisce il caso in cui l'utente non specifica il genere.
	 * @throws FormatoTelefonoException : eccezione che gestisce il caso in cui l'utente non specifica il 
	 * 									numero di telefono con il formato corretto.
	 * @throws EmailEsistenteException : eccezione che gestisce il caso in cui l'email è già presente nel database.
	 * 
	 * 
	 * */
	
	ProxyUtente registraGestoreCatalogo(String username, String password,String email, String nome, String cognome, 
			String sex, String telefono, Indirizzo indirizzo, Ruolo isCatalogManager) throws FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, UtentePresenteException, SQLException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException;
}
