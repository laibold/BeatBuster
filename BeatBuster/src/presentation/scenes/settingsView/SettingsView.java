package presentation.scenes.settingsView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * FlowPane fuer die Einstellungen
 * @author Becker, Schumacher, Laibold
 *
 */
public class SettingsView extends VBox {

	private Button backBtn = new Button();
	
	private ToggleGroup cursorGroup = new ToggleGroup();
	private ToggleButton cursor0 = new ToggleButton("Standard-Cursor");
	private ToggleButton cursor1 = new ToggleButton("Pr\u00E4zisions-C\u00F6rsor");
	private ToggleButton cursor2 = new ToggleButton("Sci-Fi-Cursor");
	private ToggleButton cursor3 = new ToggleButton("Musikalischer Cursor");
	
	private ToggleGroup locationgroup = new ToggleGroup();
	private ToggleButton location1Button = new ToggleButton();
	private ToggleButton location2Button = new ToggleButton();
	private ToggleButton location3Button = new ToggleButton();
	
	private Label saveLovationLabel = new Label("Speicherort: ");
	private TextField saveLocationField = new TextField();
	private Button saveLocationButton = new Button("\u00C4ndern");
	
	private HBox backBox = new HBox();
	private HBox locationbox = new HBox();
	private HBox cursorBox = new HBox();
	private HBox saveBox = new HBox();

	private VBox settingsBox = new VBox();
	
	private final int spacing = 20;
	
	public SettingsView() {
		this.getStylesheets().add(getClass().getResource("settingsViewStyle.css").toExternalForm());
		
		settingsBox.setSpacing(spacing);
		cursorBox.setSpacing(spacing);
		locationbox.setSpacing(spacing);
		saveBox.setSpacing(spacing);
	    backBox.setPadding(new Insets(10, 0, 10, 50));
		
	    backBox.getStyleClass().addAll("controlBox","blurDown");
	    
		backBox.getChildren().add(backBtn);
		
		backBtn.getStyleClass().addAll("backbtn","stylebtn");
		backBtn.setMinSize(50, 50);
		
		cursor0.setToggleGroup(cursorGroup);
		cursor0.setSelected(true);
		cursor1.setToggleGroup(cursorGroup);
		cursor2.setToggleGroup(cursorGroup);
		cursor3.setToggleGroup(cursorGroup);

		cursorBox.getChildren().addAll(cursor0, cursor1, cursor2, cursor3);
		cursorBox.setAlignment(Pos.CENTER_LEFT);

		location1Button.setToggleGroup(locationgroup);
		location1Button.setSelected(true);
		location1Button.getStyleClass().addAll("locationbtngarage","stylebtn","chosenLocation");
		location1Button.setMinSize(200, 125);

		location2Button.setToggleGroup(locationgroup);
		location2Button.getStyleClass().addAll("locationbtnstrandlocked","stylebtn");
		location2Button.setMinSize(200, 125);
		
		location3Button.setToggleGroup(locationgroup);
		location3Button.getStyleClass().addAll("locationbtngitarre","stylebtn");
		location3Button.setMinSize(200, 125);

		locationbox.setAlignment(Pos.CENTER_LEFT);
		locationbox.getChildren().addAll(location1Button, location2Button, location3Button);

		saveLocationField.setPrefWidth(317);
		saveLovationLabel.setTextAlignment(TextAlignment.CENTER);
		saveBox.setAlignment(Pos.CENTER_LEFT);
		saveBox.getChildren().addAll(saveLovationLabel, saveLocationField, saveLocationButton);

		VBox.setMargin(settingsBox, new Insets(30,0,0,50));
		settingsBox.getChildren().addAll(cursorBox, locationbox, saveBox);
		settingsBox.setAlignment(Pos.TOP_LEFT);

		this.getStyleClass().add("whiteBackground");
		this.getChildren().addAll(backBox,settingsBox);
		this.setAlignment(Pos.TOP_CENTER);
	}

	public ToggleButton getCursor0() {
		return cursor0;
	}

	public ToggleButton getCursor1() {
		return cursor1;
	}

	public ToggleButton getCursor2() {
		return cursor2;
	}

	public ToggleButton getCursor3() {
		return cursor3;
	}
	
	public Button getBackButton() {
		return this.backBtn;
	}
	
	public ToggleButton getLocation1Button() {
		return this.location1Button;
	}
	
	public ToggleButton getLocation2Button() {
		return this.location2Button;
	}
	
	public ToggleButton getLocation3Button() {
		return this.location3Button;
	}
	
	public TextField getSaveLocationField() {
		return this.saveLocationField;
	}

	public Button getSaveLocation() {
		return this.saveLocationButton;
	}
}
