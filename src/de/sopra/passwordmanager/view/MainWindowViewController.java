package de.sopra.passwordmanager.view;

import com.jfoenix.controls.*;
import de.sopra.passwordmanager.controller.CategoryController;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.EncryptedString;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.EntryListOrderStrategy;
import de.sopra.passwordmanager.util.EntryListSelectionStrategy;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.util.dialog.TwoOptionConfirmation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainWindowViewController implements MainWindowAUI {

    private boolean visible = false;
    private PasswordManagerController passwordManagerController;
    private SecurityQuestionViewController securityQuestionViewController;
    private CategoryEditViewController categoryEditViewController;
    private SettingsViewController settingsViewController;
    private LoginViewController loginViewController;
    private MasterPasswordViewController masterPasswordViewController;
    private Timeline timeline;

    private CredentialsBuilder currentCredentials;

    @FXML
    private JFXTextField textFieldSearch, textFieldCredentialsName, textFieldCredentialsUserName, textFieldCredentialsWebsite;
    @FXML
    private TextArea textFieldCredentialsNotes;
    @FXML
    private JFXPasswordField passwordFieldCredentialsPassword;
    @FXML
    private Spinner<Integer> spinnerCredentialsReminderDays;
    @FXML
    private JFXListView<Credentials> listViewCredentialsList;
    @FXML
    private JFXToggleButton buttonCredentialsShowPassword;
    @FXML
    private JFXButton buttonAddCredentials, buttonRemoveCredentials, buttonCredentialsAddSecurityQuestion, buttonCredentialsRemoveSecurityQuestion, buttonAddCategoryMain, buttonRemoveCategoryMain, buttonSearch, buttonCredentialsGeneratePassword, buttonCredentialsCopy, buttonEditCredentials, buttonSaveCredentials, buttonCredentialsAddCategories, buttonSettings, buttonEditCategoryMain;

    @FXML
    private JFXComboBox<CategoryItem> comboBoxCategorySelectionMain;
    @FXML
    private JFXComboBox<SecurityQuestion> comboBoxCredentialsSecurityQuestion;

    @FXML
    private JFXCheckBox checkBoxCredentialsUseReminder;

    @FXML
    private ChoiceBox<Category> choiceBoxCredentialsCategories;

    @FXML
    private JFXProgressBar progressBarCredentialsCopyTimer;

    @FXML
    private Label labelCredentialsSecurityAnswer, lableCredentialsLastChanged, lableCredentialsCreated;

    public void init() {
        setDisable(true);

        spinnerCredentialsReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999));
        spinnerCredentialsReminderDays.setDisable(true);

        labelCredentialsSecurityAnswer.setVisible(false);

        //buttonCredentialsCopy.setDisable(true);
        progressBarCredentialsCopyTimer.toFront();
        buttonCredentialsCopy.toFront();
        progressBarCredentialsCopyTimer.setProgress(1);
        progressBarCredentialsCopyTimer.setStyle("-fx-accent: green");
        timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                progressBarCredentialsCopyTimer.setProgress(progressBarCredentialsCopyTimer.progressProperty().doubleValue() - 0.001);

                if (progressBarCredentialsCopyTimer.progressProperty().doubleValue() <= 0.0) {
                    buttonCredentialsCopy.setOpacity(1.0);
                }
            }
        }));
        timeline.setCycleCount(1000);

        refreshLists();

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
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onSearchClicked() {

        CredentialsController credentialsController = passwordManagerController.getCredentialsController();
        Path categoryPath = comboBoxCategorySelectionMain.getValue().getPath();

        String pattern = textFieldSearch.getText();
        credentialsController.filterCredentials(categoryPath, pattern);
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
            //categoryEditScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            categoryEditStage.setScene(categoryEditScene);
            categoryEditViewController.setStage(categoryEditStage);
            categoryEditViewController.setMainWindowViewController(this);
            categoryEditViewController.initComboBox();
            categoryEditStage.show();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void onEditCategoryClicked() {

        Path path = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem().getPath();
        if (Path.ROOT_CATEGORY_PATH.equals(path)) {
            showError("Ändern der Hauptkategorie nicht erlaubt");
            return;
        }
        try {
            /* Kategorie bearbeiten */
            AnchorPane categoryEditPane;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Kategorie_anlegen-aendern.fxml"));
            categoryEditPane = fxmlLoader.load();
            categoryEditViewController = fxmlLoader.getController();

            Stage categoryEditStage = new Stage();
            Scene categoryEditScene = new Scene(categoryEditPane);
            //categoryEditScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            categoryEditStage.setScene(categoryEditScene);
            categoryEditViewController.setStage(categoryEditStage);
            categoryEditViewController.setMainWindowViewController(this);
            categoryEditViewController.setCurrentlyEdited(path);
            categoryEditViewController.initComboBox();
            categoryEditStage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void onRemoveCategoryClicked() {
        CategoryController catController = passwordManagerController.getCategoryController();
        boolean removeCredentialsToo = false;

        TwoOptionConfirmation removeConfirmation = new TwoOptionConfirmation("Kategorie entfernen", null,
                "Nur die Kategorie oder die Kategorie mitsamt Inhalt löschen?") {
            @Override
            public void onCancel() {
                //Nix
            }
        };

        removeConfirmation.setAlertType(Alert.AlertType.NONE);
        removeConfirmation.setOption1("Nur Kategorie");
        removeConfirmation.setOption2("Mitsamt Inhalt");
        removeConfirmation.setRun1(() -> catController.removeCategory(comboBoxCategorySelectionMain.getValue().getPath(), false));
        removeConfirmation.setRun2(() -> catController.removeCategory(comboBoxCategorySelectionMain.getValue().getPath(), true));

        removeConfirmation.open();
    }

    public void onShowPasswordClicked() {
//        CredentialsController credController = passwordManagerController.getCredentialsController();
        visible = !visible;
        //TODO show Passwort Methode implementieren
        //credController.setPasswordShown(currentCredentials, visible);
    }

    public void onCopyPasswordClicked() {
        CredentialsController credController = passwordManagerController.getCredentialsController();
        credController.copyPasswordToClipboard(currentCredentials);
        buttonCredentialsCopy.setOpacity(0.5);
        timeline.stop();
        progressBarCredentialsCopyTimer.setProgress(1.0);
        timeline.playFromStart();
    }

    public void onGeneratePasswordClicked() {
        passwordManagerController.getUtilityController().generatePassword(currentCredentials);
    }

    public void onCheckBoxClicked() {
        boolean checkBoxSelected = checkBoxCredentialsUseReminder.isSelected();
        if (checkBoxSelected) {
            spinnerCredentialsReminderDays.setDisable(false);
        } else {
            spinnerCredentialsReminderDays.setDisable(true);
        }
    }

    public void onAddSecurityQuestionClicked() {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void onRemoveSecurityQuestionClicked() {
        CredentialsController credController = passwordManagerController.getCredentialsController();
        SecurityQuestion question = comboBoxCredentialsSecurityQuestion.getValue();

        //String question = comboBoxCredentialsSecurityQuestion.getValue();
        credController.removeSecurityQuestion(question, currentCredentials);
    }

    public void onAddCredentialsClicked() {
        //TODO check if correct
        currentCredentials = new CredentialsBuilder();
        setDisable(false);
    }

    public void onRemoveCredentialsClicked() {
        CredentialsController credController = passwordManagerController.getCredentialsController();
        Credentials oldCredentials = listViewCredentialsList.getSelectionModel().getSelectedItem();
        credController.removeCredentials(oldCredentials);
    }

    public void onStartEditCredentialsClicked() {
        setDisable(false);

    }


    public void onSaveCredentialsClicked() {
        setDisable(true);

        spinnerCredentialsReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999));
        spinnerCredentialsReminderDays.setDisable(true);

        CredentialsController credController = passwordManagerController.getCredentialsController();
        Credentials oldCredentials = listViewCredentialsList.getSelectionModel().getSelectedItem();

        String name = textFieldCredentialsName.getText();
        String userName = textFieldCredentialsUserName.getText();
        String password = passwordFieldCredentialsPassword.getText();
        String website = textFieldCredentialsWebsite.getText();
        String notes = textFieldCredentialsNotes.getText();
        currentCredentials.withName(name)
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .withNotes(notes);
        int changeReminderDays = spinnerCredentialsReminderDays.getValue();
        boolean addChangeReminderDays = checkBoxCredentialsUseReminder.isSelected();
        if (addChangeReminderDays) {
            currentCredentials.withChangeReminderDays(changeReminderDays);
        } else {
            //currentCredentials.withChangeReminderDays(); XXX: credentialsBuilder braucht Methode zum entfernen von Änderungswecker
        }


        List<Category> categories = new ArrayList<Category>();
        categories = choiceBoxCredentialsCategories.getItems();


        if (oldCredentials == null) {
            credController.addCredentials(currentCredentials, categories);
        } else {
            credController.updateCredentials(oldCredentials, currentCredentials, categories);
        }

    }

    public void onChooseCategoryClicked() {
        passwordManagerController.getCredentialsController().filterCredentials(comboBoxCategorySelectionMain.getValue().getPath(), textFieldSearch.getText());
    }

    public void onChooseQuestionClicked() {
//        CredentialsController credController = passwordManagerController.getCredentialsController();
//        Map<String, String> questions = currentCredentials.getSecurityQuestions();
//        
        //TODO Methode hinzufügen für decrypt von Question und Answer
        EncryptedString selectedQuestion = comboBoxCredentialsSecurityQuestion.getValue().getQuestion();
        labelCredentialsSecurityAnswer.setText("");
        labelCredentialsSecurityAnswer.setVisible(true);
    }


    @Override
    public void refreshLists() {
        //TODO

        /* Init category combobox */
        Map<Path, Category> cats = passwordManagerController.getPasswordManager().getRootCategory().createPathMap(new Path());

        comboBoxCategorySelectionMain.getItems().clear();
        cats.keySet().stream()
                .map(path -> new CategoryItem(path, cats.get(path)))
                .forEach(comboBoxCategorySelectionMain.getItems()::add);
    }

    @Override
    public void refreshListStrategies(EntryListSelectionStrategy selection, EntryListOrderStrategy order) {

    }

    @Override
    public void refreshEntry() {

    }

    @Override
    public void refreshEntryPasswordQuality(int quality) {

    }

    @Override
    public void showError(String error) {

    }

    private void setDisable(boolean disabled) {
        textFieldCredentialsName.setDisable(disabled);
        textFieldCredentialsUserName.setDisable(disabled);
        textFieldCredentialsWebsite.setDisable(disabled);
        textFieldCredentialsNotes.setDisable(disabled);
        passwordFieldCredentialsPassword.setDisable(disabled);

        buttonCredentialsAddSecurityQuestion.setDisable(disabled);
        buttonCredentialsRemoveSecurityQuestion.setDisable(disabled);
        buttonCredentialsGeneratePassword.setDisable(disabled);
        buttonSaveCredentials.setDisable(disabled);
        buttonCredentialsAddCategories.setDisable(disabled);
        checkBoxCredentialsUseReminder.setDisable(disabled);
        choiceBoxCredentialsCategories.setDisable(disabled);
    }

    CredentialsBuilder getCredentialsBuilder() {
        return currentCredentials;
    }
}
