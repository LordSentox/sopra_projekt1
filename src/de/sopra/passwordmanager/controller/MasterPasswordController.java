package de.sopra.passwordmanager.controller;

import java.time.LocalDateTime;

/**
 * Controller für Anforderungen der Masterpassworteingabe
 *
 * @author Arne Dußin, Mel
 */
public class MasterPasswordController {
    /**
     * Der GUI zugehörige PasswordManagerController
     */
    private PasswordManagerController passwordManagerController;

    public MasterPasswordController(PasswordManagerController controller) {
        this.passwordManagerController = controller;
    }

    /**
     * Das alte Masterpasswort wird mit dem neuen Masterpasswort überschrieben.
     * Ein neuer Timer wird eingerichtet.
     *
     * @param newPassword Passwort, mit dem der Datensatz von hier an (neu) verschlüsselt wird
     * @param newReminder Zeit in Tagen, in der der Nutzer zur Änderung des Masterpasswortes aufgefordert wird
     */
    public void changePassword(String newPassword, int newReminder) {
        String oldMasterPassword = passwordManagerController.getPasswordManager().getMasterPassword();
        passwordManagerController.getPasswordManager().setMasterPassword(newPassword);
        passwordManagerController.getPasswordManager().setMasterPasswordReminderDays(newReminder);
        passwordManagerController.getCredentialsController().reencryptAll(oldMasterPassword, newPassword);
    }

    /**
     * Überprüft die Qualität des übergebenen Passwortes und aktualisiert den Qualitätsbalken in dem
     * {@link de.sopra.passwordmanager.view.MasterPasswordViewController}
     *
     * @param password Das Passwort, welches nach den festgelegten Qualitätsmerkmalen untersucht werden soll
     * @throws NullPointerException falls statt eines Passwortstrings <code>null</code> übergeben wird
     */
    public void checkQuality(String password) throws NullPointerException {
        int quality = passwordManagerController.getUtilityController().checkQuality(password,null);
        passwordManagerController.getMasterPasswordViewAUI().refreshQuality(quality);

    }

    /**
     * Überprüft, ob das übergebene Passwort mit dem momentanen Masterpasswort übereinstimmt.
     *
     * @param password Das zu überprüfende Passwort
     * @return true, wenn das übergebene Passwort mit dem hinterlegten Masterpasswort übereinstimmt. false, sonst.
     */
    boolean checkPassword(String password) {
        return passwordManagerController.getPasswordManager().getMasterPassword().equals(password);
    }

    /**
     * Überprüft, ob der Timer des momentanen Masterpasswortes abgelaufen ist.
     *
     * @return true, wenn Masterpasswort geändert werden muss. false, sonst.
     */
    boolean hasToBeChanged() {
        LocalDateTime lastChanged = passwordManagerController.getPasswordManager().getMasterPasswordLastChanged();
        int reminderDays = passwordManagerController.getPasswordManager().getMasterPasswordReminderDays();
        LocalDateTime targetTime = lastChanged.plusDays(reminderDays);
        return targetTime.compareTo(LocalDateTime.now()) <= 0;
    }

}
