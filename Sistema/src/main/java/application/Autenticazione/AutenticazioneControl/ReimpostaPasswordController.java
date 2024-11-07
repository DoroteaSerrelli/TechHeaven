package application.Autenticazione.AutenticazioneControl;

import application.Autenticazione.AutenticazioneService.AutenticazioneServiceImpl;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoPasswordException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.PasswordEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.ProxyUtente;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;

/**
 * Questa servlet gestisce la funzionalità di reimpostazione della password dell'utente.
 * Estendi la classe HttpServlet per gestire le richieste HTTP GET e POST.
 * 
 * @author raffy
 * @author Dorotea Serrelli
 */

@WebServlet(name = "ReimpostaPasswordController", urlPatterns = {"/ReimpostaPasswordController"})
public class ReimpostaPasswordController extends HttpServlet {

	/**
	 * serialVersionUID : È un campo statico finale a lungo raggio utilizzato 
	 * per la serializzazione dell'oggetto.
	 */
	private static final long serialVersionUID = 1L;
	private UtenteDAODataSource userDAO;
	private AutenticazioneServiceImpl loginService;


	public ReimpostaPasswordController() throws ServletException {
		
		Context initContext;
		Context envContext;
		DataSource ds = null;
		try {
			initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/techheaven");
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
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

		loginService = new AutenticazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);
	}


	//Costrutto per test
	public ReimpostaPasswordController(AutenticazioneServiceImpl loginService, UtenteDAODataSource userDAO) {
		this.userDAO = userDAO;
		this.loginService = loginService;
	}


	/**
	 * Questo metodo gestisce le richieste HTTP GET. 
	 * Invia qualsiasi richiesta GET a doPost(request, response).
	 * 
	 * @param request : richiesta HTTP
	 * @param response : risposta HTTP
	 */

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Questo metodo gestisce le richieste HTTP POST. 
	 * Vengono eseguite le seguenti operazioni:
	 * <ol>
	 * 		<li>Verifica se le credenziali username e email fornite dall'utente sono corrette </li>
	 * 		<li> Reindirizza in caso affermativo alla pagina di creazione password.
	 * 			<br>In caso contrario, genera messaggi di errore. </li>
	 * 		<li> Una volta creata la nuova password, si verifica il suo formato.<br>
	 * 			 Se è corretto, viene memorizzata nel database. In caso contrario, si genera un messaggio di errore.</li>
	 * </ol>
	 * 
	 * @param request : richiesta HTTP
	 * @param response : risposta HTTP
	 */

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			String action = request.getParameter("action");
			String username;
			String email;

			switch(action) {
			case "resetPasswordRequest":
				username = request.getParameter("username");
				email = request.getParameter("email");

				ProxyUtente userUsername = userDAO.doRetrieveProxyUserByKey(username);
				
				if(userUsername != null) {
					userUsername.setDAO(userDAO);
					String emailRetrieved = userUsername.mostraUtente().getProfile().getEmail();

					if(Cliente.checkValidateEmail(email)) {
						if(emailRetrieved.equals(email)) {
							request.getSession().setAttribute("username", username);
							request.getSession().setAttribute("email", email);
							response.sendRedirect(request.getContextPath() + "/protected/cliente/creaPassword.jsp");
						}else {
							request.getSession().setAttribute("error","Username o email non valide");                               
							response.sendRedirect(request.getContextPath() + "/resetPassword");
							return;
						}
					}else {
						request.getSession().setAttribute("error","L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).");                               
						response.sendRedirect(request.getContextPath() + "/resetPassword");
						return;
					}
				}else {
					request.getSession().setAttribute("error","Username o email non valide");                               
					response.sendRedirect(request.getContextPath() + "/resetPassword");
					return;
				}
				break;

			case "resetPassword":
				username = (String) request.getSession().getAttribute("username");
				email = (String) request.getSession().getAttribute("email");
				String password = request.getParameter("password");

				loginService.resetPassword(username, email, password);
				
				request.getSession().removeAttribute("username");
				request.getSession().removeAttribute("email");
				
				response.sendRedirect(request.getContextPath() + "/Autenticazione");


				break;
			}

		} catch (UtenteInesistenteException | FormatoPasswordException |PasswordEsistenteException ex) {
			request.getSession().setAttribute("error", ex.getMessage());
			response.sendRedirect(request.getContextPath() + "/protected/cliente/creaPassword.jsp");
			return;
			
		}catch(SQLException ex) {
			request.getSession().setAttribute("error", ex.getMessage());
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
		} catch (FormatoEmailException e) {
			request.getSession().setAttribute("error",e.getMessage());                               
			response.sendRedirect(request.getContextPath() + "/resetPassword");
		}
	}
}
