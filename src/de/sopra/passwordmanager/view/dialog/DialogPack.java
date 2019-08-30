package de.sopra.passwordmanager.view.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 29.08.2019
 * @since 29.08.2019
 */
public class DialogPack {

    private String title, header, text;
    private Alert.AlertType alertType;
    private StageStyle stageStyle;

    private List<String> buttons;
    private List<Runnable> functions;

    private DialogPack next;

    public DialogPack(String title, String header, String text) {
        this.title = title;
        this.header = header;
        this.text = text;
        alertType = Alert.AlertType.CONFIRMATION;
        stageStyle = StageStyle.DECORATED;
        buttons = new LinkedList<>();
        functions = new LinkedList<>();
    }

    public DialogPack(String title, String header, String text, DialogPack next) {
        this(title, header, text);
        this.next = next;
    }

    public void addButton(String text, Runnable function) {
        if (function == null)
            function = () -> {
            };
        buttons.add(text);
        functions.add(function);
    }

    public void setNext(DialogPack next) {
        this.next = next;
    }

    public void open() {
        Alert alert = new Alert(alertType);
        alert.initStyle(stageStyle);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.getButtonTypes().setAll(buttons.stream().map(ButtonType::new).collect(Collectors.toList()));

        Optional<ButtonType> result = alert.showAndWait();
        Runnable run = functions.get(buttons.indexOf(result.get()));
        run.run();
    }

    public void openChained() {
        open();
        next.openChained();
    }

}