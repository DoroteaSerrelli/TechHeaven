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
import javax.sql.DataSource;

import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Ruolo;
import application.RegistrazioneService.Utente;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;

public class UtenteDAODataSource{
	
	private static DataSource ds;

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/techheaven");

		} catch (NamingException e) {
			System.out.println("Error:" + e.getMessage());
		}	
	}
	
	private static final String TABLE_NAME = "utente";
	
	
	/*
	 * Questo metodo memorizza nel database un nuovo utente.
	 * */
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
					RuoloDAODataSource role = new RuoloDAODataSource();
					role.doSave(user_account, r);
				}
				
				/*
				 * Si intende memorizzare le informazioni personali associate all'utente.
				 * */
				
				//Profilo
				ClienteDAODataSource profileDAO = new ClienteDAODataSource();
				profileDAO.doSave(user_account.getProfile());
				
				//Indirizzo
				IndirizzoDAODataSource addressDAO = new IndirizzoDAODataSource();
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

	/*
	 * Questo metodo rimuove un utente dal database.
	 * */
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
	
	/*
	 * RESET DELLA PASSWORD
	 * */
	public synchronized void doResetPassword(String username, String password) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertUserSQL = "UPDATE " + UtenteDAODataSource.TABLE_NAME
				+ " SET USERPASSWORD = " + password + " WHERE USERNAME = ?;";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertUserSQL);
			preparedStatement.setString(1, username);

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

	/*
	 * Questo metodo fornisce tutti gli utenti del sistema e li ordina in base
	 * al criterio orderCriterion.
	 * */
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
				ClienteDAODataSource clienteDao = new ClienteDAODataSource();
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
	
	/*
	 * Questo metodo restituisce un utente in base al suo username.
	 * */
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
				ClienteDAODataSource clienteDao = new ClienteDAODataSource();
				Cliente profilo = clienteDao.doRetrieveByKey(email);
				/*
				 * Recupero degli indirizzi
				 * */
				IndirizzoDAODataSource addressDao = new IndirizzoDAODataSource();
				ArrayList<Indirizzo> indirizzi = addressDao.doRetrieveAll("IDINDIRIZZO", username);
				profilo.setIndirizzi(indirizzi);
				
				/*
				 * Recupero degli ordini
				 * */
				
				
				
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
	
	/*
	 * Questo metodo restituisce le informazioni essenziali di un utente: username e password, in base al suo username.
	 * */
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