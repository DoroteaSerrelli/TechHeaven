package application.GestioneWishlist.GestioneWishlistService;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione di una
 * operazione sulla wishlist, a seguito della violazione del contratto della
 * classe Wishlist.
 * 
 * @see application.GestioneWishlist.GestioneWishlistService.Wishlist
 * 
 * @author Dorotea Serrelli
 * */
public class WishlistException extends Exception{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Costruttore di classe di default.
	 * */

	public WishlistException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public WishlistException(String message) {
		super(message);
	}


	/**
	 * Classe statica per gestire il riferimento null ad un oggetto ProxyProdotto.
	 * */
	public static class ProdottoNulloException extends WishlistException {
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
	 * Classe statica per gestire la presenza di un prodotto nella wishlist.
	 * */
	public static class ProdottoPresenteException extends WishlistException {
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
	 * Classe statica per gestire la mancanza di un prodotto nella wishlist.
	 * */
	public static class ProdottoNonPresenteException extends WishlistException {
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
	 * Classe statica per gestire una wishlist vuota.
	 * */
	public static class WishlistVuotaException extends WishlistException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */

		public WishlistVuotaException(String message) {
			super(message);
		}
	}

}
