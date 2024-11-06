package application.GestioneCatalogo.GestioneCatalogoControl;

import application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoNulloException;
import application.GestioneOrdini.GestioneOrdiniService.GestioneOrdiniServiceImpl;
import application.Navigazione.NavigazioneControl.PaginationUtils;
import application.Navigazione.NavigazioneService.NavigazioneServiceImpl;
import application.Navigazione.NavigazioneService.Prodotto;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.Navigazione.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.ErroreTopImmagineException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoCodiceException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoModelloException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoNomeException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.Navigazione.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;
import storage.GestioneOrdiniDAO.PagamentoDAODataSource;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
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
import javax.servlet.http.Part;

//import org.apache.tomcat.jdbc.pool.DataSource;
import javax.sql.DataSource;

/**
 * Servlet per la gestione del catalogo prodotti.
 * 
 * Questa servlet consente di visualizzare, aggiungere e rimuovere prodotti dal catalogo.
 * Gestisce anche la paginazione dei risultati e il caricamento delle immagini.
 * 
 * @author raffy
 */

@MultipartConfig
@WebServlet(name = "GestioneCatalogoController", urlPatterns = {"/GestioneCatalogoController"})
public class GestioneCatalogoController extends HttpServlet {

	/**
	 * Serial Version UID: per la serializzazione della servlet.
	 */
	private static final long serialVersionUID = 1L;

	private ProdottoDAODataSource productDAO;
	private PhotoControl photoControl;
	private PagamentoDAODataSource paymentDAO;
	private UtenteDAODataSource userDAO;
	private OrdineDAODataSource orderDAO;
	private DataSource ds;
	private NavigazioneServiceImpl ns;
	private GestioneCatalogoServiceImpl gcs;
	private GestioneOrdiniServiceImpl gos;
	private PaginationUtils pu;

	private static int pr_pagina = 50;

	/**
	 * Inizializza la servlet, configurando photoControl, productDAO, pu e gcs.
	 *
	 * @throws ServletException : se si verifica un errore durante l'inizializzazione
	 */
	/*Init per Testing
	public void init() throws ServletException {
		ds = new DataSource();
		photoControl = new PhotoControl(ds);
		orderDAO = new OrdineDAODataSource(ds);
		paymentDAO = new PagamentoDAODataSource(ds);

		try {
			productDAO = new ProdottoDAODataSource(ds, photoControl);
			userDAO = new UtenteDAODataSource(ds);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ns = new NavigazioneServiceImpl(productDAO);
		gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);
		gos = new GestioneOrdiniServiceImpl(orderDAO,userDAO, productDAO, paymentDAO);
		pu = new PaginationUtils(ns, gcs, gos);
	}
	*/
	public void init() throws ServletException {
		Context initContext = new InitialContext();
		Context envContext = (Context) initContext.lookup("java:/comp/env");
		ds = (DataSource) envContext.lookup("jdbc/techheaven");
		photoControl = new PhotoControl(ds);
		orderDAO = new OrdineDAODataSource(ds);
		paymentDAO = new PagamentoDAODataSource(ds);

		try {
			productDAO = new ProdottoDAODataSource(ds, photoControl);
			userDAO = new UtenteDAODataSource(ds);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ns = new NavigazioneServiceImpl(productDAO);
		gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);
		gos = new GestioneOrdiniServiceImpl(orderDAO,userDAO, productDAO, paymentDAO);
		pu = new PaginationUtils(ns, gcs, gos);
	}
	
	//Costruttore test
	public GestioneCatalogoController(ProdottoDAODataSource productDAO, GestioneCatalogoServiceImpl gcs, PaginationUtils pu) {
		this.gcs = gcs;
		this.pu = pu;
		this.productDAO = productDAO;
	}


