package de.sopra.passwordmanager.view.multibox;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.util.Path;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class CategorySelectItem {

    private Path categoryPath;
    private Category category;
    private boolean isSelected;

    public CategorySelectItem(Path categoryPath, Category category) {
        this.categoryPath = categoryPath;
        this.category = category;
        this.isSelected = false;
    }

    public CategorySelectItem(String control, boolean selected) {
        this.category = new Category(control);
        this.isSelected = selected;
    }

    public Category getCategory() {
        return category;
    }

    public Path getCategoryPath() {
        return categoryPath;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getItemName() {
        return category.getName() + " - " + getCategoryPath().toString();
    }

}