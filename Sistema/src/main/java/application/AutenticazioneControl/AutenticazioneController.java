package application.AutenticazioneControl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import application.AutenticazioneService.AutenticazioneException;
import application.AutenticazioneService.AutenticazioneServiceImpl;
import application.RegistrazioneService.Indirizzo;
import application.RegistrazioneService.ProxyUtente;
import application.RegistrazioneService.Ruolo;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;

@WebServlet(name = "AutenticazioneController", urlPatterns = {"/AutenticazioneController"})
public class AutenticazioneController extends HttpServlet {

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
		try {
			// Call loadUserAddresses when the page is accessed directly
			loadUserAddresses(request);
		} catch (SQLException ex) {
			Logger.getLogger(AutenticazioneController.class.getName()).log(Level.SEVERE, null, ex);
		}
		// Check if an action parameter is present and not empty
		String action = request.getParameter("action");
		if (action != null && !action.isEmpty()) {
			// Forward to updateUserInfo.jsp if action is specified
			if (action.equals("updateUserInfo")) {
				response.sendRedirect(request.getContextPath() + "/UpdateUserInfo");
			}           
			// Add other actions if needed
		} else {
			// Forward to the default page (e.g., AreaRiservata.jsp) if no action is specified
			request.getRequestDispatcher("AreaRiservata").forward(request, response);
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

		try {
			String action = request.getParameter("action");
			if(action.equalsIgnoreCase("login")) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				ProxyUtente resultedUser;

				AutenticazioneServiceImpl loginService = new AutenticazioneServiceImpl();
				resultedUser = loginService.login(username, password);

				if (resultedUser!=null) {
					// Autenticazione andata a buon fine
					request.getSession().setAttribute("user", resultedUser);
					response.sendRedirect(request.getContextPath() + "/SelezioneRuolo.jsp");
                    return;
				}else {
					// Autenticazione fallita
					request.getSession().setAttribute("error","Username o Password Errati");                               
					response.sendRedirect(request.getContextPath() + "/Autenticazione");
				}
			}
			if(action.equalsIgnoreCase("roleSelection")) {
				//recupero oggetto user da sessione
				ProxyUtente resultedUser = (ProxyUtente) request.getSession().getAttribute("user");
				String ruolo = request.getParameter("ruolo");

				loadUserAddresses(request);

				ArrayList<Ruolo> ruoli;
				ruoli = resultedUser.getRuoli();
				for(Ruolo r: ruoli){ 
					System.out.println(r.getNomeRuolo());
					if(r.getNomeRuolo().equals(ruolo)){
						switch(ruolo){
						case "Cliente": 
							response.sendRedirect(request.getContextPath() +"/AreaRiservata");
							return;                                                          
						case "GestoreOrdini": 
							response.sendRedirect(request.getContextPath() +"/GestioneOrdini");
							return;                                
						case "GestoreCatalogo":                                
							response.sendRedirect(request.getContextPath() +"/GestioneCatalogo");
							return;  
						default:
							// Ruolo non associato all'utente
							request.getSession().setAttribute("error","Ruolo scelto non corrispondente ai ruoli dell'utente");
							request.getRequestDispatcher("Autenticazione").forward(request, response);
							break;    
						}
						break;
					}
					request.getSession().setAttribute("error","Ruolo scelto non corrispondente ai ruoli del utente");
					request.getRequestDispatcher("Autenticazione").forward(request, response);
				}

			}
			if(action.equals("logout")){
				request.getSession().invalidate();// Invalida la sessione
				response.sendRedirect(request.getContextPath() + "/Autenticazione"); 
				return;
			}

		} catch (SQLException | AutenticazioneException.UtenteInesistenteException ex) {
			Logger.getLogger(AutenticazioneController.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().setAttribute("error", "Username o Password Errati");
			response.sendRedirect(request.getContextPath() + "/Autenticazione");
		}
	}
	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
	public void loadUserAddresses(HttpServletRequest request) throws SQLException {
		ProxyUtente u = (ProxyUtente) request.getSession().getAttribute("user");
		if (u != null) {
			IndirizzoDAODataSource indDAO = new IndirizzoDAODataSource();
			ArrayList<Indirizzo> indirizzi = indDAO.doRetrieveAll("Indirizzo.via", u.getUsername());
			request.setAttribute("Indirizzi", indirizzi); 

		}
	}
}
