package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.util.Validate;
import de.sopra.passwordmanager.view.MainWindowAUI;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Etienne
 * Kontrolliert alle Aktionen, die mit {@link Credentials} zusammenhängen
 * @see Credentials
 */
public class CredentialsController {

    private PasswordManagerController passwordManagerController;

    public CredentialsController(PasswordManagerController controller) {
        this.passwordManagerController = controller;
    }

    /**
     * Überschreibt alte Anmeldedaten mit Neuen im {@link PasswordManager}
     * Falls {@code oldCredentials} <code>null</code> ist, wird stattdessen ein neuer Eintrag mit den Daten von {@code newCredentials} erstellt
     *
     * @param oldCredentials Die zu überschreibenden Anmeldedaten. Falls <code>null</code>, wird ein neuer Eintrag erstellt
     *                       Falls diese nicht im {@link PasswordManager} existieren, geschieht nichts.
     * @param newCredentials Die neuen Anmeldedaten, die die Alten überschreiben. Falls <code>null</code>, geschieht nichts
     * @see Credentials
     */
    public void saveCredentials(Credentials oldCredentials, CredentialsBuilder newCredentials) {

    }

    /**
     * Entfernt die gegebenen Anmeldedaten aus dem {@link PasswordManager}
     *
     * @param credentials Die zu entfernenden Anmeldedaten. Falls <code>null</code>, geschieht nichts
     * @see Credentials
     */
    public void removeCredentials(Credentials credentials) {

    }

    /**
     * Fügt dem momentan im {@link de.sopra.passwordmanager.view.MainWindowViewController} angezeigten Entry eine
     * Sicherheitsfrage hinzu
     *
     * @param question    Die Frage der {@link SecurityQuestion}. Darf nicht <code>null</code> sein
     * @param answer      Die Antwort auf die Frage. Darf nicht <code>null</code> sein
     * @param credentials Das Anmeldedatenobjekt, dem die {@link SecurityQuestion} hinzugefügt werden soll. Darf nicht <code>null</code> sein
     * @throws NullPointerException falls {@code question}, {@code answer} oder {@code credentials} <code>null</code> sind
     * @see SecurityQuestion
     * @see Credentials
     */
    public void addSecurityQuestion(String question, String answer, CredentialsBuilder credentials) throws NullPointerException {
        Validate.notNull(question, "question is null");
        Validate.notNull(answer, "answer is null");
        Validate.notNull(credentials, "credentials object is null");
        credentials.withSecurityQuestion(question, answer);
    }

    /**
     * Entfernt das gegebene Paar von Frage und Antwort von dem gegebenen {@link CredentialsBuilder}
     * Ist Frage oder Antwort null, so geschieht nichts
     *
     * @param question Die Frage der {@link SecurityQuestion}, die von den gegebenen {@link Credentials} entfernt werden sollen.
     * @param answer Die Antwort der {@link SecurityQuestion}, die von den gegebenen {@link Credentials} entfernt werden sollen.
     * @param credentials      Die {@link CredentialsBuilder}, von der die {@link SecurityQuestion} entfernt werden soll. Darf nicht <code>null</code> sein
     * @throws NullPointerException Falls {@code #credentials} <code>null</code> ist
     * @see SecurityQuestion
     * @see CredentialsBuilder
     */
    public void removeSecurityQuestion(String question, String answer, CredentialsBuilder credentials) throws NullPointerException{
        credentials.withoutSecurityQuestion(question, answer);
    }

