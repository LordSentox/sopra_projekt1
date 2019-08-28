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

    private EventHandler<MouseEvent> onCheckBoxClick = event -> {
        CheckBox chk = (CheckBox) event.getSource();
        String itemName = chk.getText();

        if (chk.isSelected()) {
            updateProvider(itemName, true);
        } else {
            updateProvider(itemName, false);
        }
    };

    public void setSelected(SelectableComboItem<T> target, boolean selected) {
        updateProvider(target.getItemName(), selected);
    }

    private void updateProvider(String itemName, boolean isSelected) {
        for (SelectableComboItem uiVO : getItems()) {
            if (uiVO.getItemName().equalsIgnoreCase(itemName)) {
                uiVO.setSelected(isSelected);
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
    }

}