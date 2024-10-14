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
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

/**
 * La classe fornisce un'implementazione al servizio di registrazione di un nuovo utente
 * al sistema: cliente, gestore degli ordini e gestore del catalogo.
 * 
 * @see application.RegistrazioneService.RegistrazioneService
 * @see application.RegistrazioneService.ProxyUtente
 * @see application.RegistrazioneService.ObjectUtente
 * @see application.RegistrazioneService.Utente
 * @see application.RegistrazioneService.Ruolo
 * @see application.RegistrazioneService.Indirizzo
 * @see application.RegistrazioneService.Cliente
 * 
 * @author Dorotea Serrelli
 * */

public class RegistrazioneServiceImpl implements RegistrazioneService{

	private UtenteDAODataSource userDAO;
	private RuoloDAODataSource roleDAO;
	private ClienteDAODataSource profileDAO;
	private IndirizzoDAODataSource addressDAO;

	public RegistrazioneServiceImpl(UtenteDAODataSource userDAO, RuoloDAODataSource roleDAO, ClienteDAODataSource profileDAO, IndirizzoDAODataSource addressDAO) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.profileDAO = profileDAO;
		this.addressDAO = addressDAO;
	}



	/**
	 * Questo metodo implementa la funzionalità di registrazione di un nuovo cliente
	 * nel sistema.
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
	 * 
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
	 * 
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

	@Override
	public ProxyUtente registraCliente(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo) throws UtentePresenteException, SQLException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException {
		if(ObjectUtente.checkUsername(username)) {
			if(userDAO.doRetrieveProxyUserByKey(username) == null) {
				if(ObjectUtente.checkPassword(password)) {
					if(Cliente.checkValidateEmail(email)) {
						if(profileDAO.doRetrieveByKey(email) == null) {

							if(Cliente.checkValidate(nome, cognome, sex, telefono, indirizzo)) {

								Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
								Utente user = new Utente(username, password, profile);

								try {
									profileDAO.doSave(profile);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database delle informazioni personali dell'utente.");
								}
								try {
									userDAO.doSave(user);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database delle credenziali di accesso dell'utente.");
								}
								try {
									roleDAO.doSave(user, new Ruolo("Cliente"));
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database del ruolo dell'utente.");
								}

								try {
									addressDAO.doSave(indirizzo, username);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database dell'indirizzo dell'utente.");
								}

								return new ProxyUtente(user.getUsername(), user.getPassword(), user.getRuoli());

							}
						} throw new EmailEsistenteException("Non è possibile associare l'email inserita al tuo account. Riprova la registrazione inserendo un'altra email.");
					}
					return null;
				}
			}
			throw new UtentePresenteException("Non è possibile associare l'username inserita al tuo account. Riprova la registrazione"+
					"inserendo un'altra username.");
		}
		return null;
	}



	/**
	 * Questo metodo implementa la funzionalità di registrazione di un gestore degli ordini nel sistema.
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
	 * 
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

	@Override
	public ProxyUtente registraGestoreOrdini(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo, Ruolo isOrderManager) throws FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, UtentePresenteException, SQLException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException {
		if(ObjectUtente.checkUsername(username)) {
			if(userDAO.doRetrieveProxyUserByKey(username) == null) {
				if(ObjectUtente.checkPassword(password)) {
					if(Cliente.checkValidateEmail(email)) {
						if(profileDAO.doRetrieveByKey(email) == null) {

							if(Cliente.checkValidate(nome, cognome, sex, telefono, indirizzo)) {
								Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
								Utente user = new Utente(username, password, profile, isOrderManager);

								try {
									profileDAO.doSave(profile);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database delle informazioni personali dell'utente.");
								}
								try {
									userDAO.doSave(user);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database delle credenziali di accesso dell'utente.");
								}
								try {
									roleDAO.doSave(user, new Ruolo("Cliente"));
									roleDAO.doSave(user, isOrderManager);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database dei ruoli dell'utente.");
								}
								try {
									addressDAO.doSave(indirizzo, username);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database dell'indirizzo dell'utente.");
								}

								return new ProxyUtente(user.getUsername(), user.getPassword(), user.getRuoli());
							}
						} 
						throw new EmailEsistenteException("Non è possibile associare l'email inserita al tuo account. Riprova la registrazione inserendo un'altra email.");
					}
					return null;
				}
			}
			throw new UtentePresenteException("Non è possibile associare l'username inserita al tuo account. Riprova la registrazione"+
					"inserendo un'altra username.");
		}
		return null;
	}



	/**
	 * Questo metodo implementa la funzionalità di registrazione di un gestore del catalogo nel sistema.
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
	 * 			ruoli (in questo caso possiede i ruoli Gestore catalogo e Cliente).
	 * 
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
	 * 
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
	@Override
	public ProxyUtente registraGestoreCatalogo(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo, Ruolo isCatalogManager) throws FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, UtentePresenteException, SQLException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException {
		if(ObjectUtente.checkUsername(username)) {
			if(userDAO.doRetrieveProxyUserByKey(username) == null) {
				if(ObjectUtente.checkPassword(password)) {
					if(Cliente.checkValidateEmail(email)) {
						if(profileDAO.doRetrieveByKey(email) == null) {

							if(Cliente.checkValidate(nome, cognome, sex, telefono, indirizzo)) {
								Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
								Utente user = new Utente(username, password, profile, isCatalogManager);

								try {
									profileDAO.doSave(profile);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database delle informazioni personali dell'utente.");
								}
								try {
									userDAO.doSave(user);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database delle credenziali di accesso dell'utente.");
								}
								try {
									roleDAO.doSave(user, new Ruolo("Cliente"));
									roleDAO.doSave(user, isCatalogManager);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database dei ruoli dell'utente.");
								}
								try {
									addressDAO.doSave(indirizzo, username);
								} catch (SQLException e) {
									System.out.println("Errore nella memorizzazione nel Database dell'indirizzo dell'utente.");
								}
								return new ProxyUtente(user.getUsername(), user.getPassword(), user.getRuoli());
							}
						}
						throw new EmailEsistenteException("Non è possibile associare l'email inserita al tuo account. Riprova la registrazione inserendo un'altra email.");
					}
					return null;
				}
			}
			throw new UtentePresenteException("Non è possibile associare l'username inserita al tuo account. Riprova la registrazione"+
					"inserendo un'altra username.");
		}
		return null;
	}
}
