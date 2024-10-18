package application.GestioneApprovvigionamenti;

import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.FormatoFornitoreException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.QuantitaProdottoDisponibileException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import application.NavigazioneService.ProxyProdotto;

/**
 * La classe rappresenta il concetto di richiesta di approvvigionamento di un prodotto.
 * Essa contiene le informazioni relative ad una richiesta di rifornimento di un prodotto 
 * venduto dall'e-commerce: codice richiesta, fornitore del prodotto verso cui si effettua la richiesta,
 * email del fornitore come modalità di contatto con il fornitore, la descrizione da allegare alla richiesta,
 * il prodotto da richiedere, la quantità del prodotto richiesta al fornitore. 
 * 
 * @see application.NavigazioneService.ProxyProdotto
 * 
 * @author Dorotea Serrelli
 * 
 * */

public class RichiestaApprovvigionamento {

	/**
	 * codice : identificativo numerico univoco di una richiesta
	 * di rifornimento.
	 * */

	private int codice;

	/**
	 * fornitore : il nominativo del fornitore.
	 * */

	private String fornitore;

	/**
	 * emailFornitore : l'email del fornitore.
	 * */

	private String emailFornitore;

	/**
	 * descrizione : la descrizione da corredo per la 
	 * richiesta di rifornimento del prodotto.
	 * */

	private String descrizione;

	/**
	 * quantità : la quantità di prodotto che si 
	 * richiede per il rifornimento.
	 * */

	private int quantità;

	/**
	 * prodotto : il prodotto per cui si effettua la
	 * richiesta di approvvigionamento.
	 * */

	private ProxyProdotto prodotto;

	/**
	 * Il metodo verifica se il nominativo del fornitore,
	 * per realizzare una richiesta di rifornimento, è espresso nel formato corretto.
	 * 
	 * @param nominativo : il nominativo del fornitore 
	 * 					   (può avere lettere, numeri e spazi - ma non inizia con uno spazio)
	 * 
	 * @return true se i dati inseriti sono stati specificati nel 
	 * 				formato corretto; false altrimenti.
	 * */

	public static boolean checkValidateNominativo(String nominativo) {
		String nominativoPattern = "^[a-zA-Z0-9 ]+$";

		return nominativo.matches(nominativoPattern);
	}

	/**
	 * Il metodo verifica se l'indirizzo di posta elettronica del fornitore
	 * è specificato nel corretto formato.
	 * 
	 * @param email : l'indirizzo di posta elettronica del fornitore
	 * 
	 * @return true se l'email è scritta nel formato corretto; false altrimenti.
	 * */

	public static boolean checkValidateEmail(String email) {
		String emailPattern = "^\\S+@\\S+\\.\\S+$";
		return email.matches(emailPattern);
	}

	/**
	 * Costruttore di classe di default.
	 * */

	public RichiestaApprovvigionamento() {

		this.codice = -1;
		this.fornitore = "";
		this.emailFornitore = "";
		this.descrizione = "";
		this.quantità = 0;
		this.prodotto = null;
	}

	/**
	 * Il metodo consente di verificare se i dati per la compilazione di una
	 * richiesta di approvvigionamento di un prodotto sono
	 * corretti.
	 * 
	 * @param fornitore : il nominativo del fornitore
	 * @param emailFornitore : l'email del fornitore
	 * @param descrizione : la descrizione da corredo per la 
	 * 						richiesta di rifornimento del prodotto.
	 * @param quantità : la quantità di prodotto che si 
	 * 					 richiede per il rifornimento.
	 * @param prodotto : il prodotto per cui si effettua la
	 * 					 richiesta di approvvigionamento.
	 * 
	 * @throws QuantitaProdottoDisponibileException : eccezione lanciata nel momento in cui il prodotto non è esaurito in
	 * 													magazzino. 
	 * 
	 * @throws FormatoFornitoreException : eccezione lanciata nel caso di formato non corretto delle informazioni 
	 * 								relative ad un fornitore.
	 * 
	 * @throws QuantitaProdottoException : eccezione lanciata nel caso di quantità di un prodotto non valida
	 * 
	 * @throws DescrizioneDettaglioException : eccezione lanciata nel caso in cui la descrizione da allegare è vuota
	 * 
	 * @throws ProdottoVendibileException : eccezione lanciata nel caso in cui si fa richiesta di rifornimento 
	 * 										di un prodotto non venduto dal negozio online
	 * 
	 * @throws FormatoEmailException : eccezione che gestisce il caso in cui l'email del fornitore è espressa nel
	 * 									formato non corretto
	 * 
	 * */

