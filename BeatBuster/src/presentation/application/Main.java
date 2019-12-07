package presentation.application;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import player.Player;
import presentation.scenes.beatView.BeatViewController;
import presentation.scenes.beatView.BeatViewPane;
import presentation.scenes.settingsView.SettingsViewController;
import tools.ImageLoader;
import tools.PropertyHandler;

/**
 * 
 * @author Becker, Schumacher, Laibold
 *
 */
public class Main extends Application {
	private Scene scene;
	private BeatViewController beatViewController;
	private SettingsViewController settingsViewController;
	private Stage primaryStage;
	private Player player; 	//Eigentlicher Musik-Player
	private ImageLoader imgLoader = new ImageLoader();
	
	private PropertyHandler propertyHandler = new PropertyHandler("resources/config.properties");
	private ImageLoader imageLoader = new ImageLoader();
	private String cursorFolder = propertyHandler.getProperty("cursorFolder");
	private String imgFolder = propertyHandler.getProperty("imgFolder");

	private Image cursorImage1 = imageLoader.loadImage(cursorFolder + "cursor.png");
	private ImageCursor cursor1 = new ImageCursor(cursorImage1, 30, 30);

	private Image cursorImage2 = imageLoader.loadImage(cursorFolder + "cursor2.png");
	private ImageCursor cursor2 = new ImageCursor(cursorImage2, 30, 30);

	private Image cursorImage3 = imageLoader.loadImage(cursorFolder + "cursor3.png");
	private ImageCursor cursor3 = new ImageCursor(cursorImage3, 30, 30);

	@Override
	public void start(Stage primaryStage) {
		////////////////////
		Dimension screenSize = this.getScreenSize();
		double startWidth = (int) (screenSize.getWidth() * 0.47);
		double startHeight = (int) (screenSize.getHeight() * 0.7);
		
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int maxHeight = winSize.height;
		////////////////////
		
		//Testen, ob der trackFolder in Properties existiert und falls nicht, auf Standard setzen
		Path path = Paths.get(propertyHandler.getProperty("trackFolder"));
		if (!Files.exists(path)) {
			String defaultTrackFolder = propertyHandler.getProperty("defaultTrackFolder");
			propertyHandler.setProperty("trackFolder", defaultTrackFolder);
		}
		
		this.primaryStage = primaryStage;
		this.player = new Player();
		this.beatViewController = new BeatViewController(player, this);	//Controller fuer die Beat-Ansicht
		this.settingsViewController = new SettingsViewController(this,beatViewController);	//Controller fuer die Einstellungs-Ansicht
		
		BeatViewPane beatViewPane = beatViewController.getView(); 	//Player-Ansicht
		this.scene = new Scene(beatViewPane, startWidth, startHeight);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("BeatBuster");
		primaryStage.getIcons().add(imgLoader.loadImage(imgFolder + "icon.png"));
		primaryStage.setMinWidth(674);
		primaryStage.setMaxHeight(maxHeight);
		
		//Minimale Hoehe wird variabel so gesetzt, dass nichts ueberdeckt wird
		((BeatViewPane) scene.getRoot()).heightProperty().addListener((obs, oldVal, newVal) -> {
			double newMinHeight = ((BeatViewPane) scene.getRoot()).getCurrentHeight();
			
			if (newMinHeight < primaryStage.getMaxHeight()) {
				primaryStage.setMinHeight(newMinHeight);
			}
		});
		
		//Minimale Hoehe muss auch von der Breite abhaengig gemacht werden, da dadurch die Bildgroesse geaendert wird
		((BeatViewPane) scene.getRoot()).widthProperty().addListener((obs, oldVal, newVal) -> {
			double newMinHeight = ((BeatViewPane) scene.getRoot()).getCurrentHeight();
			
			if (newMinHeight < primaryStage.getMaxHeight()) {
				primaryStage.setMinHeight(newMinHeight);
			}
		});
		
		primaryStage.show();
		
		//Beim Schliessen des Fensters wird das gesamte Programm beendet
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
    
	/**
	 * Wechseln der View
	 * @param viewName Name der View (playerpane oder settingspane)
	 */
	public void switchView(String viewName) {
		switch (viewName) {
			case "playerpane":
				scene.setRoot(beatViewController.getView());
				break;
			case "settingspane":
				scene.setRoot(settingsViewController.getView());
				break;
		}
	}
	
	/**
	 * Wechseln des Cursors fuer alle Views
	 * @param cursorName Name des Cursors (cursor0, cursor1, cursor2, cursor3)
	 */
	public void switchCursor(String cursorName) {
		switch (cursorName) {
		case "cursor0":
			scene.setCursor(Cursor.DEFAULT);
			break;
		case "cursor1":
			scene.setCursor(cursor1);
			break;
		case "cursor2":
			scene.setCursor(cursor2);
			break;
		case "cursor3":
			scene.setCursor(cursor3);
			break;
		}
	}
	
	/**
	 * 
	 * @return Breite des Fensters als Property
	 */
	public ReadOnlyDoubleProperty getStageW() {
		return primaryStage.widthProperty();
	}
	
	/**
	 * 
	 * @return Hoehe des Fensters als Property
	 */
	public ReadOnlyDoubleProperty getStageH() {
		return primaryStage.heightProperty();
	}

	/**
	 * 
	 * @return primaryStage
	 */
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}
	
	/**
	 * @return Bildschirmaufloesung als Dimension
	 */
	public Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
}
