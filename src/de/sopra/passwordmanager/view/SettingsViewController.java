package de.sopra.passwordmanager.view;

public class SettingsViewController {

	private MainWindowViewController mainWindowViewController;

	public void onChangeMasterpasswordClicked() {

	}

	public void onImportDataClicked() {

	}

	public void onExportDataClicked() {

	}
    
	public void onResetDataClicked() {
		//TODO: Sicherheitsabfrage
		mainWindowViewController.getPasswordManagerController()
		.removeAll();
	}

}
