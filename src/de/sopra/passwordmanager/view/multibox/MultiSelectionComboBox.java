package de.sopra.passwordmanager.view.multibox;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListCell;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
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

    private EventHandler<MouseEvent> onCheckBoxClick = event -> {
        JFXCheckBox chk = (JFXCheckBox) event.getSource();
        String itemName = chk.getText();

        if (chk.isSelected()) {
            updateProvider(itemName, true);
        } else {
            updateProvider(itemName, false);
        }
    };

    public MultiSelectionComboBox() {
        init(new LinkedList<>());
    }

    public MultiSelectionComboBox(List<SelectableComboItem<T>> listProvider) {
        init(listProvider);
    }

    private void init(List<SelectableComboItem<T>> listProvider) {
        this.listProvider = listProvider;
        setItems(FXCollections.observableArrayList(this.listProvider));
        setCellFactory(cb);
    }

    public void setListProvider(List<SelectableComboItem<T>> listProvider) {
        init(listProvider);
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

    private final Callback<ListView<SelectableComboItem<T>>, ListCell<SelectableComboItem<T>>> cb =
            new Callback<ListView<SelectableComboItem<T>>, ListCell<SelectableComboItem<T>>>() {

                @Override
                public ListCell<SelectableComboItem<T>> call(ListView<SelectableComboItem<T>> param) {
                    ListCell<SelectableComboItem<T>> cell = new JFXListCell<SelectableComboItem<T>>() {
                        @Override
                        public void updateItem(SelectableComboItem<T> item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!empty && item != null) {
                                JFXCheckBox cb = new JFXCheckBox(item.getItemName());
                                cb.setSelected(item.isSelected());
                                cb.setFocusTraversable(false);
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

    public void setSelected(SelectableComboItem<T> target, boolean selected) {
        updateProvider(target.getItemName(), selected);
    }

    private void updateProvider(String itemName, boolean isSelected) {
        for (SelectableComboItem item : getItems()) {
            if (item.getItemName().equalsIgnoreCase(itemName)) {
                item.setSelected(isSelected);
                break;
            }
        }
        updateProvider();
    }

    private void updateProvider() {
        List<SelectableComboItem<T>> listTemp = new ArrayList<>();
        listTemp.addAll(getItems());
        getItems().clear();
        setItems(FXCollections.observableArrayList(listTemp));
        //if none is selected, select the first entry
        if (getSelectedContentList().isEmpty()) {
            setSelected(getListProvider().get(0), true);
        }
    }

}