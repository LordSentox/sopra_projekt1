package de.sopra.passwordmanager.model;

/**
 * Enthält alle Daten die für den PasswortManager relevant sind in verschlüsseltem Zustand.
 */
public class PasswordManager {
    /**
     * Passwort mit dem die Daten im PasswordManager momentan verschlüsselt sind. Es ist das einzige Passwort, was
     * unverschlüsselt im Speicher liegt und nicht mit im Datensatz gespeichert wird.
     */
    private BasePassword masterPassword;

    /**
     * Die Hauptkategorie, in der alle Unterkategorien des Datenmodells enthalten sind.
     */
    private Category rootCategory;

    public BasePassword getMasterPassword() { return masterPassword; }

    public Category getRootCategory() { return rootCategory; }

    public void setMasterPassword( BasePassword masterPassword ) { this.masterPassword = masterPassword; }
}