	public static boolean checkValidate(String fornitore, String emailFornitore, String descrizione,
			int quantità, ProxyProdotto prodotto) throws QuantitaProdottoDisponibileException, FormatoFornitoreException, FormatoEmailException, DescrizioneDettaglioException, QuantitaProdottoException {

		if(prodotto.getQuantita() != 0)
			throw new QuantitaProdottoDisponibileException("In magazzino sono ancora disponibili delle scorte per il prodotto specificato.");
		
		if(quantità <= 0)
			throw new QuantitaProdottoException("La quantità del prodotto specificata non è valida.");
		
		if(!checkValidateNominativo(fornitore))
			throw new FormatoFornitoreException("Il nome del fornitore deve essere una sequenza di lettere, spazi ed, eventualmente, numeri.");

		if(!checkValidateEmail(emailFornitore))
			throw new FormatoEmailException("L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).");

		if(descrizione.isBlank())
			throw new DescrizioneDettaglioException("Questo campo non puo\' essere vuoto.");

		return true;

	}

	/**
	 * Costruttore della classe.
	 * Si costruisce un oggetto della classe RichiestaApprovvigionamento con le caratteristiche pari
	 * ai parametri passatti in input: codice, fornitore, emailFornitore, descrizione,
	 * quantità, prodotto.
	 * 
	 * @param codice : numero identificativo della richiesta di rifornimento
	 * @param fornitore : il nominativo del fornitore
	 * @param emailFornitore : l'email del fornitore
	 * @param descrizione : la descrizione da corredo per la 
	 * 						richiesta di rifornimento del prodotto.
	 * @param quantità : la quantità di prodotto che si 
	 * 					 richiede per il rifornimento.
	 * @param prodotto : il prodotto per cui si effettua la
	 * 					 richiesta di approvvigionamento.
	 * 
	 * @throws QuantitaProdottoDisponibileException : eccezione lanciata nel momento in cui il prodotto non è esaurito in
	 * 													magazzino. 
	 * 
	 * @throws FormatoFornitoreException : eccezione lanciata nel caso di formato non corretto delle informazioni 
	 * 								relative ad un fornitore.
	 * 
	 * @throws QuantitaProdottoException : eccezione lanciata nel caso di quantità di un prodotto non valida
	 * 
	 * @throws DescrizioneDettaglioException : eccezione lanciata nel caso in cui la descrizione da allegare è vuota
	 * 
	 * @throws ProdottoVendibileException : eccezione lanciata nel caso in cui si fa richiesta di rifornimento 
	 * 										di un prodotto non venduto dal negozio online
	 * 
	 * @throws FormatoEmailException : eccezione che gestisce il caso in cui l'email del fornitore è espressa nel
	 * 									formato non corretto
	 * 
	 * */

	public RichiestaApprovvigionamento(int codice, String fornitore, String emailFornitore, String descrizione,
			int quantità, ProxyProdotto prodotto) throws FormatoFornitoreException, QuantitaProdottoException, DescrizioneDettaglioException, ProdottoVendibileException, FormatoEmailException, QuantitaProdottoDisponibileException {

		if(checkValidate(fornitore, emailFornitore, descrizione, quantità, prodotto)) {
			this.codice = codice;
			this.fornitore = fornitore;
			this.emailFornitore = emailFornitore;
			this.descrizione = descrizione;
			this.quantità = quantità;
			this.prodotto = prodotto;
		}

	}


