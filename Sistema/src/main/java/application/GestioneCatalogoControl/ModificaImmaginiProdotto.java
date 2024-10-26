package application.GestioneCatalogoControl;

import application.GestioneCatalogoService.CatalogoException;
import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProdottoException.DettagliImmagineNonPresenteException;
import application.NavigazioneService.ProdottoException.ErroreDettagliImmagineException;
import application.NavigazioneService.ProdottoException.ErroreTopImmagineException;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.jdbc.pool.DataSource;

/**
 * Servlet per la gestione dell'aggiornamento delle immagini di un prodotto.
 * Permette l'inserimento e la rimozione delle immagini nella galleria di un prodotto.
 * 
 * @author raffa
 */

@MultipartConfig(
		fileSizeThreshold = 1024 * 1024,  // 1MB
		maxFileSize = 1024 * 1024 * 10,   // 10MB
		maxRequestSize = 1024 * 1024 * 50 // 50MB
		)

public class ModificaImmaginiProdotto extends HttpServlet {

	/**
	 * Serial Version UID per la serializzazione della servlet.
	 */
	private static final long serialVersionUID = 1L;

	private ProdottoDAODataSource productDAO;
	private PhotoControl photoControl;
	private DataSource ds;
	private GestioneCatalogoServiceImpl gcs;

	private int perPage= 50;

	/**
	 * Inizializza la servlet, configurando photoControl, productDAO e gcs.
	 *
	 * @throws ServletException : se si verifica un errore durante l'inizializzazione
	 */

	@Override
	public void init() throws ServletException {
		ds = new DataSource();
		photoControl = new PhotoControl(ds);
		try {
			productDAO = new ProdottoDAODataSource(new DataSource(), photoControl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);

	}

	/**
	 * Gestisce il metodo HTTP GET, ridirezionando ogni richiesta GET
	 * al metodo doPost.
	 *
	 * @param request : la richiesta
	 * @param response : la risposta
	 * 
	 * @throws ServletException : se si verifica un errore specifico del servlet
	 * @throws IOException: se si verifica un errore di I/O
	 */

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Gestisce il metodo HTTP POST per l'aggiornamento delle immagini di un prodotto.
	 * 
	 * Questo metodo recupera i dati del prodotto in formato JSON dalla richiesta, gestisce le azioni relative alle immagini,
	 * inclusa l'aggiunta di un'immagine di presentazione o di galleria, e la rimozione delle immagini esistenti.
	 * Se un'immagine viene aggiunta, viene salvata sia nel database che nella sessione dell'utente.
	 * In caso di errore, vengono registrati i dettagli dell'eccezione e viene inviata una risposta di errore al client.
	 * 
	 *
	 * @param request: la richiesta contenente i dati del prodotto e le azioni da eseguire
	 * @param response: la risposta da inviare al client
	 * 
	 * @throws ServletException: se si verifica un errore specifico nella servlet durante l'elaborazione
	 * @throws IOException: se si verifica un errore di I/O durante l'elaborazione della richiesta
	 */

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {  

		request.setCharacterEncoding("UTF-8");

		// Recupera i dati del prodotto in formato JSON dalla richiesta
		String productJson = request.getParameter("product"); 

		if (productJson != null) {
			Gson gson = new Gson();
			Prodotto product = gson.fromJson(productJson, Prodotto.class);

			try {
				String updatelog = "";
				String main_photoAction = (String) request.getParameter("main_photoAction");
				//Si recuperano le immagini di dettaglio del prodotto dalla sessione
				List<byte[]> originalGallery = (List<byte[]>) request.getSession().getAttribute("originalGallery"); 
				String gallery_photoActions = (String) request.getParameter("gallery_photoActions");  
				if(gallery_photoActions!=null && gallery_photoActions.equals("delete")){
					deleteGalleryImage(request, response, originalGallery, product);
				}
				else{
					Part filePart = request.getPart("presentazione"); // "presentazione" is the name attribute in the form                           
					InputStream fileContent = retrieveFileContent(filePart);

					byte [] inputImage = inputStreamToByteArray(fileContent);

					if(originalGallery==null || originalGallery.isEmpty()){
						originalGallery = new ArrayList<>();
					}                                   
					if(main_photoAction!=null && main_photoAction.equals("add")){

						// Recupera il contenuto del file all'inizio e lo memorizza in un
						// array di byte che viene riconvertito in un inputstream quando si aggiunge
						// l'immagine al database.
						gcs.inserimentoTopImmagine(product, "TOP_IMMAGINE", byteArrayToInputStream(inputImage), 1, perPage);
						product.setTopImmagine(inputStreamToByteArray(fileContent));
					}else

						// Gestione azioni che non corrispondono all'aggiunta di una immagine di presentazione
						throw new IllegalArgumentException("Unexpected value: " + main_photoAction);


					if (gallery_photoActions != null && gallery_photoActions.equals("addToGallery")) {

						//Recupera la parte del file dalla richiesta

						originalGallery.add(inputImage);
						product.setGalleriaImmagini((ArrayList<byte[]>) originalGallery);

						gcs.inserimentoImmagineInGalleriaImmagini(product, "AGGIUNTA_DETT_IMMAGINE", byteArrayToInputStream(inputImage), 1, perPage);
						request.getSession().setAttribute("originalGallery", originalGallery);
						updatelog+= "L'Immagine Inserita e' Stata Aggiunta Correttamente alla Galleria";

					}else
						throw new IllegalArgumentException("Unexpected gallery action: " + gallery_photoActions);


					sendGalleryUpdateOutcome(updatelog, response);
				}         

			} catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | ErroreSpecificaAggiornamentoException | ErroreDettagliImmagineException | ErroreTopImmagineException ex) {
				Logger.getLogger(ModificaImmaginiProdotto.class.getName()).log(Level.SEVERE, null, ex);
				response.getWriter().write("An error occurred: " + ex.getMessage());
				String message = "Si è verificato un errore: " + ex.getMessage();
				sendGalleryUpdateOutcome(message, response);

			}       
		}     
	}


