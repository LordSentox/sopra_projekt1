package de.sopra.passwordmanager.controller;

/**
 * Controller für Anforderungen der Masterpassworteingabe
 * @author Arne Dußin, Mel
 *
 */
public class MasterPasswordController {
	
	/**
	 * Der GUI zugehörige PasswordManagerController 
	 */
	private PasswordManagerController passwordManagerController;
	
	/**
	 * Das alte Masterpasswort wird mit dem neuen Masterpasswort überschrieben. 
	 * Ein neuer Timer wird eingerichtet.
	 * @param newPassword Passwort, mit dem der Datensatz von hier an (neu) verschlüsselt wird
	 * @param newReminder Zeit in Tagen, in der der Nutzer zur Änderung des Masterpasswortes aufgefordert wird 
	*/
	public void changePassword(String newPassword, int newReminder) {

	}
	
	/**
	 * Überprüft, ob das übergebene Passwort mit dem momentanen Masterpasswort übereinstimmt.
	 * @param password Das zu überprüfende Passwort
	 * @return true, wenn das übergebene Passwort mit dem hinterlegten Masterpasswort übereinstimmt. false, sonst.
	 */
	boolean checkPassword(String password) {
		return false;
	}
	
	/**
	 * Überprüft, ob der Timer des momentanen Masterpasswortes abgelaufen ist. 
	 * @return true, wenn Masterpasswort geändert werden muss. false, sonst.
	 */
	boolean hasToBeChanged() {
		return false;
	}

}
