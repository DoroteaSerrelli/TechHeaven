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
	 * Classe statica per gestire il caso in cui l'utente specifica l'username non rispettando il 
	 * formato.
	 * */
	public static class FormatoUsernameException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoUsernameException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il caso in cui l'utente specifica la password
	 * non rispettando il formato.
	 * */
	public static class FormatoPasswordException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoPasswordException(String message) {
			super(message);
		}
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
	
	/**
	 * Classe statica per gestire la registrazione di un utente che ha digitato un'email
	 * già memorizzata nel database.
	 * */
	public static class EmailPresenteException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public EmailPresenteException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il caso in cui l'utente specifica il nome
	 * non rispettando il formato.
	 * */
	public static class FormatoNomeException extends RegistrazioneException {
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
	 * Classe statica per gestire il caso in cui l'utente specifica il cognome
	 * non rispettando il formato.
	 * */
	public static class FormatoCognomeException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoCognomeException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il caso in cui l'utente specifica il genere
	 * non rispettando il formato.
	 * */
	public static class FormatoGenereException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoGenereException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la specifica della via di un indirizzo espressa 
	 * non correttamente.
	 * */
	public static class FormatoViaException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoViaException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la specifica del numero civico di un indirizzo espresso 
	 * non correttamente.
	 * */
	public static class FormatoNumCivicoException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoNumCivicoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la specifica della città di un indirizzo espressa 
	 * non correttamente.
	 * */
	public static class FormatoCittaException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoCittaException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la specifica del CAP di un indirizzo espresso
	 * non correttamente.
	 * */
	public static class FormatoCAPException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoCAPException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la specifica della provincia di un indirizzo espressa
	 * non correttamente.
	 * */
	public static class FormatoProvinciaException extends RegistrazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoProvinciaException(String message) {
			super(message);
		}
	}
	
}
