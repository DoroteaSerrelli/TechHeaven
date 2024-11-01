package application.AutenticazioneControl;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.AutenticazioneService.AutenticazioneException.FormatoPasswordException;
import application.AutenticazioneService.AutenticazioneException.PasswordEsistenteException;
import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Ruolo;
import application.RegistrazioneService.Utente;
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;

public class ReimpostaPasswordControllerIntegrationTest {
	

	private ReimpostaPasswordController passwordController;
	private AutenticazioneServiceImpl loginService;
	private UtenteDAODataSource userDAO;
	private IndirizzoDAODataSource addressDAO;
	private RuoloDAODataSource roleDAO;
	private ClienteDAODataSource profileDAO;
	

	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;

	@BeforeEach
	public void setUp() throws IOException {

		userDAO = mock(UtenteDAODataSource.class);
		roleDAO = mock(RuoloDAODataSource.class);
		profileDAO = mock(ClienteDAODataSource.class);
		addressDAO = mock(IndirizzoDAODataSource.class);
		
		userDAO = mock(UtenteDAODataSource.class);
		loginService = new AutenticazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

	}

	/**
	 * TEST CASES PER REIMPOSTAZIONE PASSWORD
	 * 
	 * TC4.1_1 : username non associata ad alcun utente nel database
	 * TC4.1_2 : username corretta e formato email errato
	 * TC4.1_3 : username corretta, formato email corretto e email errata
	 * TC4.1_4 : username corretta, formato email corretto, email corretta,
	 * 			 formato nuova password errato
	 * TC4.1_5 : username corretta, formato email corretto, email corretta,
	 * 			 formato nuova password corretto, nuova password == vecchia password
	 * TC4.1_6 : username corretta, formato email corretto, email corretta,
	 * 			 formato nuova password corretto, nuova password != vecchia password
	 * 
	 * */

	@Test
	public void testDoPost_TC4_1_1() throws SQLException, ServletException, IOException {

		passwordController = new ReimpostaPasswordController(loginService, userDAO);

		String username = "nonExistentUser";
		String email = "email@example.com";

		String action = "resetPasswordRequest";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("email")).thenReturn(email);

		when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

		passwordController.doPost(request, response);

		String exMessage = "Username o email non valide";

