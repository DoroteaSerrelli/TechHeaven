package application.GestioneOrdiniService;

import java.sql.SQLException;
import java.util.ArrayList;

import application.GestioneCarrelloService.*;
import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.RegistrazioneService.ProxyUtente;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;

/**
 * Questa classe fornisce un'implementazione concreta dei servizi per la gestione degli ordini.
 * @see application.GestioneOrdiniService.GestioneOrdiniService
 * @see	application.GestioneOrdiniService.ProxyOrdine
 * @see application.GestioneOrdiniService.Ordine
 * @see application.GestioneOrdiniService.ReportSpedizione
 * @see package storage.GestioneOrdiniDAO
 * 
 * @author Dorotea Serrelli 
 * */

public class GestioneOrdiniServiceImpl implements GestioneOrdiniService{
	
	/**
	 * Il metodo implementa il servizio di recupero degli ordini evasi
	 * dal negozio online.
	 * @param page : numero della pagina
	 * @param perPage: numero di ordini per pagina
	 * 
	 * @return gli ordini evasi
	 * @throws SQLException per gestire il recupero degli ordini dal DB
	 * **/
	@Override
	public ArrayList<ProxyOrdine> visualizzaOrdiniEvasi(int page, int perPage) throws SQLException {
		OrdineDAODataSource dao = new OrdineDAODataSource();
		ArrayList<ProxyOrdine> ordini = new ArrayList<> (dao.doRetrieveOrderShipped(null, 0, 0));
		return ordini;
	}
	
	/**
	 * Il metodo implementa il servizio di recupero degli ordini commissionati
	 * al negozio online ma non ancora spediti.
	 * @param page : numero della pagina
	 * @param perPage: numero di ordini per pagina
	 * 
	 * @return gli ordini da preparare alla spedizione
	 * @throws SQLException per gestire il recupero degli ordini dal DB
	 * **/
	@Override
	public ArrayList<ProxyOrdine> visualizzaOrdiniDaEvadere(int page, int perPage) throws SQLException {
		OrdineDAODataSource dao = new OrdineDAODataSource();
		ArrayList<ProxyOrdine> ordini = new ArrayList<> (dao.doRetrieveOrderToShip(null, 0, 0));
		return ordini;
	}
	
	/**
	 * Il metodo implementa il servizio di commissione (o creazione) di un ordine
	 * fatto dal cliente verso il negozio online.
	 * @param ordine : l'ordine dell'utente, contenente almeno un prodotto
	 * 					da acquistare
	 * @param user : l'utente che intende acquistare
	 * @throws SQLException 
	 * @throws ProdottoNulloException 
	 * @throws CarrelloVuotoException 
	 * @throws ProdottoNonPresenteException 
	 * **/
	@Override
	public Carrello commissionaOrdine(Carrello cart, Ordine ordine, ProxyUtente user) throws SQLException, ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException{
		OrdineDAODataSource dao = new OrdineDAODataSource();
		UtenteDAODataSource userDao = new UtenteDAODataSource();
		if(userDao.doRetrieveProxyUserByKey(user.getUsername()) == null)
			System.out.println("Errore utente!");
		dao.doSave(ordine);
		GestioneCarrelloService gestioneCarrelloService = new GestioneCarrelloServiceImpl();

	    return gestioneCarrelloService.svuotaCarrello(cart);
	}

	@Override
	public void preparazioneSpedizioneOrdine(Ordine order, ReportSpedizione report) {
		
		
	}

}
