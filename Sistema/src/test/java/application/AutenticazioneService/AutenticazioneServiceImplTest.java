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
import application.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.AutenticazioneService.AutenticazioneException.PasswordEsistenteException;
import application.AutenticazioneService.AutenticazioneException.ProfiloInesistenteException;
import application.AutenticazioneService.AutenticazioneException.TelefonoEsistenteException;
import application.AutenticazioneService.AutenticazioneException.ErroreParametroException;
import application.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
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
		String username = "fulvioGestoreOrdini";
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
		String username = "fulvioGestoreOrdini";
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
		String username = "fulvioGestoreOrdini";
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
		String username = "fulvioGestoreOrdini";
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
	public void TC4_1_5() throws SQLException {
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
	public void TC4_1_6() throws SQLException, UtenteInesistenteException, FormatoPasswordException, PasswordEsistenteException {
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


	/**
	 * TEST CASES PER MODIFICA PROFILO : MODIFICA EMAIL
	 * 
	 * TC5.1_1 : informazione da modificare non specificata correttamente
	 * TC5.1_2 : informazione da modificare != EMAIL
	 * TC5.1_3 : informazione da modificare == EMAIL, formato dell'email non corretto
	 * TC5.1_4 : informazione da modificare == EMAIL, formato dell'email corretto,
	 * 			 nuova email == vecchia email
	 * TC5.1_5 : informazione da modificare == EMAIL, formato dell'email corretto,
	 * 			 nuova email != vecchia email
	 * 
	 * @throws SQLException 
	 * 
	 * */


	@Test
	public void testAggiornaEmail_ProfiloNonAssociatoAUtente() throws SQLException {
		String username = "user";
		String password = "password";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);

		Mockito.when(userDAO.doRetrieveFullUserByKey(user.getUsername())).thenReturn(null);

		// Act & Assert
		assertThrows(ProfiloInesistenteException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, "EMAIL", "new.email@example.com");
		});
	}

	@Test
	public void TC5_1_1() throws SQLException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));   		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);

		String information = "INFOERRATA"; //information deve essere EMAIL oppure TELEFONO

		// Act & Assert
		assertThrows(InformazioneDaModificareException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "");
		});
	}

	@Test
	public void TC5_1_2() throws SQLException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		
		String information = "TELEFONO"; //information deve essere EMAIL

		// Act & Assert
		assertThrows(ErroreParametroException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "new.email@example.com");
		});
	}



	@Test
	public void TC5_1_3() throws SQLException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "377-694-3946",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		Utente existingUser = new Utente(username, password, profile);

		String information = "EMAIL";

		Mockito.when(userDAO.doRetrieveFullUserByKey(user.getUsername())).thenReturn(existingUser);

		// Act & Assert
		assertThrows(FormatoEmailException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "invalid-email-format");
		});
	}

	@Test
	public void TC5_1_4() throws SQLException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "377-694-3946",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		Utente existingUser = new Utente(username, password, profile);

		String information = "EMAIL";
		Mockito.when(userDAO.doRetrieveFullUserByKey(user.getUsername())).thenReturn(existingUser);

		// Act & Assert
		assertThrows(EmailEsistenteException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "doroteaserrelli@gmail.com");
		});
	}

	@Test
	public void TC5_1_5() throws SQLException, FormatoEmailException, ProfiloInesistenteException, EmailEsistenteException, TelefonoEsistenteException, FormatoTelefonoException, InformazioneDaModificareException, ErroreParametroException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "377-694-3946",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String information = "EMAIL";
		Mockito.when(userDAO.doRetrieveFullUserByKey(user.getUsername())).thenReturn(existingUser);
		String newEmail = "dorotea.serrelli@gmail.com";

		Mockito.when(profileDAO.updateEmail("doroteaserrelli@gmail.com", newEmail)).thenReturn(true);

		// Act
		ProxyUtente updatedUser = autenticazioneService.aggiornaProfilo(user, information, newEmail);


		// Assert
		assertNotNull(updatedUser);
		assertEquals(username, updatedUser.getUsername());
		assertEquals(newEmail, updatedUser.mostraUtente().getProfile().getEmail());
		Mockito.verify(profileDAO).updateEmail("doroteaserrelli@gmail.com", newEmail);

	}

	/**
	 * TEST CASES PER MODIFICA PROFILO : MODIFICA NUMERO DI TELEFONO
	 * 
	 * TC5.2_1 : informazione da modificare non specificata correttamente
	 * TC5.2_2 : informazione da modificare != TELEFONO
	 * TC5.2_3 : informazione da modificare == TELEFONO, formato del numero di telefono non corretto
	 * TC5.2_4 : informazione da modificare == TELEFONO, formato del numero di telefono corretto,
	 * 			 nuovo numero di telefono == vecchio numero di telefono
	 * TC5.2_5 : informazione da modificare = TELEFONO, formato del numero di telefono corretto,
	 * 			 nuovo numero di telefono != vecchio numero di telefono
	 * 
	 * @throws SQLException 
	 * 
	 * */

	@Test
	public void testAggiornaTelefono_ProfiloNonAssociatoAUtente() throws SQLException {
		String username = "user";
		String password = "password";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));
		
		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);

		// Act & Assert
		assertThrows(ProfiloInesistenteException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, "TELEFONO", "000-000-0000");
		});
	}

	@Test
	public void TC5_2_1() throws SQLException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		
		String information = "INFOERRATA"; //information deve essere EMAIL oppure TELEFONO

		// Act & Assert
		assertThrows(InformazioneDaModificareException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "");
		});
	}

	@Test
	public void TC5_2_2() throws SQLException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		
		String information = "EMAIL"; //information deve essere TELEFONO

		// Act & Assert
		assertThrows(ErroreParametroException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "111-111-1111");
		});
	}



	@Test
	public void TC5_2_3() throws SQLException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		Utente existingUser = new Utente(username, password, profile);

		String information = "TELEFONO";

		Mockito.when(userDAO.doRetrieveFullUserByKey(user.getUsername())).thenReturn(existingUser);

		// Act & Assert
		assertThrows(FormatoTelefonoException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "invalid-phone-format");
		});
	}

	@Test
	public void TC5_2_4() throws SQLException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		Utente existingUser = new Utente(username, password, profile);

		String information = "TELEFONO";
		Mockito.when(userDAO.doRetrieveFullUserByKey(user.getUsername())).thenReturn(existingUser);

		// Act & Assert
		assertThrows(TelefonoEsistenteException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "000-000-0000");
		});
	}

	@Test
	public void TC5_2_5() throws SQLException, FormatoEmailException, ProfiloInesistenteException, EmailEsistenteException, TelefonoEsistenteException, FormatoTelefonoException, InformazioneDaModificareException, ErroreParametroException {
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		Utente existingUser = new Utente(username, password, profile);

		String information = "TELEFONO";
		Mockito.when(userDAO.doRetrieveFullUserByKey(user.getUsername())).thenReturn(existingUser);
		String newTelephone = "111-111-1111";
		Mockito.when(profileDAO.updateTelephone("doroteaserrelli@gmail.com", newTelephone)).thenReturn(true);

		// Act
		ProxyUtente updatedUser = autenticazioneService.aggiornaProfilo(user, information, newTelephone);

		// Assert
		assertNotNull(updatedUser);
		assertEquals(username, updatedUser.getUsername());
		assertEquals(newTelephone, updatedUser.mostraUtente().getProfile().getTelefono());
		Mockito.verify(profileDAO).updateTelephone("doroteaserrelli@gmail.com", newTelephone);

	}
	
	/**
	 * TEST CASES PEZR MODIFICA PROFILO : RUBRICA INDIRIZZI
	 * */
	
	/**
	 * TEST CASES PER AGGIUNTA DI UN INDIRIZZO IN RUBRICA
	 * 
	 * TC6_1.1_1 : informazione da modificare non specificata correttamente
	 * TC6_1.1_2 : informazione da modificare != AGGIUNGERE-INDIRIZZO
	 * TC6_1.1_3 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa non correttamente
	 * TC6_1.1_4 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico non espresso correttamente 
	 * TC6_1.1_5 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa non correttamente
	 * TC6_1.1_6 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa correttamente, CAP non espresso correttamente
	 * TC6_1.1_7 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa correttamente, CAP espresso correttamente,
	 * 				provincia espressa non correttamente
	 * TC6_1.1_8 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa correttamente, CAP espresso correttamente,
	 * 				provincia espressa correttamente; l'indirizzo inserito è già 
	 * 				presente in rubrica
	 * TC6_1.1_9 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa correttamente, CAP espresso correttamente,
	 * 				provincia espressa correttamente; l'indirizzo inserito non è  
	 * 				presente in rubrica
	 * 
	 * */
	
	@Test
	public void testAggiungiIndirizzo_ProfiloNonAssociatoAUtente() throws SQLException {
		String username = "user";
		String password = "password";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));
		
		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);

		// Act & Assert
		assertThrows(ProfiloInesistenteException.class, () -> {
			autenticazioneService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", null);
		});
	}
	
	@Test
	public void TC6_1_1_1() throws SQLException {
		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles);
		
		String information = "INFOERRATA"; 
		//information deve essere AGGIUNGERE-INDIRIZZO, RIMUOVERE-INDIRIZZO, AGGIORNARE-INDIRIZZO		// Act & Assert
		
		assertThrows(InformazioneDaModificareException.class, () -> {
			autenticazioneService.aggiornaProfilo(user, information, "");
		});
	}

}
