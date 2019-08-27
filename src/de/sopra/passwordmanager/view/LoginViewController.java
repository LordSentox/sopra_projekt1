package de.sopra.passwordmanager.view;

import java.io.File;

import com.jfoenix.controls.JFXPasswordField;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginViewController extends AbstractViewController implements LoginViewAUI{
	
	private static final File SAVEFILE = new File("");

    private Stage mainStage;
    
    @FXML private JFXPasswordField passwordField;
    @FXML private Label labelError;
   
    public void onLoginClicked() {
    	handleLoginResult(true);
    	//FIXME: DUMMY
    	//mainWindowViewController.getPasswordManagerController().requestLogin(passwordField.getText(), SAVEFILE);
    }
    
    public void onCancelLoginClicked() {
    	stage.close();
    }

    @Override
    public void handleLoginResult(boolean result) {
    	if(result) {
    		mainStage.show();
    		stage.close();
    	}else{
    		labelError.setVisible(true);
    	}
    }
    public void setMainStage(Stage mainStage){
    	this.mainStage = mainStage;
    }
	
}
