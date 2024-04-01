package application.RegistrazioneService;

import java.sql.SQLException;
import java.util.ArrayList;
import storage.AutenticazioneDAO.UtenteDAODataSource;

public class ProxyUtente extends ObjectUtente{
	
	private Utente realUtente;

	public ProxyUtente(String username, String password, ArrayList<Ruolo> ruoli) {
        super(username, password, ruoli);
    }
	
	public ProxyUtente(String username, String password) {
        super(username, password);
    }
	
	public Utente mostraUtente() {
		if(realUtente == null) {
			UtenteDAODataSource userDao = new UtenteDAODataSource();
			try {
				Utente real = userDao.doRetrieveFullUserByKey(username);
				realUtente = real;
			} catch (SQLException e) {
				System.out.println("Errore nel recupero del profilo e degli ordini dell'utente");
			}
		}
		return realUtente;
	}
}
