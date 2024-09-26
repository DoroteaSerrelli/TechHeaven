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
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
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
			//Non ci sono utenti nel database con il nome utente pari a username
			IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();

			ArrayList <Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());        
			request.getSession().setAttribute("user", u);
			request.setAttribute("Indirizzi",indirizzi);
			response.sendRedirect(request.getContextPath() + "/AreaRiservata");
		
		/*}catch(NullPointerException e) {
			String message = "Non e\' possibile associare l'username inserita al tuo account.\n "
					+ "Riprova la registrazione inserendo un'altra username.";
			request.getSession().setAttribute("errorMessage", message);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			*/
		} catch (UtentePresenteException e) {
			request.getSession().setAttribute("errorMessage", e.getMessage());
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		}catch(SQLException e) {
			Logger.getLogger(RegistrazioneController.class.getName()).log(Level.SEVERE, null, e);
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath()+"/Registrazione");
		}
	}
}