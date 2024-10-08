package application.PagamentoService;

import java.time.LocalDate;

import application.GestioneOrdiniService.Ordine;

/**
 * Classe concreta che esprime il concetto 'pagamento con carta di credito'.
 * Essa eredita le informazioni salienti del pagamento di un ordine online
 * effettuato da un utente dell'e-commerce (non è da considerarsi come una
 * fattura fiscale) contenute in 'Pagamento': codice, ordine pagato, importo, data ed ora del pagamento.
 * Inoltre, contiene informazioni aggiuntive quali nome del titolare
 * della carta e numero della carta.
 * 
 * @see application.PagamentoService.Pagamento
 * 
 * @author Dorotea Serrelli
 * */

public class PagamentoCartaCredito extends Pagamento implements Cloneable{

	/**
	 * titolare : il titolare della carta di credito
	 * */

	private String titolare;

	/**
	 * numeroCarta: il numero della carta di credito utilizzata per il
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
	 * Il metodo verifica se le informazioni della carta di credito fornite 
	 * dal cliente sono espresse nel formato corretto.
	 * Tale metodo, pertanto, verrà utilizzato in fase di pagamento
	 * dei prodotti durante l'acquisto online. 
	 * 
	 * @param titolare : il proprietario della carta di credito
	 * @param numeroCarta : il numero della carta di credito
	 * @param dataScadenza : la data di scadenza della carta di credito
	 * @param CVV : il codice CVV posto nel retro della carta di credito
	 * 
	 * @return true se i dati inseriti sono stati specificati nel 
	 * formato corretto; false altrimenti.
	 * */
	public static boolean checkValidate(String titolare, String numeroCarta, LocalDate dataScadenza, int CVV) {

		String titolarePattern = "^[A-Za-z\s]+$";
		String numeroCartaPattern = "^\\d{16}$";
		String cvvPattern = "^\\d{3}$";

		if(!titolare.matches(titolarePattern) || !numeroCarta.matches(numeroCartaPattern) || dataScadenza.isAfter(LocalDate.now())
				|| !Integer.toString(CVV).matches(cvvPattern))
			return false;

		return true;
	}

	/**
	 * Costruttore della classe con parametri: codice di pagamento, l'ordine
	 * pagato e l'importo versato, nominativo del titolare della carta e numero della carta.
	 * 
	 * @param codicePagamento : il codice identificativo del pagamento effettuato
	 * @param ordine : l'ordine da pagare
	 * @param importo : il costo dell'operazione (costo dei prodotti ordinati + spese di spedizione)
	 * @param user : il titolare della carta di credito
	 * @param numCard : il numero della carta di credito di 16 cifre
	 * 
	 * */


	public PagamentoCartaCredito(int codicePagamento, Ordine ordine, float importo, String user, String numCard) {
		super(codicePagamento, ordine, importo);
		this.titolare = user;
		this.numeroCarta = numCard;
	}

	/**
	 * Il metodo fornisce il nome del titolare della carta
	 * di credito.
	 * 
	 * @return titolare : il proprietario della carta di credito
	 * */

	public String getTitolare() {
		return titolare;
	}

	/**
	 * Il metodo imposta il nome del titolare della carta
	 * di credito.
	 * 
	 * @param titolare : proprietario della carta di credito
	 * */

	public void setTitolare(String titolare) {
		this.titolare = titolare;
	}

	/**
	 * Il metodo fornisce il numero della carta
	 * di credito.
	 * 
	 * @return numeroCarta : numero carta di credito
	 * */

	public String getNumeroCarta() {
		return numeroCarta;
	}

	/**
	 * Il metodo imposta il numero della carta
	 * di credito.
	 * 
	 * @param numCarta : numero della carta di credito
	 * */

	public void setNumeroCarta(String numCarta) {
		this.numeroCarta = numCarta;
	}

	/**
	 * Il metodo fornisce, in formato stringa, le informazioni legate ad
	 * un'operazione di pagamento con carta di credito: codice, data, ora, importo, ordine, 
	 * proprietario dell'ordine, titolare della carta e numero della carta.
	 * 
	 * @return un oggetto della classe String contenente le informazioni circa un'operazione
	 * 			di pagamento con carta di credito
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
	 * @return clone : una copia profonda dell'oggetto PagamentoCartaCredito.
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