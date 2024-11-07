package application.RegistrazioneControl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.Autenticazione.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.Registrazione.RegistrazioneControl.RegistrazioneController;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.RegistrazioneServiceImpl;
import application.Registrazione.RegistrazioneService.Ruolo;
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

public class RegistrazioneControllerTest {
	
	private RegistrazioneController registrazioneController;
	private UtenteDAODataSource userDAO;
	private RuoloDAODataSource roleDAO;
	private ClienteDAODataSource profileDAO;
	private IndirizzoDAODataSource addressDAO;
	private RegistrazioneServiceImpl rs;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	
	
	@BeforeEach
	public void setUp() throws IOException {
		userDAO = mock(UtenteDAODataSource.class);
		roleDAO = mock(RuoloDAODataSource.class);
		profileDAO = mock(ClienteDAODataSource.class);
		addressDAO = mock(IndirizzoDAODataSource.class);
		
		rs = mock(RegistrazioneServiceImpl.class);
		
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
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
	 * */
	
	@Test
	public void testDoPost_TC1_1_1() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "newUsername34";
		String password = "newPassword";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoUsernameException("L'username deve avere almeno lunghezza pari a 5 e contenere solo lettere."));
		
		registrazioneController.doPost(request, response);
		
		String exMessage = "L'username deve avere almeno lunghezza pari a 5 e contenere solo lettere.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+"/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_1_2() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "topolino";
		String password = "newPassword12";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		ArrayList<Ruolo> mickeyRoles = new ArrayList<>();
		mickeyRoles.add(new Ruolo("Cliente"));
		ProxyUtente topolino = new ProxyUtente("topolino", "Minnie4Ever", mickeyRoles, userDAO);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new UtentePresenteException("Non è possibile associare l'username inserita al tuo account. Riprova la registrazione "
						+ "inserendo un'altra username."));
		
		registrazioneController.doPost(request, response);
		
		String exMessage = "Non è possibile associare l'username inserita al tuo account. Riprova la registrazione "
				+ "inserendo un'altra username.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+ "/common/paginaErrore.jsp");
		
	}
	
	@Test
	public void testDoPost_TC1_1_3() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "errorNewPassword";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoPasswordException("La password deve avere almeno 5 caratteri che siano lettere e numeri."));
		
		registrazioneController.doPost(request, response);
		
		String exMessage = "La password deve avere almeno 5 caratteri che siano lettere e numeri.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+"/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_1_4() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "email@example.com";
		String nome = "Pippo2";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoNomeException("Il nome deve contenere solo lettere e, eventualmente, spazi."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "Il nome deve contenere solo lettere e, eventualmente, spazi.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+"/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_1_5() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Plu8 to";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoCognomeException("Il cognome deve contenere solo lettere e, eventualmente, spazi."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "Il cognome deve contenere solo lettere e, eventualmente, spazi.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+"/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_1_6() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= null;
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, null, telefono,
				indirizzo)).thenThrow(new FormatoGenereException("Specificare il genere."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "Specificare il genere.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+"/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_1_7() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "erroremail.example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoEmailException("L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com)."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+"/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_1_8() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "topolino.email@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		ArrayList<Ruolo> mickeyRoles = new ArrayList<>();
		mickeyRoles.add(new Ruolo("Cliente"));
		Cliente mickeyProfile = new Cliente(email, "Mickey", "Mouse", Cliente.Sesso.M, "111-222-4444", indirizzo);

		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new EmailEsistenteException("Non è possibile associare l'email inserita al tuo account. Riprova la registrazione inserendo un'altra email."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "Non è possibile associare l'email inserita al tuo account. Riprova la registrazione inserendo un'altra email.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+ "/common/paginaErrore.jsp");
		
	}
	
	
	@Test
	public void testDoPost_TC1_1_9() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111 234 4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoTelefonoException("Il formato del numero di telefono deve essere xxx-xxx-xxxx."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "Il formato del numero di telefono deve essere xxx-xxx-xxxx.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+ "/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_2_0() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "Fant8asyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoViaException("La via deve contenere solo lettere e spazi"));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "La via deve contenere solo lettere e spazi";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+ "/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_2_1() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "1RR 2";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoNumCivicoException("Il numero civico è composto da numeri e, eventualmente, una lettera."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "Il numero civico è composto da numeri e, eventualmente, una lettera.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+ "/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_2_2() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Dis5ney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoCittaException("La città deve essere composta solo da lettere e spazi."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "La città deve essere composta solo da lettere e spazi.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+ "/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_2_3() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoCAPException("Il CAP deve essere formato da 5 numeri."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "Il CAP deve essere formato da 5 numeri.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+ "/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_2_4() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "F1";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia); 
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenThrow(new FormatoProvinciaException("La provincia è composta da due lettere maiuscole."));				
		registrazioneController.doPost(request, response);
		
		String exMessage = "La provincia è composta da due lettere maiuscole.";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath()+ "/Registrazione");
		
	}
	
	@Test
	public void testDoPost_TC1_2_5() throws UtentePresenteException, EmailPresenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, FormatoUsernameException, FormatoPasswordException, FormatoEmailException, FormatoNomeException, FormatoCognomeException, FormatoGenereException, FormatoTelefonoException, EmailEsistenteException, SQLException, IOException, ServletException {
		
		registrazioneController = new RegistrazioneController(rs, userDAO, roleDAO, profileDAO, addressDAO);
		
		String username = "paperino";
		String password = "newPassword56";
		String email = "pippoemail@example.com";
		String nome = "Pippo";
		String cognome = "Pluto";
		String telefono = "111-234-4444";
		String sesso= "M";
		
		String via = "FantasyLand";
		String cv = "12";
		String citta = "Disney";
		String cap = "00000";
		String provincia = "FL";
		
		Indirizzo indirizzo = new Indirizzo(1, via, cv, citta, cap, provincia);
		
		ProxyUtente expectedUser = new ProxyUtente(username, password, userDAO);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		when(request.getParameter("email")).thenReturn(email);
		when(request.getParameter("name")).thenReturn(nome);
		when(request.getParameter("surname")).thenReturn(cognome);
		when(request.getParameter("phoneNumber")).thenReturn(telefono);
		when(request.getParameter("sesso")).thenReturn(sesso);
		
		when(request.getParameter("road")).thenReturn(via);
		when(request.getParameter("cv")).thenReturn(cv);
		when(request.getParameter("city")).thenReturn(citta);
		when(request.getParameter("cap")).thenReturn(cap);
		when(request.getParameter("province")).thenReturn(provincia);
		
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(indirizzo);
		
		when(addressDAO.doRetrieveAll("Indirizzo.via", username)).thenReturn(addresses);
		
		when(rs.registraCliente(username, password, email, nome, cognome, sesso, telefono,
				indirizzo)).thenReturn(expectedUser);
		registrazioneController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("user", expectedUser);
		verify(request).setAttribute("Indirizzi", addresses);
		verify(response).sendRedirect(request.getContextPath() + "/AreaRiservata");
		
	}
}
