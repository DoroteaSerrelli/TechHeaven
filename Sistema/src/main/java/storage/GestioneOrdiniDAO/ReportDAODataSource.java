package storage.GestioneOrdiniDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.jdbc.pool.DataSource;

import application.GestioneOrdini.GestioneOrdiniService.ProxyOrdine;
import application.GestioneOrdini.GestioneOrdiniService.ReportSpedizione;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.OrdineVuotoException;

/**
 * Classe DAO per la gestione dei report di spedizione
 * degli ordini spediti nel database.
 * 
 * Questa classe implementa i metodi CRUD per la gestione dei report
 * di spedizione associati agli ordini elaborati dal negozio.
 * 
 * @author Dorotea Serrelli
 * */

public class ReportDAODataSource {
	
	/*private static DataSource ds;

	static {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/techheaven");

		} catch (NamingException e) {
			System.out.println("Error:" + e.getMessage());
		}
	}*/
	
	DataSource ds;
	
	public ReportDAODataSource(DataSource ds) {
		this.ds = ds;
	}
	
	public ReportDAODataSource() {
		 try {
		   Context initCtx = new InitialContext();
		   Context envCtx = (Context) initCtx.lookup("java:comp/env");
				
				this.ds = (DataSource) envCtx.lookup("jdbc/techheaven");
			} catch (NamingException ex) {
				//Logger.getLogger(ReportDAODataSource.class.getName()).log(Level.SEVERE, null, ex);
			}
	}
	
	private static final String TABLE_NAME = "Report_di_spedizione";
	
	/**
	 * Il metodo crea un report di spedizione per
	 * un ordine e lo memorizza nel DB.
	 * @param report : il report da salvare
	 * **/
	public synchronized void doSave(ReportSpedizione report) throws SQLException {
		//creare report
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertOrderSQL = "INSERT INTO " + ReportDAODataSource.TABLE_NAME
				+ " (NUMEROREPORT, CORRIERE, IMBALLAGGIO, DATASPEDIZIONE, ORASPEDIZIONE, ORDINE) VALUES (?, ?, ?, ?, ?, ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertOrderSQL);
			preparedStatement.setInt(1, report.getNumeroReport());
			preparedStatement.setString(2, report.getCorriere());
			preparedStatement.setString(3, report.getImballaggio());
			preparedStatement.setDate(4, java.sql.Date.valueOf(report.getDataSpedizione()));
			preparedStatement.setTime(5, java.sql.Time.valueOf(report.getOraSpedizione()));
			preparedStatement.setInt(6, report.getOrdine().getCodiceOrdine());
			
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
	 * Il metodo recupera dal DB tutte le caratteristiche di un report
	 * in base al codice dell'ordine.
	 * 
	 * @param IDOrdine : l'ordine
	 * @return il report di spedizione relativo all'ordine con codice IDOrdine 
	 * @throws ErroreTipoSpedizioneException 
	 * **/
	public synchronized ReportSpedizione doRetrieveReportByOrder(int IDOrdine) throws SQLException, OrdineVuotoException, ErroreTipoSpedizioneException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + ReportDAODataSource.TABLE_NAME + " WHERE ORDINE = ?";
		ReportSpedizione dto = new ReportSpedizione();
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDOrdine);

			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				
				dto.setNumeroReport(rs.getInt("NUMEROREPORT"));
				dto.setCorriere(rs.getString("CORRIERE"));
				dto.setImballaggio(rs.getString("IMBALLAGGIO"));
				dto.setDataSpedizione(rs.getDate("DATASPEDIZIONE").toLocalDate());
				dto.setOraSpedizione((rs.getTime("ORASPEDIZIONE")).toLocalTime());
				OrdineDAODataSource orderDAO = new OrdineDAODataSource(ds);
				ProxyOrdine order = orderDAO.doRetrieveProxyByKey(rs.getInt("ORDINE"));
				dto.setOrdine(order);
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
	 * Il metodo recupera dal DB tutte le caratteristiche di un report
	 * in base al codice del report.
	 * 
	 * @param IDReport : il codice identificativo del report
	 * @return il report di spedizione avente codice IDReport 
	 * @throws ErroreTipoSpedizioneException 
	 * **/
	public synchronized ReportSpedizione doRetrieveReportByKey(int IDReport) throws SQLException, OrdineVuotoException, ErroreTipoSpedizioneException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + ReportDAODataSource.TABLE_NAME + " WHERE NUMEROREPORT = ?";
		ReportSpedizione dto = new ReportSpedizione();
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDReport);

			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				
				dto.setNumeroReport(rs.getInt("NUMEROREPORT"));
				dto.setCorriere(rs.getString("CORRIERE"));
				dto.setImballaggio(rs.getString("IMBALLAGGIO"));
				dto.setDataSpedizione(rs.getDate("DATASPEDIZIONE").toLocalDate());
				dto.setOraSpedizione((rs.getTime("ORASPEDIZIONE")).toLocalTime());
				OrdineDAODataSource orderDAO = new OrdineDAODataSource(ds);
				ProxyOrdine order = orderDAO.doRetrieveProxyByKey(rs.getInt("ORDINE"));
				dto.setOrdine(order);
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
	 * Il metodo recupera dal DB tutti i report degli ordini commissionati al negozio online,
	 * utilizzando il meccanismo della paginazione degli elementi estratti
	 * dal DB.
	 * @param order: l'ordinamento con cui si organizzano i report degli ordini recuperati dal DB
	 * @param page : il numero di pagina
	 * @param perPage : il numero di ordini da inserire per ogni record/pagina
	 * 
	 * @return reports : le informazioni riguardanti i report degli ordini 
	 * 					(da spedire, in preparazione, evasi) commissionati al negozio online
	 * @throws ErroreTipoSpedizioneException 
	 * **/
	public synchronized Collection<ReportSpedizione> doRetrieveAll(String order, int page, int perPage) throws SQLException, ErroreTipoSpedizioneException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<ReportSpedizione> reports = new LinkedList<>();
		
		String selectSQL = "SELECT * FROM " + ReportDAODataSource.TABLE_NAME;
	    String countSQL = "SELECT COUNT(*) FROM " + ReportDAODataSource.TABLE_NAME;
		
		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;	//ordinamento dei reports
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
				ReportSpedizione dto = new ReportSpedizione();
				dto.setNumeroReport(rs.getInt("NUMEROREPORT"));
				dto.setCorriere(rs.getString("CORRIERE"));
				dto.setImballaggio(rs.getString("IMBALLAGGIO"));
				dto.setDataSpedizione(rs.getDate("DATASPEDIZIONE").toLocalDate());
				dto.setOraSpedizione((rs.getTime("ORASPEDIZIONE")).toLocalTime());
				OrdineDAODataSource orderDAO = new OrdineDAODataSource(ds);
				ProxyOrdine ordine = orderDAO.doRetrieveProxyByKey(rs.getInt("ORDINE"));
				dto.setOrdine(ordine);
				reports.add(dto);
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
		return reports;
	}
}
