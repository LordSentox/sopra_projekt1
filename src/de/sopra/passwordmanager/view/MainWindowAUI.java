package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsBuilder;

import java.util.List;

/**
 * @author Etienne
 * Bietet Methoden zum aktualisieren des Hauptfensters, sowie zum Anzeigen von Fehlermeldungen
 */
public interface MainWindowAUI {

    /**
     * Aktualisiert die Liste der anzuzeigenden {@link Credentials}
     * Ist die Liste leer, soll eine leere Liste angezeigt werden.
     * Ist die übergebene Liste <code>null</code> soll die Liste nicht geändert werden.
     *
     * @param entries Die {@link List} von {@link Credentials}, die zum Anzeigen in der UI zur Verfügung gestellt werden soll
     */
    void refreshEntryList(List<Credentials> entries);

    /**
     * Aktualisiert den momentan bearbeiteten Eintrag in der GUI
     */
    void refreshEntry();

    /**
     * Aktualisiert den momentan bearbeiteten Eintrag in der GUI.
     * Hier besteht noch die Möglichkeit ein Passwort zu übergeben, etwa um dieses in der UI anzuzeigen.
     *
     * @param credentials Der neue {@link CredentialsBuilder}, welcher im {@link MainWindowViewController} als Eintrag
     *                    der momentan bearbeiteten {@link Credentials} dienen soll.
     */
    void refreshEntry(CredentialsBuilder credentials);

    /**
     * Aktualisiert die Elemente, die zum Anzeigen der Passwortqualität erforderlich sind
     *
     * @param quality Die Qualität des Passworts
     */
    void refreshEntryPasswordQuality(int quality);

    /**
     * Zeigt eine Fehlermeldung an
     *
     * @param error Die Nachricht der Fehlermeldung
     */
    void showError(String error);

}
