package de.sopra.passwordmanager.controller;

import aes.AES;
import de.sopra.passwordmanager.model.EncryptedString;
import exceptions.DecryptionException;
import exceptions.EncryptionException;

import java.io.File;

/**
 * Der UtilityController stellt verschiedene Hilfsdienste zur Verfügung
 *
 * @author sopr049, sopr043
 */
public class UtilityController {

    /**
     * Referenz zum Passwortmanagercontroller
     */
    private PasswordManagerController passwordManagerController;

    public UtilityController(PasswordManagerController controller) {
        this.passwordManagerController = controller;
    }

    /**
     * Generiert ein Passwort, welches den Sicherheitsanforderungen entspricht nd dieses wird dann in der GUI angezeigt
     */
    public void generatePassword() {

    }

    /**
     * Die Methode exportiert die aktüllen Daten in die angegebene Datei, wenn die Datei bereits etwas enthält, wird diese überschrieben
     *
     * @param file Die Datei, in welche die daten exportiert werden sollen
     * @throws IllegalArgumentException Wenn file null ist oder der Pfad nicht existiert
     */
    public void exportFile(File file) throws IllegalArgumentException {

    }

    /**
     * Die Methode entschlüsselt einen eingegebenen text mit dem Masterpasswort
     *
     * @param text Der zu entschlüsselnde Text, dabei kann es sich um Passwörter oder die Sicherheitsfragen handeln
     * @return Der zurückgegebene String ist die entschlüsselte Version des eingegebenen Textes oder <code>null</code>,
     * wenn er nicht entschlüsselt werden konnte
     */
    public String decryptText(EncryptedString text) {
        try {
            return AES.decrypt(text.getEncryptedContent(), passwordManagerController.getPasswordManager().getMasterPassword());
        } catch (DecryptionException e) {
            System.out.println(text.getEncryptedContent() + " konnte nicht entschlüsselt werden.");
            return null;
        }
    }

    /**
     * Die Methode verschlüsselt einen eingegebenen text mit dem Masterpasswort
     *
     * @param text Der zu verschlüsselnde Text, dabei kann es sich um Passwörter oder die Sicherheitsfragen handeln
     * @return Der zurückgegebene String ist die verschlüsselte Version des eingegebenen Textes
     */
    public EncryptedString encryptText(String text) {
        try {
            return new EncryptedString(AES.encrypt(text, passwordManagerController.getPasswordManager().getMasterPassword()));
        } catch (EncryptionException e) {
            System.out.println("Ein Text konnte nicht verschlüsselt werden.");
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * Diese Methode überprüft die Qualität eines Passwortes und gibt eine Zahl zwischen 0 und 100 zurück ,wobei mehr besser ist
     *
     * @param text Das zu überprüfende Passwort
     * @return Es wird ein Wert von 0 bis 100 geliefert, der die Qualität des Passwortes angibt, dabei steht 0 für sehr schlecht und 100 für sehr sicher
     */
    int checkQuality(String text) {
        return 0;
    }

    /**
     * Die Methode importiert eine neü Datei mit Anmeldedaten. Für den Import wird das Masterpasswort der Datei benötigt.
     * Das Importieren einer neün Datei überschreibt die aktüllen Einträge.
     *
     * @param file           Die zu importierende Datei
     * @param masterPassword das Masterpasswort des zu importierenden Projektes
     * @return Die Methode liefert false, wenn ein fehler beim importieren passiert, wenn true geliefert wird,
     * hat der Import funktioniert
     */
    boolean importFile(File file, String masterPassword) {
        return false;
    }

}