package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Credentials;

import java.util.List;

public class MainWindowViewController implements MainWindowAUI {

    private PasswordManagerController passwordManagerController;

    private SecurityQuestionViewController securityQuestionViewController;

    private CategoryEditViewController categoryEditViewController;

    private SettingsViewController settingsViewController;

    private LoginViewController loginViewController;

    private MasterPasswordViewController masterPasswordViewController;

    public PasswordManagerController getPasswordManagerController() {
        return passwordManagerController;
    }

    public SecurityQuestionViewController getSecurityQuestionViewController() {
        return securityQuestionViewController;
    }

    public CategoryEditViewController getCategoryEditViewController() {
        return categoryEditViewController;
    }

    public SettingsViewController getSettingsViewController() {
        return settingsViewController;
    }

    public LoginViewController getLoginViewController() {
        return loginViewController;
    }

    public MasterPasswordViewController getMasterPasswordViewController() {
        return masterPasswordViewController;
    }

    public void onSettingsClicked() {

    }

    public void onSearchClicked() {

    }

    public void onAddCategoryClicked() {

    }

    public void onEditCategoryClicked() {

    }

    public void onRemoveCategoryClicked() {

    }

    public void onShowPasswordClicked() {

    }

    public void onCopyPasswordClicked() {

    }

    public void onGeneratePasswordClicked() {
        passwordManagerController.getUtilityController().generatePassword();
    }

    public void onAddSecurityQuestionClicked() {

    }

    public void onRemoveSecurityQuestionClicked() {

    }

    public void onAddCredentialsClicked() {

    }

    public void onRemoveCredentialsClicked() {

    }

    public void onStartEditCredentialsClicked() {

    }

    public void onSaveCredentialsClicked() {

    }

    public void onReminderToggleClicked() {

    }

    public void onChooseCategoryClicked() {

    }

    public void onChooseQuestionClicked() {

    }


    @Override
    public void refreshEntryList(List<Credentials> entries) {

    }

    public void refreshEntry() {

    }

    public void refreshEntry(String password) {

    }


    public void refreshEntryPasswordQuality(int quality) {

    }


    public void showError(String error) {

    }

}
