package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.model.Credentials;

import java.util.List;

public interface MainWindowAUI {

	void refreshEntryList(List<Credentials> entries );

	void refreshEntry();

	void refreshEntry(String password);

	void refreshEntryPasswordQuality(int quality);

	void showError(String error);

}
