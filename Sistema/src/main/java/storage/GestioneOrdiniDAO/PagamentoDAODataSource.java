package storage.GestioneOrdiniDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import application.PagamentoService.Pagamento;
import application.PagamentoService.PagamentoCartaCredito;
import application.PagamentoService.PagamentoContrassegno;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.PagamentoService.PagamentoPaypal;
import application.PagamentoService.PagamentoServiceImpl;
import application.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import java.sql.Statement;

/**
 * Classe DAO per la gestione dei pagamenti nel database.
 * Questa classe implementa i metodi CRUD per la gestione dei pagamenti nel database.
 * Inoltre, fornisce dei metodi per recuperare gli ordini in base a vari criteri,
 * come l'identificativo, la modalità di pagamento, il codice dell'ordine a cui fa riferimento, ...
 * 
 * @author Dorotea Serrelli
 * */

public class PagamentoDAODataSource {

	DataSource ds;
	
	public PagamentoDAODataSource(DataSource ds) {
		this.ds = ds;
	}

	private static final String TABLE_NAME = "Pagamento";
	private static final String TABLE_NAME_CASH = "Contrassegno";
	private static final String TABLE_NAME_PAYPAL = "Paypal";
	private static final String TABLE_NAME_CARD = "Carta_di_credito";

	/**
	 * Il metodo crea un documento di pagamento per un ordine fatto 
	 * e lo memorizza nel DB.
	 * @param payment : il pagamento da salvare
	 * **/
	private synchronized void doSave(Pagamento payment) throws SQLException {
		//creare pagamento

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertPaymentSQL = "INSERT INTO " + PagamentoDAODataSource.TABLE_NAME
				+ " ( ORDINE, DATAPAGAMENTO, ORAPAGAMENTO, IMPORTO) VALUES ( ?, ?, ?, ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertPaymentSQL, Statement.RETURN_GENERATED_KEYS);					
                        preparedStatement.setInt(1, payment.getOrdine().getCodiceOrdine());
			preparedStatement.setDate(2, java.sql.Date.valueOf(payment.getDataPagamento()));
			preparedStatement.setTime(3, java.sql.Time.valueOf(payment.getOraPagamento()));
			preparedStatement.setFloat(4, payment.getImporto());

			preparedStatement.executeUpdate();
                        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				int generatedID = generatedKeys.getInt(1);
				payment.setCodicePagamento(generatedID);  // Imposta l'ID generato sull'oggetto pagamento
				System.out.println("Debug Pagamento ID:" + generatedID);
			} else {
				throw new SQLException("Errore creazione indirizzo, non è possibile recuperare l'ultimo ID generato.");
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
	 * Il metodo crea un documento di pagamento per un ordine fatto 
	 * in contrassegno e lo memorizza nel DB.
	 * @param payment : il pagamento in contrassegno da salvare
	 * **/
	public synchronized void doSaveCash(PagamentoContrassegno payment) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		//creare pagamento
		doSave(payment);

		//creare pagamento in contrassegno

		String insertPaymentCashSQL = "INSERT INTO " + PagamentoDAODataSource.TABLE_NAME_CASH
				+ " (CODICEPAGAMENTO) VALUES (?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertPaymentCashSQL);
			preparedStatement.setInt(1, payment.getCodicePagamento());

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

	/**
	 * Il metodo crea un documento di pagamento per un ordine, fatto 
	 * online con Paypal, e lo memorizza nel DB.
	 * @param payment : il pagamento con Paypal da salvare
	 * **/
	public synchronized void doSavePaypal(PagamentoPaypal payment) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		//creare pagamento
		doSave(payment);

		//creare pagamento con Paypal

		String insertPaymentPaypalSQL = "INSERT INTO " + PagamentoDAODataSource.TABLE_NAME_PAYPAL
				+ " (CODICEPAGAMENTO) VALUES (?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertPaymentPaypalSQL);
			preparedStatement.setInt(1, payment.getCodicePagamento());

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

	/**
	 * Il metodo crea un documento di pagamento per un ordine, fatto 
	 * online con la carta di credito, e lo memorizza nel DB.
	 * @param payment : il pagamento con carta di credito da salvare
	 * **/
	public synchronized void doSaveCard(PagamentoCartaCredito payment) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		//creare pagamento
		doSave(payment);

		//creare pagamento con carta di credito

		String insertPaymentCardSQL = "INSERT INTO " + PagamentoDAODataSource.TABLE_NAME_CARD
				+ " (CODICEPAGAMENTO, TITOLARE, NUMEROCARTA) VALUES (?, ?, ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertPaymentCardSQL);
			preparedStatement.setInt(1, payment.getCodicePagamento());
			preparedStatement.setString(2, payment.getTitolare());
			preparedStatement.setString(3, payment.getNumeroCarta());

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

	/**
	 * Il metodo recupera dal DB tutte le caratteristiche di un pagamento
	 * in contanti in base al codice dell'ordine.
	 * 
	 * @param IDOrdine : l'ordine
	 * @return il pagamento in contrassegno relativo all'ordine con codice IDOrdine 
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 * **/

	public synchronized PagamentoContrassegno doRetrieveCashByOrder(int IDOrdine) throws SQLException, OrdineVuotoException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
                
		String selectSQL = "SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME_CASH + " INNER JOIN "
				+ PagamentoDAODataSource.TABLE_NAME + " ON " 
				+ PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = " + PagamentoDAODataSource.TABLE_NAME_CASH + ".CODICEPAGAMENTO "
				+ "WHERE ORDINE = ?";

		PagamentoContrassegno dto = null;
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDOrdine);

