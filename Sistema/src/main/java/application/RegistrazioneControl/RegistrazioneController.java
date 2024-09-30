package application.RegistrazioneControl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.RegistrazioneException.EmailPresenteException;
import application.RegistrazioneService.RegistrazioneException.UtentePresenteException;
import application.RegistrazioneService.RegistrazioneServiceImpl;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

/**
 * Servlet che gestisce la registrazione di un nuovo utente.
 *
 * @author Dorotea Serrelli
 */

@WebServlet(name = "RegistrazioneController", urlPatterns = {"/RegistrazioneController"})
public class RegistrazioneController extends HttpServlet {

	/**
	 * serialVersionUID : È un campo statico finale a lungo raggio utilizzato 
	 * per la serializzazione dell'oggetto.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Gestisce la richiesta HTTP GET.
	 *
	 * Questa servlet delega l'elaborazione alla funzione `doPost` in quanto la registrazione
	 * di un nuovo utente richiede l'invio di dati tramite il metodo POST.
	 *
	 * @param request : servlet request
	 * @param response : servlet response
	 * @throws ServletException : se si verifica un errore nella servlet
	 * @throws IOException : se si verifica un errore di I/O
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Gestisce la richiesta HTTP POST.
	 *
	 * Questo metodo recupera i dati dell'utente dal form di registrazione e li utilizza
	 * per creare un nuovo oggetto `Cliente` tramite il servizio `RegistrazioneServiceImpl`.
	 * Se la registrazione avviene con successo, l'utente viene reindirizzato all'Area Riservata,
	 * altrimenti viene visualizzata una pagina di errore con un messaggio appropriato.
	 *
	 * @param request : servlet request
	 * @param response : servlet response
	 * @throws ServletException : se si verifica un errore nella servlet
	 * @throws IOException : se si verifica un errore di I/O
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RegistrazioneServiceImpl reg = new RegistrazioneServiceImpl();
		String username= request.getParameter("username");
		String password= request.getParameter("password");
		String email= request.getParameter("email");
		String nome= request.getParameter("name");
		String cognome= request.getParameter("surname");
		String telefono= request.getParameter("phoneNumber");

		String via= request.getParameter("road");
		String cv= request.getParameter("cv");
		String citta= request.getParameter("city");
		String cap= request.getParameter("cap");
		String provincia= request.getParameter("province");
		String sesso= request.getParameter("sesso");

		Indirizzo indirizzo = new Indirizzo(via, cv, citta, cap, provincia);
		ProxyUtente u;

		try {
			u = reg.registraCliente(username, password, email, nome, cognome, Cliente.Sesso.valueOf(sesso), telefono, indirizzo);
			//Non ci sono utenti nel database con il nome utente pari a 'username' o con indirizzo di posta pari a 'email'
			IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();

			ArrayList <Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());        
			request.getSession().setAttribute("user", u);
			request.setAttribute("Indirizzi",indirizzi);
			response.sendRedirect(request.getContextPath() + "/AreaRiservata");

		}catch(NullPointerException e) {
			String message = "Non e\' possibile associare l'username inserita al tuo account.\n "
					+ "Riprova la registrazione inserendo un'altra username.";
			request.getSession().setAttribute("error", message);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		}catch (UtentePresenteException | EmailPresenteException e) {
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		}catch(SQLException e) {
			Logger.getLogger(RegistrazioneController.class.getName()).log(Level.SEVERE, null, e);
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath()+"/Registrazione");
		}
	}
}