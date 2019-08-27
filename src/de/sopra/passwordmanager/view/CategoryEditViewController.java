package de.sopra.passwordmanager.view;

import java.util.Map;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.util.Path;
import javafx.fxml.FXML;

public class CategoryEditViewController extends AbstractViewController{
	@FXML private JFXTextField textFieldCategoryName;
	@FXML private JFXComboBox<CategoryItem> comboBoxCategorySelection;
    
    public void onSaveClicked() {
    	//String categoryPath = comboBoxCategorySelection.getSelectionModel().getSelectedItem();
    	//Category superCategory = mainWindowViewController.getPasswordManagerController().getPasswordManager().getRootCategory().getCategoryByPath(new Path(categoryPath));
    	CategoryItem superCategory = comboBoxCategorySelection.getSelectionModel().getSelectedItem();
    	mainWindowViewController.getPasswordManagerController().getCategoryController().createCategory(superCategory.getCategory(), textFieldCategoryName.getText());
    	stage.close();
    }
    
    public void onCancelCategoryEditClicked(){
    	stage.close();
    }
    
    public void initComboBox() {
    	Category root = mainWindowViewController.getPasswordManagerController().getPasswordManager().getRootCategory();
    	Map<Path, Category> cats = root.createPathMap(new Path());

    	cats.keySet().stream()
    		.map(path -> new CategoryItem(path, cats.get(path)))
    		.forEach(comboBoxCategorySelection.getItems()::add);
    	
    	comboBoxCategorySelection.getItems().stream().filter(item -> item.getCategory().equals(root)).findFirst().get();
    	
    	comboBoxCategorySelection.getSelectionModel().select(comboBoxCategorySelection.getItems().stream().filter(item -> item.getCategory().equals(root)).findFirst().get());
    }
}
