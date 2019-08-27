package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.util.Path;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class CategoryEditViewController extends AbstractViewController{
	@FXML private JFXTextField textFieldCategoryName;
	@FXML private JFXComboBox<Category> comboBoxCategorySelection;
    
    public void onSaveClicked() {
    	//String categoryPath = comboBoxCategorySelection.getSelectionModel().getSelectedItem();
    	//Category superCategory = mainWindowViewController.getPasswordManagerController().getPasswordManager().getRootCategory().getCategoryByPath(new Path(categoryPath));
    	Category superCategory = comboBoxCategorySelection.getSelectionModel().getSelectedItem();
    	mainWindowViewController.getPasswordManagerController().getCategoryController().createCategory(superCategory, textFieldCategoryName.getText());
    	stage.close();
    }
    
    public void onCancelCategoryEditClicked(){
    	stage.close();
    }
	
    void initComboBox() {
    	Category root = mainWindowViewController.getPasswordManagerController().getPasswordManager().getRootCategory();
    	
    	
    	
    	
    	
    	comboBoxCategorySelection.getItems().add(root);
    }
}
