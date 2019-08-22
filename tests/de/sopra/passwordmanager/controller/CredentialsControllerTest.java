package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy.MainView;
import de.sopra.passwordmanager.model.BasePassword;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class CredentialsControllerTest {

    private PasswordManagerController pmc;
    private CredentialsController cc;
    private PasswordManager pm;
    // Liste im MainView eine Liste mit einem einzigen Eintrag und füge diesen auch ins Datenmodell ein
    private MainView mainView;

    @Before
    public void setUp() throws Exception {
        pmc = PasswordManagerControllerDummy.getNewController();
        cc = pmc.getCredentialsController();
        pm = pmc.getPasswordManager();
        mainView =  (MainView) this.pmc.getMainWindowAUI();
    }

    //region Save Credentials
    @Test
    public void saveCredentialsTestBasic() {
        Credentials cred = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();

        cc.saveCredentials(null, cred);
        Assert.assertTrue(pmc.getPasswordManager().getRootCategory().getCredentials().contains(cred));
    }

    @Test
    public void saveCredentialsTestReplace() {
        Credentials cred1 = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();

        Credentials cred2 = new CredentialsBuilder()
                .withName("cred2")
                .withUserName("user2")
                .withPassword("456passwort")
                .withWebsite("www.hi.com")
                .build();

        cc.saveCredentials(null, cred1);
        Assert.assertTrue(pm.getRootCategory().getCredentials().contains(cred1));
        cc.saveCredentials(cred1, cred2);

        //Nach dem ersetzen darf nur 1 Eintrag im Passwortmanager existieren und dieser muss mit cred2 übereinstimmen
        Assert.assertEquals(1, pm.getRootCategory().getCredentials().size());
        Assert.assertTrue(pm.getRootCategory().getCredentials().contains(cred2));
    }

    @Test
    public void saveCredentialsTestReplaceSameObject() {
        Credentials cred = new CredentialsBuilder()
                .withName("cred")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();
        cc.saveCredentials(null, cred);
        Assert.assertTrue(pm.getRootCategory().getCredentials().contains(cred));
        cc.saveCredentials(cred, cred);

        //Nach dem ersetzen darf nur 1 Eintrag im Passwortmanager existieren und dieser muss mit cred übereinstimmen
        Assert.assertEquals(1, pm.getRootCategory().getCredentials().size());
        Assert.assertTrue(pm.getRootCategory().getCredentials().contains(cred));
    }

    @Test
    public void saveCredentialsTestNull() {
        Credentials cred = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();

        cc.saveCredentials(cred, null);
        Assert.assertEquals("Entries changed while trying to replace Credentials with null Credentials",0, pm.getRootCategory().getCredentials().size());
        cc.saveCredentials(null, null);
        Assert.assertEquals("Entries changed while trying to insert null Credentials", 0, pm.getRootCategory().getCredentials().size());
    }

    @Test
    public void saveCredentialsTestOldCredentialsNotExist() {
        Credentials cred1 = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();

        Credentials cred2 = new CredentialsBuilder()
                .withName("cred2")
                .withUserName("user2")
                .withPassword("456passwort")
                .withWebsite("www.bye.com")
                .build();

        //Vorher, wie nachher darf kein Eintrag im Passwortmanager existieren, da cred1 nicht existiert
        Assert.assertEquals("Entries not empty", 0, pm.getRootCategory().getCredentials().size());
        cc.saveCredentials(cred1, cred2);
        Assert.assertEquals("Entries changed after trying to replace Credentials, that do not exist", 0, pm.getRootCategory().getCredentials().size());
    }
    //endregion

    //region  Remove Credentials
    @Test
    public void removeCredentialsTestView() {
        // Werden Credentials aus dem MainView auch gelöscht?
        Credentials credentials = new CredentialsBuilder()
                .withName("Hello")
                .withUserName("Hello")
                .withWebsite("schmuddelseite_sieben.de")
                .withPassword("wanken")
                .build();

        List<Credentials> credentialsList = new ArrayList<>();
        credentialsList.add(credentials);
        pm.getRootCategory().addCredentials(credentials);
        mainView.refreshEntryList(credentialsList);

        Assert.assertEquals("The Credentialslist does not contain exactly 1 entry after adding 1 Credentials object", 1, mainView.getCurrentCredentialsList().size());
        Assert.assertTrue("CredentialsList does not contain the given Credentials object after adding", mainView.getCurrentCredentialsList().contains(credentials));

        // Der Eintrag soll gelöscht werden und muss in der View aktualisiert worden sein
        cc.removeCredentials(credentials);

        Assert.assertTrue("Credentials object not removed from CredentialsList", mainView.getCurrentCredentialsList().isEmpty());
    }

    @Test
    public void removeCredentialsTestNull() {
        // Werden Credentials aus dem MainView auch gelöscht?
        Credentials credentials = new CredentialsBuilder()
                .withName("Hello")
                .withUserName("Hello")
                .withWebsite("schmuddelseite_neun.de")
                .withPassword("wnken")
                .build();

        List<Credentials> credentialsList = new ArrayList<>();
        credentialsList.add(credentials);
        pm.getRootCategory().addCredentials(credentials);
        mainView.refreshEntryList(credentialsList);

        Assert.assertEquals("The Credentialslist does not contain exactly 1 entry after adding 1 Credentials object", 1, mainView.getCurrentCredentialsList().size());
        Assert.assertTrue("CredentialsList does not contain the given Credentials object after adding", mainView.getCurrentCredentialsList().contains(credentials));

        // Der Eintrag soll gelöscht werden und muss in der View aktualisiert worden sein
        cc.removeCredentials(null);

        Assert.assertEquals("The Credentialslist does not contain exactly 1 entry after removing null", 1, mainView.getCurrentCredentialsList().size());
        Assert.assertTrue("CredentialsList does not contain the given Credentials object after removing null", mainView.getCurrentCredentialsList().contains(credentials));
    }

    @Test
    public void removeCredentialsTestNonExistant() {
        // Werden Credentials aus dem MainView auch gelöscht?
        Credentials credentials1 = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .build();

        Credentials credentials2 = new CredentialsBuilder()
                .withName("General Kenobi")
                .withUserName("Tschüss")
                .withWebsite("Junge")
                .withPassword("Warum")
                .build();

        List<Credentials> credentialsList = new ArrayList<>();
        credentialsList.add(credentials1);
        pm.getRootCategory().addCredentials(credentials1);
        mainView.refreshEntryList(credentialsList);

        Assert.assertEquals("The Credentialslist does not contain exactly 1 entry after adding 1 Credentials object", 1, mainView.getCurrentCredentialsList().size());
        Assert.assertTrue("CredentialsList does not contain the given Credentials object after adding", mainView.getCurrentCredentialsList().contains(credentials1));

        // Der Eintrag soll gelöscht werden und muss in der View aktualisiert worden sein
        cc.removeCredentials(credentials2);

        Assert.assertEquals("The CredentialsList does not contain exactly 1 entry after removing non existant Credentials object", 1, mainView.getCurrentCredentialsList().size());
        Assert.assertTrue("CredentialsList does not contain the given Credentials object after removing null", mainView.getCurrentCredentialsList().contains(credentials1));
    }
    //endregion

    //region Add Security Questions
    @Test
    public void addSecurityQuestionTest() {
        Credentials credentials = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .build();

        cc.saveCredentials(null, credentials);

        SecurityQuestion sq = new SecurityQuestion("Warum?", "Da so");
        cc.addSecurityQuestion(sq, credentials);
        cc.addSecurityQuestion("Was?", "Das", credentials);

        Assert.assertTrue("Adding SecurityQuestion object failed", credentials.getSecurityQuestions().contains(sq));
        Assert.assertTrue("Adding SecurityQuestion with question and answer failed", credentials.getSecurityQuestions().contains(new SecurityQuestion("Was?", "Das")));
    }

    @Test(expected = NullPointerException.class)
    public void addSecurityQuestionTestSecurityQuestionNull() {
        Credentials credentials = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .build();

        cc.saveCredentials(null, credentials);
        cc.addSecurityQuestion(null, credentials);
    }

    @Test(expected = NullPointerException.class)
    public void addSecurityQuestionTestQuestionNull() {
        Credentials credentials = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .build();

        cc.saveCredentials(null, credentials);
        cc.addSecurityQuestion(null, "Das", credentials);
    }

    @Test(expected = NullPointerException.class)
    public void addSecurityQuestionTestAnswerNull() {
        Credentials credentials = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .build();

        cc.saveCredentials(null, credentials);
        cc.addSecurityQuestion("Was", null, credentials);
    }

    @Test(expected = NullPointerException.class)
    public void addSecurityQuestionTestCredentialsNull() {
        cc.addSecurityQuestion("Was", "Das", null);
    }
    //endregion

    //region Remove Security Questions
    @Test
    public void removeSecurityQuestionTest() {
        SecurityQuestion sq = new SecurityQuestion("Was", "Das");

        Credentials credentials = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .withSecurityQuestion(sq)
                .build();

        cc.saveCredentials(null, credentials);
        Assert.assertTrue("Credential does not contain Security Question", credentials.getSecurityQuestions().contains(sq));
        cc.removeSecurityQuestion(sq, credentials);
        Assert.assertTrue("Removing SecurityQuestion failed", credentials.getSecurityQuestions().isEmpty());
    }

    @Test
    public void removeSecurityQuestionTestNotExist() {
        SecurityQuestion sq1 = new SecurityQuestion("Was", "Das");
        SecurityQuestion sq2 = new SecurityQuestion("Warum", "Darum");

        Credentials credentials = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .withSecurityQuestion(sq1)
                .build();

        cc.saveCredentials(null, credentials);
        Assert.assertTrue("Credential does not contain Security Question", credentials.getSecurityQuestions().contains(sq1));
        cc.removeSecurityQuestion(sq2, credentials);
        Assert.assertEquals("A credential was added to/removed from the list", 1, credentials.getSecurityQuestions().size());
        Assert.assertTrue("The wrong Security Question was removed", credentials.getSecurityQuestions().contains(sq1));
    }

    @Test
    public void removeSecurityQuestionTestNull() {
        SecurityQuestion sq1 = new SecurityQuestion("Was", "Das");

        Credentials credentials = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .withSecurityQuestion(sq1)
                .build();

        cc.saveCredentials(null, credentials);
        Assert.assertTrue("Credential does not contain Security Question", credentials.getSecurityQuestions().contains(sq1));
        cc.removeSecurityQuestion(null, credentials);
        Assert.assertEquals("A credential was added to/removed from the list", 1, credentials.getSecurityQuestions().size());
        Assert.assertTrue("A Security Question was removed", credentials.getSecurityQuestions().contains(sq1));
    }
    //endregion

    // Filter tests moved to CredentialControllerTreeTest

    //region Copy Password To Clipboard
    @Test
    public void copyPasswordToClipboardTest() {
        String password = "pass1";
        Credentials cred = new CredentialsBuilder("cred1", "user1", password, "site1").build();
        cc.copyPasswordToClipboard(cred);

        Assert.assertEquals("clipboard contents do not equal the password", password, getClipboardContents());
    }
    //endregion

    //region Set Password Shown
    @Test
    public void setPasswordShownTest() {
        String password = "pass1";
        Credentials cred = new CredentialsBuilder("cred1", "user1", password, "site1").build();

        cc.setPasswordShown(cred, true);
        Assert.assertEquals("password is not shown", password, mainView.getPasswordShown());

        cc.setPasswordShown(cred, false);
        Assert.assertNull("password is shown", mainView.getPasswordShown());
    }
    //endregion

    //Tests for getCredentialsByCategoryPath() in CredentialsControllerTreeTest

    @Test
    public void clearPasswordFromClipboardTest() {
        // Das Passwort muss aus der Zwischenablage gelöscht werden, wenn es in ihr enthalten ist
        String rawPassword = "Passwort123";
        String encPassword = this.pmc.getUtilityController().encryptText(rawPassword);
        Credentials credentials = new CredentialsBuilder("Super Secret", "bonehead27", encPassword, "lol5.org").build();
        setClipboardContents(encPassword);

        this.cc.clearPasswordFromClipboard(credentials);
        Assert.assertNotEquals("Password was not removed from clipboard", rawPassword, getClipboardContents());

        // Die Zwischenablage darf nicht verändert werden, wenn sich das Passwort nicht in ihr befindet
        rawPassword = "MeinPasswort";
        encPassword = this.pmc.getUtilityController().encryptText(rawPassword);
        credentials = new CredentialsBuilder("Super Secret", "bonehead27", encPassword, "lol5.org").build();
        setClipboardContents("NichtMeinPasswort");

        this.cc.clearPasswordFromClipboard(credentials);
        Assert.assertEquals("Clipboard was changed, eventhough it did not contain the password", "NichtMeinPasswort", getClipboardContents());
    }

    @Test
    public void reencryptAll() {

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