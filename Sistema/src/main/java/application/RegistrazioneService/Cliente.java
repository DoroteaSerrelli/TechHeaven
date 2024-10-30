package application.RegistrazioneService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdiniService.ProxyOrdine;
import application.RegistrazioneService.RegistrazioneException.FormatoCAPException;
import application.RegistrazioneService.RegistrazioneException.FormatoCittaException;
import application.RegistrazioneService.RegistrazioneException.FormatoCognomeException;
import application.RegistrazioneService.RegistrazioneException.FormatoGenereException;
import application.RegistrazioneService.RegistrazioneException.FormatoNomeException;
import application.RegistrazioneService.RegistrazioneException.FormatoNumCivicoException;
import application.RegistrazioneService.RegistrazioneException.FormatoProvinciaException;
import application.RegistrazioneService.RegistrazioneException.FormatoViaException;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;

/**
 * Questa classe detiene le informazioni personali dell'utente
 * come cliente del negozio.
 * Tale classe, inoltre, ha un riferimento ad una collezione di riferimenti di tipo ProxyOrdine,
 * in modo che si conoscano le informazioni essenziali degli ordini effettuati dall'utente.
 * 
 * @see application.GestioneOrdiniService.ProxyOrdine
 * @see application.RegistrazioneService.Indirizzo
 * 
 * @author Dorotea Serrelli
 * */

public class Cliente implements Cloneable{
	
	private OrdineDAODataSource orderDAO;
	
	/**
	 * La classe enum Sesso rappresenta il concetto di genere
	 * del cliente : F (femminile), M (maschile).
	 * */
	
	public enum Sesso{
		F,
		M
	}
	
	/**
	 * email : è l'indirizzo di posta elettronica del cliente
	 * */
	private String email;
	
	/**
	 * nome : è il nome del cliente
	 * */
	private String nome;
	
	/**
	 * cognome : è il cognome del cliente
	 * */
	private String cognome;
	
	/**
	 * sex : è il sesso del cliente
	 * */
	private Sesso sex;
	
	/**
	 * telefono : è il numero di cellulare del cliente 
	 * */
	private String telefono;
	
	/**
	 * indirizzi : è la rubrica di indirizzi specificati dall'utente,
	 * al fine di far recapitare gli ordini
	 * */
	private ArrayList<Indirizzo> indirizzi;
	
	/**
	 * proxyOrdini : è una collezione di oggetti
	 * di tipo ProxyOrdine per memorizzare le informazioni essenziali degli
	 * ordini effettuati dall'utente presso il negozio online.
	 * */
	private Collection<ProxyOrdine> proxyOrdini;
	
	/**
	 * Il metodo verifica se le informazioni personali fornite 
	 * dal nuovo cliente sono espresse nel formato corretto.
	 * Tale metodo, pertanto, verrà utilizzato in fase di registrazione 
	 * del nuovo cliente al sistema.
	 * Per la correttezza dell'indirizzo postale, si veda il metodo
	 * checkValidate nella classe Indirizzo.
	 * 
	 * @see application.RegistrazioneService.Indirizzo
	 * 
	 * @param nome : il nome del cliente
	 * @param cognome : il cognome del cliente
	 * @param sex : il genere del cliente
	 * @param telefono : il numero di telefono del cliente
	 * @param indirizzo : l'indirizzo del nuovo cliente in fase di registrazione
	 * 
	 * @return true se i dati inseriti sono stati specificati nel 
	 * formato corretto; false altrimenti.
	 * 
	 * @throws FormatoProvinciaException  : eccezione che gestisce il caso in cui la provincia
	 * 										non è espressa nel formato corretto
	 * 
	 * @throws FormatoCAPException : eccezione che gestisce il caso in cui il CAP non rispetta
	 * 								 il formato
	 * 
	 * @throws FormatoCittaException : eccezione che gestisce il caso in cui la città non
	 * 									è specificata nel formato corretto
	 * 
	 * @throws FormatoNumCivicoException : eccezione che gestisce il caso in cui il numero civico
	 * 										non è specificato nel formato corretto
	 * 
	 * @throws FormatoViaException : eccezione che gestisce il caso in cui la via non è specificata nel formato
	 * 									corretto
	 * @throws FormatoNomeException : eccezione che gestisce il caso in cui l'utente specifica il nome
	 * 									non rispettando il formato.
	 * @throws FormatoCognomeException  : eccezione che gestisce il caso in cui l'utente specifica il cognome
	 * 									non rispettando il formato.
	 * @throws FormatoGenereException : eccezione che gestisce il caso in cui l'utente non specifica il genere.
	 * @throws FormatoTelefonoException : eccezione che gestisce il caso in cui l'utente non specifica il 
	 * 									numero di telefono con il formato corretto.
	 * 
	 * */
	public static boolean checkValidate(String nome, String cognome, String sex, String telefono,
			Indirizzo indirizzo) throws FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException {
		
		
		String nomeCognomePattern = "^[A-Za-z\s]+$";
		String telefonoPattern = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
		
		
		if(!nome.matches(nomeCognomePattern))
			throw new FormatoNomeException("Il nome deve contenere solo lettere e, eventualmente, spazi.");
		
		if(!cognome.matches(nomeCognomePattern))
			throw new FormatoCognomeException("Il cognome deve contenere solo lettere e, eventualmente, spazi.");
		
		if(sex == null || sex.isBlank() || (!(sex.equalsIgnoreCase(Sesso.M.toString())) && !(sex.equalsIgnoreCase(Sesso.F.toString()))))
			throw new FormatoGenereException("Specificare il genere.");
		
		if(!telefono.matches(telefonoPattern))
			throw new FormatoTelefonoException("Il formato del numero di telefono deve essere xxx-xxx-xxxx.");
		
		/*
		 * Si effettua la verifica dell'indirizzo inserito invocando il metodo checkValidate della
		 * classe Indirizzo.
		 * **/
		if(!Indirizzo.checkValidate(indirizzo))
			return false;
		
		return true;
	}
	