	/**
	 * Gestisce le richieste GET per visualizzare i prodotti.
	 * Esegue la paginazione e restituisce i dati in formato JSON.
	 *
	 * @param request: la richiesta HTTP.
	 * @param response: la risposta HTTP.
	 * 
	 * @throws ServletException: se si verifica un errore nella servlet.
	 * @throws IOException: se si verifica un errore durante l'elaborazione 
	 * 						dell'input/output.
	 */

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try{

			int page = 1; 

			try{
				if (request.getParameter("page") != null)
					page = Integer.parseInt(
							request.getParameter("page")); 
				if(request.getParameter("action") != null){
					request.getSession().setAttribute("displayGalleryForm", request.getParameter("action"));
				}
			} catch(Exception e) {
				Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, e);
				page=1;
			}      

			Collection <ProxyProdotto> products = paginateProducts(request, page);

			Map <Integer, String> products_subcategories = new HashMap<>();
			for(ProxyProdotto prod : products){
				products_subcategories.put(prod.getCodiceProdotto(), prod.getSottocategoriaAsString());
			}
			request.getSession().setAttribute("products_subcategories", products_subcategories);
			request.getSession().setAttribute("page", page);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("products", products);
			responseData.put("hasNextPage", request.getSession().getAttribute("hasNextPage"));

			Gson gson = new Gson();
			String jsonResponse = gson.toJson(responseData);

