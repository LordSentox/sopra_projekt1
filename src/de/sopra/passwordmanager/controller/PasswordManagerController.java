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

	public void removeAll() {

	}

	public void requestLogin(String password, File file) {

	}

}
