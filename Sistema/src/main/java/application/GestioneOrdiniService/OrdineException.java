package application.GestioneOrdiniService;

/**
 * Questa classe gestisce le eccezioni lanciate durante l'esecuzione di una
 * operazione su un ordine, a seguito della violazione del contratto della
 * classe Ordine.
 * 
 * @see application.GestioneOrdiniService.Ordine
 * 
 * @author Dorotea Serrelli
 * */

public class OrdineException extends Exception{
private static final long serialVersionUID = 1L;
	
	/**
	 * Costruttore di classe di default.
	 * */

	public OrdineException() {}


	/**
	 * Costruttore di classe con messaggio personalizzato.
	 * @param message il messaggio dell'eccezione da far visualizzare
	 * */

	public OrdineException(String message) {
		super(message);
	}


	/**
	 * Classe statica per gestire un ordine privo di prodotti.
	 * */
	public static class OrdineVuotoException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public OrdineVuotoException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la mancata specifica dell'indirizzo di spedizione
	 * per l'ordine.
	 * */
	public static class IndirizzoSpedizioneNulloException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public IndirizzoSpedizioneNulloException(String message) {
			super(message);
		}
	}
	
	
	/**
	 * Classe statica per gestire la mancata/errata specifica della modalità di spedizione
	 * per l'ordine.
	 * */
	public static class ErroreTipoSpedizioneException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ErroreTipoSpedizioneException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire la mancata/errata specifica della modalità di consegna
	 * per l'ordine.
	 * */
	public static class ErroreTipoConsegnaException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ErroreTipoConsegnaException(String message) {
			super(message);
		}
	}
	
	
	
	/**
	 * Classe statica per gestire l'errore di spedizione di un ordine non ancora pronto
	 * ossia avente stato "Spedito"
	 * */
	public static class ErroreSpedizioneOrdineException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public ErroreSpedizioneOrdineException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il caso in cui il cliente ha richiesto un numero
	 * di pezzi per un prodotto maggiore rispetto alle scorte disponibili per
	 * il suddetto prodotto.
	 * */
	public static class MancanzaPezziException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public MancanzaPezziException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il caso in cui il gestore degli ordini
	 * ha specificato una quantità dei prodotti non corrispondente a
	 * quella richiesta dal cliente.
	 * */
	public static class FormatoQuantitaException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoQuantitaException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il caso in cui il gestore degli ordini
	 * ha specificato l'imballaggio nel formato non corretto.
	 * */
	public static class FormatoImballaggioException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoImballaggioException(String message) {
			super(message);
		}
	}
	
	/**
	 * Classe statica per gestire il caso in cui il gestore degli ordini
	 * ha specificato l'azienda di spedizione nel formato non corretto.
	 * */
	public static class FormatoCorriereException extends OrdineException {
		private static final long serialVersionUID = 1L;

		/**
		 * Costruttore di classe con messaggio personalizzato.
		 * @param message il messaggio dell'eccezione da far visualizzare
		 * */
		public FormatoCorriereException(String message) {
			super(message);
		}
	}
	
}
