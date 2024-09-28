package application.AutenticazioneControl;

import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.AutenticazioneService.AutenticazioneException.FormatoPasswordException;
import application.AutenticazioneService.AutenticazioneException.PasswordEsistenteException;
import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.RegistrazioneService.ProxyUtente;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			String action = request.getParameter("action");
			String username;
			String email;

			switch(action) {
			case "resetPasswordRequest":
				username = request.getParameter("username");
				email = request.getParameter("email");

				UtenteDAODataSource userDao = new UtenteDAODataSource();
				ProxyUtente userUsername = userDao.doRetrieveProxyUserByKey(username);

				if(!userUsername.getUsername().isEmpty()) {
					String emailRetrieved = userUsername.mostraUtente().getProfile().getEmail();

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
					request.getSession().setAttribute("error","Username o email non valide");                               
					response.sendRedirect(request.getContextPath() + "/resetPassword");
					return;
				}
				break;

			case "resetPassword":
				username = (String) request.getSession().getAttribute("username");
				email = (String) request.getSession().getAttribute("email");
				String password = request.getParameter("password");

				AutenticazioneServiceImpl loginService = new AutenticazioneServiceImpl();
				loginService.resetPassword(username, email, password);
				response.sendRedirect(request.getContextPath() + "/Autenticazione");

				request.getSession().removeAttribute("username");
				request.getSession().removeAttribute("email");

				break;
			}

		} catch (UtenteInesistenteException | FormatoPasswordException |PasswordEsistenteException ex) {
			request.getSession().setAttribute("error", ex.getMessage());
			response.sendRedirect(request.getContextPath() + "/protected/cliente/creaPassword.jsp");

		}catch(SQLException ex) {
			request.getSession().setAttribute("error", ex.getMessage());
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
		}
	}
}
