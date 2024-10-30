package application.NavigazioneService;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione di operazioni legate
 * alla ricerca di prodotti.
 * 
 * @see application.NavigazioneService.ProxyProdotto
 * @see application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ObjectProdotto
 * @see application.NavigazioneService.NavigazioneServiceImpl
 * 
 * @author Dorotea Serrelli
 * */


public class NavigazioneException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore di classe di default.
	 * */

	public NavigazioneException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public NavigazioneException(String message) {
		super(message);
	}

	/**
	 * Classe statica per gestire la ricerca di prodotti appartenenti ad una 
	 * categoria inesistente.
	 * */
	public static class ErroreRicercaCategoriaException extends ProdottoException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ErroreRicercaCategoriaException(String message) {
			super(message);
		}
	}


}
