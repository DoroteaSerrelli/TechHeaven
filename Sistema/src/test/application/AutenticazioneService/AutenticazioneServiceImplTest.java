package application.AutenticazioneService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Cliente.Sesso;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Utente;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

public class AutenticazioneServiceImplTest {
	
	/**
     * Inject del service per simulare le operazioni.
     */
    @InjectMocks
    private static AutenticazioneServiceImpl autenticazioneService;

    /**
     * Mocking del dao per simulare le
     * CRUD.
     */
    
    @Mock
    private static UtenteDAODataSource utenteDAO;
    
    /**
     * Mocking del dao per simulare le
     * CRUD.
     */
    @Mock
    private static ClienteDAODataSource clienteDAO;
    /**
     * Mocking del dao per simulare le
     * CRUD.
     */
    @Mock
    private static IndirizzoDAODataSource indirizzoDAO;
	
    
    /**
	 * Il metodo effettua l'autenticazione dell'utente: verifica la corrispondenza 
	 * tra le credenziali inserite (viene effettuato l'hash della password fornita) 
	 * e le credenziali dell'utente memorizzate nel database.
	 * 
	 * @param username : l'username fornito dall'utente
	 * @param password : la password fornita dall'utente (senza che sia stato effettuato l'hashing)
	 * 
	 * @return un oggetto della classe ProxyUtente corrispondente all'utente con le credenziali 
	 * 			username e password inserite, comprensivo di 
	 * 			ruoli associati all'utente autenticato
	 * 
	 * @throws SQLException 
	 * @throws UtenteInesistenteException : lanciata nel caso in cui l'utente non è
	 * 			registrato nel sistema
	 * */
	/*@Override
	public ProxyUtente login(String username, String password) throws SQLException, UtenteInesistenteException {
		UtenteDAODataSource userDAO = new UtenteDAODataSource();
		ProxyUtente userReal;
		if((userReal = userDAO.doRetrieveProxyUserByKey(username)) == null)
			throw new UtenteInesistenteException("Username o password non valide");
		else {
			Utente client = new Utente("", password, null);
			if(!client.getPassword().equals(userReal.getPassword()))
				throw new UtenteInesistenteException("Username o password non valide");
		}
		ArrayList<Ruolo> roles = (new RuoloDAODataSource()).doRetrieveByKey(username);
		return new ProxyUtente(username, userReal.getPassword(), roles);
	}*/
	
    @BeforeAll
    public static void init() {
        utenteDAO = Mockito.mock(UtenteDAODataSource.class);
        autenticazioneService = Mockito.mock(AutenticazioneServiceImpl.class);
        clienteDAO = Mockito.mock(ClienteDAODataSource.class);
        indirizzoDAO = Mockito.mock(IndirizzoDAODataSource.class);
    }

    
	@Test
	@MethodSource("provideUtente")
	public void loginTest() throws SQLException, NoSuchAlgorithmException, UtenteInesistenteException {
		
		String username = "saraNa";
		String password = "12sara";
		
		// Crea un oggetto mock per expectedUser
	    ProxyUtente expectedUser = mock(ProxyUtente.class);
	    when(expectedUser.getUsername()).thenReturn(username);
	    when(expectedUser.getPassword()).thenReturn(password);

	    when(utenteDAO.doRetrieveProxyUserByKey(username)).thenReturn(expectedUser);

	    // When
	    ProxyUtente actualUser = autenticazioneService.login(username, password);

	    // Then
	    assertEquals(expectedUser.getUsername(), actualUser.getUsername());
	    assertEquals(expectedUser.getPassword(), actualUser.getPassword());   
		
	}
	
	/**
     * Simula i dati inviati da un metodo
     * http attraverso uno stream.
     * @return Lo stream di dati.
     * */
     
    private static Stream<Arguments> provideUtente() {
    	ArrayList<Indirizzo> indirizzi = new ArrayList<>();
    	indirizzi.add(new Indirizzo("Roma", "51", "Padova", "35100", "PD"));
    	Sesso sesso = Cliente.Sesso.valueOf("F");
        return Stream.of(Arguments.of(new Utente("saraNa", "12sara",
        		new Cliente("sara.napoli12@gmail.com", "Sara", "Napoli", sesso , "339-111-0111", 
        				indirizzi))));
       
    }
}
