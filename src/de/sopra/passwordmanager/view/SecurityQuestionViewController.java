package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import javafx.fxml.FXML;


public class SecurityQuestionViewController extends AbstractViewController {

    @FXML
    private JFXTextField textFieldQuestion, textFieldAnswer;
    @FXML
    private JFXButton buttonSave, buttonCancel;
    @FXML
    private Label labelQuestion, labelAnswer;
    
    
    public void init(){
    	textFieldQuestion.textProperty().addListener((obs, oldText, newText) -> {
            onQuestionOrAnswerChanged();
        });
    	textFieldAnswer.textProperty().addListener((obs, oldText, newText) -> {
            onQuestionOrAnswerChanged();
        });

        buttonSave.setDisable(true);
    }

    public void onSecurityQuestionCancelClicked() {
		mainWindowViewController.masterPassordIsShit();
		
        stage.close();
    }
    public void onCloseClicked(){
		mainWindowViewController.masterPassordIsShit();
		
    	stage.close();
    }
    
    public void onQuestionOrAnswerChanged() {
        String question = textFieldQuestion.getText();
        String answer = textFieldAnswer.getText();
        if (question != null && !question.isEmpty() && answer != null && !answer.isEmpty()) {
            buttonSave.setDisable(false);
        } else {
        	buttonSave.setDisable(true);
        }
    }
    

    public void onSaveClicked() {
		mainWindowViewController.masterPassordIsShit();
		
        CredentialsBuilder credBuilder = mainWindowViewController.getCredentialsBuilder();
        CredentialsController credController = mainWindowViewController.getPasswordManagerController().getCredentialsController();
        credController.addSecurityQuestion(textFieldQuestion.getText(), textFieldAnswer.getText(), credBuilder);
        stage.close();
        mainWindowViewController.refreshEntry();
    }

}