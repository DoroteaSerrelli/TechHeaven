package application.AutenticazioneService;

import application.AutenticazioneService.AutenticazioneException;

/**
 * La classe gestisce le eccezioni lanciate in fase autenticazione dell'utente al sistema
 * */

public class AutenticazioneException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore di classe di default.
	 * */

	public AutenticazioneException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public AutenticazioneException(String message) {
		super(message);
	}


	/**
	 * Classe statica per gestire la mancanza di un utente nel sistema.
	 * */
	public static class UtenteInesistenteException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public UtenteInesistenteException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il formato non corretto della password.
	 * */
	public static class FormatoPasswordException extends AutenticazioneException {
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
	 * Classe statica per gestire l'assenza del profilo dell'utente.
	 * */
	public static class ProfiloInesistenteException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ProfiloInesistenteException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'email già associata all'utente.
	 * */
	public static class EmailEsistenteException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public EmailEsistenteException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il formato errato dell'email.
	 * */
	public static class FormatoEmailException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoEmailException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il numero di telefono
	 * già associato all'utente.
	 * */
	public static class TelefonoEsistenteException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public TelefonoEsistenteException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il formato non corretto del numero di telefono.
	 * */
	public static class FormatoTelefonoException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoTelefonoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la mancata selezione di un'informazione 
	 * del profilo utente da modificare.
	 * */
	public static class InformazioneDaModificareException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public InformazioneDaModificareException(String message) {
			super(message);
		}
	}

	/**
	 * Classe statica per gestire l'aggiunta di un indirizzo già associato all'utente.
	 * */
	public static class IndirizzoEsistenteException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public IndirizzoEsistenteException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il formato non corretto di un indirizzo.
	 * */
	public static class FormatoIndirizzoException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoIndirizzoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire l'assenza di un indirizzo dell'utente da eliminare o da 
	 * aggiornare.
	 * */
	public static class ModificaIndirizzoException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ModificaIndirizzoException(String message) {
			super(message);
		}
	}
	
}