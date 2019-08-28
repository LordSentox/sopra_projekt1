package de.sopra.passwordmanager.view;

import java.io.File;

import com.jfoenix.controls.JFXPasswordField;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginViewController extends AbstractViewController implements LoginViewAUI {

    private Stage mainStage;

    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private Label labelError;

    private File sourceFile = PasswordManagerController.SAVE_FILE;

    public void onLoginClicked() {
        mainWindowViewController.getPasswordManagerController().requestLogin(passwordField.getText(), this.sourceFile);
    }

    public void onCancelLoginClicked() {
        stage.close();
    }

    @Override
    public void handleLoginResult(boolean result) {
        if (result) {
            mainStage.show();
            stage.close();
        } else {
            labelError.setVisible(true);
        }
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
}
