package application.AutenticazioneControl;

import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

/**
 * Servlet che permette la visualizzazione e l'aggiornamento delle informazioni personali dell'utente.
 *
 * Questa servlet recupera le informazioni dell'utente attualmente autenticato e le inoltra
 * alla pagina JSP `protected/cliente/updateUserInfo.jsp` per la visualizzazione.
 *
 * @author raffa
 */

public class UpdateUserInfo extends HttpServlet {

	/**
	 * serialVersionUID : È un campo statico finale a lungo raggio utilizzato 
	 * per la serializzazione dell'oggetto.
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Gestisce le richieste HTTP GET e POST.
     *
     * Questo metodo controlla se l'utente è autenticato. Se l'utente non è autenticato,
     * viene reindirizzato alla pagina di autenticazione. Altrimenti, recupera le informazioni 
     * dell'utente (inclusa la lista di indirizzi) e le inoltra alla pagina JSP dedicata 
     * all'aggiornamento dei dati personali.
     *
     * @param request : servlet request
     * @param response : servlet response
     * @throws ServletException : se si verifica un errore nella servlet
     * @throws IOException : se si verifica un errore di I/O
     */
	
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
        ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
        if (u==null || u.getUsername().equals("")) {
           response.sendRedirect(request.getContextPath() + "Autenticazione");
           return;
        }    
        
        ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>) request.getAttribute("Indirizzi");
        if(indirizzi==null){
            try {
                loadUserAddresses(request, u);
            } catch (SQLException ex) {
            	String error = "Errore nel recupero della tua rubrica degli indirizzi.";
				request.setAttribute("error", error);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
            }
        }

        request.getRequestDispatcher("protected/cliente/updateUserInfo.jsp").forward(request, response);
    }
    
    
    /**
     * Recupera la lista degli indirizzi dell'utente.
     *
     * Questo metodo recupera la lista degli indirizzi associati all'utente autenticato.
     * 
     * @param request : servlet request
     * @param utente : l'utente autenticato
     * @throws SQLException : se si verifica un errore durante l'accesso al database
     */
    
    private void loadUserAddresses(HttpServletRequest request, ProxyUtente u) throws SQLException {
        IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();
        ArrayList<Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
        request.setAttribute("Indirizzi", indirizzi); 
       
    }
    
    /**
     * Gestice la richiesta HTTP GET.
     *
     * Delega l'elaborazione alla funzione processRequest
     *
     * @param request : servlet request
     * @param response : servlet response
     * @throws ServletException : se si verifica un errore nella servlet
     * @throws IOException : se si verifica un errore di I/O
     */
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Gestice la richiesta HTTP POST.
     *
     * Delega l'elaborazione alla funzione processRequest
     *
     * @param request : servlet request
     * @param response : servlet response
     * @throws ServletException : se si verifica un errore nella servlet
     * @throws IOException : se si verifica un errore di I/O
     */
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}