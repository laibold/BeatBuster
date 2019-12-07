package presentation.scenes.beatView.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import player.PlayManager;
import player.elements.Sound;
import player.elements.Track;
import presentation.application.Main;
import presentation.scenes.beatView.BeatViewPane;
import tools.BeatLocation;
import tools.ImageLoader;
import tools.PropertyHandler;

/**
 * Beinhaltet alle Drag&Drop-Funktionen, die fuer das Verschieben von Sounds
 * ueber die Thumbnail-Buttons notwendig sind. Diese koennen ueber
 * giveDragDropFunctions() einem Button zugewiesen werden
 * 
 * @author Becker, Schumacher, Laibold
 *
 */
public class DragDropFunctions {
	private PropertyHandler propHandler = new PropertyHandler("resources/config.properties");
	private PropertyHandler locationPropHandler;
	private ImageLoader imgLoader = new ImageLoader();
	private static final DataFormat soundFormat = new DataFormat("soundFormat"); // Format fuer Drag&Drop in den Thumbnail-Buttons
	
	private EventHandler<MouseEvent> onDragDetected;
	private EventHandler<DragEvent> onDragOver;
	private EventHandler<DragEvent> onDragDropped;
	private EventHandler<DragEvent> onDragDone;
	
	private EventHandler<DragEvent> deleteOnDragOver;
	private EventHandler<DragEvent> deleteOnDrop;
	private EventHandler<DragEvent> deleteOnExited;
	
