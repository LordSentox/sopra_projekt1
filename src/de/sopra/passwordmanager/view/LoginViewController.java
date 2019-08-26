package de.sopra.passwordmanager.view;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class LoginViewController implements LoginViewAUI{

    private MainWindowViewController mainWindowViewController;
    @FXML private PasswordField passwordField;
    public void onLoginClicked() {
    	//mainWindowViewController.getPasswordManagerController().requestLogin(password, file);
    }

    @Override
    public void handleLoginResult(boolean result) {
    	
    }

}
