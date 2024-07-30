package storage.WishlistDAO;

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

import application.GestioneWishlistService.Wishlist;
import application.NavigazioneService.ProxyProdotto;
import application.RegistrazioneService.ProxyUtente;


public class WishlistDAODataSource{
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
	
	private static final String TABLE_NAME = "wishlist";
	
	/**
	 * Crea una wishlist per l'utente.
	 * @param ws : la wishlist da aggiungere nel DB
	 * */
	public synchronized void doSaveWishlist(Wishlist ws) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertSQL = "INSERT INTO " + WishlistDAODataSource.TABLE_NAME
				+ " (UTENTE) VALUES (?);";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setString(1, ws.getUtente() .getUsername());

			if(preparedStatement.executeUpdate() == 0) {
				System.out.println("Errore creazione wishlist");
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
		
	}


	/**
	 * Questo metodo rimuove una wishlist associata ad un utente.
	 * @param ws : la wishlist da rimuovere dal DB
	 * @return l'esito dell'operazione
	 * */
	
	public synchronized boolean doDeleteWishlist(Wishlist ws) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String deleteSQL = "DELETE FROM " + WishlistDAODataSource.TABLE_NAME + " WHERE UTENTE = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());

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
	 * Questo metodo verifica la presenza di una wishlist per un utente.
	 * @param user : l'utente
	 * @return la wishlist dell'utente
	 * */
	public synchronized Wishlist doRetrieveWishlistByKey(ProxyUtente user) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Wishlist dto = new Wishlist(user);
		String selectSQL = "SELECT * FROM " + WishlistDAODataSource.TABLE_NAME + " WHERE USERNAME = ?";
		try {
			connection = ds.getConnection();	
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, user.getUsername());
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				if(rs.getString("USERNAME").equals(user.getUsername()))
					dto.setUtente(user);
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
		ArrayList<ProxyProdotto> temp = new ArrayList<> (doRetrieveAll("", dto));
		dto.setProdotti(temp);
		
		return dto;
	}	
	
	/**
	 * Questo metodo consente di aggiungere un nuovo prodotto alla wishlist.
	 * @param product : il prodotto da aggiungere
	 * @param ws : la wishlist
	 * @return la wishlist con il prodotto product aggiunto
	 * */
	public synchronized void doSaveProduct(ProxyProdotto product, Wishlist ws) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		
		if(doRetrieveWishlistByKey(ws.getUtente()) == null ) {
			doSaveWishlist(ws);
		}
		
		String insertSQL = "INSERT INTO COMPOSIZIONE_WISHLIST(UTENTE, PRODOTTO) VALUES (?, ?);";

		try {
			connection = ds.getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());
			preparedStatement.setInt(2, product.getCodiceProdotto());

			if(preparedStatement.executeUpdate() == 0) {
				System.out.println("Errore inserimento prodotto in wishlist");
			}

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
	 * Questo metodo rimuove un prodotto presente nella wishlist.
	 * @param IDProduct : il codice del prodotto da rimuovere
	 * @param ws : la wishlist nella quale rimuovere il prodotto
	 * @return la wishlist con il prodotto di codice IDProduct rimosso 
	 * */
	
	public synchronized Wishlist doDeleteProduct(int IDProduct, Wishlist ws) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		if(doRetrieveWishlistByKey(ws.getUtente()) == null) {
			return null;
		}
		
		if(ws.getProdotti().size() == 1)
			doDeleteWishlist(ws);

		int result = 0;

		String deleteSQL = "DELETE FROM COMPOSIZIONE_WISHLIST WHERE (UTENTE = ? AND CODICEPRODOTTO = ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());
			preparedStatement.setInt(2, IDProduct);

			result = preparedStatement.executeUpdate();

		}finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}
		
		return result != 0? doRetrieveWishlistByKey(ws.getUtente()) : null;
	}

	/**
	 * Questo metodo restituisce l'insieme dei prodotti memorizzati nella 
	 * wishlist di un utente.
	 * 
	 * @param order : l'ordine con cui si visualizzano i prodotti recuperati dal DB
	 * @param ws : la wishlist per la quale visualizzare i prodotti
	 * @return i prodotti memorizzati nella wishlist
	 * */
	public synchronized Collection<ProxyProdotto> doRetrieveAll(String order, Wishlist ws) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyProdotto> products = new LinkedList<>();

		String selectSQL = "SELECT * FROM (" + WishlistDAODataSource.TABLE_NAME + " INNER JOIN COMPOSIZIONE_WISHLIST)"
				+ " INNER JOIN PRODOTTO "
				+ "WHERE USERNAME = ? AND "
				+ "COMPOSIZIONE_WISHLIST.WISHLIST = " + WishlistDAODataSource.TABLE_NAME + ".IDWISHLIST"
				+ " AND COMPOSIZIONE_WISHLIST.UTENTE = " + WishlistDAODataSource.TABLE_NAME + ".UTENTE"
				+ " AND COMPOSIZIONE_WISHLIST.PRODOTTO = PRODOTTO.CODICEPRODOTTO";
				
		
		if (order != null && !order.equals("")) { //ordine sui prodotti da recuperare
			selectSQL += " ORDER BY " + order;
		}
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1,  ws.getUtente().getUsername());

			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				ProxyProdotto dto = new ProxyProdotto();

				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOP_DESCRIZIONE"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInVetrina(rs.getBoolean("INVETRINA"));
				dto.setInCatalogo(rs.getBoolean("INCATALOGO"));
				products.add(dto);
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
		return products;
	}
	
	/**
	 * Questo metodo recupera un determinato prodotto dalla wishlist.
	 * @param IDProduct : il codice univoco del prodotto da recuperare
	 * @param ws : la wishlist
	 * @return il prodotto con codice IDProduct presente nella wishlist ws; altrimenti
	 * restituisce un puntatore ad un oggetto nullo
	 * */
	public synchronized ProxyProdotto doRetrieveProductByKey(int IDProduct, Wishlist ws) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ProxyProdotto dto = new ProxyProdotto();
		String selectSQL = "SELECT * FROM (" + WishlistDAODataSource.TABLE_NAME + " INNER JOIN COMPOSIZIONE_WISHLIST)"
				+ " INNER JOIN PRODOTTO "
				+ "WHERE USERNAME = ? AND CODICEPRODOTTO = ? "
				+ " AND COMPOSIZIONE_WISHLIST.WISHLIST = " + WishlistDAODataSource.TABLE_NAME + ".IDWISHLIST"
				+ " AND COMPOSIZIONE_WISHLIST.UTENTE = " + WishlistDAODataSource.TABLE_NAME + ".UTENTE"
				+ " AND COMPOSIZIONE_WISHLIST.PRODOTTO = PRODOTTO.CODICEPRODOTTO";

		try {
			connection = ds.getConnection();	
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());
			preparedStatement.setInt(2, IDProduct);
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOP_DESCRIZIONE"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInVetrina(rs.getBoolean("INVETRINA"));
				dto.setInCatalogo(rs.getBoolean("INCATALOGO"));
				
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
}