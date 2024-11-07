package application.Navigazione.NavigazioneControl;

import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiControl.GestioneApprovigionamentiController;
import application.GestioneApprovvigionamenti.GestioneApprovvigionamentiService.RichiestaApprovvigionamento;
import application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.ProxyOrdine;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.Navigazione.NavigazioneService.NavigazioneService;
import application.Navigazione.NavigazioneService.NavigazioneServiceImpl;
import application.Navigazione.NavigazioneService.ProdottoException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.NavigazioneException.ErroreRicercaCategoriaException;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

/**
 *
 * @author raffy
 */

public class PaginationUtils {

	private NavigazioneServiceImpl navi_service;
	private GestioneCatalogoServiceImpl catalogoService;
	private GestioneOrdiniServiceImpl ordiniService;
	
	public PaginationUtils() throws SQLException {
		
			DataSource ds = null;
	        try {
	            Context initContext = new InitialContext();
	            Context envContext = (Context) initContext.lookup("java:/comp/env");
	            ds = (DataSource) envContext.lookup("jdbc/techheaven");  // Assicurati che il nome JNDI sia corretto
	            
	         // Crea le istanze delle classi DAO e dei servizi passando il DataSource configurato
	            PagamentoDAODataSource paymentDAO = new PagamentoDAODataSource(ds);
	            PhotoControl photoControl = new PhotoControl(ds);
	            UtenteDAODataSource userDAO = new UtenteDAODataSource(ds);
	            ProdottoDAODataSource productDAO = new ProdottoDAODataSource(ds, photoControl);
	            OrdineDAODataSource orderDAO = new OrdineDAODataSource(ds);
	            ordiniService = new GestioneOrdiniServiceImpl(orderDAO, userDAO, productDAO, paymentDAO);
	            catalogoService = new GestioneCatalogoServiceImpl(productDAO, photoControl);
	            navi_service = new NavigazioneServiceImpl(productDAO);

	        } catch (NamingException e) {
	            throw new SQLException("Error initializing DataSource via JNDI: " + e.getMessage(), e);
	        }
	        
		
	}

	public PaginationUtils(NavigazioneServiceImpl navi_service, GestioneCatalogoServiceImpl catalogoService, GestioneOrdiniServiceImpl ordiniService) {
		this.catalogoService = catalogoService;
		this.navi_service = navi_service;
		this.ordiniService = ordiniService;
	}

