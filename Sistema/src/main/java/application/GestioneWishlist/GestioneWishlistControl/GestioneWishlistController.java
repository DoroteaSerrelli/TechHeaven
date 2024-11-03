package application.GestioneWishlist.GestioneWishlistControl;

import application.GestioneWishlist.GestioneWishlistService.GestioneWishlistServiceImpl;
import application.GestioneWishlist.GestioneWishlistService.Wishlist;
import application.GestioneWishlist.GestioneWishlistService.WishlistException.ProdottoNonPresenteException;
import application.GestioneWishlist.GestioneWishlistService.WishlistException.ProdottoNulloException;
import application.GestioneWishlist.GestioneWishlistService.WishlistException.ProdottoPresenteException;
import application.GestioneWishlist.GestioneWishlistService.WishlistException.WishlistVuotaException;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.Registrazione.RegistrazioneService.ProxyUtente;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import storage.WishlistDAO.WishlistDAODataSource;

/**
 * Servlet che gestisce le operazioni sulla wishlist dell'utente.
 * 
 * @author Dorotea Serrelli
 * @author raffa
 */
public class GestioneWishlistController extends HttpServlet {

	/**
	 * serialVersionUID : Versione seriale della servlet (necessaria per la serializzazione).
	 */
	private static final long serialVersionUID = 1L;
	private ProdottoDAODataSource pdao;
	private WishlistDAODataSource wishlistDAO;
	private GestioneWishlistServiceImpl gws;


