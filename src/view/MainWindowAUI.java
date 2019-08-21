package Model;

import java.util.ArrayList;

public interface MainWindowAUI {

	public abstract void refreshEntryList(ArrayList entries);

	public abstract void refreshEntry();

	public abstract void refreshEntry(String password);

	public abstract void refreshEntryPasswordQuality(int quality);

	public abstract void showError(String error);

}
