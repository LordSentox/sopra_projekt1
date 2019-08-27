package de.sopra.passwordmanager.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;

public class SettingsViewController {

	private MainWindowViewController mainWindowViewController;
	private Stage settingsStage, masterPasswordStage;


    public void setMainWindowViewController(MainWindowViewController mainWindowViewController) {
        this.mainWindowViewController = mainWindowViewController;
    }
	
	public void onChangeMasterpasswordClicked() {
		try {
			/* MasterpasswortSetzenFenster */
			AnchorPane setMasterPasswordPane = new AnchorPane();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Masterpasswort-setzen.fxml"));
			setMasterPasswordPane = fxmlLoader.load();
			MasterPasswordViewController masterPasswordViewController = (MasterPasswordViewController) fxmlLoader
					.getController();

			masterPasswordStage = new Stage();
			Scene setMasterPasswordScene = new Scene(setMasterPasswordPane);
			//setMasterPasswordScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			masterPasswordStage.setScene(setMasterPasswordScene);
			masterPasswordViewController.setStage(masterPasswordStage);
			masterPasswordViewController.setMainWindowViewController(mainWindowViewController);
			masterPasswordStage.show();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void onImportDataClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Öffne Datei");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
		File fileToOpen = fileChooser.showOpenDialog(null);
		//TODO UtilityController -> import
		//Login Fenster wird geöffnet und richtige Vorraussetzungen müssen für onLoginClicked gegeben sein
		
	}

	public void onExportDataClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Speichere Datei");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
		File fileToSave = fileChooser.showSaveDialog(null);
		mainWindowViewController.getPasswordManagerController().getUtilityController().exportFile(fileToSave);
	}

	public void onResetDataClicked() {
	    // TODO
	    Alert alertDialog = new Alert(AlertType.CONFIRMATION);

        ButtonType buttonTypeYes = new ButtonType("Ja");
        ButtonType buttonTypeNo = new ButtonType("Nein");

        alertDialog.setHeaderText("PasswortManager wirklich zurücksetzen?");
        alertDialog.setContentText("Dieser Vorgang löscht alle Daten endgültig!");
        alertDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        alertDialog.showAndWait();

        ButtonType result = alertDialog.getResult();
        if (result == buttonTypeYes) {
            System.out.println("PasswortManager gelöscht");
        } else if (result == buttonTypeNo) {
            System.out.println("Doch nicht löschen");
        } 
		
	}

	public void onCancelSettingsClicked() {
		settingsStage.close();
    }
	
	public void setStage(Stage settingsStage) {
		this.settingsStage = settingsStage;
	}
	
	

}
