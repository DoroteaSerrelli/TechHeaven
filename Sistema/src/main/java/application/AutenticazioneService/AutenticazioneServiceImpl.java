package application.AutenticazioneService;

import java.sql.SQLException;
import java.util.ArrayList;

import application.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.AutenticazioneService.AutenticazioneException.FormatoIndirizzoException;
import application.AutenticazioneService.AutenticazioneException.FormatoPasswordException;
import application.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.AutenticazioneService.AutenticazioneException.IndirizzoEsistenteException;
import application.AutenticazioneService.AutenticazioneException.ProfiloInesistenteException;
import application.AutenticazioneService.AutenticazioneException.TelefonoEsistenteException;
import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.AutenticazioneService.AutenticazioneException.ModificaIndirizzoException;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ObjectUtente;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Ruolo;
import application.RegistrazioneService.Utente;
import storage.AutenticazioneDAO.*;

/**
 * La classe fornisce un'implementazione ai servizi definiti nell'interfaccia
 * AutenticazioneService : autenticazione al sistema, reimpostazione
 * della password ed aggiornamento del profilo personale dell'utente.
 * 
 * @see application.AutenticazioneService.AutenticazioneService
 * @see application.RegistrazioneService.Proxyutente
 * @see application.RegistrazioneService.ObjectUtente
 * @see application.RegistrazioneService.Utente
 * 
 * @author Dorotea Serrelli
 * */

public class AutenticazioneServiceImpl implements AutenticazioneService{
	
	/**
	 * Il metodo effettua l'autenticazione dell'utente: verifica la corrispondenza 
	 * tra le credenziali inserite (viene effettuato l'hash della password fornita) 
	 * e le credenziali dell'utente memorizzate nel database.
	 * @param username : l'username fornito dall'utente
	 * @param password : la password fornita dall'utente (senza che sia stato effettuato l'hashing)
	 * @return l'utente corrispondente alle credenziali inserite
	 * @throws SQLException 
	 * @throws UtenteInesistenteException 
	 * */
	@Override
	public ProxyUtente login(String username, String password) throws SQLException, UtenteInesistenteException {
		UtenteDAODataSource userDAO = new UtenteDAODataSource();
		ProxyUtente userReal;
		if((userReal = userDAO.doRetrieveProxyUserByKey(username)) == null)
			throw new UtenteInesistenteException("Username o password non valide");
		else {
			Utente client = new Utente("", password, null);
			if(!client.getPassword().equals(userReal.getPassword()))
				throw new UtenteInesistenteException("Username o password non valide");
		}
		ArrayList<Ruolo> roles = (new RuoloDAODataSource()).doRetrieveByKey(username);
		return new ProxyUtente(username, userReal.getPassword(), roles);
	}
	
	
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
	@Override
	public void resetPassword(String username, String email, String newPassword) throws UtenteInesistenteException, FormatoPasswordException, SQLException {
		UtenteDAODataSource userDAO = new UtenteDAODataSource();
		Utente userReal;
		if((userReal = userDAO.doRetrieveFullUserByKey(username)) == null)
			throw new UtenteInesistenteException("Username o email non valide");
		else {
			if(!email.equals(userReal.getProfile().getEmail()))
				throw new UtenteInesistenteException("Username o email non valide");
			else {
				if(!ObjectUtente.checkResetPassword(newPassword))
					throw new FormatoPasswordException("Formato della nuova password non valido");
				//hashing della nuova password
				userReal.setPasswordToHash(newPassword);
				userDAO.doResetPassword(username, userReal.getPassword());
				
			}
		}
	}
	
