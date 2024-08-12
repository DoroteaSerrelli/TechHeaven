package storage.AutenticazioneDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import application.RegistrazioneService.Ruolo;
import application.RegistrazioneService.Utente;

public class RuoloDAODataSource{
	
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
	
	private static final String TABLE_NAME = "ruolo";
	
	/*
	 * Questo metodo memorizza nel database un nuovo utente con un ruolo associato.
	 * */
	public synchronized void doSave(Utente user_account, Ruolo role) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertUserRoleSQL = "INSERT INTO " + RuoloDAODataSource.TABLE_NAME
				+ " (UTENTE, NOMERUOLO) VALUES (?, ?);";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertUserRoleSQL);
			preparedStatement.setString(1, user_account.getUsername());
			preparedStatement.setString(2, role.getNomeRuolo());
			

			if(preparedStatement.executeUpdate() == 0) {
				System.out.println("Errore nell'aggiunta del ruolo "+ role.getNomeRuolo() +" per l'utente "
						+ user_account.getUsername() + "nel database\n");
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
	 * Questo metodo rimuove tutti i ruoli associati ad un utente.
	 * Ciò si verifica quando l'utente viene rimosso dal sistema.
	 * */
	public boolean doDelete(String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String deleteSQL = "DELETE FROM " + RuoloDAODataSource.TABLE_NAME + " WHERE UTENTE = ?";

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
	 * Questo metodo rimuove un ruolo associato ad un utente.
	 * Se l'utente possiede un solo ruolo associato, allora si eseguirà il 
	 * metodo doDelete(String username) della classe RuoloDAODataSource.
	 * */
	public boolean doDelete(String username, Ruolo role) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;
		Collection<Ruolo> roles = doRetrieveByKey(username);
		if(roles.size() == 1) {
			for(Ruolo r : roles) {
				if(r.getNomeRuolo().equals(role.getNomeRuolo()))
					return doDelete(username); //si rimuove l'utente dal sistema
				else
					return false;//L'utente non possiede il ruolo role inserito
			}
		}
		String deleteSQL = "DELETE FROM " + RuoloDAODataSource.TABLE_NAME + " WHERE UTENTE = ? AND NOMERUOLO = ?";

		try {
			
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, role.getNomeRuolo());

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
	 * Questo metodo restituisce l'insieme dei ruoli associati ad un utente.
	 * */
	public ArrayList<Ruolo> doRetrieveByKey(String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ArrayList<Ruolo> roles = new ArrayList<>();
		String selectSQL = "SELECT * FROM " + RuoloDAODataSource.TABLE_NAME + " WHERE UTENTE = ?";
		try {
			connection = ds.getConnection();	
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				roles.add(new Ruolo((rs.getString("NOMERUOLO"))));
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
		return roles;
	}

	/*
	 * Questo metodo fornisce tutti gli utenti che hanno un determinato ruolo role.
	 * */
	public Collection<Utente> doRetrieveAllRoleUser(Ruolo role) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ArrayList<Utente> users = new ArrayList<>();
		String selectSQL = "SELECT * FROM " + RuoloDAODataSource.TABLE_NAME + " WHERE NOMERUOLO = ?";
		try {
			connection = ds.getConnection();	
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, role.getNomeRuolo());
			ResultSet rs = preparedStatement.executeQuery();
			UtenteDAODataSource userDao = new UtenteDAODataSource();
			while (rs.next()) {
				users.add(userDao.doRetrieveFullUserByKey((rs.getString("UTENTE"))));
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

}
