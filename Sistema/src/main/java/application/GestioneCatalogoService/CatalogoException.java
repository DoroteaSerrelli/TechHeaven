package application.GestioneCatalogoService;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione di una
 * operazione sul catalogo.
 * 
 * @see application.GestioneCatalogoService
 * @see application.NavigazioneService.ProxyProdotto
 * @see application.NavigazioneService.Prodotto
 * 
 * @author Dorotea Serrelli
 * */

public class CatalogoException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore di classe di default.
	 * */

	public CatalogoException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public CatalogoException(String message) {
		super(message);
	}


	/**
	 * Classe statica per gestire l'aggiunta di un prodotto
	 * già presente nel catalogo.
	 * */
	public static class ProdottoInCatalogoException extends CatalogoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ProdottoInCatalogoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la rimozione di un prodotto
	 * non presente nel catalogo.
	 * */
	public static class ProdottoNonInCatalogoException extends CatalogoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ProdottoNonInCatalogoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'aggiornamento di una specifica di un prodotto
	 * presente nel catalogo nel caso in cui il prodotto ha già per quella specifica il
	 * campo gia\' aggiornato.
	 * */
	public static class ProdottoAggiornatoException extends CatalogoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ProdottoAggiornatoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'aggiornamento di una specifica di un prodotto
	 * presente nel catalogo nel caso in cui la specifica del prodotto non esiste.
	 * */
	public static class ErroreSpecificaAggiornamentoException extends CatalogoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ErroreSpecificaAggiornamentoException(String message) {
			super(message);
		}
	}
}
