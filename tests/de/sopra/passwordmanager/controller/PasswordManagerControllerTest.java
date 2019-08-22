package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Credentials;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import de.sopra.passwordmanager.model.PasswordManager;

public class PasswordManagerControllerTest {
    private PasswordManagerController passwordManagerController;
    private PasswordManager passwordManager;

    @Before
    public void setUp() throws Exception {
        this.passwordManagerController = PasswordManagerControllerDummy.getNewController();
        this.passwordManager = this.passwordManagerController.getPasswordManager();
    }

    @Test
    public void testRemoveAll() {

        //legt Passworteinträge an
        Credentials credentialsA = new CredentialsBuilder("Namea", "Benutzernamea", "Hello_world", "URL")
                .withChangeReminderDays(3)
                .build();
        Credentials credentialsB = new CredentialsBuilder("Nameb", "Benutzernameb", "Hello_world", "URL")
                .withChangeReminderDays(2)
                .build();
        Credentials credentialsC = new CredentialsBuilder("Namec", "Benutzernamec", "Hello_world", "URL")
                .withChangeReminderDays(4)
                .build();
        Credentials credentialsD = new CredentialsBuilder("Named", "Benutzernamed", "Hello_world", "URL")
                .withChangeReminderDays(6)
                .build();
        Credentials credentialsE = new CredentialsBuilder("Namee", "Benutzernamee", "Hello_world", "URL")
                .withChangeReminderDays(5)
                .build();

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
    public void testRequestLogin() {
        //TODO was soll passieren?
    }

    @Test
    public void testSaveEntry() {
        //TODO doc fehlt
    }

}
