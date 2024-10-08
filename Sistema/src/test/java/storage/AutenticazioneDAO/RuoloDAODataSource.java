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

/**
 * Questa classe implementa il Data Access Object (DAO) per l'entità Ruolo nel database.
 * Si occupa della gestione delle operazioni CRUD (Create, Read, Update, Delete) 
 * relative ai ruoli degli utenti.
 *
 * La classe utilizza una connessione a un DataSource ottenuto tramite JNDI per accedere 
 * al database.
 * 
 * @author Dorotea Serrelli
 */

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
	
	/**
     * Il metodo memorizza nel database un nuovo ruolo associato ad un utente.
     *
     * @param user_account : L'utente a cui associare il ruolo
     * @param role : Il ruolo da associare
     * 
     * @throws SQLException - Lanciata in caso di errori di accesso al database
     */
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


	/**
     * Il metodo rimuove tutti i ruoli associati ad un utente.
     * Viene utilizzato quando l'utente viene eliminato dal sistema.
     *
     * @param username : nome utente dell'utente da cui eliminare i ruoli
     * @return true se l'operazione è andata a buon fine, false altrimenti
     * 
     * @throws SQLException - Lanciata in caso di errori di accesso al database
     */
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
	
	
	/**
     * Il metodo rimuove un ruolo specifico associato ad un utente.
     * Se l'utente possiede un solo ruolo associato, allora viene rimosso anche l'utente dal sistema
     * tramite il metodo doDelete(String username).
     *
     * @param username : Lo username dell'utente da cui eliminare il ruolo
     * @param role : Il ruolo da eliminare
     * 
     * @return true se l'operazione è andata a buon fine, false altrimenti
     * @throws SQLException - Lanciata in caso di errori di accesso al database
     */
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

	/**
	 * Il metodo recupera e restituisce l'insieme dei ruoli associati ad un utente specificato dallo username.
	 *
	 * @param username : il nome utente dell'utente di cui si vogliono recuperare i ruoli
	 * @return Una collezione (ArrayList) contenente i ruoli dell'utente. Se nessun ruolo è associato all'utente,
	 *         verrà restituita una lista vuota.
	 *         
	 * @throws SQLException - Lanciata in caso di errori di accesso al database
	 */
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

	/**
	 * Il metodo recupera e restituisce una collezione di utenti che possiedono un determinato ruolo.
	 *
	 * @param role : Il ruolo da ricercare tra gli utenti
	 * @return Una collezione (Collection) contenente gli utenti che possiedono il ruolo specificato. 
	 *         Se nessun utente possiede tale ruolo, verrà restituita una lista vuota.
	 * @throws SQLException - Lanciata in caso di errori di accesso al database
	 */
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
