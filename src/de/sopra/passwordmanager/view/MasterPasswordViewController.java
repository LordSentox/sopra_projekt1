package de.sopra.passwordmanager.view;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.UnaryOperator;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;

import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.view.dialog.SimpleDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class MasterPasswordViewController extends AbstractViewController implements MasterPasswordViewAUI {
	@FXML private JFXPasswordField passwordFieldSet;
	@FXML private JFXPasswordField passwordFieldCheck;
	@FXML private Spinner<Integer> spinnerReminderDays;
	@FXML private Label labelError;
	@FXML private JFXProgressBar progressBarQuality;
	
	public final TextFormatter<Integer> spinnerTextFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 1, 
			new UnaryOperator<TextFormatter.Change>(){

			NumberFormat format = NumberFormat.getIntegerInstance();
				@Override
				public Change apply(Change c) {
					if (c.isContentChange()) {
				        ParsePosition parsePosition = new ParsePosition(0);
				        // NumberFormat evaluates the beginning of the text
				        format.parse(c.getControlNewText(), parsePosition);
				        if (parsePosition.getIndex() == 0 ||
				                parsePosition.getIndex() < c.getControlNewText().length()) {
				            // reject parsing the complete text failed
				            return null;
				        }
				        //Länge begrenzen
				        if(c.getControlNewText().length() > 3){ 
				        	return null;
				        }
				        Integer number = Integer.parseInt(c.getControlNewText());
				        if(number < 1)
				        	return null;
				    }
				    return c;
				}} );
	
	private Stage stage, mainStage;
	
    private MainWindowViewController mainWindowViewController;
    
    private boolean openedBySettings = false;

    public void setMainWindowViewController(MainWindowViewController mainWindowViewController) {
        this.mainWindowViewController = mainWindowViewController;
    }
    
    public void setStage(Stage primaryStage){
    	this.stage = primaryStage;
    	//spinnerReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,999));
    }
    public void setMainStage(Stage mainStage){
    	this.mainStage = mainStage;
    }

    public void init(){
        spinnerReminderDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,999));
        spinnerReminderDays.getEditor().setTextFormatter(spinnerTextFormatter);

        passwordFieldSet.textProperty().addListener((obs, oldText, newText) -> {
            onPasswordChanged();
        });

    }
    
    public void openedBySettings(){
    	openedBySettings=true;
    }

    public void onSaveClicked() {
    	if (passwordFieldSet.getText().equals(passwordFieldCheck.getText())){
    		
    		int newReminder = spinnerReminderDays.getValue(); 
    		mainWindowViewController.getPasswordManagerController().getMasterPasswordController().changePassword(passwordFieldSet.getText(), newReminder);
    		stage.close();
    		if(!openedBySettings){
    			mainStage.show();
    		}
    		else {
    			SimpleDialog dialog = new SimpleDialog("Information", null, "Neues Masterpasswort erfolgreich gesetzt.");
    			dialog.setAlertType(AlertType.INFORMATION);
    			dialog.open();
    		}
            
    	}else {
    		labelError.setVisible(true);
    	}
    }


    public void onPasswordChanged() {
        String password = passwordFieldSet.getText();
        if (password != null) {
            //TODO change credentials to String in check Quality
            //XXX entfernen?
            CredentialsBuilder credBuilder = new CredentialsBuilder().withPassword(password);
            mainWindowViewController.getPasswordManagerController().getMasterPasswordController().checkQuality(password);
        }
    }
    
    public void onMasterPasswordCancelClicked(){
    	stage.close();
    }
    @Override
    public void refreshQuality(int quality) {
    	double progress = quality / 100.0;  
    	progressBarQuality.setProgress(progress); //progressBarQuality erwartet qualität zwischen 0 und 1

    	if (progress<0.3) {
    		progressBarQuality.setStyle("-fx-accent: red;");
    	} else if (progress>=0.3 && progress <= 0.6){
    		progressBarQuality.setStyle("-fx-accent: yellow;");
    	} else {
    		progressBarQuality.setStyle("-fx-accent: green;");
    	}
    }
}
