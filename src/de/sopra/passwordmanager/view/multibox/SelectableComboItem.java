package de.sopra.passwordmanager.view.multibox;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class SelectableComboItem<T> {

    private T content;
    private boolean isSelected;

    public SelectableComboItem(T content) {
        this.content = content;
        this.isSelected = false;
    }

    public T getContent() {
        return content;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getItemName() {
        return content.toString();
    }

}