			PrintWriter out = response.getWriter();
			out.print(jsonResponse);
			out.flush();

		} catch(Exception ex) {
			Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Esegue la paginazione dei prodotti.
	 *
	 * @param request: la richiesta HTTP.
	 * @param page: il numero della pagina da visualizzare.
	 * 
	 * @return una collezione di prodotti per la pagina richiesta.
	 * 
	 * @throws SQLException: se si verifica un errore durante l'accesso al database.
	 * @throws Exception: se si verifica un errore generico.
	 */

	public Collection<ProxyProdotto> paginateProducts(HttpServletRequest request, int page) throws SQLException, Exception{

		Collection <ProxyProdotto> currentPageResults;
		Collection <ProxyProdotto> nextPageResults;
		int previoslyFetchedPage = pu.getSessionAttributeAsInt(request, "previosly_fetched_page", 0);
		if(page==previoslyFetchedPage){                 
			currentPageResults = pu.getSessionCollection(request, "nextPageResults", ProxyProdotto.class);
			request.getSession().setAttribute("products", currentPageResults);              
		}
		else {
			currentPageResults = pu.performPagination(page, pr_pagina);
			request.getSession().setAttribute("products", currentPageResults);                  
		}     
		nextPageResults = pu.performPagination(page+1, pr_pagina);

		request.getSession().setAttribute("nextPageResults", nextPageResults);
		request.getSession().setAttribute("previosly_fetched_page", page+1);

		boolean hasNextPage = pu.checkIfItsTheSamePage (currentPageResults, nextPageResults, ProxyProdotto.class);   
		request.getSession().setAttribute("hasNextPage", hasNextPage);
		return currentPageResults;
	}

	/**
	 * Gestisce le richieste POST per aggiungere o rimuovere prodotti dal catalogo.
	 *
	 * @param request: la richiesta HTTP.
	 * @param response: la risposta HTTP.
	 * 
	 * @throws ServletException: se si verifica un errore nella servlet.
	 * @throws IOException: se si verifica un errore durante l'input/output.
	 */

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException { 

		String product_id = (String) request.getParameter("productId");

		String productName = request.getParameter("productName");
		String topDescrizione = request.getParameter("topDescrizione");
		String dettagli = request.getParameter("dettagli");

		float price = Float.parseFloat(request.getParameter("price"));

		String categoria = request.getParameter("categoria");
		String marca = request.getParameter("marca");
		String modello = request.getParameter("modello");    
		String quantity = (String) request.getParameter("quantita");
		String sottocategoria = request.getParameter("sottocategoria");

		String inVetrinaParam = request.getParameter("inVetrina");
		boolean inVetrina = "true".equals(inVetrinaParam);
		String inCatalogoParam = request.getParameter("inCatalogo");
		boolean inCatalogo = "true".equals(inCatalogoParam);

		//Retrieves the Action the Servlet needs to do With the Retrieved Products Information
		try {
			String action = request.getParameter("action");

			if(action.equals("addProduct")){
				// Si recupera l'immagine di presentazione del prodotto
				Part filePart = request.getPart("file"); 
				int quantità = -1;
				
				try {
					quantità = Integer.parseInt(quantity);
					
				}catch (NumberFormatException e) {
					QuantitaProdottoException ex = new QuantitaProdottoException("La quantità di un prodotto disponibile deve essere almeno 1");
					Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, ex);
					request.getSession().setAttribute("error", ex.getMessage());
					System.out.println(ex.getMessage());
					response.sendRedirect(request.getContextPath()+"/AggiuntaAlCatalogo");
				}
				if(filePart == null) {

					gcs.aggiuntaProdottoInCatalogo(product_id, productName, marca, modello, topDescrizione, dettagli, price,
							quantità, categoria, sottocategoria, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

					request.getSession().setAttribute("error", "Prodotto Aggiunto con Successo!");
				}

				if (filePart != null) {

					gcs.aggiuntaProdottoInCatalogo(product_id, productName, marca, modello, topDescrizione, dettagli, price,
							quantità, categoria, sottocategoria, inCatalogo, inVetrina, productDAO, 1, pr_pagina);

					//Si controlla se il prodotto appartiene ad una sottocategoria
					Sottocategoria s_categoria;

					if(sottocategoria==null || sottocategoria.equals("null")) 
						s_categoria= null;
					else 
						s_categoria = Sottocategoria.valueOf(sottocategoria);

					int prod_id = Integer.parseInt(product_id);

					Prodotto product = new Prodotto(prod_id, productName, topDescrizione, dettagli, price, Categoria.valueOf(categoria),
							s_categoria, marca, modello, quantità, inCatalogo, inVetrina);  

					InputStream fileContent = filePart.getInputStream();

					gcs.inserimentoTopImmagine(product, "TOP_IMMAGINE", fileContent, 1, pr_pagina);

					request.getSession().setAttribute("error", "Prodotto Con Top immagine Aggiunto con Successo!");

				}  

			}else if(action.equals("deleteProduct")){

				ProxyProdotto pr_todelete = null;

				if(product_id != null) {
					int prod_id = Integer.parseInt(product_id);
					int quantità = Integer.parseInt(quantity);

					pr_todelete = new ProxyProdotto (prod_id, productName, topDescrizione, dettagli, price, Categoria.valueOf(categoria),
							marca, modello, quantità, inCatalogo, inVetrina );
				}

				try {              

					gcs.rimozioneProdottoDaCatalogo(pr_todelete, 1, pr_pagina);
					request.getSession().setAttribute("error", "Prodotto Eliminato con Successo!");

				} catch (ProdottoNonInCatalogoException | ProdottoNulloException ex) {
					Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, ex);
					request.getSession().setAttribute("error", ex.getMessage());
					response.sendRedirect(request.getContextPath()+"/UpdateProductInfos");
					return;
				}
			}

			response.sendRedirect(request.getContextPath() + "/Catalogo");

		} catch (NumberFormatException | SQLException ex) {
			Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println(ex.getMessage());
			request.getSession().setAttribute("error", ex.getMessage());
			response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");       

		} catch (ProdottoInCatalogoException | ProdottoNonInCatalogoException | CategoriaProdottoException | 
				QuantitaProdottoException | ErroreSpecificaAggiornamentoException | ErroreTopImmagineException |
				FormatoNomeException | FormatoModelloException | FormatoMarcaException | PrezzoProdottoException
				| FormatoTopDescrizioneException | FormatoDettagliException | AppartenenzaSottocategoriaException|
				FormatoCodiceException | SottocategoriaProdottoException ex) {

			Logger.getLogger(GestioneCatalogoController.class.getName()).log(Level.SEVERE, null, ex);
			request.getSession().setAttribute("error", ex.getMessage());
			System.out.println(ex.getMessage());
			response.sendRedirect(request.getContextPath()+"/AggiuntaAlCatalogo");
		} 
	}      

}
