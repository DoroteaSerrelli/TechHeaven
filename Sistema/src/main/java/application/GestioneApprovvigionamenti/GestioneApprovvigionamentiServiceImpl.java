package application.GestioneApprovvigionamenti;

import java.sql.SQLException;
import java.util.Collection;

import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.FornitoreException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import storage.GestioneApprovvigionamentiDAO.*;

public class GestioneApprovvigionamentiServiceImpl implements GestioneApprovvigionamentiService {
	
	/**
	 * Il metodo implementa il servizio di visualizzazione delle richieste 
	 * di approvvigionamento di prodotti dell'e-commerce.
	 * 
	 * @param page : numero della pagina
	 * @param perPage: numero di richieste di approvvigionamento per pagina
	 * 
	 * @return una collezione di richieste di rifornimento dei prodotti non disponibili
	 * 			in magazzino
	 * @throws SQLException 
	 * @throws ProdottoVendibileException 
	 * @throws QuantitaProdottoException 
	 * @throws DescrizioneDettaglioException 
	 * @throws FornitoreException 
	 * */
	
	@Override
	public Collection<RichiestaApprovvigionamento> visualizzaRichiesteFornitura(int page, int perPage) throws FornitoreException, DescrizioneDettaglioException, QuantitaProdottoException, ProdottoVendibileException, SQLException {
		ApprovvigionamentoDAODataSource supplyDao = new ApprovvigionamentoDAODataSource();
		return supplyDao.doRetrieveAll(null, page, perPage);
	}
	
	
	/**
	 * Il metodo implementa il servizio di visualizzazione delle richieste 
	 * di approvvigionamento di prodotti dell'e-commerce.
	 * 
	 * @param supply : la richiesta di rifornimento effettuata da memorizzare
	 * @throws SQLException 
	 * 
	 * */
	
	@Override
	public void effettuaRichiestaApprovvigionamento(RichiestaApprovvigionamento supply) throws SQLException {
		ApprovvigionamentoDAODataSource supplyDao = new ApprovvigionamentoDAODataSource();
		supplyDao.doSave(supply);
	}

}