	/**
	 * Il metodo effettua la modifica del numero di telefono e dell'email dell'utente.
	 * @param user : l'utente che richiede la modifica della propria email/
	 * del proprio numero di telefono
	 * @param information : l'informazione da modificare ("TELEFONO" o "EMAIL")
	 * @param updatedData: la nuova informazione (email/telefono) da memorizzare
	 * @return l'utente con il profilo aggiornato
	 * @throws SQLException 
	 * @throws FormatoEmailException 
	 * @throws ProfiloInesistenteException
	 * @throws EmailEsistenteException
	 * @throws TelefonoEsistenteException 
	 * @throws FormatoTelefonoException 
	 * @throws InformazioneDaModificareException 
	 * */
	@Override
	public ProxyUtente updateProfile(ProxyUtente user, String information, String updatedData) throws SQLException, FormatoEmailException, ProfiloInesistenteException, EmailEsistenteException, TelefonoEsistenteException, FormatoTelefonoException, InformazioneDaModificareException {
		UtenteDAODataSource userDAO = new UtenteDAODataSource();
		ClienteDAODataSource profileDAO = new ClienteDAODataSource();
		Utente userReal;
		
		if(information.equalsIgnoreCase("EMAIL")) {
			if((userReal = userDAO.doRetrieveFullUserByKey(user.getUsername())) == null)
				throw new ProfiloInesistenteException("Errore nel recupero delle informazioni relative"
						+ "al profilo dell'utente");
			else {
				if(updatedData.equals(userReal.getProfile().getEmail()))
					throw new EmailEsistenteException("Email inserita già associata all'utente");
				else {
					if(!Cliente.checkValidateEmail(updatedData))
						throw new FormatoEmailException("Formato della nuova email non valido");
					profileDAO.updateEmail(userReal.getProfile().getEmail(), updatedData);
					return new ProxyUtente(userReal.getUsername(), userReal.getPassword(), userReal.getRuoli());
				}
			}
		}
		
		if(information.equalsIgnoreCase("TELEFONO")) {
			if((userReal = userDAO.doRetrieveFullUserByKey(user.getUsername())) == null)
				throw new ProfiloInesistenteException("Errore nel recupero delle informazioni relative"
						+ "al profilo dell'utente");
			else {
				if(updatedData.equals(userReal.getProfile().getTelefono()))
					throw new TelefonoEsistenteException("Numero di telefono inserito già associato all'utente");
				else {
					if(!Cliente.checkValidateTelefono(updatedData))
						throw new FormatoTelefonoException("Formato del nuovo numero di telefono non valido");
					profileDAO.updateTelephone(userReal.getProfile().getEmail(), updatedData);
					return new ProxyUtente(userReal.getUsername(), userReal.getPassword(), userReal.getRuoli());
				}
			}
		}
		throw new InformazioneDaModificareException("Selezionare un'informazione del profilo utente da modificare");
	}
	
	
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
	
	@Override
	public ProxyUtente updateAddressBook(ProxyUtente user, String information, Indirizzo updatedData) throws UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, SQLException, ModificaIndirizzoException, InformazioneDaModificareException {
		UtenteDAODataSource userDAO = new UtenteDAODataSource();
		IndirizzoDAODataSource addressDAO = new IndirizzoDAODataSource();
		
		if(information.equalsIgnoreCase("AGGIUNGERE-INDIRIZZO")) {
			if(userDAO.doRetrieveFullUserByKey(user.getUsername()) == null)
				throw new UtenteInesistenteException("Errore nel recupero delle informazioni relative"
						+ "all'utente");
			else {
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll(user.getUsername());
				if(addresses.contains(updatedData))
					throw new IndirizzoEsistenteException("Indirizzo inserito già associato all'utente");
				else {
					if(!Indirizzo.checkValidate(updatedData))
						throw new FormatoIndirizzoException("Formato del nuovo indirizzo non valido");
					
					addressDAO.doSave(updatedData, user.getUsername());
					return user;
				}
			}
		}
		
		if(information.equalsIgnoreCase("RIMUOVERE-INDIRIZZO")) {
			if(userDAO.doRetrieveFullUserByKey(user.getUsername()) == null)
				throw new UtenteInesistenteException("Errore nel recupero delle informazioni relative"
						+ "all'utente");
			else {
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll(user.getUsername());
				if(!addresses.contains(updatedData))
					throw new ModificaIndirizzoException("Indirizzo inserito non associato all'utente");
				else {
					if(!Indirizzo.checkValidate(updatedData))
						throw new FormatoIndirizzoException("Formato dell'indirizzo non valido");
					
					addressDAO.doDeleteAddress(updatedData.getIDIndirizzo(), user.getUsername());
					return user;
				}
			}
		}
		
		if(information.equalsIgnoreCase("AGGIORNARE-INDIRIZZO")) {
			if(userDAO.doRetrieveFullUserByKey(user.getUsername()) == null)
				throw new UtenteInesistenteException("Errore nel recupero delle informazioni relative"
						+ "all'utente");
			else {
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll(user.getUsername());
				if(!addresses.contains(updatedData))
					throw new ModificaIndirizzoException("Indirizzo inserito non associato all'utente");
				else {
					if(!Indirizzo.checkValidate(updatedData))
						throw new FormatoIndirizzoException("Formato del nuovo indirizzo non valido");
					
					addressDAO.doUpdateAddress(updatedData, user.getUsername());
					return user;
				}
			}
		}
		
		throw new InformazioneDaModificareException("Selezionare un'informazione del profilo utente da modificare");
	}

}
