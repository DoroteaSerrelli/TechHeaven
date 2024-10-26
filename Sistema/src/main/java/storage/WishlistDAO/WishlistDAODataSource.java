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

import org.apache.tomcat.jdbc.pool.DataSource;

import application.GestioneWishlistService.Wishlist;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProxyProdotto;
import application.RegistrazioneService.ProxyUtente;

/**
 * Classe DAO per la gestione di una wishlist.
 * Questa classe implementa le operazioni CRUD (Create, Read, Update, Delete)
 * per le wishlist di un cliente memorizzate nel database relazionale.
 * @see application.GestioneWishlistService.Wishlist
 * @see application.GestioneWishlistService.WishlistException
 * 
 * @author Dorotea Serrelli
 * */
public class WishlistDAODataSource{
	DataSource ds;

	private static final String TABLE_NAME = "wishlist";

	public WishlistDAODataSource(DataSource dataSource) throws SQLException{
		this.ds = dataSource;
	}
	
	/**
	 * Crea una wishlist per l'utente.
	 * 
	 * @param ws : la wishlist da aggiungere nel DB
	 * */
	public synchronized void doSaveWishlist(Wishlist ws) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int newId = 0;

		try {
			connection = ds.getConnection();
			// Determina il codice ID dell'ultima wishlist generata per l'utente
			int maxIdSQL = getWishlistCount(ws.getUtente());

			
			String insertSQL = "INSERT INTO " + WishlistDAODataSource.TABLE_NAME + " (UTENTE, IDWISHLIST) VALUES (?, ?)";
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());
			preparedStatement.setInt(2, newId);

