package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy.MainView;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Path;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private UtilityController uc;

    @Before
    public void setUp() throws Exception {
        pmc = PasswordManagerControllerDummy.getNewController();
        cc = pmc.getCredentialsController();
        pm = pmc.getPasswordManager();
        mainView =  (MainView) this.pmc.getMainWindowAUI();
        uc = pmc.getUtilityController();

        Category sub = new Category("sub");
        pm.getRootCategory().addSubCategory(sub);
        Category bus = new Category("bus");
        pm.getRootCategory().addSubCategory(bus);
    }

    //region Add Credentials
    @Test
    public void addCredentialsTest() {
        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de");

        Set<Category> categories = new HashSet<>();
        Category sub = pm.getRootCategory().getCategoryByPath(Path.ROOT_CATEGORY_PATH.createChildPath("sub"));
        categories.add(sub);

        cc.addCredentials(credBuilder, categories);
        Assert.assertTrue(sub.getCredentials().contains(credBuilder.build(uc)));
    }

    @Test
    public void addCredentialsTestRefreshes() {
        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de");

        pmc.getMainWindowAUI().refreshLists();

        Set<Category> categories = new HashSet<>();
        Category sub = pm.getRootCategory().getCategoryByPath(Path.ROOT_CATEGORY_PATH.createChildPath("sub"));
        categories.add(sub);

        cc.addCredentials(credBuilder, categories);
        Assert.assertTrue("adding Credentials failed", sub.getCredentials().contains(credBuilder.build(uc)));
        Assert.assertTrue("entry list didn't refresh", mainView.getCurrentCredentialsList().contains(credBuilder.build(uc)));
    }

    @Test
    public void updateCredentialsTest() {
        CredentialsBuilder credBuilder1 = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de");

        CredentialsBuilder credBuilder2 = new CredentialsBuilder()
                .withName("cred2")
                .withUserName("user2")
                .withPassword("456passwort")
                .withWebsite("www.hi.com");

        Set<Category> categories = new HashSet<>();
        Category sub = pm.getRootCategory().getCategoryByPath(Path.ROOT_CATEGORY_PATH.createChildPath("sub"));
        categories.add(sub);
        cc.addCredentials(credBuilder1, categories);

        Set<Category> categories2 = new HashSet<>();
        Category bus = pm.getRootCategory().getCategoryByPath(Path.ROOT_CATEGORY_PATH.createChildPath("bus"));
        categories2.add(bus);

        Assert.assertTrue("Credentials not added to category", sub.getCredentials().contains(credBuilder1.build(uc)));
        Credentials obar = sub.getCredentials().stream().findFirst().get();
        cc.updateCredentials(obar, credBuilder2, categories2);
        Assert.assertFalse("Credentials still in old category", sub.getCredentials().contains(obar));
        Assert.assertTrue("Credentials not in new category", bus.getCredentials().contains(obar));
        //Nach dem ersetzen darf nur 1 Eintrag im Passwortmanager existieren und dieser muss mit cred2 übereinstimmen
        Assert.assertEquals("Not exactly 1 Credentials saved after replacing",1, pm.getRootCategory().getAllCredentials().size());
        Assert.assertTrue("Credentials not replaced", pm.getRootCategory().getAllCredentials().contains(obar));
    }

    public void updateCredentialsTestNewNull() {
        CredentialsBuilder credBuilder1 = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de");

        Set<Category> categories = new HashSet<>();
        Category sub = pm.getRootCategory().getCategoryByPath(Path.ROOT_CATEGORY_PATH.createChildPath("sub"));
        categories.add(sub);
        cc.addCredentials(credBuilder1, categories);

        Set<Category> categories2 = new HashSet<>();
        Category bus = pm.getRootCategory().getCategoryByPath(Path.ROOT_CATEGORY_PATH.createChildPath("bus"));
        categories2.add(bus);

        Assert.assertTrue("Credentials not added to category", sub.getCredentials().contains(credBuilder1.build(uc)));
        Credentials obar = sub.getCredentials().stream().findFirst().get();
        cc.updateCredentials(obar, null, categories2);
        Assert.assertTrue("Credentials moved category", sub.getCredentials().contains(obar));
        Assert.assertFalse("Credentials not other category", bus.getCredentials().contains(obar));
        //Nach dem ersetzen darf nur 1 Eintrag im Passwortmanager existieren und dieser muss mit cred2 übereinstimmen
        Assert.assertEquals("Not exactly 1 Credentials saved after replacing",1, pm.getRootCategory().getAllCredentials().size());
        Assert.assertTrue("Credentials not replaced", pm.getRootCategory().getAllCredentials().contains(obar));
    }

    @Test(expected = NullPointerException.class)
    public void updateCredentialsTestOldNull() {
        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de");

        Set<Category> categories = new HashSet<>();
        cc.updateCredentials(null, credBuilder, categories);
    }

    @Test
    public void updateCredentialsTestOldCredentialsNotExist() {
        Credentials cred1 = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build(uc);

        CredentialsBuilder credBuilder2 = new CredentialsBuilder()
                .withName("cred2")
                .withUserName("user2")
                .withPassword("456passwort")
                .withWebsite("www.bye.com");


        Set<Category> categories = new HashSet<>();
        //Vorher, wie nachher darf kein Eintrag im Passwortmanager existieren, da cred1 nicht existiert
        Assert.assertEquals("Entries not empty", 0, pm.getRootCategory().getCredentials().size());
        cc.updateCredentials(cred1, credBuilder2, categories);
        Assert.assertEquals("Entries changed after trying to replace Credentials, that do not exist", 0, pm.getRootCategory().getCredentials().size());
    }
    //endregion

    //region  Remove Credentials
    @Test
    public void removeCredentialsTestRefreshes() {
        // Werden Credentials aus dem MainView auch gelöscht?
        Credentials credentials = new CredentialsBuilder()
                .withName("Hello")
                .withUserName("Hello")
                .withWebsite("schmuddelseite_sieben.de")
                .withPassword("wanken")
                .build(uc);

        List<Credentials> credentialsList = new ArrayList<>();
        credentialsList.add(credentials);
        pm.getRootCategory().addCredentials(credentials);
        mainView.refreshLists();

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
                .build(uc);

        List<Credentials> credentialsList = new ArrayList<>();
        credentialsList.add(credentials);
        pm.getRootCategory().addCredentials(credentials);
        mainView.refreshLists();

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
                .build(uc);

        Credentials credentials2 = new CredentialsBuilder()
                .withName("General Kenobi")
                .withUserName("Tschüss")
                .withWebsite("Junge")
                .withPassword("Warum")
                .build(uc);

        List<Credentials> credentialsList = new ArrayList<>();
        credentialsList.add(credentials1);
        pm.getRootCategory().addCredentials(credentials1);
        mainView.refreshLists();

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
        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test");

        cc.addSecurityQuestion("Was?", "Das", credBuilder);
        Assert.assertEquals("Adding SecurityQuestion with question and answer failed", "Das", credBuilder.getSecurityQuestions().getOrDefault("Was?", null));
    }

    @Test(expected = NullPointerException.class)
    public void addSecurityQuestionTestQuestionNull() {
        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test");

        cc.addSecurityQuestion(null, "Das", credBuilder);
    }

    @Test(expected = NullPointerException.class)
    public void addSecurityQuestionTestAnswerNull() {
        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test");

        cc.addSecurityQuestion("Was", null, credBuilder);
    }

    @Test(expected = NullPointerException.class)
    public void addSecurityQuestionTestCredentialsNull() {
        cc.addSecurityQuestion("Was", "Das", null);
    }
    //endregion

    //region Remove Security Questions
    @Test
    public void removeSecurityQuestionTest() {
        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .withSecurityQuestion("Was", "Das");

        Assert.assertEquals("Credential does not contain Security Question", "Das", credBuilder.getSecurityQuestions().getOrDefault("Was", null));
        cc.removeSecurityQuestion("Was", "Das", credBuilder);
        Assert.assertTrue("Removing SecurityQuestion failed", credBuilder.getSecurityQuestions().isEmpty());
    }

    @Test
    public void removeSecurityQuestionTestNotExist() {
        SecurityQuestion sq1 = securityQuestionFromStrings("Was", "Das");
        SecurityQuestion sq2 = securityQuestionFromStrings("Warum", "Darum");

        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .withSecurityQuestion("Was", "Das");

        Assert.assertEquals("Credential does not contain Security Question", "Das", credBuilder.getSecurityQuestions().getOrDefault("Was", null));
        cc.removeSecurityQuestion("Warum", "Darum", credBuilder);
        Assert.assertEquals("A credential was added to/removed from the list", 1, credBuilder.getSecurityQuestions().size());
        Assert.assertEquals("The wrong Security Question was removed", "Das", credBuilder.getSecurityQuestions().getOrDefault("Was", null));
    }

    @Test
    public void removeSecurityQuestionTestQuestionNull() {
        CredentialsBuilder credBuilder = new CredentialsBuilder()
                .withName("Hello There")
                .withUserName("Dies")
                .withWebsite("ist.ein")
                .withPassword("test")
                .withSecurityQuestion("Was", "Das");

        Assert.assertEquals("Credential does not contain Security Question", "Das", credBuilder.getSecurityQuestions().getOrDefault("Was", null));
        cc.removeSecurityQuestion(null,"Dies", credBuilder);
        Assert.assertEquals("A credential was added to/removed from the list", 1, credBuilder.getSecurityQuestions().size());
        Assert.assertEquals("A Security Question was removed", "Das", credBuilder.getSecurityQuestions().getOrDefault("Was", null));
    }
    //endregion

    // Filter tests moved to CredentialControllerTreeTest

    //region Copy Password To Clipboard
    @Test
    public void copyPasswordToClipboardTest() {
        String password = "pass1";
        CredentialsBuilder credBuilder = new CredentialsBuilder("cred1", "user1", password, "site1");
        cc.copyPasswordToClipboard(credBuilder);

        Assert.assertEquals("clipboard contents do not equal the password", password, getClipboardContents());
    }
    //endregion

    //region Set Password Shown
    @Test
    public void setPasswordShownTest() {
        String password = "pass1";
        CredentialsBuilder credBuilder = new CredentialsBuilder("cred1", "user1", password, "site1");

        cc.setPasswordShown(credBuilder, true);
        Assert.assertEquals("password is not shown", password, mainView.getPasswordShown());

        cc.setPasswordShown(credBuilder, false);
        Assert.assertEquals("password is shown", "", mainView.getPasswordShown());
    }
    //endregion

    //Tests for getCredentialsByCategoryPath() in CredentialsControllerTreeTest

    @Test
    public void clearPasswordFromClipboardTest() {
        // Das Passwort muss aus der Zwischenablage gelöscht werden, wenn es in ihr enthalten ist
        String rawPassword = "Passwort123";
        CredentialsBuilder credBuilder = new CredentialsBuilder("Super Secret", "bonehead27", rawPassword, "lol5.org");
        setClipboardContents(credBuilder.getPassword());

        this.cc.clearPasswordFromClipboard(credBuilder);
        Assert.assertNotEquals("Password was not removed from clipboard", rawPassword, getClipboardContents());

        // Die Zwischenablage darf nicht verändert werden, wenn sich das Passwort nicht in ihr befindet
        rawPassword = "MeinPasswort";
        credBuilder = new CredentialsBuilder("Super Secret", "bonehead27", rawPassword, "lol5.org");
        setClipboardContents("NichtMeinPasswort");

        this.cc.clearPasswordFromClipboard(credBuilder);
        Assert.assertEquals("Clipboard was changed, eventhough it did not contain the password", "NichtMeinPasswort", getClipboardContents());
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

    private SecurityQuestion securityQuestionFromStrings(String question, String answer) {
        return new SecurityQuestion(uc.encryptText(question), uc.encryptText(answer));
    }
}