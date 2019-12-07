package player.elements;

import java.io.Serializable;
import java.nio.file.Paths;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import tools.ImageLoader;
import tools.PropertyHandler;

/**
 * Sounds sind die elementaren Bestandteile eines Tracks und werden in einem
 * Beat zusammengefasst. Sie bestehen aus Titel, Pfad zum Soundfile und einem
 * Bild, das als Thumbnail verwerdet werden kann. Sounds sind serialisierbar.
 * 
 * @author Becker, Schumacher, Laibold
 *
 */
@SuppressWarnings("serial")
public class Sound implements Serializable {
	private transient PropertyHandler propHandler = new PropertyHandler("resources/config.properties");
	private transient String thumbnailFolder = propHandler.getProperty("thumbnailFolder");
	private transient ImageLoader imageLoader = new ImageLoader();

	private String soundPath;
	private transient Image soundImage;
	
	/**
	 * Neuer Sound aus dem angegebenen Soundfile
	 * @param soundPath Pfad des Soundfiles
	 * @throws FileNotFoundException wenn die Datei nicht existiert
	 */
	public Sound(String soundPath) { 
		this.soundPath = soundPath.replace("\\", "/");
		this.soundImage = imageLoader.loadImage(thumbnailFolder + this.getTitle() + ".png");
	}
	
	/**
	 * @return Pfad des Sounds (relativ zum src-Ordner)
	 */
	public String getSoundPath() {
		return this.soundPath;
	}
		
	/**
	 * @return Thumbnail-Bild des Sounds
	 */
	public Image getSoundImage() {
		return soundImage;
	}
	
	/**
	 * Erzeugt AudioClip aus dem Soundpfad
	 * @return Abspielbarer AudioClip
	 */
	public AudioClip getAudioClip() {
		return new AudioClip( Paths.get(this.getSoundPath()).toUri().toString() );
	}
	
	/**
	 * @return Dateiname des Sounds (ohne Endung)
	 */
	public String getTitle() {
		return this.soundPath.substring(soundPath.lastIndexOf("/") + 1, soundPath.lastIndexOf("."));
	}

}