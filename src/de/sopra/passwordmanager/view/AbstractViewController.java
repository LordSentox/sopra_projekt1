package de.sopra.passwordmanager.view;

import java.util.Map;

import com.jfoenix.controls.JFXComboBox;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.util.Path;
import javafx.stage.Stage;

public abstract class AbstractViewController {
	protected Stage stage; 
	protected MainWindowViewController mainWindowViewController;
    
    public void setStage(Stage primaryStage){
    	this.stage = primaryStage;
    	
    }
    
    public void setMainWindowViewController(MainWindowViewController mainWindowViewController) {
		this.mainWindowViewController = mainWindowViewController;
	}
}
