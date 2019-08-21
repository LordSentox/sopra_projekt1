package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.view.MainWindowAUI;

import java.util.List;

/**
 * Kontrolliert alle Aktionen, die mit {@link Credentials} zusammenhängen
 * @see Credentials
 */
public class CredentialsController {

	private PasswordManagerController passwordManagerController;

	public CredentialsController( PasswordManagerController controller )
	{
		this.passwordManagerController = controller;
	}

	/**
	 * Überschreibt alte Anmeldedaten mit Neuen im {@link PasswordManager}
	 * @param oldCredentials Die zu überschreibenden Anmeldedaten
	 * @param newCredentials Die neuen Anmeldedaten, die die Alten überschreiben
	 * @see Credentials
	 */
	public void saveCredentials( Credentials oldCredentials, Credentials newCredentials) {

	}

	/**
	 * Entfernt die gegebenen Anmeldedaten aus dem {@link PasswordManager}
	 * @param credentials Die zu entfernenden Anmeldedaten
	 * @see Credentials
	 */
	public void removeCredentials(Credentials credentials) {

	}

	/**
	 * Fügt dem Anmeldedatenobjekt eine {@link SecurityQuestion} hinzu
	 * @param question Die Frage der {@link SecurityQuestion}
	 * @param answer Die Antwort auf die Frage
	 * @param credentials Das Anmeldedatenobjekt, dem die {@link SecurityQuestion} hinzugefügt werden soll
	 * @see SecurityQuestion
	 * @see Credentials
	 */
	public void addSecurityQuestion(String question, String answer, Credentials credentials) {

	}

	/**
	 * Entfernt die gegebene {@link SecurityQuestion} von den gegebenen {@link Credentials}
	 * @param securityQuestion Die {@link SecurityQuestion}, die von den gegebenen {@link Credentials} entfernt werden sollen
	 * @param credentials Die {@link Credentials}, von der die {@link SecurityQuestion} entfernt werden soll
	 * @see SecurityQuestion
	 * @see Credentials
	 */
	public void removeSecurityQuestion( SecurityQuestion securityQuestion, Credentials credentials) {

	}

	/**
	 * Filtert alle {@link Credentials} im {@link PasswordManager} nach Kategorie und nach Inhalt seines Namen und refresht
	 * die Liste der Einträge durch {@link MainWindowAUI#refreshEntryList(List)}
	 * @param categoryPath Der Pfad der {@link Category} in der alle gewünschten {@link Credentials} liegen sollen
	 * @param pattern Ein String, der im Namen der {@link Credentials} enthalten sein soll
	 * @see Credentials
	 */
	public void filterCredentials(String categoryPath, String pattern) {

	}

	/**
	 * Kopiert, das in den {@link Credentials} enthaltene Passwort unverschlüsselt in die Zwischenablage.
	 * Nach 10 Sekunden wird es automatisch wieder aus der Zwischenablage entfernt
	 * @param credentials Die {@link Credentials}, dessen Passwort kopiert werden soll
	 * @see Credentials
	 */
	public void copyPasswordToClipboard(Credentials credentials) {

	}

	/**
	 * Legt fest, ob das Passwort der {@link Credentials} sichtbar in der UI angezeigt werden soll
	 * @param credentials Die {@link Credentials}, dessen Passwort (nicht) angezeigt werden soll
	 * @param visible Falls 'true', soll das Passwort im Klartext angezeigt werden, sonst nur Sternchen
	 */
	public void setPasswordShown(Credentials credentials, boolean visible) {

	}

	/**
	 * Gibt eine {@link List} aller {@link Credentials} zurück, die in der durch den gegebenen Pfad beschriebenen {@link Category} liegen
	 * @param categoryPath Der Pfad der Kategorie, dessen Inhalt zurückgegeben werden soll
	 * @return Eine {@link List} aller {@link Credentials}, die in der durch {@code categoryPath} beschriebenen {@link Category} liegen
	 * @see Credentials
	 * @see Category
	 */
	List<Credentials> getCredentialsByCategoryName(String categoryPath) {
		return null;
	}

	/**
	 * Leert die Zwischenablage, falls die Zwischenablage das Passwort der gegebenen {@link Credentials} enthält
	 * @param credentials Die {@link Credentials}, dessen Passwort aus der Zwischenablage entfernt werden soll
	 */
	void clearPasswordFromClipboard(Credentials credentials) {

	}

	/**
	 * Entschlüsselt alle Daten im {@link PasswordManager} mit dem alten Masterpasswort und verschlüsselt sie mit dem neuen Masterpasswort
	 * Diese Methode wird beim Ändern des Masterpasswortes benutzt um die gespeicherten Daten mit dem neuen Masterpasswort entschlüsseln zu können.
	 * @param oldMasterPassword Das alte Masterpasswort, mit dem die Daten entschlüsselt werden sollen
	 * @param newMasterPassword Das neue Masterpasswort, mit dem die Daten verschlüsselt werden sollen
	 */
	void reencryptAll(String oldMasterPassword, String newMasterPassword) {

	}
}
