package application.NavigazioneControl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import application.NavigazioneService.NavigazioneException.ErroreRicercaCategoriaException;

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
        try {
			sce.getServletContext().setAttribute("telefoni", pu.performPagination(categoria, 1, 6, "menu"));
		} catch (ErroreRicercaCategoriaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String categoria2 = "GRANDI_ELETTRODOMESTICI"; 
        try {
			sce.getServletContext().setAttribute("gr_elettr", pu.performPagination(categoria2, 1, 6, "menu"));
		} catch (ErroreRicercaCategoriaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        throw new UnsupportedOperationException("Invocato ContextDestroyed : operazione non supportata.");
    }
}
