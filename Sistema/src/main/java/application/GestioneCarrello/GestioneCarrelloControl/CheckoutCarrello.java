package application.GestioneCarrello.GestioneCarrelloControl;

import application.GestioneCarrello.GestioneCarrelloService.Carrello;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.ObjectOrdine;
import application.GestioneOrdini.GestioneOrdiniService.Ordine;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoConsegnaException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.ErroreTipoSpedizioneException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.IndirizzoSpedizioneNulloException;
import application.GestioneOrdini.GestioneOrdiniService.OrdineException.OrdineVuotoException;
import application.Pagamento.PagamentoService.Pagamento;
import application.Pagamento.PagamentoService.PagamentoException;
import application.Pagamento.PagamentoService.PagamentoServiceImpl;
import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;

/**
 *
 * @author raffa
 */
@WebServlet(name = "CheckoutCarrello", urlPatterns = {"/CheckoutCarrello"})
public class CheckoutCarrello extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GestioneOrdiniServiceImpl gos;
	private PagamentoServiceImpl ps;
	private UtenteDAODataSource userDAO;
	
	@Override
	public void init(){
		Context initContext;
		Context envContext;
		DataSource ds = null;
		try {
			initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/techheaven");
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public CheckoutCarrello(UtenteDAODataSource userDAO, GestioneOrdiniServiceImpl gos, PagamentoServiceImpl ps) {
		this.gos = gos;
		this.ps = ps;
		this.userDAO = userDAO;
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
		String action = request.getParameter("action");
		if(action!=null && !action.isEmpty()){
			if(action.equals("annullaPagamento")|| action.equals("annullaOrdine")){
				request.getSession().removeAttribute("preview_order");
				//Redirect alla pagina Iniziale:
				response.sendRedirect(request.getContextPath()+"/");
				return;
			}

		}
		ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
		u.setDAO(new UtenteDAODataSource());
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String action = request.getParameter("action");
		
		if(action.equals("confirmOrder"))
			elaborateCheckoutRequest(request, response);
		
		/*if(action.equals("confirmPayment"))
			finalizeOrder(request, response);*/
	}
	
	
	private void elaborateCheckoutRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			String idIndirizzo = request.getParameter("selectedAddress");
			
			if(idIndirizzo== null || idIndirizzo.isBlank()) 
				throw new IndirizzoSpedizioneNulloException("Specificare l’indirizzo di spedizione per l’ordine. Per aggiungere un altro indirizzo, annulla l’acquisto e vai nell’area riservata.");
			
			ProxyUtente user = (ProxyUtente)request.getSession().getAttribute("user");
			user.setDAO(userDAO);
			
			Carrello cart = (Carrello) request.getSession().getAttribute("usercart");
			String tipo_spedizione = request.getParameter("tipoSpedizione");
			String tipoConsegna = request.getParameter("modalitaConsegna");
			Ordine preview_order = gos.creaOrdine(cart, user, idIndirizzo, tipo_spedizione, tipoConsegna);

			
			request.getSession().setAttribute("preview_order", preview_order);
			response.sendRedirect(request.getContextPath()+"/Pagamento");

		} catch (IndirizzoSpedizioneNulloException | ErroreTipoSpedizioneException | SQLException | ErroreTipoConsegnaException | OrdineVuotoException ex) {
			request.getSession().setAttribute("error", ex.getMessage());            
			response.sendRedirect(request.getContextPath()+"/CheckoutCarrello");  
			Logger.getLogger(CheckoutCarrello.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	
	
	

	private void loadUserAddresses(HttpServletRequest request, ProxyUtente u) throws SQLException {
		
		Context initContext;
		Context envContext;
		DataSource ds = null;
		try {
			initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/techheaven");
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource(ds);
		ArrayList<Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
		request.setAttribute("Indirizzi", indirizzi); 

	}

}
