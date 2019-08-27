package de.sopra.passwordmanager.view;

import com.jfoenix.controls.*;
import de.sopra.passwordmanager.controller.CategoryController;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.EntryListOrderStrategy;
import de.sopra.passwordmanager.util.EntryListSelectionStrategy;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.util.dialog.SimpleConfirmation;
import de.sopra.passwordmanager.util.dialog.TwoOptionConfirmation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainWindowViewController implements MainWindowAUI {

    private PasswordManagerController passwordManagerController;
    private SecurityQuestionViewController securityQuestionViewController;
    private CategoryEditViewController categoryEditViewController;
    private SettingsViewController settingsViewController;
    private LoginViewController loginViewController;
    private MasterPasswordViewController masterPasswordViewController;
    private Timeline timeline;
    private Credentials oldCredentials;

    private CredentialsBuilder currentCredentials;

    @FXML
    private JFXTextField textFieldSearch, textFieldCredentialsName, textFieldCredentialsUserName, textFieldCredentialsWebsite;
    @FXML
    private JFXPasswordField passwordFieldCredentialsPassword;
    @FXML
    private JFXTextField textFieldCredentialsPassword;

    @FXML
    private TextArea textFieldCredentialsNotes;


    @FXML
    private Spinner<Integer> spinnerCredentialsReminderDays;

    @FXML
    private JFXListView<Credentials> listViewCredentialsList;

    @FXML
    private JFXToggleNode buttonCredentialsShowPassword;

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
    private JFXProgressBar progressBarCredentialsQuality;

    @FXML
    private Label labelCredentialsSecurityAnswer, lableCredentialsLastChanged, lableCredentialsCreated;


    public void init() {
        setDisable(true);

        spinnerCredentialsReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999));
        spinnerCredentialsReminderDays.setDisable(true);

        labelCredentialsSecurityAnswer.setVisible(false);

        buttonCredentialsShowPassword.setDisable(true);
        buttonCredentialsCopy.setDisable(true);
        progressBarCredentialsCopyTimer.toFront();
        buttonCredentialsCopy.toFront();

        //Timer initialisieren mit Farbe, vollem Balken, als unsichtbar und mit 10-Sekunden-Ablauf-Balken
        progressBarCredentialsCopyTimer.setOpacity(0.0);
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

        textFieldCredentialsPassword.setManaged(false);
        textFieldCredentialsPassword.setVisible(false);
        // Bind properties. Ändere textFieldCredentialsPassword und passwordFieldCredentialsPassword
        // visibility und managability properties gleichzeitig, wenn buttonCredentialsShowPassword geklickt
        // So ist nur eine Komponente (textFieldCredentialsPassword oder passwordFieldCredentialsPassword)
        // sichtbar
        textFieldCredentialsPassword.managedProperty().bind(buttonCredentialsShowPassword.selectedProperty());
        textFieldCredentialsPassword.visibleProperty().bind(buttonCredentialsShowPassword.selectedProperty());

        passwordFieldCredentialsPassword.managedProperty().bind(buttonCredentialsShowPassword.selectedProperty().not());
        passwordFieldCredentialsPassword.visibleProperty().bind(buttonCredentialsShowPassword.selectedProperty().not());

        //textFieldCredentialsPassword und passwordFieldCredentialsPassword erhalten beide den gleichen Text.
        textFieldCredentialsPassword.textProperty().bindBidirectional(passwordFieldCredentialsPassword.textProperty());

        textFieldCredentialsPassword.textProperty().addListener((obs, oldText, newText) -> {
            onCredentialsPasswordChanged();
        });

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

    CredentialsBuilder getCredentialsBuilder() {
        return currentCredentials;
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
            settingsScene.getStylesheets().add(getClass().getResource("../application/application.css").toExternalForm());
            settingsStage.setScene(settingsScene);
            settingsViewController.setStage(settingsStage);
            settingsViewController.setMainWindowViewController(this);
            settingsStage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onSearchClicked() {

        //FIXME
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
            categoryEditScene.getStylesheets().add(getClass().getResource("../application/application.css").toExternalForm());
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
            categoryEditScene.getStylesheets().add(getClass().getResource("../application/application.css").toExternalForm());
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
        TwoOptionConfirmation removeConfirmation = new TwoOptionConfirmation("Kategorie entfernen", null,
                "Nur die Kategorie oder die Kategorie mitsamt Inhalt löschen?");

        removeConfirmation.setAlertType(AlertType.CONFIRMATION);
        removeConfirmation.setOption1("Nur Kategorie");
        removeConfirmation.setOption2("Mitsamt Inhalt");
        removeConfirmation.setRun1(() -> catController.removeCategory(comboBoxCategorySelectionMain.getValue().getPath(), false));
        removeConfirmation.setRun2(() -> catController.removeCategory(comboBoxCategorySelectionMain.getValue().getPath(), true));

        removeConfirmation.open();
    }

    public void onCopyPasswordClicked() {
        CredentialsController credController = passwordManagerController.getCredentialsController();
        credController.copyPasswordToClipboard(currentCredentials);
        buttonCredentialsCopy.setOpacity(0.5);
        timeline.stop();
        progressBarCredentialsCopyTimer.setOpacity(1.0);
        progressBarCredentialsCopyTimer.setProgress(1.0);
        timeline.playFromStart();
    }

    public void onGeneratePasswordClicked() {
        passwordManagerController.getUtilityController().generatePassword(currentCredentials);
    }

    public void onCheckBoxClicked() {
        boolean checkBoxSelected = checkBoxCredentialsUseReminder.isSelected();
        spinnerCredentialsReminderDays.setDisable(!checkBoxSelected);
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

            securityQuestionAddScene.getStylesheets().add(getClass().getResource("../application/application.css").toExternalForm());
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

        //FIXME
        //String question = comboBoxCredentialsSecurityQuestion.getValue();
        credController.removeSecurityQuestion(question, currentCredentials);
    }

    public void onAddCredentialsClicked() {
        oldCredentials = null;
        //TODO check if correct
        listViewCredentialsList.getFocusModel().focus(-1);
        currentCredentials = new CredentialsBuilder();
        setDisable(false);
        buttonCredentialsCopy.setDisable(false);
        buttonCredentialsShowPassword.setDisable(false);
        refreshEntry();
    }

    public void onRemoveCredentialsClicked() {
        CredentialsController credController = passwordManagerController.getCredentialsController();
        oldCredentials = listViewCredentialsList.getSelectionModel().getSelectedItem();
        listViewCredentialsList.getFocusModel().focus(-1);
        credController.removeCredentials(oldCredentials);
        oldCredentials = null;
        currentCredentials = new CredentialsBuilder();
        refreshEntry();
    }

    public void onStartEditCredentialsClicked() {
        setDisable(false);
    }


    public void onSaveCredentialsClicked() {
        setDisable(true);

        spinnerCredentialsReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999));
        spinnerCredentialsReminderDays.setDisable(true);

        CredentialsController credController = passwordManagerController.getCredentialsController();
        oldCredentials = listViewCredentialsList.getSelectionModel().getSelectedItem();

        setBuilderFromEntry();

        List<Category> categories = new ArrayList<Category>();
        categories.add(passwordManagerController.getPasswordManager().getRootCategory());
        categories = choiceBoxCredentialsCategories.getItems();


        if (oldCredentials == null) {
            credController.addCredentials(currentCredentials, categories);
        } else {
            credController.updateCredentials(oldCredentials, currentCredentials, categories);
        }

    }

    public void onChooseCategoryClicked() {
        CredentialsController credController = passwordManagerController.getCredentialsController();
        CategoryController categoryController = passwordManagerController.getCategoryController();

        CategoryItem category = comboBoxCategorySelectionMain.getValue();
        Path categoryPath = categoryController.getPathForCategory(category.getCategory());
        String pattern = textFieldSearch.getText();
        credController.filterCredentials(categoryPath, pattern);
    }

    public void onChooseQuestionClicked() {
        CredentialsController credController = passwordManagerController.getCredentialsController();
        Map<String, String> questions = currentCredentials.getSecurityQuestions();

        //TODO Methode hinzufügen für decrypt von Question und Answer
        //SecurityQuestion chosenQuestion = credController.
        SecurityQuestion selectedQuestion = comboBoxCredentialsSecurityQuestion.getValue();
        selectedQuestion.getQuestion();
        String answer = "";
        labelCredentialsSecurityAnswer.setText(answer);
        labelCredentialsSecurityAnswer.setVisible(true);
    }

    public void onEntryChosen() {
        Credentials selectedEntry = listViewCredentialsList.getSelectionModel().getSelectedItem();
        int index = listViewCredentialsList.getFocusModel().getFocusedIndex();
        if (buttonEditCredentials.isDisabled()) {

            SimpleConfirmation confirmation = new SimpleConfirmation("Änderung verwerfen?",
                    "Zur Zeit wird ein Eintrag bearbeitet",
                    "Wollen Sie wirklich abbrechen? \n Alle Änderungen werden gelöscht.") {
                @Override
                public void onSuccess() {
                    oldCredentials = selectedEntry;
                    currentCredentials = new CredentialsBuilder(oldCredentials, passwordManagerController.getUtilityController());
                    System.out.println("Änderung abbrechen");
                }

                @Override
                public void onCancel() {
                    listViewCredentialsList.getFocusModel().focus(index);
                    System.out.println("nicht mit löschen");
                }
            };
            confirmation.setAlertType(AlertType.WARNING);
            confirmation.open();
        }

    }

    public void onCredentialsPasswordChanged() {
        String password = passwordFieldCredentialsPassword.getText();
        if (password != null) {
            currentCredentials.withPassword(password);
            passwordManagerController.checkQuality(currentCredentials);
        }
    }


    @Override
    public void refreshLists() {
        //TODO
        //FIXME

        /* Init category combobox */
        Map<Path, Category> cats = passwordManagerController.getPasswordManager().getRootCategory().createPathMap(new Path());

        comboBoxCategorySelectionMain.getItems().clear();
        cats.keySet().stream()
                .map(path -> new CategoryItem(path, cats.get(path)))
                .forEach(comboBoxCategorySelectionMain.getItems()::add);
        //TODO geht das so?
        CategoryItem chosenCat = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
        listViewCredentialsList.setItems((ObservableList<Credentials>) chosenCat.getCategory().getAllCredentials());
    }

    @Override
    public void refreshListStrategies(EntryListSelectionStrategy selection, EntryListOrderStrategy order) {

    }

    @Override
    public void refreshEntry() {
        textFieldCredentialsName.setText(currentCredentials.getName());
        textFieldCredentialsUserName.setText(currentCredentials.getUserName());
        passwordFieldCredentialsPassword.setText(currentCredentials.getPassword());
        textFieldCredentialsWebsite.setText(currentCredentials.getWebsite());
        textFieldCredentialsNotes.setText(currentCredentials.getNotes());
        //FIXME: richtige Mehode um reminderDays zu übernehmen
        //spinnerCredentialsReminderDays.setValue(currentCredentials.getChangeReminderDays());
        checkBoxCredentialsUseReminder.setSelected(currentCredentials.getChangeReminderDays() != null);
    }

    @Override
    public void refreshEntryPasswordQuality(int quality) {
        //XXX change quality to double between 0 and 1
        progressBarCredentialsQuality.setProgress((double) quality / 100);
    }

    @Override
    public void showError(String error) {
        Alert alertDialog = new Alert(AlertType.CONFIRMATION);

        ButtonType buttonTypeYes = new ButtonType("OK");

        alertDialog.setHeaderText("Achtung! es ist ein unerwarteter Fehler aufgetreten");
        alertDialog.setContentText(error);
        alertDialog.getButtonTypes().setAll(buttonTypeYes);
        alertDialog.show();
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
        buttonEditCredentials.setDisable(!disabled);
        buttonCredentialsAddCategories.setDisable(disabled);
        checkBoxCredentialsUseReminder.setDisable(disabled);
        choiceBoxCredentialsCategories.setDisable(disabled);
    }

    private void setBuilderFromEntry() {
        String name = textFieldCredentialsName.getText();
        String userName = textFieldCredentialsUserName.getText();
        String password = passwordFieldCredentialsPassword.getText();
        String website = textFieldCredentialsWebsite.getText();
        String notes = textFieldCredentialsNotes.getText();
        setBuilderNameField(name);
        setBuilderUserNameField(userName);
        setBuilderPasswordField(password);
        setBuilderWebsiteField(website);
        setBuilderNotesField(notes);
        Integer changeReminderDays = spinnerCredentialsReminderDays.getValue();
        boolean addChangeReminderDays = checkBoxCredentialsUseReminder.isSelected();
        if (addChangeReminderDays) {
            currentCredentials.withChangeReminderDays(changeReminderDays);
        } else {
            currentCredentials.withChangeReminderDays(null);
        }
        setBuilderReminderField(changeReminderDays);
    }

    private void setBuilderNameField(String name) {
        currentCredentials.withName(name);
    }

    private void setBuilderUserNameField(String userName) {
        currentCredentials.withUserName(userName);
    }

    private void setBuilderPasswordField(String password) {
        currentCredentials.withPassword(password);
    }

    private void setBuilderWebsiteField(String website) {
        currentCredentials.withWebsite(website);
    }

    private void setBuilderNotesField(String notes) {
        currentCredentials.withNotes(notes);
    }

    private void setBuilderReminderField(Integer reminder) {
        currentCredentials.withChangeReminderDays(reminder);
    }
}
