package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.Path;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import de.sopra.passwordmanager.util.CredentialsBuilder;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import java.util.List;

public class MainWindowViewController implements MainWindowAUI {

    private PasswordManagerController passwordManagerController;
    private SecurityQuestionViewController securityQuestionViewController;
    private CategoryEditViewController categoryEditViewController;
    private SettingsViewController settingsViewController;
    private LoginViewController loginViewController;
    private MasterPasswordViewController masterPasswordViewController;
    @FXML
    private JFXTextField textFieldSearch;

    public void setPasswordManagerController(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
    }

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
        try {
            /* Einstellungen */
            AnchorPane settingsPane = new AnchorPane();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Einstellungen.fxml"));
            settingsPane = fxmlLoader.load();
            settingsViewController = (SettingsViewController) fxmlLoader.getController();

            Stage settingsStage = new Stage();
            Scene settingsScene = new Scene(settingsPane);
            settingsScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            settingsStage.setScene(settingsScene);
            //settingsViewController.setStage(settingsStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onSearchClicked() {

        CredentialsController cController = passwordManagerController.getCredentialsController();

        //TODO: Add correct Method
        Path categoryPath = new Path("");
        String pattern = textFieldSearch.getText();
        cController.filterCredentials(categoryPath, pattern);
    }

    public void onAddCategoryClicked() {
        try {
            /* Kategorie hinzufügen */
            AnchorPane categoryEditPane = new AnchorPane();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Kategorie_anlegen-aendern.fxml"));
            categoryEditPane = fxmlLoader.load();
            categoryEditViewController = (CategoryEditViewController) fxmlLoader.getController();

            Stage categoryEditStage = new Stage();
            Scene categoryEditScene = new Scene(categoryEditPane);
            categoryEditScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            categoryEditStage.setScene(categoryEditScene);
            //categoryEditViewController.setStage(categoryEditStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void onEditCategoryClicked() {
        try {
            /* Kategorie bearbeiten */
            AnchorPane categoryEditPane = new AnchorPane();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Kategorie_anlegen-aendern.fxml"));
            categoryEditPane = fxmlLoader.load();
            categoryEditViewController = (CategoryEditViewController) fxmlLoader.getController();

            Stage categoryEditStage = new Stage();
            Scene categoryEditScene = new Scene(categoryEditPane);
            categoryEditScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            categoryEditStage.setScene(categoryEditScene);
            //categoryEditViewController.setStage(categoryEditStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void onRemoveCategoryClicked() {

    }

    public void onShowPasswordClicked() {

    }

    public void onCopyPasswordClicked() {

    }

    public void onGeneratePasswordClicked() {
        //passwordManagerController.getUtilityController().generatePassword();
    }

    public void onAddSecurityQuestionClicked() {
        try {
            /* Sicherheitsfrage hinzufügen */
            AnchorPane securityQuestionAddPane = new AnchorPane();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Kategorie_anlegen-aendern.fxml"));
            securityQuestionAddPane = fxmlLoader.load();
            securityQuestionViewController = (SecurityQuestionViewController) fxmlLoader.getController();

            Stage securityQuestionAddStage = new Stage();
            Scene securityQuestionAddScene = new Scene(securityQuestionAddPane);
            securityQuestionAddScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            securityQuestionAddStage.setScene(securityQuestionAddScene);
            //securityQuestionViewController.setStage(securityQuestionAddStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

    @Override
    public void refreshEntry(CredentialsBuilder credentials) {
        // TODO Auto-generated method stub

    }

}
