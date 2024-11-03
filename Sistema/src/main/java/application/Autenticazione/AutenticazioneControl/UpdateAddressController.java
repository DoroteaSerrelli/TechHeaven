package application.Autenticazione.AutenticazioneControl;

import application.Autenticazione.AutenticazioneService.AutenticazioneException;
import application.Autenticazione.AutenticazioneService.AutenticazioneServiceImpl;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.IndirizzoEsistenteException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.ModificaIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.RimozioneIndirizzoException;
import application.Autenticazione.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import application.Registrazione.RegistrazioneService.RegistrazioneException;
import application.Registrazione.RegistrazioneService.Utente;
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;

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

import org.apache.tomcat.jdbc.pool.DataSource;

/**
 * Servlet che gestisce l'aggiornamento degli indirizzi dell'utente.
 * @author raffy
 */
@WebServlet(name = "UpdateAddressController", urlPatterns = {"/UpdateAddressController"})
public class UpdateAddressController extends HttpServlet {

	/**
	 * serialVersionUID : È un campo statico finale a lungo raggio utilizzato 
	 * per la serializzazione dell'oggetto.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AutenticazioneServiceImpl loginService;
	private IndirizzoDAODataSource addressDao;

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

		loginService = new AutenticazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);
	}


	//Costrutto per test
	public UpdateAddressController(IndirizzoDAODataSource addressDAO, AutenticazioneServiceImpl loginService) {
		this.loginService = loginService;
		this.addressDao = addressDAO;

	}	


	/**
	 * Gestisce la richiesta HTTP GET, inoltrandola al metodo doPost.
	 *
	 * @param request : servlet request
	 * @param response : servlet response
	 * @throws ServletException : se si verifica un errore specifico della servlet
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
	 * Questo metodo permette all'utente di aggiungere, rimuovere o aggiornare i propri indirizzi.
	 *
	 * @param request : servlet request
	 * @param response : servlet response
	 * @throws ServletException : se si verifica un errore specifico della servlet
	 * @throws IOException : se si verifica un errore di I/O
	 */	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String action = request.getParameter("action");

			String via = request.getParameter("newVia");
			String numCivico = request.getParameter("newNumCivico");
			String cap = request.getParameter("newCap");
			String citta = request.getParameter("newCitta");
			String provincia = request.getParameter("newProvincia");

			ProxyUtente user = getUser(request);

			//Se l'utente che richiede l'operazione non è autenticato, egli viene indirizzato alla pagina di login

			if(user==null) {
				response.sendRedirect(request.getContextPath() + "/Autenticazione");
				return;
			}

			ProxyUtente updated_user = null;

			request.getSession().setAttribute("field", "address");  // Field Selezionata per modifica
			request.getSession().setAttribute("currentAction", request.getParameter("action"));    
			Indirizzo target_ind = new Indirizzo(via, numCivico, citta, cap, provincia);

			switch(action){

			case "AGGIUNGERE-INDIRIZZO":

				updated_user= loginService.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);
				break;

			case "RIMUOVERE-INDIRIZZO":

				Utente real_user = user.mostraUtente();

				int idIndirizzoR = -1;
				String addressIndex = request.getParameter("addressIndex");

				if(addressIndex!= null && !addressIndex.isBlank())
					idIndirizzoR = Integer.parseInt(request.getParameter("addressIndex"));
				else
					throw new FormatoIndirizzoException("Specificare l'indirizzo di spedizione da rimuovere.");

				Indirizzo target_indRem = new Indirizzo(idIndirizzoR, via, numCivico, citta, cap, provincia);

				//target_ind.setIDIndirizzo(idIndirizzoR);
				//fetchIndexById(real_user, target_ind);                                				
				System.out.println("USER prima della chiamata: " + user);
				System.out.println("TARGET_IND prima della chiamata: " + target_indRem);
				System.out.println("ACTION: " + action);


				updated_user = loginService.aggiornaRubricaIndirizzi(user, "RIMUOVERE-INDIRIZZO", target_indRem);

				System.out.println("UPDATED_USER dopo la chiamata: " + updated_user);

				break; 

			case "AGGIORNARE-INDIRIZZO":

				Utente realUser = user.mostraUtente();

				int idIndirizzoU = -1;

				if(request.getParameter("addressIndex")!= null && !request.getParameter("addressIndex").isBlank())
					idIndirizzoU = Integer.parseInt(request.getParameter("addressIndex"));                    
				else {
					throw new ModificaIndirizzoException("L'indirizzo inserito non è presente nella tua rubrica degli indirizzi.");			
				}