	/**
	 * Il metodo verifica se l'indirizzo di posta elettronica fornito dall'utente
	 * è stato specificato nel corretto formato.
	 * Il metodo viene principalmente utilizzato nel caso in cui l'utente
	 * debba aggiornare il proprio indirizzo email.
	 * 
	 * @param email : l'indirizzo di posta elettronica inserito dall'utente
	 * 
	 * @return true se l'email è scritta nel formato corretto; false altrimenti.
	 * @throws FormatoEmailException : eccezione che gestisce il caso in cui l'email non è specificata nel formato
	 * 									corretto
	 * */
	public static boolean checkValidateEmail(String email) throws FormatoEmailException {
		String emailPattern = "^\\S+@\\S+\\.\\S+$";
		if(!email.matches(emailPattern))
			throw new FormatoEmailException("L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).");
		return true;
	}
	
	/**
	 * Il metodo verifica se il numero di telefono fornito dall'utente
	 * è stato specificato nel corretto formato.
	 * Il metodo viene principalmente utilizzato nel caso in cui l'utente
	 * debba aggiornare il proprio recapito telefonico.
	 * 
	 * @param telefono : il numero di telefono fornito dall'utente
	 * 
	 * @return true se il numero di telefono è scritto nel formato corretto; false altrimenti.
	 * */
	public static boolean checkValidateTelefono(String telefono) {
		String telefonoPattern = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
		return telefono.matches(telefonoPattern);
	}
	
	/**
	 * Metodo costruttore della classe Cliente che permette di creare un oggetto Cliente
	 * dotato delle informazioni personali del cliente : email, nome, cognome,
	 * sesso, numero di telefono, gli indirizzi di spedizione.
	 * 
	 * @param email : l'indirizzo di posta elettronica del cliente
	 * @param nome : il nome del cliente
	 * @param cognome : il cognome del cliente
	 * @param sex : il genere del cliente
	 * @param telefono : il recapito telefonico del cliente
	 * @param indirizzi : la rubrica di indirizzi di spedizione associati al cliente
	 * 
	 * */
	public Cliente(String email, String nome, String cognome, Sesso sex, String telefono,
			ArrayList<Indirizzo> indirizzi) {
		
		this.email = email;
		this.nome = nome;
		this.cognome = cognome;
		this.sex = sex;
		this.telefono = telefono;
		this.indirizzi = indirizzi;
		this.proxyOrdini = new ArrayList<>();
	}
	
