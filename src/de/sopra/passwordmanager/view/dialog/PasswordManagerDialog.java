package de.sopra.passwordmanager.view.dialog;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public abstract class PasswordManagerDialog {

    protected StageStyle style = StageStyle.DECORATED;
    protected Alert.AlertType alertType = Alert.AlertType.CONFIRMATION;

    public void setAlertType(Alert.AlertType alertType) {
        this.alertType = alertType;
    }

    public final void setStyle(StageStyle style) {
        this.style = style;
    }

    abstract void open();

    public abstract void onCancel();

}