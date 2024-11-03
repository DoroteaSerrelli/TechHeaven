package application.Registrazione.RegistrazioneControl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import application.Autenticazione.AutenticazioneService.AutenticazioneException.EmailEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoTelefonoException;
import application.Registrazione.RegistrazioneService.Cliente;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.RegistrazioneServiceImpl;
import application.Registrazione.RegistrazioneService.RegistrazioneException.EmailPresenteException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCAPException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCittaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoCognomeException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoGenereException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoNomeException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoNumCivicoException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoPasswordException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoProvinciaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoUsernameException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.FormatoViaException;
import application.Registrazione.RegistrazioneService.RegistrazioneException.UtentePresenteException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;

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
	
	private UtenteDAODataSource userDAO;
	private DataSource ds;
	private RuoloDAODataSource roleDAO;
	private ClienteDAODataSource profileDAO;
	private IndirizzoDAODataSource addressDAO;
	private RegistrazioneServiceImpl rs;
	
	
	/**
	 * Inizializza la servlet, configurando photoControl, productDAO, pu e gcs.
	 *
	 * @throws ServletException : se si verifica un errore durante l'inizializzazione
	 */
	
	@Override
	public void init() throws ServletException {
		
		ds = new DataSource();
		try {
			userDAO = new UtenteDAODataSource(ds);
			roleDAO = new RuoloDAODataSource(ds);
			profileDAO = new ClienteDAODataSource(ds);
			addressDAO = new IndirizzoDAODataSource(ds);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		rs = new RegistrazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);
	}
	
	
	//Costruttore per il testing
	
	public RegistrazioneController(RegistrazioneServiceImpl rs, UtenteDAODataSource userDAO,
			RuoloDAODataSource roleDAO, ClienteDAODataSource profileDAO, IndirizzoDAODataSource addressDAO) {
		
		this.rs = rs;
		this.roleDAO = roleDAO;
		this.userDAO = userDAO;
		this.profileDAO = profileDAO;
		this.addressDAO = addressDAO;
		
	}
	
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
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
		
		ProxyUtente u;

		try {
			
			Indirizzo indirizzo = new Indirizzo(via, cv, citta, cap, provincia);
			Indirizzo.checkValidate(indirizzo);
			
			u = rs.registraCliente(username, password, email, nome, cognome, sesso, telefono, indirizzo);
			//Non ci sono utenti nel database con il nome utente pari a 'username' o con indirizzo di posta pari a 'email'

			ArrayList <Indirizzo> indirizzi = addressDAO.doRetrieveAll("Indirizzo.via", u.getUsername());        
			request.getSession().setAttribute("user", u);
			request.setAttribute("Indirizzi",indirizzi);
			response.sendRedirect(request.getContextPath() + "/AreaRiservata");

		}catch(NullPointerException e) {
	
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		}catch (UtentePresenteException | EmailPresenteException | EmailEsistenteException e) {
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		}catch(SQLException e) {
			Logger.getLogger(RegistrazioneController.class.getName()).log(Level.SEVERE, null, e);
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath()+"/Registrazione");
			
		} catch (FormatoUsernameException | FormatoViaException | FormatoNumCivicoException | FormatoCittaException |
				FormatoCAPException| FormatoProvinciaException |
				FormatoPasswordException | FormatoEmailException | FormatoNomeException |
				FormatoCognomeException | FormatoGenereException | FormatoTelefonoException e) {
			Logger.getLogger(RegistrazioneController.class.getName()).log(Level.SEVERE, null, e);
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath()+"/Registrazione");
		}
	}
}