package presentation.scenes.beatView;

import javafx.geometry.Insets;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import presentation.application.Main;
import presentation.scenes.beatView.components.SoundControlBox;
import presentation.scenes.beatView.components.ContentControlBox;
import presentation.scenes.beatView.components.ImageBox;
import presentation.scenes.beatView.components.ProgressBox;

/**
 * BorderPane, die alle Bestandteile der beatView zusammensetzt.
 * @author Becker, Schumacher, Laibold
 *
 */
public class BeatViewPane extends BorderPane {

	private ContentControlBox contentControlBox;
	private ImageBox imageBox;
	private SoundControlBox soundControlBox;
	private ProgressBox progressBox;
	private VBox contentControlImageVBox;
	private VBox controlProgressVBox;
	
	private GaussianBlur blurEffect = new GaussianBlur(15);
	
	/**
	 * Erzeugt neue BeatView
	 * @param main Main, die den Controller der Pane erzeugt
	 */
	public BeatViewPane(Main main) {
		contentControlBox = new ContentControlBox();
		imageBox = new ImageBox(main);
		soundControlBox = new SoundControlBox();
		progressBox = new ProgressBox();
		controlProgressVBox = new VBox();
		contentControlImageVBox = new VBox();
		
		this.getStylesheets().add(getClass().getResource("beatViewStyle.css").toExternalForm());

		//top, right, bottom, left
		VBox.setMargin(imageBox, new Insets(15, 0, 15, 0));
		contentControlImageVBox.getChildren().addAll(contentControlBox, imageBox);
		
		controlProgressVBox.getChildren().addAll(soundControlBox, progressBox);
		controlProgressVBox.setPadding(new Insets(20, 50, 20, 50));
		
		VBox.setMargin(soundControlBox, new Insets(0, 0, 10, 0));
		
		controlProgressVBox.getStyleClass().addAll("controlBox","blurUp");
		contentControlBox.getStyleClass().addAll("controlBox","blurDown");
		this.getStyleClass().add("backgroundPane");
		
		this.setTop(contentControlImageVBox);
		this.setBottom(controlProgressVBox);
	}
	
	/**
	 * Schaltet den Weichzeichner-Effekt auf der Pane um (an/aus)
	 */
	public void switchEffect() {
		if (this.getEffect() instanceof GaussianBlur) {
			this.setEffect(null);
		} else {
			this.setEffect(blurEffect);
		}
	}
	
	/**
	 * @return ImageBox, in der sich das BeatLocation-Bild befindet
	 */
	public ImageBox getImageBox() {
		return this.imageBox;
	}
	
	/**
	 * @return SoundControlBox mit allen Elementen
	 */
	public SoundControlBox getSoundControlBox() {
		return this.soundControlBox;
	}
	
	/**
	 * @return ProgressBox mit ProgressSlider und den Thumbnail-Buttons
	 */
	public ProgressBox getProgressBox() {
		return this.progressBox;
	}

	/**
	 * @return Gesamte Hoehe der Pane
	 */
	public double getCurrentHeight() {
		return 
				contentControlBox.getHeight()
				+ VBox.getMargin(imageBox).getTop()
				+ imageBox.heightProperty().doubleValue() 
				+ VBox.getMargin(imageBox).getBottom()
				+ controlProgressVBox.getHeight() + 45;
	}

	/**
	 * @return ContentControlBox mit allen Elementen
	 */
	public ContentControlBox getContentControlBox() {
		return contentControlBox;
	}
	
}