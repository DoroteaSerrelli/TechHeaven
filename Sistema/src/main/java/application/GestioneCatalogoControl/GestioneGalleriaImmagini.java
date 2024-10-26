package application.GestioneCatalogoControl;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Questa servlet gestisce la richiesta di recupero dei dati della galleria
 * dalla sessione. Converte le immagini in formato base64 e restituisce
 * i dati come risposta JSON.
 * 
 * @author raffa
 */

public class GestioneGalleriaImmagini extends HttpServlet {

	/**
	 * Serial Version UID: per la serializzazione della servlet.
	 */
	private static final long serialVersionUID = 1L;


	/**
     * Gestisce le richieste GET per recuperare la galleria di immagini associate
     * ad un prodotto.
     *
     * @param request : la richiesta HTTP contenente i dati del client.
     * @param response : la risposta HTTP da inviare al client.
     * @throws ServletException : se si verifica un errore durante la gestione della richiesta.
     * @throws IOException : se si verifica un errore durante l'input/output.
     */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Recupera la lista della galleria in formato base64 dalla sessione
		
		List<byte[]> originalGallery = (List<byte[]>) request.getSession().getAttribute("originalGallery");
		List<String> base64Gallery = new ArrayList<>();
		
		if (originalGallery != null) {
			base64Gallery = ImageResizer.processGalleryAndConvertToBase64(originalGallery, 500, 500);
		}

		// Converte in JSON
		Gson gson = new Gson();
		String base64GalleryJson = gson.toJson(base64Gallery);

		// Trasmette la risposta
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print("{ \"base64Gallery\": " + base64GalleryJson + " }");
		out.flush();
	}

	/**
     * Gestisce le richieste POST inoltrando la richiesta al metodo doGet.
     *
     * @param request : la richiesta HTTP contenente i dati del client.
     * @param response : la risposta HTTP da inviare al client.
     * @throws ServletException : se si verifica un errore durante la gestione della richiesta.
     * @throws IOException : se si verifica un errore durante l'input/output.
     */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
