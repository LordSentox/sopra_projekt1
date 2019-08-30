package de.sopra.passwordmanager.view;

import com.jfoenix.controls.*;
import com.sun.javafx.collections.ObservableListWrapper;
import de.sopra.passwordmanager.application.Main;
import de.sopra.passwordmanager.controller.CategoryController;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.CredentialsItem;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.util.PatternSyntax;
import de.sopra.passwordmanager.util.strategy.*;
import de.sopra.passwordmanager.view.dialog.SimpleConfirmation;
import de.sopra.passwordmanager.view.dialog.SimpleDialog;
import de.sopra.passwordmanager.view.dialog.TwoOptionConfirmation;
import de.sopra.passwordmanager.view.multibox.MultiSelectionComboBox;
import de.sopra.passwordmanager.view.multibox.SelectableComboItem;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.media.MediaPlayer;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static MediaPlayer player = null;
    public static String currentTheme = null;

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
    private JFXButton buttonAddCredentials, buttonRemoveCredentials, buttonCredentialsAddSecurityQuestion, buttonCredentialsRemoveSecurityQuestion, buttonAddCategoryMain, buttonRemoveCategoryMain, buttonSearch, buttonCredentialsGeneratePassword, buttonCredentialsCopy, buttonEditCredentials, buttonSaveCredentials, buttonCredentialsAddCategories, buttonSettings, buttonEditCategoryMain, buttonCancelEditCredentials;

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

    @FXML
    private Label labelReminderDays, labelCategories, labelSecurityQuestion, labelTextLastChanged, labelTextCreatedAt,
            labelEntryName, labelUserName, labelPassword, labelNotes, buttonLabelShowPassword, labelWebsite;

    //endregion

    public void init() {
        languageProvider.updateNodes(MainWindowViewController.class, this);


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
        timeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            progressBarCredentialsCopyTimer.setProgress(progressBarCredentialsCopyTimer.progressProperty().doubleValue() - 0.001);
            if (progressBarCredentialsCopyTimer.progressProperty().doubleValue() <= 0.0) {
                buttonCredentialsCopy.getStyleClass().remove("copy-button");
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
            if (newText == null) return;
            if (oldText == null) oldText = "";
            if (!oldText.equals(newText)) {
                currentCredentials.withName(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
            }
        });
        textFieldCredentialsUserName.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) return;
            if (oldText == null) oldText = "";
            if (!oldText.equals(newText)) {
                currentCredentials.withUserName(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
                passwordManagerController.checkQuality(currentCredentials);
            }
        });
        textFieldCredentialsWebsite.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) return;
            if (oldText == null) oldText = "";
            if (!oldText.equals(newText)) {
                currentCredentials.withWebsite(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
            }
        });
        textFieldCredentialsPassword.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) return;
            if (oldText == null) oldText = "";
            if (!oldText.equals(newText)) {
                currentCredentials.withPassword(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
                passwordManagerController.checkQuality(currentCredentials);
            }
        });
        textFieldCredentialsNotes.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) return;
            if (oldText == null) oldText = "";
            if (!oldText.equals(newText)) {
                currentCredentials.withNotes(newText);
                changeState(START_EDITING_ENTRY, EDITED_ENTRY);
            }
        });

        listViewCredentialsList.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<CredentialsItem>) c -> {
                    if (c.next()) {
                        if (c.getList() != null && !c.getList().isEmpty()) {
                            onEntryChosen();
                        }
                    }
                });

        comboBoxCategorySelectionMain.getSelectionModel().selectedItemProperty().addListener((obs, oldText, newText) -> {
            refreshEntryListWhenCategoryChosen();
        });

        //connect both password fields
        passwordFieldCredentialsPassword.disableProperty().bindBidirectional(textFieldCredentialsPassword.disableProperty());

        //Die ComboBox initialisieren - enthält zu Beginn nur die Root-Kategorie
        CategoryItem rootCategoryItem = new CategoryItem(Path.ROOT_CATEGORY_PATH, passwordManagerController.getPasswordManager().getRootCategory());
        comboBoxCategorySelectionMain.getItems().add(rootCategoryItem);
        comboBoxCategorySelectionMain.getSelectionModel().select(rootCategoryItem);

        checkBoxCredentialsUseReminder.disableProperty().addListener((observable, oldValue, newValue) ->
                spinnerCredentialsReminderDays.setDisable(newValue || !checkBoxCredentialsUseReminder.isSelected()));

        checkBoxCredentialsUseReminder.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!checkBoxCredentialsUseReminder.isDisabled())
                spinnerCredentialsReminderDays.setDisable(!newValue);
        });

        //Die Strategie initilisieren - sind zu Beginn Identitätsbeziehungen, d.h. ändern nichts am Input
        selectionStrategy = new SelectAllStrategy(); //es wird keine Auswahl getroffen
        orderStrategy = new AlphabeticOrderStrategy().nextOrder(new ReminderSecondaryStrategy()); //es wird nicht sortiert

        textFieldCredentialsNotes.setWrapText(true);

        //visual color for active reminders
        listViewCredentialsList.setCellFactory(param -> new JFXListCell<CredentialsItem>() {
            @Override
            public void updateItem(CredentialsItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.hasToBeChanged())
                    getStyleClass().add("reminder-on-list-cell");
                else
                    getStyleClass().removeAll("reminder-on-list-cell");
            }
        });

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
        if (state.match(CREATING_NEW_ENTRY, EDITED_ENTRY)) {
            showError("Ein Eintrag wird gerade editiert, die Änderung müssen vorher gespeichert oder verworfen werden.");
            return;
        }
        try {
            /* Einstellungen öffnen */
            openModal("/Einstellungen.fxml", SettingsViewController.class, SettingsViewController::init);
            mainWindowViewController.masterPasswordIsShit();
        } catch (Exception e) {
            showError(e);
            throw new RuntimeException(e);
        }
    }

    public void onSearchClicked() {
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll unabhängig funktionieren
        CredentialsController credentialsController = passwordManagerController.getCredentialsController();
        String pattern = textFieldSearch.getText();
        //mit neuem Pattern filtern, refresh erfolgt über controller
        credentialsController.filterCredentials(new PatternSyntax(pattern));
    }

    public void onAddCategoryClicked() {
        //STATE - soll unabhängig funktionieren
        try {
            CategoryItem selectedItem = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
            Path path = selectedItem.getPath();
            /* Kategorie hinzufügen - leeres Fenster öffnen */
            categoryEditViewController = openModal("/Kategorie_anlegen-aendern.fxml",
                    CategoryEditViewController.class, preOpen ->
                    {
                        preOpen.setShouldAdd(true);
                        preOpen.setCurrentlyEdited(path);
                        preOpen.init();
                    });
        } catch (Exception e) {
            showError(e);
            throw new RuntimeException(e);
        }
    }

    public void onEditCategoryClicked() {
        mainWindowViewController.masterPassordIsShit();
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
            categoryEditViewController = openModal("/Kategorie_anlegen-aendern.fxml",
                    CategoryEditViewController.class, preOpen ->
                    {
                        preOpen.setCurrentlyEdited(path);
                        preOpen.init();
                    });
        } catch (Exception e) {
            showError(e);
            throw new RuntimeException(e);
        }

    }

    public void onCancelEditCredentialsClicked() {
        mainWindowViewController.masterPassordIsShit();
        this.currentCredentials = null;
        refreshEntry();
        setState(VIEW_ENTRY);
        Optional<CredentialsItem> option = listViewCredentialsList.getItems().stream().filter(item -> item.getCredentials().equals(oldCredentials)).findAny();
        if (option.isPresent()) {
            listViewCredentialsList.getSelectionModel().select(option.get());
        } else listViewCredentialsList.getSelectionModel().select(-1);
        onEntryChosen();
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
        mainWindowViewController.masterPassordIsShit();
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
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll NICHT in UNSET und VIEW_ENTRY funktionieren
        if (state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell kein Password generieren");
            return;
        }
        //refresh erfolgt von controller aus
        passwordManagerController.getUtilityController().generatePassword(currentCredentials);
    }

    public void onCheckBoxClicked() {
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll NICHT in UNSET und VIEW_ENTRY funktionieren
        if (state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell den Änderungswecker nicht ändern");
            return;
        }
        changeState(START_EDITING_ENTRY, EDITED_ENTRY);
    }

    public void onAddSecurityQuestionClicked() {
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll NICHT in UNSET und VIEW_ENTRY funktionieren
        if (state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell keine Sicherheitsfragen hinzufügen");
            return;
        }

        try {
            updateCredentialsBuilderCopy();
            /* Sicherheitsfrage hinzufügen */
            openModal("/Sicherheitsfrage-und-Antwort.fxml",
                    SecurityQuestionViewController.class, identity -> {
                        identity.init();
                    });
        } catch (Exception e) {
            showError(e);
            throw new RuntimeException(e);
        }
    }

    public void onRemoveSecurityQuestionClicked() {
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll NICHT in UNSET und VIEW_ENTRY funktionieren
        if (state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell keine Sicherheitsfragen entfernen");
            return;
        }

        String selectedItem = comboBoxCredentialsSecurityQuestion.getSelectionModel().getSelectedItem();
        String value = currentCredentials.getSecurityQuestions().get(selectedItem);
        currentCredentials.withoutSecurityQuestion(selectedItem, value);
        updateCredentialsBuilderCopy();
        refreshEntry();

        //CredentialsController credController = passwordManagerController.getCredentialsController();
        //String question = comboBoxCredentialsSecurityQuestion.getValue();
        //FIXME: Direkte Änderungen sollen nicht vorgenommen werden. erst am current, beim save am tatsächlichen objekt
        //credController.removeSecurityQuestion(question, currentCredentials.getSecurityQuestions().get(question), currentCredentials);
    }

    public void onAddCredentialsClicked() {
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll nur in UNSET und VIEW_ENTRY funktionieren
        if (!state.match(UNSET, VIEW_ENTRY)) {
            showError("Du kannst aktuell keine neuen Einträge erstellen");
            return;
        }

        setState(CREATING_NEW_ENTRY);
        oldCredentials = null;
        currentCredentials = new CredentialsBuilder();
        listViewCredentialsList.getSelectionModel().clearSelection();

        updateView();
        refreshEntry();
    }

    public void onRemoveCredentialsClicked() {
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll nur in und VIEW_ENTRY funktionieren
        if (!state.match(VIEW_ENTRY)) {
            showError("Du kannst aktuell keine Einträge entfernen");
            return;
        }

        SimpleConfirmation confirmation = new SimpleConfirmation("Eintrag löschen", "Sind Sie sicher?", "Hiermit wird der gewählte Eintrag komplett gelöscht.") {
            @Override
            public void onSuccess() {
                CredentialsController credController = passwordManagerController.getCredentialsController();
                listViewCredentialsList.getSelectionModel().clearSelection();
                setState(UNSET);
                credController.removeCredentials(oldCredentials);
                oldCredentials = null;
                currentCredentials = new CredentialsBuilder();
                refreshEntry();
            }
        };
        confirmation.setButtonOk("Ja");
        confirmation.setButtonCancel("Nein");

        confirmation.open();

    }

    public void onStartEditCredentialsClicked() {
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll nur in VIEW_ENTRY funktionieren
        if (!state.match(VIEW_ENTRY)) {
            showError("Du kannst aktuell keinen Eintrag bearbeiten.\nEs muss ein Eintrag ausgewählt sein, um ihn bearbeiten zu können.");
            return;
        }

        setState(START_EDITING_ENTRY);
    }

    public void onSaveCredentialsClicked() {
        mainWindowViewController.masterPassordIsShit();
        //STATE - soll nur in CREATING_NEW_ENTRY und EDITED_ENTRY funktionieren
        if (!state.match(CREATING_NEW_ENTRY, EDITED_ENTRY)) {
            showError("Du kannst aktuell keine Einträge speichern");
            return;
        }

        setState(VIEW_ENTRY);

        CredentialsController credController = passwordManagerController.getCredentialsController();

        currentCredentials.withLastChanged(LocalDateTime.now());

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
        mainWindowViewController.masterPassordIsShit();
        refreshLists();
    }

    public void onChooseQuestionClicked() {
        mainWindowViewController.masterPassordIsShit();

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

        CredentialsItem selectedEntry = listViewCredentialsList.getSelectionModel().getSelectedItem();

        //Wenn Eingaben vorliegen, nach Verwerfung dieser Eingaben fragen
        if (selectedEntry != null && !selectedEntry.getCredentials().equals(oldCredentials)) {
            if (state.match(EDITED_ENTRY, CREATING_NEW_ENTRY)) {
                SimpleConfirmation confirmation = new SimpleConfirmation("Änderung verwerfen?",
                        "Zur Zeit wird ein Eintrag bearbeitet",
                        "Wollen Sie wirklich abbrechen? \n Alle Änderungen werden gelöscht.") {
                    @Override
                    public void onSuccess() {
                        //Änderungen nicht übernehmen
                        oldCredentials = selectedEntry.getCredentials();
                        currentCredentials = selectedEntry.getNewBuilder(passwordManagerController.getUtilityController());
                        setState(VIEW_ENTRY);
                        refreshEntry();
                    }

                    @Override
                    public void onCancel() {
                        //Änderungen behalten
                        Optional<CredentialsItem> any = listViewCredentialsList.getItems().stream().filter(item -> item.getCredentials().equals(oldCredentials)).findAny();
                        if (any.isPresent()) {
                            CredentialsItem item = any.get();
                            listViewCredentialsList.getSelectionModel().select(item);
                            listViewCredentialsList.getFocusModel().focus(listViewCredentialsList.getItems().indexOf(item));
                            listViewCredentialsList.refresh();
                        } else {
                            listViewCredentialsList.getSelectionModel()
                                    .clearSelection(listViewCredentialsList.getItems().indexOf(selectedEntry));
                            listViewCredentialsList.getFocusModel().focus(-1);
                            listViewCredentialsList.refresh();
                        }
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
    }

    public void onCloseClicked() {
        mainWindowViewController.masterPassordIsShit();
        //TODO Programm richtig beenden
        stage.close();
    }

    //endregion

    private void masterPasswordIsShit() {
        String masterPassword = getPasswordManagerController().getPasswordManager().getMasterPassword();
        if (masterPassword != null && masterPassword.length() < PasswordManagerController.SHITTY_LENGTH) {
            System.out.println(masterPassword);

            SimpleConfirmation simpleConfirmation = new SimpleConfirmation("Das Masterpasswort " + masterPassword + " ist scheiße", "", "Das MasterPasswort " + masterPassword + " ist Mist bitte ändere es") {

                @Override
                public void onSuccess() {
                    getSettingsViewController().onChangeMasterpasswordClicked();
                }

                @Override
                public void onCancel() {
                    // do nothing
                }
            };
            simpleConfirmation.open();
            simpleConfirmation.setButtonOk("Ja");
            simpleConfirmation.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationNo2 = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Du solltest dein Passwort ehrlich ändern.") {

                @Override
                public void onSuccess() {
                    // do nothing
                }

                @Override
                public void onCancel() {
                    getSettingsViewController().onChangeMasterpasswordClicked();
                }
            };
            simpleConfirmationNo2.open();
            simpleConfirmationNo2.setButtonOk("Ja");
            simpleConfirmationNo2.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationAgain = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Jetzt hör doch zu und leg ein neues MasterPasswort an.") {

                @Override
                public void onSuccess() {
                    // do nothing
                }

                @Override
                public void onCancel() {
                    getSettingsViewController().onChangeMasterpasswordClicked();
                }
            };
            simpleConfirmationAgain.open();
            simpleConfirmationAgain.setButtonOk("Ja");
            simpleConfirmationAgain.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationYes = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Dein Passwort " + masterPassword + " ist wirklich, wirklicht brutalster Bullshit.") {

                @Override
                public void onSuccess() {
                    getSettingsViewController().onChangeMasterpasswordClicked();
                }

                @Override
                public void onCancel() {
                    //getSettingsViewController().onChangeMasterpasswordClicked();
                }
            };
            simpleConfirmationYes.open();
            simpleConfirmationYes.setButtonOk("Ja");
            simpleConfirmationYes.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationForReal = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Nein wirklich. Ändere dein Passwort. Jetzt.") {

                @Override
                public void onSuccess() {
                    // do nothing
                }

                @Override
                public void onCancel() {
                    getSettingsViewController().onChangeMasterpasswordClicked();
                }
            };
            simpleConfirmationForReal.open();
            simpleConfirmationForReal.setButtonOk("Ja");
            simpleConfirmationForReal.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationDeleteAllData = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Nein wirklich. Ändere dein Passwort. Jetzt.") {

                @Override
                public void onSuccess() {
                    //getSettingsViewController().onResetDataClicked();
                }
            };
            simpleConfirmationDeleteAllData.setAlertType(AlertType.WARNING);
            simpleConfirmationDeleteAllData.open();
        }

    }


    //region refreshes

    @Override
    public void refreshLists() {

        //geänderte Daten aus dem Model beziehen
        Map<Path, Category> cats = passwordManagerController.getPasswordManager().getRootCategory().createPathMap(new Path());

        //Alle Kategorien in die Kombobox einpflegen
        CategoryItem chosenCat = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();

        //alte Inhalte entfernen
        if (state.match(UNSET, VIEW_ENTRY, START_EDITING_ENTRY)) {
            comboBoxCategorySelectionMain.getItems().clear();
            //neue Inhalte einfügen
            cats.entrySet().stream()
                    .map(entry -> new CategoryItem(entry.getKey(), entry.getValue()))
                    .forEach(comboBoxCategorySelectionMain.getItems()::add);

            comboBoxCategorySelectionMain.getItems().sort(Comparator.comparing(item -> item.getPath().length()));
        }

        //Choicebox updaten

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

        if (state.match(UNSET, VIEW_ENTRY, START_EDITING_ENTRY)) {

            //vorherige Auswahl wiederherstellen, wenn möglich
            List<CategoryItem> items = comboBoxCategorySelectionMain.getItems();
            if (!items.stream().anyMatch(item -> item.getPath().equals(chosenCat.getPath()))) {
                CategoryItem selected = items.stream().filter(item -> item.getPath().equals(Path.ROOT_CATEGORY_PATH)).findAny().get();
                comboBoxCategorySelectionMain.getSelectionModel().select(selected);
            } else {
                CategoryItem selected = items.stream().filter(item -> item.getPath().equals(chosenCat.getPath())).findAny().get();
                comboBoxCategorySelectionMain.getSelectionModel().select(selected);
            }

        }

        if (state.match(UNSET, VIEW_ENTRY, START_EDITING_ENTRY))
            refreshEntryListWhenCategoryChosen();

        listViewCredentialsList.getSelectionModel().clearSelection();

    }

    //Die Liste der Credentials updaten, wenn eine Kategorie zum Filtern ausgewählt wird
    private void refreshEntryListWhenCategoryChosen() {
        //Inhalt der Kategorie in Liste anzeigen
        CategoryItem chosenCat2 = comboBoxCategorySelectionMain.getSelectionModel().getSelectedItem();
        //getAllCredentials damit die aktuelle Kategorie, ihre Inhalte und alle untergeordneten Inhalte berücksichtigt werden
        Collection<Credentials> credentials = chosenCat2 == null ?
                passwordManagerController.getPasswordManager().getRootCategory().getAllCredentials() : chosenCat2.getCategory().getAllCredentials();
        if (!credentials.isEmpty()) {
            //select
            List<CredentialsItem> selection = selectionStrategy.select(new LinkedList<>(credentials));
            //order
            List<CredentialsItem> ordered = orderStrategy.order(selection);
            ObservableList<CredentialsItem> credsToShow = new ObservableListWrapper<>(ordered);
            listViewCredentialsList.setItems(credsToShow);
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

        if (currentCredentials.getPassword() != null)
            passwordManagerController.checkQuality(currentCredentials);

        if (currentCredentials.getCreatedAt() != null)
            labelCredentialsCreated.setText(currentCredentials.getCreatedAt().format(DateTimeFormatter.ISO_DATE));
        else labelCredentialsCreated.setText("");
        if (currentCredentials.getLastChanged() != null)
            labelCredentialsLastChanged.setText(currentCredentials.getLastChanged().format(DateTimeFormatter.ISO_DATE));
        else labelCredentialsCreated.setText("");

        changeState(START_EDITING_ENTRY, EDITED_ENTRY);

    }

    @Override
    public void refreshEntryPasswordQuality(int quality) {
        //XXX change quality to double between 0 and 1
        double progress = quality / 100.0;
        progressBarCredentialsQuality.setProgress(progress);

        if (progress < 0.3) {
            progressBarCredentialsQuality.setStyle("-fx-accent: red;");
        } else if (progress >= 0.3 && progress <= 0.6) {
            progressBarCredentialsQuality.setStyle("-fx-accent: yellow;");
        } else {
            progressBarCredentialsQuality.setStyle("-fx-accent: green;");
        }
    }

    //endregion

    //region showError

    public void showError(Exception exception) {
        mainWindowViewController.masterPassordIsShit();
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
        mainWindowViewController.masterPassordIsShit();
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
        else updateView();
    }

    private void setState(WindowState state) {
        if (this.state.match(UNSET))
            provideLanguageShiiiit();
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
                buttonCancelEditCredentials.setDisable(true);
                break;
            case VIEW_ENTRY:
                setDisable(true);
                disableAllEntryControls(false, 1.0);
                disableSaveCredentialsButton(true);
                disableEditCredentialsButton(false);
                disableInteractEntry(false);
                buttonCancelEditCredentials.setDisable(true);
                break;
            case CREATING_NEW_ENTRY:
                setDisable(false);
                disableAllEntryControls(false, 1.0);
                disableSaveCredentialsButton(false);
                disableEditCredentialsButton(true);
                disableInteractEntry(false);
                buttonCancelEditCredentials.setDisable(false);
                break;
            case START_EDITING_ENTRY:
                setDisable(false);
                disableAllEntryControls(false, 1.0);
                disableSaveCredentialsButton(true);
                disableEditCredentialsButton(true);
                disableInteractEntry(false);
                buttonCancelEditCredentials.setDisable(false);
                break;
            case EDITED_ENTRY:
                setDisable(false);
                disableAllEntryControls(false, 1.0);
                disableSaveCredentialsButton(false);
                disableEditCredentialsButton(true);
                disableInteractEntry(false);
                buttonCancelEditCredentials.setDisable(false);
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


    //vollkommen nutzlose aber lustige methode

    public void masterPassordIsShit() {
        String masterPassword = getPasswordManagerController().getPasswordManager().getMasterPassword();
        if (masterPassword != null && masterPassword.length() < PasswordManagerController.SHITTY_LENGTH) {
            System.out.println(masterPassword);

            SimpleConfirmation simpleConfirmation = new SimpleConfirmation("Das Masterpasswort " + masterPassword + " ist scheiße", "", "Das MasterPasswort " + masterPassword + " ist Mist bitte ändere es") {

                @Override
                public void onSuccess() {
                    onSettingsClicked();
                }

                @Override
                public void onCancel() {
                    // do nothing
                }
            };
            simpleConfirmation.open();
            simpleConfirmation.setButtonOk("Ja");
            simpleConfirmation.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationNo2 = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Du solltest dein Passwort ehrlich ändern.") {

                @Override
                public void onSuccess() {
                    // do nothing
                }

                @Override
                public void onCancel() {
                    //getSettingsViewController().onChangeMasterpasswordClicked();
                }
            };
            simpleConfirmationNo2.open();
            simpleConfirmationNo2.setButtonOk("Ja");
            simpleConfirmationNo2.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationAgain = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Jetzt hör doch zu und leg ein neues MasterPasswort an.") {

                @Override
                public void onSuccess() {
                    // do nothing
                }

                @Override
                public void onCancel() {
                    //getSettingsViewController().onChangeMasterpasswordClicked();
                }
            };
            simpleConfirmationAgain.open();
            simpleConfirmationAgain.setButtonOk("Ja");
            simpleConfirmationAgain.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationYes = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Dein Passwort " + masterPassword + " ist wirklich, wirklicht brutalster Bullshit.") {

                @Override
                public void onSuccess() {
                    //getSettingsViewController().onChangeMasterpasswordClicked();
                }

                @Override
                public void onCancel() {
                    //getSettingsViewController().onChangeMasterpasswordClicked();
                }
            };
            simpleConfirmationYes.open();
            simpleConfirmationYes.setButtonOk("Ja");
            simpleConfirmationYes.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationForReal = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Nein wirklich. Ändere dein Passwort. Jetzt.") {

                @Override
                public void onSuccess() {
                    // do nothing
                }

                @Override
                public void onCancel() {
                    //getSettingsViewController().onChangeMasterpasswordClicked();
                }
            };
            simpleConfirmationForReal.open();
            simpleConfirmationForReal.setButtonOk("Ja");
            simpleConfirmationForReal.setButtonCancel("Ja");
            SimpleConfirmation simpleConfirmationDeleteAllData = new SimpleConfirmation("Das Masterpasswort ist scheiße", "", "Nein wirklich. Ändere dein Passwort. Jetzt.") {

                @Override
                public void onSuccess() {
                    //getSettingsViewController().onResetDataClicked();
                }
            };
            simpleConfirmationDeleteAllData.setAlertType(AlertType.WARNING);
            simpleConfirmationDeleteAllData.open();
        }
    }

    private void provideLanguageShiiiit() {
        System.out.println("yooo");
        String masterPassword = getPasswordManagerController().getPasswordManager().getMasterPassword();
        Properties properties = new Properties();
        if (masterPassword != null && masterPassword.equals("maekel")) {

            try {
                properties.load(Main.class.getResourceAsStream("/lang/ja_JA.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            languageProvider.setBaseFile(properties);
            languageProvider.updateNodes(MainWindowViewController.class, this);
        } else {
            try {
                properties.load(Main.class.getResourceAsStream("/lang/de_DE.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            languageProvider.setBaseFile(properties);
            languageProvider.updateNodes(MainWindowViewController.class, this);
        }
    }
}
