package application.GestioneCarrelloService;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione di una
 * operazione sul carrello, a seguito della violazione del contratto della
 * classe Carrello.
 * 
 * @see java.application.GestioneCarrelloService.Carrello
 * 
 * @author Dorotea Serrelli
 * */

public class CarrelloException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore di classe di default.
	 * */

	public CarrelloException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public CarrelloException(String message) {
		super(message);
	}


	/**
	 * Classe statica per gestire il riferimento null ad un oggetto ItemCarrello.
	 * */
	public static class ProdottoNulloException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ProdottoNulloException(String message) {
			super(message);
		}
	}

	/**
	 * Classe statica per gestire la presenza di un prodotto nel carrello.
	 * */
	public static class ProdottoPresenteException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */

		public ProdottoPresenteException(String message) {
			super(message);
		}
	}

	/**
	 * Classe statica per gestire la mancanza di un prodotto nel carrello.
	 * */
	public static class ProdottoNonPresenteException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */

		public ProdottoNonPresenteException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la specifica di una quantità errata di un prodotto nel carrello.
	 * */
	public static class QuantitaProdottoException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */

		public QuantitaProdottoException(String message) {
			super(message);
		}
	}

	/**
	 * Classe statica per gestire un carrello vuoto.
	 * */
	public static class CarrelloVuotoException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */

		public CarrelloVuotoException(String message) {
			super(message);
		}
	}
}