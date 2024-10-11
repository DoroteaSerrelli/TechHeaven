package application.AutenticazioneService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.AutenticazioneService.AutenticazioneException.FormatoPasswordException;
import application.AutenticazioneService.AutenticazioneException.PasswordEsistenteException;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Ruolo;
import application.RegistrazioneService.Utente;
import application.RegistrazioneService.Indirizzo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

public class AutenticazioneServiceImplTest {

	private AutenticazioneServiceImpl autenticazioneService;
	private UtenteDAODataSource userDAO;
	private RuoloDAODataSource roleDAO;
	private ClienteDAODataSource profileDAO;
	private IndirizzoDAODataSource addressDAO;

	@BeforeEach
	public void setUp() {
		userDAO = Mockito.mock(UtenteDAODataSource.class);
		roleDAO = Mockito.mock(RuoloDAODataSource.class);
		profileDAO = Mockito.mock(ClienteDAODataSource.class);
		addressDAO = Mockito.mock(IndirizzoDAODataSource.class);
		autenticazioneService = new AutenticazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);
	}

	/**
	 * TEST CASES PER LOGIN (SINGOLO RUOLO, OVVERO CLIENTE)
	 * 
	 * TC2.1_1 : username e password errate
	 * TC2.1_2 : username corretta e password errata
	 * TC2.1_3 : username e password corrette
	 * */
	
	@Test
	public void TC2_1_1() throws SQLException {
		// Arrange
		String username = "nonExistentUser";
		String password = "anyPassword";

		// Simula il comportamento del DAO per non trovare l'utente
		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

		// Act & Assert
		assertThrows(UtenteInesistenteException.class, () -> {
			autenticazioneService.login(username, password);
		});
	}
	
	@Test
	public void TC2_1_2() throws SQLException {
		// Arrange
		String username = "saraNa";
		String correctPassword = "12sara";
		String wrongPassword = "wrongPassword";

		ProxyUtente existingUser = new ProxyUtente(username, correctPassword, new ArrayList<>());

		// Simula il comportamento del DAO per restituire l'utente corretto
		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(existingUser);

		// Act & Assert
		assertThrows(UtenteInesistenteException.class, () -> {
			autenticazioneService.login(username, wrongPassword);
		});
	}
	
	@Test
	public void TC2_1_3() throws SQLException, UtenteInesistenteException {
		// Arrange
		String username = "saraNa";
		String password = "12sara";


		StringBuilder hashString = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			for (int i = 0; i < bytes.length; i++) {
				hashString.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toLowerCase(), 1, 3);
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}

		ProxyUtente expectedUser = new ProxyUtente(username, password, new ArrayList<>());

		// Simula il comportamento del DAO per restituire l'utente corretto
		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(expectedUser);
		Mockito.when(roleDAO.doRetrieveByKey(username)).thenReturn(new ArrayList<>());

		// Act
		ProxyUtente actualUser = autenticazioneService.login(username, password);

		// Assert
		assertNotNull(actualUser);
		assertEquals(expectedUser.getUsername(), actualUser.getUsername());
		assertEquals(expectedUser.getPassword(), actualUser.getPassword());
	}
	
	
	/**
	 * TEST CASES PER LOGIN (MULTI-RUOLO)
	 * 
	 * TC3.1_1 : username errata
	 * TC3.1_2 : username corretta e password errata
	 * TC3.1_3 : username e password corrette, formato ruolo errato
	 * TC3.1_4 : username e password corrette, formato ruolo corretto, 
	 * 			 ruolo indicato non associato a username
	 * TC3.1_5 : username e password corrette, formato ruolo corretto, 
	 * 			 ruolo indicato associato a username
	 * */
	
	@Test
	public void TC3_1_1() throws SQLException {
		// Arrange
		String username = "nonExistentUser";
		String password = "anyPassword";

		// Simula il comportamento del DAO per non trovare l'utente
		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

		// Act & Assert
		assertThrows(UtenteInesistenteException.class, () -> {
			autenticazioneService.login(username, password);
		});
	}
	
	@Test
	public void TC3_1_2() throws SQLException {
		// Arrange
		String username = "fulvio";
		String correctPassword = "fulvio0";
		String wrongPassword = "wrongPassword";

		ProxyUtente existingUser = new ProxyUtente(username, correctPassword, new ArrayList<>());

		// Simula il comportamento del DAO per restituire l'utente corretto
		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(existingUser);

		// Act & Assert
		assertThrows(UtenteInesistenteException.class, () -> {
			autenticazioneService.login(username, wrongPassword);
		});
	}
	
	@Test
	public void TC3_1_3() throws SQLException, UtenteInesistenteException {
		// Arrange
		String username = "fulvio";
		String password = "fulvio0";
		Ruolo errorRole = new Ruolo("RuoloErrato");
		ArrayList<Ruolo> acceptedRoles = new ArrayList<>();
		acceptedRoles.add(new Ruolo("Cliente"));
		acceptedRoles.add(new Ruolo("GestoreOrdini"));
		acceptedRoles.add(new Ruolo("GestoreCatalogo"));
		

		StringBuilder hashString = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			for (int i = 0; i < bytes.length; i++) {
				hashString.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toLowerCase(), 1, 3);
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}

		ProxyUtente expectedUser = new ProxyUtente(username, password, new ArrayList<>());

		// Simula il comportamento del DAO per restituire l'utente corretto
		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(expectedUser);
		Mockito.when(roleDAO.doRetrieveByKey(username)).thenReturn(new ArrayList<>());

		// Act
		ProxyUtente actualUser = autenticazioneService.login(username, password);

		// Assert
		assertNotNull(actualUser);
		assertEquals(expectedUser.getUsername(), actualUser.getUsername());
		assertEquals(expectedUser.getPassword(), actualUser.getPassword());
		assertFalse(acceptedRoles.contains(errorRole));
		
	}
	
	@Test
	public void TC3_1_4() throws SQLException, UtenteInesistenteException {
		// Arrange
		String username = "fulvio";
		String password = "fulvio0";
		Ruolo selectedRole = new Ruolo("GestoreCatalogo"); //Fulvio è un cliente ed un gestore ordini
		ArrayList<Ruolo> acceptedRoles = new ArrayList<>();
		acceptedRoles.add(new Ruolo("Cliente"));
		acceptedRoles.add(new Ruolo("GestoreOrdini"));
		acceptedRoles.add(new Ruolo("GestoreCatalogo"));


		StringBuilder hashString = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			for (int i = 0; i < bytes.length; i++) {
				hashString.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toLowerCase(), 1, 3);
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}

		ProxyUtente expectedUser = new ProxyUtente(username, password, new ArrayList<>());

		// Simula il comportamento del DAO per restituire l'utente corretto
		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(expectedUser);
		Mockito.when(roleDAO.doRetrieveByKey(username)).thenReturn(new ArrayList<>());

		// Act
		ProxyUtente actualUser = autenticazioneService.login(username, password);

		// Assert
		assertNotNull(actualUser);
		assertEquals(expectedUser.getUsername(), actualUser.getUsername());
		assertEquals(expectedUser.getPassword(), actualUser.getPassword());
		assertTrue(acceptedRoles.contains(selectedRole));
		assertFalse(actualUser.getRuoli().contains(selectedRole));
	}
	
	@Test
	public void TC3_1_5() throws SQLException, UtenteInesistenteException {
	    // Arrange
	    String username = "fulvio";
	    String password = "fulvio0";
	    Ruolo selectedRole = new Ruolo("GestoreOrdini");
	    ArrayList<Ruolo> acceptedRoles = new ArrayList<>();
	    acceptedRoles.add(new Ruolo("Cliente"));
	    acceptedRoles.add(new Ruolo("GestoreOrdini"));
	    acceptedRoles.add(new Ruolo("GestoreCatalogo"));

	    // Hash della password
	    StringBuilder hashString = new StringBuilder();
	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA-512");
	        byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
	        for (int i = 0; i < bytes.length; i++) {
	            hashString.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toLowerCase(), 1, 3);
	        }
	    } catch (NoSuchAlgorithmException e) {
	        System.out.println(e);
	    }

	    ProxyUtente expectedUser = new ProxyUtente(username, password, new ArrayList<>());

	    // Simula il comportamento del DAO per restituire l'utente corretto
	    Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(expectedUser);
	    
	    // Simula il recupero dei ruoli
	    ArrayList<Ruolo> userRoles = new ArrayList<>();
	    userRoles.add(new Ruolo("Cliente"));
	    userRoles.add(selectedRole);
	    Mockito.when(roleDAO.doRetrieveByKey(username)).thenReturn(userRoles);

	    // Act
	    ProxyUtente actualUser = autenticazioneService.login(username, password);

	    // Assert
	    assertNotNull(actualUser);
	    assertEquals(expectedUser.getUsername(), actualUser.getUsername());
	    assertEquals(expectedUser.getPassword(), actualUser.getPassword());
	    assertTrue(acceptedRoles.contains(selectedRole));
	    
	    // Verifica i ruoli effettivi dell'utente
	    assertTrue(actualUser.getRuoli().contains(selectedRole));
	}

	
	
	/**
	 * TEST CASES PER REIMPOSTAZIONE PASSWORD
	 * 
	 * TC4.1_1 : username errata
	 * TC4.1_2 : username corretta e formato email errato
	 * TC4.1_3 : username corretta, formato email corretto e email errata
	 * TC4.1_4 : username corretta, formato email corretto, email corretta,
	 * 			 formato nuova password errato
	 * TC4.1_5 : username corretta, formato email corretto, email corretta,
	 * 			 formato nuova password corretto, nuova password == vecchia password
	 * TC4.1_6 : username corretta, formato email corretto, email corretta,
	 * 			 formato nuova password corretto, nuova password != vecchia password
	 * @throws SQLException 
	 * 
	 * */
	
    @Test
    public void TC4_1_1() throws SQLException {
        String username = "nonExistentUser";
        String email = "email@example.com";
        String newPassword = "newPassword123";

        // Simula il comportamento del DAO per non trovare l'utente
        Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UtenteInesistenteException.class, () -> {
            autenticazioneService.resetPassword(username, email, newPassword);
        });
    }

    @Test
    public void TC4_1_2() throws SQLException {
        String username = "saraNa";
        String oldPassword = "12sara";
        String email = "invalidEmailFormat";
        String newPassword = "newPassword123";

        Utente existingUser = new Utente(username, oldPassword, null);
        Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

        // Act & Assert
        assertThrows(FormatoEmailException.class, () -> {
            autenticazioneService.resetPassword(username, email, newPassword);
        });
    }

    @Test
    public void TC4_1_3() throws SQLException {
        String username = "saraNa";
        String oldPassword = "12sara";
        String email = "wrongEmail@example.com"; //email corretta : sara.napoli12@gmail.com
        String newPassword = "newPassword123";
        
        ArrayList<Indirizzo> addresses = new ArrayList<>();
        addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));
        
        Utente existingUser = new Utente(username, oldPassword, new Cliente("sara.napoli12@gmail.com", "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses));
        Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

        // Act & Assert
        assertThrows(UtenteInesistenteException.class, () -> {
            autenticazioneService.resetPassword(username, email, newPassword);
        });
    }

    @Test
    public void TC4_1_4() throws SQLException {
    	String username = "saraNa";
        String oldPassword = "12sara";
        String email = "sara.napoli12@gmail.com";
        String newPassword = "sho4"; // Formato non valido

        ArrayList<Indirizzo> addresses = new ArrayList<>();
        addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));
        
        Utente existingUser = new Utente(username, oldPassword, new Cliente(email, "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses));
        Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

        // Act & Assert
        assertThrows(FormatoPasswordException.class, () -> {
            autenticazioneService.resetPassword(username, email, newPassword);
        });
    }

    @Test
    public void TC4_1_5_passwordUguale() throws SQLException {
    	String username = "saraNa";
        String oldPassword = "12sara";
        String email = "sara.napoli12@gmail.com";
        String newPassword = oldPassword;

        ArrayList<Indirizzo> addresses = new ArrayList<>();
        addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));
        
        Utente existingUser = new Utente(username, oldPassword, new Cliente(email, "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses));
        Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

        // Act & Assert
        assertThrows(PasswordEsistenteException.class, () -> {
            autenticazioneService.resetPassword(username, email, newPassword);
        });
    }

    @Test
    public void TC4_1_6_resetPasswordSuccesso() throws SQLException, UtenteInesistenteException, FormatoPasswordException, PasswordEsistenteException {
    	String username = "saraNa";
        String oldPassword = "12sara";
        String email = "sara.napoli12@gmail.com";
        String newPassword = "sara0";

        ArrayList<Indirizzo> addresses = new ArrayList<>();
        addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));
        
        Utente existingUser = new Utente(username, oldPassword, new Cliente(email, "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses));
        Mockito.when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

        // Simula la reimpostazione della password
        Mockito.doNothing().when(userDAO).doResetPassword(username, newPassword);

        // Act
        assertDoesNotThrow(() -> {
            autenticazioneService.resetPassword(username, email, newPassword);
        });
        
        StringBuilder hashString = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bytes = md.digest(newPassword.getBytes(StandardCharsets.UTF_8));
			for (int i = 0; i < bytes.length; i++) {
				hashString.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toLowerCase(), 1, 3);
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}

        
        // Verifica che il metodo di reset della password sia stato chiamato
        Mockito.verify(userDAO).doResetPassword(username, hashString.toString());
    }
	

}