	/**
	 * Costruttore della classe senza specificare il codice
	 * della richiesta di approvvigionamento.
	 * Si costruisce un oggetto della classe RichiestaApprovvigionamento con le caratteristiche pari
	 * ai parametri passatti in input: fornitore, emailFornitore, descrizione,
	 * quantità, prodotto.
	 * 
	 * @param fornitore : il nominativo del fornitore
	 * @param emailFornitore : l'email del fornitore
	 * @param descrizione : la descrizione da corredo per la 
	 * 						richiesta di rifornimento del prodotto.
	 * @param quantità : la quantità di prodotto che si 
	 * 					 richiede per il rifornimento.
	 * @param prodotto : il prodotto per cui si effettua la
	 * 					 richiesta di approvvigionamento.
	 * 
	 * 
	 * @throws FornitoreException : eccezione lanciata nel caso di formato non corretto delle informazioni 
	 * 								relative ad un fornitore.
	 * @throws QuantitaProdottoException : eccezione lanciata nel caso di quantità di un prodotto non valida
	 * @throws DescrizioneDettaglioException : eccezione lanciata nel caso in cui la descrizione da allegare è vuota
	 * @throws ProdottoVendibileException : eccezione lanciata nel caso in cui si fa richiesta di rifornimento 
	 * 										di un prodotto non venduto dal negozio online
	 * @throws QuantitaProdottoDisponibileException : eccezione lanciata nel momento in cui il prodotto non è esaurito in
	 * 													magazzino. 
	 * 
	 * @throws FormatoFornitoreException : eccezione lanciata nel caso di formato non corretto delle informazioni 
	 * 								relative ad un fornitore.
	 * 
	 * @throws QuantitaProdottoException : eccezione lanciata nel caso di quantità di un prodotto non valida
	 * 
	 * @throws DescrizioneDettaglioException : eccezione lanciata nel caso in cui la descrizione da allegare è vuota
	 * 
	 * @throws ProdottoVendibileException : eccezione lanciata nel caso in cui si fa richiesta di rifornimento 
	 * 										di un prodotto non venduto dal negozio online
	 * 
	 * @throws FormatoEmailException : eccezione che gestisce il caso in cui l'email del fornitore è espressa nel
	 * 									formato non corretto
	 * 
	 * */

	public RichiestaApprovvigionamento(String fornitore, String emailFornitore, String descrizione,
			int quantità, ProxyProdotto prodotto) throws QuantitaProdottoException, DescrizioneDettaglioException, ProdottoVendibileException, QuantitaProdottoDisponibileException, FormatoFornitoreException, FormatoEmailException {

		if(checkValidate(fornitore, emailFornitore, descrizione, quantità, prodotto)) {
			this.fornitore = fornitore;
			this.emailFornitore = emailFornitore;
			this.descrizione = descrizione;
			this.quantità = quantità;
			this.prodotto = prodotto;
		}

	}



	/**
	 * Il metodo restituisce il codice della richiesta di rifornimento.
	 * 
	 * @return codice : l'identificativo della richiesta di rifornimento
	 */

	public int getCodiceRifornimento() {
		return codice;
	}

	/**
	 * Il metodo imposta il codice della richiesta di rifornimento.
	 * 
	 * @param codice : l'identificativo della richiesta di rifornimento
	 */

	public void setCodice(int codice) {
		this.codice = codice;
	}

	/**
	 * Il metodo restituisce il nominativo del fornitore a cui si fa la
	 * richiesta di approvvigionamento.
	 * 
	 * @return fornitore : il nome del fornitore
	 */

	public String getFornitore() {
		return fornitore;
	}

	/**
	 * Il metodo imposta il nominativo del fornitore a cui si fa la
	 * richiesta di approvvigionamento.
	 * 
	 * @param fornitore : il fornitore a cui si effettua la richiesta
	 * 					  del prodotto
	 * 
	 * @throws FormatoFornitoreException : eccezione lanciata nel caso di formato non corretto del
	 * 								nominativo del fornitore.
	 */

	public void setFornitore(String fornitore) throws FormatoFornitoreException {
		if(!checkValidateNominativo(fornitore))
			throw new FormatoFornitoreException("Il nominativo del fornitore non e\' espresso nel formato corretto.\n"
					+ "Il nominativo deve essere una sequenza di lettere, spazi ed, eventualmente, numeri.");
		else
			this.fornitore = fornitore;
	}

	/**
	 * Il metodo restituisce l'email del fornitore a cui si fa la
	 * richiesta di approvvigionamento.
	 * 
	 * @return emailFornitore : l'indirizzo di posta elettronica del fornitore
	 */

	public String getEmailFornitore() {
		return emailFornitore;
	}

