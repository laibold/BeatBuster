package tools;

/**
 * Enthaelt alle spielbaren Locations.
 * @author Becker, Schumacher, Laibold
 *
 */
public enum BeatLocation {
	GARAGE("garage.png", "garage_maske.png","resources/garageSounds.properties"),
	BEACH("strand.png", "strand_maske.png","resources/strandSounds.properties"),
	GUITAR("gitarre.png","gitarre_Maske.png","resources/gitarreSounds.properties");	
	
	private final String imageName;
	private final String maskName;
	private final String propertyName;
	
	private BeatLocation(String imageName, String maskName, String propertyName){
		this.imageName = imageName;
		this.maskName = maskName;
		this.propertyName = propertyName;
	}
	
	/**
	 * @return Name der Bilddatei der Location
	 */
	public String getImageName() {
		return this.imageName;
	}
	
	/**
	 * @return Name der Maske der Location, die die Farbwerte fuer die Sounds darstellt
	 */
	public String getMaskName() {
		return this.maskName;
	}
	
	/**
	 * @return Name der Property-Datei, die die Farbwerte den Soundnamen der
	 *         Location zuweist
	 */
	public String getPropertyName() {
		return this.propertyName;
	}
}
