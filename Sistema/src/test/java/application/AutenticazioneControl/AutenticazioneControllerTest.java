package application.AutenticazioneControl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.RegistrazioneService.Indirizzo;
import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.AutenticazioneService.AutenticazioneException.FormatoRuoloException;
import application.AutenticazioneService.AutenticazioneException.RuoloInesistenteException;
import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Ruolo;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

public class AutenticazioneControllerTest {
	
	private AutenticazioneController loginController;
	private AutenticazioneServiceImpl loginService;
	private IndirizzoDAODataSource addressDAO;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	
	@BeforeEach
	public void setUp() throws IOException {
		
		loginService = mock(AutenticazioneServiceImpl.class);
		addressDAO = mock(IndirizzoDAODataSource.class);
		
		outputStream = mock(ServletOutputStream.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(request.getSession()).thenReturn(mock(javax.servlet.http.HttpSession.class));
		when(request.getContextPath()).thenReturn("/test");
		
	}
	
	
	/**
	 * TEST CASES PER LOGIN (SINGOLO RUOLO, OVVERO CLIENTE)
	 * 
	 * TC2.1_1 : username username non associata ad alcun utente nel sistema
	 * TC2.1_2 : username corretta e password errata
	 * TC2.1_3 : username e password corrette
	 * */
	
	@Test
	public void testDoPost_TC2_1_1() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "login";
		String username = "UsernameInsesistente";
		String password = "password1234";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		
		when(loginService.login(username, password)).thenThrow(new UtenteInesistenteException("Username o password non valide"));
		
		loginController.doPost(request, response);
		
		String exMessage = "Username o password non corretti";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath() + "/Autenticazione");
		
	}
	
	@Test
	public void testDoPost_TC2_1_2() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "login";
		String username = "saraNa";
		//String correctPassword = "12sara";
		String password = "password1234";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		
		when(loginService.login(username, password)).thenThrow(new UtenteInesistenteException("Username o password non valide"));
		
		loginController.doPost(request, response);
		
		String exMessage = "Username o password non corretti";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath() + "/Autenticazione");
		
	}
	
	@Test
	public void testDoPost_TC2_1_3() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "login";
		String username = "saraNa";
		String password = "12sara";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		
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
		
		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		ProxyUtente expectedUser = new ProxyUtente(username, password, roles);
		
		when(loginService.login(username, password)).thenReturn(expectedUser);
		
		loginController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("user", expectedUser);
		verify(response).sendRedirect(request.getContextPath() + "/AreaRiservata");
		
	}
	
	/**
	 * TEST CASES PER LOGIN (MULTI-RUOLO)
	 * 
	 * TC3.1_1 : username non associata ad alcun utente nel sistema
	 * TC3.1_2 : username corretta e password errata
	 * TC3.1_3 : username e password corrette, formato ruolo errato
	 * TC3.1_4 : username e password corrette, formato ruolo corretto, 
	 * 			 ruolo indicato non associato a username
	 * TC3.1_5 : username e password corrette, formato ruolo corretto, 
	 * 			 ruolo indicato associato a username
	 * */
	
	@Test
	public void testDoPost_TC3_1_1() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "login";
		String username = "MariachecheckaCatalogo";
		String password = "prova12sa";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		
		when(loginService.login(username, password)).thenThrow(new UtenteInesistenteException("Username o password non valide"));
		
		loginController.doPost(request, response);
		
		String exMessage = "Username o password non corretti";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath() + "/Autenticazione");
		
	}
	
	@Test
	public void testDoPost_TC3_1_2() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "login";
		String username = "GestoreCatalogo";
		//String correctPassword = "GestoreCatalogo12";
		String password = "password1234";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		
		when(loginService.login(username, password)).thenThrow(new UtenteInesistenteException("Username o password non valide"));
		
		loginController.doPost(request, response);
		
		String exMessage = "Username o password non corretti";
		
		verify(request.getSession()).setAttribute("error", exMessage);
		verify(response).sendRedirect(request.getContextPath() + "/Autenticazione");
		
	}
	
	@Test
	public void testDoPost_TC3_1_3_Accesso() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "login";
		String username = "GestoreCatalogo";
		String password = "GestoreCatalogo12";
		
		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		
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
		
		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));
		ProxyUtente expectedUser = new ProxyUtente(username, password, roles);
		
		when(loginService.login(username, password)).thenReturn(expectedUser);
		
		loginController.doPost(request, response);
		
		verify(request.getSession()).setAttribute("user", expectedUser);
		verify(response).sendRedirect(request.getContextPath() + "/SelezioneRuolo");
		
	}
	
	@Test
	public void testDoPost_TC3_1_3_SceltaRuolo() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "roleSelection";
		String username = "GestoreCatalogo";
		String password = "GestoreCatalogo12";
		String role = "Admin34";
		
		when(request.getParameter("action")).thenReturn(action);
				
		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));
		ProxyUtente expectedUser = new ProxyUtente(username, password, roles);
		
		when(request.getSession().getAttribute("user")).thenReturn(expectedUser);
		when(request.getParameter("ruolo")).thenReturn(role);
		

		loginController.doPost(request, response);
		
		FormatoRuoloException e = new FormatoRuoloException("Il ruolo specificato non esiste.");
		verify(request.getSession()).setAttribute("errorMessage", e.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

	}
	
	@Test
	public void testDoPost_TC3_1_4() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "roleSelection";
		String username = "GestoreCatalogo";
		String password = "GestoreCatalogo12";
		String role = "GestoreOrdini";
		
		when(request.getParameter("action")).thenReturn(action);
				
		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));
		ProxyUtente expectedUser = new ProxyUtente(username, password, roles);
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "51", "Padova", "35100", "PD"));
		when(request.getSession().getAttribute("user")).thenReturn(expectedUser);
		when(request.getParameter("ruolo")).thenReturn(role);
		when(addressDAO.doRetrieveAll("Indirizzo.via", username)).thenReturn(addresses);
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		
		when(request.getRequestDispatcher("Autenticazione")).thenReturn(dispatcher);
		loginController.doPost(request, response);
		
		RuoloInesistenteException e = new RuoloInesistenteException("Ruolo scelto non associato all'utente. Riprova a selezionare un altro ruolo.");
		verify(request.getSession()).setAttribute("error", e.getMessage());
		verify(dispatcher).forward(request, response);
		verify(request).setAttribute("Indirizzi", addresses); 

	}
	
	@Test
	public void testDoPost_TC3_1_5() throws UtenteInesistenteException, SQLException, IOException, ServletException {
		
		loginController = new AutenticazioneController(loginService, addressDAO);
		
		String action = "roleSelection";
		String username = "GestoreCatalogo";
		String password = "GestoreCatalogo12";
		String role = "GestoreCatalogo";
		
		when(request.getParameter("action")).thenReturn(action);
				
		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));
		ProxyUtente expectedUser = new ProxyUtente(username, password, roles);
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(1, "Roma", "51", "Padova", "35100", "PD"));
		when(request.getSession().getAttribute("user")).thenReturn(expectedUser);
		when(request.getParameter("ruolo")).thenReturn(role);
		when(addressDAO.doRetrieveAll("Indirizzo.via", username)).thenReturn(addresses);
		
		loginController.doPost(request, response);
		
		verify(response).sendRedirect(request.getContextPath() + "/GestioneCatalogo");
		verify(request).setAttribute("Indirizzi", addresses); 

	}
	
}
