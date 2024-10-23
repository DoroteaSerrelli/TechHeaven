package application.AutenticazioneControl;

import application.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.AutenticazioneService.AutenticazioneException.ProfiloInesistenteException;
import application.AutenticazioneService.AutenticazioneException.TelefonoEsistenteException;
import application.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Utente;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException { 
		try { 
			String updated_email = (String)request.getParameter("email");
			String updated_tel =   (String)request.getParameter("telefono");
                        String information = (String) request.getParameter("information");
                       
                        if(!information.equals("telefono") && !information.equals("email")){                  
                            throw new InformazioneDaModificareException("ex");                   
                        }
			//Recupero utente dalla sessione
			ProxyUtente user = getUser(request);

			//Se l'utente non è autenticato viene indirizzato nella pagina di Autenticazione
			if(user==null) {
				response.sendRedirect(request.getContextPath() + "/Autenticazione");
				return;
			}
			ProxyUtente updated_user = user;

			AutenticazioneServiceImpl as = new AutenticazioneServiceImpl();

			//Si verifica quale campo vuole cambiare l'utente

			if (information.equals("email") && updated_email != null && !updated_email.isEmpty()) {
                                request.getSession().setAttribute("field", "email");  // Assuming we are working with addresses                           
				//Si verifica se updated_email è uguale all'email associata all'utente nel database
				Utente real_user = user.mostraUtente();
				if(real_user.getProfile().getEmail().equals(updated_email)){
					request.getSession().setAttribute("error","Non è possibile associare questa email al tuo account. Inserisci una altra email.");                               
					response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");
					return;
				}

				updated_user = as.aggiornaProfilo(user, "EMAIL", updated_email);     
			}

			if (information.equals("telefono") && updated_tel != null &&  !updated_tel.isEmpty()) {                           
                                request.getSession().setAttribute("field", "telefono");
				//Si verifica se updated_tel è uguale al recapito telefonico associato all'utente nel database

				Utente real_user = user.mostraUtente();
				if(real_user.getProfile().getTelefono().equals(updated_tel)){
					request.getSession().setAttribute("error","Non è possibile associare questo recapito telefonico al tuo account. Inserisci un altro numero di telefono.");                               
					response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");
					return;
				}

				updated_user = as.aggiornaProfilo(updated_user, "TELEFONO", updated_tel);
			}             
			request.getSession().setAttribute("user", updated_user);
			response.sendRedirect(request.getContextPath()+"/AreaRiservata");      

		}catch(FormatoEmailException ex) {
			
			String errorMsg = "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).";
			request.setAttribute("error", errorMsg);
			AutenticazioneController cont = new AutenticazioneController();
			
			try {
				cont.loadUserAddresses(request);
				
			} catch (SQLException e) {

				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			}
			request.getRequestDispatcher("updateUserInfo").forward(request, response);

		}catch(EmailEsistenteException ex) {

			String errorMsg = "Non è possibile associare questa email al tuo account. Inserisci una altra email.";
			request.setAttribute("error", errorMsg);
			AutenticazioneController cont = new AutenticazioneController();

			try {
				cont.loadUserAddresses(request);
				
			} catch (SQLException e) {
				
				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");e.printStackTrace();
			}
			
			request.getRequestDispatcher("updateUserInfo").forward(request, response);

		}catch(FormatoTelefonoException ex) {
			
			String errorMsg = "Il formato del numero di telefono deve essere xxx-xxx-xxxx.";
			request.setAttribute("error", errorMsg);
			AutenticazioneController cont = new AutenticazioneController();
			
			try {
				
				cont.loadUserAddresses(request);
			} catch (SQLException e) {
				
				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			}
			request.getRequestDispatcher("updateUserInfo").forward(request, response);

		}catch(TelefonoEsistenteException ex) {
			
			String errorMsg = "Non è possibile associare questo recapito telefonico al tuo account. Inserisci un altro numero di telefono.";
			request.setAttribute("error", errorMsg);
			AutenticazioneController cont = new AutenticazioneController();
			
			try {
				cont.loadUserAddresses(request);
				
			} catch (SQLException e) {
				
				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			}
			request.getRequestDispatcher("updateUserInfo").forward(request, response);

		}catch(ProfiloInesistenteException ex) {
			
			String errorMsg = "Si è verificato un errore nel recupero delle tue informazioni personali. Riprova più tardi.";
			request.setAttribute("error", errorMsg);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		}catch(InformazioneDaModificareException ex) {
			
			String errorMsg = "Seleziona un'informazione da modificare : telefono o email.";
			request.getSession().setAttribute("error", errorMsg);
			AutenticazioneController cont = new AutenticazioneController();
			
			try {
				cont.loadUserAddresses(request);
			} catch (SQLException e) {
				String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			}
			request.getRequestDispatcher("UpdateUserInfo").forward(request, response);

		}catch(SQLException e){
			
			try {
				Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, e);
				String errormsg = "Errore durante la modifica delle informazioni";
				request.setAttribute("error", errormsg);

				AutenticazioneController cont = new AutenticazioneController();
				cont.loadUserAddresses(request);
				request.getRequestDispatcher("UpdateUserInfo").forward(request, response);
			} catch (SQLException ex) {
				Logger.getLogger(UpdateProfileController.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
	}

}
