package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.util.Path;

class CategoryItem {

    private Path path;
    private Category category;

    public CategoryItem(Path path, Category category) {
        this.path = path;
        this.category = category;
    }

    public Category getCategory() {
        return this.category;
    }

    public Path getPath() {
        return this.path;
    }

    @Override
    public String toString() {
        return this.path.getName() + " (" + this.path + ")";
    }
}
