package application.AutenticazioneControl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.Autenticazione.AutenticazioneControl.AutenticazioneController;
import application.Autenticazione.AutenticazioneControl.UpdateProfileController;
import application.Autenticazione.AutenticazioneService.AutenticazioneServiceImpl;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.ProfiloInesistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.TelefonoEsistenteException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.Ruolo;
import application.Registrazione.RegistrazioneService.Utente;
import storage.AutenticazioneDAO.UtenteDAODataSource;

public class UpdateProfileControllerTest {

	private UpdateProfileController profiloController;
	private AutenticazioneServiceImpl loginService;
	private AutenticazioneController loginController;
	private UtenteDAODataSource userDAO;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;

	@BeforeEach
	public void setUp() throws IOException {
		userDAO = mock(UtenteDAODataSource.class);
		loginService = mock(AutenticazioneServiceImpl.class);
		loginController = mock(AutenticazioneController.class);

		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");

	}


	/**
	 * TEST CASES PER MODIFICA PROFILO : MODIFICA EMAIL
	 * 
	 * TC5.1_1 : informazione da modificare non specificata correttamente
	 * TC5.1_2 : informazione da modificare == EMAIL, formato dell'email non corretto
	 * TC5.1_3 : informazione da modificare == EMAIL, formato dell'email corretto,
	 * 			 nuova email == vecchia email
	 * TC5.1_4 : informazione da modificare == EMAIL, formato dell'email corretto,
	 * 			 nuova email != vecchia email
	 * 
	 * */

