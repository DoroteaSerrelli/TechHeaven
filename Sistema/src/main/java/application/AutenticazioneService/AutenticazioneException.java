package application.AutenticazioneService;

import application.AutenticazioneService.AutenticazioneException;

/**
 * La classe gestisce le eccezioni lanciate in fase autenticazione dell'utente al sistema,
 * di reimpostazione della password ed operazioni di gestione del profilo dell'utente.
 * 
 * @see application.AutenticazioneService.AutenticazioneService
 * @see application.AutenticazioneService.AutenticazioneServiceImpl
 * 
 * @author Dorotea Serrelli
 * 
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
	 * Classe statica per gestire il caso in cui il ruolo scelto non
	 * rispetta il formato: Cliente, GestoreCatalogo, GestoreOrdini.
	 * */
	public static class FormatoRuoloException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoRuoloException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il caso in cui il ruolo scelto non
	 * è associato all'utente.
	 * */
	public static class RuoloInesistenteException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public RuoloInesistenteException(String message) {
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
	 * Classe statica per gestire, in caso di reimpostazione dela password, 
	 * il caso in cui l'utente inserisce come nuova password la password che ha già associata 
	 * nel database.
	 * */
	public static class PasswordEsistenteException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public PasswordEsistenteException(String message) {
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
	 * Classe statica per gestire l'assenza di un indirizzo dell'utente da 
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
	
	/**
	 * Classe statica per gestire l'assenza di un indirizzo dell'utente da 
	 * rimuovere.
	 * */
	public static class RimozioneIndirizzoException extends AutenticazioneException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public RimozioneIndirizzoException(String message) {
			super(message);
		}
	}
	
}
