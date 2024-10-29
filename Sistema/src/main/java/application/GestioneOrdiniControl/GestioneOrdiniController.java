package application.GestioneOrdiniControl;

import application.GestioneCarrelloService.ItemCarrello;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdiniService.ObjectOrdine;
import application.GestioneOrdiniService.Ordine;
import application.GestioneOrdiniService.OrdineException;
import application.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdiniService.OrdineException.FormatoCorriereException;
import application.GestioneOrdiniService.OrdineException.FormatoImballaggioException;
import application.GestioneOrdiniService.OrdineException.FormatoQuantitaException;
import application.GestioneOrdiniService.OrdineException.MancanzaPezziException;
import application.GestioneOrdiniService.ProxyOrdine;
import application.GestioneOrdiniService.ReportSpedizione;
import application.NavigazioneControl.PaginationUtils;
import application.NavigazioneService.NavigazioneServiceImpl;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProxyProdotto;
import application.PagamentoService.PagamentoException;
import application.GestioneOrdiniService.OrdineException.ErroreSpedizioneOrdineException;
import application.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.jdbc.pool.DataSource;

import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffy
 */
@MultipartConfig
@WebServlet(name = "GestioneOrdiniController", urlPatterns = {"/GestioneOrdiniController"})
public class GestioneOrdiniController extends HttpServlet {
	private static final long serialVersionUID = 1L;


	private GestioneOrdiniServiceImpl gos;
	private OrdineDAODataSource orderDAO; 
	private ProdottoDAODataSource pdao;
	private PaginationUtils pu;

