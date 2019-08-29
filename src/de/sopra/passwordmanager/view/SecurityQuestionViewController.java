package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXTextField;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import javafx.fxml.FXML;


public class SecurityQuestionViewController extends AbstractViewController {

    @FXML
    private JFXTextField textFieldQuestion, textFieldAnswer;

    public void onSecurityQuestionCancelClicked() {
        stage.close();
    }
    public void onCloseClicked(){
    	stage.close();
    }

    public void onSaveClicked() {
        CredentialsBuilder credBuilder = mainWindowViewController.getCredentialsBuilder();
        CredentialsController credController = mainWindowViewController.getPasswordManagerController().getCredentialsController();
        credController.addSecurityQuestion(textFieldQuestion.getText(), textFieldAnswer.getText(), credBuilder);
        stage.close();
        mainWindowViewController.refreshEntry();
    }

}