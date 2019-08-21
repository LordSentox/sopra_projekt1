package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.view.LoginViewAUI;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MasterPasswordViewAUI;

import java.io.File;

public class PasswordManagerController {

	private PasswordManager passwordManager;

	private CredentialsController credentialsController;

	private CategoryController categoryController;

	private UtilityController utilityController;

	private MasterPasswordController masterPasswordController;

	private PasswordReminderController passwordReminderController;

	private MainWindowAUI mainWindowAUI;

	private LoginViewAUI loginViewAUI;

	private MasterPasswordViewAUI masterPasswordViewAUI;

	/**
	 * Setzt den PasswordManager zurück und löscht alle Passwörter und Kategorien.
	 */
	public void removeAll() {

	}
	/**
	 * Einloogen im Pogramm mit Masterpasswort und Daten werden importiert/geladen.
	 * Überprüft, ob das Masterpasswort stimmt und lädt die Dateien falls es stimmt.
	 * 
	 * @param password eingegebenes Passwort zum einloggen, welches überprüft werden muss
	 * @param file Daten, die geladen/importiert werden müssen 
	 */

	public void requestLogin(String password, File file) {

	}

}
