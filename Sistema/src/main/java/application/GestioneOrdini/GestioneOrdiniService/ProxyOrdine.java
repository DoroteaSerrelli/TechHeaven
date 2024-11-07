package application.GestioneOrdini.GestioneOrdiniService;

import java.sql.SQLException;

import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Registrazione.RegistrazioneService.Indirizzo;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;

/**
 * La classe permette di controllare l'accesso ad un oggetto della classe Ordine, 
 * agendo come surrogato o interfaccia di sostituzione a questo oggetto nel design pattern Proxy. 
 * 
 * ProxyOrdine agisce per conto di Ordine, memorizzando un sottoinsieme degli attributi di Ordine 
 * (quelli offerti da ObjectOrdine) e gestisce completamente le richieste che non richiedono la conoscenza delle
 * informazioni relative all'acquirente ed ai prodotti associati all'ordine.
 * 
 * ProxyOrdine ha un riferimento ad un oggetto Ordine, in modo che tutte le richieste legate alla manipolazione 
 * dei dati personali dell'acquirente (nome, cognome, numero di telefono, ...) e i prodotti
 * acquistati (nome, quantità, ...) vengono delegate a Ordine.
 * Dopo la delega, viene creato l'oggetto Ordine e caricato in memoria.
 * 
 * @see application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine
 * @see application.GestioneOrdini.GestioneOrdiniService.Ordine
 * 
 * @author Dorotea Serrelli
 * */

public class ProxyOrdine extends ObjectOrdine implements Cloneable{
	
	private OrdineDAODataSource orderDAO;
	
	/**
	 * ordinativo : riferimento all'ordine effettivo, presente nel negozio online.
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
	 * Costruttore di classe.
	 * */
	public ProxyOrdine(OrdineDAODataSource orderDAO) {
		super();
		ordinativo = null;
		this.orderDAO = orderDAO;
	}
	
	/**
	 * Costruttore di classe per creare un oggetto ProxyOrdine noti codice, stato di elaborazione,
	 * indirizzo di spedizione, tipologia di spedizione.
	 * 
	 * @param codice : l'identificativo dell'ordine
	 * @param stato : stato di elaborazione dell'ordine 
	 * @param indirizzoSpedizione : l'indirizzo di spedizione presso cui recapitare l'ordine
	 * @param spedizione : la tipologia di spedizione scelta dall'utente
	 * 
	 * */
	
	public ProxyOrdine(int codice, Stato stato, Indirizzo indirizzoSpedizione, TipoSpedizione spedizione, TipoConsegna consegna, OrdineDAODataSource orderDAO) {
        super(codice, stato, indirizzoSpedizione, spedizione, consegna);
        ordinativo = null;
        this.orderDAO = orderDAO;
    }
	
	public void setDAO(OrdineDAODataSource orderDAO) {
		this.orderDAO = orderDAO;
	}
		
	/**
	 * Il metodo fornisce il riferimento all'oggetto Ordine.
	 * Se non è presente questo riferimento, allora si crea tale oggetto e se ne mantiene in memoria
	 * il riferimento.
	 * 
	 * @return ordinativo : l'oggetto Ordine che possiede le informazioni sull'ordine come utente e prodotti
	 * 
	 * @throws OrdineVuotoException 
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 * */
	
	public Ordine mostraOrdine() throws OrdineVuotoException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		if(ordinativo == null) {
			try {
				Ordine real = orderDAO.doRetrieveFullOrderByKey(this.getCodiceOrdine());
				ordinativo = real;
			} catch (SQLException e) {
				System.out.println("Errore nel recupero dell'ordine\n");
			}
		}
		return ordinativo;
	}
	
	/**
	 * Il metodo crea una copia profonda dell'oggetto ProxyOrdine.
	 *
	 * @return clone : una copia profonda dell'oggetto ProxyOrdine.
	 * 
	 * @throws CloneNotSupportedException
	 */
	
	@Override
	public ProxyOrdine clone() throws CloneNotSupportedException{
	    ProxyOrdine clone = null;
	    clone = (ProxyOrdine) super.clone();
		if (ordinativo != null) {
		    clone.ordinativo = ordinativo.clone();
		}
	    return clone;
	}

}
