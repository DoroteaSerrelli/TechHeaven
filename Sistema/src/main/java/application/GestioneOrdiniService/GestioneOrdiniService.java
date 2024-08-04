package application.GestioneOrdiniService;

import java.sql.SQLException;
import java.util.ArrayList;

import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.RegistrazioneService.ProxyUtente;

/**
 * Interfaccia che si occupa di offrire servizi relativi alla
 * gestione degli ordini: creazione di un ordine, visualizzazione ordini evasi, 
 * visualizzazione ordini commissionati da evadere, 
 * preparazione di un ordine alla spedizione.
 * 
 * @author Dorotea Serrelli
 * */

public interface GestioneOrdiniService {
	
	/**
	 * Il metodo esprime il servizio di recupero degli ordini evasi
	 * dal negozio online.
	 * @param page : numero della pagina
	 * @param perPage: numero di ordini per pagina
	 * 
	 * @return gli ordini evasi
	 * @throws SQLException
	 * **/
	public ArrayList<ProxyOrdine> visualizzaOrdiniEvasi(int page, int perPage) throws SQLException;
	
	/**
	 * Il metodo esprime il servizio di recupero degli ordini commissionati
	 * al negozio online ma non ancora spediti. 
	 * @param page : numero della pagina
	 * @param perPage: numero di ordini per pagina
	 * 
	 * @return gli ordini da preparare alla spedizione
	 * @throws SQLException 
	 * **/
	public ArrayList<ProxyOrdine> visualizzaOrdiniDaEvadere(int page, int perPage) throws SQLException;
	
	/**
	 * Il metodo esprime il servizio di commissione (o creazione) di un ordine
	 * fatto dal cliente verso il negozio online.
	 * @param cart : il carrello del cliente
	 * @param ordine : l'ordine contenente i prodotti del carrello
	 * 					da acquistare
	 * @param user : l'utente che intende acquistare
	 * @return il carrello svuotato
	 * 
	 * @throws SQLException 
	 * @throws ProdottoNulloException 
	 * @throws CarrelloVuotoException 
	 * @throws ProdottoNonPresenteException 
	 * **/
	
	public Carrello commissionaOrdine(Carrello cart, Ordine ordine, ProxyUtente user) throws SQLException, ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException;
	
	/**
	 * Il metodo esprime il servizio di preparazione di un ordine, commissionato
	 * da un cliente verso il negozio online, alla spedizione.
	 * @param order : l'ordine da evadere
	 * @param report : il report di spedizione
	 * 
	 * **/
	public void preparazioneSpedizioneOrdine(Ordine order, ReportSpedizione report);
}
