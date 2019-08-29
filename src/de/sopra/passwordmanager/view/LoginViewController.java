package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXPasswordField;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;

public class LoginViewController extends AbstractViewController implements LoginViewAUI {

    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private Label labelError;

    private File sourceFile = PasswordManagerController.SAVE_FILE;

    private Stage backTo;

    public void setBackTo(Stage backTo) {
        this.backTo = backTo;
    }

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
            backTo.show();
        } else {
            labelError.setVisible(true);
        }
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
}
