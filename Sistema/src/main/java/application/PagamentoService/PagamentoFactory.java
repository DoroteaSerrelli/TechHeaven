package application.PagamentoService;

import java.sql.SQLException;

import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import application.PagamentoService.PagamentoException.*;


public class PagamentoFactory {
	public static Pagamento createPagamentoOrdine(int IDOrdine) throws OrdineVuotoException, SQLException, ModalitaAssenteException {
		PagamentoDAODataSource dao = new PagamentoDAODataSource();

		// Retrieve based on concrete class and return the instance
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
	
	public static Pagamento createPagamento(int IDPayment) throws OrdineVuotoException, SQLException, ModalitaAssenteException {
		PagamentoDAODataSource dao = new PagamentoDAODataSource();

		// Retrieve based on concrete class and return the instance
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