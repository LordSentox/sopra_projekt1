package de.sopra.passwordmanager.util.dialog;

import javafx.scene.control.Alert;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class SimpleDialog extends PasswordManagerDialog {

    private String title, headerText, contentText;

    public SimpleDialog(String title, String headerText, String text) {
        this.title = title;
        this.headerText = headerText;
        this.contentText = text;
    }

    @Override
    public void open() {
        Alert alert = new Alert(alertType);
        alert.initStyle(style);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    @Override
    public final void onCancel() {

    }

}