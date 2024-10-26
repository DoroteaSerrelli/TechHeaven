package application.NavigazioneControl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 *
 * @author raffa
 */
public class NewServletListener implements ServletContextListener {
	
	private PaginationUtils pu;
	
	//Costruttore per testing
	public NewServletListener(PaginationUtils pu) {
		this.pu = pu;
	}
	
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        String categoria = "TELEFONIA"; 
        sce.getServletContext().setAttribute("telefoni", pu.performPagination(categoria, 1, 6, "menu"));

        String categoria2 = "GRANDI_ELETTRODOMESTICI"; 
        sce.getServletContext().setAttribute("gr_elettr", pu.performPagination(categoria2, 1, 6, "menu"));
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        throw new UnsupportedOperationException("Invocato ContextDestroyed : operazione non supportata.");
    }
}
