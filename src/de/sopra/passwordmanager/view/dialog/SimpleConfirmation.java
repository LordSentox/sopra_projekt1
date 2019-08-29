package de.sopra.passwordmanager.view.dialog;

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

    private String buttonOk1 = "Ok", buttonCancel = "Abbrechen";

    public SimpleConfirmation(String title, String headerText, String text) {
        this.title = title;
        this.headerText = headerText;
        this.contentText = text;
    }

    public abstract void onSuccess();

    @Override
    public void onCancel() {

    }

    public void setButtonOk(String name) {
        this.buttonOk1 = name;
    }

    public void setButtonCancel(String name) {
        this.buttonCancel = name;
    }

    @Override
    public void open() {
        Alert alert = new Alert(alertType);
        alert.initStyle(style);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        ButtonType buttonTypeOk = new ButtonType(buttonOk1);
        ButtonType buttonTypeCancel = new ButtonType(buttonCancel);

        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOk) {
            onSuccess();
        } else {
            onCancel();
        }
    }


}