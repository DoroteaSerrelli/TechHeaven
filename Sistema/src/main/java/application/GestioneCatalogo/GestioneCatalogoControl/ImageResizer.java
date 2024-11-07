package application.GestioneCatalogo.GestioneCatalogoControl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;

import application.Navigazione.NavigazioneService.Prodotto;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Questa classe fornisce metodi per la manipolazione delle immagini,
 * inclusi il ridimensionamento, la conversione tra byte array e BufferedImage
 * e la codifica delle immagini in formato Base64.
 * 
 * @author raffa
 */

public class ImageResizer {

	/**
     * Converte un array di byte in un oggetto BufferedImage.
     *
     * @param imageBytes: l'array di byte che rappresenta l'immagine.
     * 
     * @return Un oggetto BufferedImage corrispondente all'immagine.
     * 
     * @throws IOException: se si verifica un errore durante la lettura dell'immagine.
     */
	
	public static BufferedImage byteArrayToImage(byte[] imageBytes) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
		return ImageIO.read(bis);
	}

	/**
     * Converte un oggetto BufferedImage in un array di byte.
     *
     * @param image : l'immagine da convertire.
     * @param format: il formato dell'immagine (ad esempio, "jpg").
     * 
     * @return Un array di byte che rappresenta l'immagine.
     * 
     * @throws IOException; se si verifica un errore durante la scrittura dell'immagine.
     */
	
	public static byte[] imageToByteArray(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, format, baos);
		return baos.toByteArray();
	}

	/**
     * Codifica un oggetto BufferedImage in formato Base64.
     *
     * @param image: l'mmagine da codificare.
     * @param format: il formato dell'immagine (ad esempio, "jpg").
     * 
     * @return una stringa che rappresenta l'immagine codificata in Base64.
     * 
     * @throws IOException: se si verifica un errore durante la scrittura dell'immagine.
     */
	
	public static String encodeImageToBase64(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, format, bos);
		byte[] imageBytes = bos.toByteArray();
		return Base64.getEncoder().encodeToString(imageBytes);
	}

	/**
     * Ridimensiona un'immagine utilizzando la libreria Thumbnailator.
     *
     * @param originalImage: l'immagine originale da ridimensionare.
     * @param width : la nuova larghezza dell'immagine.
     * @param height: la nuova altezza dell'immagine.
     * 
     * @return Un oggetto BufferedImage che rappresenta l'immagine ridimensionata.
     * 
     * @throws IOException: se si verifica un errore durante il ridimensionamento dell'immagine.
     */
	
	public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) throws IOException {
		return Thumbnails.of(originalImage)
				.size(width, height)
				.asBufferedImage();
	}

	/**
     * Elabora le immagini di una galleria in formato byte[] e 
     * le converte in Base64 dopo il ridimensionamento.
     *
     * @param galleryImages: la lista di immagini in formato byte[] da elaborare.
     * @param width: la larghezza desiderata per le immagini ridimensionate.
     * @param height: l'altezza desiderata per le immagini ridimensionate.
     * 
     * @return Una lista di stringhe in formato Base64 che rappresentano le immagini ridimensionate.
     * 
     * @throws IOException: se si verifica un errore durante l'elaborazione delle immagini.
     */
	
	public static ArrayList<String> processGalleryAndConvertToBase64(List<byte[]> galleryImages, int width, int height) throws IOException {
		ArrayList<String> resizedBase64Gallery = new ArrayList<>();

		for (byte[] imageBytes : galleryImages) {
			if(imageBytes==null || imageBytes.length==0);
			else{
				
				//Conversione di un array di byte in un oggetto BufferedImage
				BufferedImage galleryImage = byteArrayToImage(imageBytes);

				//Ridimensiona ogni immagine di dettaglio presente in galleria (es: 200x200 px)
				BufferedImage resizedGalleryImage = resizeImage(galleryImage, width, height);

				//Conversione di un immagine ridimensionata in array di byte
				byte[] resizedImageBytes = imageToByteArray(resizedGalleryImage, "jpg");

				//Conversione dell'array di byte avente immagini ridimensionate in base64 
				//al fine di archiviarle o visualizzarle
				String resizedBase64Image = Base64.getEncoder().encodeToString(resizedImageBytes);

				resizedBase64Gallery.add("data:image/jpeg;base64," + resizedBase64Image);
			}
		}

		return resizedBase64Gallery;
	}
	
	/**
     * Ridimensiona l'immagine di presentazione di un prodotto e restituisce il risultato come un array di byte.
     *
     * @param selectedProd: il prodotto di cui si desidera ridimensionare 
     * 						l'immagine in primo piano.
     * @param width: la larghezza desiderata per l'immagine ridimensionata.
     * @param height: l'altezza desiderata per l'immagine ridimensionata.
     * 
     * @return Un array di byte che rappresenta l'immagine di presentazione ridimensionata.
     * 
     * @throws IOException: se si verifica un errore durante il ridimensionamento dell'immagine.
     */
	
	public static byte[] resizeTopImage(Prodotto selectedProd, int width, int height) throws IOException{
		
		BufferedImage toResize = ImageResizer.byteArrayToImage(selectedProd.getTopImmagine());
		BufferedImage resizedImage = ImageResizer.resizeImage(toResize, width, height);
		
		return ImageResizer.imageToByteArray(resizedImage, "jpg");
	}

}
