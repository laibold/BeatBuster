package player.elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import tools.PropertyHandler;

/**
 * Ein Track beinhaltet Beats, die die einzelnen Sounds beinhalten. Jeder Track
 * benoetigt die Angabe von Beats per Minute (bpm) und Anzahl der Takte (bars).
 * Es wird immer von 4/4-Takten ausgegangen.
 * 
 * @author Becker, Schumacher, Laibold
 *
 */
public class Track {
	private PropertyHandler propHandler = new PropertyHandler("resources/config.properties");
	private Beat[] beats;
	private String name;
	private int bpm;
	private int bars;
	
	/**
	 * Erzeugt einen neuen Track
	 * @param bpm Beats per Minute des Tracks
	 * @param bars Anzahl 4/4-Takte, die der Track haben soll
	 */
	public Track(int bpm, int bars) {
		this.bpm = bpm;
		this.bars = bars;
		this.beats = new Beat[this.getLoopBeats()];
		
		//Standard-Name
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd_hh-mm-ss");
	    this.name = ft.format(new Date());
	    
	    for(int i = 0; i < beats.length; i++) {
	    	this.beats[i] = new Beat();
	    }
	}
	
	/**
	 * Erstellt einen gespeicherten Track aus einer .bb-Datei
	 * @param filePath Pfad zur Datei
	 * @throws IOException Wird geworfen, wenn die Datei nicht existiert.
	 */
	public Track(String filePath) throws IOException {
	    this.name = filePath.replace("\\", "/").substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".bb"));
	    
		FileReader fileReader = null;
		fileReader = new FileReader(new File(filePath));

		// Zeilen lesen
		BufferedReader buffReader = new BufferedReader(fileReader);
		String line = "";

		while ((line = buffReader.readLine()) != null) {
			if(!line.startsWith("#")) {
				int index = Integer.parseInt(line.split(":")[0]);
				String linePath = line.split(":")[1];
				// Sound erstellen
				if ((new File(linePath)).exists()) {
					Sound sound = new Sound(linePath);
					this.addSound(index, sound);
				} else {
					System.err.println("Der Sound \"" + line + "\" aus dem Beat " + this.name + " konnte nicht gefunden werden.");
				}
			}else {
				this.bpm =  Integer.valueOf(line.replace("#", "").split(":")[0]);
				this.bars = Integer.valueOf(line.replace("#", "").split(":")[1]);
				
				//this.beats = new Beat[(int) ((bpm / 60) * (this.getMillis() / 1000) * 4)];
				this.beats = new Beat[this.getLoopBeats()];


			    for(int i = 0; i < beats.length; i++) {
			    	this.beats[i] = new Beat();
			    }
			}
		}
		buffReader.close();
	}

	/**
	 * Fuegt Sound an Stelle ein, falls noch Platz ist
	 * @param index Stelle im Track (welcher Beat)
	 * @param sound Einzufuegender Sound
	 * @return true, wenn Einfuegen erfolgreich war (=noch Platz an der Stelle und Stelle vorhanden)
	 */
	public boolean addSound(int index, Sound sound) {
		if(index >= beats.length) {
			return false;
		}
		return beats[index].addSound(sound);
	}
	
	/**
	 * Fuegt Sound an exakt angegeber Stelle hinzu
	 * @param index Stelle im Track (welcher Beat)
	 * @param layer Stelle im Beat
	 * @param sound Einzufuegender Sound 
	 * @return true, wenn Einfuegen erfolgreich war (= noch Platz an der Stelle und Stelle vorhanden)
	 */
	public boolean addSound(int index, int layer, Sound sound) {
		return beats[index].addSound(sound, layer);
	}
	
	/**
	 * Entfernt Sound an angegebener Stelle
	 * @param index Stelle im Track (welcher Beat)
	 * @param layer Stelle im Beat
	 * @return ob Entfernen erfolgreich war (= der Sound im Beat war)
	 */
	public boolean removeSound(int index, int layer) {
		return beats[index].removeSound(layer);
	}
	
	/**
	 * Gibt Array mit allen enthaltenen Sounds des Beats an der angegebenen Stelle
	 * zurueck (Anzahl der Elemente kann also immer unterschiedlich sein)
	 * 
	 * @param index Stelle des Beats
	 * @return Sounds des Beats an der gegebenen Stelle
	 */
	public Beat getBeat(int index){
		return beats[index];
	}
	
	/**
	 * Gibt die Anzahl von Beats im Track zurueck
	 * @return Anzahl Beats (beginnend bei 0)
	 */
	public int getLength() {
		return beats.length;
	}
	
	/**
	 * Gibt an, ob der Beat an der angegebenen Stelle leer ist
	 * @param index Stelle des Beats
	 * @return true, wenn sich im Beat kein Element befindet
	 */
	public boolean isEmptyBeat(int index) {
		return beats[index].isEmpty();
	}
	
	/**
	 * Speichert den Track im .bb-Format im trackFolder, der in den Properties
	 * angegeben ist.
	 */
	public void saveTrack() {
		PrintWriter pw = null;
		FileWriter writer;
		try {
			pw = new PrintWriter(propHandler.getProperty("trackFolder") + this.name + ".bb");
			writer = new FileWriter(propHandler.getProperty("trackFolder") + this.name + ".bb", true);
			writer.write("#" + bpm + ":" + this.bars + System.getProperty("line.separator"));
			for (int i = 0; i < beats.length; i++) {
				for (Sound sound : beats[i].getSounds()) {
					if (sound != null) {
						writer.write(i + ":");
						writer.write(sound.getSoundPath());
						writer.write(System.getProperty("line.separator"));
					}
				}
			}
			pw.close();
			writer.close();
		} catch (IOException e) {
			System.err.println("Fehler beim Speichern des Beats.\n" + e.getMessage());
		}
	}
	
	/**
	 * @return Schlaege pro Minute
	 */
	public int getBpm() {
		return this.bpm;
	}
	
	/**
	 * @return Anzahl Millisekunden des Tracks
	 */
	public double getMillis() {
		return ( (double) 60 / bpm * 4 * bars * 1000);
	}
	
	/**
	 * @return Anzahl der Achtel-Schlaege des Tracks
	 */
	public int getLoopBeats() {
		return this.bars * 8; // 8 Schlaege pro Takt 
	}
	
	/**
	 * @return Anzahl der Takte des Beats
	 */
	public int getBars() {
		return this.bars;
	}
	
	/**
	 * @return Millisekunden zwischen den Achtel-Schlaegen
	 */
	public int getKey() {
		return (int) (getMillis() / getLoopBeats());
	}
	
	/**
	 * "leert" den Track, loescht alle Sounds
	 */
	public void clear() {
		for (Beat beat : beats) {
			for (int i=0; i < beat.getLength(); i++) {
				beat.removeSound(i);
			}
		}
	}
	
	/**
	 * @return Name des Tracks
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param trackName Name, den der Track bekommen soll
	 */
	public void setName(String trackName) {
		this.name = trackName;
	}
	
	public String toString() {
		String s = "";
		s += "--- Track \"" + this.name + "\" ---\n"; 
		s += "BPM: " + this.bpm + "\n";
		s += "Takte: " + this.bars + "\n";
		s += "LoopBeats: " + this.getLoopBeats() + "\n";
		s += "Key: " + this.getKey() + "\n";
		s += "---\nSounds:\n";
		for (int i = 0; i < beats.length; i++) {
			for (Sound sound : beats[i].getSounds()) {
				s += i + ":";
				if (sound != null) {
					s += " " + sound.getTitle();
				} else {
					s += "[leer]";
				}
				s += "\n";
			}
			s += "---\n";
		}
		
		
		return s;
	}
	
}
