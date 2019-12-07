package player.elements;

import tools.PropertyHandler;

/**
 * Ein Beat beinhaltet die Sounds, die auf einen Schlag wiedergegeben werden.
 * Die maximale Anzahl dieser befinden sich in den Properties. 
 * Beats sind Bestandteile eines Tracks.
 * 
 * @author Becker, Schumacher, Laibold
 *
 */
public class Beat {
	private int length;
	private Sound[] sounds;
	private PropertyHandler propHandler = new PropertyHandler("resources/config.properties");

	/**
	 * Neuer leerer Beat, der eine gewisse Anzahl Sounds (aus den Properties
	 * entnommen) aufnehmen kann
	 */
	public Beat() {
		this.length = Integer.valueOf(propHandler.getProperty("maxSoundsPerBeat"));
		this.sounds = new Sound[length];
	}
	
	/**
	 * Fuegt einen Sound an einer bestimmten Stelle im Beat hinzu
	 * @param sound hinzuzufuegender Sound
	 * @param index Platz im Beat
	 * @return ob Sound hinzugefuegt werden konnte
	 */
	public boolean addSound(Sound sound, int index) {
		if (sounds[index] == null) {
			sounds[index] = sound;
			return true;
		}
		return false;
	}
	
	/**
	 * Fuegt Sound an erstmoeglicher Stelle zum Beat hinzu, falls noch Platz ist
	 * @param sound hinzuzufuegender Sound
	 * @return ob Sound hinzugefuegt werden konnte
	 */
	public boolean addSound(Sound sound) {
		if (this.getFirstEmptyIndex() != -1) {
			sounds[this.getFirstEmptyIndex()] = sound;
			return true;
		}
		return false;
	}
		
	/**
	 * Entfernt Sound
	 * @param index Stelle, an der Sound entfernt werden soll
	 * @return ob Entfernen erfolgreich war (= der Sound im Beat war)
	 */
	public boolean removeSound(int index) {
		if (sounds[index] != null) {
			sounds[index] = null;
			return true;
		}
		return false;
	}
	
	/**
	 * Gibt einen Sound an der angegebenen Stelle zurueck (kann null sein)
	 * @param index Stelle des Sounds
	 * @return Sound an der angegebenen Stelle 
	 */
	public Sound getSound(int index) {
		return this.sounds[index];
	}
	
	/**
	 * Gibt alle Sounds des Beats zurueck
	 * @return Sound-Array mit allen enthaltenen Sounds
	 */
	public Sound[] getSounds() {
		return this.sounds;
	}
	
	/**
	 * Gibt die erste freie Stelle im Beat-Array zurueck
	 * @return erste freie Array-Stelle, -1, wenn Array voll
	 */
	public int getFirstEmptyIndex() {
		for(int i=0; i < length; i++) {
			if(sounds[i] == null) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * @return Anzahl der Sounds im Beat (beginnend mit 0)
	 */
	public int getLength() {
		return sounds.length;
	}
	
	/**
	 * 
	 * @return true, wenn Beat leer ist
	 */
	public boolean isEmpty() {
		return (this.getLength() == 0);
	}
}
