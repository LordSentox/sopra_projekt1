package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.sopra.passwordmanager.controller.CategoryController;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Path;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.List;

public class MainWindowViewController implements MainWindowAUI {

    private PasswordManagerController passwordManagerController;
    private SecurityQuestionViewController securityQuestionViewController;
    private CategoryEditViewController categoryEditViewController;
    private SettingsViewController settingsViewController;
    private LoginViewController loginViewController;
    private MasterPasswordViewController masterPasswordViewController;
    @FXML private JFXTextField textFieldSearch;
    @FXML private JFXComboBox<Category> comboBoxCategorySelectionMain;
    @FXML private Spinner<Integer> spinnerCredentialsReminderDays;
    @FXML private JFXCheckBox checkBoxCredentialsUseReminder;

    public void init(){
        spinnerCredentialsReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,999));
        spinnerCredentialsReminderDays.setDisable(true);
    }

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
		try{
			/* Einstellungen */
			AnchorPane settingsPane = new AnchorPane();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Einstellungen.fxml"));
			settingsPane = fxmlLoader.load();
			settingsViewController = (SettingsViewController) fxmlLoader.getController();

			Stage settingsStage = new Stage();
			Scene settingsScene = new Scene(settingsPane);
			//settingsScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			settingsStage.setScene(settingsScene);
			settingsViewController.setStage(settingsStage);
			settingsViewController.setMainWindowViewController(this);
			settingsStage.show();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
    }

    public void onSearchClicked() {
    	
    	CredentialsController credentialsController = passwordManagerController.getCredentialsController();
    	CategoryController categoryController = passwordManagerController.getCategoryController();

    	//TODO: Add correct Method
    	Category category = comboBoxCategorySelectionMain.getValue();
    	Path categoryPath = categoryController.getPathForCategory(category);
    	String pattern = textFieldSearch.getText();
    	credentialsController.filterCredentials(categoryPath, pattern);
    }

    public void onAddCategoryClicked() {
		try{
			/* Kategorie hinzufügen */
			AnchorPane categoryEditPane = new AnchorPane();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Kategorie_anlegen-aendern.fxml"));
			categoryEditPane = fxmlLoader.load();
			categoryEditViewController = (CategoryEditViewController) fxmlLoader.getController();
		
			Stage categoryEditStage = new Stage();
			Scene categoryEditScene = new Scene(categoryEditPane);
			//categoryEditScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			categoryEditStage.setScene(categoryEditScene);
			categoryEditViewController.setStage(categoryEditStage);
			categoryEditStage.show();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}

    }

    public void onEditCategoryClicked() {
		try{
			/* Kategorie bearbeiten */
			AnchorPane categoryEditPane = new AnchorPane();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Kategorie_anlegen-aendern.fxml"));
			categoryEditPane = fxmlLoader.load();
			categoryEditViewController = (CategoryEditViewController) fxmlLoader.getController();
		
			Stage categoryEditStage = new Stage();
			Scene categoryEditScene = new Scene(categoryEditPane);
			//categoryEditScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			categoryEditStage.setScene(categoryEditScene);
			categoryEditViewController.setStage(categoryEditStage);
			categoryEditStage.show();
		}
		catch(Exception e) {
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

    public void onCheckBoxClicked(){
        boolean checkBoxSelected = checkBoxCredentialsUseReminder.isSelected();
        if(checkBoxSelected){
            spinnerCredentialsReminderDays.setDisable(false);
        } else{
            spinnerCredentialsReminderDays.setDisable(true);
        }
    }

    public void onAddSecurityQuestionClicked() {
		try{
			/* Sicherheitsfrage hinzufügen */
			AnchorPane securityQuestionAddPane = new AnchorPane();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Sicherheitsfrage-und-Antwort.fxml"));
			securityQuestionAddPane = fxmlLoader.load();
			securityQuestionViewController = (SecurityQuestionViewController) fxmlLoader.getController();

			Stage securityQuestionAddStage = new Stage();
			Scene securityQuestionAddScene = new Scene(securityQuestionAddPane);
			//securityQuestionAddScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			securityQuestionAddStage.setScene(securityQuestionAddScene);
			securityQuestionViewController.setStage(securityQuestionAddStage);
			securityQuestionAddStage.show();
		}
		catch(Exception e) {
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
