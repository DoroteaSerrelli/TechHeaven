package storage.GestioneOrdiniDAO;

import java.sql.Connection;
import java.sql.Date;
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

import application.GestioneCarrelloService.ItemCarrello;
import application.GestioneOrdiniService.Ordine;
import application.GestioneOrdiniService.OrdineException.ErroreSpedizioneOrdineException;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.GestioneOrdiniService.ProxyOrdine;
import application.GestioneOrdiniService.ReportSpedizione;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.RegistrazioneService.Cliente;
import storage.AutenticazioneDAO.ClienteDAODataSource;

/**
 * @author Dorotea Serrelli
 * */
public class OrdineDAODataSource {
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

	private static final String TABLE_NAME = "ordine";
	
	/**
	 * Il metodo crea un ordine e lo memorizza nel DB.
	 * Questo metodo viene utilizzato quando l'ordine non e\' stato ancora
	 * preparato per la spedizione.
	 * In tal caso, consultare il metodo @see storage.OrdineDAODataSource.doSaveToShip
	 * 
	 * @param order : l'ordine da salvare
	 * @throws ModalitaAssenteException 
	 * @throws OrdineVuotoException 
	 * @throws CloneNotSupportedException 
	 * **/
	public synchronized void doSave(Ordine order) throws SQLException, OrdineVuotoException, ModalitaAssenteException, CloneNotSupportedException {
		//creare ordine
		Connection connection2 = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertOrderSQL = "INSERT INTO " + OrdineDAODataSource.TABLE_NAME
				+ " (CODICEORDINE, STATO, EMAIL, INDIRIZZOSPEDIZIONE, TIPOSPEDIZIONE, DATAORDINE, ORAORDINE) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertOrderSQL);
			preparedStatement.setInt(1, order.getCodiceOrdine());
			preparedStatement.setString(2, order.getStatoAsString());
			preparedStatement.setString(3, order.getAcquirente().getEmail());
			preparedStatement.setString(4, order.getIndirizzoSpedizione());
			preparedStatement.setString(5, order.getSpedizioneAsString());
			preparedStatement.setDate(6, java.sql.Date.valueOf(order.getData()));
			preparedStatement.setTime(7, java.sql.Time.valueOf(order.getOra()));
			
			
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
		
		//associare ordine a prodotti
		
		String insertOrderProductsSQL = "INSERT INTO Composizione_Ordine(ORDINE, PRODOTTO, QUANTITàACQUISTATA, PREZZOACQUISTATO) VALUES (?, ?, ?, ?)";

		try {
			connection2 = ds.getConnection();
			preparedStatement = connection2.prepareStatement(insertOrderProductsSQL);
			for(ItemCarrello i : order.getProdotti()) {
				preparedStatement.setInt(1, order.getCodiceOrdine());
				preparedStatement.setInt(2, i.getCodiceProdotto());
				preparedStatement.setInt(3, i.getQuantita());
				preparedStatement.setFloat(4, i.getPrezzo());

				preparedStatement.executeUpdate();
			}
			connection2.setAutoCommit(false);
			connection2.commit();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} finally {
				if (connection2 != null)
					connection2.close();
			}
		}

	}
	
	/**
	 * Il metodo crea un ordine già spedito e lo memorizza nel DB.
	 * 
	 * @param order : l'ordine con status "Spedito" da salvare
	 * @param report : il report di spedizione da associare all'ordine spedito
	 * @throws ErroreSpedizioneOrdineException per gestire la memorizzazione di un ordine spedito,
	 * che ancora non è in tale stato
	 * @throws ModalitaAssenteException 
	 * @throws OrdineVuotoException 
	 * @throws CloneNotSupportedException 
	 * **/
	public synchronized void doSaveToShip(Ordine order, ReportSpedizione report) throws SQLException, ErroreSpedizioneOrdineException, OrdineVuotoException, ModalitaAssenteException, CloneNotSupportedException {
		
		if(!order.getStatoAsString().equals("Spedito"))
			throw new ErroreSpedizioneOrdineException("Non e\' possibile completare l'operazione perche\' l'ordine non ha lo stato \"Spedito\"");
		
		//rimuovere l'ordine preesistente (per CASCADE viene rimosso anche l'oggetto Pagamento)
		if(!doDelete(order.getCodiceOrdine())) {
			System.out.println("L'ordine non esiste!");
			return;
		}
		//creare l'ordine
		doSave(order);
		
		//creare report di spedizione da associare all'ordine
		
		ReportDAODataSource reportDAO = new ReportDAODataSource();
		reportDAO.doSave(report);
	}
	
	/**
	 * Il metodo recupera dal DB le caratteristiche peculiari di un ordine 
	 * a partire dal suo codice identificativo.
	 * @param IDOrdine : l'ordine da cercare nel DB
	 * @return dto : le informazioni essenziali dell'ordine cercato
	 * **/
	
	public synchronized ProxyOrdine doRetrieveProxyByKey(int IDOrdine) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		ProxyOrdine dto = new ProxyOrdine();

		String selectSQL = "SELECT * FROM " + OrdineDAODataSource.TABLE_NAME + " WHERE CODICEORDINE = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDOrdine);

			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				dto.setCodiceOrdine(rs.getInt("CODICEORDINE"));
				dto.setStatoAsString(rs.getString("STATO"));
				dto.setIndirizzoSpedizioneString(rs.getString("INDIRIZZOSPEDIZIONE"));
				dto.setSpedizioneAsString(rs.getString("TIPOSPEDIZIONE"));
				dto.setData(rs.getDate("DATAORDINE").toLocalDate());
				dto.setOra((rs.getTime("ORAORDINE")).toLocalTime());
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
	 * Il metodo recupera dal DB tutte le caratteristiche di un ordine 
	 * a partire dal suo codice identificativo.
	 * @param IDOrdine : l'ordine da cercare nel DB
	 * @return dto : tutte le informazioni dell'ordine cercato
	 * @throws OrdineVuotoException 
	 * @throws CategoriaProdottoException 
	 * **/
	public synchronized Ordine doRetrieveFullOrderByKey(int IDOrdine) throws SQLException, OrdineVuotoException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		Ordine dto = new Ordine();

		String selectSQL = "SELECT * FROM " + OrdineDAODataSource.TABLE_NAME + " WHERE CODICEORDINE = ?";
		
		//dati da ordine
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDOrdine);

			ResultSet rs = preparedStatement.executeQuery();
			
			
			while (rs.next()) {
				dto.setCodiceOrdine(rs.getInt("CODICEORDINE"));
				dto.setStatoAsString(rs.getString("STATO"));
				ClienteDAODataSource clientDAO = new ClienteDAODataSource();
				Cliente client = clientDAO.doRetrieveByKey(rs.getString("EMAIL"));
				dto.setAcquirente(client);
				dto.setIndirizzoSpedizioneString(rs.getString("INDIRIZZOSPEDIZIONE"));
				dto.setSpedizioneAsString(rs.getString("TIPOSPEDIZIONE"));
				dto.setData(rs.getDate("DATAORDINE").toLocalDate());
				dto.setOra((rs.getTime("ORAORDINE")).toLocalTime());
				
				//dati da composizione ordine
				dto.setProdotti(doRetrieveAllOrderProducts(IDOrdine));
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
	 * Il metodo recupera dal DB i prodotti che compongono un ordine, a partire dal 
	 * a partire dal suo codice identificativo.
	 * @param IDOrdine : l'ordine da cercare nel DB
	 * @return prodotti : le informazioni essenziali dei prodotti dell'ordine cercato
	 * @throws CategoriaProdottoException 
	 * **/
	public synchronized ArrayList<ItemCarrello> doRetrieveAllOrderProducts(int IDOrdine) throws SQLException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		ArrayList<ItemCarrello> prodotti = new ArrayList<>();

		String selectSQL = "SELECT * FROM COMPOSIZIONE_ORDINE INNER JOIN PRODOTTO "
				+ "ON COMPOSIZIONE_ORDINE.PRODOTTO = PRODOTTO.CODICEPRODOTTO "
				+ "WHERE ORDINE = ?";
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDOrdine);

			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				ItemCarrello dto = new ItemCarrello();
				
				dto.setCodiceProdotto(rs.getInt("PRODOTTO"));
				dto.setNomeProdotto(rs.getString("NOME"));
				dto.setPrezzo(rs.getFloat("PREZZOACQUISTATO"));
				dto.setQuantita(rs.getInt("QUANTITàACQUISTATA"));
				dto.setCategoria(rs.getString("CATEGORIA"));
				dto.setModello(rs.getString("MODELLO"));
				dto.setMarca(rs.getString("MARCA"));
				
				prodotti.add(dto);
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
		return prodotti;
	}
	
	/**
	 * Il metodo elimina un ordine memorizzato nel DB.
	 * @param IDOrdine : l'ordine da eliminare
	 * @return 1 se l'ordine è stato eliminato; 0 altrimenti.
	 * **/
	public synchronized boolean doDelete(int IDOrdine) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int result = 0;

		String deleteSQL = "DELETE FROM " + OrdineDAODataSource.TABLE_NAME + " WHERE CODICEORDINE = ?";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(deleteSQL);
			preparedStatement.setInt(1, IDOrdine);

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
	 * Il metodo recupera dal DB tutti gli ordini commissionati al negozio online,
	 * utilizzando il meccanismo della paginazione degli elementi estratti
	 * dal DB.
	 * @param order: l'ordinamento con cui si organizzano gli ordini recuperati dal DB
	 * @param page : il numero di pagina
	 * @param perPage : il numero di ordini da inserire per ogni record/pagina
	 * 
	 * @return ordini : le informazioni essenziali degli ordini 
	 * 					(da spedire, in preparazione, evasi) commissionati al negozio online
	 * **/
	public synchronized Collection<ProxyOrdine> doRetrieveAll(String order, int page, int perPage) throws SQLException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyOrdine> ordini = new LinkedList<>();
		
		String selectSQL = "SELECT * FROM " + OrdineDAODataSource.TABLE_NAME;
	    String countSQL = "SELECT COUNT(*) FROM " + OrdineDAODataSource.TABLE_NAME;
		
		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;	//ordinamento degli ordini
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
				ProxyOrdine dto = new ProxyOrdine();
				dto.setCodiceOrdine(rs.getInt("CODICEORDINE"));
				dto.setStatoAsString(rs.getString("STATO"));
				dto.setIndirizzoSpedizioneString(rs.getString("INDIRIZZOSPEDIZIONE"));
				dto.setSpedizioneAsString(rs.getString("TIPOSPEDIZIONE"));
				dto.setData(rs.getDate("DATAORDINE").toLocalDate());
				dto.setOra((rs.getTime("ORAORDINE")).toLocalTime());
				
				ordini.add(dto);
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
		return ordini;
	}
	
	/**
	 * Il metodo recupera dal DB tutti gli ordini commissionati al negozio online,
	 * che devono essere ancora spediti, 
	 * utilizzando il meccanismo della paginazione degli elementi estratti
	 * dal DB.
	 * @param order: l'ordinamento con cui si organizzano gli ordini recuperati dal DB
	 * @param page : il numero di pagina
	 * @param perPage : il numero di ordini da inserire per ogni record/pagina
	 * 
	 * @return ordini : le informazioni essenziali degli ordini 
	 * 					(da spedire, in preparazione) commissionati al negozio online
	 * **/
	public synchronized Collection<ProxyOrdine> doRetrieveOrderToShip(String order, int page, int perPage) throws SQLException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyOrdine> ordini = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + OrdineDAODataSource.TABLE_NAME 
				+ " WHERE STATO = 'RICHIESTA_EFFETTUATA' OR STATO = 'PREPARAZIONE_INCOMPLETA'";
	    String countSQL = "SELECT COUNT(*) FROM " + OrdineDAODataSource.TABLE_NAME
	    		+ " WHERE STATO = 'RICHIESTA_EFFETTUATA' OR STATO = 'PREPARAZIONE_INCOMPLETA'";

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;	//ordinamento degli ordini
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
				ProxyOrdine dto = new ProxyOrdine();
				dto.setCodiceOrdine(rs.getInt("CODICEORDINE"));
				dto.setStatoAsString(rs.getString("STATO"));
				dto.setIndirizzoSpedizioneString(rs.getString("INDIRIZZOSPEDIZIONE"));
				dto.setSpedizioneAsString(rs.getString("TIPOSPEDIZIONE"));
				dto.setData(rs.getDate("DATAORDINE").toLocalDate());
				dto.setOra((rs.getTime("ORAORDINE")).toLocalTime());
				
				ordini.add(dto);
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
		return ordini;
	}
	
	/**
	 * Il metodo recupera dal DB tutti gli ordini commissionati da un particolare
	 * utente al negozio online,
	 * utilizzando il meccanismo della paginazione degli elementi estratti
	 * dal DB.
	 * @param email : l'email dell'utente per il quale si stanno ricercando gli ordini commissionati
	 * @param order: l'ordinamento con cui si organizzano gli ordini recuperati dal DB
	 * @param page : il numero di pagina
	 * @param perPage : il numero di ordini da inserire per ogni record/pagina
	 * 
	 * @return ordini : le informazioni essenziali degli ordini commissionati da un utente
	 * 					al negozio online
	 * **/
	public synchronized Collection<ProxyOrdine> doRetrieveOrderToUser(String email, String order, int page, int perPage) throws SQLException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyOrdine> ordini = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + OrdineDAODataSource.TABLE_NAME 
				+ " WHERE EMAIL = ?";
		
		String countSQL = "SELECT COUNT(*) FROM " + OrdineDAODataSource.TABLE_NAME
				+ " WHERE EMAIL = ?";

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;	//ordinamento degli ordini
			countSQL += " ORDER BY " + order;
		}
		
		int totalRecords;
	    int totalPages;
		
		try {
			connection = ds.getConnection();
			try {
	    		// Recupera il numero totale di record
	    		preparedStatement = connection.prepareStatement(countSQL);
	    		preparedStatement.setString(1, email);
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
	        preparedStatement.setString(1, email);
	        preparedStatement.setInt(2, perPage);
	        preparedStatement.setInt(3, offset);

	        // Recupera i record paginati
	        ResultSet rs = preparedStatement.executeQuery();
	        			
			while (rs.next()) {
				ProxyOrdine dto = new ProxyOrdine();
				dto.setCodiceOrdine(rs.getInt("CODICEORDINE"));
				dto.setStatoAsString(rs.getString("STATO"));
				dto.setIndirizzoSpedizioneString(rs.getString("INDIRIZZOSPEDIZIONE"));
				dto.setSpedizioneAsString(rs.getString("TIPOSPEDIZIONE"));
				dto.setData(rs.getDate("DATAORDINE").toLocalDate());
				dto.setOra((rs.getTime("ORAORDINE")).toLocalTime());
				
				ordini.add(dto);
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
		return ordini;
	}
	
	/**
	 * Il metodo recupera dal DB tutti gli ordini commissionati al negozio online,
	 * che sono stati già spediti, 
	 * utilizzando il meccanismo della paginazione degli elementi estratti
	 * dal DB.
	 * @param order: l'ordinamento con cui si organizzano gli ordini recuperati dal DB
	 * @param page : il numero di pagina
	 * @param perPage : il numero di ordini da inserire per ogni record/pagina
	 * 
	 * @return ordini : le informazioni essenziali degli ordini 
	 * 					già spediti dal negozio online
	 * **/
	public synchronized Collection<ProxyOrdine> doRetrieveOrderShipped(String order, int page, int perPage) throws SQLException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ProxyOrdine> ordini = new LinkedList<>();

		String selectSQL = "SELECT * FROM " + OrdineDAODataSource.TABLE_NAME 
				+ " WHERE STATO = 'SPEDITO'";
		
		String countSQL = "SELECT COUNT(*) FROM " + OrdineDAODataSource.TABLE_NAME + " WHERE STATO = 'SPEDITO'";
		
		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;	//ordinamento degli ordini
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
				ProxyOrdine dto = new ProxyOrdine();
				dto.setCodiceOrdine(rs.getInt("CODICEORDINE"));
				dto.setStatoAsString(rs.getString("STATO"));
				dto.setIndirizzoSpedizioneString(rs.getString("INDIRIZZOSPEDIZIONE"));
				dto.setSpedizioneAsString(rs.getString("TIPOSPEDIZIONE"));
				dto.setData(rs.getDate("DATAORDINE").toLocalDate());
				dto.setOra((rs.getTime("ORAORDINE")).toLocalTime());
				
				ordini.add(dto);
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
		return ordini;
	}
	
	/**
	 * Il metodo recupera dal DB tutti gli ordini commissionati al negozio online 
	 * entro un intervallo di tempo (espresso in date),
	 * utilizzando il meccanismo della paginazione degli elementi estratti
	 * dal DB.
	 * @param startDate : la data di inizio per la quale si stanno ricercando gli ordini commissionati
	 * @param endDate : la data di fine per la quale si stanno ricercando gli ordini commissionati
	 * @param order: l'ordinamento con cui si organizzano gli ordini recuperati dal DB
	 * @param page : il numero di pagina
	 * @param perPage : il numero di ordini da inserire per ogni record/pagina
	 * 
	 * @return ordini : le informazioni essenziali degli ordini commissionati da un utente
	 * 					al negozio online
	 * **/
	public synchronized Collection<ProxyOrdine> doRetrieveOrderForDate(int page, int perPage, Date startDate, Date endDate) throws SQLException {
	    Connection connection = null;
	    Connection connection2 = null;
	    PreparedStatement preparedStatement = null;

	    Collection<ProxyOrdine> ordini = new LinkedList<>();

	    String selectSQL = "SELECT * FROM " + OrdineDAODataSource.TABLE_NAME + " WHERE (DATAORDINE BETWEEN ? AND ?)";
	    String countSQL = "SELECT COUNT(*) FROM " + OrdineDAODataSource.TABLE_NAME + "WHERE (DATAORDINE BETWEEN ? AND ?)";
	    
	    selectSQL += " ORDER BY DATAORDINE"; //ordinare gli ordini per data
        countSQL += " ORDER BY DATAORDINE";
	    
        int totalRecords;
	    int totalPages;
        
	    try {
	        connection = ds.getConnection();
	        try {
	    		// Recupera il numero totale di record
	    		preparedStatement = connection.prepareStatement(countSQL);
	    		preparedStatement.setDate(1, startDate);
		        preparedStatement.setDate(2, endDate);
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
	        preparedStatement.setDate(1, startDate);
	        preparedStatement.setDate(2, endDate);
	        preparedStatement.setInt(3, perPage);
	        preparedStatement.setInt(4, offset);

	        // Recupera i record paginati
	        ResultSet rs = preparedStatement.executeQuery();
	       
	        while (rs.next()) {
	        	ProxyOrdine dto = new ProxyOrdine();
				dto.setCodiceOrdine(rs.getInt("CODICEORDINE"));
				dto.setStatoAsString(rs.getString("STATO"));
				dto.setIndirizzoSpedizioneString(rs.getString("INDIRIZZOSPEDIZIONE"));
				dto.setSpedizioneAsString(rs.getString("TIPOSPEDIZIONE"));
				dto.setData(rs.getDate("DATAORDINE").toLocalDate());
				dto.setOra((rs.getTime("ORAORDINE")).toLocalTime());
				
				ordini.add(dto);
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
	    return ordini;
	}
}