			if (preparedStatement.executeUpdate() == 0) {
				System.out.println("Errore creazione wishlist");
			} else {
				ws.setId(maxIdSQL+1);  // Imposta ID nella wishlist
				System.out.println("Wishlist created with ID: " + newId);
			}

		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			
			try {
				if (preparedStatement != null) preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * Questo metodo fornisce il numero di wishlist che l'utente possiede.
	 * 
	 * @param user : l'utente
	 * @return il numero di wishlist di cui l'utente user è
	 * 			proprietario
	 * */

	public synchronized int getWishlistCount(ProxyUtente user) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String query = "SELECT COUNT(*) FROM WISHLIST WHERE UTENTE = ?";

		int count = 0;

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, user.getUsername());

			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next())
				count = rs.getInt(1);

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection != null)
					connection.close();
			}
		}

		return count;
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

		String deleteSQL = "DELETE FROM " + WishlistDAODataSource.TABLE_NAME + " WHERE UTENTE = ? AND IDWISHLIST = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());
			preparedStatement.setInt(2, ws.getId());

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
	 * Questo metodo verifica la presenza di una particolare 
	 * wishlist per un utente.
	 * 
	 * @param user : l'utente
	 * @param id : identificativo della wishlist
	 * 
	 * @return la wishlist con codice id dell'utente
	 * @throws CategoriaProdottoException 
	 * */
	public synchronized Wishlist doRetrieveWishlistByKey(ProxyUtente user, int id) throws SQLException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Wishlist dto = new Wishlist(user);
		String selectSQL = "SELECT * FROM " + WishlistDAODataSource.TABLE_NAME + " WHERE UTENTE = ? AND IDWISHLIST = ?";
		try {
			connection = ds.getConnection();	
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, user.getUsername());
			preparedStatement.setInt(2, id);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				if(rs.getString("UTENTE").equals(user.getUsername()) && rs.getInt("IDWISHLIST") == id) {
					dto.setUtente(user);
					dto.setId(id);
				}
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
		Collection<ProxyProdotto> temp = new ArrayList<> (doRetrieveAllWishes("", dto));
		dto.setProdotti(temp);

		return dto;
	}	


	/**
	 * Questo metodo consente di aggiungere un nuovo prodotto alla wishlist.
	 * 
	 * @param product : il prodotto da aggiungere
	 * @param ws : la wishlist
	 * 
	 * @throws CategoriaProdottoException 
	 * */
	public synchronized boolean doSaveProduct(ProxyProdotto product, Wishlist ws) throws SQLException, CategoriaProdottoException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;


		if(doRetrieveWishlistByKey(ws.getUtente(), ws.getId()) == null ) {
			doSaveWishlist(ws);
		}

		String insertSQL = "INSERT INTO COMPOSIZIONE_WISHLIST(UTENTE, WISHLIST, PRODOTTO) VALUES (?, ?, ?);";
		int result;
		try {
			connection = ds.getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());
			preparedStatement.setInt(2, ws.getId());
			preparedStatement.setInt(3, product.getCodiceProdotto());

			if((result = preparedStatement.executeUpdate()) == 0) {
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
		
		return result != 0;
	}

	/**
	 * Questo metodo rimuove un prodotto presente nella wishlist.
	 * @param IDProduct : il codice del prodotto da rimuovere
	 * @param ws : la wishlist nella quale rimuovere il prodotto
	 * @return la wishlist con il prodotto di codice IDProduct rimosso 
	 * @throws CategoriaProdottoException 
	 * */

	public synchronized Wishlist doDeleteProduct(int IDProduct, Wishlist ws) throws SQLException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		if(doRetrieveWishlistByKey(ws.getUtente(), ws.getId()) == null) {
			return null;
		}

		if(ws.getProdotti().size() == 1)
			doDeleteWishlist(ws);

		int result = 0;

		String deleteSQL = "DELETE FROM COMPOSIZIONE_WISHLIST WHERE (UTENTE = ? AND WISHLIST =? AND PRODOTTO = ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());
			preparedStatement.setInt(2, ws.getId());
			preparedStatement.setInt(3, IDProduct);

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

		return result != 0? doRetrieveWishlistByKey(ws.getUtente(), ws.getId()) : null;
	}

	/**
	 * Questo metodo restituisce l'insieme dei prodotti memorizzati nella 
	 * wishlist di un utente.
	 * 
	 * @param order : l'ordine con cui si visualizzano i prodotti recuperati dal DB
	 * @param ws : la wishlist per la quale visualizzare i prodotti
	 * @return i prodotti memorizzati nella wishlist
	 * @throws CategoriaProdottoException 
	 * */
	public synchronized Collection<ProxyProdotto> doRetrieveAllWishes(String order, Wishlist ws) throws SQLException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyProdotto> products = new LinkedList<>();

		String selectSQL = "SELECT * FROM (" + WishlistDAODataSource.TABLE_NAME + " INNER JOIN COMPOSIZIONE_WISHLIST ON " 
				+"COMPOSIZIONE_WISHLIST.WISHLIST = " + WishlistDAODataSource.TABLE_NAME + ".IDWISHLIST"
				+ " AND COMPOSIZIONE_WISHLIST.UTENTE = " + WishlistDAODataSource.TABLE_NAME + ".UTENTE"
				+ ") INNER JOIN PRODOTTO ON "
				+ "COMPOSIZIONE_WISHLIST.PRODOTTO = PRODOTTO.CODICEPRODOTTO "
				+ "WHERE WISHLIST.UTENTE = ? AND WISHLIST.IDWISHLIST = ? ";


		if (order != null && !order.equals("")) { //ordine sui prodotti da recuperare
			selectSQL += " ORDER BY " + order;
		}

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1,  ws.getUtente().getUsername());
			preparedStatement.setInt(2,  ws.getId());

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ProxyProdotto dto = new ProxyProdotto();

				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
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
	 * Questo metodo restituisce l'insieme di tutte le wishlist di un utente.
	 * 
	 * @param order : l'ordine con cui si visualizzano i prodotti recuperati dal DB
	 * @param user : l'utente
	 * @return le wishlist dell'utente user
	 * @throws CategoriaProdottoException 
	 * */
	public synchronized Collection<Wishlist> doRetrieveAllWishesUser(String order, ProxyUtente user) throws SQLException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Collection<Wishlist> wishlistes = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + WishlistDAODataSource.TABLE_NAME + " WHERE UTENTE = ? ";


		if (order != null && !order.equals("")) { //ordine sui prodotti da recuperare
			selectSQL += " ORDER BY " + order;
		}

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1,  user.getUsername());

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Wishlist dto = new Wishlist(user);

				dto.setId(rs.getInt("IDWISHLIST"));
				dto.setProdotti(doRetrieveAllWishes(order, dto));

				wishlistes.add(dto);
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
		return wishlistes;
	}
	
	/**
	 * Questo metodo restituisce la wishlist di un utente (vincolo: ogni utente ha una sola wishlist).
	 * 
	 * @param user : l'utente
	 * @return dto : la wishlist dell'utente user
	 * @throws CategoriaProdottoException 
	 * */
	public synchronized Wishlist doRetrieveAllWishUser(ProxyUtente user) throws SQLException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Wishlist dto = new Wishlist(user);

		String selectSQL = "SELECT * FROM " + WishlistDAODataSource.TABLE_NAME + " WHERE UTENTE = ? ";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1,  user.getUsername());

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				
				dto.setId(rs.getInt("IDWISHLIST"));
				dto.setProdotti(doRetrieveAllWishes("", dto));
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
	 * Questo metodo recupera un determinato prodotto dalla wishlist.
	 * @param IDProduct : il codice univoco del prodotto da recuperare
	 * @param ws : la wishlist
	 * @return il prodotto con codice IDProduct presente nella wishlist ws; altrimenti
	 * restituisce un puntatore ad un oggetto nullo
	 * @throws CategoriaProdottoException 
	 * */
	public synchronized ProxyProdotto doRetrieveProductByKey(int IDProduct, Wishlist ws) throws SQLException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ProxyProdotto dto = null;
		String selectSQL = "SELECT * FROM " + WishlistDAODataSource.TABLE_NAME + " "
				+ "INNER JOIN COMPOSIZIONE_WISHLIST ON " + WishlistDAODataSource.TABLE_NAME + ".IDWISHLIST = COMPOSIZIONE_WISHLIST.WISHLIST "
				+ "AND " + WishlistDAODataSource.TABLE_NAME + ".UTENTE = COMPOSIZIONE_WISHLIST.UTENTE "
				+ "INNER JOIN PRODOTTO ON COMPOSIZIONE_WISHLIST.PRODOTTO = PRODOTTO.CODICEPRODOTTO "
				+ "WHERE " + WishlistDAODataSource.TABLE_NAME + ".UTENTE = ? "
				+ "AND " + WishlistDAODataSource.TABLE_NAME + ".IDWISHLIST = ? "
				+ "AND PRODOTTO.CODICEPRODOTTO = ?";
		try {
			connection = ds.getConnection();	
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, ws.getUtente().getUsername());
			preparedStatement.setInt(2, ws.getId());
			preparedStatement.setInt(3, IDProduct);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				dto = new ProxyProdotto();
				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
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
