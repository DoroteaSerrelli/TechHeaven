package application.NavigazioneService;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione operazioni legate
 * ad un prodotto.
 * 
 * @see java.application.NavigazioneService.ProxyProdotto;
 * @see java.application.NavigazioneService.Prodotto;
 * @see java.application.NavigazioneService.ObjectProdotto;
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

}
