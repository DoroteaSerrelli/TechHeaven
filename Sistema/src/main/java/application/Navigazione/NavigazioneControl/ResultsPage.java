package application.Navigazione.NavigazioneControl;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import application.Navigazione.NavigazioneService.ProxyProdotto;

/**
 *
 * @author raffa
 */
@WebServlet(name = "ResultsPage", urlPatterns = "/ResultsPage")
public class ResultsPage extends HttpServlet {

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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
        Collection<ProxyProdotto> searchResult = (Collection<ProxyProdotto>) request.getSession().getAttribute("products");
        
        if(searchResult==null){
            String keyword = (String) request.getParameter("keyword");
            response.sendRedirect(request.getContextPath() + "/NavigazioneController?keyword="+keyword);
            return;
        }
        
        String keyword = (String) request.getSession().getAttribute("keyword");
        request.setAttribute("keyword", keyword);

        request.setAttribute("page",(int)request.getSession().getAttribute("page"));
        request.setAttribute("hasNextPage", request.getSession().getAttribute("hasNextPage"));
        
        request.getRequestDispatcher("common/searchResults.jsp").forward(request, response);
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
