package de.sopra.passwordmanager.view;

/**
 * 
 * @author Hannah
 *
 */
public interface LoginViewAUI {

	/** Leitet bei erfolgreichem login weiter und gibt bei nicht erfolgreichem login eine Fehlermeldung oder Warnung aus.
	 * 
	 * @param result <strong>true</strong> Login war erfolgreich
	 *         <br>
	 *         <strong>false</strong> Login war nicht erfolgreich
	 */
	void handleLoginResult( boolean result );

}
