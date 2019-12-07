package player;

import javafx.scene.media.AudioClip;
import player.elements.Sound;

/**
 * Eigenticher Player, der die Sounds als AudioClips in einzelnen Threads
 * wiedergibt. Lautstaerke kann ueber setVolume() eingestellt werden.
 * 
 * @author Becker, Schumacher, Laibold
 *
 */
public class Player {
	private double volume = 1; // Muss hier gespeichert werden, um bei neuem Lied Lautstaerke beizubehalten
	
	/**
	 * Legt einen neuen Thread an und 
	 * Spielt den aktuellen Sound ab
	 * @param sound Sound, der wiedergegeben werden soll
	 */
	public void play(Sound sound) {
		Thread playThread = new Thread() {
			public void run() {
				AudioClip playSound = sound.getAudioClip();
				playSound.play(volume);
			}
		};
		playThread.start();
	}
	
	/**
	 * Aendert die Lautstaerke fuer alle zukuenftigen Sounds
	 * 
	 * @param value Wert zwischen 0 und 1
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}
}