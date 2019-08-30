package de.sopra.passwordmanager.controller;

import java.io.File;

public class IOController {
    private PasswordManagerController passwordManagerController;

    IOController(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
    }

    /**
     * Die Methode importiert eine neü Datei mit Anmeldedaten. Für den Import wird das Masterpasswort der Datei benötigt.
     * Das Importieren einer neün Datei überschreibt die aktüllen Einträge.
     *
     * @param encryptionPassword das neue Passwort zum reencrypten, kann identisch zu decryptionPassword sein
     * @return Die Methode liefert false, wenn ein fehler beim importieren passiert, wenn true geliefert wird,
     * hat der Import funktioniert
     */
    boolean importFile(File file, String decryptionPassword, String encryptionPassword, boolean setMaster) {
        Importer importer = new Importer(file, decryptionPassword, passwordManagerController);
        return importer.importFile(encryptionPassword, setMaster);
    }

    /**
     * Die Methode exportiert die aktuellen Daten in die angegebene Datei, wenn die Datei bereits etwas enthält, wird diese überschrieben
     *
     * @param file Die Datei, in welche die daten exportiert werden sollen
     * @throws IllegalArgumentException Wenn file null ist oder der Pfad nicht existiert
     */
    public void exportFile(File file) throws IllegalArgumentException {
        Exporter exporter = new Exporter(passwordManagerController);
        exporter.exportFile(file);
    }
}
