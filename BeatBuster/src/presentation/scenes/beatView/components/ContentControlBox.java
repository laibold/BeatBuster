package presentation.scenes.beatView.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * BorderPane, die alle Elemente beinhaltet, die Tracks erstellen, speichern und
 * laden sowie der Button, der zu den Einstellungen fuehrt
 * 
 * @author Becker, Schumacher, Laibold
 *
 */
public class ContentControlBox extends BorderPane{
	private Button saveBtn = 			new Button();
	private TextField saveTextField = 	new TextField ();
	private MenuButton loadBtn = 		new MenuButton();
	private Button settingsBtn = 		new Button();
	
	private Button newTrackBtn = 		new Button();
	
	private HBox leftBox = 	new HBox();
	private HBox rightBox = new HBox();
	
	public ContentControlBox() {
		this.getStylesheets().add(getClass().getResource("../beatViewStyle.css").toExternalForm());

		newTrackBtn.setMinSize(50, 50);
		newTrackBtn.getStyleClass().addAll("neuersongbtn","stylebtn");

		saveTextField.setVisible(false);

		saveBtn.setMinSize(50,50);
		saveBtn.getStyleClass().addAll("savebtn","stylebtn");
		
		settingsBtn.setMinSize(50,50);
		settingsBtn.getStyleClass().addAll("settingsbtn","stylebtn");
		
		//top, right, bottom, left
		leftBox.setPadding(new Insets(10, 0, 10, 50));
		rightBox.setPadding(new Insets(10, 50, 10, 0));
		
		HBox.setMargin(newTrackBtn, 	new Insets(0,10,0,0)); 
		HBox.setMargin(saveTextField,	new Insets(0,10,0,0));
		HBox.setMargin(saveBtn, 		new Insets(0,10,0,0));
		HBox.setMargin(loadBtn, 		new Insets(0,10,0,0));
		
		loadBtn.setMinSize(50, 50);
		loadBtn.getStyleClass().addAll("loadbtn","stylebtn");
		
		rightBox.setAlignment(Pos.CENTER);
		
		leftBox.getChildren().addAll(newTrackBtn);
		rightBox.getChildren().addAll(saveTextField, saveBtn, loadBtn, settingsBtn);

		this.setLeft(leftBox);
		this.setRight(rightBox);
	}
	
	/**
	 * @return Button, der einen neuen Track erzeugt
	 */
	public Button getNewTrackButton() {
		return this.newTrackBtn;
	}
	
	/**
	 * @return Button, der einen Track speichert
	 */
	public Button getSaveButton() {
		return this.saveBtn;
	}
	
	/**
	 * @return Button, der zu den Eistellungen fuehrt
	 */
	public Button getSettingsButton() {
		return this.settingsBtn;
	}
	
	/**
	 * @return MenuButton, der einen Track laedt
	 */
	public MenuButton getLoadButton() {
		return this.loadBtn;
	}
	
	/**
	 * @return Textfeld zur Eingabe eines Names zum Speichern eines Tracks
	 */
	public TextField getSaveTextField() {
		return this.saveTextField;
	}
	
}