	/**
	 * Metodo costruttore della classe Cliente che permette di creare un oggetto Cliente
	 * dotato delle informazioni personali del cliente : email, nome, cognome,
	 * sesso, numero di telefono, un indirizzo di spedizione.
	 * 
	 * @param email : l'indirizzo di posta elettronica del cliente
	 * @param nome : il nome del cliente
	 * @param cognome : il cognome del cliente
	 * @param sex : il genere del cliente
	 * @param telefono : il recapito telefonico del cliente
	 * @param indirizzo : l'indirizzo di spedizione associato al cliente
	 * 
	 * */
	public Cliente(String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo) {
		
		this.email = email;
		this.nome = nome;
		this.cognome = cognome;
		this.sex = sex;
		this.telefono = telefono;
		this.indirizzi = new ArrayList<>();
		indirizzi.add(indirizzo);
		this.proxyOrdini = new ArrayList<>();
	}
	
	
	public Cliente(String email, String nome, String cognome, Sesso sex, String telefono,
			ArrayList<Indirizzo> indirizzi, OrdineDAODataSource orderDAO) {
		
		this.email = email;
		this.nome = nome;
		this.cognome = cognome;
		this.sex = sex;
		this.telefono = telefono;
		this.indirizzi = indirizzi;
		this.proxyOrdini = new ArrayList<>();
		this.orderDAO = orderDAO;
	}
	
	public Cliente(String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo, OrdineDAODataSource orderDAO) {
		
		this.email = email;
		this.nome = nome;
		this.cognome = cognome;
		this.sex = sex;
		this.telefono = telefono;
		this.indirizzi = new ArrayList<>();
		indirizzi.add(indirizzo);
		this.proxyOrdini = new ArrayList<>();
		this.orderDAO = orderDAO;
	}
	
	/**
	 * Il metodo fornisce l'indirizzo email del cliente
	 * 
	 * @return email : l'indirizzo di posta elettronica del cliente
	 * */
	public String getEmail() {
		return email;
	}

	/**
	 * Il metodo imposta l'indirizzo email del cliente
	 * 
	 * @param email : l'indirizzo di posta elettronica del cliente
	 * */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Il metodo restituisce il nome del cliente
	 * 
	 * @return nome : nome del cliente
	 * */
	public String getNome() {
		return nome;
	}
	
	/**
	 * Il metodo imposta il nome del cliente
	 * 
	 * @param nome : il nome del cliente
	 * */
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	/**
	 * Il metodo restituisce il cognome del cliente
	 * 
	 * @return cognome : cognome del cliente
	 * */
	public String getCognome() {
		return cognome;
	}
	
	/**
	 * Il metodo imposta il cognome del cliente
	 * 
	 * @param cognome : il cognome del cliente
	 * */
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	
	/**
	 * Il metodo restituisce il genere del cliente.
	 * 
	 * @return sex : il sesso del cliente
	 * */
	public Sesso getSex() {
		return sex;
	}
	
	/**
	 * Il metodo restituisce il genere del cliente in formato stringa.
	 * 
	 * @return il sesso del cliente come oggetto della classe String
	 * */
	public String getSexAsString() {
		return sex.toString();
	}
	
	/**
	 * Il metodo imposta il genere del cliente.
	 * 
	 * @param sex : il sesso del cliente
	 * */
	public void setSex(Sesso sex) {
		this.sex = sex;
	}
	
	/**
	 * Il metodo imposta il genere del cliente in formato stringa.
	 * 
	 * @param sex : il sesso del cliente fornito come 
	 * 			oggetto della classe String
	 * */
	public void setSex(String sex) {
	    if(sex.equalsIgnoreCase("F")) {
	        this.sex = Sesso.F;
	    } else if(sex.equalsIgnoreCase("M")) {
	        this.sex = Sesso.M;
	    } else {
	        throw new IllegalArgumentException("Il valore del sesso deve essere 'F' o 'M'.");
	    }
	}
	
	/**
	 * Il metodo restituisce il numero di telefono del cliente.
	 * 
	 * @return telefono : il numero di telefono del cliente
	 * */
	public String getTelefono() {
		return telefono;
	}
	
	/**
	 * Il metodo imposta il recapito telefonico del cliente.
	 * 
	 * @param telefono : il numero di telefono del cliente
	 * */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	/**
	 * Il metodo restituisce la rubrica di indirizzi di spedizione
	 * del cliente.
	 * 
	 * @return indirizzi : lista di indirizzi di spedizione associati al cliente
	 * */
	public ArrayList<Indirizzo> getIndirizzi() {
		return indirizzi;
	}
	
