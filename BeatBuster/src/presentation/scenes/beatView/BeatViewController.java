package presentation.scenes.beatView;

import java.io.IOException;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import player.PlayManager;
import player.Player;
import player.elements.Beat;
import player.elements.Sound;
import player.elements.Track;
import presentation.application.Main;
import presentation.scenes.beatView.components.DragDropFunctions;
import tools.BeatLocation;
import tools.PropertyHandler;

/**
 * Controller, der die Elemente der BeatViewPane (bestehend aus ImageBox,
 * SoundControlBox und ProgressBox) mit Funktionen fuellt.
 * 
 * @param player Musikplayer
 * @param main   Main
 */
public class BeatViewController {
	private PropertyHandler propHandler = new PropertyHandler("resources/config.properties");
	private PropertyHandler soundPropHandler;
	private String trackFolder = propHandler.getProperty("trackFolder");
	
	private Main main;
	private BeatViewPane beatViewPane;
	private PlayManager playMgr;
	private boolean textFieldShown = false;
	private DragDropFunctions dragDropFunctions;

	public BeatViewController(Player player, Main main) {
		this.main = main;
		this.beatViewPane = new BeatViewPane(main);
		this.playMgr = new PlayManager(player);
		playMgr.setSlider(beatViewPane.getProgressBox().getSlider());

		this.dragDropFunctions = new DragDropFunctions(beatViewPane, playMgr, main);
		
		this.setLocation(BeatLocation.GARAGE);
		
		//Standard-Track setzen
		int defaultBPM = Integer.valueOf(propHandler.getProperty("defaultBPM"));
		int defaultBars = Integer.valueOf(propHandler.getProperty("defaultBars"));
		this.setTrack(new Track(defaultBPM, defaultBars));

		//Neuer Track-Button
		beatViewPane.getContentControlBox().getNewTrackButton().addEventHandler(ActionEvent.ACTION, e -> {
			playMgr.stopLooping();
			switchPlayButtonState();
			
			beatViewPane.switchEffect(); //Blur-Effekt auf Pane
			
			//Dialog oeffnen, der Werte fuer neuen Track zurueckgibt
			Optional<Integer[]> trackValues = openTrackDialog();
			
			if (trackValues != null) {
				this.setTrack(new Track(trackValues.get()[0], trackValues.get()[1]));
			}
			
			beatViewPane.switchEffect();
		});
		
		//Funktion fuer Speicher-Button
		beatViewPane.getContentControlBox().getSaveButton().addEventHandler(ActionEvent.ACTION, e -> {
			if (!textFieldShown) {
				String name = playMgr.getTrack().getName();
				beatViewPane.getContentControlBox().getSaveTextField().setVisible(true);
				beatViewPane.getContentControlBox().getSaveTextField().setText(name);
				textFieldShown = !textFieldShown;
			} else {
				playMgr.getTrack().setName(beatViewPane.getContentControlBox().getSaveTextField().getText());
				playMgr.saveCurrTrack();
				beatViewPane.getContentControlBox().getSaveTextField().setVisible(false);
				textFieldShown = !textFieldShown;
			}
		});

		// Laden-Button fuellen
		beatViewPane.getContentControlBox().getLoadButton().addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
			this.trackFolder = propHandler.getProperty("trackFolder");
			beatViewPane.getContentControlBox().getLoadButton().getItems().clear();
			for (Track track : playMgr.getAllTracks()) {
				String itemName = track.getName();
				MenuItem currItem = new MenuItem(itemName.substring(itemName.lastIndexOf("/") + 1));
				beatViewPane.getContentControlBox().getLoadButton().getItems().add(currItem);
				currItem.setOnAction(event -> {
					try {
						Track trackToLoad = new Track(trackFolder + currItem.getText() + ".bb");
						this.setTrack(trackToLoad);

						for (int i = 0; i < trackToLoad.getLength(); i++) {
							Beat currBeat = playMgr.getTrack().getBeat(i);
							for (int j = 0; j < currBeat.getLength(); j++) {
								Sound currSound = currBeat.getSound(j);
								if (currSound != null) {
									beatViewPane.getProgressBox().addThumbnail(i, j, playMgr.getTrack().getLoopBeats(),
											currSound.getSoundImage()); // Thumbnail hinzufuegen
								}
							}
						}
					} catch (IOException e1) {
						System.err.println(
								"IOException: Fehler beim Laden des Tracks " + trackFolder + currItem.getText() + ".bb");
					}
				});
			}
		});

		// Wechsel zu Einstellungen
		beatViewPane.getContentControlBox().getSettingsButton().addEventHandler(ActionEvent.ACTION, e -> {
			main.switchView("settingspane");
		});
		
		// Abfrage des Rot-Werts der Maske beim Klicken auf das Bild und entsprechendes Hinzufuegen der Sounds und Thumbnails
		beatViewPane.getImageBox().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			// wandelt den x-double-Wert in einen prozentualen Wert um
			double x = e.getX() / beatViewPane.getImageBox().getImageView().getFitWidth() * 100; 
			// wandelt den y-double-Wert in einen prozentualen Wert um
			double y = e.getY() / beatViewPane.getImageBox().getImageView().getFitHeight() * 100;

			int redValue = beatViewPane.getImageBox().getRedValue(x, y);
			if (redValue != 0) {
				try {
					Sound newSound = playMgr.getSound(soundPropHandler.getProperty(String.valueOf(redValue)));
					playMgr.boom(newSound);
				} catch (NullPointerException ex) {
					System.err.println("Fehler beim Abfragen des Rotwertes " + redValue + ".");
				}
			}
		});
		
		// Dem Muelleimer-Button die Loesch-Funktion geben
		dragDropFunctions.giveDeleteOnDropFunction(beatViewPane.getSoundControlBox().getTrashButton());

		// Play/Pause-Button
		beatViewPane.getSoundControlBox().getPlayButton().addEventHandler(ActionEvent.ACTION, e -> {
			playMgr.startStopLooping();
			this.switchPlayButtonState();
		});

		// Listener auf den Lautstaerkeregler
		beatViewPane.getSoundControlBox().getVolumeSlider().valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number newValue) {
				player.setVolume(newValue.floatValue());
			}
		});

		// Action fuer den Clear-Button
		beatViewPane.getSoundControlBox().getClearButton().addEventHandler(ActionEvent.ACTION, e -> {
			playMgr.stopLooping();
			switchPlayButtonState();
			playMgr.getTrack().clear();
			beatViewPane.getProgressBox().resetThumbnailGraphics();
		});		
		
		// Buttons fuer Thumbnails hinzufuegen - LoopBeats entsprechen der Anzahl der Buttons
		beatViewPane.getProgressBox().createThumbnailButtons(playMgr.getTrack().getLoopBeats());
		this.giveDragDropFunctions();
	}

	/**
	 * Oeffnet den Dialog, in dem man fuer den neuen Track BPM und Anzahl der Takte
	 * einstellen kann
	 * 
	 * @return Integer-Array mit BPM in [0] und Anzahl Takte in [1]
	 */
	private Optional<Integer[]> openTrackDialog() {
		if (playMgr.isLooping()) {
			playMgr.stopLooping();
		}

		int defaultBPM = Integer.valueOf(propHandler.getProperty("defaultBPM"));
		int defaultBars = Integer.valueOf(propHandler.getProperty("defaultBars"));
		
		Dialog<Integer[]> dialog = new Dialog<>();
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setTitle("Neuer Track");
		dialog.setHeaderText("Voreinstellungen des Tracks:");
		dialog.setResizable(false);

		Label label1 = new Label("BPM: ");
		Label label2 = new Label("Takte: ");

		Spinner<Integer> spinner = new Spinner<Integer>();
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(80, 140,defaultBPM ,10); // min, max, init. step
		spinner.setValueFactory(valueFactory);
		spinner.setPrefWidth(80);

		ObservableList<Integer> options = FXCollections.observableArrayList(1, 2, 3,4);
		ComboBox<Integer> comboBox = new ComboBox<Integer>(options);
		comboBox.setValue(defaultBars);
		comboBox.setPrefWidth(80);

		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(spinner, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(comboBox, 2, 2);

		grid.setVgap(10);
		grid.setHgap(10);
		dialog.getDialogPane().setContent(grid);

		ButtonType cancelButton = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType okButton = new ButtonType("Erstellen", ButtonData.OK_DONE);

		dialog.getDialogPane().getButtonTypes().addAll(cancelButton, okButton);

		dialog.setResultConverter(new Callback<ButtonType, Integer[]>() {
			@Override
			public Integer[] call(ButtonType b) {
				if (b == okButton) {
					Integer[] retVal = { spinner.getValue(), comboBox.getValue() };
					return retVal;
				}
				return null;
			}
		});

		Optional<Integer[]> result = dialog.showAndWait();

		if (result.isPresent()) {
			return result;
		}

		return null;
	}

	/**
	 * Laedt Track in den playManager und aendert die Eigenschaften in der Pane
	 * 
	 * @param track zu ladender Track
	 */
	private void setTrack(Track track) {
		playMgr.stopLooping();
		this.switchPlayButtonState();
		
		// Wenn schon ein Track gesetzt wurde 
		if (playMgr.getTrack() != null) {
			if (playMgr.getTrack().getBpm() != track.getBpm() || playMgr.getTrack().getBars() != track.getBars()) {
				//wenn die BPM oder Bars des neuen Tracks nicht mit den alten uebereinstimmen, alle Thumbnail-Buttons loeschen und neu setzen
				beatViewPane.getProgressBox().deleteThumbnailButtons();
				beatViewPane.getProgressBox().createThumbnailButtons(track.getLoopBeats());
			} else {
				// sonst die Thumbnails nur reseten
				beatViewPane.getProgressBox().resetThumbnailGraphics();
			}
		}
		
		playMgr.setTrack(track);
		beatViewPane.getProgressBox().setKey(playMgr.getTrack().getKey());
		beatViewPane.getProgressBox().getSlider().setMax(track.getMillis());
		
		this.giveDragDropFunctions();
	}
	
	/**
	 * Passt die Oberflaeche des Play/Pause-Buttons entsprechend dem Zustand des
	 * PlayManagers (isLooping) an
	 */
	private void switchPlayButtonState() {
		if (playMgr.isLooping()) {
			beatViewPane.getSoundControlBox().getPlayButton().getStyleClass().remove("playbtn");
			beatViewPane.getSoundControlBox().getPlayButton().getStyleClass().add("pausebtn");
		} else {
			beatViewPane.getSoundControlBox().getPlayButton().getStyleClass().remove("pausebtn");
			beatViewPane.getSoundControlBox().getPlayButton().getStyleClass().add("playbtn");
		}
	}
	
	/**
	 * Gibt allen Thumbnail-Buttons in der ProgressBox der BeatViewPane die
	 * Drag&Drop-Funktionen
	 */
	private void giveDragDropFunctions() {
		DragDropFunctions dragDropFunctions = new DragDropFunctions(beatViewPane, playMgr, this.main);
		for (HBox pane : beatViewPane.getProgressBox().getThumbBoxes()) {
			for (Node btn : pane.getChildren()) {
				dragDropFunctions.giveDragDropFunctions((Button) btn);
			}
		}
	}

	/**
	 * Aendert die Location und passt die zugehoerigen Sounds an
	 * @param location Wert aus dem Enum BeatLocation
	 */
	public void setLocation(BeatLocation location) {
		this.soundPropHandler = new PropertyHandler(location.getPropertyName());
		beatViewPane.getImageBox().setLocation(location);
		dragDropFunctions.giveDragFunction((ImageView) beatViewPane.getImageBox().getImageView(), location);
	}
	
	/**
	 * 
	 * @return BeatView mit Logik
	 */
	public BeatViewPane getView() {
		return beatViewPane;
	}

}