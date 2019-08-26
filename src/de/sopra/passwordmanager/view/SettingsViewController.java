package de.sopra.passwordmanager.view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class SettingsViewController {

	private MainWindowViewController mainWindowViewController;
	private Stage settingsStage, masterPasswordStage;

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
			setMasterPasswordScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			masterPasswordStage.setScene(setMasterPasswordScene);
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
		//nutzt gleiche onLoginClicked() Methode wie Hauptlogin, muss entsprechend überschrieben werden
		
	}

	public void onExportDataClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Speichere Datei");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
		File fileToSave = fileChooser.showSaveDialog(null);
		mainWindowViewController.getPasswordManagerController().getUtilityController().exportFile(fileToSave);
	}

	public void onResetDataClicked() {
		JDialog.setDefaultLookAndFeelDecorated(true);
	    int response = JOptionPane.showConfirmDialog(null, "PasswortManager wirklich zurücksetzen? Dieser Vorgang löscht alle Daten endgültig!", "",
	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	    if (response == JOptionPane.NO_OPTION) {
	      //System.out.println("");
	    } else if (response == JOptionPane.YES_OPTION) {
	      System.out.println("PasswortManager gelöscht.");
	      mainWindowViewController.getPasswordManagerController().removeAll();
	    } else if (response == JOptionPane.CLOSED_OPTION) {
	      //System.out.println("JOptionPane closed");
	    }
		
	}

	public void onCancelSettingsClicked() {
		settingsStage.close();
    }
	
	public void setStage(Stage settingsStage) {
		this.settingsStage = settingsStage;
	}
	
	

}
