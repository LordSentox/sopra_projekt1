package de.sopra.passwordmanager.util.dialog;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author Etienne
 */
public class DialogTest {

    private boolean jfxIsSetup;

    private static class DummyPasswordManagerDialog extends PasswordManagerDialog {

        @Override
        void open() {}

        @Override
        public void onCancel() {}

        StageStyle getStyle() {
            return style;
        }

        Alert.AlertType getAlertType() {
            return alertType;
        }
    }

    private static class SimpleConfirmationDummy extends SimpleConfirmation{

        SimpleConfirmationDummy(String title, String headerText, String text) {
            super(title, headerText, text);
        }

        @Override
        public void onSuccess() {
            System.out.println("nice");
        }
    }

    @Test
    public void simpleConfirmationTest() {
        doOnJavaFXThread(() -> {
            SimpleConfirmationDummy confirmation = new SimpleConfirmationDummy("Hey", "header", "text");
            confirmation.onSuccess();
            confirmation.onCancel();
            confirmation.open();
        });
    }

    @Test
    public void simpleDialogTest() {
        doOnJavaFXThread(() -> {
            SimpleDialog dialog = new SimpleDialog("title", "header", "text");
            dialog.onCancel();
            dialog.open();
        });
    }

    @Test
    public void passwordManagerDialogTest() {
        DummyPasswordManagerDialog dialog = new DummyPasswordManagerDialog();
        dialog.onCancel();
        dialog.open();
        dialog.setStyle(StageStyle.UTILITY);
        Assert.assertEquals("Styles not equal", StageStyle.UTILITY, dialog.getStyle());
        dialog.setAlertType(Alert.AlertType.ERROR);
        Assert.assertEquals("Alert type not equal", Alert.AlertType.ERROR, dialog.getAlertType());
    }

    @Test
    public void threeOptionConfirmationTest() {
        doOnJavaFXThread(() -> {
            ThreeOptionConfirmation confirmation = new ThreeOptionConfirmation("threeOption", "header", "text");
            confirmation.setOption1("1");
            confirmation.setOption2("2");
            confirmation.setOption3("3");
            confirmation.setRun1(() -> System.out.println(1));
            confirmation.setRun2(() -> System.out.println(2));
            confirmation.setRun3(() -> System.out.println(3));
            confirmation.open();
            confirmation.onCancel();
        });
    }

    @Test
    public void twoOptionConfirmationTest() {
        doOnJavaFXThread(() -> {
            TwoOptionConfirmation confirmation = new TwoOptionConfirmation("twoOption", "header", "text");
            confirmation.setOption1("1");
            confirmation.setOption2("2");
            confirmation.setRun1(() -> System.out.println(1));
            confirmation.setRun2(() -> System.out.println(2));
            confirmation.open();
            confirmation.onCancel();
        });
    }

    private void doOnJavaFXThread(Runnable pRun) throws RuntimeException {
        if (!jfxIsSetup) {
            setupJavaFX();
            jfxIsSetup = true;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            pRun.run();
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupJavaFX() throws RuntimeException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.setImplicitExit(false);
        SwingUtilities.invokeLater(() -> {
            JFXPanel panel = new JFXPanel(); // initializes JavaFX environment
            panel.setSize(100, 100);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
