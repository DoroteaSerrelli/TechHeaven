package application.PagamentoService;

import java.time.LocalDate;
import java.time.LocalTime;

import application.GestioneOrdiniService.Ordine;

/**
 * Classe astratta che esprime il concetto 'pagamento'.
 * Essa contiene le informazioni salienti del pagamento di un ordine online
 * effettuato da un utente dell'e-commerce (non è da considerarsi come una
 * fattura fiscale): codice, ordine pagato, importo, data ed ora del pagamento.
 * Per i metodi di pagamento 'Contrassegno', 'Paypal', 'Carta di credito' verranno 
 * create delle apposite classi concrete di Pagamento.
 * 
 * @see java.application.PagamentoService
 * 
 * @author Dorotea Serrelli
 * */

public abstract class Pagamento {
	
	/**
	 * Il codice univoco per identificare un'operazione 
	 * di pagamento online
	 * */
	private int codicePagamento;
	
	/**
	 * L'ordine a cui è associato il pagamento effettuato dall'utente,
	 * proprietario dell'ordine.
	 * 
	 * @see application.GestioneOrdiniService.Ordine
	 * */
	private Ordine ordine;
	
	/**
	 * La data in cui è stata effettuata l'operazione di pagamento
	 * */
	private LocalDate dataPagamento;
	
	/**
	 * L'ora in cui è stata effettuata l'operazione di pagamento
	 * */
	private LocalTime oraPagamento;
	
	/**
	 * L'importo pagato (costo dei prodotti e spese di spedizione)
	 * */
	private float importo;
	
	/**
	 * Costruttore di default della classe.
	 * */
	protected Pagamento() {
		this.codicePagamento = -1;
		this.ordine = null;
		this.importo = 0;
		this.dataPagamento = null;
		this.oraPagamento = null;
	}
	
	/**
	 * Costruttore della classe con parametri: codice di pagamento, l'ordine
	 * pagato e l'importo versato.
	 * @param codicePagamento : il codice identificativo del pagamento effettuato
	 * @param ordine : l'ordine che è stato pagato
	 * @param importo : il costo dell'operazione (costo dei prodotti ordinati + spese di spedizione)
	 * 
	 * @return l'oggetto Pagamento contenente le informazioni codicePagamento, ordine, importo, 
	 * 			data corrente, ora corrente.
	 * */
	
	protected Pagamento(int codicePagamento, Ordine ordine, float importo) {
		this.codicePagamento = codicePagamento;
		this.ordine = ordine;
		this.importo = importo;
		this.dataPagamento = LocalDate.now();
		this.oraPagamento = LocalTime.now();
	}
	
	/**
	 * Il metodo fornisce il codice di pagamento dell'operazione.
	 * @return identificativo del pagamento
	 * */
	public int getCodicePagamento() {
		return codicePagamento;
	}
	
	/**
	 * Il metodo imposta il codice di pagamento dell'operazione.
	 * @param codicePagamento : identificativo del pagamento
	 * */
	public void setCodicePagamento(int codicePagamento) {
		this.codicePagamento = codicePagamento;
	}
	
	/**
	 * Il metodo fornisce l'ordine per cui è stato effettuato il pagamento.
	 * @return l'ordine pagato
	 * */
	public Ordine getOrdine() {
		return ordine;
	}
	
	/**
	 * Il metodo imposta l'ordine che è stato pagato.
	 * @param ordine : l'ordine pagato
	 * */
	public void setOrdine(Ordine ordine) {
		this.ordine = ordine;
	}
	
	/**
	 * Il metodo fornisce la data in cui si è effettuata 
	 * l'operazione di pagamento.
	 * @return data del pagamento
	 * */
	public LocalDate getDataPagamento() {
		return dataPagamento;
	}
	
	/**
	 * Il metodo imposta la data in cui si è effettuata 
	 * l'operazione di pagamento.
	 * @param dataPagamento : data del pagamento
	 * */
	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	
	/**
	 * Il metodo fornisce l'ora in cui si è effettuata 
	 * l'operazione di pagamento.
	 * @return ora del pagamento
	 * */
	public LocalTime getOraPagamento() {
		return oraPagamento;
	}
	
	/**
	 * Il metodo imposta l'ora in cui si è effettuata 
	 * l'operazione di pagamento.
	 * @param oraPagamento : ora del pagamento
	 * */
	public void setOraPagamento(LocalTime oraPagamento) {
		this.oraPagamento = oraPagamento;
	}
	
	/**
	 * Il metodo fornisce l'importo pagato.
	 * @return costo del pagamento
	 * */
	public float getImporto() {
		return importo;
	}
	
	/**
	 * Il metodo imposta l'importo pagato.
	 * @param importo : costo del pagamento
	 * */
	public void setImporto(float importo) {
		this.importo = importo;
	}
	
	/**
	 * Il metodo fornisce, in formato stringa, le informazioni legate ad
	 * un'operazione di pagamento: codice, data, ora, importo, ordine e 
	 * proprietario dell'ordine.
	 * */
	@Override
	public String toString() {
		return "Pagamento [codicePagamento=" + codicePagamento + ", ordine n.=" + ordine.getCodiceOrdine() + " effettuato da : " + ordine.getAcquirente().toString()+ "\n dataPagamento="
				+ dataPagamento + ", oraPagamento=" + oraPagamento + ", importo=" + importo + "]";
	}
	
}