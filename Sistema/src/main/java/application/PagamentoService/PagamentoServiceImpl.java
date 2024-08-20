package application.PagamentoService;

import java.sql.SQLException;

import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import storage.GestioneOrdiniDAO.*;

/**
 * La classe implementa i servizi per la creazione e il recupero di oggetti di tipo Pagamento.
 * Fornisce metodi statici per creare oggetti Pagamento in base a diversi criteri
 * come il codice dell'ordine e il codice del pagamento.
 * 
 * @author Dorotea Serrelli
 * */

public class PagamentoServiceImpl implements PagamentoService{

	/**
	 * Il metodo crea un oggetto Pagamento da salvare: 
	 * clona l'oggetto Pagamento passato come parametro e lo salva nel database
	 * in base al suo tipo concreto.
	 * 
	 * @param payment l'oggetto Pagamento da salvare
	 * @return l'oggetto Pagamento salvato
	 * 
	 * @throws OrdineVuotoException se l'ordine associato al pagamento è vuoto
	 * @throws SQLException se si verifica un errore di accesso al database
	 * @throws ModalitaAssenteException se la modalità di pagamento non è supportata
	 * @throws CloneNotSupportedException 
	 */
	public  <T extends Pagamento> Pagamento effettuaPagamento(T payment) throws OrdineVuotoException, SQLException, ModalitaAssenteException, CloneNotSupportedException {
		PagamentoDAODataSource dao = new PagamentoDAODataSource();

		if (payment instanceof PagamentoContrassegno) {
			PagamentoContrassegno pagamentoContrassegno = (PagamentoContrassegno) payment.clone();
			dao.doSaveCash(pagamentoContrassegno);
			return pagamentoContrassegno;
		}

		if (payment instanceof PagamentoPaypal) {
			PagamentoPaypal pagamentoPaypal = (PagamentoPaypal) payment.clone();
			dao.doSavePaypal(pagamentoPaypal);
			return pagamentoPaypal;
		}

		if (payment instanceof PagamentoCartaCredito) {
			PagamentoCartaCredito pagamentoCarta = (PagamentoCartaCredito) payment.clone();
			dao.doSaveCard(pagamentoCarta);
			return pagamentoCarta;
		}

		throw new ModalitaAssenteException("Modalità di pagamento non ammessa. È possibile pagare l'ordine: \n- in contrassegno;"
				+ "\n- con Paypal; \n- con carta di credito.");
	}

	/**
	 * Il metodo crea un oggetto Pagamento in base all'ID dell'ordine associato.
	 * 
	 * Recupera il pagamento dall'archivio in base al tipo di pagamento e restituisce
	 * l'oggetto Pagamento corrispondente.
	 * 
	 * @param IDOrdine l'ID dell'ordine associato al pagamento
	 * @return l'oggetto Pagamento creato
	 * 
	 * @throws OrdineVuotoException se l'ordine non esiste
	 * @throws SQLException se si verifica un errore di accesso al database
	 * @throws ModalitaAssenteException se non esiste una modalità di pagamento associata all'ordine
	 * @throws CategoriaProdottoException 
	 */

	public static Pagamento createPagamentoOrdine(int IDOrdine) throws OrdineVuotoException, SQLException, ModalitaAssenteException, CategoriaProdottoException {
		PagamentoDAODataSource dao = new PagamentoDAODataSource();

		PagamentoContrassegno pagamentoContrassegno = dao.doRetrieveCashByOrder(IDOrdine);
		if (pagamentoContrassegno != null) {
			return pagamentoContrassegno;
		}

		PagamentoPaypal pagamentoPaypal = dao.doRetrievePaypalByOrder(IDOrdine);
		if (pagamentoPaypal != null) {
			return pagamentoPaypal;
		}

		PagamentoCartaCredito pagamentoCartaCredito = dao.doRetrieveCardByOrder(IDOrdine);
		if (pagamentoCartaCredito != null) {
			return pagamentoCartaCredito;
		}

		throw new ModalitaAssenteException("Modalita\' di pagamento non ammessa. E\' possibile pagare l'ordine: \n- in contrassegno;"
				+ "\n- con Paypal; \n- con carta di credito.");
	}

	/**
	 * Il metodo crea un oggetto Pagamento in base all'ID del pagamento: 
	 * recupera il pagamento dall'archivio in base all'ID e restituisce
	 * l'oggetto Pagamento corrispondente.
	 * 
	 * @param IDPayment l'ID del pagamento
	 * @return l'oggetto Pagamento creato
	 * 
	 * @throws OrdineVuotoException se l'ordine associato al pagamento non esiste
	 * @throws SQLException se si verifica un errore di accesso al database
	 * @throws ModalitaAssenteException se non esiste una modalità di pagamento associata al pagamento
	 * @throws CategoriaProdottoException 
	 */

	public static Pagamento createPagamento(int IDPayment) throws OrdineVuotoException, SQLException, ModalitaAssenteException, CategoriaProdottoException {
		PagamentoDAODataSource dao = new PagamentoDAODataSource();

		PagamentoContrassegno pagamentoContrassegno = dao.doRetrieveCashByKey(IDPayment);
		if (pagamentoContrassegno != null) {
			return pagamentoContrassegno;
		}

		PagamentoPaypal pagamentoPaypal = dao.doRetrievePaypalByKey(IDPayment);
		if (pagamentoPaypal != null) {
			return pagamentoPaypal;
		}

		PagamentoCartaCredito pagamentoCartaCredito = dao.doRetrieveCardByKey(IDPayment);
		if (pagamentoCartaCredito != null) {
			return pagamentoCartaCredito;
		}

		throw new ModalitaAssenteException("Modalita\' di pagamento non ammessa. E\' possibile pagare l'ordine: \n- in contrassegno;"
				+ "\n- con Paypal; \n- con carta di credito.");
	}
}
