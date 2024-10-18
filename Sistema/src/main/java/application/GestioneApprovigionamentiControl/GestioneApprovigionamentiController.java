package application.GestioneApprovigionamentiControl;

import application.AutenticazioneService.AutenticazioneException.FormatoEmailException;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiServiceImpl;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamento;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException;
import application.GestioneApprovvigionamenti.RichiestaApprovvigionamentoException.QuantitaProdottoDisponibileException;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneControl.SearchResult;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProxyProdotto;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import storage.GestioneApprovvigionamentiDAO.ApprovvigionamentoDAODataSource;
import storage.NavigazioneDAO.ProdottoDAODataSource;

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

	private ProdottoDAODataSource pdao;
	private GestioneApprovvigionamentiServiceImpl gas;
	private PaginationUtils pu;
	private ApprovvigionamentoDAODataSource supplyDAO;
	
	@Override
	public void init() throws ServletException {
		supplyDAO = new ApprovvigionamentoDAODataSource(new DataSource());
		gas = new GestioneApprovvigionamentiServiceImpl();
		pdao = new ProdottoDAODataSource();
		pu = new PaginationUtils();
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
			} catch (RichiestaApprovvigionamentoException.FormatoFornitoreException | RichiestaApprovvigionamentoException.DescrizioneDettaglioException | RichiestaApprovvigionamentoException.QuantitaProdottoException | RichiestaApprovvigionamentoException.ProdottoVendibileException | SQLException ex) {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String productIdParam = request.getParameter("product_id");

		if (productIdParam == null || productIdParam.isEmpty()) {
			// Handle the case where product_id is missing
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is required.");
			return;
		}
		try {

			int productId = Integer.parseInt(productIdParam); 
			ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);
			int quantity = Integer.parseInt(request.getParameter("quantity"));
			
			String fornitore = request.getParameter("fornitore");
			String email_fornitore = request.getParameter("email_fornitore");
			String descrizione = request.getParameter("descrizione");
			
			gas.effettuaRichiestaApprovvigionamento(prodotto, quantity, fornitore, email_fornitore, descrizione);
			request.getSession().setAttribute("error", "Richiesta Approvigionamento Avvenuta Con Successo!");            
			response.sendRedirect(request.getContextPath() + "/Approvigionamento");
		
		} catch (NumberFormatException e) {
			// Handle invalid number format
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Product ID format.");
		} catch (SQLException | RichiestaApprovvigionamentoException.FormatoFornitoreException | RichiestaApprovvigionamentoException.QuantitaProdottoException | RichiestaApprovvigionamentoException.DescrizioneDettaglioException | RichiestaApprovvigionamentoException.ProdottoVendibileException ex) {
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().setAttribute("error", "Richiesta approvigionamento non valida, c'è stato un errore.");
			response.sendRedirect(request.getContextPath() + "/Approvigionamento");
		} catch (ProdottoException.SottocategoriaProdottoException ex) {
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ProdottoException.CategoriaProdottoException ex) {
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
		} catch (QuantitaProdottoDisponibileException e) {
			
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, e);
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath() + "/Approvigionamento");
			
		} catch (FormatoEmailException e) {
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, e);
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect(request.getContextPath() + "/Approvigionamento");
		}
	}
}
