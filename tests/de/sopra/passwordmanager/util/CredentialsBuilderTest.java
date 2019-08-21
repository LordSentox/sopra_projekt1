package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.SecurityQuestion;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CredentialsBuilderTest {

    @Test
    public void basicBuildTest() {
        String name = "cred1";
        String userName = "user1";
        String password = "passwort123";
        String website = "www.hallo.de";
        int changeReminder = 3;
        Date created = new Date();
        Date lastChanged = new Date();
        String notes = "Dies ist ein Debug Eintrag";
        SecurityQuestion sq1 = new SecurityQuestion("Warum?", "Da so");
        String question2 = "Was machen Sachen?";
        String answer2 = "Dinge";
        SecurityQuestion sq3 = new SecurityQuestion("Was ist die Antwort auf alles?", "42");
        Set<SecurityQuestion> questions = new HashSet<>();
        questions.add(sq3);


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
                .build();

        Assert.assertEquals(cred1.getName(), name);
        Assert.assertEquals(cred1.getUserName(), userName);
        Assert.assertEquals(cred1.getPassword(), password);
        Assert.assertEquals(cred1.getWebsite(), website);
        Assert.assertEquals(cred1.getChangeReminderDays(), new Integer(changeReminder));
        Assert.assertEquals(cred1.getCreatedAt(), created);
        Assert.assertEquals(cred1.getLastChanged(), lastChanged);
        Assert.assertEquals(cred1.getNotes(), notes);

        Collection<SecurityQuestion> questions1 = cred1.getSecurityQuestions();

        Assert.assertTrue(questions1.contains(sq1));
        Assert.assertTrue(questions1.contains(new SecurityQuestion(question2, answer2)));
        Assert.assertTrue(questions1.containsAll(questions));
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
                .build();
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
                .build();
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
                .build();
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
                .build();
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
                .build();
    }
}
