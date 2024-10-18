package application.GestioneOrdiniService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import application.GestioneCarrelloService.*;
import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneOrdiniService.ObjectOrdine.TipoConsegna;
import application.GestioneOrdiniService.ObjectOrdine.TipoSpedizione;
import application.GestioneOrdiniService.OrdineException.ErroreSpedizioneOrdineException;
import application.GestioneOrdiniService.OrdineException.ErroreTipoConsegnaException;
import application.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdiniService.OrdineException.FormatoCorriereException;
import application.GestioneOrdiniService.OrdineException.FormatoImballaggioException;
import application.GestioneOrdiniService.OrdineException.FormatoQuantitaException;
import application.GestioneOrdiniService.OrdineException.IndirizzoSpedizioneNulloException;
import application.GestioneOrdiniService.OrdineException.MancanzaPezziException;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.PagamentoService.PagamentoException.FormatoCVVCartaException;
import application.PagamentoService.PagamentoException.FormatoDataCartaException;
import application.PagamentoService.PagamentoException.FormatoNumeroCartaException;
import application.PagamentoService.PagamentoException.FormatoTitolareCartaException;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
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

	private OrdineDAODataSource orderDAO;
	private UtenteDAODataSource userDAO;
	private ProdottoDAODataSource productDAO;
	private PagamentoDAODataSource paymentDAO;

	public GestioneOrdiniServiceImpl(OrdineDAODataSource orderDAO, UtenteDAODataSource userDAO, ProdottoDAODataSource productDAO, PagamentoDAODataSource paymentDAO) {
		this.orderDAO = orderDAO;
		this.userDAO = userDAO;
		this.productDAO = productDAO;
		this.paymentDAO = paymentDAO;
	}

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
	 * @throws ErroreTipoSpedizioneException 
	 * **/

	@Override
	public Collection<ProxyOrdine> visualizzaOrdiniEvasi(int page, int perPage) throws SQLException, ErroreTipoSpedizioneException {

		Collection<ProxyOrdine> ordini = orderDAO.doRetrieveOrderShipped(null, page, perPage);
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
	 * @throws ErroreTipoSpedizioneException 
	 * **/

	@Override
	public Collection<ProxyOrdine> visualizzaOrdiniDaEvadere(int page, int perPage) throws SQLException, ErroreTipoSpedizioneException {

		Collection<ProxyOrdine> ordini = orderDAO.doRetrieveOrderToShip(null, page, perPage);
		return ordini;
	}

	//metodo per creare ordine
	public Ordine creaOrdine(Carrello cart, ProxyUtente user, String idIndirizzo, String modSpedizione, String modConsegna) throws IndirizzoSpedizioneNulloException, ErroreTipoSpedizioneException, SQLException, ErroreTipoConsegnaException, OrdineVuotoException {

		Ordine order = new Ordine();

		if(idIndirizzo.isBlank())
			throw new IndirizzoSpedizioneNulloException("Specificare l'indirizzo di spedizione per l'ordine. Per aggiungere un altro indirizzo, annulla l'acquisto e vai nell'area riservata.");

		ArrayList<Indirizzo> userAddresses = user.mostraUtente().getProfile().getIndirizzi();
		for(Indirizzo i : userAddresses) {
			if(i.getIDIndirizzo() == Integer.parseInt(idIndirizzo))
				order.setIndirizzoSpedizione(i);
		}

		if(order.getIndirizzoSpedizione() == null)
			throw new IndirizzoSpedizioneNulloException("Errore nel recupero dell'indirizzo di spedizione: riprova ad inserirlo");


		switch (modSpedizione.toUpperCase()) {
		case "SPEDIZIONE_STANDARD", "SPEDIZIONE STANDARD":
			order.setSpedizione(TipoSpedizione.Spedizione_standard);
		break;
		case "SPEDIZIONE_PRIME", "SPEDIZIONE PRIME":
			order.setSpedizione(TipoSpedizione.Spedizione_prime);
		break;
		case "SPEDIZIONE_ASSICURATA", "SPEDIZIONE ASSICURATA":
			order.setSpedizione(TipoSpedizione.Spedizione_assicurata);
		break;
		default:
			throw new ErroreTipoSpedizioneException("Specificare la modalità di spedizione per l'ordine: standard, prime, assicurata.");
		}

		switch (modConsegna.toUpperCase()) {
		case "DOMICILIO":
			order.setConsegna(TipoConsegna.Domicilio);
			break;
		case "PUNTO_RITIRO", "PUNTO RITIRO":
			order.setConsegna(TipoConsegna.Punto_ritiro);
		break;
		case "PRIORITY":
			order.setConsegna(TipoConsegna.Priority);
			break;
		default:
			throw new ErroreTipoConsegnaException("Specificare la modalità di consegna per l'ordine: domicilio, punto di ritiro, priority/fascia oraria.");
		}

		order.setAcquirente(user.mostraUtente().getProfile());
		order.setProdotti(cart.getProducts());

		return order;
	}

	//metodo per creare pagamento

	public <T extends Pagamento> Pagamento creaPagamento_PaypalContrassegno(Carrello cart, Ordine order, String modPagamento) throws ModalitaAssenteException {

		Pagamento pagamento = null;

		switch(modPagamento.toUpperCase()) {
		case "PAYPAL":
			pagamento = new PagamentoPaypal(1, order, (float) cart.totalAmount());
			break; 

		case "CONTRASSEGNO":
			pagamento = new PagamentoContrassegno(1, order, (float) cart.totalAmount());
			break;

		default :
			throw new ModalitaAssenteException("Specificare la modalità di pagamento: carta di credito, Paypal, contrassegno.");
		}

		return pagamento;

	}

	public <T extends Pagamento> Pagamento creaPagamento_cartaCredito(Carrello cart, Ordine order, String modPagamento, String titolare, String noCard, String expiryDate, String CVV) throws ModalitaAssenteException, FormatoCVVCartaException, FormatoDataCartaException, FormatoTitolareCartaException, FormatoNumeroCartaException {

		Pagamento pagamento = null;

		if(!modPagamento.toUpperCase().equals("CARTA_CREDITO"))
			throw new ModalitaAssenteException("Specificare la modalità di pagamento: carta di credito, Paypal, contrassegno.");

		try{
			if(PagamentoCartaCredito.checkValidate(titolare, noCard, expiryDate, CVV)) {
				pagamento = new PagamentoCartaCredito(1, order, (float) cart.totalAmount(), titolare, noCard);
			}
		}catch(FormatoDataCartaException ex) {

			throw new FormatoDataCartaException("La data di scadenza della carta non è valida.");

		}catch(FormatoTitolareCartaException e) {
			throw new FormatoTitolareCartaException("Il titolare deve essere una sequenza di lettere e spazi.");

		}catch(FormatoNumeroCartaException e) {
			throw new FormatoNumeroCartaException("Il numero della carta è formato da 16 numeri.");

		}catch(FormatoCVVCartaException e) {
			throw new FormatoCVVCartaException("Il numero CVV è formato da 3 numeri.");

		}

		return pagamento;
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
		if(userDAO.doRetrieveProxyUserByKey(user.getUsername()) == null)
			System.out.println("Errore utente!");
		orderDAO.doSave(order);

		//effettua il pagamento
		PagamentoService gestionePagamentoService = new PagamentoServiceImpl(paymentDAO);
		gestionePagamentoService.effettuaPagamento(payment);

		GestioneCarrelloService gestioneCarrelloService = new GestioneCarrelloServiceImpl(productDAO);

		return gestioneCarrelloService.svuotaCarrello(cart);
	}

	
	public ReportSpedizione creaReportSpedizione(Ordine ordineSelezionato, ArrayList<ItemCarrello> prodottiRichiesti, ArrayList<Integer> quantità, String imballaggio, String corriere) throws SottocategoriaProdottoException, CategoriaProdottoException, MancanzaPezziException, FormatoQuantitaException, FormatoImballaggioException, FormatoCorriereException, SQLException, CloneNotSupportedException {
		
		if(ReportSpedizione.checkValidateReport(prodottiRichiesti, quantità, imballaggio, corriere, productDAO)) {
			
			ReportSpedizione report = new ReportSpedizione(ordineSelezionato.getCodiceOrdine(), corriere, imballaggio, ordineSelezionato);          
	        return report;
			
		}
		
		return null;
	}
	
	
	
	
	/**
	 * Il metodo implementa il servizio di preparazione di un ordine, commissionato
	 * da un cliente verso il negozio online, alla spedizione.
	 * 
	 * @param order : l'ordine da evadere
	 * @param report : il report di spedizione di order
	 * @return l'ordine spedito
	 * 
	 * @throws SQLException 
	 * @throws ModalitaAssenteException : gestire una modalità di pagamento non consentita nel sito di e-commerce
	 * @throws OrdineVuotoException : gestire il caso in cui si voglia concludere un acquisto con un ordine senza prodotti 
	 * @throws ErroreSpedizioneOrdineException : gestire il caso in cui si voglia memorizzare un ordine che non è nello stato 'Spedito'
	 * @throws CloneNotSupportedException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 * **/

	@Override
	public Ordine preparazioneSpedizioneOrdine(Ordine order, ReportSpedizione report) throws ErroreSpedizioneOrdineException, OrdineVuotoException, ModalitaAssenteException, SQLException, CloneNotSupportedException, SottocategoriaProdottoException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		//Si imposta lo stato dell'ordine in 'SPEDITO'
		order.setStatoAsString("SPEDITO");

		//Ritrovare il pagamento dell'ordine
		Pagamento payment = PagamentoServiceImpl.createPagamentoOrdine(order.getCodiceOrdine(), paymentDAO);

		//Aggiornamento ordine nel Database
		orderDAO.doSaveToShip(order, report);

		//Reinserire il pagamento (il metodo doSaveToShip rimuove il pagamento per aggiornare lo stato dell'ordine)
		PagamentoService gestionePagamentoService = new PagamentoServiceImpl(paymentDAO);
		gestionePagamentoService.effettuaPagamento(payment);

		//Aggiornamento delle quantità dei prodotti ordinati in magazzino
		ArrayList<ItemCarrello> orderedProducts = order.getProdotti();

		for(ItemCarrello item : orderedProducts) {
			//recupero numero scorte in magazzino per quel prodotto
			int quantity = (productDAO.doRetrieveCompleteByKey(item.getCodiceProdotto())).getQuantita();
			//aggiornamento quantità
			productDAO.updateQuantity(0, quantity - item.getQuantita());
		}
		
		return order;
	}
}