	/**
	 * Erzeugt eine neue Instanz von DragDropFunctions, die den Thumbnail-Buttons,
	 * dem Loeschen-Button und der ImageView Drag&Drop-Funktionen zuweisen koennen
	 * 
	 * @param beatViewPane Pane, in der sich die ImageBox und die Maske befinden
	 * @param playMgr PlayManager, der den Player verwaltet
	 * @param main Main
	 */
	public DragDropFunctions(BeatViewPane beatViewPane, PlayManager playMgr, Main main) {
		this.onDragDetected = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getSource() instanceof Button) {

				Button btn = (Button) event.getSource();

				Track track = playMgr.getTrack();
				int index = getIndex(btn);
				int layer = getLayer(btn);
				
				if (track.getBeat(index).getSound(layer) != null) { // Ueberpruefe, ob an dieser Stelle ein Sound ist				
					Sound sound = track.getBeat(index).getSound(layer);
					
					//Drag-Funktionalitaet
					Dragboard dragboard = btn.startDragAndDrop(TransferMode.COPY_OR_MOVE); // Drag&Drop starten - es kann kopiert oder verschoben werden
					
					List<File> fileList = new ArrayList<File>();
					fileList.add(new File(sound.getSoundPath()));
					
					ClipboardContent content = new ClipboardContent(); // zum Kapseln der Daten
					content.put(soundFormat, sound);
					
					dragboard.setContent(content); // Daten ins Dragboard
				}
			

				}
				if (event.getSource() instanceof ImageView) {
					ImageView view = (ImageView) event.getSource();
					double x = event.getX() / beatViewPane.getImageBox().getImageView().getFitWidth() * 100; //wandelt den x-double-Wert in einen prozentualen Wert um
					double y = event.getY() / beatViewPane.getImageBox().getImageView().getFitHeight() * 100; //wandelt den y-double-Wert in einen prozentualen Wert um
					
					int redValue = beatViewPane.getImageBox().getRedValue(x, y);
						if (redValue != 0) {
							
							try {
								Sound sound = playMgr.getSound(locationPropHandler.getProperty(String.valueOf(redValue)));
								Dragboard dragboard = view.startDragAndDrop(TransferMode.MOVE); // Drag&Drop starten - wird verschoben
								ClipboardContent content = new ClipboardContent(); // zum Kapseln der Daten
								content.put(soundFormat, sound);
								dragboard.setContent(content);
								dragboard.setDragViewOffsetX(sound.getSoundImage().getWidth());
								dragboard.setDragViewOffsetY(sound.getSoundImage().getHeight());
								dragboard.setDragView(sound.getSoundImage());
							}catch(NullPointerException ex) {
								System.err.println("Fehler beim Abfragen des Rotwertes " + redValue + ".");
							}
						}
				}
				event.consume();
			}

		};

		this.onDragOver = new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				boolean dropSupported = true;
				boolean copySupported = true;
				Dragboard dragboard;
				
				Set<TransferMode> modes;
				
				dragboard = event.getDragboard();
				
				if (!dragboard.hasContent(soundFormat)) {
					dropSupported = false;
				}
				
				modes = dragboard.getTransferModes(); // COPY, MOVE
				
				for (TransferMode mode : modes) {
					copySupported = copySupported || TransferMode.COPY == mode;
				}
				
				if (copySupported && dropSupported) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}
				
				event.consume();
			}
		};
		
		this.onDragDropped = new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Button btn = (Button) event.getSource();
				Dragboard dragboard = event.getDragboard();
				
				int index = getIndex(btn);
				int layer = getLayer(btn);
				
				Sound droppedSound = (Sound) dragboard.getContent(soundFormat);
				
				if (playMgr.addSound(index, layer, droppedSound)) {
					/* hier muss das Image ausnahmsweise extern geladen werden, da aus dem Sound
					   keine Image-Methode ausgefuehrt werden kann */
					Image img = imgLoader.loadImage(propHandler.getProperty("thumbnailFolder") + droppedSound.getTitle() + ".png");
					beatViewPane.getProgressBox().addThumbnail(index, layer, playMgr.getTrack().getLoopBeats(), img);
				}else {
					playMgr.getTrack().removeSound(index, layer);
					playMgr.addSound(index, layer, droppedSound);
					Image img = imgLoader.loadImage(propHandler.getProperty("thumbnailFolder") + droppedSound.getTitle() + ".png");
					beatViewPane.getProgressBox().addThumbnail(index, layer, playMgr.getTrack().getLoopBeats(), img);
				}
				event.setDropCompleted(true);
				event.consume();
			}
		};

		this.onDragDone = new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Button btn = (Button) event.getSource();
				int index = getIndex(btn);
				int layer = getLayer(btn);
				
				if (event.getTransferMode() == TransferMode.MOVE) { 
					((Button) btn).setGraphic(null);
					playMgr.removeSound(index, layer);
				}
			}
		};

		this.deleteOnDragOver = new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Button btn = (Button) event.getSource();
				btn.getStylesheets().add(getClass().getResource("../beatViewStyle.css").toExternalForm());
				
				// Bild des offenen Muelleimers als css-Klasse hinzufuegen
				if (!btn.getStyleClass().contains("trashbtn_open")) {
					btn.getStyleClass().add("trashbtn_open");
				}
				
				Dragboard dragboard;
				dragboard = event.getDragboard();
				
				if (dragboard.hasContent(soundFormat)) {
					event.acceptTransferModes(TransferMode.MOVE);
				}
				event.consume();
			}
		};
	
		this.deleteOnDrop = new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Button btn = (Button) event.getSource();
				// Bild des offenen Muelleimers wieder entfernen, alte Klasse ist noch zugewiesen
				btn.getStyleClass().remove("trashbtn_open");
				
				event.setDropCompleted(true);
				event.consume();
			}
		};
		
		this.deleteOnExited = new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Button btn = (Button) event.getSource();
				// Bild des offenen Muelleimers wieder entfernen, alte Klasse ist noch zugewiesen
				btn.getStyleClass().remove("trashbtn_open");
			}
		};
	}
	
	/**
	 * Gibt die "Ebene" des Buttons zurueck Ebene = Nummer der HBox in der GUI =
	 * Arrayfeld des entsprechenden Beats
	 * 
	 * @param btn Button, dessen Ebene ermittelt werden soll
	 * @return Nummer der Ebene/des Feldes beginnend mit 0
	 */
	private int getLayer(Button btn) {
		return Integer.valueOf(btn.getParent().getId()); // "Ebene" des Beats herausfinden (=ID der Parent-Node, in Klasse gesetzt)
	}
	
	/**
	 * Gibt den horizontalen Index des Buttons zurueck Index = Nummer des Buttons in
	 * der GUI von links aus = Arrayfeld des entsprechenden Tracks
	 * 
	 * @param btn Button, dessen Index ermittelt werden soll
	 * @return Nummer des Button-Indexes/des Feldes beginnend mit 0
	 */
	private int getIndex(Button btn) {
		ObservableList<Node> list = btn.getParent().getChildrenUnmodifiable(); // Liste mit allen Buttons des Parents
		for (int i=0; i<list.size(); i++) {
			if (list.get(i) == btn) { // Ueberpruefe, welcher Button in der Liste gerade gewaehlt wurde
				return i; // Das ist der Index des Buttons, der gewaehlt wurde und damit auch die Stelle im Track
			}
		}
		return -1;
	}

	/**
	 * Gibt dem mitgegebenen Button die Drag&Drop-Funktionen, damit ueber ihn Sounds
	 * in andere Beats verschoben werden koennen
	 * 
	 * @param btn Button, der die Funktionen erhalten soll
	 */
	public void giveDragDropFunctions(Button btn) {
		btn.setOnDragDetected(this.onDragDetected);
		btn.setOnDragOver(this.onDragOver);
		btn.setOnDragDropped(this.onDragDropped);
		btn.setOnDragDone(this.onDragDone);
	}
	
	/**
	 * Gibt einer ImageView die Funktion, von dort aus Sounds zu draggen
	 * @param view ImageView, die die Funktion bekommen soll
	 */
	public void giveDragFunction(ImageView view, BeatLocation location) {
		this.locationPropHandler = new PropertyHandler(location.getPropertyName());
		view.setOnDragDetected(this.onDragDetected);
	}
	
	/**
	 * Gibt einem Button die Funktion, als "Muelleimer zu dienen" und damit Sounds
	 * aufzunehmen, die dann bei den Quell-Buttons geloescht werden
	 * 
	 * @param btn Button, der die Funktion bekommen soll
	 */
	public void giveDeleteOnDropFunction(Button btn) {
		btn.setOnDragOver(this.deleteOnDragOver);
		btn.setOnDragDropped(this.deleteOnDrop);
		btn.setOnDragExited(this.deleteOnExited);
	}
}
