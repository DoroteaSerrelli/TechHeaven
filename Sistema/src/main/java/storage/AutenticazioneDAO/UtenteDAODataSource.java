package storage.AutenticazioneDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.jdbc.pool.DataSource;

import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.Ruolo;
import application.Registrazione.RegistrazioneService.Utente;

/**
 * Questa classe di data access object (DAO) gestisce le operazioni CRUD (Create, Read, Update, Delete) 
 * sugli utenti nel database.
 * 
 * Utilizzando un DataSource ottenuto tramite JNDI, la classe si connette al database e interagisce 
 * con la tabella "utente" per memorizzare, recuperare, aggiornare ed eliminare utenti.
 */

public class UtenteDAODataSource{
	
	DataSource ds = null;
	
	/*
	private static DataSource ds;

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/techheaven");

		} catch (NamingException e) {
			System.out.println("Error:" + e.getMessage());
		}	
	}*/
	
	private static final String TABLE_NAME = "utente";
	
	public UtenteDAODataSource(DataSource dataSource) throws SQLException{
		this.ds = dataSource;
	}
	public UtenteDAODataSource(){
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			
			this.ds = (DataSource) envCtx.lookup("jdbc/techheaven");
		} catch (NamingException ex) {
			//Logger.getLogger(UtenteDAODataSource.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
	/**
     * Il metodo memorizza un nuovo utente nel database.
     * 
     * @param user_account : l'oggetto Utente contenente i dati dell'utente da memorizzare.
     * @throws SQLException Lancia un' eccezione SQLException in caso di errori con il database.
     */
	public synchronized void doSave(Utente user_account) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertUserSQL = "INSERT INTO " + UtenteDAODataSource.TABLE_NAME
				+ " (USERNAME, USERPASSWORD, EMAIL) VALUES (?, ?, ?);";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertUserSQL);
			preparedStatement.setString(1, user_account.getUsername());
			preparedStatement.setString(2, user_account.getPassword());
			preparedStatement.setString(3, user_account.getProfile().getEmail());

			if(preparedStatement.executeUpdate() == 0) {
				System.out.println("Errore nell'aggiunta dell'utente "+ user_account.getUsername() 
				+ "nel database\n");
			}else {
				/*
				 * Si intende memorizzare tutti i ruoli associati all'utente.
				 * */
				for(Ruolo r : user_account.getRuoli()) {
					RuoloDAODataSource role = new RuoloDAODataSource(ds);
					role.doSave(user_account, r);
				}
				
				/*
				 * Si intende memorizzare le informazioni personali associate all'utente.
				 * */
				
				//Profilo
				ClienteDAODataSource profileDAO = new ClienteDAODataSource(ds);
				profileDAO.doSave(user_account.getProfile());
				
				//Indirizzo
				IndirizzoDAODataSource addressDAO = new IndirizzoDAODataSource(ds);
				for(Indirizzo address : user_account.getProfile().getIndirizzi())
					addressDAO.doSave(address, user_account.getUsername());
			}
			
