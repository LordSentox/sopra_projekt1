package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Credentials;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In PasswordreminderController werden die Passwörter, bei denen der Timer abgelaufen ist, verwaltet.
 *
 * @author
 */

public class PasswordReminderController {


    private PasswordManagerController passwordManagerController;

    public PasswordReminderController(PasswordManagerController controller) {
        this.passwordManagerController = controller;
    }

    /**
     * Abfrage, ob der Timer von einem Passwort abgelaufen ist und ob es geändert werden muss.
     *
     * @param password Das Passwort was überprüft werden soll.
     * @return <strong>true</strong> Timer ist abgelaufen/Passwort muss geändert werden,
     * <br>
     * <strong>false</strong> Timer ist nicht abgelaufen/Passwort muss nicht geändert werden.
     */

    boolean hasToBeChanged(Credentials password) {
        if (password.getChangeReminderDays() == null) return false;

        LocalDateTime lastChanged = password.getLastChanged();
        LocalDateTime changeDate = lastChanged.plusDays(password.getChangeReminderDays());
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(changeDate) || now.isEqual(changeDate);
    }


    /**
     * Erstellt ein Set mit Passwörtern bei denen der Timer abgelaufen ist und geändert werden müssen.
     *
     * @return Set der Passwörter, wo der Timer abgelaufen ist und geändert werden müssen.
     */

    Set<Credentials> passwordsToBeChanged() {
        return passwordManagerController.getPasswordManager().getRootCategory().getAllCredentials().stream()
                .filter(this::hasToBeChanged)
                .collect(Collectors.toSet());
    }

}
