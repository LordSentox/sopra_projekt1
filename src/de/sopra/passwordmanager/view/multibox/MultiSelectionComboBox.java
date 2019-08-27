package de.sopra.passwordmanager.view.multibox;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class MultiSelectionComboBox extends ComboBox<CategorySelectItem> {

    private List<CategorySelectItem> listProvider;
    private List<String> listSelected;

    private MultiSelectionComboBox cmb = this;

    // Style.
    private static final String STYLE_BORDER_FILTER_PRESENT = "-fx-border-color:  #FF0000";

    public MultiSelectionComboBox() {
        init(new LinkedList<>());
        selectUnselectAll(true);
    }

    /********************************************** Constructor **************************************************/
    public MultiSelectionComboBox(List<CategorySelectItem> listProvider) {
        init(listProvider);
        // SELECT_ALL_INITIALLY:
        selectUnselectAll(true);
    }

    /************************************************ Init *******************************************************/
    private void init(List<CategorySelectItem> listProvider) {
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
    public void setListProvider(List<CategorySelectItem> listProvider) {
        init(listProvider); // It will set COMBO_SIZE also.
        setListSelected(); // It will set listSelected.
    }

    private void setListSelected() {
        for (CategorySelectItem uiVO : listProvider) {
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

    public List<CategorySelectItem> getListProvider() {
        return listProvider;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MultiSelectComboSkin(this);
    }

    /******************************************
     * Button Cell
     ***********************************************************/
    private ListCell<CategorySelectItem> buttonCell = new ListCell<CategorySelectItem>() {
        protected void updateItem(CategorySelectItem item, boolean empty) {
            super.updateItem(item, empty);
            setText("");
        }
    };
    /******************************************
     * Call Back Factory
     ******************************************************/
    private final Callback<ListView<CategorySelectItem>, ListCell<CategorySelectItem>> cb = new Callback<ListView<CategorySelectItem>, ListCell<CategorySelectItem>>() {

        @Override
        public ListCell<CategorySelectItem> call(ListView<CategorySelectItem> param) {
            ListCell<CategorySelectItem> cell = new ListCell<CategorySelectItem>() {
                @Override
                protected void updateItem(CategorySelectItem item, boolean empty) {
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

    /*******************************************
     * Control Button Click
     *************************************************/
    private EventHandler<Event> onButtonClick = event -> {
        Button btn = (Button) event.getSource();
        MultiSelectComboEvent e;
        if (btn.getText().equalsIgnoreCase("Ok")) {
            e = new MultiSelectComboEvent(cmb, MultiSelectComboEvent.EVENT_OK);
        } else {
            e = new MultiSelectComboEvent(cmb, MultiSelectComboEvent.EVENT_CANCEL);
        }
        fireEvent(e);
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
            for (CategorySelectItem uiVO : getItems()) {
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
        for (CategorySelectItem uiVO : getItems()) {
            if (uiVO.getItemName().equalsIgnoreCase(itemName)) {
                uiVO.setSelected(isSelected);
                break;
            }
        }
        // UPDATE_ONLY_SELECT_ALL:
        updateProvider();
    }

    private void updateProvider() {
        List<CategorySelectItem> listTemp = new ArrayList<>();
        for (CategorySelectItem uiVO : getItems()) {
            listTemp.add(uiVO);
        }
        getItems().clear();
        setItems(FXCollections.observableArrayList(listTemp));
    }

    private void updateProvider(boolean isSelected) {
        List<CategorySelectItem> listTemp = new ArrayList<>();
        for (CategorySelectItem uiVO : getItems()) {
            uiVO.setSelected(isSelected);
            listTemp.add(uiVO);
        }
        getItems().clear();
        setItems(FXCollections.observableArrayList(listTemp));
    }

}