package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy;
import de.sopra.passwordmanager.controller.UtilityController;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.SecurityQuestion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CredentialsBuilderTest {

    private PasswordManagerController pmc;
    private UtilityController uc;

    @Before
    public void setUp() {
        pmc = PasswordManagerControllerDummy.getNewController();
        uc = pmc.getUtilityController();
    }

    @Test
    public void fullBuildTest() {
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";
        EncryptedString encryptedPassword = uc.encryptText(password);
        String website = "www.hallo.de";
        int changeReminder = 3;
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime lastChanged = LocalDateTime.now();
        String notes = "Dies ist ein Debug Eintrag";
        SecurityQuestion sq1 = new SecurityQuestion("Warum?", "Da so");
        String question2 = "Was machen Sachen?";
        String answer2 = "Dinge";
        SecurityQuestion sq3 = new SecurityQuestion("Was ist die Antwort auf alles?", "42");
        Set<SecurityQuestion> questions = new HashSet<>();
        questions.add(sq3);

        //Erstelle das erste Credentials mithilfe des Builders
        Credentials cred1 = new CredentialsBuilder()
                .withName(name)
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .withChangeReminderDays(changeReminder)
                .withCreated(created)
                .withLastChanged(lastChanged)
                .withNotes(notes)
                .withSecurityQuestion(sq1)
                .withSecurityQuestion(question2, answer2)
                .withSecurityQuestions(questions)
                .build(uc);

        // Erstelle das zweite Credentials objekt mit denselben daten, aber hier mit dem Konstruktor
        Credentials cred2 = new Credentials(name, userName, encryptedPassword, created);
        cred2.setWebsite(website);
        cred2.setChangeReminderDays(changeReminder);
        cred2.setLastChanged(lastChanged);
        cred2.setNotes(notes);
        cred2.addSecurityQuestion(sq1);
        cred2.addSecurityQuestion(question2, answer2);
        cred2.addSecurityQuestion(sq3);

        Assert.assertEquals("Credentials not equal", cred1, cred2);
    }

    @Test
    public void testBuildMinimal(){
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";
        EncryptedString encryptedPassword = uc.encryptText(password);
        String website = "www.hallo.de";

        Credentials cred1 = new CredentialsBuilder()
                .withName(name)
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .build(uc);

        Credentials cred2 = new Credentials(name, userName, encryptedPassword, cred1.getCreatedAt());
        cred2.setLastChanged(cred1.getLastChanged());
        cred2.setWebsite(website);
        cred2.setNotes("");

        Assert.assertEquals("Minimal built credentials not equal to expected", cred1, cred2);
    }

    @Test(expected = CredentialsBuilder.CredentialsBuilderException.class)
    public void missingNameTest() {
        String userName = "user1";
        String password = "passwort123";
        String website = "www.hallo.de";

        //Muss Exception werfen, weil name fehlt
        new CredentialsBuilder()
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .build(uc);
    }

    @Test(expected = CredentialsBuilder.CredentialsBuilderException.class)
    public void missingUserNameTest() {
        String name = "cred1";
        String password = "passwort123";
        String website = "www.hallo.de";

        //Muss Exception werfen, weil userName fehlt
        new CredentialsBuilder()
                .withName(name)
                .withPassword(password)
                .withWebsite(website)
                .build(uc);
    }


    @Test(expected = CredentialsBuilder.CredentialsBuilderException.class)
    public void missingPasswordTest() {
        String name = "cred1";
        String userName = "user1";
        String website = "www.hallo.de";

        //Muss Exception werfen, weil passwort fehlt
        new CredentialsBuilder()
                .withName(name)
                .withUserName(userName)
                .withWebsite(website)
                .build(uc);
    }

    @Test(expected = CredentialsBuilder.CredentialsBuilderException.class)
    public void missingWebsiteTest() {
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";

        //Muss Exception werfen, weil website fehlt
        new CredentialsBuilder()
                .withName(name)
                .withUserName(userName)
                .withPassword(password)
                .build(uc);
    }

    @Test(expected = CredentialsBuilder.CredentialsBuilderException.class)
    public void invalidChangeReminderTest() {
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";
        String website = "www.hallo.de";
        int changeReminder = -1;

        //Muss Exception werfen, weil changeReminder < 1 ist
        new CredentialsBuilder()
                .withName(name)
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .withChangeReminderDays(changeReminder)
                .build(uc);
    }
}
