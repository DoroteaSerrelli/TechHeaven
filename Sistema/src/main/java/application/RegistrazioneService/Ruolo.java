package application.RegistrazioneService;

/**
 * La classe esprime il concetto di ruolo di un individuo all'interno
 * del sistema quando si autentica.
 * I possibili ruoli per un individuo sono utente, gestore degli ordini 
 * e gestore del catalogo.
 * 
 * @author Dorotea Serrelli
 * 
 * */

public class Ruolo {
	/**
	 * Il nome del ruolo associato ad un individuo.
	 * */
	private String nomeRuolo;
	
	/**
	 * Costruttore della classe
	 * @param nomeRuolo : il ruolo
	 * @return un oggetto della classe Ruolo con attributo nomeRuolo
	 * */
	public Ruolo(String nomeRuolo) {
		this.nomeRuolo = nomeRuolo;
	}
	
	/**
	 * Il metodo fornisce il nome del ruolo.
	 * @return valore attributo nomeRuolo
	 * */
	public String getNomeRuolo() {
		return nomeRuolo;
	}
	
	/**
	 * Il metodo imposta il nome del ruolo.
	 * @param nomeRuolo : il nome del ruolo
	 * */
	public void setNomeRuolo(String nomeRuolo) {
		this.nomeRuolo = nomeRuolo;
	}

	/**
	 * Il metodo crea una copia esatta di questo ruolo.
	 * Poiché la classe `Ruolo` contiene solo un attributo immutabile (il nome del ruolo),
	 * questa operazione crea essenzialmente un nuovo oggetto con lo stesso nome.
	 *
	 * @return Una nuova istanza di `Ruolo` identica a questa.
	 * @throws AssertionError Se il metodo `clone()` di `Object` lancia un'eccezione inaspettata.
	 */

	public Ruolo clone() {
		try {
			return (Ruolo) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}