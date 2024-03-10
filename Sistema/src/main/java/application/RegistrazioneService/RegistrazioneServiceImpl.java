package application.RegistrazioneService;

import application.RegistrazioneService.Cliente.Sesso;

public class RegistrazioneServiceImpl implements RegistrazioneService{

	@Override
	/*
	 * Implementa la funzionalità di registrazione di un cliente.
	 * */
	public Utente registraCliente(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo) {
		if(Utente.checkValidate(username, password)) {
			if(Cliente.checkValidate(email, nome, cognome, sex, telefono, indirizzo)) {
				Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
				return new Utente(username, password, profile);
			}
			return null;
		}
		return null;
	}

	@Override
	/*
	 * Implementa la funzionalità di registrazione di un gestore degli ordini.
	 * */
	public Utente registraGestoreOrdini(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo, Ruolo isOrderManager) {
		if(Utente.checkValidate(username, password)) {
			if(Cliente.checkValidate(email, nome, cognome, sex, telefono, indirizzo)) {
				Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
				return new Utente(username, password, profile, isOrderManager);
			}
			return null;
		}
		return null;
	}

	@Override
	/*
	 * Implementa la funzionalità di registrazione di un gestore del catalogo.
	 * */
	public Utente registraGestoreCatalogo(String username, String password, String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo, Ruolo isCatalogManager) {
		if(Utente.checkValidate(username, password)) {
			if(Cliente.checkValidate(email, nome, cognome, sex, telefono, indirizzo)) {
				Cliente profile = new Cliente(email, nome, cognome, sex, telefono, indirizzo);
				return new Utente(username, password, profile, isCatalogManager);
			}
			return null;
		}
		return null;
	}

}
