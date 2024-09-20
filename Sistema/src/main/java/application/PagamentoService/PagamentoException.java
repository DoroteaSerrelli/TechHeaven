package application.PagamentoService;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione di una
 * operazione di pagamento su un ordine, a seguito della violazione del contratto della
 * classe Pagamento.
 * 
 * @see application.PagamentoService.Pagamento
 * 
 * @author Dorotea Serrelli
 * */

public class PagamentoException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore di classe di default.
	 * */

	public PagamentoException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public PagamentoException(String message) {
		super(message);
	}


	/**
	 * Classe statica per gestire una modalità di pagamento 
	 * non consentita nell'e-commerce.
	 * */
	public static class ModalitaAssenteException extends PagamentoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ModalitaAssenteException(String message) {
			super(message);
		}
	}
}
