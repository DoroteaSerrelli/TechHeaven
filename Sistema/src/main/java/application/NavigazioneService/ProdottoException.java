package application.NavigazioneService;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione operazioni legate
 * ad un prodotto.
 * 
 * @see application.NavigazioneService.ProxyProdotto
 * @see application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ObjectProdotto
 * 
 * @author Dorotea Serrelli
 * */


public class ProdottoException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore di classe di default.
	 * */

	public ProdottoException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public ProdottoException(String message) {
		super(message);
	}

	/**
	 * Classe statica per gestire l'inserimento di un codice per il prodotto 
	 * non numerico.
	 * */
	public static class FormatoCodiceException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoCodiceException(String message) {
			super(message);
		}
	}

	
	/**
	 * Classe statica per gestire l'inserimento di un prodotto in una categoria
	 * non ammissibile.
	 * */
	public static class CategoriaProdottoException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public CategoriaProdottoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'inserimento di un prodotto in una sottocategoria
	 * non ammissibile.
	 * */
	public static class SottocategoriaProdottoException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public SottocategoriaProdottoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'inserimento di scorte in magazzino per un prodotto 
	 * con valori negativi (quantità deve avere valori positivi o 0)
	 * */
	public static class QuantitaProdottoException extends ProdottoException {
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
	 * Classe statica per gestire l'inserimento del prezzo di un prodotto che non sia
	 * negativo o pari a 0.0.
	 * */
	public static class PrezzoProdottoException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public PrezzoProdottoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'inserimento del nome del prodotto nel formato
	 * errato.
	 * */
	public static class FormatoNomeException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoNomeException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'inserimento del modello del prodotto nel formato
	 * errato.
	 * */
	public static class FormatoModelloException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoModelloException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'inserimento della marca del prodotto nel formato
	 * errato.
	 * */
	public static class FormatoMarcaException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoMarcaException(String message) {
			super(message);
		}
	}
	
	
	/**
	 * Classe statica per gestire l'inserimento della descrizione
	 * in evidenza del prodotto nel formato
	 * errato.
	 * */
	public static class FormatoTopDescrizioneException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoTopDescrizioneException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'inserimento della descrizione
	 * di dettaglio del prodotto nel formato
	 * errato.
	 * */
	public static class FormatoDettagliException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoDettagliException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'inserimento della sottocategoria
	 * di un prodotto che non appartiene alla categoria specificata 
	 * per quel prodotto.
	 * */
	public static class AppartenenzaSottocategoriaException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public AppartenenzaSottocategoriaException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'inserimento della messa in evidenza
	 * di un prodotto non espressa nel corretto formato.
	 * */
	public static class FormatoVetrinaException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoVetrinaException(String message) {
			super(message);
		}
	}
	
}
