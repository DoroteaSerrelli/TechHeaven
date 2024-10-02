package application.GestioneWishlistControl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet che gestisce la wishlist dell'utente.
 *
 * @author raffa
 */
public class Wishlist extends HttpServlet {

	/**
	 * serialVersionUID : È un campo statico finale a lungo raggio utilizzato 
	 * per la serializzazione dell'oggetto.
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Gestisce la richiesta HTTP GET.
     *
     * Invia la richiesta al metodo doPost(HttpServletRequest, HttpServletResponse)
     * per mantenere la coerenza con il metodo POST.
     *
     * @param request : la richiesta HTTP
     * @param response : la risposta HTTP
     * @throws ServletException : se si verifica un errore specifico della servlet
     * @throws IOException : se si verifica un errore di input/output
     */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
     * Gestisce la richiesta HTTP POST.
     *
     * Controlla se l'attributo "Wishlist" è presente nella sessione dell'utente.
     * Se l'attributo è assente, reindirizza l'utente al controller GestioneWishlistController
     * con l'azione "viewwishlist" per visualizzare la wishlist.
     *
     * Infine, inoltra la richiesta alla pagina adibita per la wishlist.
     *
     * @param request : la richiesta HTTP
     * @param response : la risposta HTTP
     * @throws ServletException : se si verifica un errore specifico della servlet
     * @throws IOException : se si verifica un errore di input/output
     */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		if(request.getSession().getAttribute("Wishlist")==null){
			System.out.println("STO IN WISHLIST.JAVA");
			response.sendRedirect(request.getContextPath() + "/GestioneWishlistController?action=viewwishlist");
			return;
		}
		
		String errormsg = (String)request.getSession().getAttribute("errormsg");
		String status = (String)request.getSession().getAttribute("status");
		
		//Controllo se so sono avvenuti errori e nel caso visualizzo il messaggio
		// salvandolo come attributo, e lo elimino dagli attributi di sessione.
		
		if(errormsg!=null){
			request.setAttribute("error", errormsg);
			request.setAttribute("status", status);

			request.getSession().removeAttribute("errormsg");
			request.getSession().removeAttribute("status");

		}

		request.getRequestDispatcher("/protected/cliente/wishlist.jsp").forward(request, response);
	}

}
