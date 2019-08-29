package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXPasswordField;
import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.view.dialog.SimpleConfirmation;
import de.sopra.passwordmanager.view.dialog.SimpleDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static de.sopra.passwordmanager.view.MainWindowViewController.WindowState.VIEW_ENTRY;

import java.io.File;
import java.io.IOException;

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
    public void onCloseClicked(){
    	stage.close();
    }
    @Override
    public void handleLoginResult(boolean result) {
        if (result) {
            if(mainWindowViewController.getPasswordManagerController().getMasterPasswordController().hasToBeChanged() && sourceFile.equals(PasswordManagerController.SAVE_FILE) ){
                
            	System.out.println("lol");
                SimpleConfirmation confirmation = new SimpleConfirmation("Information",
                        "",
                        "Das Masterpassswort ist abgelaufen. Wollen Sie es jetzt ändern?") {
                    @Override
                    public void onSuccess() {
                    	MasterPasswordViewController masterPasswordViewController;
						try {
							masterPasswordViewController = mainWindowViewController.openModal("/Masterpasswort-setzen.fxml",
							        MasterPasswordViewController.class, preOpen ->
							        {
							            preOpen.setBackTo(backTo);
							            preOpen.init();
							        });
	                        //set AUI link
	                        mainWindowViewController.getPasswordManagerController().setMasterPasswordViewAUI(masterPasswordViewController);
						} catch (IOException e) {
							
							e.printStackTrace();
						}


                    }

                    @Override
                    public void onCancel() {
                        backTo.show();
                    }
                };
                //confirmation.setAlertType(AlertType.CONFIRMATION);
                confirmation.open();  
                stage.close();
            } else {
                stage.close();
            	backTo.show();
            }
            



        } else {
            labelError.setVisible(true);
        }
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
}
