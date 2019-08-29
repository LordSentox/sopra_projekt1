package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.util.Path;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;

public class CategoryEditViewController extends AbstractViewController {
    @FXML
    private JFXTextField textFieldCategoryName;

    @FXML
    private JFXButton buttonSave,buttonCancel;

    @FXML
    private Label labelChooseCategory, labelCategoryName;

    @FXML
    private JFXComboBox<CategoryItem> comboBoxCategorySelection;

    private Path current;

    private boolean shouldAdd;

    public void setShouldAdd(boolean shouldAdd) {
        this.shouldAdd = shouldAdd;
    }

    public void setCurrentlyEdited(Path current) {
        this.current = current;
    }

    public void onSaveClicked() {
        //String categoryPath = comboBoxCategorySelection.getSelectionModel().getSelectedItem();
        //Category superCategory = mainWindowViewController.getPasswordManagerController().getPasswordManager().getRootCategory().getCategoryByPath(new Path(categoryPath));
        CategoryItem superCategory = comboBoxCategorySelection.getSelectionModel().getSelectedItem();
        String text = textFieldCategoryName.getText().trim();
        if (shouldAdd) {
            mainWindowViewController.getPasswordManagerController().getCategoryController().createCategory(superCategory.getCategory(), text);
        } else {
            mainWindowViewController.getPasswordManagerController().getCategoryController().moveCategory(current, superCategory.getPath().createChildPath(text));
        }
        stage.close();
    }

    public void onCancelCategoryEditClicked() {
        stage.close();
    }

    public void onCloseClicked() {
        stage.close();
    }

    public void init() {
        Category root = mainWindowViewController.getPasswordManagerController().getPasswordManager().getRootCategory();
        Map<Path, Category> cats = root.createPathMap(new Path());

        cats.keySet().stream()
                .map(path -> new CategoryItem(path, cats.get(path)))
                .forEach(comboBoxCategorySelection.getItems()::add);

        Category selecting = current == null ? root : (shouldAdd ? root.getCategoryByPath(current) : root.getCategoryByPath(current.getParent()));

        CategoryItem select = comboBoxCategorySelection.getItems().stream()
                .filter(item -> item.getCategory().equals(selecting)).findFirst().get();
        comboBoxCategorySelection.getSelectionModel().select(select);

        if (current != null && !shouldAdd) {
            textFieldCategoryName.textProperty().set(current.getName());
        }

        if (current == null)
            buttonSave.setDisable(true);

        textFieldCategoryName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty())
                buttonSave.setDisable(true);
            else buttonSave.setDisable(false);
        });

    }
}
