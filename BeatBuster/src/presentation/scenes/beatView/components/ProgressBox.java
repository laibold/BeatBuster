package presentation.scenes.beatView.components;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Vbox, die die ProgressBar und die Thumbnails der Sounds beinhaltet
 * @author Becker, Schumacher, Laibold
 *
 */
public class ProgressBox extends VBox {
	private Slider progressSlider;
	private HBox thumbBox0 = new HBox();
	private HBox thumbBox1 = new HBox();
	private HBox thumbBox2 = new HBox();
	private ArrayList<HBox> thumbBoxes = new ArrayList<HBox>();
	
	public ProgressBox() {
		this.getStylesheets().add(getClass().getResource("../beatViewStyle.css").toExternalForm());
		
		this.progressSlider = new Slider();
		progressSlider.setBlockIncrement(1);
		progressSlider.setShowTickMarks(true);
		progressSlider.setMinorTickCount(0);
		progressSlider.setSnapToTicks(true);
		
		//IDs werden verwendet, damit die enthaltenen Buttons ihre Parents identifizieren koennen
		thumbBox0.setId("0");
		thumbBox1.setId("1");
		thumbBox2.setId("2");
		
		thumbBoxes.add(thumbBox0);
		thumbBoxes.add(thumbBox1);
		thumbBoxes.add(thumbBox2);
		
		this.getChildren().addAll(progressSlider, thumbBox0, thumbBox1, thumbBox2);
	}
	
	/**
	 * Der Key legt fest, wie gross die Abstaende zwischen den Beats sein sollen
	 * @param key Millisekunden zwischen den Beats
	 */
	public void setKey(int key) {
		this.progressSlider.setMajorTickUnit(key);
	}
	
	/**
	 * @return Fortschritts-Slider
	 */
	public Slider getSlider() {
		return this.progressSlider;
	}
	
	/**
	 * Alle HBoxen, die die Thumbnail-Buttons enthalten
	 * @return Hboxen in ArrayList
	 */
	public ArrayList<HBox> getThumbBoxes() {
		return this.thumbBoxes;
	}
	
	/**
	 * Buttons fuer die Thumbnails erstellen. Anzahl richtet sich nach der Anzahl der Schlaege im Track (=LoopBeats).
	 * Bei jedem neuen Track muss diese Methode neu aufgerufen werden.
	 * @param loopBeats loopBeats aus dem aktuellen Track
	 */
	public void createThumbnailButtons(int loopBeats) {
		for(HBox box : thumbBoxes) {
			VBox.setMargin(box, new Insets(0, 0, 2, 0));
			
			for (int i = 0; i < loopBeats; i++) {
				Button btn = new Button();
				btn.minWidthProperty().bind(progressSlider.widthProperty().divide(loopBeats * 1.015));
				btn.maxWidthProperty().bind(progressSlider.widthProperty().divide(loopBeats * 1.015));
				
				btn.setMaxHeight(20);
				btn.setMinHeight(20);
				btn.getStyleClass().add("thumbnailBtn");
				btn.setAlignment(Pos.CENTER_LEFT);
				
				box.getChildren().add(btn);
		    }
		}
	}
	
	/**
	 * Fuegt einen Thumbnail fuer den Sound unter die Progressbar
	 * @param index "Horizontale Stelle" unterhalb des Sliders
	 * @param layer "Vertikale Ebene", also erste freie Stelle im Beat 
	 * @param loopBeats Anzahl der Achtel-Schlaege im Track
	 * @param soundName Name des Sounds, der hinzugefuegt werden soll
	 */
	public void addThumbnail(int index, int layer, int loopBeats, Image soundImage) {
		ImageView imageView = new ImageView(soundImage);
		
		imageView.setFitWidth(20);
		imageView.setFitHeight(20);
		
		Button button = null;
		switch (layer) {
		case 0:
			button = (Button) thumbBox0.getChildren().get(index);
			break;
		case 1:
			button = (Button) thumbBox1.getChildren().get(index);
			break;
		case 2:
			button = (Button) thumbBox2.getChildren().get(index);
		}

		button.setGraphic(imageView);
	}
	
	/**
	 * Entfernt alle Thumbnail-Buttons aus den Boxen
	 */
	public void deleteThumbnailButtons() {
		for (HBox box : this.thumbBoxes) {
			box.getChildren().clear();
		}
	}
	
	/**
	 * Entfernt alle Thumbnails von den Thumbnail-Buttons
	 */
	public void resetThumbnailGraphics() {
		for (HBox box : thumbBoxes) {
			for (Node btn : box.getChildren()) {
				((Button) btn).setGraphic(null);
			}
		}
	}
 	
}