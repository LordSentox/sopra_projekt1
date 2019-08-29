package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.view.dialog.SimpleConfirmation;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;

public class SettingsViewController extends AbstractViewController {

    public void onChangeMasterpasswordClicked() {
        try {
            /* MasterpasswortSetzenFenster */
     
            openModal(stage, "../view/Masterpasswort-setzen.fxml", MasterPasswordViewController.class, control -> {control.init(); control.openedBySettings();});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void onImportDataClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öffne Datei");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
        File fileToOpen = fileChooser.showOpenDialog(stage);
        if(fileToOpen != null) {
	        try {
	            openModal(stage,"../view/Einloggen.fxml", LoginViewController.class, controller ->
                {
                    controller.setSourceFile(fileToOpen);
                    controller.setBackTo(stage);
                });
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
    }

    public void onExportDataClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere Datei");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
        File fileToSave = fileChooser.showSaveDialog(stage);
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
        stage.close();
    }
    public void onCloseClicked() {
    	stage.close();
    }

}
