package application.Autenticazione.AutenticazioneControl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

import application.Registrazione.RegistrazioneService.Indirizzo;
import application.Registrazione.RegistrazioneService.ProxyUtente;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import storage.AutenticazioneDAO.UtenteDAODataSource;

/**
 *
 * @author raffa
 */
public class UpdateUserInfo extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IndirizzoDAODataSource addressDAO;
	
	
	public UpdateUserInfo() throws ServletException {
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

		try {
			addressDAO = new IndirizzoDAODataSource(ds);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	//Costrutto per test
	public UpdateUserInfo(IndirizzoDAODataSource addressDAO) {
		this.addressDAO = addressDAO;
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
	
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
        u.setDAO(new UtenteDAODataSource());
        if (u==null || u.getUsername().equals("")) {
           response.sendRedirect(request.getContextPath() + "Autenticazione");
           return;
        }    
        // Retrieve data from request or session if needed
        ArrayList<Indirizzo> indirizzi = (ArrayList<Indirizzo>) request.getAttribute("Indirizzi");
        if(indirizzi==null){
            try {
                loadUserAddresses(request, u);
            } catch (SQLException ex) {
                Logger.getLogger(AreaRiservata.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(request.getSession().getAttribute("error")!=null){
            request.setAttribute("error", request.getSession().getAttribute("error")); 
            request.getSession().removeAttribute("error");
        }
        // Forward to JSP
        request.getRequestDispatcher("protected/cliente/updateUserInfo.jsp").forward(request, response);
    }
    
    private void loadUserAddresses(HttpServletRequest request, ProxyUtente u) throws SQLException {
        
        ArrayList<Indirizzo> indirizzi = addressDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
        request.setAttribute("Indirizzi", indirizzi); 
       
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
