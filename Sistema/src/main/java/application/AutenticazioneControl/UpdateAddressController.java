package application.AutenticazioneControl;

import application.AutenticazioneService.AutenticazioneException;
import application.AutenticazioneService.AutenticazioneException.InformazioneDaModificareException;
import application.AutenticazioneService.AutenticazioneException.FormatoIndirizzoException;
import application.AutenticazioneService.AutenticazioneException.IndirizzoEsistenteException;
import application.AutenticazioneService.AutenticazioneException.RimozioneIndirizzoException;
import application.AutenticazioneService.AutenticazioneException.ModificaIndirizzoException;
import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.RegistrazioneService.Indirizzo;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.RegistrazioneException;
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
/* Servlet che gestisce l'aggiornamento degli indirizzi dell'utente.
/**
 *
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String action = request.getParameter("action");

			String via = request.getParameter("newVia");
			String numCivico = request.getParameter("newNumCivico");
			String cap = request.getParameter("newCap");
			String citta = request.getParameter("newCitta");
			String provincia = request.getParameter("newProvincia");

			Indirizzo target_ind = new Indirizzo(via, numCivico, citta, cap, provincia);

			ProxyUtente user = getUser(request);

			//Se l'utente che richiede l'operazione non è autenticato, egli viene indirizzato alla pagina di login

			if(user==null) {
				response.sendRedirect(request.getContextPath() + "/Autenticazione");
				return;
			}

			ProxyUtente updated_user = user;

			Utente real_user = user.mostraUtente();
                        
                        request.getSession().setAttribute("field", "address");  // Field Selezionata per modifica
                        request.getSession().setAttribute("currentAction", request.getParameter("action"));    
			switch(action){

			case "AddIndirizzo":

				AutenticazioneServiceImpl asAdd = new AutenticazioneServiceImpl();
				updated_user= asAdd.aggiornaRubricaIndirizzi(user, "AGGIUNGERE-INDIRIZZO", target_ind);
				break;

			case "RemoveIndirizzo":

				int idIndirizzoR = Integer.parseInt(request.getParameter("addressIndex"));
				target_ind.setIDIndirizzo(idIndirizzoR);

				AutenticazioneServiceImpl asRem = new AutenticazioneServiceImpl();
				updated_user= asRem.aggiornaRubricaIndirizzi(user, "RIMUOVERE-INDIRIZZO", target_ind);
				break; 

			case "UpdateIndirizzo":

				int idIndirizzoU = real_user.getProfile().getIndirizzi().get(0).getIDIndirizzo();
				if(request.getParameter("addressIndex")!=null)
					idIndirizzoU = Integer.parseInt(request.getParameter("addressIndex"));                    
				target_ind.setIDIndirizzo(idIndirizzoU);
				AutenticazioneServiceImpl asUp = new AutenticazioneServiceImpl();
				IndirizzoDAODataSource addressDao = new IndirizzoDAODataSource();
				Indirizzo address = addressDao.doRetrieveByKey(idIndirizzoU, user.getUsername());
				
				if(address.equals(target_ind)) { //l'indirizzo aggiornato già esiste nella rubrica degli indirizzi
					
					String errorMsg = "L\\'indirizzo inserito è già presente nella tua rubrica degli indirizzi.";
					request.getSession().setAttribute("error", errorMsg);
					response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");
					return;
				}
					
				updated_user= asUp.aggiornaRubricaIndirizzi(user, "AGGIORNARE-INDIRIZZO", target_ind);
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

			String errorMsg = "L\\'indirizzo inserito è già presente nella tua rubrica degli indirizzi.";
			request.getSession().setAttribute("error", errorMsg);
			response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");

		}catch(RimozioneIndirizzoException | ModificaIndirizzoException ex) {

			String errorMsg = "L\\'indirizzo inserito non è presente nella tua rubrica degli indirizzi.";
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
}
