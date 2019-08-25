package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PasswordManagerControllerTest {
    private PasswordManagerController passwordManagerController;
    private PasswordManager passwordManager;
    private UtilityController uc;

    @Before
    public void setUp() throws Exception {
        this.passwordManagerController = PasswordManagerControllerDummy.getNewController();
        this.passwordManager = this.passwordManagerController.getPasswordManager();
        uc = passwordManagerController.getUtilityController();
    }

    @Test
    public void testRemoveAll() {

        //legt Passworteinträge an
        Credentials credentialsA = new CredentialsBuilder("Namea", "Benutzernamea", "Hello_world", "URL")
                .withChangeReminderDays(3)
                .build(uc);
        Credentials credentialsB = new CredentialsBuilder("Nameb", "Benutzernameb", "Hello_world", "URL")
                .withChangeReminderDays(2)
                .build(uc);
        Credentials credentialsC = new CredentialsBuilder("Namec", "Benutzernamec", "Hello_world", "URL")
                .withChangeReminderDays(4)
                .build(uc);
        Credentials credentialsD = new CredentialsBuilder("Named", "Benutzernamed", "Hello_world", "URL")
                .withChangeReminderDays(6)
                .build(uc);
        Credentials credentialsE = new CredentialsBuilder("Namee", "Benutzernamee", "Hello_world", "URL")
                .withChangeReminderDays(5)
                .build(uc);

        //holt sich RootCollection vom PasswordManager
        Collection<Credentials> testList = passwordManagerController.getPasswordManager().getRootCategory().getCredentials();
        //füllt RootCollection
        testList.add(credentialsA);
        testList.add(credentialsB);
        testList.add(credentialsC);
        testList.add(credentialsD);
        testList.add(credentialsE);
        //führt Funktion aus
        passwordManagerController.removeAll();
        Assert.assertTrue(testList.isEmpty());


    }

    @Test
    public void requestLoginTest() {
        //TODO was soll passieren?
    }

    @Test
    public void checkQualityTest() {
        CredentialsBuilder builder = new CredentialsBuilder("My Account", "Leonidas", "Sparta did nothing wrong", "this-is.sparta");
        List<String> passwords = Arrays.asList("Stratzenbl1tz", "123", "", "Hello, there", "142aB5][9p5assw15ort!5xD", "aA53]@`");

        for (String password : passwords) {
            // TODO: Sollten hier feste Werte angegeben werden, statt einen Aufruf auf checkQuality zu tätigen?
            int passwordQuality = this.passwordManagerController.getUtilityController().checkQuality(password);

            builder.withPassword(password);
            this.passwordManagerController.checkQuality(builder);

            PasswordManagerControllerDummy.MainView mainView =
                    (PasswordManagerControllerDummy.MainView) this.passwordManagerController.getMainWindowAUI();
            Assert.assertEquals(passwordQuality, mainView.getPasswordQuality());
        }
    }

    @Test
    public void saveEntryTest() {
        //TODO doc fehlt
    }
}
