package storage.GestioneOrdiniDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import application.PagamentoService.Pagamento;
import application.PagamentoService.PagamentoCartaCredito;
import application.PagamentoService.PagamentoContrassegno;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.PagamentoService.PagamentoPaypal;
import application.PagamentoService.PagamentoServiceImpl;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;

/**
 * Classe DAO per la gestione del datasource dei pagamenti.
 * Questa classe implementa le operazioni CRUD (Create, Read, Update, Delete)
 * e la ricerca dei pagamenti memorizzati nel database relazionale.
 * 
 * @author : Dorotea Serrelli
 * */

public class PagamentoDAODataSource {

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
				+ " (CODICEPAGAMENTO, ORDINE, DATAPAGAMENTO, ORAPAGAMENTO, IMPORTO) VALUES (?, ?, ?, ?, ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertPaymentSQL);
			preparedStatement.setInt(1, payment.getCodicePagamento());
			preparedStatement.setInt(2, payment.getOrdine().getCodiceOrdine());
			preparedStatement.setDate(3, java.sql.Date.valueOf(payment.getDataPagamento()));
			preparedStatement.setTime(4, java.sql.Time.valueOf(payment.getOraPagamento()));
			preparedStatement.setFloat(5, payment.getImporto());

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
	 * **/

	public synchronized PagamentoContrassegno doRetrieveCashByOrder(int IDOrdine) throws SQLException, OrdineVuotoException, CategoriaProdottoException {
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
				OrdineDAODataSource orderDao = new OrdineDAODataSource();

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
	 * in Paypal in base al codice dell'ordine.
	 * 
	 * @param IDOrdine : l'ordine
	 * @return il pagamento con Paypal relativo all'ordine con codice IDOrdine 
	 * @throws CategoriaProdottoException 
	 * **/

	public synchronized PagamentoPaypal doRetrievePaypalByOrder(int IDOrdine) throws SQLException, OrdineVuotoException, CategoriaProdottoException {
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
				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
				OrdineDAODataSource orderDao = new OrdineDAODataSource();

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
	 * in carta di credito in base al codice dell'ordine.
	 * 
	 * @param IDOrdine : l'ordine
	 * @return il pagamento con carta di credito relativo all'ordine con codice IDOrdine 
	 * @throws CategoriaProdottoException 
	 * **/

	public synchronized PagamentoCartaCredito doRetrieveCardByOrder(int IDOrdine) throws SQLException, OrdineVuotoException, CategoriaProdottoException {
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
				OrdineDAODataSource orderDao = new OrdineDAODataSource();

				dto.setCodicePagamento(rs.getInt("CODICEPAGAMENTO"));
				dto.setOrdine(orderDao.doRetrieveFullOrderByKey(rs.getInt("ORDINE")));
				dto.setDataPagamento(rs.getDate("DATAPAGAMENTO").toLocalDate());
				dto.setOraPagamento((rs.getTime("ORAPAGAMENTO")).toLocalTime());
				dto.setImporto(rs.getFloat("IMPORTO"));
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
	 */

	public synchronized <T extends Pagamento> T doRetrievePaymentByOrder(Class<T> paymentClass, int IDOrdine) throws SQLException, OrdineVuotoException, ModalitaAssenteException, CategoriaProdottoException {
		Pagamento payment = PagamentoServiceImpl.createPagamentoOrdine(IDOrdine);

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
	 * **/
	public synchronized PagamentoContrassegno doRetrieveCashByKey(int IDPayment) throws SQLException, OrdineVuotoException, CategoriaProdottoException {
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

				OrdineDAODataSource orderDao = new OrdineDAODataSource();

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
	 * **/
	public synchronized PagamentoPaypal doRetrievePaypalByKey(int IDPayment) throws SQLException, OrdineVuotoException, CategoriaProdottoException {
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

				OrdineDAODataSource orderDao = new OrdineDAODataSource();

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
	 * **/
	public synchronized PagamentoCartaCredito doRetrieveCardByKey(int IDPayment) throws SQLException, OrdineVuotoException, CategoriaProdottoException {
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

				OrdineDAODataSource orderDao = new OrdineDAODataSource();

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
	 */

	public synchronized <T extends Pagamento> T doRetrievePaymentByKey(Class<T> paymentClass, int IDPagamento) throws SQLException, OrdineVuotoException, ModalitaAssenteException, CategoriaProdottoException {
		Pagamento payment = PagamentoServiceImpl.createPagamento(IDPagamento);

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
	 */
	public synchronized List<Pagamento> doRetrieveAllPayments(String order, int page, int perPage) throws SQLException, OrdineVuotoException, ModalitaAssenteException, CategoriaProdottoException {
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
				Pagamento pagamento = PagamentoServiceImpl.createPagamento(rs.getInt("CODICEPAGAMENTO"));
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
