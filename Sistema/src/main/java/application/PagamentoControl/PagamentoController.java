package application.PagamentoControl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import application.GestioneCarrelloControl.CheckoutCarrello;
import application.GestioneCarrelloService.Carrello;
import application.GestioneCarrelloService.CarrelloException;
import application.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdiniService.ObjectOrdine;
import application.GestioneOrdiniService.Ordine;
import application.GestioneOrdiniService.OrdineException;
import application.PagamentoService.Pagamento;
import application.PagamentoService.PagamentoCartaCredito;
import application.PagamentoService.PagamentoException.ModalitaAssenteException;
import application.PagamentoService.PagamentoException.FormatoCVVCartaException;
import application.PagamentoService.PagamentoException.FormatoDataCartaException;
import application.PagamentoService.PagamentoException.FormatoNumeroCartaException;
import application.PagamentoService.PagamentoException.FormatoTitolareCartaException;
import application.PagamentoService.PagamentoServiceImpl;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

@WebServlet(name = "PagamentoController", urlPatterns = {"/PagamentoController"})
public class PagamentoController extends HttpServlet{
	private static final long serialVersionUID = 1L;

	private GestioneOrdiniServiceImpl gos;
	private PagamentoServiceImpl ps;

	@Override
	public void init(){
		DataSource ds = new DataSource();
		OrdineDAODataSource orderDAO = new OrdineDAODataSource(ds);
		UtenteDAODataSource userDAO = null;
		ProdottoDAODataSource productDAO = null;
		PhotoControl photoControl = new PhotoControl(ds);
		try {
			userDAO = new UtenteDAODataSource(ds);
			productDAO = new ProdottoDAODataSource(ds, photoControl);

		} catch (SQLException e) {

			e.printStackTrace();
		}
		PagamentoDAODataSource paymentDAO = new PagamentoDAODataSource(ds);

		gos = new GestioneOrdiniServiceImpl(orderDAO, userDAO, productDAO, paymentDAO);
		ps = new PagamentoServiceImpl(paymentDAO);
	}

