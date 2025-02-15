package storage.GestioneApprovvigionamentiDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamento;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.FormatoFornitoreException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Classe DAO per la gestione delle richieste di approvvigionamento
 * di prodotti nel database.
 * 
 * Questa classe implementa i metodi CRUD per la gestione delle richieste
 * di approvvigionamento dei prodotti presenti nel
 * catalogo del negozio.
 * 
 * @author Dorotea Serrelli
 * */

public class ApprovvigionamentoDAODataSource {
	
	DataSource ds;
	
	public ApprovvigionamentoDAODataSource(DataSource ds) {
		this.ds = ds;
	}
	
	public ApprovvigionamentoDAODataSource(){
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            this.ds = (DataSource) envCtx.lookup("jdbc/techheaven");
        } catch (NamingException ex) {
            //Logger.getLogger(ProdottoDAODataSource.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
	
private static final String TABLE_NAME = "richiesta_fornitura";
	
	/**
	 * Il metodo crea una richiesta di approvvigionamento di un prodotto e 
	 * la memorizza nel DB.
	 * 
	 * @param supplyRequest : la richiesta di approvvigionamento da salvare
	 * 
	 * @throws SQLException
	 * **/

	public synchronized void doSave(RichiestaApprovvigionamento supplyRequest) throws SQLException {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String insertOrderSQL = "INSERT INTO " + ApprovvigionamentoDAODataSource.TABLE_NAME
				+ " (CODICERICHIESTA, FORNITORE, EMAILFORNITORE, DESCRIZIONE, QUANTITàRIFORNIMENTO, PRODOTTO) VALUES (?, ?, ?, ?, ?, ?)";

		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(insertOrderSQL);
			preparedStatement.setInt(1, supplyRequest.getCodiceRifornimento());
			preparedStatement.setString(2, supplyRequest.getFornitore());
			preparedStatement.setString(3, supplyRequest.getEmailFornitore());
			preparedStatement.setString(4, supplyRequest.getDescrizione());
			preparedStatement.setInt(5, supplyRequest.getQuantitaRifornimento());
			preparedStatement.setInt(6, supplyRequest.getProdotto().getCodiceProdotto());
			
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
	 * Il metodo recupera dal DB tutte le richieste di approvvigionamento
	 * effettuate per un prodotto, in base al codice del prodotto.
	 * 
	 * @param IDProdotto : il codice del prodotto
	 * @return l'insieme delle richieste di approvvigionamento fatte per un prodotto con codice IDProdotto 
	 * @throws FormatoFornitoreException 
	 * @throws DescrizioneDettaglioException 
	 * @throws QuantitaProdottoException 
	 * @throws ProdottoVendibileException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * **/
	public synchronized RichiestaApprovvigionamento doRetrieveReportByOrder(int IDProdotto) throws SQLException, FormatoFornitoreException, DescrizioneDettaglioException, QuantitaProdottoException, ProdottoVendibileException, SottocategoriaProdottoException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + ApprovvigionamentoDAODataSource.TABLE_NAME + " WHERE PRODOTTO = ?";
		RichiestaApprovvigionamento dto = new RichiestaApprovvigionamento();
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDProdotto);

			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				
				dto.setCodice(rs.getInt("CODICERICHIESTA"));
				dto.setFornitore(rs.getString("FORNITORE"));
				dto.setEmailFornitore(rs.getString("EMAILFORNITORE"));
				dto.setDescrizione(rs.getString("DESCRIZIONE"));
				dto.setQuantitaRifornimento(rs.getInt("QUANTITàRIFORNIMENTO"));
				ProdottoDAODataSource productDAO = new ProdottoDAODataSource(ds, new PhotoControl(ds));
				ProxyProdotto product = productDAO.doRetrieveProxyByKey(rs.getInt("PRODOTTO"));
				dto.setProdotto(product);
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
	 * Il metodo recupera dal DB tutte le caratteristiche di una richiesta di approvvigionamento
	 * in base al codice della richiesta.
	 * 
	 * @param IDSupplyRequest : il codice identificativo della richiesta di rifornimento
	 * @return la richiesta di rifornimento avente codice IDSupplyRequest 
	 * @throws FormatoFornitoreException 
	 * @throws DescrizioneDettaglioException 
	 * @throws QuantitaProdottoException 
	 * @throws ProdottoVendibileException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * **/
	public synchronized RichiestaApprovvigionamento doRetrieveReportByKey(int IDSupplyRequest) throws SQLException, OrdineVuotoException, FormatoFornitoreException, DescrizioneDettaglioException, QuantitaProdottoException, ProdottoVendibileException, SottocategoriaProdottoException, CategoriaProdottoException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT * FROM " + ApprovvigionamentoDAODataSource.TABLE_NAME + " WHERE CODICERICHIESTA = ?";
		RichiestaApprovvigionamento dto = new RichiestaApprovvigionamento();
		
		try {
			connection = ds.getConnection();
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, IDSupplyRequest);

			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				
				dto.setCodice(rs.getInt("CODICERICHIESTA"));
				dto.setFornitore(rs.getString("FORNITORE"));
				dto.setEmailFornitore(rs.getString("EMAILFORNITORE"));
				dto.setDescrizione(rs.getString("DESCRIZIONE"));
				dto.setQuantitaRifornimento(rs.getInt("QUANTITàRIFORNIMENTO"));
				ProdottoDAODataSource productDAO = new ProdottoDAODataSource(ds, new PhotoControl(ds));
				ProxyProdotto product = productDAO.doRetrieveProxyByKey(rs.getInt("PRODOTTO"));
				dto.setProdotto(product);
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
	 * Il metodo recupera dal DB tutte le richieste di approvvigionamento 
	 * utilizzando il meccanismo della paginazione degli elementi estratti
	 * dal DB.
	 * 
	 * @param order: l'ordinamento con cui si organizzano le richieste di rifornimento recuperate dal DB
	 * @param page : il numero di pagina
	 * @param perPage : il numero di ordini da inserire per ogni record/pagina
	 * 
	 * @return supplyRequests : le informazioni riguardanti le richieste di rifornimento
	 * @throws FormatoFornitoreException 
	 * @throws DescrizioneDettaglioException 
	 * @throws QuantitaProdottoException 
	 * @throws ProdottoVendibileException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * **/
	public synchronized Collection<RichiestaApprovvigionamento> doRetrieveAll(String order, int page, int perPage) throws SQLException, FormatoFornitoreException, DescrizioneDettaglioException, QuantitaProdottoException, ProdottoVendibileException, SottocategoriaProdottoException, CategoriaProdottoException {
		Connection connection = null;
		Connection connection2 = null;
		PreparedStatement preparedStatement = null;

		Collection<RichiestaApprovvigionamento> supplyRequests = new LinkedList<>();
		
		String selectSQL = "SELECT * FROM " + ApprovvigionamentoDAODataSource.TABLE_NAME;
	    String countSQL = "SELECT COUNT(*) FROM " + ApprovvigionamentoDAODataSource.TABLE_NAME;
		
		if (order != null && !order.equals("")) {
			selectSQL += " ORDER BY " + order;	//ordinamento delle richieste di approvvigionamento
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
				RichiestaApprovvigionamento dto = new RichiestaApprovvigionamento();
				dto.setCodice(rs.getInt("CODICERICHIESTA"));
				dto.setFornitore(rs.getString("FORNITORE"));
				dto.setEmailFornitore(rs.getString("EMAILFORNITORE"));
				dto.setDescrizione(rs.getString("DESCRIZIONE"));
				dto.setQuantitaRifornimento(rs.getInt("QUANTITàRIFORNIMENTO"));
				ProdottoDAODataSource productDAO = new ProdottoDAODataSource(ds, new PhotoControl(ds));
				ProxyProdotto product = productDAO.doRetrieveProxyByKey(rs.getInt("PRODOTTO"));
				dto.setProdotto(product);
				supplyRequests.add(dto);
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
		return supplyRequests;
	}
}
