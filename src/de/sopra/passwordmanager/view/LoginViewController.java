package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXPasswordField;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.File;

public class LoginViewController extends AbstractViewController implements LoginViewAUI {

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
            stage.close();
        } else {
            labelError.setVisible(true);
        }
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
}