	/**
	 * Il metodo imposta l'email del fornitore a cui si fa la
	 * richiesta di approvvigionamento.
	 * 
	 * @param emailFornitore : l'indirizzo di posta elettronica del fornitore
	 * 
	 * @throws FormatoFornitoreException : eccezione lanciata nel caso di formato non corretto dell'email
	 * 								del fornitore.
	 */

	public void setEmailFornitore(String emailFornitore) throws FormatoFornitoreException {
		if(!checkValidateEmail(emailFornitore))
			throw new FormatoFornitoreException("L'email del fornitore non e\' espressa nel formato corretto.\n"
					+ "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).");
		else
			this.emailFornitore = emailFornitore;
	}

	/**
	 * Il metodo fornisce una descrizione di dettaglio da allegare
	 * alla richiesta di approvvigionamento.
	 * 
	 * @return descrizione : la descrizione di dettaglio per la richiesta di fornitura
	 */

	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * Il metodo imposta la descrizione di dettaglio da allegare
	 * alla richiesta di approvvigionamento.
	 * 
	 * @param descrizione : la descrizione di dettaglio per la richiesta di fornitura
	 * 
	 * @throws DescrizioneDettaglioException : eccezione lanciata nel caso in cui la descrizione da allegare è vuota
	 */

	public void setDescrizione(String descrizione) throws DescrizioneDettaglioException {
		if(descrizione.isEmpty())
			throw new DescrizioneDettaglioException("La descrizione di dettaglio non puo\' essere vuota.");
		else
			this.descrizione = descrizione;
	}

	/**
	 * Il metodo fornisce la quantità di rifornimento richiesta al fornitore
	 * per il prodotto.
	 * 
	 * @return quantità : la quantità di prodotto richiesta al fornitore
	 */

	public int getQuantitaRifornimento() {
		return quantità;
	}

	/**
	 * Il metodo imposta la quantità di rifornimento richiesta al fornitore
	 * per il prodotto.
	 * 
	 * @param quantità : la quantità di prodotto richiesta al fornitore
	 * 
	 * @throws QuantitaProdottoException : eccezione lanciata nel caso di quantità di un prodotto non valida
	 */

	public void setQuantitaRifornimento(int quantità) throws QuantitaProdottoException {
		if(quantità <= 0)
			throw new QuantitaProdottoException("La quantità di un prodotto deve essere maggiore di 0.");
		else 
			this.quantità = quantità;

	}

	/**
	 * Il metodo fornisce le caratteristiche essenziali del prodotto, 
	 * venduto dal negozio, per cui si effettua la 
	 * richiesta di rifornimento.
	 * 
	 * @return prodotto : il prodotto per il quale fare 
	 * 					  la richiesta di approvvigionamento
	 */

	public ProxyProdotto getProdotto() {
		return prodotto;
	}

	/**
	 * Il metodo imposta le caratteristiche essenziali del prodotto, 
	 * venduto dal negozio, per cui si effettua la 
	 * richiesta di rifornimento.
	 * 
	 * @param prodotto : il prodotto per il quale fare 
	 * 					 la richiesta di approvvigionamento
	 * 
	 * @throws ProdottoVendibileException : eccezione lanciata nel caso in cui si fa richiesta di rifornimento 
	 * 										di un prodotto non venduto dal negozio online
	 * */

	public void setProdotto(ProxyProdotto prodotto) throws ProdottoVendibileException {
		if(!prodotto.isInCatalogo())
			throw new ProdottoVendibileException("Il prodotto specificato non e\' presente nel catalogo.");
		else
			this.prodotto = prodotto;
	}

	/**
	 * Il metodo fornisce in formato stringa le caratteristiche associate ad
	 * una richiesta di approvvigionamento.
	 * 
	 * @return un oggetto della classe String che fornisce le caratteristiche della
	 * 		   richiesta di approvvigionamento
	 * */

	@Override
	public String toString() {
		return "RichiestaApprovvigionamento [codice=" + codice + ", fornitore=" + fornitore + ", emailFornitore="
				+ emailFornitore + ", descrizione=" + descrizione + ", quantità=" + quantità + ", \nProdotto\n" + prodotto.toString()
				+ "]";
	}
}
