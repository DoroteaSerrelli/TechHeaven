package application.Navigazione.NavigazioneControl;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.Navigazione.NavigazioneService.NavigazioneServiceImpl;
import application.Navigazione.NavigazioneService.NavigazioneException.ErroreRicercaCategoriaException;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 * Web application lifecycle listener.
 *
 * @author raffa
 */
public class NewServletListener implements ServletContextListener {
	
	private PaginationUtils pu;
	
	// Costruttore senza argomenti (richiesto da Tomcat)
    public NewServletListener() throws SQLException {
        // Recupera il DataSource configurato tramite JNDI
    	DataSource ds = null;
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup("jdbc/techheaven");  // Assicurati che il nome JNDI sia corretto
        } catch (NamingException e) {
            throw new SQLException("Error initializing DataSource via JNDI: " + e.getMessage(), e);
        }

        // Crea le istanze delle classi DAO e dei servizi passando il DataSource configurato
        PagamentoDAODataSource paymentDAO = new PagamentoDAODataSource(ds);
        PhotoControl photoControl = new PhotoControl(ds);
        UtenteDAODataSource userDAO = new UtenteDAODataSource(ds);
        ProdottoDAODataSource productDAO = new ProdottoDAODataSource(ds, photoControl);
        OrdineDAODataSource orderDAO = new OrdineDAODataSource(ds);
        GestioneOrdiniServiceImpl gos = new GestioneOrdiniServiceImpl(orderDAO, userDAO, productDAO, paymentDAO);
        GestioneCatalogoServiceImpl gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);
        NavigazioneServiceImpl navi_service = new NavigazioneServiceImpl(productDAO);

        pu = new PaginationUtils(navi_service, gcs, gos);
    }
	
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
