package application.PagamentoService;

import application.GestioneOrdiniService.Ordine;

/**
 * Classe concreta che esprime il concetto 'pagamento con carta di credito'.
 * Essa ereditata le informazioni contenute in 'Pagamento': salienti del pagamento di un ordine online
 * effettuato da un utente dell'e-commerce (non è da considerarsi come una
 * fattura fiscale): codice, ordine pagato, importo, data ed ora del pagamento.
 * Inoltre, contiene informazioni aggiuntive quali nome del titolare
 * della carta e numero della carta.
 * 
 * 
 * @see java.application.PagamentoService.Pagamento
 * 
 * @author Dorotea Serrelli
 * */

public class PagamentoCartaCredito extends Pagamento{
	
	/**
	 * Il titolare della carta di credito
	 * */
	
	private String titolare;
	
	/**
	 * Il numero della carta di credito utilizzata per il
	 * pagamento dell'ordine
	 * */
	
	private String numeroCarta;
	
	/**
	 * Costruttore di default di classe.
	 * */
	
	public PagamentoCartaCredito() {
		super();
	}
	
	/**
	 * Costruttore della classe con parametri: codice di pagamento, l'ordine
	 * pagato e l'importo versato.
	 * @param codicePagamento : il codice identificativo del pagamento effettuato
	 * @param ordine : l'ordine che è stato pagato
	 * @param importo : il costo dell'operazione (costo dei prodotti ordinati + spese di spedizione)
	 * @param user : il titolare della carta di credito
	 * @param numCard : il numero della carta di credito di 16 cifre
	 * 
	 * @return l'oggetto PagamentoCartaCredito contenente le informazioni codicePagamento, ordine, importo, 
	 * 			data corrente, ora corrente, titolare user e numero di carta numCard.
	 * */
	

	public PagamentoCartaCredito(int codicePagamento, Ordine ordine, float importo, String user, String numCard) {
		super(codicePagamento, ordine, importo);
	}
	
	/**
	 * Il metodo fornisce il nome del titolare della carta
	 * di credito.
	 * @return titolare carta
	 * */
	public String getTitolare() {
		return titolare;
	}
	
	/**
	 * Il metodo imposta il nome del titolare della carta
	 * di credito.
	 * @param titolare : proprietario della carta di credito
	 * */
	public void setTitolare(String titolare) {
		this.titolare = titolare;
	}
	
	/**
	 * Il metodo fornisce il numero della carta
	 * di credito.
	 * @return numero carta di credito
	 * */
	public String getNumeroCarta() {
		return numeroCarta;
	}
	
	/**
	 * Il metodo imposta il numero della carta
	 * di credito.
	 * @param numCarta : numero carta di credito
	 * */
	public void setNumeroCarta(String numCarta) {
		this.numeroCarta = numCarta;
	}

	/**
	 * Il metodo fornisce, in formato stringa, le informazioni legate ad
	 * un'operazione di pagamento con carta di credito: codice, data, ora, importo, ordine, 
	 * proprietario dell'ordine, titolare della carta e numero della carta.
	 * */
	@Override
	public String toString() {
		return "PagamentoCartaCredito \n[titolare=" + titolare + ", numeroCarta=" + numeroCarta
				+ ", Codice Pagamento=" + getCodicePagamento() + ", Codice ordine =" + getOrdine().getCodiceOrdine()
				+ ", Data Pagamento =" + getDataPagamento() + ", Ora Pagamento=" + getOraPagamento()
				+ ", Importo totale (prodotti + spedizione)=" + getImporto() + "]";
	}
	
	/**
	 * Il metodo crea una copia profonda dell'oggetto PagamentoCartaCredito.
	 *
	 * @return Una copia profonda dell'oggetto PagamentoCartaCredito.
	 * 
	 */
	
	@Override
	public PagamentoCartaCredito clone() throws CloneNotSupportedException{
	    PagamentoCartaCredito clone = null;
	    clone = (PagamentoCartaCredito) super.clone();
	    
		// Copia degli attributi specifici di PagamentoCartaCredito
		clone.titolare = this.titolare;
		clone.numeroCarta = this.numeroCarta;
	    return clone;
	}

}
