package tools;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;

/**
 * Laedt Images und BufferedImages mit internem Exception-Handling.
 * @author Becker, Schumacher, Laibold
 *
 */
public class ImageLoader {
	Image image;
	BufferedImage bufferedImage;
	
	/**
	 * Erzeugt einen neuen ImageLoader, der Images und BufferedImages laden kann
	 */
	public ImageLoader() {
		this.image = null;
		this.bufferedImage = null;
	}
	
	/**
	 * Laedt ein Bild vom angegebenen Pfad als Image-Objekt
	 * @param path Pfad inkl. Datei des zu ladenden Bildes
	 * @return Bild als Image-Objekt
	 */
	public Image loadImage(String path) {
		try {
			image = new Image(new FileInputStream(path));
		} catch (FileNotFoundException e1) {
			System.err.println("Fehler: " + path + " konnte nicht geladen werden.");
		}
		return image;
	}
}
