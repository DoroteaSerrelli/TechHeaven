package application.PagamentoService;

import application.GestioneOrdiniService.Ordine;

/**
 * Classe concreta che esprime il concetto 'pagamento in contrassegno'.
 * Essa ereditata le informazioni contenute in 'Pagamento': salienti del pagamento di un ordine online
 * effettuato da un utente dell'e-commerce (non è da considerarsi come una
 * fattura fiscale): codice, ordine pagato, importo, data ed ora del pagamento.
 * 
 * 
 * @see java.application.PagamentoService.Pagamento
 * 
 * @author Dorotea Serrelli
 * */

public class PagamentoContrassegno extends Pagamento implements Cloneable{

	/**
	 * Costruttore di default di classe.
	 * */
	
	public PagamentoContrassegno() {
		super();
	}
	
	/**
	 * Costruttore della classe con parametri: codice di pagamento, l'ordine
	 * pagato e l'importo versato.
	 * @param codicePagamento : il codice identificativo del pagamento effettuato
	 * @param ordine : l'ordine che è stato pagato
	 * @param importo : il costo dell'operazione (costo dei prodotti ordinati + spese di spedizione)
	 * 
	 * @return l'oggetto PagamentoContrassegno contenente le informazioni codicePagamento, ordine, importo, 
	 * 			data corrente, ora corrente.
	 * */
	
	public PagamentoContrassegno(int codicePagamento, Ordine ordine, float importo) {
		super(codicePagamento, ordine, importo);
	}
	
	/**
	 * Crea una copia profonda dell'oggetto PagamentoContrassegno.
	 * 
	 * Dato che questa classe non introduce nuovi attributi rispetto alla classe base,
	 * la clonazione viene delegata alla classe padre.
	 *
	 * @return Una copia profonda dell'oggetto PagamentoContrassegno.
	 * @throws RuntimeException se si verifica un errore durante la clonazione.
	 */
	@Override
	public PagamentoContrassegno clone() throws CloneNotSupportedException{
	    return (PagamentoContrassegno) super.clone();
	}

}