			connection.setAutoCommit(false);
			connection.commit();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
	}

	/**
     * Il metodo rimuove un utente dal database in base al suo username.
     * 
     * @param username : il nome utente dell'utente da eliminare.
     * @return Restituisce true se l'eliminazione ha avuto successo, false altrimenti.
     * 
     * @throws SQLException Lancia un' eccezione SQLException in caso di errori con il database.
     */
	public synchronized boolean doDelete(String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String deleteSQL = "DELETE FROM " + UtenteDAODataSource.TABLE_NAME + " WHERE USERNAME = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setString(1, username);

			result = preparedStatement.executeUpdate();

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		return (result != 0);
	}
	
	/**
	 * Il metodo permette di resettare la password di un utente.
	 * 
	 * @param username : username dell'utente per cui si vuole effettuare il reset della password.
	 * @param password : la nuova password dell'utente.
	 * 
	 * @throws SQLException Lancia un'eccezione `SQLException` in caso di errori con il database.
	 */
	public synchronized void doResetPassword(String username, String password) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertUserSQL = "UPDATE " + UtenteDAODataSource.TABLE_NAME
				+ " SET USERPASSWORD = ? WHERE USERNAME = ?;";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertUserSQL);
			preparedStatement.setString(1, password);
			preparedStatement.setString(2, username);

			if(preparedStatement.executeUpdate() == 0) {
				System.out.println("Errore nella reimpostazione della password dell'utente "+ username 
				+ "nel database\n");
			}
			
			connection.setAutoCommit(false);
			connection.commit();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
	}

	/**
	 * Il metodo recupera tutti gli utenti presenti nel database e li restituisce in una collezione.
	 * Gli utenti possono essere ordinati in base a un criterio specificato dal parametro `orderCriterion`.
	 * 
	 * @param orderCriterion : il criterio di ordinamento (es. "USERNAME", "EMAIL").
	 *                         Può essere nullo se non si desidera ordinare gli utenti.
	 * @return Restituisce una collezione di oggetti `Utente` contenenti tutti gli utenti del sistema.
	 * 
	 * @throws SQLException Lancia un'eccezione `SQLException` in caso di errori con il database.
	 */
	public synchronized Collection<Utente> doRetrieveAll(String orderCriterion) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Collection<Utente> users = new LinkedList<Utente>();

		String selectSQL = "SELECT * FROM " + UtenteDAODataSource.TABLE_NAME;
		
		if (orderCriterion != null && !orderCriterion.equals("")) {
			selectSQL += " ORDER BY " + orderCriterion;
		}
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				
				String username = rs.getString("USERNAME");
				String password = rs.getString("USERPASSWORD");
				String email = rs.getString("EMAIL");
				ClienteDAODataSource clienteDao = new ClienteDAODataSource(ds);
				Cliente profilo = clienteDao.doRetrieveByKey(email);
				Utente dto = new Utente(username, password, profilo);
				users.add(dto);
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		return users;
	}
	
	/**
	 * Il metodo recupera le informazioni complete di 
	 * un utente specifico dal database, in base al suo username.
	 * @see application.Registrazione.RegistrazioneService.Utente
	 * @see application.Registrazione.RegistrazioneService.ProxyUtente
	 * 
	 * @param username : username dell'utente da recuperare.
	 * @return Restituisce un oggetto `Utente` contenente tutte le informazioni dell'utente specificato,
	 *         oppure un oggetto `Utente` vuoto se l'utente non viene trovato.
	 * 
	 * @throws SQLException Lancia un'eccezione `SQLException` in caso di errori con il database.
	 */
	public synchronized Utente doRetrieveFullUserByKey(String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Utente user = new Utente("", "", null);
		String selectSQL = "SELECT * FROM " + UtenteDAODataSource.TABLE_NAME + " WHERE USERNAME = ?";
		try {
			connection = ds.getConnection();	
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				user.setUsername(rs.getString("USERNAME"));
				user.setPassword(rs.getString("USERPASSWORD"));
				String email = rs.getString("EMAIL");
				ClienteDAODataSource clienteDao = new ClienteDAODataSource(ds);
				Cliente profilo = clienteDao.doRetrieveByKey(email);
				
				/*
				 * Recupero degli indirizzi
				 * */
				IndirizzoDAODataSource addressDao = new IndirizzoDAODataSource(ds);
				ArrayList<Indirizzo> indirizzi = addressDao.doRetrieveAll("IDINDIRIZZO", username);
				profilo.setIndirizzi(indirizzi);
				
				user.setProfile(profilo);
				
			}
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		return user;
	}
	
	/**
	 * Il metodo recupera le informazioni essenziali di un utente (username e password) 
	 * in base allo username fornito.
	 * 
	 * @param username : username dell'utente di cui si vogliono recuperare le informazioni.
	 * @return Restituisce un oggetto ProxyUtente contenente username e password dell'utente, 
	 * 			oppure un oggetto ProxyUtente vuoto se l'utente non viene trovato.
	 * 
	 * @throws SQLException Lancia un' eccezione SQLException in caso di errori con il database.
	 */
	public ProxyUtente doRetrieveProxyUserByKey(String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ProxyUtente user = new ProxyUtente("", "", null);
		String selectSQL = "SELECT * FROM " + UtenteDAODataSource.TABLE_NAME + " WHERE USERNAME = ?";
		try {
			connection = ds.getConnection();	
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				user.setUsername(rs.getString("USERNAME"));
				user.setPassword(rs.getString("USERPASSWORD"));
			}
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		return user;
	}
	
}