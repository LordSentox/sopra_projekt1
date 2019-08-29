package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.util.Path;
import javafx.fxml.FXML;

import java.util.Map;

public class CategoryEditViewController extends AbstractViewController {
    @FXML
    private JFXTextField textFieldCategoryName;
    @FXML
    private JFXComboBox<CategoryItem> comboBoxCategorySelection;

    private Path current;

    public void setCurrentlyEdited(Path current) {
        this.current = current;
    }

    public void onSaveClicked() {
        //String categoryPath = comboBoxCategorySelection.getSelectionModel().getSelectedItem();
        //Category superCategory = mainWindowViewController.getPasswordManagerController().getPasswordManager().getRootCategory().getCategoryByPath(new Path(categoryPath));
        CategoryItem superCategory = comboBoxCategorySelection.getSelectionModel().getSelectedItem();
        if (current == null) {
            mainWindowViewController.getPasswordManagerController().getCategoryController().createCategory(superCategory.getCategory(), textFieldCategoryName.getText());
        } else {
            mainWindowViewController.getPasswordManagerController().getCategoryController().moveCategory(current, superCategory.getPath().createChildPath(textFieldCategoryName.getText()));
        }
        stage.close();
    }

    public void onCancelCategoryEditClicked() {
        stage.close();
    }
    public void onCloseClicked() {
    	stage.close();
    }
    public void initComboBox() {
        Category root = mainWindowViewController.getPasswordManagerController().getPasswordManager().getRootCategory();
        Map<Path, Category> cats = root.createPathMap(new Path());

        cats.keySet().stream()
                .map(path -> new CategoryItem(path, cats.get(path)))
                .forEach(comboBoxCategorySelection.getItems()::add);

        comboBoxCategorySelection.getItems().stream().filter(item -> item.getCategory().equals(root)).findFirst().get();
        if (current != null) {
            Category byPath = root.getCategoryByPath(current.getParent());
            comboBoxCategorySelection.getSelectionModel().select(comboBoxCategorySelection.getItems().stream().filter(item -> item.getCategory().equals(byPath)).findAny().get());
            textFieldCategoryName.setText(current.getName());
        } else {
            comboBoxCategorySelection.getSelectionModel().select(comboBoxCategorySelection.getItems().stream().filter(item -> item.getCategory().equals(root)).findFirst().get());
        }
    }
}
