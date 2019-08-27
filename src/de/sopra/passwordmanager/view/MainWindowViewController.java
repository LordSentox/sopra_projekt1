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
import de.sopra.passwordmanager.util.EntryListOrderStrategy;
import de.sopra.passwordmanager.util.EntryListSelectionStrategy;
import de.sopra.passwordmanager.util.Path;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

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

        //ComboBoxCategorySelection soll immer die rootCategory haben.
        CategoryItem rootCategoryItem = new CategoryItem(Path.ROOT_CATEGORY_PATH, passwordManagerController.getPasswordManager().getRootCategory());
        comboBoxCategorySelectionMain.getItems().add(rootCategoryItem);
        comboBoxCategorySelectionMain.getSelectionModel().select(rootCategoryItem);

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
        boolean removeCredentialsToo = false;
        Alert alertDialog = new Alert(AlertType.CONFIRMATION);

        ButtonType buttonTypeYes = new ButtonType("Ja");
        ButtonType buttonTypeNo = new ButtonType("Nein");
        ButtonType buttonTypeCancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);

        alertDialog.setHeaderText("Alle zur Kategorie gehörigen Daten ebenfalls Löschen?");
        alertDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
        alertDialog.showAndWait();
        ButtonType result = alertDialog.getResult();
        if (result == buttonTypeYes) {
            removeCredentialsToo = true;
            System.out.println("mit löschen");
        } else if (result == buttonTypeNo) {
            removeCredentialsToo = false;
            System.out.println("nicht mit löschen");
        } else {
            System.out.println("abbrechen");
            return;
        }
        CategoryItem selectedCat = comboBoxCategorySelectionMain.getValue();
        Path categoryPath = catController.getPathForCategory(selectedCat.getCategory());

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

        catController.removeCategory(categoryPath, removeCredentialsToo);
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
        setBuilderFromEntry();
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
        Credentials selectedEntry = listViewCredentialsList.getSelectionModel().getSelectedItem();
        int index = listViewCredentialsList.getFocusModel().getFocusedIndex();
        if (buttonEditCredentials.isDisabled()) {
            Alert alertDialog = new Alert(AlertType.CONFIRMATION);

            ButtonType buttonTypeYes = new ButtonType("Ja");
            ButtonType buttonTypeNo = new ButtonType("Abbrechen");

            alertDialog.setHeaderText("Zur Zeit wird ein Eintrag bearbeitet");
            alertDialog.setContentText("Wollen Sie wirklich abbrechen? \n Alle Änderungen werden gelöscht.");
            alertDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            alertDialog.showAndWait();
            ButtonType result = alertDialog.getResult();

            if (result == buttonTypeYes) {
                oldCredentials = selectedEntry;
                currentCredentials = new CredentialsBuilder(oldCredentials, passwordManagerController.getUtilityController());
                System.out.println("Änderung abbrechen");
            } else if (result == buttonTypeNo) {
                listViewCredentialsList.getFocusModel().focus(index);
                System.out.println("nicht mit löschen");
                return;
            }
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
        if (!chosenCat2.getCategory().getCredentials().isEmpty()) {
            ObservableList<Credentials> credsToShow = new ObservableListWrapper<>(new LinkedList<>(chosenCat2.getCategory().getCredentials()));
            listViewCredentialsList.setItems(credsToShow);
        } else {
            //TODO
            listViewCredentialsList.setItems(new ObservableListWrapper<>(Collections.emptyList()));
            System.out.println("wir sind im else");
        }

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
        spinnerCredentialsReminderDays.getValueFactory().setValue(currentCredentials.getChangeReminderDays());
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
