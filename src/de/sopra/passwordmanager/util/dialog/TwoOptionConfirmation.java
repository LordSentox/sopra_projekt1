package de.sopra.passwordmanager.util.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class TwoOptionConfirmation extends PasswordManagerDialog {

    private String option1, option2;
    private Runnable run1, run2;

    private String title, headerText, contentText;

    public TwoOptionConfirmation(String title, String headerText, String text) {
        this.title = title;
        this.headerText = headerText;
        this.contentText = text;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setRun1(Runnable run1) {
        this.run1 = run1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setRun2(Runnable run2) {
        this.run2 = run2;
    }

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

        ButtonType buttonTypeOne = new ButtonType(option1);
        ButtonType buttonTypeTwo = new ButtonType(option2);
        ButtonType buttonTypeCancel = new ButtonType("Abbrechen", CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            run1.run();
        } else if (result.get() == buttonTypeTwo) {
            run2.run();
        } else {
            onCancel();
        }
    }
}