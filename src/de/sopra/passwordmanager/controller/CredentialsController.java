package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.view.MainWindowAUI;

import java.util.List;

/**
 * @author Etienne
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
	 * Falls {@code oldCredentials} <code>null</code> ist, wird stattdessen ein neuer Eintrag mit den Daten von {@code newCredentials} erstellt
	 * @param oldCredentials Die zu überschreibenden Anmeldedaten. Falls <code>null</code>, wird ein neuer Eintrag erstellt
	 *                       Falls diese nicht im {@link PasswordManager} existieren, geschieht nichts.
	 * @param newCredentials Die neuen Anmeldedaten, die die Alten überschreiben. Falls <code>null</code>, geschieht nichts
	 * @see Credentials
	 */
	public void saveCredentials(Credentials oldCredentials, Credentials newCredentials) {

	}

	/**
	 * Entfernt die gegebenen Anmeldedaten aus dem {@link PasswordManager}
	 * @param credentials Die zu entfernenden Anmeldedaten. Falls <code>null</code>, geschieht nichts
	 * @see Credentials
	 */
	public void removeCredentials(Credentials credentials) {

	}

	/**
	 * Fügt dem {@link Credentials} Objekt im {@link PasswordManager} eine {@link SecurityQuestion} hinzu
	 * @param question Die Frage der {@link SecurityQuestion}. Darf nicht <code>null</code> sein
	 * @param answer Die Antwort auf die Frage. Darf nicht <code>null</code> sein
	 * @param credentials Das Anmeldedatenobjekt, dem die {@link SecurityQuestion} hinzugefügt werden soll. Darf nicht <code>null</code> sein
	 * @throws NullPointerException falls {@code question}, {@code answer} oder {@code credentials} <code>null</code> sind
	 * @see SecurityQuestion
	 * @see Credentials
	 */
	public void addSecurityQuestion(String question, String answer, Credentials credentials) throws NullPointerException {

	}

	/**
	 * Fügt dem {@link Credentials} Objekt im {@link PasswordManager} eine {@link SecurityQuestion} hinzu
	 * @param securityQuestion Die {@link SecurityQuestion}. Darf nicht <code>null</code> sein
	 * @param credentials Das Anmeldedatenobjekt, dem die {@link SecurityQuestion} hinzugefügt werden soll. Darf nicht <code>null</code> sein
	 * @throws NullPointerException falls {@code securityQuestion} oder {@code credentials} <code>null</code> sind
	 * @see SecurityQuestion
	 * @see Credentials
	 */
	public void addSecurityQuestion(SecurityQuestion securityQuestion, Credentials credentials) throws NullPointerException {

	}

	/**
	 * Entfernt die gegebene {@link SecurityQuestion} von den gegebenen {@link Credentials}
	 * @param securityQuestion Die {@link SecurityQuestion}, die von den gegebenen {@link Credentials} entfernt werden sollen. Falls <code>null</code>, geschieht nichts
	 * @param credentials Die {@link Credentials}, von der die {@link SecurityQuestion} entfernt werden soll. Darf nicht <code>null</code> sein
	 * @see SecurityQuestion
	 * @see Credentials
	 */
	public void removeSecurityQuestion( SecurityQuestion securityQuestion, Credentials credentials) {

	}

	/**
	 * Filtert alle {@link Credentials} im {@link PasswordManager} nach Kategorie und nach Inhalt seines Namen und refresht
	 * die Liste der Einträge durch {@link MainWindowAUI#refreshEntryList(List)}
	 * @param categoryPath Der Pfad der {@link Category} in der alle gewünschten {@link Credentials} liegen sollen. Falls <code>null</code>, wird nicht nach Kategorie gefiltert
	 * @param pattern Ein String, der im Namen der {@link Credentials} enthalten sein soll. Falls <code>null</code>, wird nicht nach Eintragsnamen gesucht
	 * @see Credentials
	 */
	public void filterCredentials(String categoryPath, String pattern) {

	}

	/**
	 * Kopiert das in den {@link Credentials} enthaltene Passwort unverschlüsselt in die Zwischenablage.
	 * @param credentials Die {@link Credentials}, dessen Passwort kopiert werden soll. Falls <code>null</code>, geschieht nichts
	 * @see Credentials
	 */
	public void copyPasswordToClipboard(Credentials credentials) {

	}

	/**
	 * Legt fest, ob das Passwort der {@link Credentials} sichtbar in der UI angezeigt werden soll
	 * @param credentials Die {@link Credentials}, dessen Passwort (nicht) angezeigt werden soll. Falls <code>null</code> geschieht nichts
	 * @param visible Falls 'true', soll das Passwort im Klartext angezeigt werden, sonst nur Sternchen
	 */
	public void setPasswordShown(Credentials credentials, boolean visible) {

	}

	/**
	 * Gibt eine {@link List} aller {@link Credentials} zurück, die in der durch den gegebenen Pfad beschriebenen {@link Category} liegen
	 * @param categoryPath Der Pfad der Kategorie, dessen Inhalt zurückgegeben werden soll. Darf nicht <code>null</code> sein
	 * @return Eine {@link List} aller {@link Credentials}, die in der durch {@code categoryPath} beschriebenen {@link Category} liegen
	 * @see Credentials
	 * @see Category
	 */
	List<Credentials> getCredentialsByCategoryPath(String categoryPath) {
		return null;
	}

	/**
	 * Leert die Zwischenablage, falls die Zwischenablage das Passwort der gegebenen {@link Credentials} enthält
	 * @param credentials Die {@link Credentials}, dessen Passwort aus der Zwischenablage entfernt werden soll. Falls <code>null</code> geschieht nichts
	 */
	void clearPasswordFromClipboard(Credentials credentials) {

	}

	/**
	 * Entschlüsselt alle Daten im {@link PasswordManager} mit dem alten Masterpasswort und verschlüsselt sie mit dem neuen Masterpasswort
	 * Diese Methode wird beim Ändern des Masterpasswortes benutzt um die gespeicherten Daten mit dem neuen Masterpasswort entschlüsseln zu können.
	 * @param oldMasterPassword Das alte Masterpasswort, mit dem die Daten entschlüsselt werden sollen. Darf nicht <code>null</code> sein
	 * @param newMasterPassword Das neue Masterpasswort, mit dem die Daten verschlüsselt werden sollen. Darf nicht <code>null</code> sein
	 */
	void reencryptAll(String oldMasterPassword, String newMasterPassword) {

	}
}