    /**
     * Filtert die Liste der {@link Credentials} im {@link de.sopra.passwordmanager.view.MainWindowViewController} nach
     * Kategorie und nach Inhalt seines Namens. Aktualisirt mit {@link MainWindowAUI#refreshEntryList(List)}
     *
     * @param categoryPath Der Pfad der {@link Category} in der alle gewünschten {@link Credentials} liegen sollen. Falls <code>null</code>, wird nicht nach Kategorie gefiltert
     * @param pattern      Ein String, der im Namen der {@link Credentials} enthalten sein soll. Falls <code>null</code>, wird nicht nach Eintragsnamen gesucht
     * @see CredentialsBuilder
     */
    public void filterCredentials(Path categoryPath, String pattern) {
        Collection<Credentials> credentials = passwordManagerController.getPasswordManager().getRootCategory().getAllCredentials();
        Stream<Credentials> credentialsStream = credentials.stream();
        if(categoryPath != null) {
            Category category = passwordManagerController.getPasswordManager().getRootCategory().getCategoryByPath(categoryPath);
            if(category == null) {
                passwordManagerController.getMainWindowAUI().refreshEntryList(new ArrayList<>());
                return;
            }
            credentialsStream = credentialsStream.filter(cred -> category.getAllCredentials().contains(cred));
        }
        if(pattern != null) {
            credentialsStream = credentialsStream.filter(cred -> cred.getName().contains(pattern));
        }
        passwordManagerController.getMainWindowAUI().refreshEntryList(credentialsStream.collect(Collectors.toList()));
    }

    /**
     * Kopiert das in dem {@link CredentialsBuilder} enthaltene Passwort unverschlüsselt in die Zwischenablage.
     *
     * @param credentials Der {@link CredentialsBuilder}, dessen Passwort kopiert werden soll. Falls <code>null</code>,
     *                    geschieht nichts
     * @see CredentialsBuilder
     */
    public void copyPasswordToClipboard(CredentialsBuilder credentials) {
        setClipboardContents(credentials.getPassword());
    }

    /**
     * Legt fest, ob das Passwort im {@link CredentialsBuilder} sichtbar in der UI angezeigt werden soll
     *
     * @param credentials Der {@link CredentialsBuilder}, dessen Passwort (nicht) angezeigt werden soll. Falls
     *                    <code>null</code> geschieht nichts
     * @param visible     Falls 'true', soll das Passwort im Klartext angezeigt werden, sonst nur Sternchen
     */
    public void setPasswordShown(CredentialsBuilder credentials, boolean visible) {
        if (visible) {
            passwordManagerController.getMainWindowAUI().refreshEntry(credentials);
        } else {
            passwordManagerController.getMainWindowAUI().refreshEntry();
        }
    }

    /**
     * Gibt eine {@link List} aller {@link Credentials} zurück, die in der durch den gegebenen Pfad beschriebenen {@link Category} liegen
     *
     * @param categoryPath Der Pfad der Kategorie, dessen Inhalt zurückgegeben werden soll. Darf nicht <code>null</code> sein
     * @return Eine {@link List} aller {@link Credentials}, die in der durch {@code categoryPath} beschriebenen {@link Category} liegen
     * @throws NullPointerException Falls {@code categoryPath} <code>null</code> ist
     * @see Credentials
     * @see Category
     */
    Collection<Credentials> getCredentialsByCategoryPath(Path categoryPath) {
        return null;
    }

    /**
     * Leert die Zwischenablage, falls die Zwischenablage das Passwort des gegebenen {@link CredentialsBuilder} enthält
     *
     * @param credentials Der {@link CredentialsBuilder}, dessen Passwort aus der Zwischenablage entfernt werden soll. Falls <code>null</code> geschieht nichts
     */
    void clearPasswordFromClipboard(CredentialsBuilder credentials) {
        if(credentials.getPassword().equals(getClipboardContents())) {
            setClipboardContents("*****");
        }
    }

    /**
     * Entschlüsselt alle Daten im {@link PasswordManager} mit dem alten Masterpasswort und verschlüsselt sie mit dem neuen Masterpasswort
     * Diese Methode wird beim Ändern des Masterpasswortes benutzt um die gespeicherten Daten mit dem neuen Masterpasswort entschlüsseln zu können.
     *
     * @param oldMasterPassword Das alte Masterpasswort, mit dem die Daten entschlüsselt werden sollen. Darf nicht <code>null</code> sein
     * @param newMasterPassword Das neue Masterpasswort, mit dem die Daten verschlüsselt werden sollen. Darf nicht <code>null</code> sein
     */
    void reencryptAll(String oldMasterPassword, String newMasterPassword) {

    }

    private static void setClipboardContents(String contents) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(contents), null);
    }

    private static String getClipboardContents() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
