package de.sopra.passwordmanager.util.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public abstract class SimpleConfirmation extends PasswordManagerDialog {

    private String title, headerText, contentText;

    public SimpleConfirmation(String title, String headerText, String text) {
        this.title = title;
        this.headerText = headerText;
        this.contentText = text;
    }

    public abstract void onSuccess();

    @Override
    public void onCancel() {

    }

    @Override
    public void open() {
        Alert alert = new Alert(alertType);
        alert.initStyle(style);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            onSuccess();
        } else {
            onCancel();
        }
    }



}