package application.RegistrazioneService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import application.Autenticazione.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.RegistrazioneServiceImpl;
import application.Registrazione.RegistrazioneService.Ruolo;
import application.Registrazione.RegistrazioneService.Utente;
import application.Registrazione.RegistrazioneService.Cliente.Sesso;
import application.Registrazione.RegistrazioneService.RegistrazioneException.EmailPresenteException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCAPException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCittaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCognomeException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoGenereException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoNomeException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoNumCivicoException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoPasswordException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoProvinciaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoUsernameException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoViaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.UtentePresenteException;
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;

public class RegistrazioneServiceImplTest {

	private RegistrazioneServiceImpl registrazioneService;
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
		registrazioneService = new RegistrazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);
	}

	/**
	 * TEST CASES PER REGISTRAZIONE DI UN CLIENTE
	 * 
	 * TC1.1_1 : username espresso nel formato errato
	 * 
	 * TC1.1_2 : username espresso nel formato corretto, ma è stato già
	 * 			 assegnato ad un altro utente nel database
	 * 
	 * TC1.1_3 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita non rispetta il formato.
	 * 
	 * TC1.1_4 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome non è specificato nel formato corretto.
	 * 
	 * TC1.1_5 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome non è specificato nel formato corretto.
	 * 
	 * TC1.1_6 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere non è stato specificato correttamente.
	 * 
	 * TC1.1_7 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email non è stata scritta nel corretto formato.
	 * 
	 * TC1.1_8 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email è scritta nel corretto formato, ma è già presente nel database, 
	 * 			 associata ad un altro utente.
	 * 
	 * TC1.1_9 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email è scritta nel corretto formato e non è presente nel database;
	 * 			 il numero di telefono non è scritto secondo il formato.
	 * 
	 * TC1.2_0 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email è scritta nel corretto formato e non è presente nel database;
	 * 			 il numero di telefono rispetta il formato.
	 * 			 Specifica indirizzo:
	 * 			 - la via non è scritta nel formato corretto;
	 * 
	 * TC1.2_1 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email è scritta nel corretto formato e non è presente nel database;
	 * 			 il numero di telefono rispetta il formato.
	 * 			 Specifica indirizzo:
	 * 			 - la via è scritta nel formato corretto;
	 * 			 - il numero civico non è scritto nel formato corretto;
	 * 
	 * TC1.2_2 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email è scritta nel corretto formato e non è presente nel database;
	 * 			 il numero di telefono rispetta il formato.
	 * 			 Specifica indirizzo:
	 * 			 - la via è scritta nel formato corretto;
	 * 			 - il numero civico è scritto nel formato corretto;
	 * 			 - la città non è espressa nel formato corretto;
	 * 
	 * TC1.2_3 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email è scritta nel corretto formato e non è presente nel database;
	 * 			 il numero di telefono rispetta il formato.
	 * 			 Specifica indirizzo:
	 * 			 - la via è scritta nel formato corretto;
	 * 			 - il numero civico è scritto nel formato corretto;
	 * 			 - la città è espressa nel formato corretto;
	 * 			 - il CAP non è definito nel formato corretto;
	 * 
	 * TC1.2_4 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email è scritta nel corretto formato e non è presente nel database;
	 * 			 il numero di telefono rispetta il formato.
	 * 			 Specifica indirizzo:
	 * 			 - la via è scritta nel formato corretto;
	 * 			 - il numero civico è scritto nel formato corretto;
	 * 			 - la città è espressa nel formato corretto;
	 * 			 - il CAP è definito nel formato corretto;
	 * 			 - la provincia non è definita nel formato corretto;
	 * 
	 * TC1.2_5 : username espresso nel formato corretto e non presente già nel database;
	 * 			 la password inserita rispetta il formato;
	 * 			 il nome è specificato nel formato corretto;
	 * 			 il cognome è specificato nel formato corretto;
	 * 			 il genere è specificato correttamente;
	 * 			 l'email è scritta nel corretto formato e non è presente nel database;
	 * 			 il numero di telefono rispetta il formato.
	 * 			 Specifica indirizzo:
	 * 			 - la via è scritta nel formato corretto;
	 * 			 - il numero civico è scritto nel formato corretto;
	 * 			 - la città è espressa nel formato corretto;
	 * 			 - il CAP è definito nel formato corretto;
	 * 			 - la provincia è definita nel formato corretto.
	 * 
	 * 
	 * */


	@Test
	public void TC1_1_1() {
		String username = "newUsername34";
		String password = "newPassword";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		// Act & Assert
		assertThrows(FormatoUsernameException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_1_2() throws SQLException {
		String username = "topolino";
		String password = "newPassword12";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		ArrayList<Ruolo> mickeyRoles = new ArrayList<>();
		mickeyRoles.add(new Ruolo("Cliente"));
		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		ProxyUtente topolino = new ProxyUtente("topolino", "Minnie4Ever", mickeyRoles, userDAO);

		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(topolino);

		// Act & Assert
		assertThrows(UtentePresenteException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_1_3() throws SQLException {
		String username = "paperino";
		String password = "errorNewPassword";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 


		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoPasswordException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_1_4() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "email@example.com";
		String nome = "Pippo2";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 


		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoNomeException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_1_5() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Plu8 to";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 


		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoCognomeException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_1_6() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= null;
		
		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 


		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoGenereException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_1_7() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "erroremail.example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 


		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoEmailException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_1_8() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "topolino.email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		ArrayList<Ruolo> mickeyRoles = new ArrayList<>();
		mickeyRoles.add(new Ruolo("Cliente"));
		Cliente mickeyProfile = new Cliente(email, "Mickey", "Mouse", Sesso.valueOf(sesso), "111-222-4444", indirizzo);

		//Utente topolino = new Utente("topolino", "Minnie4Ever", mickeyProfile);


		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);
		Mockito.when(profileDAO.doRetrieveByKey(email)).thenReturn(mickeyProfile);

		// Act & Assert
		assertThrows(EmailEsistenteException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_1_9() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String errorTelefono = "111 234 4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);
		Mockito.when(profileDAO.doRetrieveByKey(email)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoTelefonoException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, errorTelefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_2_0() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "Fant8asyLand", "12", "Disney", "00000", "FL"); 

		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);
		Mockito.when(profileDAO.doRetrieveByKey(email)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoViaException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_2_1() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "1RR 2", "Disney", "00000", "FL"); 

		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);
		Mockito.when(profileDAO.doRetrieveByKey(email)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoNumCivicoException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_2_2() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Dis5ney", "00000", "FL"); 

		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);
		Mockito.when(profileDAO.doRetrieveByKey(email)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoCittaException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_2_3() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "000", "FL"); 

		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);
		Mockito.when(profileDAO.doRetrieveByKey(email)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoCAPException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_2_4() throws SQLException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "Fl"); 

		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);
		Mockito.when(profileDAO.doRetrieveByKey(email)).thenReturn(null);

		// Act & Assert
		assertThrows(FormatoProvinciaException.class, () -> {
			registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono,
					indirizzo);
		});
	}

	@Test
	public void TC1_2_5() throws SQLException, UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException {
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";

		Indirizzo indirizzo = new Indirizzo(1, "FantasyLand", "12", "Disney", "00000", "FL"); 

		Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);
		Mockito.when(profileDAO.doRetrieveByKey(email)).thenReturn(null);

		ProxyUtente result = registrazioneService.registraCliente(username, password, email, nome, cognome, sesso, telefono, indirizzo);

		//Assert
		assertNotNull(result);
		assertEquals(username, result.getUsername());
		assertEquals(hashPassword(password).toString(), result.getPassword());
		
		//Verify
		verify(profileDAO).doSave(any(Cliente.class));
		verify(userDAO).doSave(any(Utente.class));
		verify(roleDAO).doSave(any(Utente.class), any(Ruolo.class));
		verify(addressDAO).doSave(any(Indirizzo.class), eq(username));

	}
	
	private StringBuilder hashPassword(String password) {
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
		return hashString;
	}

}