			ResultSet rs = preparedStatement.executeQuery();  
			while (rs.next()) {
                                dto = new PagamentoContrassegno(); 
				OrdineDAODataSource orderDao = new OrdineDAODataSource(ds);

				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
                                System.out.println("id ordine value: "+rs.getInt("ORDINE"));
				dto.setOrdine(orderDao.doRetrieveFullOrderByKey(rs.getInt("ORDINE")));
				dto.setDataPagamento(rs.getDate("DATAPAGAMENTO").toLocalDate());
				dto.setOraPagamento((rs.getTime("ORAPAGAMENTO")).toLocalTime());
				dto.setImporto(rs.getFloat("IMPORTO"));                              
                                
			}
                        
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch(SQLException e) {return null;}
                        
                        finally {
				if (connection != null)
					connection.close();
			}
		}
		return dto;
	}

	/**
	 * Il metodo recupera dal DB tutte le caratteristiche di un pagamento
	 * in Paypal in base al codice dell'ordine.
	 * 
	 * @param IDOrdine : l'ordine
	 * @return il pagamento con Paypal relativo all'ordine con codice IDOrdine 
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 * **/

	public synchronized PagamentoPaypal doRetrievePaypalByOrder(int IDOrdine) throws SQLException, OrdineVuotoException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME_PAYPAL + " INNER JOIN "
				+ PagamentoDAODataSource.TABLE_NAME + " ON " 
				+ PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = " + PagamentoDAODataSource.TABLE_NAME_PAYPAL + ".CODICEPAGAMENTO "
				+ "WHERE ORDINE = ?";

		PagamentoPaypal dto = null;
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDOrdine);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
                                dto = new PagamentoPaypal();
				OrdineDAODataSource orderDao = new OrdineDAODataSource(ds);

				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
				dto.setOrdine(orderDao.doRetrieveFullOrderByKey(rs.getInt("ORDINE")));
				dto.setDataPagamento(rs.getDate("DATAPAGAMENTO").toLocalDate());
				dto.setOraPagamento((rs.getTime("ORAPAGAMENTO")).toLocalTime());
				dto.setImporto(rs.getFloat("IMPORTO"));

			}
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch(SQLException e){
                            System.out.println(e);
                        }
                        finally {
				if (connection != null)
					connection.close();
			}
		}		
                   
		return dto;
	}

	/**
	 * Il metodo recupera dal DB tutte le caratteristiche di un pagamento
	 * in carta di credito in base al codice dell'ordine.
	 * 
	 * @param IDOrdine : l'ordine
	 * @return il pagamento con carta di credito relativo all'ordine con codice IDOrdine 
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 * **/

	public synchronized PagamentoCartaCredito doRetrieveCardByOrder(int IDOrdine) throws SQLException, OrdineVuotoException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME_CARD + " INNER JOIN "
				+ PagamentoDAODataSource.TABLE_NAME + " ON " 
				+ PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = " + PagamentoDAODataSource.TABLE_NAME_CARD + ".CODICEPAGAMENTO "
				+ "WHERE ORDINE = ?";

		PagamentoCartaCredito dto = null;
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDOrdine);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
                                dto = new PagamentoCartaCredito();
				OrdineDAODataSource orderDao = new OrdineDAODataSource(ds);

				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
				dto.setOrdine(orderDao.doRetrieveFullOrderByKey(rs.getInt("ORDINE")));
				dto.setDataPagamento(rs.getDate("DATAPAGAMENTO").toLocalDate());
				dto.setOraPagamento((rs.getTime("ORAPAGAMENTO")).toLocalTime());
				dto.setImporto(rs.getFloat("IMPORTO"));
				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
				dto.setTitolare(rs.getString("TITOLARE"));
				dto.setNumeroCarta(rs.getString("NUMEROCARTA"));
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
	 * Il metodo recupera dal database il pagamento associato all'ordine specificato.
	 * Si verifica che il tipo di pagamento richiesto corrisponda al tipo effettivo del pagamento
	 * recuperato. In caso contrario, viene lanciata una `ModalitaAssenteException`.
	 *
	 * @param <T> il tipo di pagamento atteso (deve essere una sottoclasse di `Pagamento`)
	 * @param paymentClass la classe del pagamento atteso
	 * @param IDOrdine il codice identificativo dell'ordine
	 * 
	 * @return il pagamento recuperato, di tipo `T`
	 * 
	 * @throws SQLException se si verifica un errore di accesso al database
	 * @throws OrdineVuotoException se l'ordine specificato non esiste
	 * @throws ModalitaAssenteException se il tipo di pagamento richiesto non è supportato
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 */

	public synchronized <T extends Pagamento> T doRetrievePaymentByOrder(Class<T> paymentClass, int IDOrdine) throws SQLException, OrdineVuotoException, ModalitaAssenteException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		Pagamento payment = PagamentoServiceImpl.createPagamentoOrdine(IDOrdine, new PagamentoDAODataSource(ds));

		// Si verifica se l'oggetto restituito è una sottoclasse di Pagamento
		if (!paymentClass.isInstance(payment)) {
			throw new ModalitaAssenteException("Modalita\' di pagamento non ammessa. E\' possibile pagare l'ordine: \n- in contrassegno;"
					+ "\n- con Paypal; \n- con carta di credito.");
		}

		return paymentClass.cast(payment);
	}


	/**
	 * Il metodo recupera dal DB tutte le caratteristiche di un pagamento
	 * in contrassegno basandosi sul codice del pagamento.
	 * 
	 * @param IDPayment : il codice identificativo del pagamento in contanti
	 * @return il pagamento in contrassegno avente codice IDPayment 
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 * **/
	public synchronized PagamentoContrassegno doRetrieveCashByKey(int IDPayment) throws SQLException, OrdineVuotoException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME + " INNER JOIN " +
				PagamentoDAODataSource.TABLE_NAME_CASH + " ON " + PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = "+
				PagamentoDAODataSource.TABLE_NAME_CASH + ".CODICEPAGAMENTO "+
				" WHERE CODICEPAGAMENTO = ?";
		PagamentoContrassegno dto = new PagamentoContrassegno();

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDPayment);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				OrdineDAODataSource orderDao = new OrdineDAODataSource(ds);

				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
				dto.setOrdine(orderDao.doRetrieveFullOrderByKey(rs.getInt("ORDINE")));
				dto.setDataPagamento(rs.getDate("DATAPAGAMENTO").toLocalDate());
				dto.setOraPagamento((rs.getTime("ORAPAGAMENTO")).toLocalTime());
				dto.setImporto(rs.getFloat("IMPORTO"));
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
	 * Il metodo recupera dal DB tutte le caratteristiche di un pagamento
	 * Paypal basandosi sul codice del pagamento.
	 * 
	 * @param IDPayment : il codice identificativo del pagamento Paypal
	 * @return il pagamento Paypal avente codice IDPayment 
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 * **/
	public synchronized PagamentoPaypal doRetrievePaypalByKey(int IDPayment) throws SQLException, OrdineVuotoException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME + " INNER JOIN " +
				PagamentoDAODataSource.TABLE_NAME_PAYPAL + " ON " + PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = "+
				PagamentoDAODataSource.TABLE_NAME_PAYPAL + ".CODICEPAGAMENTO "+
				" WHERE CODICEPAGAMENTO = ?";
		PagamentoPaypal dto = new PagamentoPaypal();

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDPayment);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				OrdineDAODataSource orderDao = new OrdineDAODataSource(ds);

				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
				dto.setOrdine(orderDao.doRetrieveFullOrderByKey(rs.getInt("ORDINE")));
				dto.setDataPagamento(rs.getDate("DATAPAGAMENTO").toLocalDate());
				dto.setOraPagamento((rs.getTime("ORAPAGAMENTO")).toLocalTime());
				dto.setImporto(rs.getFloat("IMPORTO"));
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
	 * Il metodo recupera dal DB tutte le caratteristiche di un pagamento
	 * con carta di credito basandosi sul codice del pagamento.
	 * 
	 * @param IDPayment : il codice identificativo del pagamento con carta di credito
	 * @return il pagamento con carta di credito avente codice IDPayment 
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 * **/
	public synchronized PagamentoCartaCredito doRetrieveCardByKey(int IDPayment) throws SQLException, OrdineVuotoException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME + " INNER JOIN " +
				PagamentoDAODataSource.TABLE_NAME_CARD + " ON " + PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = "+
				PagamentoDAODataSource.TABLE_NAME_CARD + ".CODICEPAGAMENTO "+
				" WHERE CODICEPAGAMENTO = ?";
		PagamentoCartaCredito dto = new PagamentoCartaCredito();

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDPayment);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				OrdineDAODataSource orderDao = new OrdineDAODataSource(ds);

				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
				dto.setOrdine(orderDao.doRetrieveFullOrderByKey(rs.getInt("ORDINE")));
				dto.setDataPagamento(rs.getDate("DATAPAGAMENTO").toLocalDate());
				dto.setOraPagamento((rs.getTime("ORAPAGAMENTO")).toLocalTime());
				dto.setImporto(rs.getFloat("IMPORTO"));
				dto.setNumeroCarta(rs.getString("NUMEROCARTA"));
				dto.setTitolare(rs.getString("TITOLARE"));
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
	 * Il metodo recupera dal database un pagamento specifico in base al suo ID.
	 * Esso consente di ottenere un oggetto della sottoclasse `Pagamento` specifico,
	 * verificando che corrisponda al tipo atteso. 
	 *
	 * @param <T> il tipo di pagamento atteso (deve essere una sottoclasse di `Pagamento`)
	 * @param paymentClass la classe del pagamento atteso
	 * @param IDPagamento l'identificativo unico del pagamento
	 * 
	 * @return il pagamento recuperato, di tipo `T`
	 * 
	 * @throws SQLException se si verifica un errore di accesso al database
	 * @throws OrdineVuotoException se l'ordine associato al pagamento non esiste
	 * @throws ModalitaAssenteException se il tipo di pagamento richiesto non è supportato
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 */

	public synchronized <T extends Pagamento> T doRetrievePaymentByKey(Class<T> paymentClass, int IDPagamento) throws SQLException, OrdineVuotoException, ModalitaAssenteException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		Pagamento payment = PagamentoServiceImpl.createPagamento(IDPagamento, new PagamentoDAODataSource(ds));

		// Si verifica se l'oggetto restituito è una sottoclasse di Pagamento
		if (!paymentClass.isInstance(payment)) {
			throw new ModalitaAssenteException("Modalita\' di pagamento non ammessa. E\' possibile pagare l'ordine: \n- in contrassegno;"
					+ "\n- con Paypal; \n- con carta di credito.");
		}

		return paymentClass.cast(payment);
	}

	/**
	 * Questo metodo recupera i dati di tutti i pagamenti presenti nel database,
	 * indipendentemente dalla sottoclasse specifica di `Pagamento` a cui appartengono.
	 * 
	 * @param order: l'ordinamento con cui si organizzano i pagamenti recuperati dal DB
	 * @param page : il numero di pagina
	 * @param perPage : il numero di pagamenti da inserire per ogni record/pagina
	 * 
	 * @return una lista contenente tutti i pagamenti recuperati
	 * @throws SQLException se si verifica un errore di accesso al database
	 * @throws OrdineVuotoException se si verifica un errore durante il recupero dell'ordine associato al pagamento 
	 * @throws ModalitaAssenteException se il tipo di pagamento richiesto non è supportato
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 */
	public synchronized List<Pagamento> doRetrieveAllPayments(String order, int page, int perPage) throws SQLException, OrdineVuotoException, ModalitaAssenteException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		List<Pagamento> pagamenti = new LinkedList<>();

		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME + " INNER JOIN " +
				PagamentoDAODataSource.TABLE_NAME_CASH + " ON " + PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = " +
				PagamentoDAODataSource.TABLE_NAME_CASH + ".CODICEPAGAMENTO " +
				"UNION ALL " +
				"SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME + " INNER JOIN " +
				PagamentoDAODataSource.TABLE_NAME_PAYPAL + " ON " + PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = " +
				PagamentoDAODataSource.TABLE_NAME_PAYPAL + ".CODICEPAGAMENTO " +
				"UNION ALL " +
				"SELECT * FROM " + PagamentoDAODataSource.TABLE_NAME + " INNER JOIN " +
				PagamentoDAODataSource.TABLE_NAME_CARD + " ON " + PagamentoDAODataSource.TABLE_NAME + ".CODICEPAGAMENTO = " +
				PagamentoDAODataSource.TABLE_NAME_CARD + ".CODICEPAGAMENTO";
		String countSQL = "SELECT COUNT(*) FROM " + PagamentoDAODataSource.TABLE_NAME;

		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;	//ordinamento dei pagamenti
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
				Pagamento pagamento = PagamentoServiceImpl.createPagamento(rs.getInt("CODICEPAGAMENTO"), new PagamentoDAODataSource(ds));
				pagamenti.add(pagamento);
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

		return pagamenti;
	}

}
