package application.Navigazione.NavigazioneControl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffy
 */
@WebServlet(name = "NavigazioneController", urlPatterns = {"/NavigazioneController","/TechHeaven"})
public class NavigazioneController extends HttpServlet {

	private static final long serialVersionUID = 1L; 

	private int perPage=10;
	private PaginationUtils pu;

	//Costruttore
	public NavigazioneController(int perPage, PaginationUtils pu) {

		this.perPage = perPage;
		this.pu = pu;
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	public void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Imposta l'attributo di sessione previous_page con l'URL corrente
	    String currentPage = request.getRequestURI();
	    request.getSession().setAttribute("previous_page", currentPage);
		
		//Visualizzazione e paginazione dei risultati.

		String keyword = (String)request.getParameter("keyword");
		if (keyword == null || keyword.isEmpty()) {
			request.getSession().setAttribute("empty_search", "Compila questo campo.");
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;

		} 
		
		int page = 1;
		try {
			if (request.getParameter("page") != null) 
				page = Integer.parseInt( 
						request.getParameter("page")); 
		}catch(NumberFormatException e){
			page=1;
		}    
		request.getSession().setAttribute("keyword", keyword);

		// L'utility di Paginazione effettua la ricerca per tipo di Ricerca (barra - menu)
		// e compila il tutto in una classe searchResult che incapsula
		// i risultati della ricerca e il numero di risultati trovati.
		// Questi vengono passati al termine dell'elaborazione alla servlet 
		// dei risultati che si occuperà di Paginare il risultato della Ricerca.

		String searchType = request.getParameter("search_type");        
		request.getSession().setAttribute("search_type",searchType);

		if(searchType==null){
			
			response.sendRedirect(request.getContextPath() + "/");
			return;

		}
		pu.detectActionChanges(request, searchType);

		pu.paginateSearchedProducts(request, response, page, perPage, keyword, searchType);
		response.sendRedirect(request.getContextPath() + "/ResultsPage");

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
		processRequest(request, response);
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
		processRequest(request, response);
	}
}
