package application.Navigazione.NavigazioneControl;

import application.GestioneCatalogo.GestioneCatalogoControl.ImageResizer;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.Navigazione.NavigazioneService.Prodotto;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.tomcat.jdbc.pool.DataSource;
import javax.sql.DataSource;

import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffa
 */
public class ImageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProdottoDAODataSource productDAO;
	public PhotoControl photoControl;
	public DataSource ds;

	/*Init per Testing
	public void init() throws ServletException {
		ds = new DataSource();
		photoControl = new PhotoControl(ds);
		try {
			productDAO = new ProdottoDAODataSource(ds, photoControl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	public void init() throws ServletException {
		Context initContext = new InitialContext();
		Context envContext = (Context) initContext.lookup("java:/comp/env");		 
		ds = new (DataSource) envContext.lookup("jdbc/techheaven");
		photoControl = new PhotoControl(ds);
		try {
			productDAO = new ProdottoDAODataSource(ds, photoControl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Costruttore per il test
	public ImageServlet(DataSource ds, PhotoControl photoControl, ProdottoDAODataSource productDAO) {
		this.ds = ds;
		this.photoControl = photoControl;
		this.productDAO = productDAO;
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
	public void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String productId = request.getParameter("productId");

			if (productId == null) {
				ProdottoNonInCatalogoException e = new ProdottoNonInCatalogoException("Non è possibile visualizzare le immagini del prodotto richiesto "
						+ "perché il codice del prodotto non è specificato");
				request.getSession().setAttribute("errorMessage", e.getMessage());
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
				return;
			}

			ProxyProdotto proxy = productDAO.doRetrieveProxyByKey(Integer.parseInt(productId));

			if(proxy == null) {
				ProdottoNonInCatalogoException e = new ProdottoNonInCatalogoException("Non è possibile visualizzare le immagini del prodotto richiesto "
						+ "perché il prodotto specificato non esiste in catalogo.");
				request.getSession().setAttribute("errorMessage", e.getMessage());
				response.sendRedirect(request.getContextPath() + "/common/paginaErrore.jsp");
				return;
			}

			Prodotto selectedProduct = proxy.mostraProdotto(); //non può essere null
			// Default placeholder image path
			String placeholderPath = getServletContext().getRealPath("/images/site_images/placeholder.png");

			BufferedImage imageToServe = null;

			if (selectedProduct.getTopImmagine() != null) {

				byte[] topImageBytes = selectedProduct.getTopImmagine();
				BufferedImage originalImage = ImageResizer.byteArrayToImage(topImageBytes);

				// Get resizing parameters from query
				int width = 400; // default width
				int height = 400; // default height

				try {
					String widthParam = request.getParameter("width");
					String heightParam = request.getParameter("height");

					if (widthParam != null) {
						width = Integer.parseInt(widthParam);
					}
					if (heightParam != null) {
						height = Integer.parseInt(heightParam);
					}
				} catch (NumberFormatException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dimensioni dell'immagine fornite non valide");
					return;
				}

				// Resize image to specified dimensions
				imageToServe = ImageResizer.resizeImage(originalImage, width, height);
			}

			if (imageToServe == null) {
				// Load and use placeholder image if no image to serve
				File placeholderFile = new File(placeholderPath);
				imageToServe = ImageIO.read(placeholderFile);
			}

			// Convert BufferedImage back to byte array
			byte[] imageBytes = ImageResizer.imageToByteArray(imageToServe, "jpg");

			// Set response content type
			response.setContentType("image/jpeg");

			try ( // Write image to response output stream
				
					ServletOutputStream outStream = response.getOutputStream()) {
				outStream.write(imageBytes);
				outStream.flush();
			}
		} catch (SQLException | SottocategoriaProdottoException | CategoriaProdottoException ex) {
			Logger.getLogger(ImageServlet.class.getName()).log(Level.SEVERE, null, ex);

		}
	}    

	// Method to convert byte[] to Base64 String
	public static String convertToBase64(byte[] imageBytes) {
		// Encode byte array into a Base64 string
		return Base64.getEncoder().encodeToString(imageBytes);
	}

	public static List<String> loadGallery(Prodotto product) throws IOException {
		// Get the image gallery from Prodotto
		List<byte[]> galleryImages = product.getGalleriaImmagini(); 

		// Convert the byte[] images to base64 strings
		List<String> base64Gallery = new ArrayList<>();
		for (byte[] imageData : galleryImages) {
			if(imageData==null || imageData.length==0);
			else{
				// Detect image type (for simplicity, assume JPEG, but you can use libraries like Apache Tika)
				String base64Image = Base64.getEncoder().encodeToString(imageData);
				String mimeType = detectImageMimeType(imageData); // This method can be implemented to detect image type
				String imageUrl = "data:" + mimeType + ";base64," + base64Image;
				base64Gallery.add(imageUrl);
			}
		}
		return base64Gallery;
	}

	private static String detectImageMimeType(byte[] imageData) throws IOException {
		// Simple implementation using ImageIO to check image type
		InputStream is = new ByteArrayInputStream(imageData);
		BufferedImage bufferedImage = ImageIO.read(is);
		if (bufferedImage == null) {
			throw new IOException("Cannot detect image format");
		}
		String formatName = ImageIO.getImageReadersByFormatName("jpeg").hasNext() ? "jpeg" : "png"; // adjust as needed
		return "image/" + formatName;
	}

	// Static method to load presentation image for a product
	public static String loadPresentationPhoto(Prodotto product) throws IOException {
		// Get the image gallery directly from the Prodotto object
		byte[] presentationImage = product.getTopImmagine(); // Use the Prodotto method to get images

		// Convert the byte[] images to base64 strings
		String base64MainPhoto;

		String base64Image = Base64.getEncoder().encodeToString(presentationImage);
		String imageUrl = "data:image/jpeg;base64," + base64Image;
		base64MainPhoto= imageUrl;

		return base64MainPhoto;
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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

}