		verify(request.getSession()).setAttribute("error",exMessage);                               
		verify(response).sendRedirect(request.getContextPath() + "/resetPassword");
	}

	@Test
	public void testDoPost_TC4_1_2() throws SQLException, ServletException, IOException {

		passwordController = new ReimpostaPasswordController(loginService, userDAO);

		String username = "saraNa";
		String oldPassword = "12sara";
		String email = "invalidEmailFormat";
		String newPassword = "newPassword123";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		Cliente profile = new Cliente("sara.napoli12@gmail.com", "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses);

		Utente existingUser = new Utente(username, oldPassword, profile);
		ProxyUtente existingProxyUser = new ProxyUtente(username, oldPassword, roles, userDAO);

		String action = "resetPasswordRequest";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("email")).thenReturn(email);

		when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(existingProxyUser);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

		passwordController.doPost(request, response);

		String exMessage = "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).";

		verify(request.getSession()).setAttribute("error",exMessage);                               
		verify(response).sendRedirect(request.getContextPath() + "/resetPassword");
	}

	@Test
	public void testDoPost_TC4_1_3() throws SQLException, ServletException, IOException {

		passwordController = new ReimpostaPasswordController(loginService, userDAO);

		String username = "saraNa";
		String oldPassword = "12sara";
		String email = "wrongEmail@example.com";
		String newPassword = "newPassword123";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		Cliente profile = new Cliente("sara.napoli12@gmail.com", "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses);

		Utente existingUser = new Utente(username, oldPassword, profile);
		ProxyUtente existingProxyUser = new ProxyUtente(username, oldPassword, roles, userDAO);

		String action = "resetPasswordRequest";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("email")).thenReturn(email);

		when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(existingProxyUser);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

		passwordController.doPost(request, response);

		String exMessage = "Username o email non valide";

		verify(request.getSession()).setAttribute("error",exMessage);                               
		verify(response).sendRedirect(request.getContextPath() + "/resetPassword");
	}

	@Test
	public void testDoPost_TC4_1_3_RichiestaResetSuccesso() throws SQLException, ServletException, IOException {

		passwordController = new ReimpostaPasswordController(loginService, userDAO);

		String username = "saraNa";
		String oldPassword = "12sara";
		String email = "sara.napoli12@gmail.com";

		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		Cliente profile = new Cliente("sara.napoli12@gmail.com", "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses);

		Utente existingUser = new Utente(username, oldPassword, profile);
		ProxyUtente existingProxyUser = new ProxyUtente(username, oldPassword, roles, userDAO);

		String action = "resetPasswordRequest";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("email")).thenReturn(email);

		when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(existingProxyUser);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

		passwordController.doPost(request, response);

		verify(request.getSession()).setAttribute("username", username);
		verify(request.getSession()).setAttribute("email", email);
		verify(response).sendRedirect(request.getContextPath() + "/protected/cliente/creaPassword.jsp");
	}
	
	
	@Test
	public void testDoPost_TC4_1_4() throws SQLException, ServletException, IOException, UtenteInesistenteException, FormatoPasswordException, PasswordEsistenteException, FormatoEmailException {

		passwordController = new ReimpostaPasswordController(loginService, userDAO);

		String username = "saraNa";
		String oldPassword = "12sara";
		String email = "sara.napoli12@gmail.com";
		String password = "sho4";


		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		Cliente profile = new Cliente("sara.napoli12@gmail.com", "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses);

		Utente existingUser = new Utente(username, oldPassword, profile);
		ProxyUtente existingProxyUser = new ProxyUtente(username, oldPassword, roles, userDAO);

		String action = "resetPassword";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("username")).thenReturn(username);
		when(request.getSession().getAttribute("email")).thenReturn(email);

		when(request.getParameter("password")).thenReturn(password);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		passwordController.doPost(request, response);
		
		assertThrows(FormatoPasswordException.class, () -> {
			loginService.resetPassword(username, email, password);	
		});
		
		
		String exMessage = "La password deve avere almeno 5 caratteri che siano lettere e numeri.";
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath() + "/protected/cliente/creaPassword.jsp");

	}
	
	
	@Test
	public void testDoPost_TC4_1_5() throws SQLException, ServletException, IOException, UtenteInesistenteException, FormatoPasswordException, PasswordEsistenteException, FormatoEmailException {

		passwordController = new ReimpostaPasswordController(loginService, userDAO);

		String username = "saraNa";
		String oldPassword = "12sara";
		String email = "sara.napoli12@gmail.com";
		String password = "12sara";


		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		Cliente profile = new Cliente("sara.napoli12@gmail.com", "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses);

		Utente existingUser = new Utente(username, oldPassword, profile);
		ProxyUtente existingProxyUser = new ProxyUtente(username, oldPassword, roles, userDAO);

		String action = "resetPassword";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("username")).thenReturn(username);
		when(request.getSession().getAttribute("email")).thenReturn(email);

		when(request.getParameter("password")).thenReturn(password);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);

		passwordController.doPost(request, response);
		
		assertThrows(PasswordEsistenteException.class, () -> {
			loginService.resetPassword(username, email, password);	
		});

		String exMessage = "Non è possibile associare questa password al tuo account. Inserisci una altra password.";
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath() + "/protected/cliente/creaPassword.jsp");

	}
	
	@Test
	public void testDoPost_TC4_1_6() throws SQLException, ServletException, IOException, UtenteInesistenteException, FormatoPasswordException, PasswordEsistenteException, FormatoEmailException {

		passwordController = new ReimpostaPasswordController(loginService, userDAO);

		String username = "saraNa";
		String oldPassword = "12sara";
		String email = "sara.napoli12@gmail.com";
		String password = "sara0";


		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(4, "Roma", "51", "Padova", "35100", "PD"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		Cliente profile = new Cliente("sara.napoli12@gmail.com", "Sara", "Napoli", Cliente.Sesso.F, "339-111-0111", addresses);

		Utente existingUser = new Utente(username, oldPassword, profile);
		ProxyUtente existingProxyUser = new ProxyUtente(username, oldPassword, roles, userDAO);

		String action = "resetPassword";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getSession().getAttribute("username")).thenReturn(username);
		when(request.getSession().getAttribute("email")).thenReturn(email);

		when(request.getParameter("password")).thenReturn(password);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		passwordController.doPost(request, response);

		verify(request.getSession()).removeAttribute("username");
		verify(request.getSession()).removeAttribute("email");
		verify(response).sendRedirect(request.getContextPath() + "/Autenticazione");
	}
	
}