	@Override
	public void init() throws ServletException {
		DataSource ds = mock(DataSource.class);
		PhotoControl photoControl = new PhotoControl(ds);

		try {
			pdao = new ProdottoDAODataSource(ds, photoControl);
			wishlistDAO = new WishlistDAODataSource(ds);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		gws = new GestioneWishlistServiceImpl(wishlistDAO);

	}

	//Costruttore per il test

	public GestioneWishlistController(ProdottoDAODataSource productDAO, WishlistDAODataSource wishlistDAO, GestioneWishlistServiceImpl gws) {
		this.pdao = productDAO;
		this.wishlistDAO = wishlistDAO;
		this.gws = gws;
	}



	/**
	 * Recupera la wishlist dell'utente dalla sessione. Se la wishlist non è presente,
	 * la crea e la salva nel database.
	 *
	 * @param request : la richiesta HTTP
	 * @param user : l'utente corrente
	 * @return la wishlist dell'utente
	 * @throws CategoriaProdottoException : se si verifica un errore durante il recupero
	 *                                    delle categorie dei prodotti
	 * @throws SQLException : se si verifica un errore di connessione al database
	 */

	private Wishlist createNewWishlistIfNotExists(HttpServletRequest request, ProxyUtente user) throws CategoriaProdottoException{
		try {

			Wishlist w = (Wishlist)request.getSession().getAttribute("Wishlist");

			if(w==null){
				//Determino il numero di wishlist dell'utente
				int check_user_wishlist = wishlistDAO.getWishlistCount(user); 
				//System.out.println("Numero wishlist utente: "+ check_user_wishlist);
				if(check_user_wishlist == 0){ //l'utente non possiede una wishlist
					w =  new Wishlist(user);
					//System.out.println("STO CREANDO WISHLIST");
					wishlistDAO.doSaveWishlist(w);
					w = wishlistDAO.doRetrieveAllWishUser(user);
					request.getSession().removeAttribute("Wishlist");
					request.getSession().setAttribute("Wishlist", w);

				}else{
					System.out.println("STO check_user : " + check_user_wishlist);

					try {
						w = wishlistDAO.doRetrieveAllWishUser(user);
						request.getSession().removeAttribute("Wishlist");
						request.getSession().setAttribute("Wishlist", w);

					} catch (CategoriaProdottoException ex) {
						Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
					}

				}

			}
			//System.out.println("w in createIfNotExists è con id: " + w.getId());
			return w;
		} catch (SQLException ex) {
			Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println(ex.getMessage());
		}
		return null;
	}

	/**
	 * Converte una stringa che rappresenta l'identificativo di un prodotto in un intero.
	 *
	 * @param pid : la stringa che rappresenta l'identificativo del prodotto
	 * @return l'identificativo del prodotto come intero
	 */

	private int parseProductId(String pid) {

		if (pid != null && !pid.isEmpty()) {
			return Integer.parseInt(pid);
		}
		return 0;
	} 

	/**
	 * Gestisce la richiesta HTTP GET.
	 *
	 * Inoltra la richiesta al metodo doPost per mantenere la coerenza con il metodo POST.
	 *
	 * @param request : la richiesta HTTP
	 * @param response : la risposta HTTP
	 * @throws ServletException : se si verifica un errore specifico della servlet
	 * @throws IOException : se si verifica un errore di input/output
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Gestisce la richiesta HTTP POST.
	 *
	 * Gestisce le varie operazioni sulla wishlist dell'utente in base al parametro "action"
	 * presente nella richiesta: visualizzazione, aggiunta prodotto, rimozione prodotto.
	 *
	 * @param request : la richiesta HTTP
	 * @param response : la risposta HTTP
	 * @throws ServletException : se si verifica un errore specifico della servlet
	 * @throws IOException : se si verifica un errore di input/output
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ProxyUtente user = (ProxyUtente) request.getSession().getAttribute("user");

		//Utente non autenticato
		if (user==null || user.getUsername().equals("")) {
			response.sendRedirect(request.getContextPath() + "/Autenticazione");
			return;
		}

		request.getSession().setAttribute("errormsg", null);

		String action = request.getParameter("action");

		if (action == null) {
			String errorMsg = "Errore nell'esecuzione di un'operazione sulla wishlist. Puoi fare le seguenti operazioni nella wishlist: "
					+ "visualizzazione wishlist, aggiunta prodotto in wishlist, rimozione prodotto in wishlist.";
			request.getSession().setAttribute("error", errorMsg);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			return;
		} 

		switch (action) {

		case "viewwishlist":

			try {
				Wishlist w;
				w = createNewWishlistIfNotExists(request, user);
				request.getSession().setAttribute("Wishlist", w);
				response.sendRedirect(request.getContextPath() + "/Wishlist");
				break;
			} catch (CategoriaProdottoException e) {
				Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, e);
			}


		case "addtowishlist":        
			try {

				Wishlist wish = createNewWishlistIfNotExists(request, user);

				int productId = parseProductId(request.getParameter("productId"));
				ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);   
				
				wish = gws.aggiungiProdottoInWishlist(wish, prodotto, user);

				request.getSession().setAttribute("Wishlist", wish);
				request.getSession().setAttribute("errormsg", "Prodotto aggiunto nella wishlist con successo");
				request.getSession().setAttribute("status", "valid");
				response.sendRedirect(request.getContextPath() + "/Wishlist");

			}catch(ProdottoPresenteException ex) {
				String errorMsg = "Prodotto già presente nella wishlist";
				request.getSession().setAttribute("errormsg", errorMsg);
				request.getSession().setAttribute("status", "invalid");

				response.sendRedirect(request.getContextPath() + "/Wishlist");
				return;

			} catch (ProdottoNulloException ex) {

				String errorMsg = "Errore nell'aggiunta di un prodotto nella wishlist.";
				request.getSession().setAttribute("error", errorMsg);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

			}catch(SQLException ex) {

				Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
				request.getSession().setAttribute("errormsg", "Errore nell'aggiunta di un prodotto nella wishlist.");
				request.getSession().setAttribute("status", "invalid");

				response.sendRedirect(request.getContextPath() + "/Wishlist");

			} catch (SottocategoriaProdottoException | CategoriaProdottoException  ex) {
				Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
			}

			break;   

		case "removefromwishlist":
			try { 

				int productId = parseProductId(request.getParameter("productId"));
				ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);   

				Wishlist wishRem = createNewWishlistIfNotExists(request, user);
				
				Wishlist newWishRem = gws.rimuoviProdottoDaWishlist(wishRem, user, prodotto);
				request.getSession().setAttribute("errormsg", "Prodotto rimosso con successo dalla wishlist");
				request.getSession().setAttribute("status", "valid");

				if(newWishRem.getProdotti().isEmpty()){
					request.getSession().removeAttribute("Wishlist");
					createNewWishlistIfNotExists(request, user);
				}
				else 
					request.getSession().setAttribute("Wishlist", newWishRem);
				response.sendRedirect(request.getContextPath() + "/Wishlist");

			} catch (ProdottoNonPresenteException ex) {
				String errorMsg = "Prodotto non presente nella wishlist.";
				request.getSession().setAttribute("errormsg", errorMsg);
				response.sendRedirect(request.getContextPath() + "/Wishlist");

			}catch (ProdottoNulloException ex) {

				String errorMsg = "Errore nell'aggiunta di un prodotto nella wishlist.";
				request.getSession().setAttribute("error", errorMsg);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

			}catch(WishlistVuotaException ex) {

				String errorMsg = "Non sono presenti prodotti nella tua wishlist.";
				request.getSession().setAttribute("errormsg", errorMsg);
				response.sendRedirect(request.getContextPath() + "/Wishlist");

			}catch(SQLException ex) {

				Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
				request.getSession().setAttribute("errormsg", "Errore nella rimozione di un prodotto dalla wishlist.");
				request.getSession().setAttribute("status", "invalid");
				response.sendRedirect(request.getContextPath() + "/Wishlist");

			} catch (SottocategoriaProdottoException | CategoriaProdottoException ex) {
				Logger.getLogger(GestioneWishlistController.class.getName()).log(Level.SEVERE, null, ex);
			}

			break;    

		default:
			String errorMsg = "Errore nell'esecuzione di un'operazione sulla wishlist. Puoi fare le seguenti operazioni nella wishlist: "
					+ "visualizzazione wishlist, aggiunta prodotto in wishlist, rimozione prodotto in wishlist.";
			request.getSession().setAttribute("error", errorMsg);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			break;
		}
	}

}
