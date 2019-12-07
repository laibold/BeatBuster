package tools;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Liest grundlegende Parameter aus .properties-Datei aus.
 * @author Becker, Schumacher, Laibold
 *
 */
public class PropertyHandler {
	private String propertyPath;
	
	/**
	 * Erzeugt einen neuen PropertyReader
	 * @param propertyPath Pfad der Datei, die gelesen werden soll
	 */
	public PropertyHandler(String propertyPath) {
		this.propertyPath = propertyPath;
	}
	
	/**
	 * Gibt angegebenen Property-Wert als String zurueck
	 * @param property gewuenschter Propery-Wert, wie er in den Poperties vor dem "=" steht
	 * @return Inhalt des Werts als String
	 */
	public String getProperty(String property) {
		Properties prop = new Properties();

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(this.propertyPath);
			prop.load(inputStream);
		} catch (FileNotFoundException e1) {
			System.err.println("Fehler beim Lesen der Properties: Property-Datei \"" + propertyPath + "\" wurde nicht gefunden.");
		} catch (IOException e) {
			System.err.println("Fehler beim Lesen der Properties \"" + propertyPath + "\".  IO-Fehler.");
		}
		
		if (prop.getProperty(property) == null) {
			System.err.println("Fehler: Property \"" + property + "\" existiert nicht.");
		}
		
		return prop.getProperty(property);
	}
	
	/**
	 * Setzt den angegebenen Property-Wert
	 * @param property Name des Properties
	 * @param value Wert, den das Property bekommen soll
	 */
	public void setProperty(String property, String value) {
		Properties prop = new Properties();

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertyPath);
			prop.load(inputStream);
			if (prop.getProperty(property) == null) {
				System.err.println("Fehler: Property \"" + property + "\" existiert nicht.");
			} else {
				prop.setProperty(property, value);
				prop.store(new FileOutputStream(propertyPath), null);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Fehler beim Schreiben der Properties: Property-Datei \"" + propertyPath
					+ "\" wurde nicht gefunden.");
		} catch (IOException e) {
			System.err.println("Fehler beim Schreiben der Properties \"" + propertyPath + "\".  IO-Fehler.");
		}
	}
}