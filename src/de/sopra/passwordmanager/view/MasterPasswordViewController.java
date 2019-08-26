package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSpinner;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MasterPasswordViewController implements MasterPasswordViewAUI {
	@FXML private JFXPasswordField passwordFieldSet;
	@FXML private JFXPasswordField passwordFieldCheck;
	@FXML private JFXSpinner spinnerReminderDays;
	@FXML private Label labelError;
	@FXML private JFXProgressBar progressBarQuality;
	
	private Stage stage;
	
    private MainWindowViewController mainWindowViewController;
    
    public void setStage(Stage primaryStage){
    	this.stage = primaryStage;
    }
    
    public void onSaveClicked() {
    	if (passwordFieldSet.getText().equals(passwordFieldCheck.getText())){
    		
    		int newReminder = Integer.parseInt(spinnerReminderDays.getAccessibleText()); 
    		mainWindowViewController.getPasswordManagerController().getMasterPasswordController().changePassword(passwordFieldSet.getText(), newReminder);
    	}else {
    		labelError.setVisible(true);
    	}
    	stage.close();
    }

    public void onPasswordChanged() {
    	//int quality = mainWindowViewController.getPasswordManagerController().getUtilityController().checkQuality(passwordFieldSet.getText());
    	//CheckQuality im UtilityController muss public sein
    	//refreshQuality(quality); 
    }
    
    public void onMasterPasswordCancelClicked(){
    	stage.close();
    }
    @Override
    public void refreshQuality(int quality) {
    	double progress = quality / 100;  
    	progressBarQuality.setProgress(progress); //progressBarQuality erwartet qualität zwischen 0 und 1
    	
    	if (progress<0.3){
    		progressBarQuality.setStyle("-fx-accent: red;");
    	}else if (progress>=0.3 && progress <= 0.6){
    		progressBarQuality.setStyle("-fx-accent: yellow;");
    	}else{
    		progressBarQuality.setStyle("-fx-accent: green;");
    	}
    }
}
