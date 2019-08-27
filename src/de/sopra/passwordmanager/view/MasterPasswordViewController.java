package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

public class MasterPasswordViewController implements MasterPasswordViewAUI {
	@FXML private JFXPasswordField passwordFieldSet;
	@FXML private JFXPasswordField passwordFieldCheck;
	@FXML private Spinner<Integer> spinnerReminderDays;
	@FXML private Label labelError;
	@FXML private JFXProgressBar progressBarQuality;
	
	private Stage stage, mainStage;
	
    private MainWindowViewController mainWindowViewController;

    public void setMainWindowViewController(MainWindowViewController mainWindowViewController) {
        this.mainWindowViewController = mainWindowViewController;
    }
    
    public void setStage(Stage primaryStage){
    	this.stage = primaryStage;
    	spinnerReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,999));
    }
    public void setMainStage(Stage mainStage){
    	this.mainStage = mainStage;
    }
    
    public void onSaveClicked() {
    	if (passwordFieldSet.getText().equals(passwordFieldCheck.getText())){
    		
    		int newReminder = spinnerReminderDays.getValue(); 
    		mainWindowViewController.getPasswordManagerController().getMasterPasswordController().changePassword(passwordFieldSet.getText(), newReminder);
    		mainStage.show();
            stage.close();
    	}else {
    		labelError.setVisible(true);
    	}
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
