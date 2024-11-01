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
import application.RegistrazioneService.RegistrazioneException.FormatoCAPException;
import application.RegistrazioneService.RegistrazioneException.FormatoCittaException;
import application.RegistrazioneService.RegistrazioneException.FormatoNumCivicoException;
import application.RegistrazioneService.RegistrazioneException.FormatoProvinciaException;
import application.RegistrazioneService.RegistrazioneException.FormatoViaException;
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
		if((userReal = userDAO.doRetrieveProxyUserByKey(username)) == null) {
			throw new UtenteInesistenteException("Username o password non corretti");
			
		}
		else {

			Utente client = new Utente("", password, null);
			if(!client.getPassword().equals(userReal.getPassword()))
				throw new UtenteInesistenteException("Username o password non corretti");
		}
		userReal.setRuoli(roleDAO.doRetrieveByKey(userReal.getUsername()));

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
	 * @throws FormatoEmailException 
	 * */

	@Override
	public void resetPassword(String username, String email, String newPassword) throws UtenteInesistenteException, FormatoPasswordException, SQLException, PasswordEsistenteException, FormatoEmailException {

		Utente userReal;
		if((userReal = userDAO.doRetrieveFullUserByKey(username)) == null)
			throw new UtenteInesistenteException("Username o email non valide");
		else {
			if(!Cliente.checkValidateEmail(email))
				throw new FormatoEmailException("L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).");
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
	 * @throws ErroreParametroException 
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
					throw new EmailEsistenteException("Non è possibile associare questa email al tuo account. Inserisci una altra email.");
				else {
					if(!Cliente.checkValidateEmail(updatedData))
						throw new FormatoEmailException("L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).");

					ProxyUtente updatedUser = new ProxyUtente(userReal.getUsername(), userReal.getPassword(), userReal.getRuoli(), userDAO);
					updatedUser.setPassword(userReal.getPassword());
					if(profileDAO.updateEmail(userReal.getProfile().getEmail(), updatedData))
						updatedUser.mostraUtente().getProfile().setEmail(updatedData);

					return updatedUser;
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

					ProxyUtente updatedUser = new ProxyUtente(userReal.getUsername(), userReal.getPassword(), userReal.getRuoli(), userDAO);
					updatedUser.setPassword(userReal.getPassword());

					if(profileDAO.updateTelephone(userReal.getProfile().getEmail(), updatedData))
						updatedUser.mostraUtente().getProfile().setTelefono(updatedData);
					
					return updatedUser;
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
	 * @throws ProfiloInesistenteException 
	 * @throws FormatoProvinciaException 
	 * @throws FormatoCAPException 
	 * @throws FormatoCittaException 
	 * @throws FormatoNumCivicoException 
	 * @throws FormatoViaException 
	 * */

	@Override
	public ProxyUtente aggiornaRubricaIndirizzi(ProxyUtente user, String information, Indirizzo updatedData) throws UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, SQLException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {

		if(information.equalsIgnoreCase("AGGIUNGERE-INDIRIZZO")) {
			if(userDAO.doRetrieveFullUserByKey(user.getUsername()) == null)
				throw new ProfiloInesistenteException("Errore nel recupero delle informazioni relative"
						+ "all'utente");
			else {
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll("", user.getUsername());

				try {
					if(Indirizzo.checkValidate(updatedData)) {

						if(addresses.contains(updatedData))
							throw new IndirizzoEsistenteException("L'indirizzo inserito è già presente nella tua rubrica degli indirizzi.");


						addressDAO.doSave(updatedData, user.getUsername());
						return user;
					}
				} catch (FormatoViaException ex) {
					throw new FormatoViaException("La via deve contenere solo lettere e spazi");

				}catch(FormatoNumCivicoException e) {
					throw new FormatoNumCivicoException("Il numero civico è composto da numeri e, eventualmente, una lettera.");

				}catch(FormatoCittaException e) {
					throw new FormatoCittaException("La città deve essere composta solo da lettere e spazi.");

				}catch(FormatoCAPException e) {
					throw new FormatoCAPException("Il CAP deve essere formato da 5 numeri.");

				}catch(FormatoProvinciaException e) {
					throw new FormatoProvinciaException("La provincia è composta da due lettere maiuscole.");
				}

			}
		}

		if(information.equalsIgnoreCase("RIMUOVERE-INDIRIZZO")) {
			if(userDAO.doRetrieveFullUserByKey(user.getUsername()) == null)
				throw new ProfiloInesistenteException("Errore nel recupero delle informazioni relative"
						+ " all'utente");
			else {
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll("", user.getUsername());
				
				
					if(updatedData != null) {

						if(!addresses.contains(updatedData)) {

							throw new RimozioneIndirizzoException("Indirizzo inserito non associato all'utente");
						}
						addressDAO.doDeleteAddress(updatedData.getIDIndirizzo(), user.getUsername());
						return user;
					}else {
						throw new FormatoIndirizzoException("Specificare l'indirizzo di spedizione da rimuovere.");
					}
				
			}
		}

		if(information.equalsIgnoreCase("AGGIORNARE-INDIRIZZO")) {
			if(userDAO.doRetrieveFullUserByKey(user.getUsername()) == null)
				throw new ProfiloInesistenteException("Errore nel recupero delle informazioni relative"
						+ "all'utente");
			else {
				ArrayList<Indirizzo> addresses = addressDAO.doRetrieveAll("", user.getUsername());
				for(Indirizzo search_ind: addresses){
					if(search_ind.getIDIndirizzo() == updatedData.getIDIndirizzo()){
						try {
							if(Indirizzo.checkValidate(updatedData)) {
								if(addresses.contains(updatedData))
									throw new IndirizzoEsistenteException("L'indirizzo inserito è già presente nella tua rubrica degli indirizzi.");

								addressDAO.doUpdateAddress(updatedData, user.getUsername());
							}
							return user;

						} catch (FormatoViaException ex) {
							throw new FormatoViaException("La nuova via deve contenere solo lettere e spazi");

						}catch(FormatoNumCivicoException e) {
							throw new FormatoNumCivicoException("Il nuovo numero civico è composto da numeri e, eventualmente, una lettera.");

						}catch(FormatoCittaException e) {
							throw new FormatoCittaException("La nuova città deve essere composta solo da lettere e spazi.");

						}catch(FormatoCAPException e) {
							throw new FormatoCAPException("Il nuovo CAP deve essere formato da 5 numeri.");

						}catch(FormatoProvinciaException e) {
							throw new FormatoProvinciaException("La nuova provincia è composta da due lettere maiuscole.");
						}
					}
						
				}

				throw new ModificaIndirizzoException("L'indirizzo inserito non è presente nella tua rubrica degli indirizzi.");			
			}
		}

		throw new InformazioneDaModificareException("Selezionare un'informazione del profilo utente da modificare");
	}

}
