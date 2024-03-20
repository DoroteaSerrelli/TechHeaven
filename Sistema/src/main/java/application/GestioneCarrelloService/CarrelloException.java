package application.GestioneCarrelloService;

/*
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione di una
 * operazione sul carrello, a seguito della violazione del contratto della
 * classe Carrello.
 * 
 * @author Dorotea Serrelli
 * @see java.application.GestioneCarrelloService.Carrello
 *
 * */

public class CarrelloException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public CarrelloException() {}
	public CarrelloException(String message) {
	    super(message);
	}
	
	public static class ProdottoNulloException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		public ProdottoNulloException(String message) {
            super(message);
        }
    }
	
	
	public static class ProdottoPresenteException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		public ProdottoPresenteException(String message) {
            super(message);
        }
    }
	
	public static class ProdottoNonPresenteException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		public ProdottoNonPresenteException(String message) {
            super(message);
        }
    }
	
	public static class CarrelloVuotoException extends CarrelloException {
		private static final long serialVersionUID = 1L;

		public CarrelloVuotoException(String message) {
            super(message);
        }
    }
	
	

}
