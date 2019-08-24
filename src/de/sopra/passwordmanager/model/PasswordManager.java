package de.sopra.passwordmanager.model;

import java.time.LocalDateTime;

/**
 * Enthält alle Daten die für den PasswortManager relevant sind in verschlüsseltem Zustand.
 */
public class PasswordManager {
    /**
     * Passwort mit dem die Daten im PasswordManager momentan verschlüsselt sind. Es ist das einzige Passwort, was
     * unverschlüsselt im Speicher liegt und nicht mit im Datensatz gespeichert wird.
     */
    private String masterPassword;

    private int masterPasswordReminderDays;

    private LocalDateTime masterPassordLastChanged;

    /**
     * Die Hauptkategorie, in der alle Unterkategorien des Datenmodells enthalten sind.
     */
    private Category rootCategory;

    public PasswordManager() {
        rootCategory = new Category("alle");
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public Category getRootCategory() {
        return rootCategory;
    }

    public int getMasterPasswordReminderDays() {
        return masterPasswordReminderDays;
    }

    public LocalDateTime getMasterPassordLastChanged() {
        return masterPassordLastChanged;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void setMasterPasswordReminderDays(int masterPasswordReminderDays) {
        this.masterPasswordReminderDays = masterPasswordReminderDays;
    }

    public void setMasterPassordLastChanged(LocalDateTime masterPassordLastChanged) {
        this.masterPassordLastChanged = masterPassordLastChanged;
    }

    public void setMasterPassordLastChanged() {
        setMasterPassordLastChanged(LocalDateTime.now());
    }

}
