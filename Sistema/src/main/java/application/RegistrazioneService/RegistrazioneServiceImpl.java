package application.RegistrazioneService;

import java.sql.SQLException;

import application.RegistrazioneService.Cliente.Sesso;
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

/**
 * La classe fornisce un'implementazione al servizio di registrazione di un nuovo utente
 * al sistema: cliente, gestore degli ordini e gestore del catalogo.
 * @see application.RegistrazioneService
 * @see application.RegistrazioneService.Proxyutente
 * @see application.RegistrazioneService.ObjectUtente
 * @see application.RegistrazioneService.Utente
 * 
 * @author Dorotea Serrelli
 * */

public class RegistrazioneServiceImpl implements RegistrazioneService{
	
	@Override
	/**
	 * Implementa la funzionalità di registrazione di un cliente.
	 * */
	public ProxyUtente registraCliente(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo) {
		if(ObjectUtente.checkValidate(username, password)) {
			if(Cliente.checkValidate(email, nome, cognome, sex, telefono, indirizzo)) {
				Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
				Utente user = new Utente(username, password, profile);
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
			return null;
		}
		return null;
	}

	@Override
	/**
	 * Implementa la funzionalità di registrazione di un gestore degli ordini con ruoli: Cliente, Gestore ordini.
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
	 * Implementa la funzionalità di registrazione di un gestore del catalogo.
	 * */
	public ProxyUtente registraGestoreCatalogo(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo, Ruolo isCatalogManager) {
		if(ObjectUtente.checkValidate(username, password)) {
			if(Cliente.checkValidate(email, nome, cognome, sex, telefono, indirizzo)) {
				Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
				Utente user = new Utente(username, password, profile, new Ruolo("Gestore catalogo"));
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
