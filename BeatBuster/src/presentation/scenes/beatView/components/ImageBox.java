package presentation.scenes.beatView.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.StackPane;
import presentation.application.Main;
import tools.BeatLocation;
import tools.ImageLoader;
import tools.PropertyHandler;

/**
 * StackPane, die das aktuelle Bild und die dazugehoerigen Buttons beinhaltet
 * 
 * @author Becker, Schumacher, Laibold
 *
 */
public class ImageBox extends StackPane {
	private PropertyHandler propHandler = new PropertyHandler("resources/config.properties");
	private String sceneFolder = propHandler.getProperty("sceneFolder");
	private ImageLoader imgLoader = new ImageLoader();
	
	private ImageView bgImageView;
	private Image maskImage;
	private BeatLocation beatLocation;
	
	/**
	 * Neue ImageBox
	 * @param main Main, die Breite und Hoehe der Stage weitergeben kann
	 */
	public ImageBox(Main main) {
		this.getStylesheets().add(getClass().getResource("../beatViewStyle.css").toExternalForm());
		
		this.beatLocation = BeatLocation.GARAGE;
		bgImageView = new ImageView(imgLoader.loadImage(sceneFolder + beatLocation.getImageName()));
		maskImage = imgLoader.loadImage(sceneFolder + beatLocation.getMaskName());
		
		double maxWidth = main.getScreenSize().getWidth() * 0.52; 
		bgImageView.maxWidth(maxWidth);
		
		main.getStageW().addListener((obs, oldVal, newVal) -> {
			if ((newVal.doubleValue() - 118) < maxWidth) {
				bgImageView.setFitWidth(newVal.doubleValue() - 118);
				bgImageView.setFitHeight(newVal.doubleValue() / 1.58 - 118);
			}
		});
		
		//Listener auf Maximier-Property
		main.getPrimaryStage().maximizedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) { //wenn maximiert
				bgImageView.setFitWidth(maxWidth);
				bgImageView.setFitHeight(maxWidth / 1.58);
			}
		});

		bgImageView.setLayoutY(0);
		bgImageView.getStyleClass().add("blurAround");
		
		this.getChildren().add(bgImageView);
	}

	/**
	 * Gibt den Rot-Wert an der prozentual ausgewaehlten Stelle zuueck
	 * @param x  x-Wert in Prozent 0-100
	 * @param y  y-Wert in Prozent 0-100
	 * @return Rot-Wert zwischen 0 und 255
	 */
	public int getRedValue(double x, double y) {
		int red;
		int xOut;
		int yOut;

		PixelReader reader = maskImage.getPixelReader();
		yOut = (int) ((maskImage.getHeight()) / 100 * y);
		xOut = (int) ((maskImage.getWidth()) / 100 * x);

		red = reader.getArgb(xOut, yOut);
		red = (red >> 16) & 0xFF;
		return red;
	}
	
	/**
	 * @return ImageView, die das Loaction-Bild beinhaltet
	 */
	public ImageView getImageView() {
		return this.bgImageView;
	}
	
	/**
	 * Aendert die Location, setzt Bild und Maske, um mit den neuen Sounds der Location zu arbeiten
	 * @param location Enum-Wert der neuen Location
	 */
	public void setLocation(BeatLocation location) {
		this.beatLocation = location;
		bgImageView.setImage(imgLoader.loadImage(sceneFolder + beatLocation.getImageName()));
		maskImage = imgLoader.loadImage(sceneFolder + beatLocation.getMaskName());
	}
	
}