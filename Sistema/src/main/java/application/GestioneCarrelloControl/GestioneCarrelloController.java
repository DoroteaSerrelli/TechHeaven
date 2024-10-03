package application.GestioneCarrelloControl;

import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.QuantitaProdottoException;
import application.GestioneCarrelloService.GestioneCarrelloServiceImpl;
import application.GestioneCarrelloService.ItemCarrello;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProxyProdotto;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffy
 */

@WebServlet(name = "GestioneCarrelloController", urlPatterns = {"/GestioneCarrelloController"})
public class GestioneCarrelloController extends HttpServlet {

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

		String action = request.getParameter("action");

		if (action == null) {
			String errorMsg = "Errore nell'esecuzione di un'operazione sul carrello. Puoi fare le seguenti operazioni nel carrello virtuale: "
					+ "visualizzazione carrello, aggiunta prodotto in carrello, rimozione prodotto in carrello, aumento/diminuzione "
					+ "quantità di un prodotto nel carrello.";
			request.getSession().setAttribute("error", errorMsg);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
			return;
		}  else {

			switch (action) {
			case "viewCart":
				request.getSession().getAttribute("usercart");
				break;

			case "increaseQuantity":
				increaseQuantity(request, response);
				break;
				
			case "decreaseQuantity":
				decreaseQuantity(request, response);
				break;

			case "addToCart":                        
				try {
					addToCart(request, response);
				} catch (QuantitaProdottoException e) {

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;   
			case "removeFromCart":
				removeFromCart(request, response);
				break;    

			default:
				String errorMsg = "Errore nell'esecuzione di un'operazione sul carrello. Puoi fare le seguenti operazioni nel carrello virtuale: "
						+ "visualizzazione carrello, aggiunta prodotto in carrello, rimozione prodotto in carrello, aumento/diminuzione "
						+ "quantità di un prodotto nel carrello.";
				request.getSession().setAttribute("error", errorMsg);
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
				return;
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
		doGet(request, response);
	}


	public void addToCart(HttpServletRequest request, HttpServletResponse response) throws IOException, QuantitaProdottoException{

		GestioneCarrelloServiceImpl gc = new GestioneCarrelloServiceImpl();
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();
		ItemCarrello inCart = null;

		HashMap hs = (HashMap) request.getSession().getAttribute("products_available_inStock");
		if(hs==null) hs = new HashMap();

		try{

			int productId = parseProductId(request.getParameter("productId"));

			ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);                   
			inCart = new ItemCarrello();
			Carrello cart = getUserCart(request);
			setInfoItemCarrello(prodotto, inCart);

			if(!cart.isPresent(inCart)){
				gc.aggiungiAlCarrello(cart, inCart);

				// Se l'ID del prodotto non è presente nella Mappa vuol dire che
				// il Range non e' settato con la Q.Max di prodotto disponibile in magazzino.
				if(!hs.containsKey(productId)){
					hs.put(productId, prodotto.getQuantita());
				}

				double updatedPrice = inCart.getPrezzo() * inCart.getQuantita();
				double totalAmount = cart.totalAmount();
				request.getSession().setAttribute("error", "Prodotto aggiunto nel carrello con successo");
				request.getSession().setAttribute("status", "valid");
				prepareJsonOutputMessage("valid", "Prodotto aggiunto nel carrello con successo", updatedPrice, inCart.getQuantita(), totalAmount, request, response);                   
			}
			else {  

				request.getSession().setAttribute("error", "Prodotto già presente nel carrello.");
				request.getSession().setAttribute("status", "invalid");
				prepareJsonOutputMessage("invalid", "Prodotto già presente nel carrello.", 0, 0, cart.totalAmount(), request, response);                        //response.sendError(1, "Item già inserito nel carrello");

			}

			request.getSession().setAttribute("products_available_inStock", hs); 
			request.getSession().setAttribute("usercart", cart);

		}catch(ProdottoPresenteException ex) {
			prepareJsonOutputMessage("invalid", "Prodotto già presente nel carrello", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/cart");

		}catch(ProdottoNulloException ex) {
			prepareJsonOutputMessage("invalid", "Errore nell'aggiunta del prodotto nel carrello. Riprova più tardi.", 0, 0, 0, request, response);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} catch (SQLException  | SottocategoriaProdottoException | CategoriaProdottoException ex) {
			Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
			prepareJsonOutputMessage("invalid", "Errore nell\\'operazione di aggiunta del prodotto nel carrello. Riprova più tardi.", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} 
	}

	public void increaseQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException{

		int productId = parseProductId(request.getParameter("productId"));
		int quantità = parseQuantity(request.getParameter("prod_quantità"));

		GestioneCarrelloServiceImpl gc = new GestioneCarrelloServiceImpl();
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();

		try {

			ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);
			ItemCarrello inCart = new ItemCarrello();

			Carrello cart = getUserCart(request);
			setInfoItemCarrello(prodotto, inCart);

			if(!cart.isPresent(inCart)){                    
				prepareJsonOutputMessage("invalid", "Prodotto non presente nel carrello", 0, 0, 0, request, response);                            
				response.sendRedirect(request.getContextPath() + "/cart");
				return;
			}else{

				int quantità_deposito =  prodotto.getQuantita();
				if(quantità > inCart.getQuantita() && quantità<=quantità_deposito){
					gc.aumentaQuantitaNelCarrello(cart, inCart, quantità);                        
					double updatedPrice = inCart.getPrezzo() * quantità;
					double totalAmount = cart.totalAmount();
					prepareJsonOutputMessage("valid", "Quantità modificata nel carrello con successo", updatedPrice, quantità, totalAmount, request, response);
				}
				else if(quantità <= inCart.getQuantita()){
					prepareJsonOutputMessage("invalid", "La quantià inserita non è maggiore della quantità del prodotto nel carrello", 0, 0, cart.totalAmount(), request, response);                    
				}else {
					prepareJsonOutputMessage("invalid", "La quantià inserita supera le scorte del prodotto in magazzino", 0, 0, cart.totalAmount(), request, response);                      
				}
			}

			request.getSession().setAttribute("usercart", cart); 

		}catch(CarrelloVuotoException ex) {
			prepareJsonOutputMessage("invalid", "Il carrello è vuoto", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/cart");

		}catch(ProdottoNonPresenteException ex) {
			prepareJsonOutputMessage("invalid", "Prodotto non presente nel carrello", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/cart");

		}catch(ProdottoNulloException ex) {
			prepareJsonOutputMessage("invalid", "Errore nell'aggiornamento della quantità del prodotto nel carrello. Riprova più tardi.", 0, 0, 0, request, response);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} catch (QuantitaProdottoException ex) {
			Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
			prepareJsonOutputMessage("invalid", ex.getMessage(), 0, 0, 0, request, response);               
			response.sendRedirect(request.getContextPath() + "/cart");

		} catch (SQLException  | SottocategoriaProdottoException | CategoriaProdottoException ex) {
			Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
			prepareJsonOutputMessage("invalid", "Errore nell\\'operazione di aggiornamento della quantità del prodotto nel carrello. Riprova più tardi.", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} 
	}


	public void decreaseQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException{

		int productId = parseProductId(request.getParameter("productId"));
		int quantità = parseQuantity(request.getParameter("prod_quantità"));

		GestioneCarrelloServiceImpl gc = new GestioneCarrelloServiceImpl();
		ProdottoDAODataSource pdao = new ProdottoDAODataSource();

		try {

			ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);
			ItemCarrello inCart = new ItemCarrello();

			Carrello cart = getUserCart(request);
			setInfoItemCarrello(prodotto, inCart);

			if(!cart.isPresent(inCart)){                    
				prepareJsonOutputMessage("invalid", "Prodotto non presente nel carrello", 0, 0, 0, request, response);                            
				response.sendRedirect(request.getContextPath() + "/cart");
				return;
			}else{

				int quantità_deposito =  prodotto.getQuantita();
				if(quantità > 0 && quantità < inCart.getQuantita() && inCart.getQuantita()<=quantità_deposito){
					gc.decrementaQuantitaNelCarrello(cart, inCart, quantità);                        
					double updatedPrice = inCart.getPrezzo() * quantità;
					double totalAmount = cart.totalAmount();
					prepareJsonOutputMessage("valid", "Quantità modificata nel carrello con successo", updatedPrice, quantità, totalAmount, request, response);
				}
				
				if(quantità >= inCart.getQuantita()){
					prepareJsonOutputMessage("invalid", "La quantià inserita non è minore della quantità del prodotto nel carrello", 0, 0, cart.totalAmount(), request, response);                    
				
				}else if(quantità == 0){
					prepareJsonOutputMessage("invalid", "La quantià inserita è 0", 0, 0, cart.totalAmount(), request, response);                      
				
				}else if(inCart.getQuantita() > quantità_deposito && quantità_deposito != 0){
					//il prodotto inserito nel carrello ha subito nel corso del tempo una diminuzione delle sue scorte in magazzino
					gc.decrementaQuantitaNelCarrello(cart, inCart, inCart.getQuantita()-1);
				
				}else if(quantità_deposito == 0){
					gc.rimuoviDalCarrello(cart, inCart);
				}
			}

			request.getSession().setAttribute("usercart", cart); 

		}catch(CarrelloVuotoException ex) {
			prepareJsonOutputMessage("invalid", "Il carrello è vuoto", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/cart");

		}catch(ProdottoNonPresenteException ex) {
			prepareJsonOutputMessage("invalid", "Prodotto non presente nel carrello", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/cart");

		}catch(ProdottoNulloException ex) {
			prepareJsonOutputMessage("invalid", "Errore nell'aggiornamento della quantità del prodotto nel carrello. Riprova più tardi.", 0, 0, 0, request, response);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} catch (QuantitaProdottoException ex) {
			Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
			prepareJsonOutputMessage("invalid", ex.getMessage(), 0, 0, 0, request, response);               
			response.sendRedirect(request.getContextPath() + "/cart");

		} catch (SQLException  | SottocategoriaProdottoException | CategoriaProdottoException ex) {
			Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
			prepareJsonOutputMessage("invalid", "Errore nell\\'operazione di aggiornamento della quantità del prodotto nel carrello. Riprova più tardi.", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} 
	}

	public void removeFromCart(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			String pid = request.getParameter("productId");
			HashMap hs = (HashMap) request.getSession().getAttribute("products_available_inStock"); 
			int productId = 0;

			if (pid != null && !pid.isEmpty()) {
				productId = Integer.parseInt(pid);
				GestioneCarrelloServiceImpl gc = new GestioneCarrelloServiceImpl();
				ProdottoDAODataSource pdao = new ProdottoDAODataSource();
				ProxyProdotto prodotto = pdao.doRetrieveProxyByKey(productId);
				ItemCarrello inCart = new ItemCarrello();
				setInfoItemCarrello(prodotto, inCart);
				Carrello cart = (Carrello)request.getSession().getAttribute("usercart");
				if(cart==null){
					cart = new Carrello();
				}

				if(cart.isPresent(inCart)){
					int quantità_deposito =  prodotto.getQuantita();
					gc.rimuoviDalCarrello(cart, inCart);

					// Rimuovo la Quantità Max in deposito dalla Mappa che gestisce il Range, usando l'ID 
					// del prodotto come chiave.

					hs.remove(productId);
					// Prepare the updated cart total for response
					double totalAmount = cart.totalAmount();
					prepareJsonOutputMessage("valid", "Prodotto rimosso con successo dal carrello", 0, 0, totalAmount, request, response);                }
				//Aggiorno la Mappa nella Sessione.
				request.getSession().setAttribute("products_available_inStock", hs);
				request.getSession().setAttribute("usercart", cart);
			}

		}catch(CarrelloVuotoException ex) {
			prepareJsonOutputMessage("invalid", "Il carrello è vuoto", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/cart");

		}catch(ProdottoNonPresenteException ex) {
			prepareJsonOutputMessage("invalid", "Prodotto non presente nel carrello", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/cart");

		}catch(ProdottoNulloException ex) {
			prepareJsonOutputMessage("invalid", "Errore nella rimozione del prodotto nel carrello. Riprova più tardi.", 0, 0, 0, request, response);
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		} catch (SQLException  | SottocategoriaProdottoException | CategoriaProdottoException ex) {
			Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
			prepareJsonOutputMessage("invalid", "Errore nell\\'operazione di rimozione del prodotto nel carrello. Riprova più tardi.", 0, 0, 0, request, response);                            
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");

		}
	}

	public void setInfoItemCarrello(ProxyProdotto prodotto, ItemCarrello inCart){
		// Set product details in ItemCarrello
		inCart.setCodiceProdotto(prodotto.getCodiceProdotto());
		inCart.setNomeProdotto(prodotto.getNomeProdotto());
		inCart.setCategoria(prodotto.getCategoria());
		inCart.setMarca(prodotto.getMarca());
		inCart.setPrezzo(prodotto.getPrezzo());
		inCart.setModello(prodotto.getModello());
		inCart.setDettagli(prodotto.getTopDescrizione());
	}

	private void prepareJsonOutputMessage(String status, String msg, double updatedPrice, int updatedQuantity, double totalAmount, HttpServletRequest request, HttpServletResponse response) {
		try {
			// Prepare the data to be returned as JSON
			Map<String, Object> jsonResponse = new HashMap<>();
			jsonResponse.put("message", msg);
			jsonResponse.put("status", status);
			jsonResponse.put("updatedPrice", String.format("%.2f", updatedPrice));  // Format the price to two decimal places
			jsonResponse.put("updatedQuantity", updatedQuantity);
			jsonResponse.put("totalAmount", String.format("%.2f", totalAmount));    // Format total amount to two decimal places

			// Convert the Map to JSON using Gson
			Gson gson = new Gson();
			String jsonResponseString = gson.toJson(jsonResponse);

			// Set response content type to JSON
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			// Write the JSON response back to the client
			response.getWriter().write(jsonResponseString);
		} catch (IOException ex) {
			Logger.getLogger(GestioneCarrelloController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	private int parseProductId(String pid) {
		if (pid != null && !pid.isEmpty()) {
			return Integer.parseInt(pid);
		}
		return 0;
	}

	private int parseQuantity(String q) {
		if (q != null && !q.isEmpty()) {
			return Integer.parseInt(q);
		}
		return 1;
	}

	private Carrello getUserCart(HttpServletRequest request) {
		Carrello cart = (Carrello) request.getSession().getAttribute("usercart");
		if (cart == null) {
			cart = new Carrello();
		}
		return cart;
	}
}
