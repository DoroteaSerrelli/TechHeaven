package application.AutenticazioneControl;

import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import storage.AutenticazioneDAO.IndirizzoDAODataSource;

/**
 * La servlet controlla l'accesso ad una pagina riservata agli utenti autenticati.
 *
 * @author raffa
 */

public class AreaRiservata extends HttpServlet {
	
	private IndirizzoDAODataSource addressDAO;
	
	/**
	 * serialVersionUID : È un campo statico finale a lungo raggio utilizzato 
	 * per la serializzazione dell'oggetto.
	 */

	private static final long serialVersionUID = 1L;
	
	public void init() throws ServletException {
		DataSource ds = new DataSource();
		try {
			addressDAO = new IndirizzoDAODataSource(ds);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	//Costrutto per test
	public AreaRiservata(IndirizzoDAODataSource address) {
		this.addressDAO = address;
	}
	

	/**
	 * Gestisce le richieste HTTP GET alla servlet. 
	 *
	 * - Verifica l'autenticazione: Controlla se l'utente è autenticato, cercando l'oggetto `ProxyUtente` nella sessione HTTP. 
	 *      Se l'utente non è autenticato, reindirizza alla pagina di autenticazione.
	 * - Recupero degli indirizzi: Se l'utente è autenticato, verifica se gli indirizzi dell'utente sono già stati recuperati e memorizzati nella richiesta. 
	 *      Se gli indirizzi non sono presenti, li carica dal database utilizzando il metodo `loadUserAddresses`.</li>
	 * - Inoltro alla JSP: Inoltra la richiesta alla pagina JSP `/protected/cliente/AreaRiservata.jsp`, che visualizzerà l'area riservata dell'utente, inclusi i suoi indirizzi.</li>
	 * 
	 * @param request : La richiesta HTTP inviata dal client.
	 * @param response : La risposta HTTP che verrà inviata al client.
	 * @throws ServletException : Se si verifica un errore specifico della servlet.
	 * @throws IOException : Se si verifica un errore di input/output.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
		if (u==null || u.getUsername().equals("")) {
			response.sendRedirect(request.getContextPath() + "/Autenticazione");
			return;
		}    

		//Recupero degli indirizzi dell'utente
		ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>) request.getAttribute("Indirizzi");
		if(indirizzi==null){
			try {
				loadUserAddresses(request, u);
			} catch (SQLException ex) {
				Logger.getLogger(AreaRiservata.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		request.getRequestDispatcher("/protected/cliente/AreaRiservata.jsp").forward(request, response);
	}

	/**
	 * Gestisce le richieste HTTP con metodo POST.
	 *
	 * Controlla se l'utente è autenticato, recuperando l'oggetto `ProxyUtente` dalla sessione.
	 * Se l'utente non è autenticato, reindirizza alla pagina di autenticazione.
	 * Se l'utente è autenticato, recupera gli indirizzi dell'utente se non sono già presenti nella richiesta.
	 * Infine, inoltra la richiesta alla pagina JSP `/protected/cliente/AreaRiservata.jsp`.
	 *
	 * @param request : la richiesta servlet
	 * @param response : la risposta servlet
	 * @throws ServletException : se si verifica un errore specifico della servlet
	 * @throws IOException : se si verifica un errore di I/O
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
		if (u==null || u.getUsername().equals("")) {
			response.sendRedirect(request.getContextPath() + "/Autenticazione");
			return;
		}   

		//Recupero degli indirizzi dell'utente
		ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>) request.getAttribute("Indirizzi");
		if(indirizzi==null){
			try {
				loadUserAddresses(request, u);
			} catch (SQLException ex) {
				Logger.getLogger(AreaRiservata.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		request.getRequestDispatcher("/protected/cliente/AreaRiservata.jsp").forward(request, response);
	}

	/**
	 * Carica gli indirizzi dell'utente dal database.
	 *
	 * @param request : la richiesta servlet
	 * @param utente : l'oggetto `ProxyUtente` che rappresenta l'utente
	 * @throws SQLException : se si verifica un errore con il database
	 */ 
	private void loadUserAddresses(HttpServletRequest request, ProxyUtente u) throws SQLException {
		
		ArrayList<Indirizzo> indirizzi = addressDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
		request.setAttribute("Indirizzi", indirizzi); 

	}
}
