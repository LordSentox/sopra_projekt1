package de.sopra.passwordmanager.view;

import java.util.ArrayList;

public interface MainWindowAUI {

	void refreshEntryList( ArrayList entries );

	void refreshEntry();

	void refreshEntry(String password);

	void refreshEntryPasswordQuality(int quality);

	void showError(String error);

}
