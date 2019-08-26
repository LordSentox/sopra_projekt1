package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.EntryListStrategy;

/**
 * @author Etienne
 * Bietet Methoden zum aktualisieren des Hauptfensters, sowie zum Anzeigen von Fehlermeldungen
 */
public interface MainWindowAUI {

    /**
     * Aktualisiert die Liste der anzuzeigenden {@link Credentials} unter Verwendung der bekannten Strategie.
     * Alle Listen der Kategorien werden hier mit eingeschlossen.
     */
    void refreshLists();

    /**
     * Ändert die Strategie mit der die Listen der Einträge gefüllt werden.
     * Soll nach Abschluss selbstständig zu einem Aufruf von {@link #refreshLists()} führen.
     *
     * @param newStrategy die neue Strategy zur Verarbeitung der Daten
     */
    void refreshEntryListStrategy(EntryListStrategy newStrategy);

    /**
     * Aktualisiert den momentan bearbeiteten Eintrag in der GUI
     */
    void refreshEntry();

    /**
     * Aktualisiert die Elemente, die zum Anzeigen der Passwortqualität erforderlich sind
     *
     * @param quality Die Qualität des Passworts
     */
    void refreshEntryPasswordQuality(int quality);

    /**
     * Zeigt eine Fehlermeldung mit dem gegebenen String als Inhalt an
     *
     * @param error Die Nachricht der Fehlermeldung
     */
    void showError(String error);

}
