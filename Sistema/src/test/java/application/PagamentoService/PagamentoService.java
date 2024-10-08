package application.PagamentoService;

import java.sql.SQLException;

import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;

/**
 * Questa interfaccia definisce i servizi di pagamento per gli ordini online,
 * gestendo diverse modalità di pagamento.
 * 
 * @see application.PagamentoService.PagamentoServiceImpl
 * @see application.PagamentoService.Pagamento
 * @see application.PagamentoService.PagamentoException
 * 
 * @author Dorotea Serrelli
 * */

public interface PagamentoService {
	
	/**
	 * Questo metodo fornisce il servizio di pagamento online
	 * di un ordine.
	 * 
	 * @param payment : le informazioni salienti di un pagamento
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
	 *
	 * */
	
	public <T extends Pagamento> Pagamento effettuaPagamento(T payment) throws OrdineVuotoException, SQLException, ModalitaAssenteException, CloneNotSupportedException;
	
}
