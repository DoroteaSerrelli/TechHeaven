package storage.NavigazioneDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ArrayList;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.NavigazioneService.ProxyProdotto;

/**
 * Classe DAO per la gestione di un prodotto.
 * Questa classe implementa le operazioni CRUD (Create, Read, Update, Delete)
 * per i prodotti memorizzati nel database relazionale.
 * 
 * @see application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ProxyProdotto
 * @see storage.NavigazioneDAO.PhotoControl
 * 
 * @author Dorotea Serrelli
 * */

public class ProdottoDAODataSource{
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

	private static final String TABLE_NAME = "prodotto";


	/**
	 * Il metodo permette di memorizzare un nuovo prodotto nel database.
	 * 
	 * @param product : il prodotto da memorizzare nella base di dati
	 * */
	public synchronized void doSave(Prodotto product) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertSQL = "INSERT INTO " + ProdottoDAODataSource.TABLE_NAME
				+ " (CODICEPRODOTTO, NOME, TOPDESCRIZIONE, DETTAGLI, PREZZO, CATEGORIA, SOTTOCATEGORIA, MARCA, MODELLO, INCATALOGO, INVETRINA,"
				+ "TOPIMMAGINE, QUANTITà) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertSQL);
			preparedStatement.setInt(1, product.getCodiceProdotto());
			preparedStatement.setString(2, product.getNomeProdotto());
			preparedStatement.setString(3, product.getTopDescrizione());
			preparedStatement.setString(4, product.getDettagli());
			preparedStatement.setFloat(5, product.getPrezzo());
			preparedStatement.setString(6, product.getCategoriaAsString());
			preparedStatement.setString(7, product.getSottocategoriaAsString());
			preparedStatement.setString(8, product.getMarca());
			preparedStatement.setString(9, product.getModello());
			preparedStatement.setInt(10, product.isInCatalogoInt());
			preparedStatement.setInt(11, product.isInVetrinaInt());
			preparedStatement.setBytes(12, product.getTopImmagine());
			preparedStatement.setInt(13, product.getQuantita());


			System.out.println("Righe aggiornate: " + preparedStatement.executeUpdate());

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
	 * Il metodo permette di recuperare le informazioni di un prodotto, eccetto la descrizione dettagliata,
	 * l'immagine di presentazione e la galleria di immagini di dettaglio.
	 * 
	 * @param IDProduct : il codice univoco del prodotto da recuperare nella base di dati
	 * 
	 * @return dto: un oggetto della classe ProxyProdotto contenente le informazioni
	 * 				essenziali del prodotto con codice IDProduct 
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	public synchronized ProxyProdotto doRetrieveProxyByKey(int IDProduct) throws SQLException, SottocategoriaProdottoException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		ProxyProdotto dto = new ProxyProdotto();

		String selectSQL = "SELECT * FROM " + ProdottoDAODataSource.TABLE_NAME + " WHERE CODICEPRODOTTO = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDProduct);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setSottocategoria(rs.getString("SOTTOCATEGORIA"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInCatalogo(rs.getInt("INCATALOGO") == 1 ? true : false);
				dto.setInVetrina(rs.getInt("INVETRINA") == 1 ? true : false);
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
	 * Il metodo permette di recuperare tutte le informazioni associate ad un prodotto,
	 * incluse: descrizione dettagliata, immagine di presentazione e 
	 * galleria di immagini di dettaglio.
	 * 
	 * @param IDProduct : il codice univoco del prodotto da recuperare nella base di dati
	 * 
	 * @return dto: un oggetto della classe Prodotto contenente tutte le informazioni
	 * 				del prodotto con codice IDProduct 
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	public synchronized Prodotto doRetrieveCompleteByKey(int IDProduct) throws SQLException, SottocategoriaProdottoException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Prodotto dto = new Prodotto();

