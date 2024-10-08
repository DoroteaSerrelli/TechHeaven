package application.GestioneOrdiniService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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
 * Questa classe fornisce un'implementazione concreta dei servizi 
 * per la gestione degli ordini del sito e-commerce.
 * 
 * @see application.GestioneOrdiniService.GestioneOrdiniService
 * @see	application.GestioneOrdiniService.ProxyOrdine
 * @see application.GestioneOrdiniService.Ordine
 * @see application.GestioneOrdiniService.OrdineException
 * @see application.GestioneOrdiniService.ReportSpedizione
 * @see application.RegistrazioneService.ProxyUtente
 * @see application.GestioneCarrelloService.Carrello
 * @see application.PagamentoService.Pagamento
 * @see storage.GestioneOrdiniDAO
 * 
 * @author Dorotea Serrelli 
 * */

public class GestioneOrdiniServiceImpl implements GestioneOrdiniService{

	/**
	 * Il metodo implementa il servizio di recupero degli ordini evasi
	 * dal negozio online.
	 * I suddetti ordini vengono forniti usando il meccanismo
	 * della paginazione.
	 * 
	 * @param page : numero della pagina
	 * @param perPage: numero di ordini per pagina
	 * 
	 * @return gli ordini evasi dal negozio
	 * 
	 * @throws SQLException
	 * **/
	
	@Override
	public Collection<ProxyOrdine> visualizzaOrdiniEvasi(int page, int perPage) throws SQLException {
		OrdineDAODataSource dao = new OrdineDAODataSource();
		Collection<ProxyOrdine> ordini = dao.doRetrieveOrderShipped(null, page, perPage);
		return ordini;
	}

	/**
	 * Il metodo implementa il servizio di recupero degli ordini commissionati
	 * al negozio online ma non ancora spediti.
	 * I suddetti ordini vengono forniti usando il meccanismo
	 * della paginazione.
	 * 
	 * @param page : numero della pagina
	 * @param perPage: numero di ordini per pagina
	 * 
	 * @return gli ordini da preparare alla spedizione
	 * 
	 * @throws SQLException 
	 * **/
	
	@Override
	public Collection<ProxyOrdine> visualizzaOrdiniDaEvadere(int page, int perPage) throws SQLException {
		OrdineDAODataSource dao = new OrdineDAODataSource();
		Collection<ProxyOrdine> ordini = dao.doRetrieveOrderToShip(null, page, perPage);
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
	 * @throws ProdottoNulloException : gestire il riferimento null ad un oggetto ItemCarrello
	 * @throws CarrelloVuotoException : gestire il caso in cui si voglia fare un acquisto con un carrello vuoto
	 * @throws ProdottoNonPresenteException : gestire la mancanza di un prodotto nel carrello
	 * @throws ModalitaAssenteException : gestire una modalità di pagamento non consentita nel sito di e-commerce
	 * @throws OrdineVuotoException : gestire il caso in cui si voglia concludere un acquisto con un ordine senza prodotti 
	 * @throws CloneNotSupportedException 
	 ***/
	
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
	 * @param report : il report di spedizione di order
	 * 
	 * @throws SQLException 
	 * @throws ModalitaAssenteException : gestire una modalità di pagamento non consentita nel sito di e-commerce
	 * @throws OrdineVuotoException : gestire il caso in cui si voglia concludere un acquisto con un ordine senza prodotti 
	 * @throws ErroreSpedizioneOrdineException : gestire il caso in cui si voglia memorizzare un ordine che non è nello stato 'Spedito'
	 * @throws CloneNotSupportedException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
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
