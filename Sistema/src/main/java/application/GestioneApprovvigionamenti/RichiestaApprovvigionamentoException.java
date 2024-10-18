package application.GestioneApprovvigionamenti;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione di una
 * operazione su una richiesta di approvvigionamento, a seguito della violazione del contratto della
 * classe RichiestaApprovvigionamento.
 * 
 * @see application.GestioneApprovvigionamenti.RichiestaApprovvigionamento
 * 
 * @author Dorotea Serrelli
 * */

public class RichiestaApprovvigionamentoException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore di classe di default.
	 * */

	public RichiestaApprovvigionamentoException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public RichiestaApprovvigionamentoException(String message) {
		super(message);
	}


	/**
	 * Classe statica per gestire errori nel formato di nominativo o email 
	 * del fornitore.
	 * */
	public static class FormatoFornitoreException extends RichiestaApprovvigionamentoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoFornitoreException(String message) {
			super(message);
		}
	}

	/**
	 * Classe statica per gestire quantità del prodotto non corretta 
	 * (uguale a 0 o negativa)
	 * 
	 * */
	public static class QuantitaProdottoException extends RichiestaApprovvigionamentoException {
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
	 * Classe statica per gestire una descrizione di dettaglio vuota.
	 * */
	public static class DescrizioneDettaglioException extends RichiestaApprovvigionamentoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public DescrizioneDettaglioException(String message) {
			super(message);
		}
	}
	
	
	/**
	 * Classe statica per gestire la richiesta di approvvigionamento di un prodotto
	 * che non ha numero di scorte in magazzino pari a 0.
	 * */
	public static class QuantitaProdottoDisponibileException extends RichiestaApprovvigionamentoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public QuantitaProdottoDisponibileException(String message) {
			super(message);
		}
	}
	
	
	/**
	 * Classe statica per gestire la richiesta di approvvigionamento di un prodotto
	 * non più in vendita nell'e-commerce.
	 * */
	public static class ProdottoVendibileException extends RichiestaApprovvigionamentoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ProdottoVendibileException(String message) {
			super(message);
		}
	}

}
