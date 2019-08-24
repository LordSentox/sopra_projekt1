package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.view.LoginViewAUI;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MasterPasswordViewAUI;

import java.io.File;
import java.util.Collection;

/**
 * Verwaltet die anderen Controller
 *
 * @author Hannah, Jin
 */

public class PasswordManagerController {

    /**
     * Minimale Qualität, die ein Passwort benötigt um als sicher zu gelten
     */
    public static final int MINUM_SAFE_QUALITY = 50;

    private PasswordManager passwordManager;

    private CredentialsController credentialsController;

    private CategoryController categoryController;

    private UtilityController utilityController;

    private MasterPasswordController masterPasswordController;

    private PasswordReminderController passwordReminderController;

    private MainWindowAUI mainWindowAUI;

    private LoginViewAUI loginViewAUI;

    private MasterPasswordViewAUI masterPasswordViewAUI;

    public PasswordManagerController() {
        this.passwordManager = new PasswordManager();
        this.credentialsController = new CredentialsController(this);
        this.categoryController = new CategoryController(this);
        this.utilityController = new UtilityController(this);
        this.masterPasswordController = new MasterPasswordController(this);
        this.passwordReminderController = new PasswordReminderController(this);
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }

    public CredentialsController getCredentialsController() {
        return credentialsController;
    }

    public CategoryController getCategoryController() {
        return categoryController;
    }

    public UtilityController getUtilityController() {
        return utilityController;
    }

    public MasterPasswordController getMasterPasswordController() {
        return masterPasswordController;
    }

    public PasswordReminderController getPasswordReminderController() {
        return passwordReminderController;
    }

    public MainWindowAUI getMainWindowAUI() {
        return mainWindowAUI;
    }

    public LoginViewAUI getLoginViewAUI() {
        return loginViewAUI;
    }

    public MasterPasswordViewAUI getMasterPasswordViewAUI() {
        return masterPasswordViewAUI;
    }

    public void setLoginViewAUI(LoginViewAUI loginViewAUI) {
        this.loginViewAUI = loginViewAUI;
    }

    public void setMasterPasswordViewAUI(MasterPasswordViewAUI masterPasswordViewAUI) {
        this.masterPasswordViewAUI = masterPasswordViewAUI;
    }

    public void setMainWindowAUI(MainWindowAUI mainWindowAUI) {
        this.mainWindowAUI = mainWindowAUI;
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
     * @param password eingegebenes Passwort zum einloggen, welches überprüft werden muss
     * @param file     Daten, die geladen/importiert werden müssen
     */
    public void requestLogin(String password, File file) {

    }

    /**
     * //TODO
     *
     * @param oldCredentials
     * @param newCredentials
     * @param newCategories
     */
    public void saveEntry(Credentials oldCredentials, CredentialsBuilder newCredentials, Collection<Category> newCategories) {
//        categoryController.removeCredentialsFromCategories(oldCredentials);
//        credentialsController.saveCredentials(oldCredentials, newCredentials);
//        categoryController.addCredentialsToCategories(newCredentials, newCategories);
    }

}
