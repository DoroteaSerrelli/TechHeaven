package application.PagamentoService;

import java.sql.SQLException;

import application.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import storage.GestioneOrdiniDAO.*;

/**
 * La classe implementa i servizi per la creazione e il recupero di oggetti di tipo Pagamento.
 * Fornisce metodi statici per creare oggetti Pagamento in base a diversi criteri
 * come il codice dell'ordine e il codice del pagamento.
 * 
 * @see application.PagamentoService.PagamentoService
 * @see application.PagamentoService.Pagamento
 * @see application.PagamentoService.PagamentoException
 * 
 * @author Dorotea Serrelli
 * */

public class PagamentoServiceImpl implements PagamentoService{
	
	private PagamentoDAODataSource paymentDAO;
	
	public PagamentoServiceImpl(PagamentoDAODataSource paymentDAO) {
		this.paymentDAO = paymentDAO;
	}
	
	/**
	 * Il metodo implementa il servizio di pagamento di un ordine di un
	 * cliente: 
	 * clona l'oggetto Pagamento passato come parametro e lo salva nel database
	 * in base al suo tipo concreto.
	 * 
	 * @param payment : il pagamento dell'ordine di un cliente da salvare
	 * 
	 * @return un oggetto Pagamento o sua sottoclasse contenente le informazioni
	 * 			importanti del pagamento dell'ordine
	 * 
	 * @throws OrdineVuotoException : eccezione che gestisce il caso in cui
	 * 								l'ordine associato al pagamento è vuoto
	 * @throws SQLException : eccezione che gestisce il caso in cui si verifica un errore di accesso al database
	 * @throws ModalitaAssenteException : eccezione che gestisce il caso in cui 
	 * 									la modalità di pagamento specificata non è supportata
	 * @throws CloneNotSupportedException 
	 */
	
	public  <T extends Pagamento> Pagamento effettuaPagamento(T payment) throws OrdineVuotoException, SQLException, ModalitaAssenteException, CloneNotSupportedException {
		
		if (payment instanceof PagamentoContrassegno) {
			
			PagamentoContrassegno pagamentoContrassegno = new PagamentoContrassegno();
			pagamentoContrassegno.setCodicePagamento(payment.getCodicePagamento());
			pagamentoContrassegno.setOrdine(payment.getOrdine());
			pagamentoContrassegno.setDataPagamento(payment.getDataPagamento());
			pagamentoContrassegno.setOraPagamento(payment.getOraPagamento());
			pagamentoContrassegno.setImporto(payment.getImporto());
			
			paymentDAO.doSaveCash(pagamentoContrassegno);
			return pagamentoContrassegno;
		}

		if (payment instanceof PagamentoPaypal) {
			
			PagamentoPaypal pagamentoPaypal = new PagamentoPaypal();
			pagamentoPaypal.setCodicePagamento(payment.getCodicePagamento());
			pagamentoPaypal.setOrdine(payment.getOrdine());
			pagamentoPaypal.setDataPagamento(payment.getDataPagamento());
			pagamentoPaypal.setOraPagamento(payment.getOraPagamento());
			pagamentoPaypal.setImporto(payment.getImporto());
			paymentDAO.doSavePaypal(pagamentoPaypal);
			
			
			return pagamentoPaypal;
		}

		if (payment instanceof PagamentoCartaCredito) {
			PagamentoCartaCredito pagamentoCartaOther = (PagamentoCartaCredito) payment.clone();
			PagamentoCartaCredito pagamentoCarta = new PagamentoCartaCredito();
			pagamentoCarta.setCodicePagamento(pagamentoCartaOther.getCodicePagamento());
			pagamentoCarta.setOrdine(pagamentoCartaOther.getOrdine());
			pagamentoCarta.setDataPagamento(pagamentoCartaOther.getDataPagamento());
			pagamentoCarta.setOraPagamento(pagamentoCartaOther.getOraPagamento());
			pagamentoCarta.setImporto(pagamentoCartaOther.getImporto());
			pagamentoCarta.setTitolare(pagamentoCartaOther.getTitolare());
			pagamentoCarta.setNumeroCarta(pagamentoCartaOther.getNumeroCarta());
			
			paymentDAO.doSaveCard(pagamentoCarta);
			
			return pagamentoCarta;
		}

		throw new ModalitaAssenteException("Modalità di pagamento non ammessa. È possibile pagare l'ordine: \n- in contrassegno;"
				+ "\n- con Paypal; \n- con carta di credito.");
	}

	/**
	 * Il metodo crea un oggetto Pagamento in base all'ID dell'ordine associato.
	 * 
	 * Recupera il pagamento dal database in base al tipo di pagamento e restituisce
	 * l'oggetto Pagamento corrispondente.
	 * 
	 * @param IDOrdine : l'ID dell'ordine associato al pagamento
	 * 
	 * @return l'oggetto Pagamento associato all'ordine con codice IDOrdine
	 * 
	 * 
	 * @throws OrdineVuotoException : eccezione che gestisce il caso in cui
	 * 								l'ordine associato al pagamento è vuoto
	 * @throws SQLException : eccezione che gestisce il caso in cui si verifica un errore di accesso al database
	 * @throws ModalitaAssenteException : eccezione che gestisce il caso in cui 
	 * 									la modalità di pagamento specificata non è supportata
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 ***/

	public static Pagamento createPagamentoOrdine(int IDOrdine, PagamentoDAODataSource paymentDAO) throws OrdineVuotoException, SQLException, ModalitaAssenteException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		
		PagamentoContrassegno pagamentoContrassegno = paymentDAO.doRetrieveCashByOrder(IDOrdine);
		if (pagamentoContrassegno != null) {
			return pagamentoContrassegno;
		}

		PagamentoPaypal pagamentoPaypal = paymentDAO.doRetrievePaypalByOrder(IDOrdine);
		if (pagamentoPaypal != null) {
			return pagamentoPaypal;
		}

		PagamentoCartaCredito pagamentoCartaCredito = paymentDAO.doRetrieveCardByOrder(IDOrdine);
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
	 * @param IDPayment: l'ID del pagamento
	 * @return l'oggetto Pagamento avente codice IDPayment
	 * 
	 * 
	 * @throws OrdineVuotoException : eccezione che gestisce il caso in cui
	 * 								l'ordine associato al pagamento è vuoto
	 * @throws SQLException : eccezione che gestisce il caso in cui si verifica un errore di accesso al database
	 * @throws ModalitaAssenteException : eccezione che gestisce il caso in cui 
	 * 									la modalità di pagamento specificata non è supportata
	 * @throws CategoriaProdottoException 
	 * @throws ErroreTipoSpedizioneException 
	 */

	public static Pagamento createPagamento(int IDPayment, PagamentoDAODataSource dao) throws OrdineVuotoException, SQLException, ModalitaAssenteException, CategoriaProdottoException, ErroreTipoSpedizioneException {
		
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
