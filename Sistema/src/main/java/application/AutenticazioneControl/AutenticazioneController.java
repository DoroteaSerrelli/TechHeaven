package application.AutenticazioneControl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import application.AutenticazioneService.AutenticazioneException;
import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Ruolo;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

/**
 * Questa servlet gestisce l'autenticazione degli utenti e le loro informazioni.
 * 
 * Gestisce in particolare le seguenti funzionalità:
 * 
 * - autenticazione per utente con singolo ruolo;
 * - autenticazione per utente con più di un ruolo associato;
 * - logout di un utente;
 * - caricamento delle informazioni personali dell'utente (cliente) per
 *   ridirezionarlo nell'area riservata.
 * 
 * Estende la classe HttpServlet per gestire le richieste HTTP GET e POST.
 * 
 * @author raffy
 * @author Dorotea Serrelli
 */

@WebServlet(name = "AutenticazioneController", urlPatterns = {"/AutenticazioneController"})
public class AutenticazioneController extends HttpServlet {

	/**
	 * serialVersionUID : è un campo statico finale utilizzato per la serializzazione dell'oggetto.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Questo metodo gestisce le richieste HTTP GET. 
	 * In particolare, se l'utente è già autenticato e intende raggiungere direttamente la pagina
	 * area riservata "AreaRiservata.jsp"(parametro action = null), allora vengono caricati gli indirizzi dell'utente.
	 * 
	 * Se intende, invece, raggiungere la pagina di modifica dei propri dati personali (action = "updateUserInfo"),
	 * allora la servlet reindirizza l'utente alla pagina "UpdateUserInfo.jsp".
	 * 
	 * Altri valori del parametro "action" possono essere aggiunti in futuro per gestire altre funzionalità.
	 * 
	 * @param request : la richiesta HTTP
	 * @param response : la risposta HTTP
	 */

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Accesso diretto alla pagina AreaRiservata.jsp
			loadUserAddresses(request);
		} catch (SQLException ex) {
			Logger.getLogger(AutenticazioneController.class.getName()).log(Level.SEVERE, null, ex);
		}
		// Verifica valore parametro action
		String action = request.getParameter("action");
		if (action != null && !action.isEmpty()) {
			// Forward to updateUserInfo.jsp if action is specified
			if (action.equals("updateUserInfo")) {
				response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");
			}           
		} else {
			// Forward alla pagina AreaRiservata.jsp se non è stata specificata alcuna azione action
			request.getRequestDispatcher("AreaRiservata").forward(request, response);
		}
	}

	/**
	 * Questo metodo gestisce le richieste HTTP POST. 
	 * Vengono eseguite le seguenti operazioni:
	 * <ul>
	 * 		<li>autenticazione per utente con singolo ruolo;</li>
	 * 		<li>autenticazione per utente con più di un ruolo associato;</li>
	 * 		<li>logout di un utente;</li>
	 * </ul>
	 * 
	 * @param request : richiesta HTTP
	 * @param response : risposta HTTP
	 */

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String action = request.getParameter("action");
			if(action.equalsIgnoreCase("login")) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				ProxyUtente resultedUser;

				AutenticazioneServiceImpl loginService = new AutenticazioneServiceImpl();
				resultedUser = loginService.login(username, password);

				if (resultedUser!=null) {
					// Autenticazione andata a buon fine
					request.getSession().setAttribute("user", resultedUser);
					if(resultedUser.getRuoli().size() == 1)
						response.sendRedirect(request.getContextPath() + "/AreaRiservata");
					else
						response.sendRedirect(request.getContextPath() + "/SelezioneRuolo");
					return;
				}else {
					// Autenticazione fallita
					request.getSession().setAttribute("error","Username o password non corretti");                               
					response.sendRedirect(request.getContextPath() + "/Autenticazione");
					return;
				}
			}
			if (action.equalsIgnoreCase("roleSelection")) {
				// Retrieve the user object from session
				ProxyUtente resultedUser = (ProxyUtente) request.getSession().getAttribute("user");                               
				String ruolo = request.getParameter("ruolo");                           
				loadUserAddresses(request);

				ArrayList<Ruolo> ruoli = resultedUser.getRuoli();
				boolean roleMatched = true; // Flag to track if role is found

				for (Ruolo r : ruoli) { 
					if (r.getNomeRuolo().equals(ruolo)) {                                          
						switch (ruolo) {
						case "Cliente": 
							response.sendRedirect(request.getContextPath() + "/AreaRiservata");
							return;                                                          
						case "GestoreOrdini": 
							response.sendRedirect(request.getContextPath() + "/GestioneOrdini");
							return;                                
						case "GestoreCatalogo":                                
							response.sendRedirect(request.getContextPath() + "/GestioneCatalogo");
							return;  
						default: 
							roleMatched = false;
							break;   
						}
					}
				}
				if(!roleMatched){
					// In caso di assenza del ruolo, si genera un errore e si ridireziona l'utente alla
					//pagina di autenticazione
					
					request.getSession().setAttribute("error", "Ruolo scelto non corrispondente ai ruoli del utente");
					request.getRequestDispatcher("Autenticazione").forward(request, response);
					return; // Stop
				}
			}

			if(action.equals("logout")){
				request.getSession().invalidate();// Invalida la sessione
				response.sendRedirect(request.getContextPath() + "/Autenticazione"); 
				return;
			}

		} catch (SQLException | AutenticazioneException.UtenteInesistenteException ex) {
			Logger.getLogger(AutenticazioneController.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().setAttribute("error", "Username o Password Errati");
			response.sendRedirect(request.getContextPath() + "/Autenticazione");
		}
	}

	/**
	 * Questo metodo carica gli indirizzi dell'utente attualmente in sessione. 
	 * Si recupera l'oggetto utente dalla sessione e, se l'utente è valido, si recuperano tutti gli indirizzi dell'utente.
	 * Gli indirizzi recuperati vengono memorizzati nella richiesta come attributo "Indirizzi".
	 * 
	 * @param request : richiesta HTTP
	 */
	public void loadUserAddresses(HttpServletRequest request) throws SQLException {
		ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
		if (u != null) {
			IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();
			ArrayList<Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
			request.setAttribute("Indirizzi", indirizzi); 
		}
	}
}