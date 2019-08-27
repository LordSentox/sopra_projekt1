package de.sopra.passwordmanager.view.multibox;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class MultiSelectComboSkin extends ComboBoxListViewSkin<SelectableComboItem> {

    public MultiSelectComboSkin(MultiSelectionComboBox comboBox) {
        super(comboBox);
    }

    protected boolean isHideOnClickEnabled() {
        return false;
    }

}