	public Collection<ProxyProdotto> performPagination(String keyword, int page, int resultsPerPage, String searchType) throws ErroreRicercaCategoriaException {

		SearchResult res = new SearchResult();
		Collection <ProxyProdotto> results;
		switch (searchType) {
		case "bar" -> {
			try {
				results = navi_service.ricercaProdottoBar(keyword, page, resultsPerPage);
				return results;
			} catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException ex) {
				Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

		case "menu" -> {
			try {
				results = navi_service.ricercaProdottoMenu(keyword, page, resultsPerPage);
				return results;
			} catch (SQLException | ProdottoException.CategoriaProdottoException | ProdottoException.SottocategoriaProdottoException ex) {
				Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		default -> // Handle unknown search types or throw an exception
		throw new IllegalArgumentException("Invalid search type: " + searchType);
		}
		return null;
	}


	public Collection<ProxyProdotto> performPagination(int page, int resultsPerPage) {
		try {      
			return catalogoService.visualizzaCatalogo(page, resultsPerPage);
		} catch (SQLException | ProdottoException.CategoriaProdottoException | ProdottoException.SottocategoriaProdottoException ex) {
			Logger.getLogger(PaginationUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public Collection<ProxyOrdine> performPagination(int page, int resultsPerPage, String action) throws SQLException, ErroreTipoSpedizioneException {
		if(action.matches("fetch_da_spedire")){
			return ordiniService.visualizzaOrdiniDaEvadere(page, resultsPerPage); 
		}   
		else if(action.matches("fetch_spediti"))
			return ordiniService.visualizzaOrdiniEvasi(page, resultsPerPage);
		else return null;
	}  


	public void paginateSearchedProducts(HttpServletRequest request, HttpServletResponse response, int page, int resultsPerPage,String keyword, String searchType){
		//searchType ---> pseudo_action                 
		request.getSession().setAttribute("page", page);
		// Fetch the previosly_fetched_page being the last page retrieved in the flow of instruction:
		// nextPageItems = > (if page==previous nextPage) I don't need to retrieve the items from the db
		// as I already have them available inside the session.
		int previoslyFetchedPage = getSessionAttributeAsInt(request, "previosly_fetched_page", 0);
		Collection <ProxyProdotto> currentPageResults = null;
		Collection <ProxyProdotto> nextPageResults = null;
		try {            
			if(page==previoslyFetchedPage){                 
				currentPageResults = getSessionCollection(request, "nextPageResults", ProxyProdotto.class);
				request.getSession().setAttribute("products", currentPageResults);              
			}
			else {
				if(searchType!=null && searchType.equals("bar"))
					currentPageResults = navi_service.ricercaProdottoBar(keyword, page, resultsPerPage);
				else {
					try {
						currentPageResults = navi_service.ricercaProdottoMenu(keyword, page, resultsPerPage);
					}catch(ErroreRicercaCategoriaException e) {
						// Reindirizza alla pagina precedente
						String previousPage = (String) request.getSession().getAttribute("previous_page");
						if (previousPage != null) {
							response.sendRedirect(previousPage);
							return;
						}
					}
				}
				request.getSession().setAttribute("products", currentPageResults);                  
			}          
			if(searchType!=null && searchType.equals("bar"))
				nextPageResults = navi_service.ricercaProdottoBar(keyword, page+1, resultsPerPage);
			else {
				try {
					nextPageResults = navi_service.ricercaProdottoMenu(keyword, page+1, resultsPerPage);
				}catch(ErroreRicercaCategoriaException e) {
					// Reindirizza alla pagina precedente
					String previousPage = (String) request.getSession().getAttribute("previous_page");
					if (previousPage != null) {
						response.sendRedirect(previousPage);
						return;
					}
				}
			}
			request.getSession().setAttribute("nextPageResults", nextPageResults);

			request.getSession().setAttribute("previosly_fetched_page", page+1);

			boolean hasNextPage = checkIfItsTheSamePage (currentPageResults, nextPageResults, ProxyProdotto.class);                               
			request.getSession().setAttribute("hasNextPage", hasNextPage);
			request.getSession().setAttribute("keyword", keyword);
		} catch (SQLException ex) {
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().setAttribute("error", "Recupero Prodotti Fallito");
		} catch (Exception ex) {
			Logger.getLogger(GestioneApprovigionamentiController.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().removeAttribute("products");
		}
	}   

	// Utility method to retrieve session attribute as an Integer with a default value if null.
	public int getSessionAttributeAsInt(HttpServletRequest request, String attributeName, int defaultValue) {
		Integer value = (Integer) request.getSession().getAttribute(attributeName);
		return value != null ? value : defaultValue;
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<T> getSessionCollection(HttpServletRequest request, String attributeName, Class<T> type) {
		Collection<T> collection = (Collection<T>) request.getSession().getAttribute(attributeName);
		if (collection == null) {
			collection = new ArrayList<>();
		}
		return collection;
	}
	public Integer getId(Object item, Class<?> clazz) throws Exception {
		// Dynamically determine the method based on the class type
		// Determine the method name based on the class type
		String methodName;
		if (clazz == ProxyProdotto.class) {
			methodName = "getCodiceProdotto";
		} else if (clazz == ProxyOrdine.class) {
			methodName = "getCodiceOrdine";
		} else if (clazz == RichiestaApprovvigionamento.class) {
			methodName = "getCodiceRifornimento";
		} else {
			throw new IllegalArgumentException("Unsupported class type: " + clazz.getName());
		}      
		java.lang.reflect.Method method = clazz.getMethod(methodName);
		Object result = method.invoke(item);
		return result != null ? (Integer) result : null;
	}
	//Metodo che verifica se sto osservando la stessa pagina
	public  <T> boolean checkIfItsTheSamePage(Collection <T> currentPageItems, Collection <T> nextPageItems, Class<T> clazz) throws Exception{       
		Integer currentPageItemId = 1;
		Integer nextPageItemId = 1;
		// Using Generic Types to avoid redundant code we retrieve the first item of each Collection.
		// Extract the first item from each collection (changes based on the action attribute)
		T firstCurrentPageItem = currentPageItems.isEmpty() ? null : currentPageItems.iterator().next();
		T firstNextPageItem = nextPageItems.isEmpty() ? null : nextPageItems.iterator().next();

		///We retrieve the first item identifier based on the Collection class.
		if (firstCurrentPageItem != null) {
			currentPageItemId = getId(firstCurrentPageItem, clazz);
		}

		if (firstNextPageItem != null) {
			nextPageItemId = getId(firstNextPageItem, clazz);
		}      
		// Debugging: Print IDs
		System.out.println("Current Page Item ID: " + currentPageItemId);
		System.out.println("Next Page Item ID: " + nextPageItemId);

		// Check if the first item ID of the next page is the same as the first item ID of the current page
		boolean isSameAsCurrentPage = currentPageItemId != null && currentPageItemId.equals(nextPageItemId);

		// Set hasNextPage based on whether nextPageItems is empty or has the same first item ID as currentPageItems
		return nextPageItems != null && !nextPageItems.isEmpty() && !isSameAsCurrentPage;
	}

	// This method retrieve the parameter action and last_action from the session that keeps track
	// of the last selected action if they are different this means the user selected something different
	// and I need to reset session attributes related to the other action to avoid trobules with pagination.
	public void detectActionChanges(HttpServletRequest request, String action){
		String lastAction = (String) request.getSession().getAttribute("last_action");

		if (lastAction == null || !lastAction.equals(action)) {
			// Action has changed, reset all session attributes related to pagination
			request.getSession().removeAttribute("previosly_fetched_page");
			request.getSession().removeAttribute("nextPageResults");
			request.getSession().removeAttribute("products");
			request.getSession().removeAttribute("hasNextPage");
		}

		// Update the session with the current action
		request.getSession().setAttribute("last_action", action);
	}
}


/*In caso di "si voglia" cambiare l'implementazione lascio questi metodi commentati che includon getTotalRecords.
    public static void setPaginationAttributes(HttpServletRequest request, SearchResult searchResult, String keyword, int resultsPerPage) {
        int totalRecords = searchResult.getTotalRecords();
        int totalPages = (int) Math.ceil((double) totalRecords / resultsPerPage);

        request.setAttribute("products", searchResult.getProducts());
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);

    }
    public static void setPaginationAttributes(HttpServletRequest request, SearchResult searchResult, int resultsPerPage) {
        int totalRecords = searchResult.getTotalRecords();
        int totalPages = (int) Math.ceil((double) totalRecords / resultsPerPage);

        request.setAttribute("products", searchResult.getProducts());
        request.setAttribute("totalPages", totalPages);        
    }*/