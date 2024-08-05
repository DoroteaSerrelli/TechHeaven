package application.GestioneOrdiniService;

import java.sql.SQLException;

import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.RegistrazioneService.Indirizzo;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;

/**
 * La classe permette di controllare l'accesso ad un oggetto della classe Ordine, 
 * agendo come surrogato o interfaccia di sostituzione a questo oggetto nel design pattern Proxy. 
 * ProxyOrdine agisce per conto di Ordine, memorizzando un sottoinsieme degli attributi di Ordine 
 * (quelli offerti da ObjectOrdine) e gestisce completamente le richieste che non richiedono la conoscenza delle
 * informazioni relative all'acquirente ed ai prodotti associati all'ordine.
 * ProxyOrdine ha un riferimento ad un oggetto Ordine, in modo che tutte le richieste legate alla manipolazione 
 * dei dati personali dell'acquirente (nome, cognome, numero di telefono, ...) e i prodotti
 * acquistati (nome, quantità, ...) vengono delegate a Ordine.
 * Dopo la delega, viene creato l'oggetto Ordine e caricato in memoria.
 * 
 * @see application.GestioneOrdiniService.ObjectOrdine
 * @see application.GestioneOrdiniService.Ordine
 * 
 * @author Dorotea Serrelli
 * */

public class ProxyOrdine extends ObjectOrdine{
	
	/**
	 * ordine : riferimento all'ordine effettivo commissionato al negozio online.
	 * */
	private Ordine ordinativo;
	
	
	/**
	 * Costruttore di classe di default.
	 * */
	public ProxyOrdine() {
		super();
		ordinativo = null;
	}
	
	/**
	 * Costruttore di classe per creare un oggetto ProxyOrdine noti codice, stato di elaborazione,
	 * indirizzo di spedizione, tipologia di spedizione.
	 * @param codice : l'identificativo dell'ordine
	 * @param stato : stato di elaborazione dell'ordine 
	 * @param indirizzoSpedizione : l'indirizzo di spedizione presso cui recapitare l'ordine
	 * @param spedizione : la tipologia di spedizione scelta dall'utente
	 * */
	public ProxyOrdine(int codice, Stato stato, Indirizzo indirizzoSpedizione, TipoSpedizione spedizione) {
        super(codice, stato, indirizzoSpedizione, spedizione);
        ordinativo = null;
    }
		
	/**
	 * Il metodo fornisce il riferimento all'oggetto Utente.
	 * Se non è presente questo riferimento, allora si crea tale oggetto e se ne mantiene in memoria
	 * il riferimento.
	 * @return l'oggetto Utente che possiede le informazioni personali dell'utente
	 * @throws OrdineVuotoException 
	 * */
	public Ordine mostraOrdine() throws OrdineVuotoException {
		if(ordinativo == null) {
			OrdineDAODataSource orderDao = new OrdineDAODataSource();
			try {
				Ordine real = orderDao.doRetrieveFullOrderByKey(this.getCodiceOrdine());
				ordinativo = real;
			} catch (SQLException e) {
				System.out.println("Errore nel recupero dell'ordine\n");
			}
		}
		return ordinativo;
	}
}
