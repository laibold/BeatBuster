package player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.Slider;
import player.elements.Sound;
import player.elements.Track;
import tools.PropertyHandler;

/**
 * Stellt die Schnittstelle zwischen GUI und Player dar. Verwaltet alle Sounds
 * in einer Map, setzt Beats in die Tracks und beinhaltet den Thread, der den
 * Slider bewegt und die Sounds wiedergibt.
 * 
 * @author Becker, Schumacher, Laibold
 * 
 */
public class PlayManager {
	private PropertyHandler propHandler = new PropertyHandler("resources/config.properties");
	private int maxSoundsPerBeat = Integer.valueOf(propHandler.getProperty("maxSoundsPerBeat"));

	private Player player;
	private HashMap<String, Sound> allSounds = new HashMap<>();
	private Track currTrack;
	private Slider progressSlider;
	private Thread loopThread;
	private Thread progressBarThread;
	private boolean loop;
	private final int PROGRESSBAR_SLEEP_MILLIS = 15;

	/**
	 * Neuer PlayManager mit Player und Map aus allen Sounds
	 * 
	 * @param player
	 */
	public PlayManager(Player player) {
		this.player = player;
		this.createMap();
		this.initLoopThread();
		this.initProgressBarThread();
		// ProgressSlider wird ueber Methode hinzugefuegt
	}

	/**
	 * @return true, wenn der loopThread, der den Slider bewegt und die passenden
	 *         Sounds abspielt, laeuft
	 */
	public boolean isLooping() {
		return this.loop;
	}

	/**
	 * Initialisiert loopThread neu
	 */
	public void initLoopThread() {
		this.loopThread = new Thread() {
			public void run() {
				int pos;
				int key = currTrack.getKey();

				while (loop) {
					pos = 0;

					while (loop && (pos) < currTrack.getLength()) {

						if (!currTrack.isEmptyBeat(pos)) {
							for (int i = 0; i < maxSoundsPerBeat; i++) {
								if (currTrack.getBeat(pos).getSound(i) != null) {
									boom(currTrack.getBeat(pos).getSound(i));
								}
							}
						}

						try {
							Thread.sleep(key);
						} catch (InterruptedException e) {
							System.err.println("Fehler beim schlafen.");
						}

						pos++;
					}
				}
			}
		};
	}

	/**
	 * Initialisiert ProgressBarThread neu
	 */
	public void initProgressBarThread() {
		this.progressBarThread = new Thread() {
			public void run() {
				long startTime;

				while (loop) {
					startTime = System.currentTimeMillis();

					while (loop && progressSlider.getValue() < progressSlider.getMax()) {
						long tempTime = System.currentTimeMillis() - startTime;
						Platform.runLater(() -> progressSlider.setValue(tempTime));

						try {
							Thread.sleep(PROGRESSBAR_SLEEP_MILLIS);
						} catch (InterruptedException e) {
							System.err.println("progressBarThread: Fehler beim schlafen.");
						}

					}
					progressSlider.setValue(0);
				}

			}
		};
	}
	
	/**
	 * Startet den Thread, der den Slider bewegt und die Ueberpruefung der Sounds an
	 * den entsprechenden Stellen durchfuehrt
	 */
	public void startStopLooping() {
		if (loop) {
			this.stopLooping();
		} else {
			loop = true;
			initLoopThread();
			loopThread.start();
			initProgressBarThread();
			progressBarThread.start();
		}
	}

	/**
	 * Erstellt eine Map aus allen Sound-Dateien im soundFolder (aus den Properties)
	 */
	private void createMap() {
		File f = new File(propHandler.getProperty("soundFolder"));
		File[] fileArray = f.listFiles();

		for (File file : fileArray) {
			Sound sound = new Sound(file.toString());
			this.allSounds.put(sound.getTitle(), sound);
		}
	}

	/**
	 * Legt den aktuell zu behandelnden Track fest
	 * 
	 * @param track Track, der "geladen" werden soll
	 */
	public void setTrack(Track track) {
		this.currTrack = track;
	}

	/**
	 * @return Aktuell "geladener" Track
	 */
	public Track getTrack() {
		return this.currTrack;
	}

	/**
	 * Gibt angegebenen Sound wieder
	 * 
	 * @param sound Sound, der abgespielt werden soll
	 */
	public void boom(Sound sound) {
		player.play(sound);
	}

	/**
	 * Fuegt Sound an exakt angegeber Stelle hinzu
	 * 
	 * @param index Stelle im Track (welcher Beat)
	 * @param layer Stelle im Beat
	 * @param sound Einzufuegender Sound
	 * @return
	 */
	public boolean addSound(int index, int layer, Sound sound) {
		return currTrack.addSound(index, layer, sound);
	}

	/**
	 * Entferne Sound an angegebener Stelle
	 * 
	 * @param index Stelle im Track (welcher Beat)
	 * @param layer Stelle im Beat
	 * @return
	 */
	public boolean removeSound(int index, int layer) {
		return currTrack.removeSound(index, layer);
	}

	/**
	 * Ruft die Speicher-Methode des aktuellen Tracks auf (speichern im .bb-Format
	 * im Track-Ordner (Properties))
	 */
	public void saveCurrTrack() {
		currTrack.saveTrack();
	}

	/**
	 * Gibt einen Sound aus der Sound-Map zurueck
	 * 
	 * @param name Name des Sounds
	 * @return Sound-Objekt
	 */
	public Sound getSound(String name) {
		return allSounds.get(name);
	}

	/**
	 * Legt den zu bewegenden Slider fest
	 * 
	 * @param slider
	 */
	public void setSlider(Slider slider) {
		this.progressSlider = slider;
	}

	/**
	 * Gibt alle Tracks aus dem Trackordner zurueck
	 * 
	 * @return File-Array mit allen Track-Files
	 */
	public ArrayList<Track> getAllTracks() {
		File f = new File(propHandler.getProperty("trackFolder"));

		ArrayList<Track> trackList = new ArrayList<Track>();
		File[] fileArray = f.listFiles();
		for (File file : fileArray) {
			if (file.toString().endsWith(".bb")) {
				try {
					trackList.add(new Track(file.toString()));
				} catch (IOException e) {
					System.err.println("\n\ngetAllTracks(): Fehler beim Laden von " + file.toString());
				}
			}
		}
		return trackList;
	}

	/**
	 * Unterbricht den Loop-Thread
	 */
	public void stopLooping() {
		loop = false;
		Platform.runLater(() -> progressSlider.setValue(0));
	}
}