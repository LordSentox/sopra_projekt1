package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.view.LoginViewAUI;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MainWindowViewController;
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
    public static final int MINIMUM_SAFE_QUALITY = 50;
    public static final File SAVE_FILE = new File("../data.xml");

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
    	passwordManager.getRootCategory().getSubCategories().clear();
    	passwordManager.getRootCategory().getCredentials().clear();
    	SAVE_FILE.delete();
    	mainWindowAUI.refreshListStrategies(identity -> identity, identity -> identity);
    	mainWindowAUI.refreshEntry();
    	mainWindowAUI.refreshLists();
    	mainWindowAUI.refreshEntryPasswordQuality(0);
    }

    /**
     * Einloggen im Pogramm mit Masterpasswort und Daten werden importiert/geladen oder Daten werden für den Import geladen.
     * Überprüft, ob das Masterpasswort stimmt und lädt die Dateien falls es stimmt.
     *
     * @param password eingegebenes Passwort zum einloggen, welches überprüft werden muss
     * @param file     Daten, die geladen/importiert werden müssen
     */
    public void requestLogin(String password, File file) {
    	//ist null, wenn kein sondern Login
    	if(passwordManager.getMasterPassword() == null){
    		boolean result = utilityController.importFile(file, password, password, true);
	    	loginViewAUI.handleLoginResult(result);
    	} else {
    		boolean result = utilityController.importFile(file, password, password, false);
	    	loginViewAUI.handleLoginResult(result);
    	}

    }

    /**
     * Überprüft die Qualität des im übergebenen {@link CredentialsBuilder} enthaltenen Passwortes und aktualisiert den
     * Qualitätsbalken im {@link de.sopra.passwordmanager.view.MainWindowViewController}.
     * Ist das Passwort im {@link CredentialsBuilder} <code>null</code> soll <b>keine</b> NullPointerException geworfen
     * werden
     *
     * @param credentials Der {@link CredentialsBuilder}, welcher das zu prüfende Passwort beinhaltet
     * @throws NullPointerException falls statt eines {@link CredentialsBuilder} <code>null</code> übergeben wird
     */
    public void checkQuality(CredentialsBuilder credentials) throws NullPointerException {
    	int quality = utilityController.checkQuality(credentials.getPassword());
    	mainWindowAUI.refreshEntryPasswordQuality(quality);
    }

    /**
     * Ersetzt die alten {@link Credentials} mit den Neuen.
     *
     * @param oldCredentials Die zu ersetzenden {@link Credentials}
     * @param newCredentials Die {@link Credentials}, die die alten ersetzen
     * @param newCategories Die Kategorien, in die die neuen {@link Credentials} eingefügt werden sollen. Waren die
     *                      oldCredentials davor in anderen Kategorien, werden sie aus den nicht angegebenen entfernt.
     * @see Category
     */
    public void saveEntry(Credentials oldCredentials, CredentialsBuilder newCredentials, Collection<Category> newCategories) {
    	   credentialsController.removeCredentials(oldCredentials);
    	   credentialsController.addCredentials(newCredentials, newCategories);
    	   mainWindowAUI.refreshEntry();
    }
}
