package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy;
import de.sopra.passwordmanager.controller.UtilityController;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.EncryptedString;
import de.sopra.passwordmanager.model.SecurityQuestion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        String question1 = "Warum?";
        String answer1 = "Da so";
        String question2 = "Was machen Sachen?";
        String answer2 = "Dinge";
        SecurityQuestion sq2 = securityQuestionFromStrings(question2, answer2);
        Map<String, String> questions = new HashMap<>();
        questions.put(question2, answer2);

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
                .withSecurityQuestion(question1, answer1)
                .withSecurityQuestions(questions)
                .build(uc);

        // Erstelle das zweite Credentials objekt mit denselben daten, aber hier mit dem Konstruktor
        Credentials cred2 = new Credentials(name, userName, encryptedPassword, created);
        cred2.setWebsite(website);
        cred2.setChangeReminderDays(changeReminder);
        cred2.setLastChanged(lastChanged);
        cred2.setNotes(notes);
        cred2.addSecurityQuestion(uc.encryptText(question1), uc.encryptText(answer1));
        cred2.addSecurityQuestion(sq2);

        Assert.assertEquals("Credentials not equal", cred1, cred2);
    }

    @Test
    public void minimalBuildTest(){
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
    @Test
    public void minimalBuildTestAbbreviatedConstructor(){
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";
        EncryptedString encryptedPassword = uc.encryptText(password);
        String website = "www.hallo.de";

        Credentials cred1 = new CredentialsBuilder(name, userName, password, website)
                .build(uc);

        Credentials cred2 = new Credentials(name, userName, encryptedPassword, cred1.getCreatedAt());
        cred2.setLastChanged(cred1.getLastChanged());
        cred2.setWebsite(website);
        cred2.setNotes("");

        Assert.assertEquals("Minimal built credentials not equal to expected", cred1, cred2);
    }

    @Test(expected = NullPointerException.class)
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

    @Test(expected = NullPointerException.class)
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


    @Test(expected = NullPointerException.class)
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

    @Test(expected = NullPointerException.class)
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

    /**
     * Testet, ob der Konstruktor aus einem Credentials Objekt funktioniert
     * Setzt voraus, dass encryption und decryption des {@link UtilityController} funktionieren
     */
    @Test
    public void fromCredentialsConstructorTest(){
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";
        EncryptedString encryptedPassword = uc.encryptText(password);
        String website = "www.hallo.de";
        int changeReminder = 3;
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime lastChanged = LocalDateTime.now();
        String notes = "Dies ist ein Debug Eintrag";
        String question1 = "Warum?";
        String answer1 = "Da so";
        String question2 = "Was machen Sachen?";
        String answer2 = "Dinge";
        SecurityQuestion sq2 = securityQuestionFromStrings(question2, answer2);
        Map<String, String> questions = new HashMap<>();
        questions.put(question2, answer2);

        CredentialsBuilder credBuilder1 = new CredentialsBuilder()
                .withName(name)
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .withChangeReminderDays(changeReminder)
                .withCreated(created)
                .withLastChanged(lastChanged)
                .withNotes(notes)
                .withSecurityQuestion(question1, answer1)
                .withSecurityQuestions(questions);

        Credentials cred1 = new Credentials(name, userName, encryptedPassword, created);
        cred1.setWebsite(website);
        cred1.setChangeReminderDays(changeReminder);
        cred1.setLastChanged(lastChanged);
        cred1.setNotes(notes);
        cred1.addSecurityQuestion(securityQuestionFromStrings(question1, answer1));
        cred1.addSecurityQuestion(sq2);

        CredentialsBuilder credBuilder2 = new CredentialsBuilder(cred1, uc);

        Assert.assertEquals("CredentialsBuilders not equal", credBuilder1, credBuilder2);
    }

    @Test
    public void copyToTest(){
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";
        EncryptedString encryptedPassword = uc.encryptText(password);
        String website = "www.hallo.de";
        int changeReminder = 3;
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime lastChanged = LocalDateTime.now();
        String notes = "Dies ist ein Debug Eintrag";
        String question1 = "Warum?";
        String answer1 = "Da so";

        Credentials cred = new Credentials("Name", "userName", uc.encryptText("saaswort"), created);
        cred.setLastChanged(LocalDateTime.now());
        cred.setNotes("sooos");
        cred.setWebsite("");
        cred.setChangeReminderDays(1);
        cred.addSecurityQuestion(securityQuestionFromStrings("Huh?", "oof"));

        CredentialsBuilder credBuilder1 = new CredentialsBuilder()
                .withName(name)
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .withChangeReminderDays(changeReminder)
                .withCreated(created)
                .withLastChanged(lastChanged)
                .withNotes(notes)
                .withSecurityQuestion(question1, answer1);
        credBuilder1.copyTo(cred, uc);
        Credentials cred2 = credBuilder1.build(uc);

        Assert.assertEquals("copyTo failed", cred, cred2);
    }

    @Test
    public void getterTest() {
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";
        EncryptedString encryptedPassword = uc.encryptText(password);
        String website = "www.hallo.de";
        Integer changeReminder = 3;
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime lastChanged = LocalDateTime.now();
        String notes = "Dies ist ein Debug Eintrag";
        String question1 = "Warum?";
        String answer1 = "Da so";
        HashMap<String, String> questions = new HashMap<>();
        questions.put(question1, answer1);

        CredentialsBuilder credBuilder1 = new CredentialsBuilder()
                .withName(name)
                .withUserName(userName)
                .withPassword(password)
                .withWebsite(website)
                .withChangeReminderDays(changeReminder)
                .withCreated(created)
                .withLastChanged(lastChanged)
                .withNotes(notes)
                .withSecurityQuestions(questions);
        Assert.assertEquals("name getter value not equal", name, credBuilder1.getName());
        Assert.assertEquals("username getter value not equal", userName, credBuilder1.getUserName());
        Assert.assertEquals("password getter value not equal", password, credBuilder1.getPassword());
        Assert.assertEquals("website getter value not equal", website, credBuilder1.getWebsite());
        Assert.assertEquals("change reminder getter value not equal", changeReminder, credBuilder1.getChangeReminderDays());
        Assert.assertEquals("created getter value not equal", created, credBuilder1.getCreatedAt());
        Assert.assertEquals("last changed getter value not equal", lastChanged, credBuilder1.getLastChanged());
        Assert.assertEquals("notes getter value not equal", notes, credBuilder1.getNotes());
        Assert.assertEquals("security questions getter value not equal", questions, credBuilder1.getSecurityQuestions());
    }

    private SecurityQuestion securityQuestionFromStrings(String question, String answer) {
        return new SecurityQuestion(uc.encryptText(question), uc.encryptText(answer));
    }
}
