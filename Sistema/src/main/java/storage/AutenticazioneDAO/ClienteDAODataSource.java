package storage.AutenticazioneDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.tomcat.jdbc.pool.DataSource;

import application.Registrazione.RegistrazioneService.Cliente;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import storage.NavigazioneDAO.ProdottoDAODataSource;


public class ClienteDAODataSource{
	
	DataSource ds;

	private static final String TABLE_NAME = "Cliente_DatiPersonali";

        public ClienteDAODataSource(DataSource dataSource) throws SQLException{
		this.ds = dataSource;
	}
	 public ClienteDAODataSource(){
            try {
                Context initCtx = new InitialContext();
                Context envCtx = (Context) initCtx.lookup("java:comp/env");
                
                this.ds = (DataSource) envCtx.lookup("jdbc/techheaven");
            } catch (NamingException ex) {
                Logger.getLogger(ProdottoDAODataSource.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
	public synchronized void doSave(Cliente user) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertSQL = "INSERT INTO " + ClienteDAODataSource.TABLE_NAME
				+ " (EMAIL, NOME, COGNOME, SESSO, TELEFONO) VALUES (?, ?, ?, ?, ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setString(1, user.getEmail());
			preparedStatement.setString(2, user.getNome());
			preparedStatement.setString(3, user.getCognome());
			preparedStatement.setString(4, user.getSexAsString());
			preparedStatement.setString(5, user.getTelefono());

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


	public synchronized Cliente doRetrieveByKey(String email) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Cliente dto = new Cliente("", "", "", null, "", new ArrayList<>());

		String selectSQL = "SELECT * FROM " + ClienteDAODataSource.TABLE_NAME + " WHERE EMAIL = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, email);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				dto.setEmail(rs.getString("EMAIL"));
				dto.setNome(rs.getString("NOME"));
				dto.setCognome(rs.getString("COGNOME"));
				dto.setTelefono(rs.getString("TELEFONO"));
				dto.setSex(rs.getString("SESSO"));
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


	public synchronized boolean doDelete(String email) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String deleteSQL = "DELETE FROM " + ClienteDAODataSource.TABLE_NAME + " WHERE EMAIL = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setString(1, email);

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
	 * Questo metodo modifica l'email
	 * */
	
	public synchronized boolean updateEmail(String exEmail, String newEmail) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;
		
		String updateSQL = "UPDATE " + ClienteDAODataSource.TABLE_NAME + 
				" SET EMAIL = ? WHERE EMAIL = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setString(1, newEmail);
			preparedStatement.setString(2, exEmail);

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
	 * Questo metodo modifica il numero di telefono
	 * */
	
	public synchronized boolean updateTelephone(String email, String newTel) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;
		
		String updateSQL = "UPDATE " + ClienteDAODataSource.TABLE_NAME + 
				" SET TELEFONO = ? WHERE EMAIL = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setString(1, newTel);
			preparedStatement.setString(2, email);

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
}