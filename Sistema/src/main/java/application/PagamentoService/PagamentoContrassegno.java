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

public class PagamentoContrassegno extends Pagamento {

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
}
