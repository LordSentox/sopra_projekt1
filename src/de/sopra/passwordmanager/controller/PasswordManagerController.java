package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.view.LoginViewAUI;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MasterPasswordViewAUI;

import java.io.File;

/**
 * Verwaltet die anderen Controller
 * 
 * @author Hannah, Jin
 *
 */

public class PasswordManagerController
{

    private PasswordManager passwordManager;

    private CredentialsController credentialsController;

    private CategoryController categoryController;

    private UtilityController utilityController;

    private MasterPasswordController masterPasswordController;

    private PasswordReminderController passwordReminderController;

    private MainWindowAUI mainWindowAUI;

    private LoginViewAUI loginViewAUI;

    private MasterPasswordViewAUI masterPasswordViewAUI;

    public PasswordManagerController( PasswordManager passwordManager, CredentialsController credentialsController,
                                      CategoryController categoryController, UtilityController utilityController,
                                      MasterPasswordController masterPasswordController,
                                      PasswordReminderController passwordReminderController, MainWindowAUI mainWindowAUI,
                                      LoginViewAUI loginViewAUI, MasterPasswordViewAUI masterPasswordViewAUI )
    {
        this.passwordManager = passwordManager;
        this.credentialsController = credentialsController;
        this.categoryController = categoryController;
        this.utilityController = utilityController;
        this.masterPasswordController = masterPasswordController;
        this.passwordReminderController = passwordReminderController;
        this.mainWindowAUI = mainWindowAUI;
        this.loginViewAUI = loginViewAUI;
        this.masterPasswordViewAUI = masterPasswordViewAUI;
    }

    public PasswordManagerController( MainWindowAUI mainWindowAUI, LoginViewAUI loginViewAUI, MasterPasswordViewAUI masterPasswordViewAUI )
    {
        this.passwordManager = new PasswordManager();
        this.credentialsController = new CredentialsController(this);
        this.categoryController = new CategoryController(this);
        this.utilityController = new UtilityController(this);
        this.masterPasswordController = new MasterPasswordController(this);
        this.passwordReminderController = new PasswordReminderController(this);
        this.mainWindowAUI = mainWindowAUI;
        this.loginViewAUI = loginViewAUI;
        this.masterPasswordViewAUI = masterPasswordViewAUI;
    }

    public PasswordManager getPasswordManager()
    {
        return passwordManager;
    }

	public CredentialsController getCredentialsController()
    {
        return credentialsController;
    }

    public CategoryController getCategoryController()
    {
        return categoryController;
    }

    public UtilityController getUtilityController()
    {
        return utilityController;
    }

    public MasterPasswordController getMasterPasswordController()
    {
        return masterPasswordController;
    }

    public PasswordReminderController getPasswordReminderController()
    {
        return passwordReminderController;
    }

    public MainWindowAUI getMainWindowAUI()
    {
        return mainWindowAUI;
    }

    public LoginViewAUI getLoginViewAUI()
    {
        return loginViewAUI;
    }

    public MasterPasswordViewAUI getMasterPasswordViewAUI()
    {
        return masterPasswordViewAUI;
    }

    //-------------------------------------------------------------------------------------------

	/**
	 * Setzt den PasswordManager zurück und löscht alle Passwörter und Kategorien. Das Masterpasswort bleibt erhalten.
	 */
	public void removeAll() {

	}
    
    /**
	 * Einloggen im Pogramm mit Masterpasswort und Daten werden importiert/geladen oder Daten werden für den Import geladen.
	 * Überprüft, ob das Masterpasswort stimmt und lädt die Dateien falls es stimmt.
	 * 
	 * 
	 * @param password eingegebenes Passwort zum einloggen, welches überprüft werden muss
	 * @param file Daten, die geladen/importiert werden müssen 
	 */
    public void requestLogin( String password, File file ) {

    }

}
