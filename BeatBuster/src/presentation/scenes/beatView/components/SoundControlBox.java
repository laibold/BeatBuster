package presentation.scenes.beatView.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * BorderPane mit Elementen, die zur Bedienung des Progress-Bereichs dienen.
 * @author Becker, Schumacher, Laibold
 *
 */
public class SoundControlBox extends BorderPane{
	
	private Button trashBtn = 	new Button();
	private Button playBtn = 	new Button();
	private Button clearBtn =	new Button();
	
	private Slider volumeSlider = new Slider(0,1,1);

	private HBox leftBox = new HBox();	
	private HBox centerBox = new HBox();
	private HBox rightBox = new HBox();

	public SoundControlBox() {	
		this.getStylesheets().add(getClass().getResource("../beatViewStyle.css").toExternalForm());
		
		trashBtn.setMinSize(50, 50);
		trashBtn.getStyleClass().addAll("trashbtn","stylebtn");
		
		playBtn.setMinSize(50, 50);	
		playBtn.getStyleClass().addAll("playbtn","stylebtn");
		
		clearBtn.setMinSize(50, 50);
		clearBtn.getStyleClass().addAll("clearbtn","stylebtn");
		
		HBox.setMargin(playBtn, new Insets(0,10,0,0)); //nach rechts
		
		centerBox.setAlignment(Pos.CENTER);
		
		leftBox.getChildren().add(trashBtn);
		centerBox.getChildren().addAll(playBtn, volumeSlider);
		rightBox.getChildren().add(clearBtn);
		
		this.setLeft(leftBox);
		this.setCenter(centerBox);
		this.setRight(rightBox);
	}
	
	public Button getTrashButton() {
		return this.trashBtn;
	}
	
	public Button getPlayButton() {
		return this.playBtn;
	}
	
	public Slider getVolumeSlider() {
		return this.volumeSlider;
	}

	public Button getClearButton() {
		return this.clearBtn;
	}
	
}