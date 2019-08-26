package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXTextField;
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
    	stage.close();	
    }

}