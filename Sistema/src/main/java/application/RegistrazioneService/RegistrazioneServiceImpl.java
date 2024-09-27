package application.RegistrazioneService;

import java.sql.SQLException;

import application.RegistrazioneService.Cliente.Sesso;
import application.RegistrazioneService.RegistrazioneException.EmailPresenteException;
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
	
	@Override
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
	 * @throws SQLException 
	 * @throws UtentePresenteException : gestisce il caso in cui un visitatore si registra con un username
	 * 									 associata ad un utente presente già nel database.
	 * 
	 * @throws EmailPresenteException : gestisce il caso in cui un visitatore si registra con un'email
	 * 									associata ad un utente già presente nel database.
	 * 
	 * */

	public ProxyUtente registraCliente(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo) throws UtentePresenteException, SQLException, EmailPresenteException {
		if(ObjectUtente.checkValidate(username, password)) {
			if(Cliente.checkValidate(email, nome, cognome, sex, telefono, indirizzo)) {
				
				Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
				Utente user = new Utente(username, password, profile);
				UtenteDAODataSource userDAO = new UtenteDAODataSource();
				
				/* *
				 * Si verifica l'esistenza di un utente nel database con il nome utente username.
				 * In caso affermativo, non sarà possibile procedere con la registrazione.
				 * */
				ProxyUtente userP = userDAO.doRetrieveProxyUserByKey(username);

				if(userP != null && userP.getUsername().equals(username))
					throw new UtentePresenteException("Non e\' possibile associare al tuo account l'username inserita.\nRiprova la registrazione inserendo un'altra username.");
				
				ClienteDAODataSource profileDAO = new ClienteDAODataSource();
				
				/* *
				 * Si verifica l'esistenza di un utente nel database con l'indirizzo di posta elettronica
				 * pari a email.
				 * In caso affermativo, non sarà possibile procedere con la registrazione.
				 * */
				
				Cliente profileP = profileDAO.doRetrieveByKey(email);
				
				if(profileP != null && profileP.getEmail().equals(email))
					throw new EmailPresenteException("Non e\' possibile associare al tuo account l'email inserita.\nRiprova la registrazione inserendo un'altra email.");
				
				
				RuoloDAODataSource roleDAO = new RuoloDAODataSource();
				IndirizzoDAODataSource addressDAO = new IndirizzoDAODataSource();
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
			System.out.println("Mi trovo al primo null");
			return null;
		}
		System.out.println("Mi trovo al secondo null");
		return null;
	}

	
	@Override
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
	 * */
	public ProxyUtente registraGestoreOrdini(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo, Ruolo isOrderManager) {
		if(ObjectUtente.checkValidate(username, password)) {
			if(Cliente.checkValidate(email, nome, cognome, sex, telefono, indirizzo)) {
				Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
				Utente user = new Utente(username, password, profile, isOrderManager);
				ClienteDAODataSource profileDAO = new ClienteDAODataSource();
				UtenteDAODataSource userDAO = new UtenteDAODataSource();
				RuoloDAODataSource roleDAO = new RuoloDAODataSource();
				IndirizzoDAODataSource addressDAO = new IndirizzoDAODataSource();
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
			return null;
		}
		return null;
	}

	
	@Override
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
	 * */
	public ProxyUtente registraGestoreCatalogo(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo, Ruolo isCatalogManager) {
		if(ObjectUtente.checkValidate(username, password)) {
			if(Cliente.checkValidate(email, nome, cognome, sex, telefono, indirizzo)) {
				Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
				Utente user = new Utente(username, password, profile, isCatalogManager);
				ClienteDAODataSource profileDAO = new ClienteDAODataSource();
				UtenteDAODataSource userDAO = new UtenteDAODataSource();
				RuoloDAODataSource roleDAO = new RuoloDAODataSource();
				IndirizzoDAODataSource addressDAO = new IndirizzoDAODataSource();
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
			return null;
		}
		return null;
	}
}