	@Test
	public void testDoPost_TC5_1_1() throws ServletException, IOException {

		profiloController = new UpdateProfileController(userDAO, loginController, loginService);

		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));   		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);

		String information = "INFOERRATA"; //information deve essere EMAIL oppure TELEFONO
		String updated_email = "";
		String updated_tel = "";

		when(request.getParameter("email")).thenReturn(updated_email);
		when(request.getParameter("telefono")).thenReturn(updated_tel);
		when(request.getParameter("information")).thenReturn(information);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		profiloController.doPost(request, response);

		String exMessage = "Seleziona un'informazione da modificare : telefono o email.";

		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect("/UpdateUserInfo");

	}

	@Test
	public void testDoPost_TC5_1_2() throws ServletException, IOException, SQLException {

		profiloController = new UpdateProfileController(userDAO, loginController, loginService);
		
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		ProxyUtente proxyUser = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String information = "EMAIL"; //information deve essere EMAIL oppure TELEFONO
		String updated_email = "invalid-email-format";
		String updated_tel = "";

		when(request.getParameter("email")).thenReturn(updated_email);
		when(request.getParameter("telefono")).thenReturn(updated_tel);
		when(request.getParameter("information")).thenReturn(information);
		when(request.getSession().getAttribute("user")).thenReturn(proxyUser);

		
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("updateUserInfo")).thenReturn(dispatcher);

		profiloController.doPost(request, response);

		String exMessage = "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).";
		
		verify(request.getSession()).setAttribute("field", "email");
		verify(request.getSession()).getAttribute("user");
		verify(request).setAttribute("error", exMessage);
		verify(request.getRequestDispatcher("updateUserInfo")).forward(request, response);

	}
	
	@Test
	public void testDoPost_TC5_1_3() throws ServletException, IOException, SQLException {

		profiloController = new UpdateProfileController(userDAO, loginController, loginService);
		
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		ProxyUtente proxyUser = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String information = "EMAIL"; //information deve essere EMAIL oppure TELEFONO
		String updated_email = "doroteaserrelli@gmail.com";
		String updated_tel = "";

		when(request.getParameter("email")).thenReturn(updated_email);
		when(request.getParameter("telefono")).thenReturn(updated_tel);
		when(request.getParameter("information")).thenReturn(information);
		when(request.getSession().getAttribute("user")).thenReturn(proxyUser);

		
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("updateUserInfo")).thenReturn(dispatcher);

		profiloController.doPost(request, response);

		String exMessage = "Non è possibile associare questa email al tuo account. Inserisci una altra email.";
		
		verify(request.getSession()).setAttribute("field", "email");
		verify(request.getSession()).getAttribute("user");
		verify(request).setAttribute("error", exMessage);
		verify(request.getRequestDispatcher("updateUserInfo")).forward(request, response);

	}

	@Test
	public void testDoPost_TC5_1_4() throws ServletException, IOException, SQLException, FormatoEmailException, ProfiloInesistenteException, EmailEsistenteException, TelefonoEsistenteException, FormatoTelefonoException, InformazioneDaModificareException {

		profiloController = new UpdateProfileController(userDAO, loginController, loginService);
		
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		ProxyUtente proxyUser = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String information = "EMAIL"; //information deve essere EMAIL oppure TELEFONO
		String updated_email = "dorotea.serrelli@gmail.com";
		String updated_tel = "";

		when(request.getParameter("email")).thenReturn(updated_email);
		when(request.getParameter("telefono")).thenReturn(updated_tel);
		when(request.getParameter("information")).thenReturn(information);
		when(request.getSession().getAttribute("user")).thenReturn(proxyUser);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		when(loginService.aggiornaProfilo(proxyUser, "EMAIL", updated_email)).thenReturn(proxyUser);
		
		Cliente profileUpdated = new Cliente("dorotea.serrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses); 
		
		Utente updatedUser = new Utente(username, password, profileUpdated);

		
		profiloController.doPost(request, response);

		
		verify(request.getSession()).setAttribute("field", "email");
		verify(request.getSession()).getAttribute("user");
		verify(request.getSession()).setAttribute("user", proxyUser);
		verify(response).sendRedirect(request.getContextPath()+"/AreaRiservata");

	}
	
	
	/**
	 * TEST CASES PER MODIFICA PROFILO : MODIFICA NUMERO DI TELEFONO
	 * 
	 * TC5.2_1 : informazione da modificare non specificata correttamente
	 * TC5.2_2 : informazione da modificare == TELEFONO, formato del numero di telefono non corretto
	 * TC5.2_3 : informazione da modificare == TELEFONO, formato del numero di telefono corretto,
	 * 			 nuovo numero di telefono == vecchio numero di telefono
	 * TC5.2_4 : informazione da modificare = TELEFONO, formato del numero di telefono corretto,
	 * 			 nuovo numero di telefono != vecchio numero di telefono
	 * 
	 * 
	 * */

	@Test
	public void testDoPost_TC5_2_1() throws ServletException, IOException {

		profiloController = new UpdateProfileController(userDAO, loginController, loginService);

		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));   		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);

		String information = "INFOERRATA"; //information deve essere EMAIL oppure TELEFONO
		String updated_email = "";
		String updated_tel = "";

		when(request.getParameter("email")).thenReturn(updated_email);
		when(request.getParameter("telefono")).thenReturn(updated_tel);
		when(request.getParameter("information")).thenReturn(information);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		profiloController.doPost(request, response);

		String exMessage = "Seleziona un'informazione da modificare : telefono o email.";

		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect("/UpdateUserInfo");

	}

	@Test
	public void testDoPost_TC5_2_2() throws ServletException, IOException, SQLException {

		profiloController = new UpdateProfileController(userDAO, loginController, loginService);
		
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		ProxyUtente proxyUser = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String information = "telefono"; //information deve essere EMAIL oppure TELEFONO
		String updated_tel = "invalid-phone-format";
		String  updated_email = "";

		when(request.getParameter("email")).thenReturn(updated_email);
		when(request.getParameter("telefono")).thenReturn(updated_tel);
		when(request.getParameter("information")).thenReturn(information);
		when(request.getSession().getAttribute("user")).thenReturn(proxyUser);

		
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("updateUserInfo")).thenReturn(dispatcher);

		profiloController.doPost(request, response);

		String exMessage = "Il formato del numero di telefono deve essere xxx-xxx-xxxx.";
		
		verify(request.getSession()).setAttribute("field", "telefono");
		verify(request.getSession()).getAttribute("user");
		verify(request).setAttribute("error", exMessage);
		verify(request.getRequestDispatcher("updateUserInfo")).forward(request, response);

	}
	
	@Test
	public void testDoPost_TC5_2_3() throws ServletException, IOException, SQLException {

		profiloController = new UpdateProfileController(userDAO, loginController, loginService);
		
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		ProxyUtente proxyUser = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String information = "TELEFONO"; //information deve essere EMAIL oppure TELEFONO
		String updated_email = "";
		String updated_tel = "000-000-0000";

		when(request.getParameter("email")).thenReturn(updated_email);
		when(request.getParameter("telefono")).thenReturn(updated_tel);
		when(request.getParameter("information")).thenReturn(information);
		when(request.getSession().getAttribute("user")).thenReturn(proxyUser);

		
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("updateUserInfo")).thenReturn(dispatcher);

		profiloController.doPost(request, response);

		String exMessage = "Non è possibile associare questo recapito telefonico al tuo account. Inserisci un altro numero di telefono.";
		
		verify(request.getSession()).setAttribute("field", "telefono");
		verify(request.getSession()).getAttribute("user");
		verify(request).setAttribute("error", exMessage);
		verify(request.getRequestDispatcher("updateUserInfo")).forward(request, response);

	}

	@Test
	public void testDoPost_TC5_2_4() throws ServletException, IOException, SQLException, FormatoEmailException, ProfiloInesistenteException, EmailEsistenteException, TelefonoEsistenteException, FormatoTelefonoException, InformazioneDaModificareException {

		profiloController = new UpdateProfileController(userDAO, loginController, loginService);
		
		String username = "dorotea";
		String password = "dorotea0";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "21", "Avellino", "83100", "AV"));


		Cliente profile = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "000-000-0000",
				addresses);     		

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));

		ProxyUtente proxyUser = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String information = "TELEFONO"; //information deve essere EMAIL oppure TELEFONO
		String updated_email = "";
		String updated_tel = "111-111-1111";

		when(request.getParameter("email")).thenReturn(updated_email);
		when(request.getParameter("telefono")).thenReturn(updated_tel);
		when(request.getParameter("information")).thenReturn(information);
		when(request.getSession().getAttribute("user")).thenReturn(proxyUser);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		when(loginService.aggiornaProfilo(proxyUser, "TELEFONO", updated_tel)).thenReturn(proxyUser);
		
		Cliente profileUpdated = new Cliente("doroteaserrelli@gmail.com", "Dorotea", "Serrelli", Cliente.Sesso.F, "111-111-1111",
				addresses); 
		
		Utente updatedUser = new Utente(username, password, profileUpdated);

		profiloController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("field", "telefono");
		verify(request.getSession()).getAttribute("user");
		verify(request.getSession()).setAttribute("user", proxyUser);
		verify(response).sendRedirect(request.getContextPath()+"/AreaRiservata");

	}

}