package de.sopra.passwordmanager.controller;

import java.io.File;

/**
 * Der UtilityController stellt verschiedene Hilfsdienste zur Verfuegung
 * @author sopr049
 *
 */
public class UtilityController {
	
	/**
	 * Referenz zum Passwortmanagercontroller
	 */
	private PasswordManagerController passwordManagerController;
	
	/**
	 * Generiert ein Passwort, welches den Sicherheitsanforderungen entspricht
	 */
	public void generatePassword() {

	}
	
	/**
	 * Die Methode exportiert die aktuellen Daten in die angegebene Datei
	 * @param file Die Datei, in welche die daten exportiert werden sollen
	 */
	public void exportFile(File file) {

	}
	
	/**
	 * Die Methode entschluesselt einen eingegebenen text mit dem Masterpasswort
	 * @param text Der zu entschluesselnde Text, dabei kann es sich um Passwoerter oder die Sicherheitsfragen handeln
	 * @return Der zurueckgegebene String ist die entschluesselte Version des eingegebenen Textes 
	 */
	String encryptText(String text) {
		return null;
	}
	
	/**
	 * Die Methode verschluesselt einen eingegebenen text mit dem Masterpasswort
	 * @param text Der zu verschluesselnde Text, dabei kann es sich um Passwoerter oder die Sicherheitsfragen handeln
	 * @return Der zurueckgegebene String ist die verschluesselte Version des eingegebenen Textes 
	 */
	String decryptText(String text) {
		return null;
	}
	/**
	 * Diese Methode ueberprueft die Qualitaet eines Passwortes und gibt eine Zahl zwischen 0 und 100 zurueck ,wobei mehr besser ist
	 * @param text Das zu ueberpruefende Passwort
	 * @return Es wird ein Wert von 0 bis 100 geliefert, der die Qualitaet des Passwortes angibt, dabei steht 0 fuer sehr schlecht und 100 fuer sehr sicher
	 */
	int checkQuality(String text) {
		return 0;
	}
	
	/**
	 * Die Methode importiert eine neue Datei mit Anmeldedaten. Fuer den Import wird das Masterpasswort der Datei benoetigt.
	 * Das Importieren einer neuen Datei ueberschreibt die aktuellen Eintraege.
	 * @param file Die zu importierende Datei
	 * @param masterPassword das Masterpasswort des zu importierenden Projektes
	 * @return Die Methodee liefert false, wenn ein fehler beim importieren passiert, wenn true geliefert wird, 
	 * 		   hat der Import funktioniert
	 */
	boolean importFile(File file, String masterPassword) {
		return false;
	}

}