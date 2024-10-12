package application.RegistrazioneService;

/**
 * La classe esprime il concetto di ruolo di un individuo all'interno
 * del sistema quando si autentica.
 * I possibili ruoli per un individuo sono cliente, gestore degli ordini 
 * e gestore del catalogo.
 * 
 * @author Dorotea Serrelli
 * 
 * */

public class Ruolo implements Cloneable{
	
	/**
	 * nomeRuolo : il nome del ruolo associato ad un individuo.
	 * */
	private String nomeRuolo;
	
	/**
	 * Costruttore della classe.
	 * Si costruisce un oggetto della classe Ruolo con 
	 * attributo nomeRuolo.
	 * 
	 * @param nomeRuolo : il ruolo
	 * 
	 * */
	
	public Ruolo(String nomeRuolo) {
		this.nomeRuolo = nomeRuolo;
	}
	
	/**
	 * Il metodo fornisce il nome del ruolo.
	 * 
	 * @return valore attributo nomeRuolo
	 * */
	
	public String getNomeRuolo() {
		return nomeRuolo;
	}
	
	/**
	 * Il metodo imposta il nome del ruolo.
	 * 
	 * @param nomeRuolo : il nome del ruolo
	 * 
	 * */
	
	public void setNomeRuolo(String nomeRuolo) {
		this.nomeRuolo = nomeRuolo;
	}
	
	/**
	 * Confronta questo oggetto `Ruolo` con un altro oggetto per verificare se sono uguali.
	 * Due oggetti `Ruolo` sono considerati uguali se hanno lo stesso nome del ruolo.
	 *
	 * @param obj : L'oggetto da confrontare con questo `Ruolo`.
	 * @return {@code true} se l'oggetto specificato è uguale a questo `Ruolo`; {@code false} altrimenti.
	 */
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
	        return true; // Riferimento identico
	    }
	    if (obj == null || getClass() != obj.getClass()) {
	        return false; // Oggetto nullo o classi diverse
	    }
	    Ruolo ruolo = (Ruolo) obj; // Cast dell'oggetto
	    return nomeRuolo != null ? nomeRuolo.equals(ruolo.nomeRuolo) : ruolo.nomeRuolo == null; // Confronto del nomeRuolo
	}
	
	/**
	 * Restituisce un valore hash per questo oggetto `Ruolo`.
	 * Il valore hash è calcolato utilizzando il nome del ruolo.
	 *
	 * @return Un valore hash intero rappresentante questo `Ruolo`.
	 */
	
	@Override
	public int hashCode() {
	    return nomeRuolo != null ? nomeRuolo.hashCode() : 0; // Calcolo dell'hash
	}


	/**
	 * Il metodo crea una copia esatta di questo ruolo.
	 * Poiché la classe `Ruolo` contiene solo un attributo immutabile (il nome del ruolo),
	 * questa operazione crea essenzialmente un nuovo oggetto con lo stesso nome.
	 *
	 * @return Una nuova istanza di `Ruolo` identica a questa.
	 * @throws AssertionError Se il metodo `clone()` di `Object` lancia un'eccezione inaspettata.
	 */
	
	@Override
	public Ruolo clone() throws CloneNotSupportedException{
		try {
			return (Ruolo) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}