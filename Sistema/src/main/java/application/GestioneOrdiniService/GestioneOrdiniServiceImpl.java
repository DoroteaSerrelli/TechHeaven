package application.GestioneOrdiniService;

import java.sql.SQLException;
import java.util.ArrayList;

import application.GestioneCarrelloService.*;
import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneOrdiniService.OrdineException.ErroreSpedizioneOrdineException;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.PagamentoService.Pagamento;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.RegistrazioneService.ProxyUtente;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import application.PagamentoService.*;
import storage.NavigazioneDAO.*;

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
		ArrayList<ProxyOrdine> ordini = new ArrayList<> (dao.doRetrieveOrderShipped(null, page, perPage));
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
		ArrayList<ProxyOrdine> ordini = new ArrayList<> (dao.doRetrieveOrderToShip(null, page, perPage));
		return ordini;
	}

	/**
	 * Il metodo implementa il servizio di commissione (o creazione) di un ordine
	 * fatto dal cliente verso il negozio online.
	 * 
	 * @param user : l'utente che intende acquistare
	 * @param cart : il carrello dell'utente contenente i prodotti da acquistare
	 * @param order : l'ordine effettuato dall'utente, contenente almeno un prodotto 
	 * 					acquistato, da memorizzare
	 * @param payment : il pagamento associato all'ordine order
	 * 
	 * @return il carrello dell'utente cart svuotato
	 * 
	 * @throws SQLException 
	 * @throws ProdottoNulloException 
	 * @throws CarrelloVuotoException 
	 * @throws ProdottoNonPresenteException 
	 * @throws ModalitaAssenteException 
	 * @throws OrdineVuotoException 
	 * @throws CloneNotSupportedException 
	 * **/
	@Override
	public <T extends Pagamento> Carrello commissionaOrdine(Carrello cart, Ordine order, T payment, ProxyUtente user) throws SQLException, ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException, OrdineVuotoException, ModalitaAssenteException, CloneNotSupportedException{
		OrdineDAODataSource dao = new OrdineDAODataSource();
		UtenteDAODataSource userDao = new UtenteDAODataSource();
		if(userDao.doRetrieveProxyUserByKey(user.getUsername()) == null)
			System.out.println("Errore utente!");
		dao.doSave(order);

		//effettua il pagamento
		PagamentoService gestionePagamentoService = new PagamentoServiceImpl();
		gestionePagamentoService.effettuaPagamento(payment);

		GestioneCarrelloService gestioneCarrelloService = new GestioneCarrelloServiceImpl();

		return gestioneCarrelloService.svuotaCarrello(cart);
	}


	/**
	 * Il metodo implementa il servizio di preparazione di un ordine, commissionato
	 * da un cliente verso il negozio online, alla spedizione.
	 * 
	 * @param order : l'ordine da evadere
	 * @param report : il report di spedizione
	 * 
	 * @throws SQLException 
	 * @throws ModalitaAssenteException 
	 * @throws OrdineVuotoException 
	 * @throws ErroreSpedizioneOrdineException 
	 * @throws CloneNotSupportedException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * 
	 * **/

	@Override
	public void preparazioneSpedizioneOrdine(Ordine order, ReportSpedizione report) throws ErroreSpedizioneOrdineException, OrdineVuotoException, ModalitaAssenteException, SQLException, CloneNotSupportedException, SottocategoriaProdottoException, CategoriaProdottoException {
		//Si imposta lo stato dell'ordine in 'SPEDITO'
		order.setStatoAsString("SPEDITO");

		//Ritrovare il pagamento dell'ordine
		Pagamento payment = PagamentoServiceImpl.createPagamentoOrdine(order.getCodiceOrdine());

		//Aggiornamento ordine nel Database
		OrdineDAODataSource orderDao = new OrdineDAODataSource();
		orderDao.doSaveToShip(order, report);

		//Reinserire il pagamento (il metodo doSaveToShip rimuove il pagamento per aggiornare lo stato dell'ordine)
		PagamentoService gestionePagamentoService = new PagamentoServiceImpl();
		gestionePagamentoService.effettuaPagamento(payment);

		//Aggiornamento delle quantità dei prodotti ordinati in magazzino
		ArrayList<ItemCarrello> orderedProducts = order.getProdotti();
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();
		for(ItemCarrello item : orderedProducts) {
			//recupero numero scorte in magazzino per quel prodotto
			int quantity = (productDao.doRetrieveCompleteByKey(item.getCodiceProdotto())).getQuantita();
			//aggiornamento quantità
			productDao.updateQuantity(0, quantity - item.getQuantita());
		}
	}
}
