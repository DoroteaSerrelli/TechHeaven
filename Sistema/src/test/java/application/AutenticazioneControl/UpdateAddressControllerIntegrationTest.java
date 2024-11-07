package application.AutenticazioneControl;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
import org.mockito.Mockito;

import application.Autenticazione.AutenticazioneControl.UpdateAddressController;
import application.Autenticazione.AutenticazioneService.AutenticazioneServiceImpl;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.IndirizzoEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.ModificaIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.ProfiloInesistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.RimozioneIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.Ruolo;
import application.Registrazione.RegistrazioneService.Utente;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCAPException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCittaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoNumCivicoException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoProvinciaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoViaException;
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;

public class UpdateAddressControllerIntegrationTest {

	private UpdateAddressController indirizzoController;
	private AutenticazioneServiceImpl loginService;
	private IndirizzoDAODataSource addressDAO;
	private UtenteDAODataSource userDAO;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;

	@BeforeEach
	public void setUp() throws IOException {
		addressDAO = mock(IndirizzoDAODataSource.class);
		RuoloDAODataSource roleDAO = mock(RuoloDAODataSource.class);
		ClienteDAODataSource profileDAO = mock(ClienteDAODataSource.class);
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
	 * TEST CASES PER AGGIUNTA DI UN INDIRIZZO IN RUBRICA
	 * 
	 * TC6_1.1_1 : informazione da modificare non specificata correttamente
	 * TC6_1.1_2 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa non correttamente
	 * TC6_1.1_3 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico non espresso correttamente 
	 * TC6_1.1_4 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa non correttamente
	 * TC6_1.1_5 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa correttamente, CAP non espresso correttamente
	 * TC6_1.1_6 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa correttamente, CAP espresso correttamente,
	 * 				provincia espressa non correttamente
	 * TC6_1.1_7 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa correttamente, CAP espresso correttamente,
	 * 				provincia espressa correttamente; l'indirizzo inserito è già 
	 * 				presente in rubrica
	 * TC6_1.1_8 : informazione da modificare == AGGIUNGERE-INDIRIZZO, 
	 * 				via espressa correttamente, numero civico espresso correttamente,
	 * 				città espressa correttamente, CAP espresso correttamente,
	 * 				provincia espressa correttamente; l'indirizzo inserito non è  
	 * 				presente in rubrica
	 * 
	 * 
	 * */

	@Test
	public void testDoPost_TC6_1_1_1() throws IOException, ServletException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));
		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);

		String action = "INFOERRATA";

		String via = "";
		String numCivico = "";
		String cap = "";
		String citta = "";
		String provincia = "";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		indirizzoController.doPost(request, response);

		InformazioneDaModificareException ex = new InformazioneDaModificareException("Seleziona una informazione da modificare : AGGIUNGERE-INDIRIZZO, RIMUOVERE-INDIRIZZO, AGGIORNARE-INDIRIZZO.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_1_1_2() throws IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, SQLException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);


		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		Indirizzo target_ind = new Indirizzo(10, "1 Prova", "11", "ProvaProva", "11111", "PR");


		String action = "AGGIUNGERE-INDIRIZZO";

		String via = "1 Prova";
		String numCivico = "11";
		String cap = "11111";
		String citta = "ProvaProva";
		String provincia = "PR";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);

		indirizzoController.doPost(request, response);

		assertThrows(FormatoViaException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);
		});

		FormatoViaException ex =  new FormatoViaException("La via deve contenere solo lettere e spazi");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_1_1_3() throws IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, SQLException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);


		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		Indirizzo target_ind = new Indirizzo(10, "Prova", "11RR", "ProvaProva", "11111", "PR");


		String action = "AGGIUNGERE-INDIRIZZO";

		String via = "Prova";
		String numCivico = "11RR";
		String cap = "11111";
		String citta = "ProvaProva";
		String provincia = "PR";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);


		indirizzoController.doPost(request, response);

		assertThrows(FormatoNumCivicoException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);
		});

		FormatoNumCivicoException ex =  new FormatoNumCivicoException("Il numero civico è composto da numeri e, eventualmente, una lettera.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_1_1_4() throws IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, SQLException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);


		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String via = "Prova";
		String numCivico = "11R";
		String cap = "11111";
		String citta = "Prova4Prova";
		String provincia = "PR";

		Indirizzo target_ind = new Indirizzo(10, via, numCivico, citta, cap, provincia);


		String action = "AGGIUNGERE-INDIRIZZO";


		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);


		indirizzoController.doPost(request, response);

		assertThrows(FormatoCittaException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);
		});

		FormatoCittaException ex =  new FormatoCittaException("La città deve essere composta solo da lettere e spazi.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_1_1_5() throws IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, SQLException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);


		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String via = "Prova";
		String numCivico = "11R";
		String cap = "116111";
		String citta = "ProvaProva";
		String provincia = "PR";

		Indirizzo target_ind = new Indirizzo(10, via, numCivico, citta, cap, provincia);


		String action = "AGGIUNGERE-INDIRIZZO";


		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);


		indirizzoController.doPost(request, response);

		assertThrows(FormatoCAPException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);
		});

		FormatoCAPException ex =  new FormatoCAPException("Il CAP deve essere formato da 5 numeri.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_1_1_6() throws IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, SQLException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);


		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String via = "Prova";
		String numCivico = "11R";
		String cap = "11111";
		String citta = "ProvaProva";
		String provincia = "Pr";

		Indirizzo target_ind = new Indirizzo(10, via, numCivico, citta, cap, provincia);


		String action = "AGGIUNGERE-INDIRIZZO";


		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);


		indirizzoController.doPost(request, response);

		assertThrows(FormatoProvinciaException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);
		});

		FormatoProvinciaException ex =  new FormatoProvinciaException("La provincia è composta da due lettere maiuscole.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_1_1_7() throws IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, SQLException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);


		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String via = "Platani";
		String numCivico = "13";
		String cap = "64100";
		String citta = "Teramo";
		String provincia = "AQ";

		Indirizzo target_ind = new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ");


		String action = "AGGIUNGERE-INDIRIZZO";


		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);

		assertThrows(IndirizzoEsistenteException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);
		});

		indirizzoController.doPost(request, response);

		IndirizzoEsistenteException ex =  new IndirizzoEsistenteException("L'indirizzo inserito è già presente nella tua rubrica degli indirizzi.");;

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_1_1_8() throws IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, SQLException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);


		// Arrange
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		String via = "Corso Garibaldi";
		String numCivico = "67";
		String cap = "82100";
		String citta = "Benevento";
		String provincia = "BN";

		Indirizzo target_ind = new Indirizzo(3, via, numCivico, citta, cap, provincia);


		String action = "AGGIUNGERE-INDIRIZZO";


		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);

		when(addressDAO.doSave(target_ind, username)).thenReturn(true);

		ProxyUtente realProxy = loginService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);

		indirizzoController.doPost(request, response);

		assertEquals(realProxy, user);
		verify(request.getSession()).setAttribute("user", user);
		verify(response).sendRedirect(request.getContextPath() + "/AreaRiservata");      

		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	/**
	 * TEST CASES PER RIMOZIONE DI UN INDIRIZZO IN RUBRICA
	 * 
	 * TC6_2.1_1 : informazione da modificare non specificata correttamente
	 * TC6_2.1_2 : informazione da modificare == RIMUOVERE-INDIRIZZO, 
	 * 				indirizzo da rimuovere non specificato
	 * TC6_2.1_3 : informazione da modificare == RIMUOVERE-INDIRIZZO, 
	 * 				indirizzo da rimuovere specificato,
	 * 				indirizzo specificato associato all'utente
	 * 
	 * */

	@Test
	public void testDoPost_TC6_2_1_1() throws SQLException, IOException, ServletException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));
		// Arrange
		UtenteDAODataSource userDAO = mock(UtenteDAODataSource.class);
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);

		String action = "INFOERRATA";

		String via = "";
		String numCivico = "";
		String cap = "";
		String citta = "";
		String provincia = "";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		indirizzoController.doPost(request, response);

		InformazioneDaModificareException ex = new InformazioneDaModificareException("Seleziona una informazione da modificare : AGGIUNGERE-INDIRIZZO, RIMUOVERE-INDIRIZZO, AGGIORNARE-INDIRIZZO.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}


	@Test
	public void testDoPost_TC6_2_1_2() throws SQLException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, ServletException, IOException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "RIMUOVERE-INDIRIZZO";

		String via = "";
		String numCivico = "";
		String cap = "";
		String citta = "";
		String provincia = "";
		String addressIndex = "";
		Indirizzo address = null;

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);

		indirizzoController.doPost(request, response);

		assertThrows(FormatoIndirizzoException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, action, address);
		});

		FormatoIndirizzoException ex = new FormatoIndirizzoException("Specificare l'indirizzo di spedizione da rimuovere.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(request.getSession(), times(2)).setAttribute("field", "address");
		verify(request.getSession(), times(2)).setAttribute("currentAction", request.getParameter("action"));
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");

	}


	@Test
	public void testDoPost_TC6_2_1_3() throws SQLException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException, IOException, ServletException {

		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "RIMUOVERE-INDIRIZZO";

		String via = "Platani";
		String numCivico = "13";
		String citta = "Teramo";
		String cap = "64100";
		String provincia = "AQ";
		String addressIndex = "3";
		Indirizzo target_ind = new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ");

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);
		when(addressDAO.doDeleteAddress(target_ind.getIDIndirizzo(), username)).thenReturn(true);

		ProxyUtente realProxy = loginService.aggiornaRubricaIndirizzi(user, action, target_ind);

		indirizzoController.doPost(request, response);

		assertEquals(realProxy, user);
		verify(request.getSession()).setAttribute("user", user);
		verify(response).sendRedirect(request.getContextPath() + "/AreaRiservata");      

		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);      

	}

	/**
	 * TEST CASES PER AGGIORNAMENTO DI UN INDIRIZZO IN RUBRICA
	 * 
	 * TC6_3.1_1 : informazione da modificare non specificata correttamente
	 * 
	 * TC6_3.1_2 : informazione da modificare == AGGIORNARE-INDIRIZZO, 
	 * 				indirizzo selezionato non presente in rubrica
	 * 
	 * TC6_3.1_3 : informazione da modificare == AGGIORNARE-INDIRIZZO, 
	 * 				indirizzo selezionato presente in rubrica,
	 * 				la nuova via non è espressa nel formato corretto.
	 * 
	 * TC6_3.1_4 : informazione da modificare == AGGIORNARE-INDIRIZZO, 
	 * 				indirizzo selezionato presente in rubrica,
	 * 				la nuova via è espressa correttamente; il nuovo numero civico 
	 * 				non è espresso nel formato corretto.
	 * 
	 * TC6_3.1_5 : informazione da modificare == AGGIORNARE-INDIRIZZO, 
	 * 				indirizzo selezionato presente in rubrica,
	 * 				la nuova via è espressa correttamente; il nuovo numero civico 
	 * 				è espresso nel formato corretto; la nuova città non è espressa
	 * 				nel corretto formato.
	 * 
	 * TC6_3.1_6 : informazione da modificare == AGGIORNARE-INDIRIZZO, 
	 *				indirizzo selezionato presente in rubrica,
	 * 				la nuova via è espressa correttamente;  il nuovo numero civico 
	 * 				è espresso nel formato corretto; la nuova città è espressa
	 * 				correttamente; il nuovo CAP non è espresso nel corretto formato.
	 * 
	 * TC6_3.1_7 : informazione da modificare == AGGIORNARE-INDIRIZZO, 
	 * 				indirizzo selezionato presente in rubrica,
	 * 				la nuova via è espressa correttamente; il nuovo numero civico 
	 * 				è espresso nel formato corretto; la nuova città è espressa
	 * 				correttamente; il nuovo CAP è espresso nel corretto formato;
	 * 				la nuova provincia non è stata espressa correttamente.
	 * 
	 * TC6_3.1_8 : informazione da modificare == AGGIORNARE-INDIRIZZO, 
	 * 				indirizzo selezionato presente in rubrica,
	 * 				la nuova via è espressa correttamente; il nuovo numero civico 
	 * 				è espresso nel formato corretto; la nuova città è espressa
	 * 				correttamente; il nuovo CAP è espresso nel corretto formato;
	 * 				la nuova provincia è espressa correttamente.
	 * 				Il nuovo indirizzo è già presente nella rubrica degli indirizzi.
	 * 
	 * TC6_3.1_9 : informazione da modificare == AGGIORNARE-INDIRIZZO, 
	 * 				indirizzo selezionato presente in rubrica,
	 * 				la nuova via è espressa correttamente; il nuovo numero civico 
	 * 				è espresso nel formato corretto; la nuova città è espressa
	 * 				correttamente; il nuovo CAP è espresso nel corretto formato;
	 * 				la nuova provincia è espressa correttamente.
	 * 				Il nuovo indirizzo non è presente nella rubrica degli indirizzi.
	 * 
	 * */

	@Test
	public void testDoPost_TC6_3_1_1() throws SQLException, IOException, ServletException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));
		// Arrange
		UtenteDAODataSource userDAO = mock(UtenteDAODataSource.class);
		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);

		String action = "INFOERRATA";

		String via = "";
		String numCivico = "";
		String cap = "";
		String citta = "";
		String provincia = "";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		indirizzoController.doPost(request, response);

		InformazioneDaModificareException ex = new InformazioneDaModificareException("Seleziona una informazione da modificare : AGGIUNGERE-INDIRIZZO, RIMUOVERE-INDIRIZZO, AGGIORNARE-INDIRIZZO.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_3_1_2() throws SQLException, IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Platani", "13", "Teramo", "64100", "AQ"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);

		Indirizzo doUpAddress = new Indirizzo(2, "Alberto da Giusano", "25", "Roma", "00176", "RM");

		String action = "AGGIORNARE-INDIRIZZO";

		String via = "via Alberto da Giusano";
		String numCivico = "25";
		String citta = "Roma";
		String cap = "00176";
		String provincia = "RM";
		String addressIndex = "2";

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);

		int idIndirizzoU = 2;
		when(addressDAO.doRetrieveByKey(idIndirizzoU, username)).thenReturn(doUpAddress);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);
		
		indirizzoController.doPost(request, response);
		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		
		assertThrows(ModificaIndirizzoException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", doUpAddress);
		});

		ModificaIndirizzoException ex = new ModificaIndirizzoException("L'indirizzo inserito non è presente nella tua rubrica degli indirizzi.");			

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_3_1_3() throws SQLException, IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Francesco la Francesca", "8", "Salerno", "84121", "SA"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "AGGIORNARE-INDIRIZZO";

		String via = "Alb3rt <> da G1usan0";
		String numCivico = "10";
		String citta = "Avellino";
		String cap = "83100";
		String provincia = "AV";
		String addressIndex = "3";

		Indirizzo doUpAddress = new Indirizzo(3, via, numCivico, citta, cap, provincia);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);

		indirizzoController.doPost(request, response);
		
		assertThrows(FormatoViaException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", doUpAddress);
		});

		
		FormatoViaException ex = new FormatoViaException("La nuova via deve contenere solo lettere e spazi");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_3_1_4() throws SQLException, IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Francesco la Francesca", "8", "Salerno", "84121", "SA"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "AGGIORNARE-INDIRIZZO";

		String via = "via Alberto da Giusano";
		String numCivico = "newCiv19999";
		String citta = "Avellino";
		String cap = "83100";
		String provincia = "AV";
		String addressIndex = "3";

		Indirizzo doUpAddress = new Indirizzo(3, via, numCivico, citta, cap, provincia);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);
		
		
		indirizzoController.doPost(request, response);
		
		assertThrows(FormatoNumCivicoException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", doUpAddress);
		});
		
		FormatoNumCivicoException ex = new FormatoNumCivicoException("Il nuovo numero civico è composto da numeri e, eventualmente, una lettera.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_3_1_5() throws SQLException, IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Francesco la Francesca", "8", "Salerno", "84121", "SA"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "AGGIORNARE-INDIRIZZO";

		String via = "via Alberto da Giussano";
		String numCivico = "8";
		String citta = "newCitta!(1999)";
		String cap = "83100";
		String provincia = "AV";
		String addressIndex = "3";

		Indirizzo doUpAddress = new Indirizzo(3, via, numCivico, citta, cap, provincia);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);
		
		indirizzoController.doPost(request, response);
		
		assertThrows(FormatoCittaException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", doUpAddress);
		});
		
		FormatoCittaException ex = new FormatoCittaException("La nuova città deve essere composta solo da lettere e spazi.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_3_1_6() throws SQLException, IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Francesco la Francesca", "8", "Salerno", "84121", "SA"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "AGGIORNARE-INDIRIZZO";

		String via = "via Alberto da Giussano";
		String numCivico = "8";
		String citta = "Roma";
		String cap = "R879";
		String provincia = "AV";
		String addressIndex = "3";

		Indirizzo doUpAddress = new Indirizzo(3, via, numCivico, citta, cap, provincia);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);
		
		
		indirizzoController.doPost(request, response);
		
		assertThrows(FormatoCAPException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", doUpAddress);
		});

		FormatoCAPException ex = new FormatoCAPException("Il nuovo CAP deve essere formato da 5 numeri.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_3_1_7() throws SQLException, IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Francesco la Francesca", "8", "Salerno", "84121", "SA"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "AGGIORNARE-INDIRIZZO";

		String via = "via Alberto da Giussano";
		String numCivico = "8";
		String citta = "Roma";
		String cap = "00176";
		String provincia = "(RM)";
		String addressIndex = "3";

		Indirizzo doUpAddress = new Indirizzo(3, via, numCivico, citta, cap, provincia);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);
		

		indirizzoController.doPost(request, response);
		
		assertThrows(FormatoProvinciaException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", doUpAddress);
		});
		
		FormatoProvinciaException ex = new FormatoProvinciaException("La nuova provincia è composta da due lettere maiuscole.");

		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_3_1_8() throws SQLException, IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Francesco la Francesca", "8", "Salerno", "84121", "SA"));
		addresses.add(new Indirizzo(41, "via Marco Polo", "13A", "Bologna", "40131", "BO"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "AGGIORNARE-INDIRIZZO";

		String via = "via Marco Polo";
		String numCivico = "13A";
		String citta = "Bologna";
		String cap = "40131";
		String provincia = "BO";
		String addressIndex = "3";

		Indirizzo doUpAddress = new Indirizzo(3, via, numCivico, citta, cap, provincia);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);
		
		indirizzoController.doPost(request, response);
		
		assertThrows(IndirizzoEsistenteException.class, () -> {
			loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", doUpAddress);
		});
		
		IndirizzoEsistenteException ex = new IndirizzoEsistenteException("L'indirizzo inserito è già presente nella tua rubrica degli indirizzi.");


		verify(request.getSession()).setAttribute("error", ex.getMessage());
		verify(response).sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

	@Test
	public void testDoPost_TC6_3_1_9() throws SQLException, IOException, ServletException, UtenteInesistenteException, IndirizzoEsistenteException, FormatoIndirizzoException, ModificaIndirizzoException, InformazioneDaModificareException, RimozioneIndirizzoException, ProfiloInesistenteException, FormatoViaException, FormatoNumCivicoException, FormatoCittaException, FormatoCAPException, FormatoProvinciaException {
		indirizzoController = new UpdateAddressController(addressDAO, loginService);

		String username = "mariaGestoreCatalogo";
		String password = "01maria01";
		ArrayList<Indirizzo> addresses = new ArrayList<>();
		addresses.add(new Indirizzo(3, "Francesco la Francesca", "8", "Salerno", "84121", "SA"));
		addresses.add(new Indirizzo(41, "via Marco Polo", "13A", "Bologna", "40131", "BO"));

		ArrayList<Ruolo> roles = new ArrayList<>();
		roles.add(new Ruolo("Cliente"));
		roles.add(new Ruolo("GestoreCatalogo"));

		Cliente profile = new Cliente("mariateresa.milani90@gmail.com", "Maria Teresa", "Milani", Cliente.Sesso.F, "222-333-4444",
				addresses);

		ProxyUtente user = new ProxyUtente(username, password, roles, userDAO);
		Utente existingUser = new Utente(username, password, profile);


		String action = "AGGIORNARE-INDIRIZZO";

		String via = "via Alberto da Giussano";
		String numCivico = "8";
		String citta = "Roma";
		String cap = "00176";
		String provincia = "RM";
		String addressIndex = "3";

		Indirizzo doUpAddress = new Indirizzo(3, via, numCivico, citta, cap, provincia);

		when(request.getParameter("action")).thenReturn(action);
		when(request.getParameter("newVia")).thenReturn(via);
		when(request.getParameter("newNumCivico")).thenReturn(numCivico);
		when(request.getParameter("newCap")).thenReturn(cap);
		when(request.getParameter("newCitta")).thenReturn(citta);
		when(request.getParameter("newProvincia")).thenReturn(provincia);

		when(request.getSession().getAttribute("user")).thenReturn(user);

		when(userDAO.doRetrieveFullUserByKey(username)).thenReturn(existingUser);
		when(request.getParameter("addressIndex")).thenReturn(addressIndex);
		when(addressDAO.doRetrieveAll("", username)).thenReturn(addresses);
		
		when(addressDAO.doUpdateAddress(doUpAddress, username)).thenReturn(true);
		
		indirizzoController.doPost(request, response);

		ProxyUtente realProxy = loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", doUpAddress);

		assertEquals(realProxy, user);
		verify(request.getSession()).setAttribute("user", user);
		verify(response).sendRedirect(request.getContextPath() + "/AreaRiservata");      
		verify(request.getSession()).setAttribute("field", "address");
		verify(request.getSession()).setAttribute("currentAction", action);    

	}

}