				target_ind.setIDIndirizzo(idIndirizzoU);
				
				//Indirizzo address = addressDao.doRetrieveByKey(idIndirizzoU, user.getUsername());

				/*
				if(address.equals(target_ind)) { //l'indirizzo aggiornato già esiste nella rubrica degli indirizzi

					String errorMsg = "L'indirizzo inserito è già presente nella tua rubrica degli indirizzi.";
					request.getSession().setAttribute("error", errorMsg);
					response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");
					return;
				}*/

					updated_user = loginService.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", target_ind);
				
				break;  

			default:

				InformazioneDaModificareException ex = new InformazioneDaModificareException("Seleziona una informazione da modificare : AGGIUNGERE-INDIRIZZO, RIMUOVERE-INDIRIZZO, AGGIORNARE-INDIRIZZO.");
				request.getSession().setAttribute("error", ex.getMessage());
				response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");
				return;  
			}
			request.getSession().setAttribute("user", updated_user);
			response.sendRedirect(request.getContextPath() + "/AreaRiservata");      
			
		}catch(InformazioneDaModificareException ex) {

			String errorMsg = "Seleziona una informazione da modificare : AGGIUNGERE-INDIRIZZO, RIMUOVERE-INDIRIZZO, AGGIORNARE-INDIRIZZO.";
			request.getSession().setAttribute("error", errorMsg);
			response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		}catch(FormatoIndirizzoException ex) {

			request.getSession().setAttribute("error", ex.getMessage());
			request.getSession().setAttribute("field", "address");  // Assuming we are working with addresses
			request.getSession().setAttribute("currentAction", request.getParameter("action"));
			response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		}catch(IndirizzoEsistenteException ex) {

			String errorMsg = "L'indirizzo inserito è già presente nella tua rubrica degli indirizzi.";
			request.getSession().setAttribute("error", errorMsg);
			response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		}catch(RimozioneIndirizzoException | ModificaIndirizzoException ex) {

			String errorMsg = "L'indirizzo inserito non è presente nella tua rubrica degli indirizzi.";
			request.getSession().setAttribute("error", errorMsg);                     
			response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		} catch (UtenteInesistenteException | SQLException ex) {

			Logger.getLogger(UpdateAddressController.class.getName()).log(Level.SEVERE, null, ex);
			String errormsg = "Errore durante la modifica delle informazioni";
			request.getSession().setAttribute("error", errormsg);                   
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");          

		} catch (AutenticazioneException.ProfiloInesistenteException | RegistrazioneException.FormatoViaException | RegistrazioneException.FormatoNumCivicoException | RegistrazioneException.FormatoCittaException | RegistrazioneException.FormatoCAPException | RegistrazioneException.FormatoProvinciaException ex) {
			Logger.getLogger(UpdateAddressController.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().setAttribute("error", ex.getMessage());
			response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");
		}    
	}

	/**
	 * Recupera l'utente corrente dalla sessione HTTP.
	 *
	 * @param request : La richiesta HTTP.
	 * @return user : L'utente corrente, se presente nella sessione. Altrimenti, null.
	 */
	private ProxyUtente getUser(HttpServletRequest request) {

		ProxyUtente user = (ProxyUtente) request.getSession().getAttribute("user");
		if (user == null)
			return null;

		return user;
	}

	/***  
	 * Recupera l'ID dell'indirizzo dalla lista in caso in cui le info non combaciano 
	 * Si verifica quando l'utente inserisce le informazioni direttamente nel form senza
	 * interagire con la Selezione Rapida (click su Indrizzo da modificare-eliminare)
	 * 
	 * @param user : Utente a cui appartiene la lista indirizzi.
	 * @param index: Indrizzo da controllare nella lista
	 * @return user : L'utente corrente, se presente nella sessione. Altrimenti, null.
	 */
	private void fetchIndexById(Utente user, Indirizzo index){
		ArrayList<Indirizzo> lista_indirizzi = user.getProfile().getIndirizzi();
		if(lista_indirizzi.contains(index)){
			for(Indirizzo ind : lista_indirizzi){
				if(index.equals(ind) && index.getIDIndirizzo()!=ind.getIDIndirizzo()){
					index.setIDIndirizzo(ind.getIDIndirizzo());                     
				} 
			}
		}
	}      
}
