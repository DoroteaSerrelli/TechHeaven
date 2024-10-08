package application.PagamentoService;

import application.GestioneOrdiniService.Ordine;

/**
 * Classe concreta che esprime il concetto 'pagamento online Paypal'.
 * Essa eredita le informazioni salienti del pagamento di un ordine online
 * effettuato da un utente dell'e-commerce (non è da considerarsi come una
 * fattura fiscale) contenute in 'Pagamento': codice, ordine pagato, importo, data ed ora del pagamento.
 * 
 * @see application.PagamentoService.Pagamento
 * 
 * @author Dorotea Serrelli
 * */

public class PagamentoPaypal extends Pagamento implements Cloneable{
	
	/**
	 * Costruttore di default di classe.
	 * */
	
	public PagamentoPaypal() {
		super();
	}
	
	/**
	 * Costruttore della classe con parametri: codice di pagamento, l'ordine
	 * pagato e l'importo versato.
	 * Si crea un oggetto PagamentoPaypal contenente le informazioni codicePagamento, 
	 * ordine, importo, data corrente, ora corrente.
	 * 
	 * @param codicePagamento : il codice identificativo del pagamento effettuato
	 * @param ordine : l'ordine che è stato pagato
	 * @param importo : il costo dell'operazione (costo dei prodotti ordinati + spese di spedizione)
	 * 
	 * */
	
	public PagamentoPaypal(int codicePagamento, Ordine ordine, float importo) {
		super(codicePagamento, ordine, importo);
	}
	
	/**
	 * Il metodo crea una copia profonda dell'oggetto PagamentoPaypal.
	 * 
	 * Dato che questa classe non introduce nuovi attributi rispetto alla classe base,
	 * la clonazione viene delegata alla classe padre.
	 *
	 * @return una copia profonda dell'oggetto PagamentoPaypal.
	 * @throws RuntimeException : eccezione lanciata in caso di errore durante la clonazione.
	 */
	
	@Override
	public PagamentoPaypal clone() throws CloneNotSupportedException{
	    return (PagamentoPaypal) super.clone();
	}

}
