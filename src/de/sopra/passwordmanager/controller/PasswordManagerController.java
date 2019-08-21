package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.view.LoginViewAUI;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MasterPasswordViewAUI;

import java.io.File;

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

    public PasswordManagerController( MainWindowAUI mainWindowAUI, LoginViewAUI loginViewAUI, MasterPasswordViewAUI masterPasswordViewAUI )
    {
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

    public void removeAll() {

    }

    public void requestLogin( String password, File file ) {

    }

}
