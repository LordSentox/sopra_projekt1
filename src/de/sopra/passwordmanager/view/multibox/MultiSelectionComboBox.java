package de.sopra.passwordmanager.view.multibox;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class MultiSelectionComboBox<T> extends ComboBox<SelectableComboItem<T>> {

    private List<SelectableComboItem<T>> listProvider;
    private List<String> listSelected;

    private MultiSelectionComboBox cmb = this;

    // Style.
    private static final String STYLE_BORDER_FILTER_PRESENT = "-fx-border-color:  #FF0000";

    public MultiSelectionComboBox() {
        init(new LinkedList<>());
        selectUnselectAll(true);
    }

    /********************************************** Constructor **************************************************/
    public MultiSelectionComboBox(List<SelectableComboItem<T>> listProvider) {
        init(listProvider);
        // SELECT_ALL_INITIALLY:
        selectUnselectAll(true);
    }

    /************************************************ Init *******************************************************/
    private void init(List<SelectableComboItem<T>> listProvider) {
        listSelected = new ArrayList<>();

        // Provider.
        this.listProvider = listProvider;
        setItems(FXCollections.observableArrayList(this.listProvider));

        // Button cell.
        setButtonCell(buttonCell);

        // Cell Factory.
        setCellFactory(cb);
    }

    /******************************************************* Provider *************************************************/
    public void setListProvider(List<SelectableComboItem<T>> listProvider) {
        init(listProvider); // It will set COMBO_SIZE also.
        setListSelected(); // It will set listSelected.
    }

    private void setListSelected() {
        for (SelectableComboItem uiVO : listProvider) {
            if (uiVO.isSelected()) {
                listSelected.add(uiVO.getItemName());
            }
        }
    }

    /******************************************
     * Selected Items
     **********************************************/
    public List<String> getListSelected() {
        return listSelected;
    }

    public List<SelectableComboItem<T>> getListProvider() {
        return listProvider;
    }

    public List<T> getSelectedContentList() {
        return getListProvider().stream()
                .filter(SelectableComboItem::isSelected)
                .map(item -> item.getContent())
                .collect(Collectors.toList());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MultiSelectComboSkin(this);
    }

    /******************************************
     * Button Cell
     ***********************************************************/
    private ListCell<SelectableComboItem<T>> buttonCell = new ListCell<SelectableComboItem<T>>() {
        protected void updateItem(SelectableComboItem item, boolean empty) {
            super.updateItem(item, empty);
            setText("");
        }
    };
    /******************************************
     * Call Back Factory
     ******************************************************/
    private final Callback<ListView<SelectableComboItem<T>>, ListCell<SelectableComboItem<T>>> cb = new Callback<ListView<SelectableComboItem<T>>, ListCell<SelectableComboItem<T>>>() {

        @Override
        public ListCell<SelectableComboItem<T>> call(ListView<SelectableComboItem<T>> param) {
            ListCell<SelectableComboItem<T>> cell = new ListCell<SelectableComboItem<T>>() {
                @Override
                protected void updateItem(SelectableComboItem<T> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        CheckBox cb = new CheckBox(item.getItemName());
                        cb.setSelected(item.isSelected());
                        cb.addEventHandler(MouseEvent.MOUSE_CLICKED, onCheckBoxClick);
                        setGraphic(cb);
                    } else {
                        setGraphic(null);
                    }
                }
            };
            return cell;
        }
    };

    /**************************************************
     * Combo Check
     ****************************************************/
    private EventHandler<MouseEvent> onCheckBoxClick = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            CheckBox chk = (CheckBox) event.getSource();
            String itemName = chk.getText();

            if (chk.isSelected()) {
                if (!listSelected.contains(itemName)) {
                    listSelected.add(itemName);
                    updateProvider(itemName, true);
                }
            } else {
                if (listSelected.contains(itemName)) {
                    listSelected.remove(itemName);
                    updateProvider(itemName, false);
                }
            }

            // changeComboColor();
        }
    };

    /***************************************************
     * Select/UnSelect All
     ***********************************************************/
    private void selectUnselectAll(boolean check) {
        if (check) {
            for (SelectableComboItem uiVO : getItems()) {
                if (!listSelected.contains(uiVO.getItemName())) {
                    listSelected.add(uiVO.getItemName());
                }
            }
        } else {
            listSelected = new ArrayList<>();
        }
        updateProvider(check);
    }

    /**************************************************
     * Update Provider
     ****************************************************/
    private void updateProvider(String itemName, boolean isSelected) {
        // UPDATE_SELECTED_ITEM:
        for (SelectableComboItem uiVO : getItems()) {
            if (uiVO.getItemName().equalsIgnoreCase(itemName)) {
                uiVO.setSelected(isSelected);
                break;
            }
        }
        // UPDATE_ONLY_SELECT_ALL:
        updateProvider();
    }

    private void updateProvider() {
        List<SelectableComboItem<T>> listTemp = new ArrayList<>();
        listTemp.addAll(getItems());
        getItems().clear();
        setItems(FXCollections.observableArrayList(listTemp));
    }

    private void updateProvider(boolean isSelected) {
        List<SelectableComboItem<T>> listTemp = new ArrayList<>();
        for (SelectableComboItem<T> uiVO : getItems()) {
            uiVO.setSelected(isSelected);
            listTemp.add(uiVO);
        }
        getItems().clear();
        setItems(FXCollections.observableArrayList(listTemp));
    }

}