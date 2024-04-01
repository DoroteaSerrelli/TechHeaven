package storage.AutenticazioneDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import application.RegistrazioneService.Indirizzo;


public class IndirizzoDAODataSource {
	private static DataSource ds;
	private static final Logger LOGGER = Logger.getLogger(IndirizzoDAODataSource.class.getName());

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/techheaven");

		} catch (NamingException e) {
			LOGGER.log(null, "Error:" + e.getMessage());
		}
	}

	private static final String TABLE_NAME = "indirizzo";

	public synchronized void doSave(Indirizzo address, String username) throws SQLException {

		Connection connection = null;
	    PreparedStatement preparedStatement = null;

	    String insertSQL = "INSERT INTO " + IndirizzoDAODataSource.TABLE_NAME
	            + "(IDINDIRIZZO, VIA, NUMCIVICO, CITTA, CAP, PROVINCIA) VALUES (?, ?, ?, ?, ?, ?)";

	    try {
	        connection = ds.getConnection();
	        connection.setAutoCommit(false);
	        preparedStatement = connection.prepareStatement(insertSQL);
	        preparedStatement.setInt(1, address.getIDIndirizzo());
	        preparedStatement.setString(2, address.getVia());
	        preparedStatement.setString(3, address.getNumCivico());
	        preparedStatement.setString(4, address.getCitta());
	        preparedStatement.setString(5, address.getCap());
	        preparedStatement.setString(6, address.getProvincia());

	        preparedStatement.executeUpdate();

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

	    String insertSQL2 = "INSERT INTO POSSIEDE_INDIRIZZO(UTENTE, INDIRIZZO) VALUES (?, ?)";

	    try {
	        connection = ds.getConnection();
	        preparedStatement = connection.prepareStatement(insertSQL2);
	        preparedStatement.setString(1, username);
	        preparedStatement.setInt(2, address.getIDIndirizzo());
	        preparedStatement.executeUpdate();

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


	public synchronized Indirizzo doRetrieveByKey(int IDIndirizzo, String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Indirizzo dto = new Indirizzo(-1, "", "", "", "", "");

		String selectSQL = "SELECT * FROM " + IndirizzoDAODataSource.TABLE_NAME + " WHERE (UTENTE = ? AND INDIRIZZO = ?) ";

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

	/*
	 * Questo metodo rimuove un indirizzo dalla rubrica degli indirizzi dell'utente.
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
	
	/*
	 * Questo metodo rimuove un indirizzo dal database.
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
	
	/*
	 * Questo metodo rimuove un indirizzo dalla rubrica degli indirizzi dell'utente.
	 * */
	public synchronized boolean doUpdateAddress(Indirizzo newAddress, String username) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String updateSQL = "UPDATE POSSIEDE_INDIRIZZO INNER JOIN INDIRIZZO"
				+ "SET VIA = ?, NUMCIVICO = ?, CITTA = ?, CAP = ?, PROVINCIA = ?"
				+ " WHERE (" +
				IndirizzoDAODataSource.TABLE_NAME + ".idIndirizzo = POSSIEDE_INDIRIZZO.indirizzo)"
						+ "AND (UTENTE = ? AND INDIRIZZO = ?)";

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
	
	
	public synchronized ArrayList<Indirizzo> doRetrieveAll(String orderCriterion, String username) throws SQLException {	//lista degli indirizzi dell'utente
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		ArrayList<Indirizzo> addresses = new ArrayList<>();

		String primaryKeyAddressTable = "idIndirizzo";
		String selectSQL = "SELECT * FROM (" + IndirizzoDAODataSource.TABLE_NAME + " INNER JOIN POSSIEDE_INDIRIZZO) "
				+ "WHERE UTENTE = ? AND " + IndirizzoDAODataSource.TABLE_NAME + "." + primaryKeyAddressTable + " = POSSIEDE_INDIRIZZO.INDIRIZZO";

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
	
	public synchronized ArrayList<Indirizzo> doRetrieveAll(String username) throws SQLException {	//lista degli indirizzi dell'utente
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		String primaryKeyAddressTable = "idIndirizzo";
		String selectSQL = "SELECT * FROM (" + IndirizzoDAODataSource.TABLE_NAME + " INNER JOIN POSSIEDE_INDIRIZZO) "
				+ "WHERE UTENTE = ? AND " + IndirizzoDAODataSource.TABLE_NAME + "." + primaryKeyAddressTable + " = POSSIEDE_INDIRIZZO.INDIRIZZO";

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
