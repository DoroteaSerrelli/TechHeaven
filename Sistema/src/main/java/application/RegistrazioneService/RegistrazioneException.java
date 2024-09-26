package application.RegistrazioneService;

/**
 * Questa classe gestisce le eccezioni lanciate durante la registrazione di un utente 
 * al sistema.
 * 
 * @see application.RegistrazioneService.RegistrazioneService
 * 
 * @author Dorotea Serrelli
 * */

public class RegistrazioneException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore di classe di default.
	 * */

	public RegistrazioneException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public RegistrazioneException(String message) {
		super(message);
	}


	/**
	 * Classe statica per gestire la registrazione di un utente che ha digitato un username
	 * già memorizzata nel database.
	 * */
	public static class UtentePresenteException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public UtentePresenteException(String message) {
			super(message);
		}
	}
}