		String selectSQL = "SELECT * FROM " + ProdottoDAODataSource.TABLE_NAME + " WHERE CODICEPRODOTTO = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDProduct);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
				dto.setDettagli(rs.getString("DETTAGLI"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setSottocategoria(rs.getString("SOTTOCATEGORIA"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInCatalogo(rs.getInt("INCATALOGO") == 1 ? true : false);
				dto.setInVetrina(rs.getInt("INVETRINA") == 1 ? true : false);
				dto.setTopImmagine(rs.getBytes("TOPIMMAGINE"));
				LinkedList<Integer> idImages = PhotoControl.loadPhotoGallery(IDProduct);
				ArrayList<byte[]> photos = new ArrayList<>();
				for(Integer idImg : idImages)
					photos.add(PhotoControl.loadPhotoOfGallery(IDProduct, idImg));
				dto.setGalleriaImmagini(photos);
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
	 * Il metodo permette di porre il flag InCatalogo di un prodotto presente nel database a false.
	 * 
	 * @param IDProduct : l'identificativo del prodotto da aggiornare
	 * 
	 * @return l'esito dell'operazione
	 * */
	
	public synchronized boolean doDelete(int IDProduct) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String deleteSQL = "UPDATE " + ProdottoDAODataSource.TABLE_NAME + " SET INCATALOGO = 0 WHERE CODICEPRODOTTO = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setInt(1, IDProduct);

			result = preparedStatement.executeUpdate();
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
		return (result != 0);
	}

	/**
	 * Il metodo recupera le informazioni dei prodotti presenti nel database ed effettua la paginazione 
	 * di tali prodotti.
	 * 
	 * @param order : l'ordine con il quale mostrare i prodotti
	 * @param page : rappresenta il numero di pagina desiderato
	 * @param perPage : indica il numero di elementi per pagina
	 * 
	 * @return products : i prodotti presenti nel database ordinati per order
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	public synchronized Collection<ProxyProdotto> doRetrieveAll(String order, int page, int perPage) throws SQLException, SottocategoriaProdottoException, CategoriaProdottoException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;
		Collection<ProxyProdotto> products = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + ProdottoDAODataSource.TABLE_NAME;
		String countSQL = "SELECT COUNT(*) FROM " + ProdottoDAODataSource.TABLE_NAME;

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;
			countSQL += " ORDER BY " + order;
		}

		int totalRecords;
		int totalPages;

		try {
			connection = ds.getConnection();
			try {
				// Recupera il numero totale di record
				preparedStatement = connection.prepareStatement(countSQL);
				ResultSet rs = preparedStatement.executeQuery();
				rs.next();
				totalRecords = rs.getInt(1);

				// Calcola il numero totale di pagine
				totalPages = (int) Math.ceil((double) totalRecords / perPage);
			}finally {
				try {
					if (preparedStatement != null)
						preparedStatement.close();
				} finally {
					if (connection != null)
						connection.close();
				}
			}
			// Verifica se la pagina richiesta è valida
			if (page > totalPages) {
				page = totalPages;
			}  

			// Esegui la query con LIMIT e OFFSET
			connection2 = ds.getConnection();  
			int offset = (page - 1) * perPage;
			selectSQL += " LIMIT ? OFFSET ?";
			preparedStatement = connection2.prepareStatement(selectSQL);
			preparedStatement.setInt(1, perPage);
			preparedStatement.setInt(2, offset);

			// Recupera i record paginati
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ProxyProdotto dto = new ProxyProdotto();

				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setSottocategoria(rs.getString("SOTTOCATEGORIA"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInCatalogo(rs.getInt("INCATALOGO") == 1 ? true : false);
				dto.setInVetrina(rs.getInt("INVETRINA") == 1 ? true : false);

				products.add(dto);
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection2 != null)
					connection2.close();
			}
		}
		return products;
	}

	/**
	 * Il metodo recupera le informazioni dei prodotti non presenti nel catalogo ed effettua
	 * la paginazione di tali prodotti.
	 * 
	 * @param order : l'ordine con il quale mostrare i prodotti
	 * @param page : rappresenta il numero di pagina desiderato
	 * @param perPage : indica il numero di elementi per pagina
	 * 
	 * @return products : i prodotti rimossi dal catalogo ordinati per order
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	public synchronized Collection<ProxyProdotto> doRetrieveAllDeleted(String order, int page, int perPage) throws SQLException, SottocategoriaProdottoException, CategoriaProdottoException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyProdotto> products = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + ProdottoDAODataSource.TABLE_NAME + " WHERE INCATALOGO = 0";
		String countSQL = "SELECT COUNT(*) FROM " + ProdottoDAODataSource.TABLE_NAME + " WHERE INCATALOGO = 0";

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order; //ordinare i prodotti per nome
			countSQL += " ORDER BY " + order;
		}

		int totalRecords;
		int totalPages;
		
		try {
			connection = ds.getConnection();
			try {
				// Recupera il numero totale di record
				preparedStatement = connection.prepareStatement(countSQL);
				ResultSet rs = preparedStatement.executeQuery();
				rs.next();
				totalRecords = rs.getInt(1);

				// Calcola il numero totale di pagine
				totalPages = (int) Math.ceil((double) totalRecords / perPage);
			}finally {
				try {
					if (preparedStatement != null)
						preparedStatement.close();
				} finally {
					if (connection != null)
						connection.close();
				}
			}
			// Verifica se la pagina richiesta è valida
			if (page > totalPages) {
				page = totalPages;
			}
			

			// Esegui la query con LIMIT e OFFSET
			connection2 = ds.getConnection();   
			int offset = (page - 1) * perPage;
			selectSQL += " LIMIT ? OFFSET ?";
			preparedStatement = connection2.prepareStatement(selectSQL);
			preparedStatement.setInt(1, perPage);
			preparedStatement.setInt(2, offset);

			// Recupera i record paginati
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ProxyProdotto dto = new ProxyProdotto();

				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setSottocategoria(rs.getString("SOTTOCATEGORIA"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInCatalogo(rs.getInt("INCATALOGO") == 1 ? true : false);
				dto.setInVetrina(rs.getInt("INVETRINA") == 1 ? true : false);

				products.add(dto);
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection2 != null)
					connection2.close();
			}
		}
		return products;
	}


	/**
	 * Il metodo recupera le informazioni dei prodotti presenti nel catalogo ed effettua la paginazione
	 * di tali prodotti.
	 * 
	 * @param order : l'ordine con il quale mostrare i prodotti
	 * @param page : rappresenta il numero di pagina desiderato
	 * @param perPage : indica il numero di elementi per pagina
	 * 
	 * @return products : i prodotti del catalogo ordinati per order
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	public synchronized Collection<ProxyProdotto> doRetrieveAllExistent(String order, int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyProdotto> products = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + ProdottoDAODataSource.TABLE_NAME + " WHERE INCATALOGO = 1";
		String countSQL = "SELECT COUNT(*) FROM " + ProdottoDAODataSource.TABLE_NAME + " WHERE INCATALOGO = 1";

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order; //ordinare i prodotti per nome
			countSQL += " ORDER BY " + order;
		}

		int totalRecords;
		int totalPages;
		
		try {
			connection = ds.getConnection();
			try {
				// Recupera il numero totale di record
				preparedStatement = connection.prepareStatement(countSQL);
				ResultSet rs = preparedStatement.executeQuery();
				rs.next();
				totalRecords = rs.getInt(1);

				// Calcola il numero totale di pagine
				totalPages = (int) Math.ceil((double) totalRecords / perPage);
			}finally {
				try {
					if (preparedStatement != null)
						preparedStatement.close();
				} finally {
					if (connection != null)
						connection.close();
				}
			}
			// Verifica se la pagina richiesta è valida
			if (page > totalPages) {
				page = totalPages;
			}
			

			// Esegui la query con LIMIT e OFFSET
			connection2 = ds.getConnection();
			int offset = (page - 1) * perPage;
			selectSQL += " LIMIT ? OFFSET ?";
			preparedStatement = connection2.prepareStatement(selectSQL);
			preparedStatement.setInt(1, perPage);
			preparedStatement.setInt(2, offset);

			// Recupera i record paginati
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ProxyProdotto dto = new ProxyProdotto();

				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setSottocategoria(rs.getString("SOTTOCATEGORIA"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInCatalogo(rs.getInt("INCATALOGO") == 1 ? true : false);
				dto.setInVetrina(rs.getInt("INVETRINA") == 1 ? true : false);

				products.add(dto);
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection2 != null)
					connection2.close();
			}
		}
		return products;
	}

	/**
	 * Il metodo recupera le informazioni dei prodotti nel catalogo che presentano nel nome,
	 * nel modello, nel brand, nella descrizione in evidenza o nella descrizione dettagliata la parola
	 * searchTerm. Viene effettuata la paginazione dei risultati ottenuti.
	 * 
	 * @param searchTerm : la parola di ricerca
	 * @param order : l'ordine con il quale mostrare i prodotti
	 * @param page : rappresenta il numero di pagina desiderato
	 * @param perPage : indica il numero di elementi per pagina
	 * 
	 * @return products : i prodotti del catalogo ordinati per order che presentano nel nome,
	 * 						nel modello, nel brand, nella descrizione in evidenza o nella descrizione 
	 * 						dettagliata la parola searchTerm.
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	public synchronized Collection<ProxyProdotto> searching(String order, String searchTerm, int page, int perPage) throws SQLException, SottocategoriaProdottoException, CategoriaProdottoException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyProdotto> products = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + ProdottoDAODataSource.TABLE_NAME + 
				" WHERE INCATALOGO = 1 AND ((NOME LIKE ?) OR (TOPDESCRIZIONE LIKE ?) OR (DETTAGLI LIKE ?) OR (MODELLO LIKE ?) OR"
				+ " (MARCA LIKE ?))";

		String countSQL = "SELECT COUNT(*) FROM " + ProdottoDAODataSource.TABLE_NAME + " WHERE INCATALOGO = 1 AND ((NOME LIKE ?) "
				+ "OR (TOPDESCRIZIONE LIKE ?) OR (DETTAGLI LIKE ?) OR (MODELLO LIKE ?) "
				+ "OR (MARCA LIKE ?))";

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order; //ordinare i prodotti per nome
			countSQL += " ORDER BY " + order;
		}

		int totalRecords;
		int totalPages;
		
		try {
			connection = ds.getConnection();
			try {
				// Recupera il numero totale di record
				preparedStatement = connection.prepareStatement(countSQL);
				preparedStatement.setString(1, "%" + searchTerm + "%");
				preparedStatement.setString(2, "%" + searchTerm + "%");
				preparedStatement.setString(3, "%" + searchTerm + "%");
				preparedStatement.setString(4, "%" + searchTerm + "%");
				preparedStatement.setString(5, "%" + searchTerm + "%");
				ResultSet rs = preparedStatement.executeQuery();
				rs.next();
				totalRecords = rs.getInt(1);

				// Calcola il numero totale di pagine
				totalPages = (int) Math.ceil((double) totalRecords / perPage);
			}finally {
				try {
					if (preparedStatement != null)
						preparedStatement.close();
				} finally {
					if (connection != null)
						connection.close();
				}
			}
			// Verifica se la pagina richiesta è valida
			if (page > totalPages) {
				page = totalPages;
			}
			    

			// Esegui la query con LIMIT e OFFSET
			connection2 = ds.getConnection();
			int offset = Math.max(0, (page - 1) * perPage);
			selectSQL += " LIMIT ? OFFSET ?";
			preparedStatement = connection2.prepareStatement(selectSQL);
			preparedStatement.setString(1, "%" + searchTerm + "%");
			preparedStatement.setString(2, "%" + searchTerm + "%");
			preparedStatement.setString(3, "%" + searchTerm + "%");
			preparedStatement.setString(4, "%" + searchTerm + "%");
			preparedStatement.setString(5, "%" + searchTerm + "%");
			preparedStatement.setInt(6, perPage);
			preparedStatement.setInt(7, offset);

			// Recupera i record paginati
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ProxyProdotto dto = new ProxyProdotto();

				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setSottocategoria(rs.getString("SOTTOCATEGORIA"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInCatalogo(rs.getInt("INCATALOGO") == 1 ? true : false);
				dto.setInVetrina(rs.getInt("INVETRINA") == 1 ? true : false);

				products.add(dto);
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection2 != null)
					connection2.close();
			}
		}
		return products;
	}
	
	/**
	 * Il metodo recupera le informazioni dei prodotti nel catalogo che appartengono alla categoria
	 * richiesta. Viene effettuata la paginazione dei risultati ottenuti.
	 * 
	 * @param category : la categoria di ricerca
	 * @param order : l'ordine con il quale mostrare i prodotti
	 * @param page : rappresenta il numero di pagina desiderato
	 * @param perPage : indica il numero di elementi per pagina
	 * 
	 * @return products : i prodotti del catalogo, ordinati per order, appartenenti alla
	 * 						categoria category
	 * 
	 * @throws CategoriaProdottoException : @see application.NavigazioneService.ProdottoException.CategoriaProdottoException
	 * @throws SottocategoriaProdottoException : @see application.NavigazioneService.ProdottoException.SottocategoriaProdottoException
	 * */
	
	public synchronized Collection<ProxyProdotto> searchingByCategory(String order, String category, int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyProdotto> products = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + ProdottoDAODataSource.TABLE_NAME + 
				" WHERE INCATALOGO = 1 AND (CATEGORIA LIKE ?)";

		String countSQL = "SELECT COUNT(*) FROM " + ProdottoDAODataSource.TABLE_NAME + " WHERE INCATALOGO = 1 AND (CATEGORIA LIKE ?)";

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order; //ordinare i prodotti per nome
			countSQL += " ORDER BY " + order;
		}

		int totalRecords;
		int totalPages;
		
		try {
			connection = ds.getConnection();
			try {
				// Recupera il numero totale di record
				preparedStatement = connection.prepareStatement(countSQL);
				preparedStatement.setString(1, "%" + category + "%");
				
				ResultSet rs = preparedStatement.executeQuery();
				rs.next();
				totalRecords = rs.getInt(1);

				// Calcola il numero totale di pagine
				totalPages = (int) Math.ceil((double) totalRecords / perPage);
			}finally {
				try {
					if (preparedStatement != null)
						preparedStatement.close();
				} finally {
					if (connection != null)
						connection.close();
				}
			}
			// Verifica se la pagina richiesta è valida
			if (page > totalPages) {
				page = totalPages;
			}
			    

			// Esegui la query con LIMIT e OFFSET
			connection2 = ds.getConnection();
			int offset = Math.max(0, (page - 1) * perPage);
			selectSQL += " LIMIT ? OFFSET ?";
			preparedStatement = connection2.prepareStatement(selectSQL);
			preparedStatement.setString(1, "%" + category + "%");
			preparedStatement.setInt(2, perPage);
			preparedStatement.setInt(3, offset);

			// Recupera i record paginati
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ProxyProdotto dto = new ProxyProdotto();

				dto.setCodiceProdotto(rs.getInt("CODICEPRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setTopDescrizione(rs.getString("TOPDESCRIZIONE"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setSottocategoria(rs.getString("SOTTOCATEGORIA"));
				dto.setPrezzo(rs.getFloat("PREZZO"));
				dto.setMarca(rs.getString("MARCA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setQuantita(rs.getInt("QUANTITà"));
				dto.setInCatalogo(rs.getInt("INCATALOGO") == 1 ? true : false);
				dto.setInVetrina(rs.getInt("INVETRINA") == 1 ? true : false);

				products.add(dto);
			}

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection2 != null)
					connection2.close();
			}
		}
		return products;
	}

	/**
	 * Il metodo permette di modificare le seguenti informazioni associate ad un prodotto del catalogo:
	 * marca, modello, descrizione in evidenza, descrizione dettagliata, categoria, sottocategoria.
	 * 
	 * @param idProdotto : l'identificativo del prodotto da aggiornare
	 * @param campo : l'informazione che si intende aggiornare
	 * @param valore : la nuova informazione da memorizzare
	 * 
	 * @return esito dell'operazione
	 * */
	
	public boolean updateData(int idProdotto, String campo, String valore) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;
		String updateSQL = "UPDATE " + ProdottoDAODataSource.TABLE_NAME;

		if(campo.equals("TOPDESCRIZIONE")) {
			updateSQL +=  " SET TOPDESCRIZIONE = ? WHERE CODICEPRODOTTO = ?";
		}
		if(campo.equals("MODELLO")) {
			updateSQL +=  " SET MODELLO = ? WHERE CODICEPRODOTTO = ?";
		}
		if(campo.equals("DETTAGLI")) {
			updateSQL +=  " SET DETTAGLI = ? WHERE CODICEPRODOTTO = ?";
		}
		if(campo.equals("MARCA")) {
			updateSQL +=  " SET MARCA = ? WHERE CODICEPRODOTTO = ?";
		}
		if(campo.equals("CATEGORIA")) {
			updateSQL +=  " SET CATEGORIA = ? WHERE CODICEPRODOTTO = ?";
		}

		if(campo.equals("SOTTOCATEGORIA")) {
			updateSQL +=  " SET SOTTOCATEGORIA = ? WHERE CODICEPRODOTTO = ?";
		}


		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setString(1, valore);
			preparedStatement.setInt(2, idProdotto);

			result = preparedStatement.executeUpdate();
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
		return (result != 0);
	}

	/**
	 * Il metodo permette di aggiornare il prezzo di un prodotto del catalogo.
	 * 
	 * @param idProdotto : l'identificativo del prodotto da aggiornare
	 * @param price : il prezzo da applicare al prodotto
	 * 
	 * @return esito dell'operazione
	 * */
	
	public boolean updatePrice(int idProdotto, float price) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String updateSQL = "UPDATE " + ProdottoDAODataSource.TABLE_NAME + " SET PREZZO = ? WHERE CODICEPRODOTTO = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setFloat(1, price);
			preparedStatement.setInt(2, idProdotto);

			result = preparedStatement.executeUpdate();
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
		return (result != 0);
	}

	/**
	 * Il metodo permette di porre un prodotto del catalogo come prodotto in una vetrina del negozio online.
	 * 
	 * @param idProdotto : l'identificativo del prodotto da aggiornare
	 * @param flagView : indica se il prodotto verrà messo in vetrina
	 * 
	 * @return esito dell'operazione
	 * */
	
	public boolean updateDataView(int idProdotto, int flagView) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;
		String updateSQL = "UPDATE " + ProdottoDAODataSource.TABLE_NAME + " SET INVETRINA = ? WHERE CODICEPRODOTTO = ?";	

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setInt(1, flagView);
			preparedStatement.setInt(2, idProdotto);

			result = preparedStatement.executeUpdate();
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
		return (result != 0);
	}

	/**
	 * Il metodo permette di aggiornare le quantità di un prodotto del catalogo in magazzino.
	 * 
	 * @param idProdotto : l'identificativo del prodotto da aggiornare
	 * @param quantity : la quantità del prodotto da impostare
	 * 
	 * @return esito dell'operazione
	 * */
	
	public boolean updateQuantity(int idProdotto, int quantity) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;
		String updateSQL = "UPDATE " + ProdottoDAODataSource.TABLE_NAME + " SET QUANTITà = ? WHERE CODICEPRODOTTO = ?";	

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(updateSQL);
			preparedStatement.setInt(1, quantity);
			preparedStatement.setInt(2, idProdotto);

			result = preparedStatement.executeUpdate();
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
		return (result != 0);
	}
}
