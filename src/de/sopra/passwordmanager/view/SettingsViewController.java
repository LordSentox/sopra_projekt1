package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.view.dialog.SimpleConfirmation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;

public class SettingsViewController extends AbstractViewController {

    private MainWindowViewController mainWindowViewController;
    private Stage settingsStage, loginStage;


    public void setMainWindowViewController(MainWindowViewController mainWindowViewController) {
        this.mainWindowViewController = mainWindowViewController;
    }

    public void onChangeMasterpasswordClicked() {
        try {

            openModal(settingsStage, "../view/Masterpasswort-setzen.fxml", MasterPasswordViewController.class, control -> {control.init(); control.openedBySettings();});


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void onImportDataClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öffne Datei");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
        File fileToOpen = fileChooser.showOpenDialog(settingsStage);

        if(fileToOpen != null) {
	        try {
	            openModal("../view/Einloggen.fxml", LoginViewController.class, controller -> controller.setSourceFile(fileToOpen));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
    }

    public void onExportDataClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere Datei");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
        File fileToSave = fileChooser.showSaveDialog(settingsStage);
        if(fileToSave != null)
        	mainWindowViewController.getPasswordManagerController().getIOController().exportFile(fileToSave);
    }

    public void onResetDataClicked() {

        SimpleConfirmation removeConfirmation = new SimpleConfirmation("Passwortmanager zurücksetzen", null, "Passwortmanager wirklich zurücksetzen?") {
            @Override
            public void onSuccess() {
                mainWindowViewController.getPasswordManagerController().removeAll();
            }
        };

        removeConfirmation.open();

    }

    public void onCancelSettingsClicked() {
        settingsStage.close();
    }

    public void setStage(Stage settingsStage) {
        this.settingsStage = settingsStage;
    }


}
