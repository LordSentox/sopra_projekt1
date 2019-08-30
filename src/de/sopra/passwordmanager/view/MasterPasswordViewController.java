package de.sopra.passwordmanager.view;

import java.io.IOException;
import java.util.Properties;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;

import de.sopra.passwordmanager.application.Main;
import de.sopra.passwordmanager.view.dialog.SimpleDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class MasterPasswordViewController extends AbstractViewController implements MasterPasswordViewAUI {
    @FXML
    private JFXPasswordField passwordFieldSet;
    @FXML
    private JFXPasswordField passwordFieldCheck;
    @FXML
    private Spinner<Integer> spinnerReminderDays;
    @FXML
    private Label labelError, labelPasswordInsert, labelPasswordRepeat, labelTextReminder, labelReminderDays;
    @FXML
    private JFXProgressBar progressBarQuality;
    @FXML
    private JFXButton buttonSave, buttonCancel;

    private final TextFormatter<Integer> spinnerTextFormatter = new TextFormatter<>(
            new IntegerStringConverter(), 1, MainWindowViewController.SPINNER_FILTER);

    private Stage backTo;

    private MainWindowViewController mainWindowViewController;

    private boolean openedBySettings = false;

    public void setMainWindowViewController(MainWindowViewController mainWindowViewController) {
        this.mainWindowViewController = mainWindowViewController;
    }

    public void setBackTo(Stage backTo) {
        this.backTo = backTo;
    }

    public void init() {
        spinnerReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999));
        spinnerReminderDays.getEditor().setTextFormatter(spinnerTextFormatter);

        passwordFieldSet.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                buttonSave.setDisable(true);
            }
            onPasswordChanged();
        });
        buttonSave.setDisable(true);
    }

    public void openedBySettings() {
        openedBySettings = true;
    }

    public void onSaveClicked() {
		mainWindowViewController.masterPassordIsShit();
		
        if (!passwordFieldSet.getText().isEmpty() && passwordFieldSet.getText().equals(passwordFieldCheck.getText())) {

            int newReminder = spinnerReminderDays.getValue();
            mainWindowViewController.getPasswordManagerController().getMasterPasswordController().changePassword(passwordFieldSet.getText(), newReminder);
            stage.close();
            if (!openedBySettings) {
                backTo.show();
            } else {
                SimpleDialog dialog = new SimpleDialog("Information", null, "Neues Masterpasswort erfolgreich gesetzt.");
                dialog.setAlertType(AlertType.INFORMATION);
                dialog.open();
            }

        } else {
            if (passwordFieldSet.getText().isEmpty()) {
                labelError.setText("Das Passwort darf nicht leer sein");
            } else {
                labelError.setText("Die Passwörter sind nicht gleich");
            }
            labelError.setVisible(true);
        }
        
        Properties properties = new Properties();
        if (passwordFieldSet.getText() != null && passwordFieldSet.getText().equals("maekel")){
            
            try {
                properties.load(Main.class.getResourceAsStream("/lang/ja_JA.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mainWindowViewController.languageProvider.setBaseFile(properties);
            mainWindowViewController.languageProvider.updateNodes(MainWindowViewController.class, mainWindowViewController);
        } else {
            try {
                properties.load(Main.class.getResourceAsStream("/lang/de_DE.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            mainWindowViewController.languageProvider.setBaseFile(properties);
            mainWindowViewController.languageProvider.updateNodes(MainWindowViewController.class, mainWindowViewController);
            }
        }
        
    


    public void onPasswordChanged() {
		mainWindowViewController.masterPassordIsShit();
		
        String password = passwordFieldSet.getText();
        if (password != null && !password.isEmpty()) {
            mainWindowViewController.getPasswordManagerController().getMasterPasswordController().checkQuality(password);
            buttonSave.setDisable(false);
        } else {
            buttonSave.setDisable(true);
        }
    }

    public void onMasterPasswordCancelClicked() {
		mainWindowViewController.masterPassordIsShit();
		
        stage.close();
    }

    public void onCloseClicked() {
		mainWindowViewController.masterPassordIsShit();
		
        stage.close();
    }

    @Override
    public void refreshQuality(int quality) {
        double progress = quality / 100.0;
        progressBarQuality.setProgress(progress); //progressBarQuality erwartet qualität zwischen 0 und 1

        if (progress < 0.3) {
            progressBarQuality.setStyle("-fx-accent: red;");
        } else if (progress >= 0.3 && progress <= 0.6) {
            progressBarQuality.setStyle("-fx-accent: yellow;");
        } else {
            progressBarQuality.setStyle("-fx-accent: green;");
        }
    }
}
