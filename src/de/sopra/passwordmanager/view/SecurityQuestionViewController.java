package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXTextField;
import de.sopra.passwordmanager.controller.CredentialsController;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import javafx.fxml.FXML;

import static de.sopra.passwordmanager.view.MainWindowViewController.WindowState.EDITED_ENTRY;
import static de.sopra.passwordmanager.view.MainWindowViewController.WindowState.START_EDITING_ENTRY;


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
        mainWindowViewController.changeState(START_EDITING_ENTRY, EDITED_ENTRY);
        mainWindowViewController.refreshEntry();
    }

}