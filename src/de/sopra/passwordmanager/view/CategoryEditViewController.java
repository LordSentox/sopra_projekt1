package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class CategoryEditViewController {
	@FXML private JFXTextField textFieldCategoryName;

    private Stage stage; 
    private MainWindowViewController mainWindowViewController;
    
    public void setStage(Stage primaryStage){
    	this.stage = primaryStage;
    }
    public void onSaveClicked() {
    	//FIXME MainWindowViewController benoetigt getter fuer superCatergory 
    	//mainWindowViewController.getPasswordManagerController().getCategoryController().createCategory(superCategory, textFieldCategoryName.getText());
    	stage.close();
    }
    public void onCancelCategoryEditClicked(){
    	stage.close();
    }
}
