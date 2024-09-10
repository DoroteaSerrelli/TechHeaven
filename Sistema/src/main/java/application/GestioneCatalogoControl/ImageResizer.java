/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.GestioneCatalogoControl;

import application.NavigazioneService.Prodotto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author raffa
 */
public class ImageResizer {
    
   // Convert byte[] to BufferedImage
    public static BufferedImage byteArrayToImage(byte[] imageBytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(bis);
    }

    // Convert BufferedImage to byte[]
    public static byte[] imageToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos); // e.g., "jpg"
        return baos.toByteArray();
    }
    
    // Method to encode BufferedImage back to base64 (if needed)
    public static String encodeImageToBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, format, bos);
        byte[] imageBytes = bos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    // Method to resize an image using Thumbnailator
    public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) throws IOException {
        return Thumbnails.of(originalImage)
                         .size(width, height)
                         .asBufferedImage();
    }

  // Utility Method to Process Gallery Images as byte[] and Convert to Base64 After Resizing
public static ArrayList<String> processGalleryAndConvertToBase64(List<byte[]> galleryImages) throws IOException {
    ArrayList<String> resizedBase64Gallery = new ArrayList<>();

    for (byte[] imageBytes : galleryImages) {
        // Convert byte array to BufferedImage
        BufferedImage galleryImage = byteArrayToImage(imageBytes);

        // Resize each gallery image (example: 200x200 pixels)
        BufferedImage resizedGalleryImage = resizeImage(galleryImage, 200, 200);

        // Convert resized image to byte array
        byte[] resizedImageBytes = imageToByteArray(resizedGalleryImage, "jpg");

        // Convert the resized byte array back to base64 for storage/display purposes
        String resizedBase64Image = Base64.getEncoder().encodeToString(resizedImageBytes);
        
        // Add the base64 image to the list
        resizedBase64Gallery.add("data:image/jpeg;base64," + resizedBase64Image);
    }

    return resizedBase64Gallery;
}
    // Utility Method That Resizes Top Image And Gives It Back as A (Resized) Byte Array
    public static byte[] resizeTopImage(Prodotto selectedProd, int width, int height) throws IOException{
        //Store the decoded base 64 image as a BufferedImage <---- Forse c'è modo di saltare
        //questo passaggio con I byte? :_)
        // Resize the top image (selectedProd.getTopImmagine() returns a byte[])
        BufferedImage toResize = ImageResizer.byteArrayToImage(selectedProd.getTopImmagine());
        //DEBUG PRINT ORIGINAL IMAGE SIZE BEFORE RESIZING:
        //System.out.println("Original Top Image Dimensions: " + toResize.getWidth() + "x" + toResize.getHeight());

        // Resize the image to 100x100 pixels
        BufferedImage resizedImage = ImageResizer.resizeImage(toResize, width, height);
        //DEBUG PRINT RESIZED IMAGE SIZE:
      //  System.out.println("Resized Top Image Dimensions: " + resizedImage.getWidth() + "x" + resizedImage.getHeight());

        // Convert resized image back to byte[]
        byte[] resizedImageBytes = ImageResizer.imageToByteArray(resizedImage, "jpg");
        
        return resizedImageBytes;
    }

}
