package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.BasePassword;

import java.util.ArrayList;

public class PasswordReminderController {

	private PasswordManagerController passwordManagerController;

	boolean hasToBeChanged(BasePassword password) {
		return false;
	}

	ArrayList passwordsToBeChanged() {
		return null;
	}

}
