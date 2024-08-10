package storage.NavigazioneDAO;

import java.sql.SQLException;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * La classe consente di effettuare le operazioni di caricamento, recupero, aggiunta e rimozione
 * dell'immagine di presentazione (topImage) di un prodotto e della galleria di immagini
 * di dettaglio ad esso associate.
 * 
 * @see package application.NavigazioneService.Prodotto;
 * @author Dorotea Serrelli
 * */

public class PhotoControl {
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
	
	
	/**
	 * Il metodo effettua il recupero dell'immagine di presentazione di un prodotto dal database.
	 * @param id è il codice univoco del prodotto
	 * @return bt l'immagine di presentazione del prodotto
	 * */
	public static synchronized byte[] loadTopImage(int id) throws SQLException {
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		byte[] bt = null;

		try {
			
			connection = ds.getConnection();
			String sql = "SELECT TOPIMMAGINE FROM prodotto WHERE CODICEPRODOTTO = ?";
			stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			if (rs.next()) {
				bt = rs.getBytes("TOPIMMAGINE");
			}

		} catch (SQLException sqlException) {
			System.out.println(sqlException);
		} 
			finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException sqlException) {
				System.out.println(sqlException);
			} finally {
				if (connection != null) 
					connection.close();
			}
		}
		return bt;
	}
	
	/**
	 * Il metodo consente il caricamento nel databse dell'immagine di presentazione del prodotto.
	 * @param idP è l'identificativo numerico del prodotto
	 * @param photo è l'immagine di presentazione da associare al prodotto con codice idP
	 * */
	public static synchronized void updateTopImage(int idP, InputStream photo) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.prepareStatement("UPDATE prodotto SET TOPIMMAGINE = ? WHERE CODICEPRODOTTO = ?");
			try {
				stmt.setBinaryStream(1, photo, photo.available());
				stmt.setInt(2, idP);	
				stmt.executeUpdate();
				con.setAutoCommit(false);
				con.commit();
			} catch (IOException e) {
				System.out.println(e);
			}
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException sqlException) {
				System.out.println(sqlException);
			} finally {
				if (con != null)
					con.close();
			}
		}
	}
	
	/**
	 * Il metodo consente di recuperare una particolare immagine di dettaglio di un prodotto,
	 * memorizzata nel database.
	 * @param idP è il codice del prodotto
	 * @param idI è il codice dell'immagine presente nella galleria delle immagini di dettaglio 
	 * del prodotto
	 * @return bt l'immagine di dettaglio, con codice idI, del prodotto con codice idP
	 * */
	public static synchronized byte[] loadPhotoOfGallery(int idP, int idI) throws SQLException {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		byte[] bt = null;

		try {

			connection = ds.getConnection();
			String sql = "SELECT CONTENUTO FROM immagine_di_dettaglio WHERE (PRODOTTO = ? AND CODICEIMMAGINE = ?)";
			stmt = connection.prepareStatement(sql);

			stmt.setInt(1, idP);
			stmt.setInt(2, idI);
			rs = stmt.executeQuery();

			if (rs.next()) {
				bt = rs.getBytes("CONTENUTO");
			}

		} catch (SQLException sqlException) {
			System.out.println(sqlException);
		} 
		finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException sqlException) {
				System.out.println(sqlException);
			} finally {
				if (connection != null) 
					connection.close();
			}
		}
		return bt;
	}
	
	/**
	 * Il metodo consente di recuperare tutte le immagini di dettaglio di un prodotto,
	 * memorizzate nel database, in termini di identificativi numerici.
	 * @param idP è il codice del prodotto
	 * @return photos gli identificativi delle immagini di dettaglio del prodotto con codice idP
	 * */
	public static synchronized LinkedList<Integer> loadPhotoGallery(int idP) throws SQLException {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		LinkedList<Integer> photos = new LinkedList<Integer>();

		try {

			connection = ds.getConnection();
			String sql = "SELECT CODICEIMMAGINE FROM immagine_di_dettaglio WHERE (PRODOTTO = ?)";
			stmt = connection.prepareStatement(sql);

			stmt.setInt(1, idP);
			rs = stmt.executeQuery();

		    while (rs.next()) {
		      photos.add(rs.getInt("CODICEIMMAGINE"));
		    }

		} catch (SQLException sqlException) {
			System.out.println(sqlException);
		} 
		finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException sqlException) {
				System.out.println(sqlException);
			} finally {
				if (connection != null) 
					connection.close();
			}
		}
		return photos;
	}
	
	/**
	 * Il metodo consente di memorizzare una foto di dettaglio del prodotto nella galleria
	 * di immagini di dettaglio nel database.
	 * @param idP l'identificativo numerico del prodotto
	 * @param photo l'immagine di dettaglio da memorizzare
	 * */
	public static synchronized void addPhotoInGallery(int idP, InputStream photo) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.prepareStatement("INSERT INTO immagine_di_dettaglio(PRODOTTO, CONTENUTO) VALUES(?, ?)");
			try {
				stmt.setInt(1, idP);
				stmt.setBinaryStream(2, photo, photo.available());
				stmt.executeUpdate();
				con.setAutoCommit(false);
				con.commit();
			} catch (IOException e) {
				System.out.println(e);
			}
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException sqlException) {
				System.out.println(sqlException);
			} finally {
				if (con != null)
					con.close();
			}
		}
	}
	
	/**
	 * Il metodo consente di rimuovere una foto di dettaglio del prodotto dalla galleria
	 * di immagini di dettaglio nel database.
	 * @param idP l'identificativo numerico del prodotto
	 * @param idI il codice dell'immagine di dettaglio da rimuovere
	 * */
	public static synchronized void deletePhotoInGallery(int idP, int idI) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.prepareStatement("DELETE FROM immagine_di_dettaglio WHERE (PRODOTTO = ? AND CODICEIMMAGINE = ?)");
			stmt.setInt(1, idP);
			stmt.setInt(2, idI);
			stmt.executeUpdate();
			con.setAutoCommit(false);
			con.commit();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException sqlException) {
				System.out.println(sqlException);
			} finally {
				if (con != null)
					con.close();
			}
		}
	}
}