	/**
	 * Il metodo imposta una lista di indirizzi di spedizione al cliente.
	 * 
	 * @param indirizzi : una lista di indirizzi di spedizione da associare al cliente
	 * */
	public void setIndirizzi(ArrayList<Indirizzo> indirizzi) {
		this.indirizzi = indirizzi;
	}

	/**
	 * Il metodo fornisce il riferimento alle informazioni essenziali
	 * degli ordini effettuati dall'utente.
	 * Se non è presente questo riferimento, allora si crea tale oggetto e se ne mantiene in memoria
	 * il riferimento.
	 * 
	 * @param page rappresenta il numero di pagina desiderato
	 * @param perPage indica il numero di elementi per pagina
	 * 
	 * @return proxyOrdini : una collezione di oggetti di tipo ProxyOrdine che contiene gli
	 * 				ordini fatti dall'utente presso il negozio online.
	 * @throws ErroreTipoSpedizioneException 
	 * */
	public Collection<ProxyOrdine> mostraOrdiniCliente(int page, int perPage) throws ErroreTipoSpedizioneException {
		if(proxyOrdini == null) {
			
			try {
				Collection<ProxyOrdine> proxyOrders = orderDAO.doRetrieveOrderToUser(email, "DATAORDINE", page, perPage);
				proxyOrdini = proxyOrders;
			} catch (SQLException e) {
				System.out.println("Errore nel recupero degli ordini del cliente\n");
			}
		}
		return proxyOrdini;
	}

	/**
	 * Metodo che fornisce una stringa contenente tutte le informazioni associate al cliente.
	 * Esso si serve di un metodo helper, toStringIndirizzi(), per stampare la rubrica 
	 * di indirizzi del cliente.
	 *  
	 * @return un oggetto di tipo String contenente tutte le informazioni relative al cliente
	 * */
	
	@Override
	public String toString() {
		return "Cliente [email=" + email + ", nome=" + nome + ", cognome=" + cognome + ", sex=" + sex + ", telefono="
				+ telefono + ", indirizzi=" + toStringIndirizzi() + "]";
	}
	
	/**
	 * Metodo helper che fornisce una stringa contenente tutti gli indirizzi di spedizione
	 * che sono associati al cliente.
	 * 
	 * @return rubricaIndirizzi : un oggetto di tipo String contenente tutti gli indirizzi di spedizione
	 * 			del cliente
	 * */
	
	private String toStringIndirizzi() {
		String rubricaIndirizzi = "";
		for(Indirizzo d : this.indirizzi)
			rubricaIndirizzi = rubricaIndirizzi.concat("\n" + d.toString());
		
		return rubricaIndirizzi;
	}
	
	/**
	 * Metodo che fornisce il nominativo del cliente in formato stringa.
	 * 
	 * @return un oggetto di tipo String contenente il nome e il cognome del cliente.
	 * */
	public String toStringNominativo() {
		return "Cliente [Nome = " + nome + ", Cognome = " + cognome + "]";
	}
	
	/**
	 * Il metodo crea un nuovo oggetto Cliente che è una copia indipendente
	 * dell'oggetto originale. Sia gli attributi primitivi che gli oggetti contenuti
	 * negli ArrayList (Indirizzi e ProxyOrdini) vengono copiati in modo profondo,
	 * garantendo che le modifiche apportate alla copia non influenzino l'oggetto originale.
	 *
	 * @return clone : Una copia profonda dell'oggetto Cliente.
	 */
	@Override
	public Cliente clone() throws CloneNotSupportedException {
	    Cliente clone = new Cliente(this.email, this.nome, this.cognome, this.sex, this.telefono, new ArrayList<>());
	    
	    // Copia profonda dell'ArrayList di indirizzi
	    clone.indirizzi = new ArrayList<>();
	    for (Indirizzo indirizzo : this.indirizzi) {
	        clone.indirizzi.add(indirizzo.clone());
	    }
	    
	    // Copia profonda dell'ArrayList di proxyOrdini (se necessario)
	    clone.proxyOrdini  = new ArrayList<>();
	    for (ProxyOrdine proxyOrdine : this.proxyOrdini) {
	        clone.proxyOrdini.add(proxyOrdine.clone());
	    }
	    return clone;
	}
}