	//Costruttore per test
	public PagamentoController(GestioneOrdiniServiceImpl gos, PagamentoServiceImpl ps) {
		this.gos = gos;
		this.ps = ps;
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
		String action = request.getParameter("action");
		if(action!=null && !action.isEmpty()){
			if(action.equals("annullaPagamento")){
				request.getSession().removeAttribute("preview_order");
				//Redirect alla pagina Iniziale:
				response.sendRedirect(request.getContextPath()+"/");
				return;
			}

		}
		ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
		if (u==null || u.getUsername().equals("")) {
			response.sendRedirect(request.getContextPath() + "/Autenticazione");
			return;
		}   
		Carrello c = (Carrello) request.getSession().getAttribute("usercart");
		if (c==null || c.getNumProdotti()==0){
			response.sendRedirect(request.getContextPath() + "/Autenticazione");
			return;    
		}
		// Retrieve data from request or session if needed
		ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>) request.getAttribute("Indirizzi");
		if(indirizzi==null){
			try {
				loadUserAddresses(request, u);
			} catch (SQLException ex) {
				Logger.getLogger(CheckoutCarrello.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		// Forward to JSP
		request.getRequestDispatcher("CompletaOrdine").forward(request, response);
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
		String action = request.getParameter("action");

		if(action.equals("confirmPayment"))
			finalizeOrder(request, response);
	}

	private void finalizeOrder(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			String metodoPagamento = request.getParameter("metodoPagamento");
			if(metodoPagamento==null || metodoPagamento.equals("")) 
				throw new ModalitaAssenteException("Specificare la modalità di pagamento: carta di credito, Paypal, contrassegno.");  
			Ordine preview_order = (Ordine) request.getSession().getAttribute("preview_order");
			Carrello cart = (Carrello) request.getSession().getAttribute("usercart");
			ProxyUtente user = (ProxyUtente) request.getSession().getAttribute("user");
			Pagamento pagamento = null;
			Boolean success = false ;
			
			
			switch(metodoPagamento.toUpperCase()){
			
			case "CARTA_CREDITO":
				// Extract credit card details from the request
				String titolare = request.getParameter("titolare");
				String ccNumber = request.getParameter("cc_number");
				String ccExpiry = request.getParameter("cc_expiry");
				String ccCvc = request.getParameter("cc_cvc");


				if(PagamentoCartaCredito.checkValidate(titolare, ccNumber, ccExpiry, ccCvc)){

					// Initialize the credit card payment object
					pagamento = gos.creaPagamento_cartaCredito(cart, preview_order, metodoPagamento, titolare, ccNumber, ccExpiry, ccCvc);
					//System.out.println(titolare);
					success = processPayment(request, pagamento, user, cart, preview_order); 
				}
				break;
			case "PAYPAL":
				pagamento = gos.creaPagamento_PaypalContrassegno(cart, preview_order, metodoPagamento);
				success = processPayment(request, pagamento, user, cart, preview_order);
				break;    
			case "CONTRASSEGNO":
				pagamento = gos.creaPagamento_PaypalContrassegno(cart, preview_order, metodoPagamento);
				success = processPayment(request, pagamento, user, cart, preview_order);
				break;    
			}
			if(!success){
				response.sendRedirect(request.getContextPath()+"/ErrorePagamento");
				return;
			}
			response.sendRedirect(request.getContextPath()+"/SuccessoPagamento");
		} catch (ModalitaAssenteException | FormatoCVVCartaException | FormatoDataCartaException | FormatoTitolareCartaException | FormatoNumeroCartaException ex) {
			Logger.getLogger(CheckoutCarrello.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().setAttribute("error", ex.getMessage());            
			response.sendRedirect(request.getContextPath()+"/Pagamento");  
		}
	}


	private <T extends Pagamento> boolean processPayment(HttpServletRequest request, T pagamento, ProxyUtente user, Carrello cart, Ordine preview_order){
		// If validation is successful, proceed with payment processing...                                    
		try {
			preview_order.setStato(ObjectOrdine.Stato.Richiesta_effettuata);
			gos.commissionaOrdine(cart, preview_order, pagamento, user);
			request.getSession().removeAttribute("usercart");
			request.getSession().removeAttribute("preview_order");            
			return true;
			// Handle other payment methods (e.g., Paypal)
		} catch (SQLException | CarrelloException.ProdottoNonPresenteException | CarrelloException.CarrelloVuotoException | CarrelloException.ProdottoNulloException | OrdineException.OrdineVuotoException | ModalitaAssenteException | CloneNotSupportedException ex) {
			Logger.getLogger(CheckoutCarrello.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println(ex.getMessage());
			request.getSession().setAttribute("error", ex.getMessage());        
			return false;
		}catch(ConcurrentModificationException ce){
			Logger.getLogger(CheckoutCarrello.class.getName()).log(Level.SEVERE, null, ce);
			System.out.println(ce.getMessage());
			return true;
		}  
	}

	private boolean validateCreditCardPayment(HttpServletRequest request, String titolare, String ccNumber, String ccExpiry, String ccCVC) throws IOException{
		// Server-side validation
		if (titolare == null || ccNumber == null || ccExpiry == null || ccCVC == null ||
				titolare.isEmpty() || ccNumber.isEmpty() || ccExpiry.isEmpty() || ccCVC.isEmpty()) {
			request.getSession().setAttribute("error", "Inserisci tutte le informazioni della carta di credito.");            
			return false;
		}
		// Check if card expiry is valid
		// Assuming the format is YYYY-MM-DD
		String[] expiryParts = ccExpiry.split("/");  
		int expYear = Integer.parseInt(expiryParts[1]);  // Year
		int expMonth = Integer.parseInt(expiryParts[0]); // Month

		java.util.Calendar currentDate = java.util.Calendar.getInstance();
		int currentYear = currentDate.get(java.util.Calendar.YEAR);
		int currentMonth = currentDate.get(java.util.Calendar.MONTH) + 1; // Months are 0-based

		// Now check if the card is expired
		if (expYear < currentYear || (expYear == currentYear && expMonth < currentMonth)) {
			request.getSession().setAttribute("errorMessage", "La carta di credito è scaduta.");
			return false;
		}

		return true;
	}

	private void loadUserAddresses(HttpServletRequest request, ProxyUtente u) throws SQLException {
		IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource(new DataSource());
		ArrayList<Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
		request.setAttribute("Indirizzi", indirizzi); 

	}
}
