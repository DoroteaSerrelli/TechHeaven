package storage.AutenticazioneDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import application.RegistrazioneService.Indirizzo;

/**
 * 
 * Classe DAO per la gestione degli indirizzi di un utente.
 * Questa classe implementa le operazioni CRUD (Create, Read, Update, Delete)
 * per gli indirizzi di un cliente memorizzati nel database relazionale.
 * 
 * @see application.RegistrazioneService.Indirizzo
 * @see application.RegistrazioneService.Utente
 * @see application.RegistrazioneService.ProxyUtente
 * 
 * 
 * @author Dorotea Serrelli
 * */

public class IndirizzoDAODataSource {
	
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

	private static final String TABLE_NAME = "indirizzo";
	
	public IndirizzoDAODataSource(DataSource dataSource) throws SQLException{
		this.ds = dataSource;
	}

	/**
	 * Questo metodo salva un indirizzo nel database e lo associa all'utente specificato tramite username.
	 * 
	 * @param address : l'indirizzo da salvare
	 * @param username : il nome utente dell'utente a cui associare l'indirizzo
	 * @throws SQLException : in caso di errore durante l'accesso al database
	 * */

	public synchronized boolean doSave(Indirizzo address, String username) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int result = 0;
		
		String insertSQL = "INSERT INTO " + IndirizzoDAODataSource.TABLE_NAME
				+ "(IDINDIRIZZO, VIA, NUMCIVICO, CITTA, CAP, PROVINCIA) VALUES (?, ?, ?, ?, ?, ?)";

		try {
			connection = ds.getConnection();
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, address.getIDIndirizzo());
			preparedStatement.setString(2, address.getVia());
			preparedStatement.setString(3, address.getNumCivico());
			preparedStatement.setString(4, address.getCitta());
			preparedStatement.setString(5, address.getCap());
			preparedStatement.setString(6, address.getProvincia());
			preparedStatement.executeUpdate();

