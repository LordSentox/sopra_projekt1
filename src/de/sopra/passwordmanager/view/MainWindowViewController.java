package de.sopra.passwordmanager.view;

import com.jfoenix.controls.*;
import com.sun.javafx.collections.ObservableListWrapper;
import de.sopra.passwordmanager.controller.CategoryController;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.util.dialog.SimpleConfirmation;
import de.sopra.passwordmanager.util.dialog.SimpleDialog;
import de.sopra.passwordmanager.util.dialog.TwoOptionConfirmation;
import de.sopra.passwordmanager.util.strategy.EntryListOrderStrategy;
import de.sopra.passwordmanager.util.strategy.EntryListSelectionStrategy;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class MainWindowViewController implements MainWindowAUI {

    //controller attributes
    private PasswordManagerController passwordManagerController;
    private SecurityQuestionViewController securityQuestionViewController;
    private CategoryEditViewController categoryEditViewController;
    private SettingsViewController settingsViewController;
    private LoginViewController loginViewController;
    private MasterPasswordViewController masterPasswordViewController;

    //content and temporary attributes
    private Timeline timeline;
    private Credentials oldCredentials;
    private CredentialsBuilder currentCredentials;
    private EntryListSelectionStrategy selectionStrategy;
    private EntryListOrderStrategy orderStrategy;


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
    private JFXListView<CredentialsItem> listViewCredentialsList;

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

        listViewCredentialsList.getSelectionModel().selectedItemProperty().addListener((obs, oldText, newText) -> {
            onEntryChosen();
        });

        //Die ComboBox initialisieren - enthält zu Beginn nur die Root-Kategorie
        CategoryItem rootCategoryItem = new CategoryItem(Path.ROOT_CATEGORY_PATH, passwordManagerController.getPasswordManager().getRootCategory());
        comboBoxCategorySelectionMain.getItems().add(rootCategoryItem);
        comboBoxCategorySelectionMain.getSelectionModel().select(rootCategoryItem);

        //Die Strategie initilisieren - sind zu Beginn Identitätsbeziehungen, d.h. ändern nichts am Input
        selectionStrategy = identity -> identity; //es wird keine Auswahl getroffen
        orderStrategy = identity -> identity; //es wird nicht sortiert

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
            AnchorPane settingsPane;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Einstellungen.fxml"));
            settingsPane = fxmlLoader.load();
            settingsViewController = fxmlLoader.getController();

            Stage settingsStage = new Stage();
            Scene settingsScene = new Scene(settingsPane);
            settingsScene.getStylesheets().add(getClass().getResource("../application/application.css").toExternalForm());
            settingsStage.setScene(settingsScene);
            settingsViewController.setStage(settingsStage);
            settingsViewController.setMainWindowViewController(this);
            settingsStage.show();
        } catch (Exception e) {
            showError(e);
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
            categoryEditViewController.initComboBox();
            categoryEditStage.show();

        } catch (Exception e) {
            showError(e);
            throw new RuntimeException(e);
        }

    }

    public void onEditCategoryClicked() {

        try {

            CategoryItem selectedItem = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
            Path path = selectedItem.getPath();
            if (Path.ROOT_CATEGORY_PATH.equals(path)) {
                showError("Das Ändern der Hauptkategorie ist nicht erlaubt.");
                return;
            }

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
            showError(e);
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
        updateCredentialsBuilderCopy();
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
            showError(e);
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
        oldCredentials = listViewCredentialsList.getSelectionModel().getSelectedItem().getCredentials();
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

        updateCredentialsBuilderCopy();

        List<Category> categories = new ArrayList<>();
        categories.add(passwordManagerController.getPasswordManager().getRootCategory());
        //categories.add(comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem().getCategory());
        //TODO auslesen aus choiceBox und an liste anhängen
        //categories = choiceBoxCredentialsCategories.getItems();


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
        CredentialsItem selectedEntry = listViewCredentialsList.getSelectionModel().getSelectedItem();
        int index = listViewCredentialsList.getFocusModel().getFocusedIndex();
        if (buttonEditCredentials.isDisabled()) {
            SimpleConfirmation confirmation = new SimpleConfirmation("Änderung verwerfen?",
                    "Zur Zeit wird ein Eintrag bearbeitet",
                    "Wollen Sie wirklich abbrechen? \n Alle Änderungen werden gelöscht.") {
                @Override
                public void onSuccess() {
                    //Änderungen nicht übernehmen
                    oldCredentials = selectedEntry.getCredentials();
                    currentCredentials = selectedEntry.getNewBuilder(passwordManagerController.getUtilityController());
                    setDisable(true);
                    listViewCredentialsList.getFocusModel().focus(-1);
                    refreshEntry();
                }

                @Override
                public void onCancel() {
                    //nicht löschen
                    listViewCredentialsList.getFocusModel().focus(-1);
                }
            };
            confirmation.setAlertType(AlertType.CONFIRMATION);
            confirmation.open();
        } else {
            oldCredentials = selectedEntry.getCredentials();
            currentCredentials = selectedEntry.getNewBuilder(passwordManagerController.getUtilityController());
            refreshEntry();
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

        //geänderte Daten aus dem Model beziehen
        Map<Path, Category> cats = passwordManagerController.getPasswordManager().getRootCategory().createPathMap(new Path());

        //Alle Kategorien in die Kombobox einpflegen
        CategoryItem chosenCat = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
        comboBoxCategorySelectionMain.getItems().clear();
        cats.keySet().stream()
                .map(path -> new CategoryItem(path, cats.get(path)))
                .forEach(comboBoxCategorySelectionMain.getItems()::add);
        List<CategoryItem> items = comboBoxCategorySelectionMain.getItems();
        if (!items.stream().anyMatch(item -> item.getPath().equals(chosenCat.getPath()))) {
            CategoryItem selected = items.stream().filter(item -> item.getPath().equals(Path.ROOT_CATEGORY_PATH)).findAny().get();
            comboBoxCategorySelectionMain.getSelectionModel().select(selected);
        } else {
            CategoryItem selected = items.stream().filter(item -> item.getPath().equals(chosenCat.getPath())).findAny().get();
            comboBoxCategorySelectionMain.getSelectionModel().select(selected);
        }

        //Inhalt der Kategorie in Liste anzeigen
        CategoryItem chosenCat2 = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
        Collection<Credentials> credentials = chosenCat2.getCategory().getCredentials();
        if (!credentials.isEmpty()) {
            ObservableList<CredentialsItem> credsToShow = new ObservableListWrapper<>(
                    credentials.stream().map(CredentialsItem::new).collect(Collectors.toList()));
            listViewCredentialsList.setItems(credsToShow);
        } else {
            //TODO
            listViewCredentialsList.setItems(new ObservableListWrapper<>(Collections.emptyList()));
            oldCredentials = null;
            currentCredentials = null;
            refreshEntry();
        }

    }

    @Override
    public void refreshListStrategies(EntryListSelectionStrategy selection, EntryListOrderStrategy order) {
        selectionStrategy = selection == null ? selectionStrategy : selection;
        orderStrategy = order == null ? orderStrategy : order;
        refreshLists();
    }

    @Override
    public void refreshEntry() {
        if (currentCredentials == null)
            currentCredentials = new CredentialsBuilder();
        textFieldCredentialsName.setText(currentCredentials.getName());
        textFieldCredentialsUserName.setText(currentCredentials.getUserName());
        passwordFieldCredentialsPassword.setText(currentCredentials.getPassword());
        textFieldCredentialsWebsite.setText(currentCredentials.getWebsite());
        textFieldCredentialsNotes.setText(currentCredentials.getNotes());
        Integer changeReminderDays = currentCredentials.getChangeReminderDays();
        spinnerCredentialsReminderDays.getValueFactory().setValue(changeReminderDays != null ? changeReminderDays : 1);
        checkBoxCredentialsUseReminder.setSelected(changeReminderDays != null);
    }

    @Override
    public void refreshEntryPasswordQuality(int quality) {
        //XXX change quality to double between 0 and 1
        progressBarCredentialsQuality.setProgress((double) quality / 100);
    }

    public void showError(Exception exception) {
        Throwable throwable = exception;
        while (throwable.getCause() != null)
            throwable = throwable.getCause();
        StringBuilder builder = new StringBuilder(exception.toString());
        int count = 0;
        for (StackTraceElement trace : exception.getStackTrace()) {
            count++;
            if (count <= 15)
                builder.append("\n" + trace.toString());
        }
        if (count > 15)
            builder.append("\n...and " + (count - 25) + " more...");
        showError(builder.toString());
    }

    @Override
    public void showError(String error) {
        SimpleDialog dialog = new SimpleDialog("Ein Fehler ist aufgetreten!",
                "Warnung! Es ist ein Fehler aufgetreten.", error);
        dialog.setAlertType(AlertType.ERROR);
        dialog.setStyle(StageStyle.UTILITY);
        dialog.open();
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

    private void updateCredentialsBuilderCopy() {
        String name = textFieldCredentialsName.getText();
        String userName = textFieldCredentialsUserName.getText();
        String password = passwordFieldCredentialsPassword.getText();
        String website = textFieldCredentialsWebsite.getText();
        String notes = textFieldCredentialsNotes.getText();
        currentCredentials
                .withName(name)
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .withNotes(notes);
        Integer changeReminderDays = spinnerCredentialsReminderDays.getValue();
        boolean addChangeReminderDays = checkBoxCredentialsUseReminder.isSelected();
        if (addChangeReminderDays) {
            currentCredentials.withChangeReminderDays(changeReminderDays);
        } else {
            currentCredentials.withChangeReminderDays(null);
        }
        currentCredentials.withChangeReminderDays(changeReminderDays);
    }
}
