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
import application.AutenticazioneService.AutenticazioneException.RimozioneIndirizzoException;
import application.AutenticazioneService.AutenticazioneException.PasswordEsistenteException;
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
 * @see application.RegistrazioneService.ProxyUtente
 * @see application.RegistrazioneService.ObjectUtente
 * @see application.RegistrazioneService.Utente
 * @see application.RegistrazioneService.Ruolo
 * 
 * @author Dorotea Serrelli
 * */

public class AutenticazioneServiceImpl implements AutenticazioneService{
	
	private UtenteDAODataSource userDAO;
	private RuoloDAODataSource roleDAO;
	private ClienteDAODataSource profileDAO;
	private IndirizzoDAODataSource addressDAO;
	
	public AutenticazioneServiceImpl(UtenteDAODataSource userDAO, RuoloDAODataSource roleDAO, ClienteDAODataSource profileDAO, IndirizzoDAODataSource addressDAO) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.profileDAO = profileDAO;
		this.addressDAO = addressDAO;
	}
	
	/**
	 * Il metodo effettua l'autenticazione dell'utente: verifica la corrispondenza 
	 * tra le credenziali inserite (viene effettuato l'hash della password fornita) 
	 * e le credenziali dell'utente memorizzate nel database.
	 * 
	 * @param username : l'username fornito dall'utente
	 * @param password : la password fornita dall'utente (senza che sia stato effettuato l'hashing)
	 * 
	 * @return un oggetto della classe ProxyUtente corrispondente all'utente con le credenziali 
	 * 			username e password inserite, comprensivo di 
	 * 			ruoli associati all'utente autenticato
	 * 
	 * @throws SQLException 
	 * @throws UtenteInesistenteException : lanciata nel caso in cui l'utente non è
	 * 			registrato nel sistema
	 * */

	@Override
	public ProxyUtente login(String username, String password) throws SQLException, UtenteInesistenteException {
		
		ProxyUtente userReal;
		if((userReal = userDAO.doRetrieveProxyUserByKey(username)) == null)
			throw new UtenteInesistenteException("Username o password non valide");
		else {
			Utente client = new Utente("", password, null);
			if(!client.getPassword().equals(userReal.getPassword()))
				throw new UtenteInesistenteException("Username o password non valide");
		}

		return userReal;
	}


	/**
	 * Il metodo effettua la reimpostazione della password dell'utente: prima, verifica la corrispondenza 
	 * tra le credenziali inserite e le credenziali dell'utente memorizzate nel database, poi, 
	 * memorizza la nuova password.
	 * 
	 * @param username : l'username fornito dall'utente
	 * @param email : l'email fornita dall'utente
	 * @param newPassword : la password fornita dall'utente (senza che sia stato effettuato l'hashing)
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
	 * */

	@Override
	public void resetPassword(String username, String email, String newPassword) throws UtenteInesistenteException, FormatoPasswordException, SQLException, PasswordEsistenteException {
		
		Utente userReal;
		if((userReal = userDAO.doRetrieveFullUserByKey(username)) == null)
			throw new UtenteInesistenteException("Username o email non valide");
		else {
			if(!email.equals(userReal.getProfile().getEmail()))
				throw new UtenteInesistenteException("Username o email non valide");
			else {
				if(!ObjectUtente.checkResetPassword(newPassword))
					throw new FormatoPasswordException("La password deve avere almeno 5 caratteri che siano lettere e numeri.");
				//hashing della nuova password
				Utente isEqual = new Utente("", newPassword, null);

				if(userReal.getPassword().equals(isEqual.getPassword()))
					throw new PasswordEsistenteException("Non è possibile associare questa password al tuo account. Inserisci una altra password.");

				userReal.setPasswordToHash(newPassword);
				userDAO.doResetPassword(username, userReal.getPassword());

			}
		}
	}

	/**
	 * Il metodo effettua la modifica del numero di telefono e dell'email dell'utente.
	 * 
	 * @param user : l'utente che richiede la modifica della propria email/
	 * 				del proprio numero di telefono
	 * @param information : l'informazione da modificare ("TELEFONO" o "EMAIL")
	 * 
	 * @param updatedData: la nuova informazione (email/telefono) da memorizzare
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
	 * */

	@Override
	public ProxyUtente aggiornaProfilo(ProxyUtente user, String information, String updatedData) throws SQLException, FormatoEmailException, ProfiloInesistenteException, EmailEsistenteException, TelefonoEsistenteException, FormatoTelefonoException, InformazioneDaModificareException {
		
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
	 * */

	@Override
	public ProxyUtente aggiornaRubricaIndirizzi(ProxyUtente user, String information, Indirizzo updatedData) throws UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, SQLException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException {

		if(information.equalsIgnoreCase("AGGIUNGERE-INDIRIZZO")) {
			if(userDAO.doRetrieveFullUserByKey(user.getUsername()) == null)
				throw new UtenteInesistenteException("Errore nel recupero delle informazioni relative"
						+ "all'utente");
			else {
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll("", user.getUsername());
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
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll("", user.getUsername());
				if(!addresses.contains(updatedData))
					throw new RimozioneIndirizzoException("Indirizzo inserito non associato all'utente");
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
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll("", user.getUsername());
				for(Indirizzo search_ind: addresses){
					if(search_ind.getIDIndirizzo() == updatedData.getIDIndirizzo()){
						if(!Indirizzo.checkValidate(updatedData))
							throw new FormatoIndirizzoException("Formato del nuovo indirizzo non valido");

						addressDAO.doUpdateAddress(updatedData, user.getUsername());
						return user;
					}
				}                            
				throw new ModificaIndirizzoException("Indirizzo inserito non associato all'utente");			
			}
		}

		throw new InformazioneDaModificareException("Selezionare un'informazione del profilo utente da modificare");
	}

}
