package application.Autenticazione.AutenticazioneControl;

import application.Autenticazione.AutenticazioneService.AutenticazioneServiceImpl;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.ProfiloInesistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.TelefonoEsistenteException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.ClienteDAODataSource;

//import org.apache.tomcat.jdbc.pool.DataSource;
import javax.sql.DataSource;

/**
 * Servlet che gestisce la modifica del profilo utente.
 *
 * Questa servlet permette agli utenti autenticati di aggiornare le proprie informazioni personali,
 * precisamente l'indirizzo email e il numero di telefono.
 *
 * @author raffy
 */

@WebServlet(name = "UpdateProfileController", urlPatterns = {"/UpdateProfileController"})
public class UpdateProfileController extends HttpServlet {

	/**
	 * serialVersionUID : È un campo statico finale a lungo raggio utilizzato 
	 * per la serializzazione dell'oggetto.
	 */
	private static final long serialVersionUID = 1L;
	private AutenticazioneController loginController;
	private AutenticazioneServiceImpl as;
	/*Init per Testing
	public void init() throws ServletException {
		DataSource ds = new DataSource();
		UtenteDAODataSource userDAO = null;
		RuoloDAODataSource roleDAO = null;
		ClienteDAODataSource profileDAO = null;
		IndirizzoDAODataSource addressDAO = null;
		try {
			roleDAO = new RuoloDAODataSource(ds);
			profileDAO = new ClienteDAODataSource(ds);
			addressDAO = new IndirizzoDAODataSource(ds);
			userDAO = new UtenteDAODataSource(ds);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		as = new AutenticazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);
		loginController = new AutenticazioneController(as, addressDAO);
	}
	*/
	public void init() throws ServletException {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/techheaven");
			RuoloDAODataSource roleDAO = null;
			ClienteDAODataSource profileDAO = null;
			IndirizzoDAODataSource addressDAO = null;
			roleDAO = new RuoloDAODataSource(ds);
			profileDAO = new ClienteDAODataSource(ds);
			addressDAO = new IndirizzoDAODataSource(ds);
			userDAO = new UtenteDAODataSource(ds);
		
		loginService = new AutenticazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
	}

	//Costrutto per test
	public UpdateProfileController(AutenticazioneController loginController, AutenticazioneServiceImpl as) {
		this.loginController = loginController;
		this.as = as;
	}	
	
	/**
	 * Recupera l'oggetto utente dalla sessione HTTP.
	 *
	 * Questo metodo cerca l'oggetto `ProxyUtente` nella sessione HTTP corrente.
	 * L'oggetto `ProxyUtente` rappresenta l'utente attualmente autenticato.
	 *
	 * @param request : La richiesta HTTP inviata dal client.
	 * @return user : L'oggetto `ProxyUtente` se l'utente è autenticato, altrimenti `null`.
	 */

	private ProxyUtente getUser(HttpServletRequest request) {
		ProxyUtente user = (ProxyUtente) request.getSession().getAttribute("user");
		if (user == null) {
			return null;
		}
		return user;
	}

