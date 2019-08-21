package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.model.Credentials;

import java.util.List;

/**
 * @author Etienne
 * Bietet Methoden zum aktualisieren des Hauptfensters, sowie zum Anzeigen von Fehlermeldungen
 */
public interface MainWindowAUI {

	/**
	 * Aktualisiert die Liste der anzuzeigenden {@link Credentials}
	 * @param entries Die {@link List} von {@link Credentials}, die zum Anzeigen in der UI zur Verfügung gestellt werden soll
	 */
	void refreshEntryList(List<Credentials> entries );

	/**
	 * Aktualisiert den momentan bearbeiteten Eintrag in der GUI
	 */
	void refreshEntry();

	/**
	 * Aktualisiert den momentan bearbeiteten Eintrag in der GUI.
	 * Hier besteht noch die Möglichkeit ein Passwort zu übergeben, etwa um dieses in der UI anzuzeigen.
	 * @param password Das anzuzeigende Passwort
	 */
	void refreshEntry(String password);

	/**
	 * Aktualisiert die Elemente, die zum Anzeigen der Passwortqualität erforderlich sind
	 * @param quality Die Qualität des Passworts
	 */
	void refreshEntryPasswordQuality(int quality);

	/**
	 * Zeigt eine Fehlermeldung an
	 * @param error Die Nachricht der Fehlermeldung
	 */
	void showError(String error);

}
