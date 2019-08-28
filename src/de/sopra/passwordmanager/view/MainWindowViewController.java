package de.sopra.passwordmanager.view;

import com.jfoenix.controls.*;
import com.sun.javafx.collections.ObservableListWrapper;
import de.sopra.passwordmanager.controller.CategoryController;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.CredentialsItem;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.util.PatternSyntax;
import de.sopra.passwordmanager.util.strategy.AlphabeticOrderStrategy;
import de.sopra.passwordmanager.util.strategy.EntryListOrderStrategy;
import de.sopra.passwordmanager.util.strategy.EntryListSelectionStrategy;
import de.sopra.passwordmanager.util.strategy.SelectAllStrategy;
import de.sopra.passwordmanager.view.dialog.SimpleConfirmation;
import de.sopra.passwordmanager.view.dialog.SimpleDialog;
import de.sopra.passwordmanager.view.dialog.TwoOptionConfirmation;
import de.sopra.passwordmanager.view.multibox.MultiSelectionComboBox;
import de.sopra.passwordmanager.view.multibox.SelectableComboItem;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static de.sopra.passwordmanager.view.MainWindowViewController.WindowState.*;

public class MainWindowViewController extends AbstractViewController implements MainWindowAUI {

    public static final UnaryOperator<TextFormatter.Change> SPINNER_FILTER = new UnaryOperator<TextFormatter.Change>() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        @Override
        public Change apply(Change c) {
            if (c.isContentChange()) {
                ParsePosition parsePosition = new ParsePosition(0);
                // NumberFormat evaluates the beginning of the text
                format.parse(c.getControlNewText(), parsePosition);
                if (parsePosition.getIndex() == 0 ||
                        parsePosition.getIndex() < c.getControlNewText().length()) {
                    // reject parsing the complete text failed
                    return null;
                }
                //Länge begrenzen
                if (c.getControlNewText().length() > 3) {
                    return null;
                }
                Integer number = Integer.parseInt(c.getControlNewText());
                if (number < 1)
                    return null;
            }
            return c;
        }
    };

    private final TextFormatter<Integer> spinnerTextFormatter =
            new TextFormatter<Integer>(new IntegerStringConverter(), 1, SPINNER_FILTER);

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
    private WindowState state = UNSET;

    enum WindowState {
        UNSET,
        VIEW_ENTRY,
        CREATING_NEW_ENTRY,
        START_EDITING_ENTRY,
        EDITED_ENTRY;

        public boolean match(WindowState... states) {
            for (WindowState state : states) {
                if (state == this)
                    return true;
            }
            return false;
        }

    }

    //region fx-members

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
    private JFXComboBox<String> comboBoxCredentialsSecurityQuestion;

    @FXML
    private JFXCheckBox checkBoxCredentialsUseReminder;

    @FXML
    private MultiSelectionComboBox<CategoryItem> choiceBoxCredentialsCategories;

    @FXML
    private JFXProgressBar progressBarCredentialsCopyTimer;
    @FXML
    private JFXProgressBar progressBarCredentialsQuality;

    @FXML
    private Label labelCredentialsSecurityAnswer, labelCredentialsLastChanged, labelCredentialsCreated;

    //endregion

    public void init() {
        currentCredentials = new CredentialsBuilder();
        updateView();

        spinnerCredentialsReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999));
        spinnerCredentialsReminderDays.setDisable(true);
        spinnerCredentialsReminderDays.setEditable(true);
        spinnerCredentialsReminderDays.getEditor().setTextFormatter(spinnerTextFormatter);

        labelCredentialsSecurityAnswer.setVisible(false);

        progressBarCredentialsCopyTimer.toFront();
        buttonCredentialsCopy.toFront();

        //Timer initialisieren mit Farbe, vollem Balken, als unsichtbar und mit 10-Sekunden-Ablauf-Balken
        progressBarCredentialsCopyTimer.setOpacity(0.0);
        progressBarCredentialsCopyTimer.setProgress(1);
        progressBarCredentialsCopyTimer.setStyle("-fx-accent: green");
        timeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            progressBarCredentialsCopyTimer.setProgress(progressBarCredentialsCopyTimer.progressProperty().doubleValue() - 0.001);
            if (progressBarCredentialsCopyTimer.progressProperty().doubleValue() <= 0.0) {
                buttonCredentialsCopy.setOpacity(1.0);
            }
        }));
        timeline.setCycleCount(1000);
        timeline.setOnFinished(event -> passwordManagerController.getCredentialsController().clearPasswordFromClipboard(currentCredentials));

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

        textFieldCredentialsName.textProperty().addListener((obs, oldText, newText) -> {
            if (oldText == null || newText == null) return;
            if (Math.abs(oldText.length() - newText.length()) <= 1) {
                currentCredentials.withName(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
            }
        });
        textFieldCredentialsUserName.textProperty().addListener((obs, oldText, newText) -> {
            if (oldText == null || newText == null) return;
            if (Math.abs(oldText.length() - newText.length()) <= 1) {
                currentCredentials.withUserName(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
                passwordManagerController.checkQuality(currentCredentials);
            }
        });
        textFieldCredentialsWebsite.textProperty().addListener((obs, oldText, newText) -> {
            if (oldText == null || newText == null) return;
            if (Math.abs(oldText.length() - newText.length()) <= 1) {
                currentCredentials.withWebsite(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
            }
        });
        textFieldCredentialsPassword.textProperty().addListener((obs, oldText, newText) -> {
            if (oldText == null || newText == null) return;
            if (Math.abs(oldText.length() - newText.length()) <= 1) {
                currentCredentials.withPassword(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
                passwordManagerController.checkQuality(currentCredentials);
            }
        });
        textFieldCredentialsNotes.textProperty().addListener((obs, oldText, newText) -> {
            if (oldText == null || newText == null) return;
            if (Math.abs(oldText.length() - newText.length()) <= 1) {
                currentCredentials.withNotes(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
            }
        });

        listViewCredentialsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue))
                onEntryChosen();
        });

        comboBoxCategorySelectionMain.getSelectionModel().selectedItemProperty().addListener((obs, oldText, newText) -> {
            refreshEntryListWhenCategoryChosen();
        });

        //Die ComboBox initialisieren - enthält zu Beginn nur die Root-Kategorie
        CategoryItem rootCategoryItem = new CategoryItem(Path.ROOT_CATEGORY_PATH, passwordManagerController.getPasswordManager().getRootCategory());
        comboBoxCategorySelectionMain.getItems().add(rootCategoryItem);
        comboBoxCategorySelectionMain.getSelectionModel().select(rootCategoryItem);

        //Die Strategie initilisieren - sind zu Beginn Identitätsbeziehungen, d.h. ändern nichts am Input
        selectionStrategy = new SelectAllStrategy(); //es wird keine Auswahl getroffen
        orderStrategy = new AlphabeticOrderStrategy(); //es wird nicht sortiert

        textFieldCredentialsNotes.setWrapText(true);

    }

    //region controller
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
    //endregion

    public CredentialsBuilder getCredentialsBuilder() {
        return currentCredentials;
    }

    //region action handler
    public void onSettingsClicked() {
        //STATE - soll unabhängig funktionieren
        try {
            /* Einstellungen öffnen */
            AnchorPane settingsPane;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Einstellungen.fxml"));
            settingsPane = fxmlLoader.load();
            settingsViewController = fxmlLoader.getController();

            Stage settingsStage = new Stage();
            Scene settingsScene = new Scene(settingsPane);
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(stage);
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
        //STATE - soll unabhängig funktionieren
        CredentialsController credentialsController = passwordManagerController.getCredentialsController();
        String pattern = textFieldSearch.getText();
        //mit neuem Pattern filtern, refresh erfolgt über controller
        credentialsController.filterCredentials(new PatternSyntax(pattern));
    }

    private void openCategoryEditWindow() throws IOException {
        categoryEditViewController = openModal("../view/Kategorie_anlegen-aendern.fxml",
                CategoryEditViewController.class, CategoryEditViewController::initComboBox);
    }

    public void onAddCategoryClicked() {
        //STATE - soll unabhängig funktionieren
        try {
            /* Kategorie hinzufügen - leeres Fenster öffnen */
            openCategoryEditWindow();
        } catch (Exception e) {
            showError(e);
            throw new RuntimeException(e);
        }
    }

    public void onEditCategoryClicked() {
        //STATE - soll nur in UNSET und VIEW_ENTRY funktionieren
        if (!state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst die Kategorien aktuell nicht editieren");
            return;
        }
        try {
            CategoryItem selectedItem = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
            Path path = selectedItem.getPath();
            if (Path.ROOT_CATEGORY_PATH.equals(path)) {
                showError("Das Ändern der Hauptkategorie ist nicht erlaubt.");
                return;
            }
            openCategoryEditWindow();
            //aktuelle Auswahl zur Bearbeitung angeben
            categoryEditViewController.setCurrentlyEdited(path);
        } catch (Exception e) {
            showError(e);
            throw new RuntimeException(e);
        }

    }

    public void onRemoveCategoryClicked() {
        //STATE - soll nur in UNSET und VIEW_ENTRY funktionieren
        if (!state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst die Kategorie aktuell nicht löschen");
            return;
        }

        CategoryController catController = passwordManagerController.getCategoryController();

        //Sicherheitsabfrage
        TwoOptionConfirmation removeConfirmation = new TwoOptionConfirmation("Kategorie entfernen", null,
                "Nur die Kategorie oder die Kategorie mitsamt Inhalt löschen?");

        removeConfirmation.setAlertType(AlertType.CONFIRMATION);
        removeConfirmation.setOption1("Nur Kategorie");
        removeConfirmation.setOption2("Mitsamt Inhalt");
        //Das eigentliche Entfernen wird über den Controller hier in die Runnables gesetzt
        removeConfirmation.setRun1(() -> catController.removeCategory(comboBoxCategorySelectionMain.getValue().getPath(), false));
        removeConfirmation.setRun2(() -> catController.removeCategory(comboBoxCategorySelectionMain.getValue().getPath(), true));

        removeConfirmation.open();
    }

    public void onCopyPasswordClicked() {
        //STATE - soll NICHT in UNSET funktionieren
        if (state.match(UNSET)) {
            showError("Du kannst aktuell kein Password kopieren");
            return;
        }

        CredentialsController credController = passwordManagerController.getCredentialsController();
        credController.copyPasswordToClipboard(currentCredentials);
        buttonCredentialsCopy.getStyleClass().add("copy-button");
        timeline.stop();
        progressBarCredentialsCopyTimer.setOpacity(1.0);
        progressBarCredentialsCopyTimer.setProgress(1.0);
        timeline.playFromStart();
    }

    public void onGeneratePasswordClicked() {
        //STATE - soll NICHT in UNSET und VIEW_ENTRY funktionieren
        if (state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell kein Password generieren");
            return;
        }
        //refresh erfolgt von controller aus
        passwordManagerController.getUtilityController().generatePassword(currentCredentials);
    }

    public void onCheckBoxClicked() {
        //STATE - soll NICHT in UNSET und VIEW_ENTRY funktionieren
        if (state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell den Änderungswecker nicht ändern");
            return;
        }

        boolean checkBoxSelected = checkBoxCredentialsUseReminder.isSelected();
        spinnerCredentialsReminderDays.setDisable(!checkBoxSelected);
    }

    public void onAddSecurityQuestionClicked() {

        //STATE - soll NICHT in UNSET und VIEW_ENTRY funktionieren
        if (state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell keine Sicherheitsfragen hinzufügen");
            return;
        }

        try {
            /* Sicherheitsfrage hinzufügen */
            openModal("../view/Sicherheitsfrage-und-Antwort.fxml",
                    SecurityQuestionViewController.class, identity -> {
                    });
        } catch (Exception e) {
            showError(e);
            throw new RuntimeException(e);
        }
    }

    public void onRemoveSecurityQuestionClicked() {
        //STATE - soll NICHT in UNSET und VIEW_ENTRY funktionieren
        if (state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell keine Sicherheitsfragen entfernen");
            return;
        }

        String selectedItem = comboBoxCredentialsSecurityQuestion.getSelectionModel().getSelectedItem();
        String value = currentCredentials.getSecurityQuestions().get(selectedItem);
        currentCredentials.withoutSecurityQuestion(selectedItem, value);

        refreshEntry();

        //CredentialsController credController = passwordManagerController.getCredentialsController();
        //String question = comboBoxCredentialsSecurityQuestion.getValue();
        //FIXME: Direkte Änderungen sollen nicht vorgenommen werden. erst am current, beim save am tatsächlichen objekt
        //credController.removeSecurityQuestion(question, currentCredentials.getSecurityQuestions().get(question), currentCredentials);
    }

    public void onAddCredentialsClicked() {
        //STATE - soll nur in UNSET und VIEW_ENTRY funktionieren
        if (!state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell keine neuen Einträge erstellen");
            return;
        }

        setState(CREATING_NEW_ENTRY);
        oldCredentials = null;
        currentCredentials = new CredentialsBuilder();
        listViewCredentialsList.getSelectionModel().clearSelection();

        if (currentCredentials == null ||
                currentCredentials.getName() == null ||
                currentCredentials.getUserName() == null ||
                currentCredentials.getPassword() == null ||
                currentCredentials.getWebsite() == null) {

            buttonSaveCredentials.setDisable(true);
        }
        refreshEntry();
    }

    public void onRemoveCredentialsClicked() {
        //STATE - soll nur in und VIEW_ENTRY funktionieren
        if (!state.match(VIEW_ENTRY)) {
            showError("Du kannst aktuell keine Einträge entfernen");
            return;
        }

        CredentialsController credController = passwordManagerController.getCredentialsController();
        oldCredentials = listViewCredentialsList.getSelectionModel().getSelectedItem().getCredentials();
        listViewCredentialsList.getSelectionModel().clearSelection();
        setState(UNSET);
        //listViewCredentialsList.getFocusModel().focus(-1);
        credController.removeCredentials(oldCredentials);
        oldCredentials = null;
        currentCredentials = new CredentialsBuilder();
        refreshEntry();
    }

    public void onStartEditCredentialsClicked() {
        //STATE - soll nur in VIEW_ENTRY funktionieren
        if (!state.match(VIEW_ENTRY)) {
            showError("Du kannst aktuell keinen Eintrag bearbeiten.\nEs muss ein Eintrag ausgewählt sein, um ihn bearbeiten zu können.");
            return;
        }

        setState(START_EDITING_ENTRY);
    }

    public void onSaveCredentialsClicked() {
        //STATE - soll nur in CREATING_NEW_ENTRY und EDITED_ENTRY funktionieren
        if (!state.match(CREATING_NEW_ENTRY, EDITED_ENTRY)) {
            showError("Du kannst aktuell keine Einträge speichern");
            return;
        }

        setState(VIEW_ENTRY);

        spinnerCredentialsReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999));
        spinnerCredentialsReminderDays.setDisable(true);

        CredentialsController credController = passwordManagerController.getCredentialsController();

        updateCredentialsBuilderCopy();

        List<CategoryItem> categoryItems = choiceBoxCredentialsCategories.getSelectedContentList();
        List<Category> categoryList = categoryItems.stream()
                .map(CategoryItem::getCategory)
                .collect(Collectors.toList());

        if (oldCredentials == null) {
            credController.addCredentials(currentCredentials, categoryList);
        } else {
            credController.updateCredentials(oldCredentials, currentCredentials, categoryList);
        }
        refreshEntry();
    }

    public void onChooseCategoryClicked() {
        refreshLists();
    }

    public void onChooseQuestionClicked() {

        //Wenn es geleert wurde, einfach die Ansicht zurücksetzen
        if (comboBoxCredentialsSecurityQuestion.getSelectionModel().getSelectedIndex() == -1) {
            labelCredentialsSecurityAnswer.setText("");
            labelCredentialsSecurityAnswer.setVisible(false);
            return;
        }

        //STATE - soll nur in CREATING_NEW_ENTRY, START_EDITING_ENTRY und EDITED_ENTRY funktionieren
        if (state.match(UNSET)) {
            showError("Du kannst aktuell keine Sicherheitsfrage auswählen");
            return;
        }

        String selectedQuestion = comboBoxCredentialsSecurityQuestion.getValue();
        String answer = currentCredentials.getSecurityQuestions().get(selectedQuestion);
        labelCredentialsSecurityAnswer.setText(answer);
        labelCredentialsSecurityAnswer.setVisible(true);

    }

    public void onEntryChosen() {
        //STATE - soll unabhängig funktionieren, aber zur state relative Entscheidungen treffen

        if (buttonCredentialsShowPassword.isDisabled()) {
            buttonCredentialsShowPassword.setDisable(false);
        }
        if (buttonCredentialsCopy.isDisabled()) {
            buttonCredentialsCopy.setDisable(false);
            progressBarCredentialsCopyTimer.setOpacity(1.0);
        }
        if (comboBoxCredentialsSecurityQuestion.isDisabled()) {
            comboBoxCredentialsSecurityQuestion.setDisable(false);
        }

        buttonCredentialsShowPassword.setSelected(false);
        CredentialsItem selectedEntry = listViewCredentialsList.getSelectionModel().getSelectedItem();
        int index = listViewCredentialsList.getFocusModel().getFocusedIndex();

        //Wenn Eingaben vorliegen, nach Verwerfung dieser Eingaben fragen
        if (state.match(EDITED_ENTRY, CREATING_NEW_ENTRY)) {
            SimpleConfirmation confirmation = new SimpleConfirmation("Änderung verwerfen?",
                    "Zur Zeit wird ein Eintrag bearbeitet",
                    "Wollen Sie wirklich abbrechen? \n Alle Änderungen werden gelöscht.") {
                @Override
                public void onSuccess() {
                    //Änderungen nicht übernehmen
                    oldCredentials = selectedEntry.getCredentials();
                    currentCredentials = selectedEntry.getNewBuilder(passwordManagerController.getUtilityController());
                    listViewCredentialsList.getSelectionModel().select(index);
                    setState(VIEW_ENTRY);
                    refreshEntry();
                }

                @Override
                public void onCancel() {
                    //nicht löschen
                    listViewCredentialsList.getSelectionModel().clearSelection();
                    updateView();
                }
            };
            confirmation.setAlertType(AlertType.CONFIRMATION);
            confirmation.open();
        } else {
            oldCredentials = selectedEntry.getCredentials();
            currentCredentials = selectedEntry.getNewBuilder(passwordManagerController.getUtilityController());
            setState(VIEW_ENTRY);
            refreshEntry();
        }
    }

    //endregion

    //region refreshes

    @Override
    public void refreshLists() {

        //geänderte Daten aus dem Model beziehen
        Map<Path, Category> cats = passwordManagerController.getPasswordManager().getRootCategory().createPathMap(new Path());

        //Alle Kategorien in die Kombobox einpflegen
        CategoryItem chosenCat = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
        //alte Inhalte entfernen
        comboBoxCategorySelectionMain.getItems().clear();
        //neue Inhalte einfügen
        cats.entrySet().stream()
                .map(entry -> new CategoryItem(entry.getKey(), entry.getValue()))
                .forEach(comboBoxCategorySelectionMain.getItems()::add);

        comboBoxCategorySelectionMain.getItems().sort(Comparator.comparing(item -> item.getPath().length()));

        List<SelectableComboItem<CategoryItem>> oldList = choiceBoxCredentialsCategories.getListProvider();

        List<SelectableComboItem<CategoryItem>> catItems = cats.entrySet().stream()
                .map(entry -> new SelectableComboItem<>(new CategoryItem(entry.getKey(), entry.getValue())))
                .collect(Collectors.toList());
        for (SelectableComboItem<CategoryItem> item : catItems) {
            if (oldList.stream().anyMatch(itemx -> itemx.getContent().getPath().equals(item.getContent().getPath()) && itemx.isSelected()))
                item.setSelected(true);
        }
        catItems.sort(Comparator.comparing(item -> item.getContent().getPath().length()));
        choiceBoxCredentialsCategories.setListProvider(catItems);

        //vorherige Auswahl wiederherstellen, wenn möglich
        List<CategoryItem> items = comboBoxCategorySelectionMain.getItems();
        if (!items.stream().anyMatch(item -> item.getPath().equals(chosenCat.getPath()))) {
            CategoryItem selected = items.stream().filter(item -> item.getPath().equals(Path.ROOT_CATEGORY_PATH)).findAny().get();
            comboBoxCategorySelectionMain.getSelectionModel().select(selected);
        } else {
            CategoryItem selected = items.stream().filter(item -> item.getPath().equals(chosenCat.getPath())).findAny().get();
            comboBoxCategorySelectionMain.getSelectionModel().select(selected);
        }

        refreshEntryListWhenCategoryChosen();

    }

    //Die Liste der Credentials updaten, wenn eine Kategorie zum Filtern ausgewählt wird
    private void refreshEntryListWhenCategoryChosen() {
        //Inhalt der Kategorie in Liste anzeigen
        CategoryItem chosenCat2 = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
        //getAllCredentials damit die aktuelle Kategorie, ihre Inhalte und alle untergeordneten Inhalte berücksichtigt werden
        Collection<Credentials> credentials = chosenCat2 == null ?
                passwordManagerController.getPasswordManager().getRootCategory().getAllCredentials() : chosenCat2.getCategory().getAllCredentials();
        if (!credentials.isEmpty()) {
            List<CredentialsItem> selection = selectionStrategy.select(new LinkedList<>(credentials));
            List<CredentialsItem> ordered = orderStrategy.order(selection);
            ObservableList<CredentialsItem> credsToShow = new ObservableListWrapper<>(ordered);
            listViewCredentialsList.setItems(credsToShow);
            if (credsToShow.size() > 0)
                listViewCredentialsList.getSelectionModel().select(0);
        } else {
            listViewCredentialsList.setItems(new ObservableListWrapper<>(Collections.emptyList()));
            setState(UNSET);
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

        //SecurityQuestionComboBox refreshen
        comboBoxCredentialsSecurityQuestion.getItems().clear();
        for (Map.Entry<String, String> question : currentCredentials.getSecurityQuestions().entrySet()) {
            comboBoxCredentialsSecurityQuestion.getItems().add(question.getKey());
        }
        if (!currentCredentials.getSecurityQuestions().entrySet().isEmpty())
            comboBoxCredentialsSecurityQuestion.getSelectionModel().select(0);

        //MultiSelectionComboBox updaten
        Collection<Category> categories = passwordManagerController.getCredentialsController().getCategoriesOfCredentials(
                passwordManagerController.getPasswordManager().getRootCategory(), oldCredentials);
        List<SelectableComboItem<CategoryItem>> listProvider = choiceBoxCredentialsCategories.getListProvider();
        for (SelectableComboItem<CategoryItem> item : listProvider) {
            boolean selected = categories.contains(item.getContent().getCategory());
            choiceBoxCredentialsCategories.setSelected(item, selected);
        }
    }

    @Override
    public void refreshEntryPasswordQuality(int quality) {
        //XXX change quality to double between 0 and 1
        progressBarCredentialsQuality.setProgress((double) quality / 100);
    }

    //endregion

    //region showError

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

    //endregion

    void changeState(WindowState expected, WindowState newState) {
        if (this.state == expected)
            setState(newState);
        updateView();
    }

    private void setState(WindowState state) {
        if (state != this.state)
            System.out.println("state changed: " + this.state + " -> " + state);
        this.state = state;
        updateView();
    }

    private void updateView() {
        switch (state) {
            case UNSET:
                setDisable(true);
                disableAllEntryControls(true, 0.0);
                disableSaveCredentialsButton(true);
                disableEditCredentialsButton(true);
                disableInteractEntry(true);
                break;
            case VIEW_ENTRY:
                setDisable(true);
                disableAllEntryControls(false, 1.0);
                disableSaveCredentialsButton(true);
                disableEditCredentialsButton(false);
                disableInteractEntry(false);
                break;
            case CREATING_NEW_ENTRY:
                setDisable(false);
                disableAllEntryControls(false, 1.0);
                disableSaveCredentialsButton(false);
                disableEditCredentialsButton(true);
                disableInteractEntry(false);
                break;
            case START_EDITING_ENTRY:
                setDisable(false);
                disableAllEntryControls(false, 1.0);
                disableSaveCredentialsButton(true);
                disableEditCredentialsButton(true);
                disableInteractEntry(false);
                break;
            case EDITED_ENTRY:
                setDisable(false);
                disableAllEntryControls(false, 1.0);
                disableSaveCredentialsButton(false);
                disableEditCredentialsButton(true);
                disableInteractEntry(false);
                break;
        }
    }

    private void disableInteractEntry(boolean disable) {
        buttonCredentialsCopy.setDisable(disable);
        buttonCredentialsShowPassword.setDisable(disable);
    }

    private void disableEditCredentialsButton(boolean disabled) {
        buttonEditCredentials.setDisable(disabled || listViewCredentialsList.getSelectionModel().getSelectedItem() == null);
    }

    private void disableSaveCredentialsButton(boolean disabled) {
        if (currentCredentials.getName() == null
                || currentCredentials.getPassword() == null
                || currentCredentials.getUserName() == null
                || currentCredentials.getWebsite() == null
                || currentCredentials.getName().isEmpty()
                || currentCredentials.getPassword().isEmpty()
                || currentCredentials.getUserName().isEmpty()
                || currentCredentials.getWebsite().isEmpty()) {
            buttonSaveCredentials.setDisable(true);
        } else buttonSaveCredentials.setDisable(disabled);
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
        buttonCredentialsAddCategories.setDisable(disabled);
        checkBoxCredentialsUseReminder.setDisable(disabled);
        choiceBoxCredentialsCategories.setDisable(disabled);

        buttonEditCategoryMain.setDisable(!disabled);
        buttonRemoveCategoryMain.setDisable(!disabled);
        buttonEditCredentials.setDisable(!disabled);
        buttonSaveCredentials.setDisable(disabled);
    }

    private void disableAllEntryControls(boolean disabled, double opacity) {
        buttonCredentialsShowPassword.setDisable(disabled);
        buttonCredentialsCopy.setDisable(disabled);
        progressBarCredentialsCopyTimer.setOpacity(opacity);
        if (disabled)
            progressBarCredentialsQuality.setProgress(0.0);
        comboBoxCredentialsSecurityQuestion.setDisable(disabled);
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
    }

}
