package presentation.scenes.settingsView;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import presentation.application.Main;
import presentation.scenes.beatView.BeatViewController;
import tools.BeatLocation;
import tools.PropertyHandler;

/**
 * Controller, der die SettingsView mit Logik fuellt und ausgeben kann.
 * @author Becker, Schumacher, Laibold
 *
 */
public class SettingsViewController {

	private SettingsView settingsView = new SettingsView();
	private PropertyHandler propHandler = new PropertyHandler("resources/config.properties");
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private Stage newWindow = new Stage();

	public SettingsViewController(Main main, BeatViewController beatViewController) {
		settingsView.getBackButton().addEventHandler(ActionEvent.ACTION, e -> {
			main.switchView("playerpane");
		});

		settingsView.getCursor0().addEventHandler(ActionEvent.ACTION, e -> {
			main.switchCursor("cursor0");
		});

		settingsView.getCursor1().addEventHandler(ActionEvent.ACTION, e -> {
			main.switchCursor("cursor1");
		});

		settingsView.getCursor2().addEventHandler(ActionEvent.ACTION, e -> {
			main.switchCursor("cursor2");
		});

		settingsView.getCursor3().addEventHandler(ActionEvent.ACTION, e -> {
			main.switchCursor("cursor3");
		});

		settingsView.getLocation1Button().addEventHandler(ActionEvent.ACTION, e -> {
			settingsView.getLocation2Button().getStyleClass().remove("chosenLocation");
			settingsView.getLocation3Button().getStyleClass().remove("chosenLocation");
			
			settingsView.getLocation1Button().getStyleClass().add("chosenLocation");
			beatViewController.setLocation(BeatLocation.GARAGE);
		});

		settingsView.getLocation2Button().addEventHandler(ActionEvent.ACTION, e -> {
			settingsView.getLocation1Button().getStyleClass().remove("chosenLocation");
			settingsView.getLocation3Button().getStyleClass().remove("chosenLocation");
			
			settingsView.getLocation2Button().getStyleClass().remove("locationbtnstrandlocked");
			settingsView.getLocation2Button().getStyleClass().addAll("chosenLocation","locationbtnstrand");
			beatViewController.setLocation(BeatLocation.BEACH);
		});
		
		settingsView.getLocation3Button().addEventHandler(ActionEvent.ACTION, e -> {
			settingsView.getLocation1Button().getStyleClass().remove("chosenLocation");
			settingsView.getLocation2Button().getStyleClass().remove("chosenLocation");
			
			settingsView.getLocation3Button().getStyleClass().addAll("chosenLocation");
			beatViewController.setLocation(BeatLocation.GUITAR);
		});
		
		settingsView.getSaveLocationField().setDisable(true);
		this.refreshTextField();
		
		settingsView.getSaveLocation().addEventHandler(ActionEvent.ACTION, e -> {
			File dir = directoryChooser.showDialog(newWindow);
			if (dir != null) {
				propHandler.setProperty("trackFolder", (dir.toString().replace("\\", "/")+"/"));
				this.refreshTextField();
			}
		});	
	}
	
	/**
	 * @return SettingsView mit Logik
	 */
	public SettingsView getView() {
		return settingsView;
	}
	
	/**
	 * Erneuert die Anzeige im TrackFolder-Textfeld
	 */
	public void refreshTextField() {
		String directory = System.getProperty("user.dir");
		String newDir = propHandler.getProperty("trackFolder");
		settingsView.getSaveLocationField().clear();
		if (newDir.startsWith("..")) {
			settingsView.getSaveLocationField().setText(directory.replace("\\", "/") + newDir);
		} else {
			settingsView.getSaveLocationField().setText(newDir);
		}
	}
}