	/**
	 * Gestisce le richieste HTTP GET.
	 *
	 * Questo metodo reindirizza le richieste GET al metodo `doPost` in quanto
	 * le modifiche al profilo richiedono l'invio di dati tramite il metodo POST.
	 *
	 * @param request : La richiesta HTTP GET.
	 * @param response : La risposta HTTP da inviare al client.
	 * @throws ServletException : se si verifica un errore durante l'elaborazione della richiesta.
	 * @throws IOException : se si verifica un errore di input/output.
	 */

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Gestisce le richieste HTTP POST.
	 *
	 * Questo metodo elabora i dati inviati tramite il metodo POST per aggiornare
	 * le informazioni dell'utente, ovvero email e numero di telefono.
	 *
	 * @param request : La richiesta HTTP POST.
	 * @param response : La risposta HTTP da inviare al client.
	 * @throws ServletException : se si verifica un errore durante l'elaborazione della richiesta.
	 * @throws IOException : se si verifica un errore di input/output.
	 */

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException { 
		try { 
			String updated_email = (String)request.getParameter("email");
			String updated_tel =   (String)request.getParameter("telefono");
			String information = (String) request.getParameter("information");

			if(!information.equalsIgnoreCase("telefono") && !information.equalsIgnoreCase("email")){                  
				throw new InformazioneDaModificareException("Seleziona un'informazione da modificare: telefono o email.");                   
			}
			//Recupero utente dalla sessione
			ProxyUtente user = getUser(request);

			//Se l'utente non è autenticato viene indirizzato nella pagina di Autenticazione
			if(user==null) {
				response.sendRedirect(request.getContextPath() + "/Autenticazione");
				return;
			}
			ProxyUtente updated_user = null;
			Utente real_userR = user.mostraUtente();
			
			//Si verifica quale campo vuole cambiare l'utente

			if (information.equalsIgnoreCase("email") && updated_email != null && !updated_email.isEmpty()) {
				request.getSession().setAttribute("field", "email");                           

				if(Cliente.checkValidateEmail(updated_email)) {
					//Si verifica se updated_email è uguale all'email associata all'utente nel database
					Utente real_user = user.mostraUtente();

					if(real_user.getProfile().getEmail().equals(updated_email)){
						throw new EmailEsistenteException("Non è possibile associare questa email al tuo account. Inserisci una altra email.");
					}

					updated_user = as.aggiornaProfilo(user, "EMAIL", updated_email); 
				}
			}

			if (information.equalsIgnoreCase("telefono") && updated_tel != null &&  !updated_tel.isEmpty()) {                           
				request.getSession().setAttribute("field", "telefono");


				if(Cliente.checkValidateTelefono(updated_tel)) {
					//Si verifica se updated_tel è uguale al recapito telefonico associato all'utente nel database

					Utente real_user = user.mostraUtente();
					
					if(real_user.getProfile().getTelefono().equals(updated_tel)){
						throw new TelefonoEsistenteException("Non è possibile associare questo recapito telefonico al tuo account. Inserisci un altro numero di telefono.");
					}

					updated_user = as.aggiornaProfilo(user, "TELEFONO", updated_tel);
					
				}else {
					throw new FormatoTelefonoException("Il formato del numero di telefono deve essere xxx-xxx-xxxx.");
				} 
			}
			request.getSession().setAttribute("user", updated_user);
			
			response.sendRedirect(request.getContextPath()+"/AreaRiservata");      

		}catch(FormatoEmailException ex) {
			String errorMsg = "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).";
			request.setAttribute("error", errorMsg);

			try {
				loginController.loadUserAddresses(request);

			} catch (SQLException e) {

				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			}

			request.getRequestDispatcher("updateUserInfo").forward(request, response);
			return;

		}catch(EmailEsistenteException ex) {
			String errorMsg = "Non è possibile associare questa email al tuo account. Inserisci una altra email.";
			request.setAttribute("error", errorMsg);

			try {
				loginController.loadUserAddresses(request);

			} catch (SQLException e) {

				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");e.printStackTrace();
			}

			request.getRequestDispatcher("updateUserInfo").forward(request, response);
			return;

		}catch(FormatoTelefonoException ex) {

			String errorMsg = "Il formato del numero di telefono deve essere xxx-xxx-xxxx.";
			request.setAttribute("error", errorMsg);

			try {

				loginController.loadUserAddresses(request);
			} catch (SQLException e) {

				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			}
			request.getRequestDispatcher("updateUserInfo").forward(request, response);
			return;

		}catch(TelefonoEsistenteException ex) {

			String errorMsg = "Non è possibile associare questo recapito telefonico al tuo account. Inserisci un altro numero di telefono.";
			request.setAttribute("error", errorMsg);

			try {
				loginController.loadUserAddresses(request);

			} catch (SQLException e) {

				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			}
			request.getRequestDispatcher("updateUserInfo").forward(request, response);

			return;
		}catch(ProfiloInesistenteException ex) {

			String errorMsg = "Si è verificato un errore nel recupero delle tue informazioni personali. Riprova più tardi.";
			request.setAttribute("error", errorMsg);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		}catch(InformazioneDaModificareException ex) {

			String errorMsg = "Seleziona un'informazione da modificare : telefono o email.";
			request.getSession().setAttribute("error", errorMsg);

			try {
				loginController.loadUserAddresses(request);
			} catch (SQLException e) {
				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.getSession().setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			}
			response.sendRedirect("/UpdateUserInfo");

		}catch(SQLException e){

			try {
				Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, e);
				String errormsg = "Errore durante la modifica delle informazioni";
				request.setAttribute("error", errormsg);

				loginController.loadUserAddresses(request);
				request.getRequestDispatcher("UpdateUserInfo").forward(request, response);
			} catch (SQLException ex) {
				Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
	}

}
