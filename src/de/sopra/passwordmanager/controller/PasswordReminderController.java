package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.BasePassword;

import java.util.ArrayList;

/**
 * In PasswordreminderController werden die Passwörter, bei denen der Timer abgelaufen ist, verwaltet.
 *
 * @author
 */

public class PasswordReminderController {


	private PasswordManagerController passwordManagerController;

	public PasswordReminderController( PasswordManagerController controller )
	{
		this.passwordManagerController = controller;
	}

	/**
	 * Abfrage ob der Timer von einem Passwort abgelaufen ist/das passwort geändert werden muss.
	 * @param password Das Passwort was überprüft werden soll.
	 * 
	 * @return <strong>true</strong> Timer ist abgelaufen/Passwort muss geändert werden,
	 *         <br>
	 *         <strong>false</strong> Timer ist nicht abgelaufen/Passwort muss nicht geändert werden.
	 */
	
	boolean hasToBeChanged(BasePassword password) {
		return false;
	}
	
	/**
	 * Welche Passwörter müssen geändert werden/bei welchen Passwörtern ist der Timer abgelaufen.
	 * @return Liste der Passwörter, wo der Timer abgelaufen ist/die geändert werden müssen.
	 * 
	 */
	
	ArrayList passwordsToBeChanged() {
		return null;
	}

}