	@Override
	public void init() throws ServletException {

		DataSource ds = mock(DataSource.class);
		PhotoControl photoControl = new PhotoControl(ds);

		try {
			pdao = new ProdottoDAODataSource(ds, photoControl);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		GestioneCatalogoServiceImpl gcs = new GestioneCatalogoServiceImpl(pdao, photoControl);
		NavigazioneServiceImpl ns = new NavigazioneServiceImpl(pdao);
		PagamentoDAODataSource paymentDAO = new PagamentoDAODataSource(ds);
		UtenteDAODataSource userDAO = null;
		try {
			userDAO = new UtenteDAODataSource(ds);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		orderDAO = new OrdineDAODataSource(ds);
		gos = new GestioneOrdiniServiceImpl(orderDAO, userDAO, pdao, paymentDAO);
		pu = new PaginationUtils(ns, gcs, gos);

	}

	//Costruttore per il test

	public GestioneOrdiniController(ProdottoDAODataSource productDAO, OrdineDAODataSource orderDAO, GestioneOrdiniServiceImpl gos, PaginationUtils pu) {
		this.pdao = productDAO;
		this.orderDAO = orderDAO;
		this.gos = gos;
		this.pu = pu;
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
		
		int page = 1;
		String action = "";
		String status = "Richiesta_effettuata";
		try{
			if (request.getParameter("page") != null)
				page = Integer.parseInt(
						request.getParameter("page"));
			if(request.getParameter("action") != null){
				action = request.getParameter("action");
				if (!action.equals("incomplete_order")) 
					pu.detectActionChanges(request, action);                  
			}
		} catch(Exception e) {
			Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, e);
			page=1;
		}
		
		// Perform pagination and fetch products for the requested page
		//Collection<ProxyOrdine> orders_to_send = (Collection<ProxyOrdine>) request.getSession().getAttribute("products");
		Ordine ordine = new Ordine();
		if(action.equals("incomplete_order")){
			ordine = (Ordine)request.getSession().getAttribute("selected_ordine");
			request.getSession().removeAttribute("selected_ordine");   
			status = "Preparazione_incompleta";
			action = "fetch_da_spedire";
		}
		Collection <ProxyOrdine> orders_to_send = new ArrayList<>();
		try {
			orders_to_send = paginateOrders(request, page, action);
		} catch (Exception ex) {
			Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, ex);
		}

		if(status.equals("Preparazione_incompleta"))
			updateOrderStatusInList(orders_to_send, ordine, status);


		// Set content type to JSON
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// Create a response object to hold products and total pages
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("orders", orders_to_send);
		responseData.put("hasNextPage", request.getSession().getAttribute("hasNextPage"));

		// Convert response object to JSON
		GsonBuilder gsonBuilder = new GsonBuilder(); // Using Gson for JSON conversion
		gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
		gsonBuilder.registerTypeAdapter(LocalTime.class, new LocalTimeAdapter());

		Gson gson = gsonBuilder.create();
		String jsonResponse = gson.toJson(responseData);

		// Write JSON response
		PrintWriter out = response.getWriter();
		out.print(jsonResponse);
		out.flush();

	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	private static int pr_pagina = 4;   

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {   

		String action = request.getParameter("action");

		if(action.equals("accept_order")){
			try {
				int orderID = Integer.parseInt(request.getParameter("orderId"));
				
				ArrayList<ItemCarrello> order_products = orderDAO.doRetrieveAllOrderProducts(orderID);
				ProxyOrdine proxy_ordine = orderDAO.doRetrieveProxyByKey(orderID);

				proxy_ordine.setStato(ObjectOrdine.Stato.In_lavorazione);
				request.getSession().setAttribute("proxy_ordine", proxy_ordine);
				request.getSession().setAttribute("selected_ordine", proxy_ordine.mostraOrdine());

				HashMap<Integer, Integer> hs = new HashMap<Integer, Integer>();
				for(ItemCarrello item : order_products){
					ProxyProdotto pr = pdao.doRetrieveProxyByKey(item.getCodiceProdotto());
					if(pr.getQuantita()<item.getQuantita()){
						request.getSession().removeAttribute("proxy_ordine");                                         
						response.sendRedirect(request.getContextPath()+"/error_preparazioneOrdine");                            
						return;
					}
					hs.put(item.getCodiceProdotto(),  pr.getQuantita());                    
				}
				request.getSession().setAttribute("order_products", order_products);                  
				request.getSession().setAttribute("order_products_available", hs);                    

				response.sendRedirect(request.getContextPath() + "/fill_order_details");
			} catch (SQLException | OrdineException.OrdineVuotoException | ProdottoException.CategoriaProdottoException | ProdottoException.SottocategoriaProdottoException | ErroreTipoSpedizioneException ex) {
				Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		else if(action.equals("complete_order")){
			Ordine order= (Ordine)request.getSession().getAttribute("selected_ordine");
			ProxyOrdine order_proxy = (ProxyOrdine)request.getSession().getAttribute("proxy_ordine");

			try {
				ArrayList <ItemCarrello> carrello = (ArrayList <ItemCarrello>)request.getSession().getAttribute("order_products");
				String[] productIds = request.getParameterValues("product_id");
				String[] itemAmounts = request.getParameterValues("item_amount");
				ArrayList<Integer> quantities = new ArrayList<>();
				
				for(String quantity : itemAmounts)
					quantities.add(Integer.parseInt(quantity));
				
				/* Validate cart quantities
				/*boolean validQuantities = validateCartQuantities(carrello, productIds, itemAmounts, request);
				if (!validQuantities) {
					request.getRequestDispatcher("GestioneOrdini").forward(request, response);
					return;
				}  */    

				String imballaggio = request.getParameter("Imballaggio");
				String corriere = request.getParameter("Corriere");

				order_proxy.setStato(ObjectOrdine.Stato.Spedito);
				order.setStato(ObjectOrdine.Stato.Spedito);
				order.setProdotti(carrello);
				
				ReportSpedizione report = gos.creaReportSpedizione(order, carrello, quantities, imballaggio, corriere);//new ReportSpedizione(order_proxy.getCodiceOrdine(), corriere, imballaggio, order_proxy);          

				gos.preparazioneSpedizioneOrdine(order, report);
				
				request.getSession().setAttribute("error", "Ordine Spedito Con Successo!");

				request.getSession().removeAttribute("proxy_ordine");                    
				request.getSession().removeAttribute("selected_ordine");
				request.getSession().removeAttribute("order_products");

				response.sendRedirect(request.getContextPath() + "/GestioneOrdini");                 
			
			} catch (MancanzaPezziException e) {
				String message = "Avviso importante!\n"
						+ e.getMessage() 
						+ "\n\nContatta il cliente " + order.getAcquirente().getNome() + " " + order.getAcquirente().getCognome()
						+ " ai seguenti contatti: \n"
						+ "\n- Email: "+ order.getAcquirente().getEmail()
						+ "\n- Telefono: "+ order.getAcquirente().getTelefono()
						+ "\nPer poter procedere con una delle seguenti opzioni:\n "
						+ "- Ricevere il rimborso totale dell’ordine;\n"
						+ "- Ricevere il rimborso dei pezzi mancanti;\n"
						+ "- Attendere l’arrivo dei prodotti in negozio per poter soddisfare l’ordine.\n";
				
				request.getSession().setAttribute("error", message); 
				response.sendRedirect(request.getContextPath()+"/GestioneOrdini");

			
			} catch (FormatoImballaggioException | FormatoCorriereException | FormatoQuantitaException e) {
				request.getSession().setAttribute("error", e.getMessage()); 
				response.sendRedirect(request.getContextPath()+"/GestioneOrdini");

			} catch(IOException | NumberFormatException | SQLException e){} 
			
			catch (ErroreTipoSpedizioneException | ErroreSpedizioneOrdineException | OrdineVuotoException | PagamentoException.ModalitaAssenteException | CloneNotSupportedException ex) {
				Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, ex);
				request.getSession().setAttribute("error","C'è stato un errore durante la preparazione dell'ordine");                 
				request.getSession().removeAttribute("selected_ordine");
				response.sendRedirect(request.getContextPath()+"/GestioneOrdini");

			} catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException ex) {
				Logger.getLogger(GestioneOrdiniController.class.getName()).log(Level.SEVERE, null, ex);
				request.getSession().setAttribute("error","C'è stato un errore durante la preparazione dell'ordine"); 
				request.getSession().removeAttribute("selected_ordine");
				response.sendRedirect(request.getContextPath()+"/GestioneOrdini");
			
			}
		}

	}
	
	
	
	
	// This method handles retrieve and pagination of current_page results while also giving back the Order Collection for
	// the current page.
	private Collection<ProxyOrdine> paginateOrders(HttpServletRequest request, int page, String action) throws SQLException, Exception{
		Collection <ProxyOrdine> currentPageResults;
		Collection <ProxyOrdine> nextPageResults;
		int previoslyFetchedPage = pu.getSessionAttributeAsInt(request, "previosly_fetched_page", 0);
		if(page==previoslyFetchedPage){                 
			currentPageResults = pu.getSessionCollection(request, "nextPageResults", ProxyOrdine.class);
			request.getSession().setAttribute("products", currentPageResults);              
		}
		else {
			currentPageResults = pu.performPagination(page, pr_pagina, action);
			request.getSession().setAttribute("products", currentPageResults);                  
		}     
		nextPageResults = pu.performPagination(page+1, pr_pagina, action);

		request.getSession().setAttribute("nextPageResults", nextPageResults);
		request.getSession().setAttribute("previosly_fetched_page", page+1);

		boolean hasNextPage = pu.checkIfItsTheSamePage (currentPageResults, nextPageResults, ProxyOrdine.class);   
		request.getSession().setAttribute("hasNextPage", hasNextPage);
		return currentPageResults;
	}


	// This method validates the quantities picked by the order_manager and return true if every quantity was correct.
	private boolean validateCartQuantities(ArrayList<ItemCarrello> carrello, String[] productIds, String[] itemAmounts, HttpServletRequest request) {
		if (productIds != null && itemAmounts != null) {
			// Process each product in the order
			for (int i = 0; i < productIds.length; i++) {
				String productId = productIds[i];
				int quantity = Integer.parseInt(itemAmounts[i]);

				// Find the corresponding product in the cart
				ItemCarrello item = carrello.stream()
						.filter(c -> String.valueOf(c.getCodiceProdotto()).equals(productId))
						.findFirst()
						.orElse(null);

				if (item != null) {
					// Validate the quantity
					if (quantity < item.getQuantita()) {
						request.setAttribute("error", "La quantità non può essere inferiore alla quantità richiesta per il prodotto " + item.getNomeProdotto());
						break;
					}
					// Update the item's quantity to be shipped
					item.setQuantita(quantity);
				}
			}      
		} 
		return true;
	}

	// This method updates the order status inside the Order retrieved to a set one passed as a parameter.
	// as a well as its own status.
	private void updateOrderStatusInList(Collection<ProxyOrdine> orders, Ordine ordineToUpdate, String newStatus) {
		ordineToUpdate.setStato(ObjectOrdine.Stato.valueOf(newStatus));
		for (ProxyOrdine proxyOrdine : orders) {
			if (proxyOrdine.getCodiceOrdine() == ordineToUpdate.getCodiceOrdine()) {
				// Temporarily update the status of this order
				proxyOrdine.setStato(ObjectOrdine.Stato.valueOf(newStatus));
				break; // Exit loop once the order is found and updated
			}
		}
	}

}