	/**
	 * Elimina un'immagine di dettaglio dalla galleria di immagini associate al prodotto.
	 *
	 * @param request : la richiesta client
	 * @param response : la risposta da fornire al client
	 * @param originalGallery: la galleria di immagini di dettaglio
	 * @param product: il prodotto associato
	 * 
	 * @return un messaggio di log relativo all'aggiornamento
	 */

	private String deleteGalleryImage(HttpServletRequest request, HttpServletResponse response, List<byte[]> originalGallery, Prodotto product){
		String updatelog = "";

		// Recupera l'indice dell'immagine dalla richiesta
		int imageToRemoveIndex = Integer.parseInt(request.getParameter("imageIndex"));

		// Verifica se l'indice è valido
		if (imageToRemoveIndex >= 0 && imageToRemoveIndex < originalGallery.size()) {
			try {
				// Ottiene l'immagine in byte[] da rimuovere e la converte in InputStream
				byte[] imageBytesToRemove = originalGallery.get(imageToRemoveIndex);

				InputStream imageStream = new ByteArrayInputStream(imageBytesToRemove);
				gcs.cancellazioneImmagineInGalleria(product, "RIMOZIONE_DETT_IMMAGINE", imageStream, 1, perPage);
				originalGallery.remove(imageToRemoveIndex);

				// Aggiorna l'attributo di sessione con la galleria modificata
				request.getSession().setAttribute("originalGallery", originalGallery);
				response.getWriter().write("Image deleted successfully.");                                 
				updatelog+= "L'Immagine Selezionata e' Stata Rimossa Con Successo dalla Galleria";

			} catch (ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | IOException | ErroreSpecificaAggiornamentoException | ErroreDettagliImmagineException | DettagliImmagineNonPresenteException ex) {
				Logger.getLogger(ModificaImmaginiProdotto.class.getName()).log(Level.SEVERE, null, ex);
				updatelog+= ex.getMessage();
			}
		}
		else{
			updatelog+= "Non e' Stato Possibile Rimuovere L'Immagine dalla Galleria";
		}
		return updatelog;
	}

	/**
	 * Invia il risultato dell'aggiornamento della galleria come risposta JSON.
	 *
	 * @param message : il messaggio da inviare
	 * @param response : la risposta elaborata dal server
	 */

	private void sendGalleryUpdateOutcome(String message, HttpServletResponse response){
		Gson gson = new Gson();
		String jsonResponse = gson.toJson(message);
		response.setContentType("application/json");


		try (PrintWriter out = response.getWriter()) {
			out.print(jsonResponse);
		} catch (IOException ex) {
			Logger.getLogger(ModificaImmaginiProdotto.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Metodo per trasformare un InputStream in un array di byte.
	 *
	 * @param inputStream: l'InputStream da convertire
	 * 
	 * @return l'array di byte risultante
	 * 
	 * @throws IOException: se si verifica un errore di I/O
	 */

	private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, length);
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Metodo per convertire un array di byte in un InputStream.
	 *
	 * @param byteArray: l'array di byte da convertire
	 * 
	 * @return l'InputStream risultante
	 */

	private InputStream byteArrayToInputStream(byte[] byteArray) {
		return new ByteArrayInputStream(byteArray);
	}

	/**
	 * Metodo per recuperare un InputStream da un file part ricevuto 
	 * da un modulo.
	 *
	 * @param filePart : la parte del file da cui recuperare il contenuto
	 * 
	 * @return l'InputStream del contenuto del file
	 * 
	 * @throws IOException : se si verifica un errore di I/O
	 */

	private InputStream retrieveFileContent(Part filePart) throws IOException{
		InputStream fileContent = null;

		if (filePart != null)
			fileContent = filePart.getInputStream();          

		return fileContent;
	}
}
