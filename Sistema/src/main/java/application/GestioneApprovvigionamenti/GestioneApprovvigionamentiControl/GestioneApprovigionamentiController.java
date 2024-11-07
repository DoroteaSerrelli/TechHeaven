package application.GestioneApprovvigionamenti.GestioneApprovvigionamentiControl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import application.Autenticazione.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.GestioneApprovvigionamentiServiceImpl;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamento;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.CodiceRichiestaException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.DescrizioneDettaglioException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.FormatoFornitoreException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.ProdottoVendibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoDisponibileException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamentoException.QuantitaProdottoException;
import application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.Navigazione.NavigazioneControl.PaginationUtils;
import application.Navigazione.NavigazioneService.NavigazioneServiceImpl;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import storage.GestioneApprovvigionamentiDAO.ApprovvigionamentoDAODataSource;

/**
 *
 * @author raffa
 */
public class GestioneApprovigionamentiController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int perPage=50;
	private ProdottoDAODataSource pdao;
	private GestioneApprovvigionamentiServiceImpl gas;
	private PaginationUtils pu;


	public GestioneApprovigionamentiController() throws SQLException {
		// Recupera il DataSource configurato tramite JNDI
		DataSource ds = null;
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/techheaven");  // Assicurati che il nome JNDI sia corretto
		} catch (NamingException e) {
			throw new SQLException("Error initializing DataSource via JNDI: " + e.getMessage(), e);
		}

		// Crea le istanze delle classi DAO e dei servizi passando il DataSource configurato
		PagamentoDAODataSource paymentDAO = new PagamentoDAODataSource(ds);
		PhotoControl photoControl = new PhotoControl(ds);
		UtenteDAODataSource userDAO = new UtenteDAODataSource(ds);
		pdao = new ProdottoDAODataSource(ds, photoControl);
		OrdineDAODataSource orderDAO = new OrdineDAODataSource(ds);
		ApprovvigionamentoDAODataSource supplyDAO = new ApprovvigionamentoDAODataSource(ds);

		NavigazioneServiceImpl ns = new NavigazioneServiceImpl(pdao);
		GestioneCatalogoServiceImpl gcs = new GestioneCatalogoServiceImpl(pdao, photoControl);
		GestioneOrdiniServiceImpl gos = new GestioneOrdiniServiceImpl(orderDAO, userDAO, pdao, paymentDAO);
		pu = new PaginationUtils(ns, gcs, gos);
		gas = new GestioneApprovvigionamentiServiceImpl(supplyDAO);


	}


	//Costrutto per test
	public GestioneApprovigionamentiController(ProdottoDAODataSource pdao, GestioneApprovvigionamentiServiceImpl gas, 
			PaginationUtils pu) {
		this.pu = pu;
		this.pdao = pdao;
		this.gas = gas;
	}


	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = retrieveActionAndDetectChanges(request);

		int page = Integer.parseInt(request.getParameter("page"));
		request.getSession().setAttribute("action", action);
		request.getSession().setAttribute("page", page);
		// Fetch the previosly_fetched_page being the last page retrieved in the flow of instruction:
		// nextPageItems = > (if page==previous nextPage) I don't need to retrieve the items from the db
		// as I already have them available inside the session.
		int previoslyFetchedPage = pu.getSessionAttributeAsInt(request, "previosly_fetched_page", 0);

		if(action!=null && action.equals("viewProductList")){
			try {
				Collection <ProxyProdotto> currentPageResults;
				Collection <ProxyProdotto> nextPageResults;
				if(page==previoslyFetchedPage){                 
					currentPageResults = pu.getSessionCollection(request, "nextPageResults", ProxyProdotto.class);
					request.getSession().setAttribute("products", currentPageResults);              
				}
				else {
					currentPageResults = pdao.doRetrieveAll(null, page, perPage);
					request.getSession().setAttribute("products", currentPageResults);                  
				}               
				nextPageResults = pdao.doRetrieveAll(null, page+1, perPage);
				request.getSession().setAttribute("nextPageResults", nextPageResults);

				request.getSession().setAttribute("previosly_fetched_page", page+1);

				boolean hasNextPage = pu.checkIfItsTheSamePage (currentPageResults, nextPageResults, ProxyProdotto.class);   

				request.getSession().setAttribute("hasNextPage", hasNextPage);
				response.sendRedirect(request.getContextPath() + "/Approvigionamento");
			} catch (SQLException ex) {
				Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
				request.getSession().setAttribute("error", "Recupero Prodotti Fallito");
			} catch (Exception ex) {
				Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		if(action!=null && action.equals("viewList")){
			try {                                                    
				// Use a generic method to get the collection
				Collection<RichiestaApprovvigionamento> supplyRequests;
				Collection <RichiestaApprovvigionamento> nextPageResults;

				if(page==previoslyFetchedPage){                 
					//Datas about the current supply_request gets stored to compare it with the next Page data to make sure it's not
					//the same page being fetched, and disabling navigation control.
					supplyRequests = pu.getSessionCollection(request, "nextPageResults", RichiestaApprovvigionamento.class);
					// Handle the case where the session attribute is null               
					// Store the current page data in the session that being the previously fetched that in this case.
					request.getSession().setAttribute("supply_requests", supplyRequests);              
				}
				else {
					//I need to retrieve the data for supply_requests from the db othervise.
					supplyRequests = gas.visualizzaRichiesteFornitura(page, perPage);
					request.getSession().setAttribute("supply_requests", supplyRequests);                  
				}               

				//Retrieving the nextPage data from the db and setting it as nextPageResults:
				nextPageResults = gas.visualizzaRichiesteFornitura(page+1, perPage);
				request.getSession().setAttribute("nextPageResults", nextPageResults);

				//Setting the previosly_fetched page attribute inside the session to the value of nextPage. 
				request.getSession().setAttribute("previosly_fetched_page", page+1);

				//Verifico se le pagine sono identiche in caso affermativo il valore viene settato a false.
				// Impedendo nella jsp la navigazione alla prossima pagina.          
				// Get current and next page items
				boolean hasNextPage = pu.checkIfItsTheSamePage (supplyRequests, nextPageResults, RichiestaApprovvigionamento.class);   

				request.getSession().setAttribute("hasNextPage", hasNextPage);

				response.sendRedirect(request.getContextPath() + "/Approvigionamento");
			} catch (FormatoFornitoreException | DescrizioneDettaglioException | QuantitaProdottoException | ProdottoVendibileException | SQLException ex) {
				Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
				request.getSession().setAttribute("error", ex);
				//Servlet DO-GET GestioneOrdini TO-DO:
				response.sendRedirect(request.getContextPath() + "/GestioneOrdini");
			} catch (Exception ex) {
				Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String productIdParam = request.getParameter("product_id");

		if (productIdParam == null || productIdParam.isEmpty()) {

			CodiceRichiestaException ex = new CodiceRichiestaException("Errore nella generazione del codice della richiesta (codice = null).\n Riprovare più tardi.");

			request.getSession().setAttribute("error",ex.getMessage());                               
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			return;

		}
		try {
			int productId = -1;

			try {
				productId = Integer.parseInt(productIdParam); 
			}catch (NumberFormatException e) {

				CodiceRichiestaException ex = new CodiceRichiestaException("Errore nella generazione del codice della richiesta (codice non è un numero intero).\n Riprovare più tardi.");
				request.getSession().setAttribute("error",ex.getMessage());                               
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
				return;
			}

			ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);
			
			if(prodotto == null || !prodotto.isInCatalogo()) {
				
				ProdottoVendibileException ex = new ProdottoVendibileException("Non è possibile fare l'approvvigionamento di un prodotto non in catalogo.");
				request.getSession().setAttribute("error",ex.getMessage());                               
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
				return;
			}
			prodotto.setProductDAO(pdao);
			
			int quantity = Integer.parseInt(request.getParameter("quantity"));

			String fornitore = request.getParameter("fornitore");
			String email_fornitore = request.getParameter("email_fornitore");
			String descrizione = request.getParameter("descrizione");

			gas.effettuaRichiestaApprovvigionamento(prodotto, quantity, fornitore, email_fornitore, descrizione);
			request.getSession().setAttribute("error", "Richiesta Approvigionamento Avvenuta Con Successo!");            
			response.sendRedirect(request.getContextPath() + "/Approvigionamento");

		} catch (NumberFormatException | QuantitaProdottoException  e) {

			QuantitaProdottoException ex = new QuantitaProdottoException("La quantità del prodotto specificata non è valida");
			request.getSession().setAttribute("error",ex.getMessage());                               
			response.sendRedirect(request.getContextPath() + "/Approvigionamento");

		}catch(DescrizioneDettaglioException |
				FormatoFornitoreException | QuantitaProdottoDisponibileException |
				FormatoEmailException ex) {

			request.getSession().setAttribute("error",ex.getMessage());                               
			response.sendRedirect(request.getContextPath() + "/Approvigionamento");

		}catch(ProdottoVendibileException ex) {
			request.getSession().setAttribute("error","Non è possibile fare l'approvvigionamento di un prodotto non in catalogo.");                               
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} catch (SQLException ex) {
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().setAttribute("error", "Errore nell'elaborazione della richiesta di approvigionamento: errore nell'accesso al database.");
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} catch (SottocategoriaProdottoException | CategoriaProdottoException ex) {
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	// This method retrieve the parameter action and last_action from the session that keeps track
	// of the last selected action if they are different this means the user selected something different
	// and I need to reset session attributes related to the other action to avoid trobules with pagination.

	private String retrieveActionAndDetectChanges(HttpServletRequest request){
		String action = request.getParameter("action");
		String lastAction = (String) request.getSession().getAttribute("last_action");

		if (lastAction == null || !lastAction.equals(action)) {
			// Action has changed, reset all session attributes related to pagination
			request.getSession().removeAttribute("previosly_fetched_page");
			request.getSession().removeAttribute("nextPageResults");
			request.getSession().removeAttribute("supply_requests");
			request.getSession().removeAttribute("hasNextPage");
		}

		// Update the session with the current action
		request.getSession().setAttribute("last_action", action);
		return action;
	}
}
