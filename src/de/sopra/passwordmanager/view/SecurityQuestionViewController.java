package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXTextField;

import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import javafx.fxml.FXML;
import javafx.stage.Stage;



public class SecurityQuestionViewController {

	@FXML private JFXTextField textFieldQuestion;
	@FXML private JFXTextField textFieldAnswer;
    private MainWindowViewController mainWindowViewController;
    private Stage stage; 
    
    public void setStage(Stage primaryStage){
    	this.stage = primaryStage;
    }
    public void onSecurityQuestionCancelClicked(){
    	stage.close();
    }
    public void onSaveClicked() {
    	//TODO Credentials getter in MainWindowViewController
    	//mainWindowViewController.getPasswordManagerController().getCredentialsController().addSecurityQuestion(textFieldQuestion.getText(), textFieldAnswer.getText()), Credentials ;
    	CredentialsBuilder credBuilder = mainWindowViewController.getCredentialsBuilder();
    	CredentialsController credController = mainWindowViewController.getPasswordManagerController().getCredentialsController();
    	credController.addSecurityQuestion(textFieldQuestion.getText(), textFieldAnswer.getText(), credBuilder);
        stage.close();	
    }

}