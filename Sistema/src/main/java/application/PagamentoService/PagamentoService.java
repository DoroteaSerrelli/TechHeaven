package application.PagamentoService;

import java.sql.SQLException;

import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;

/**
 * Questa interfaccia definisce i servizi di pagamento per gli ordini online,
 * gestendo diverse modalità di pagamento.
 * 
 * @author Dorotea Serrelli
 * */

public interface PagamentoService {
	
	/**
	 * Questo metodo fornisce il servizio di pagamento online
	 * di un ordine.
	 * 
	 * @param payment : le informazioni salienti di un pagamento
	 * @return un oggetto Pagamento o sua sottoclasse contenente le informazioni
	 * 			importanti del pagamento dell'ordine
	 * */
	
	public <T extends Pagamento> Pagamento effettuaPagamento(T payment) throws OrdineVuotoException, SQLException, ModalitaAssenteException, CloneNotSupportedException;
	
}
