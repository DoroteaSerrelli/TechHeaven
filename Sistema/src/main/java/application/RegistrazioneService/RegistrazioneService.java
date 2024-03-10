package application.RegistrazioneService;

import application.RegistrazioneService.Cliente.Sesso;

public interface RegistrazioneService {
	/*
	 * Interfaccia che si occupa di offrire servizi relativi alla
	 * registrazione di un nuovo utente: cliente, gestore degli ordini e 
	 * gestore del catalogo.
	 * */
	
	Utente registraCliente(String username, String password, String email, String nome, String cognome, 
			Sesso sex, String telefono, Indirizzo indirizzo);
	
	Utente registraGestoreOrdini(String username, String password, String email, String nome, String cognome, 
			Sesso sex, String telefono, Indirizzo indirizzo, Ruolo isOrderManager);
	
	Utente registraGestoreCatalogo(String username, String password,String email, String nome, String cognome, 
			Sesso sex, String telefono, Indirizzo indirizzo, Ruolo isCatalogManager);
}
