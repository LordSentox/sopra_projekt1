package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.util.dialog.SimpleConfirmation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;

public class SettingsViewController extends AbstractViewController{

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
			MasterPasswordViewController masterPasswordViewController = (MasterPasswordViewController) fxmlLoader.getController();

			masterPasswordStage = new Stage();
			Scene setMasterPasswordScene = new Scene(setMasterPasswordPane);
			//setMasterPasswordScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			masterPasswordStage.setScene(setMasterPasswordScene);
			masterPasswordViewController.setStage(masterPasswordStage);
			masterPasswordViewController.setMainWindowViewController(mainWindowViewController);
			masterPasswordViewController.init();
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