			// Recupera l'ID generato
			ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				int generatedID = generatedKeys.getInt(1);
				address.setIDIndirizzo(generatedID);  // Imposta l'ID generato sull'oggetto address
				System.out.println("Debug Indirizzo ID:" + generatedID);
			} else {
				throw new SQLException("Errore creazione indirizzo, non è possibile recuperare l'ultimo ID generato.");
			}

			// Secondo insert
			String insertSQL2 = "INSERT INTO POSSIEDE_INDIRIZZO(UTENTE, INDIRIZZO) VALUES (?, ?)";
			preparedStatement.close();
			preparedStatement = connection.prepareStatement(insertSQL2);
			preparedStatement.setString(1, username);
			preparedStatement.setInt(2, address.getIDIndirizzo());
			result = preparedStatement.executeUpdate();

			connection.commit();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result != 0;

	}

	
	/**
	 * Questo metodo recupera un indirizzo dal database filtrando per l'ID dell'indirizzo e l'username dell'utente a cui è associato.
	 * @param IDIndirizzo : L'identificativo dell'indirizzo da recuperare
	 * @param username : il nome utente dell'utente a cui è associato l'indirizzo
	 * 
	 * @return L'oggetto Indirizzo recuperato dal database, oppure un oggetto vuoto se l'indirizzo non viene trovato
	 * @throws SQLException Lanciata in caso di errore durante l'accesso al database
	 * */
	
	public synchronized Indirizzo doRetrieveByKey(int IDIndirizzo, String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Indirizzo dto = new Indirizzo(-1, "", "", "", "", "");

		String selectSQL = "SELECT * FROM " + IndirizzoDAODataSource.TABLE_NAME + 
				" INNER JOIN POSSIEDE_INDIRIZZO ON (POSSIEDE_INDIRIZZO.INDIRIZZO = "
				+ IndirizzoDAODataSource.TABLE_NAME + ".IDINDIRIZZO )"+
				" WHERE (UTENTE = ? AND INDIRIZZO = ?) ";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, username);
			preparedStatement.setInt(2, IDIndirizzo);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				dto.setIDIndirizzo(rs.getInt("IDINDIRIZZO"));
				dto.setVia(rs.getString("VIA"));
				dto.setNumCivico(rs.getString("NUMCIVICO"));
				dto.setCitta(rs.getString("CITTA"));
				dto.setCap(rs.getString("CAP"));
				dto.setProvincia(rs.getString("PROVINCIA"));
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
		return dto;
	}

	/**
	 * Questo metodo rimuove un indirizzo (identificato dall'ID) dalla rubrica degli indirizzi dell'utente (identificato da username).
	 * 
	 * @param IDIndirizzo : L'identificativo dell'indirizzo da eliminare
	 * @param username : il nome utente dell'utente a cui è associato l'indirizzo
	 * 
	 * @return true se l'indirizzo è stato eliminato con successo, false altrimenti
	 * @throws SQLException Lanciata in caso di errore durante l'accesso al database
	 * 
	 * */
	
	public synchronized boolean doDeleteAddress(int IDIndirizzo, String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String deleteSQL = "DELETE FROM POSSIEDE_INDIRIZZO WHERE (UTENTE = ? AND INDIRIZZO = ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setString(1, username);
			preparedStatement.setInt(2, IDIndirizzo);

			result = preparedStatement.executeUpdate();
			doDelete(IDIndirizzo);

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
	 * Questo metodo rimuove un indirizzo dal database.
	 * 
	 * @param IDIndirizzo: L'identificativo dell'indirizzo da eliminare
	 * @return true se l'indirizzo è stato eliminato con successo, false altrimenti
	 * 
	 * @throws SQLException Lanciata in caso di errore durante l'accesso al database
	 * */
	
	public synchronized boolean doDelete(int IDIndirizzo) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet risultato = null;

		int result = 0;

		String selectSQL = "SELECT * FROM POSSIEDE_INDIRIZZO WHERE (INDIRIZZO = ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDIndirizzo);

			risultato = preparedStatement.executeQuery();
			if(risultato.next()) {
				result = 1;
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

		if(result == 1) {
			String deleteSQL = "DELETE FROM "+IndirizzoDAODataSource.TABLE_NAME+ " WHERE (IDINDIRIZZO = ?)";
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setInt(1, IDIndirizzo);

			result = preparedStatement.executeUpdate();
		}

		return (result != 0);
	}

	/**
	 * Questo metodo aggiorna un indirizzo presente nella rubrica degli indirizzi dell'utente,
	 * filtrando per l'ID dell'indirizzo e lo username dell'utente a cui è associato.
	 * @param newAddress : l'indirizzo contenente i dati aggiornati
	 * @param username : il nome utente dell'utente a cui è associato l'indirizzo
	 * 
	 * @return true se l'indirizzo è stato aggiornato con successo, false altrimenti
	 * @throws SQLException Lanciata in caso di errore durante l'accesso al database
	 * */
	public synchronized boolean doUpdateAddress(Indirizzo newAddress, String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String updateSQL = "UPDATE POSSIEDE_INDIRIZZO INNER JOIN INDIRIZZO ON "
				+ IndirizzoDAODataSource.TABLE_NAME + ".idIndirizzo = POSSIEDE_INDIRIZZO.indirizzo "
				+ "SET VIA = ?, NUMCIVICO = ?, CITTA = ?, CAP = ?, PROVINCIA = ?"
				+ " WHERE (POSSIEDE_INDIRIZZO.UTENTE = ? AND POSSIEDE_INDIRIZZO.INDIRIZZO = ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setString(1, newAddress.getVia());
			preparedStatement.setString(2, newAddress.getNumCivico());
			preparedStatement.setString(3, newAddress.getCitta());
			preparedStatement.setString(4, newAddress.getCap());
			preparedStatement.setString(5, newAddress.getProvincia());
			preparedStatement.setString(6, username);
			preparedStatement.setInt(7, newAddress.getIDIndirizzo());

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
	 * Questo metodo recupera tutti gli indirizzi associati ad un utente specificato tramite username. 
	 * È possibile specificare un criterio di ordinamento tramite il parametro orderCriterion.
	 * 
	 * @param orderCriterion : il criterio di ordinamento (può essere vuota)
	 * @param username : il nome utente dell'utente a cui sono associati gli indirizzi
	 * 
	 * @return Una lista di indirizzi associati all'utente username recuperati dal database
	 * @throws SQLException Lanciata in caso di errore durante l'accesso al database
	 * */

	public synchronized ArrayList<Indirizzo> doRetrieveAll(String orderCriterion, String username) throws SQLException {	//lista degli indirizzi dell'utente
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		ArrayList<Indirizzo> addresses = new ArrayList<>();

		String primaryKeyAddressTable = "idIndirizzo";
		String selectSQL = "SELECT * FROM " + IndirizzoDAODataSource.TABLE_NAME + " INNER JOIN POSSIEDE_INDIRIZZO ON  "
				+ IndirizzoDAODataSource.TABLE_NAME + "." + primaryKeyAddressTable + " = POSSIEDE_INDIRIZZO.INDIRIZZO "
				+ "WHERE POSSIEDE_INDIRIZZO.UTENTE = ?";

		if (orderCriterion != null && !orderCriterion.equals("")) {
			selectSQL += " ORDER BY " + orderCriterion;
		}

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, username);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Indirizzo dto = new Indirizzo(-1, "", "", "", "", "");

				dto.setIDIndirizzo(rs.getInt("IDINDIRIZZO"));
				dto.setVia(rs.getString("VIA"));
				dto.setNumCivico(rs.getString("NUMCIVICO"));
				dto.setCitta(rs.getString("CITTA"));
				dto.setCap(rs.getString("CAP"));
				dto.setProvincia(rs.getString("PROVINCIA"));
				addresses.add(dto);
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
		return addresses;
	}
}
