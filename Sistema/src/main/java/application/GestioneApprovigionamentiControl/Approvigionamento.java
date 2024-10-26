package application.GestioneApprovigionamentiControl;

import application.GestioneApprovvigionamenti.RichiestaApprovvigionamento;
import application.NavigazioneService.ProxyProdotto;
import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raffa
 */
public class Approvigionamento extends HttpServlet {

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
        
        if(request.getSession().getAttribute("action").equals("viewProductList")){
            //Checking if the value if the user accesses Approvigionamento directly.
            if(request.getSession().getAttribute("products")==null){
                response.sendRedirect(request.getContextPath() + "/GestioneApprovigionamentiController?action=viewProductList&page=1");
                return;
            }
            Collection<ProxyProdotto> all_products_list = (Collection<ProxyProdotto>) request.getSession().getAttribute("products");
            request.setAttribute("all_pr_list", all_products_list);
            int page = (int)request.getSession().getAttribute("page");
            request.setAttribute("page", page);
            boolean hasNextPage = (boolean)request.getSession().getAttribute("hasNextPage");
            request.setAttribute("hasNextPage", hasNextPage);
            request.getRequestDispatcher("protected/gestoreOrdini/approvigionamento.jsp").forward(request, response);
        }     
        else{
            if(request.getSession().getAttribute("supply_requests")==null){
                response.sendRedirect(request.getContextPath() + "/GestioneApprovigionamentiController?action=viewList&page=1");
                return;
            }
            Collection<RichiestaApprovvigionamento> supply_requests = (Collection<RichiestaApprovvigionamento>) request.getSession().getAttribute("supply_requests");        
            request.setAttribute("supply_requests", supply_requests);   
            int page = (int)request.getSession().getAttribute("page");
            request.setAttribute("page", page);
            boolean hasNextPage = (boolean)request.getSession().getAttribute("hasNextPage");
            request.setAttribute("hasNextPage", hasNextPage);
            request.getRequestDispatcher("protected/gestoreOrdini/richiesteApprovigionamento.jsp").forward(request, response);
        }
        String error = (String)request.getSession().getAttribute("error");
        request.getSession().removeAttribute("error");
        request.setAttribute("error", error);       
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

}
