package de.sopra.passwordmanager.controller;

import aes.AES;
import de.sopra.passwordmanager.model.*;
import de.sopra.passwordmanager.util.*;
import de.sopra.passwordmanager.util.strategy.EntryListSelectionStrategy;
import de.sopra.passwordmanager.view.MainWindowAUI;
import exceptions.DecryptionException;
import exceptions.EncryptionException;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

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
     * @param oldCredentials Die zu überschreibenden Anmeldedaten. Darf nicht <code>null</code> sein
     *                       Falls diese nicht im {@link PasswordManager} existieren, geschieht nichts.
     * @param newCredentials Die neuen Anmeldedaten, die die Alten überschreiben. Falls <code>null</code>, geschieht nichts
     * @param categories     Die Kategorien in denen die {@link Credentials} liegen sollen
     * @throws NullPointerException falls {@code oldCredentials} oder {@code categories} null sind
     * @see Credentials
     */
    public void updateCredentials(Credentials oldCredentials, CredentialsBuilder newCredentials, Collection<Category> categories) throws NullPointerException {
        if (newCredentials == null) return;
        passwordManagerController.getPasswordManager().getRootCategory().removeCredentialsFromTree(oldCredentials);
        newCredentials.copyTo(oldCredentials, passwordManagerController.getUtilityController());
        for (Category category : categories) {
            category.addCredentials(oldCredentials);
        }
        passwordManagerController.getMainWindowAUI().refreshLists();
    }

    /**
     * Speichert neue {@link Credentials} im {@link PasswordManager}
     *
     * @param newCredentials Die neuen Anmeldedaten. Falls <code>null</code>, geschieht nichts
     * @param categories     Die Kategorien in denen die {@link Credentials} liegen sollen
     * @see Credentials
     * @see CredentialsBuilder
     */
    public void addCredentials(CredentialsBuilder newCredentials, Collection<Category> categories) {
        Credentials credentials = newCredentials.build(passwordManagerController.getUtilityController());
        for (Category category : categories) {
            category.addCredentials(credentials);
		}
        passwordManagerController.getMainWindowAUI().refreshLists();
        passwordManagerController.getIOController().exportFile(PasswordManagerController.SAVE_FILE);
    }

    /**
     * Entfernt die gegebenen Anmeldedaten aus dem {@link PasswordManager}
     *
     * @param credentials Die zu entfernenden Anmeldedaten. Falls <code>null</code>, geschieht nichts
     * @see Credentials
     */
    public void removeCredentials(Credentials credentials) {
        passwordManagerController.getPasswordManager().getRootCategory().removeCredentialsFromTree(credentials);
        passwordManagerController.getMainWindowAUI().refreshLists();
        passwordManagerController.getIOController().exportFile(PasswordManagerController.SAVE_FILE);
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
     * @param question    Die Frage der {@link SecurityQuestion}, die von den gegebenen {@link Credentials} entfernt werden sollen.
     * @param answer      Die Antwort der {@link SecurityQuestion}, die von den gegebenen {@link Credentials} entfernt werden sollen.
     * @param credentials Die {@link CredentialsBuilder}, von der die {@link SecurityQuestion} entfernt werden soll. Darf nicht <code>null</code> sein
     * @throws NullPointerException Falls {@code #credentials} <code>null</code> ist
     * @see SecurityQuestion
     * @see CredentialsBuilder
     */
    public void removeSecurityQuestion(String question, String answer, CredentialsBuilder credentials) throws NullPointerException {
        credentials.withoutSecurityQuestion(question, answer);
    }


    public void removeSecurityQuestion(SecurityQuestion question, CredentialsBuilder credentials) throws NullPointerException {
        credentials.withoutSecurityQuestion(
                passwordManagerController.getUtilityController().decryptText(question.getQuestion()),
                passwordManagerController.getUtilityController().decryptText(question.getAnswer())
        );
    }

    /**
     * Filtert die Liste der {@link Credentials} im {@link de.sopra.passwordmanager.view.MainWindowViewController} nach
     * Kategorie und nach Inhalt seines Namens. Aktualisirt mit {@link MainWindowAUI#refreshLists()}
     *
     * @param pattern Ein PatternSyntax, der in den {@link Credentials} enthalten sein soll.
     * @see CredentialsBuilder
     */
    public void filterCredentials(PatternSyntax pattern) {
        if (pattern.getPatternFilter() == PatternSyntax.PatternSyntaxFilter.COMMAND) {
            //TODO: remove when program is finish, this is just the dev tool
            DevTool.fillWithData(passwordManagerController);
            passwordManagerController.getMainWindowAUI().refreshLists();
            return;
        }
        EntryListSelectionStrategy strategy = credentials -> {
            LinkedList<CredentialsItem> selection = new LinkedList<>();
            for (Credentials creds : credentials) {
                if (pattern.include(creds)) {
                    CredentialsItem item = new CredentialsItem(creds);
                    item.setNamingStrategy(pattern.getPatternFilter());
                    selection.add(item);
                }
            }
            return selection;
        };
        passwordManagerController.getMainWindowAUI().refreshListStrategies(strategy, null);
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
     * @deprecated in die MainView umlagern
     */
    @Deprecated
    public void setPasswordShown(CredentialsBuilder credentials, boolean visible) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Gibt eine {@link Collection} aller {@link Credentials} zurück, die in der durch den gegebenen Pfad beschriebenen {@link Category} liegen
     *
     * @param categoryPath Der Pfad der Kategorie, dessen Inhalt zurückgegeben werden soll. Darf nicht <code>null</code> sein
     * @return Eine {@link Collection} aller {@link Credentials}, die in der durch {@code categoryPath} beschriebenen {@link Category} liegen
     * @throws NullPointerException Falls {@code categoryPath} <code>null</code> ist
     * @see Credentials
     * @see Category
     */
    Collection<Credentials> getCredentialsByCategoryPath(Path categoryPath) {
        Category category = passwordManagerController.getPasswordManager().getRootCategory().getCategoryByPath(categoryPath);
        if (category == null) {
            return Collections.emptySet();
        }
        return category.getAllCredentials();
    }

    /**
     * Leert die Zwischenablage, falls die Zwischenablage das Passwort des gegebenen {@link CredentialsBuilder} enthält
     *
     * @param credentials Der {@link CredentialsBuilder}, dessen Passwort aus der Zwischenablage entfernt werden soll. Falls <code>null</code> geschieht nichts
     */
    public void clearPasswordFromClipboard(CredentialsBuilder credentials) {
        if (credentials.getPassword().equals(getClipboardContents())) {
            setClipboardContents(null);
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
        Collection<Credentials> credentials = passwordManagerController.getPasswordManager().getRootCategory().getAllCredentials();

        credentials.forEach(cred -> {
            cred.setPassword(reencryptText(cred.getPassword(), oldMasterPassword, newMasterPassword));
            Set<SecurityQuestion> newQuestions = cred.getSecurityQuestions().stream().map(securityQuestion -> new SecurityQuestion(
                    reencryptText(securityQuestion.getQuestion(), oldMasterPassword, newMasterPassword),
                    reencryptText(securityQuestion.getAnswer(), oldMasterPassword, newMasterPassword)
            )).collect(Collectors.toSet());
            cred.clearSecurityQuesions();
            cred.addSecurityQuestions(newQuestions);
        });

        passwordManagerController.getIOController().exportFile(PasswordManagerController.SAVE_FILE);
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

    private static EncryptedString reencryptText(EncryptedString text, String oldKey, String newKey) {
        try {
            return new EncryptedString(AES.encrypt(AES.decrypt(text.getEncryptedContent(), oldKey), newKey));
        } catch (EncryptionException | DecryptionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
