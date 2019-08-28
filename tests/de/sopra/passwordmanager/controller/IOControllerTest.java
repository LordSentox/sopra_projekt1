package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class IOControllerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void importFile() {
    }

    @Test
    public void exportFileTest() {
        PasswordManagerController passwordManagerController = PasswordManagerControllerDummy.getNewController();
        PasswordManager passwordManager = passwordManagerController.getPasswordManager();
        UtilityController utilityController = passwordManagerController.getUtilityController();
        IOController ioController = passwordManagerController.getIOController();
        //daten in das Modell eintragen
        File file = new File("importExportTest.xml"); //FIXME: Dateipfad festlegen
        String masterPassword = "test";
        passwordManager.setMasterPassword(masterPassword);
        passwordManager.setMasterPasswordReminderDays(5);
        passwordManager.setMasterPasswordLastChanged(LocalDateTime.now());

        Category root = passwordManager.getRootCategory();
        Category sub = new Category("sub");
        root.addSubCategory(sub);
        Category sub1 = new Category("sub1");
        sub.addSubCategory(sub1);
        Credentials credentials1 = new CredentialsBuilder("c1", "c2", "pw", "url")
                .withChangeReminderDays(5)
                .build(utilityController);
        Credentials credentials2 = new CredentialsBuilder("c21", "c22", "pw2", "url2")
                .withChangeReminderDays(6)
                .build(utilityController);
        sub.addCredentials(credentials1);
        sub.addCredentials(credentials2);
        sub1.addCredentials(credentials2);

        //Test
        ioController.exportFile(file);
        ioController.importFile(file, masterPassword, masterPassword, true);

        // Daten testen, ob diese im Modell vorhanden sind
        root = passwordManager.getRootCategory();
        Assert.assertFalse(root.getSubCategories().isEmpty());
        Assert.assertEquals(root.getSubCategories().size(), 1);

        Category subCategory1 = root.getSubCategories().stream().findFirst().get();
        Assert.assertEquals(subCategory1.getName(), "sub");
        Assert.assertFalse(subCategory1.getSubCategories().isEmpty());
        Assert.assertEquals(subCategory1.getSubCategories().size(), 1);

        Category subCategory2 = subCategory1.getSubCategories().stream().findFirst().get();
        Assert.assertEquals(subCategory2.getName(), "sub1");
        Assert.assertTrue(subCategory2.getSubCategories().isEmpty());

        Assert.assertFalse(subCategory1.getCredentials().isEmpty());
        Assert.assertEquals(subCategory1.getCredentials().size(), 2);
        Assert.assertFalse(subCategory2.getCredentials().isEmpty());
        Assert.assertEquals(subCategory2.getCredentials().size(), 1);

        Credentials credentials = subCategory2.getCredentials().stream().findFirst().get();
        Assert.assertEquals(credentials.getName(), credentials2.getName());
        Assert.assertEquals(credentials.getUserName(), credentials2.getUserName());
        Assert.assertEquals(credentials.getPassword(), credentials2.getPassword());
        Assert.assertEquals(credentials.getWebsite(), credentials2.getWebsite());
        Assert.assertEquals(credentials.getChangeReminderDays(), credentials2.getChangeReminderDays());
        Assert.assertEquals(credentials.getNotes(), credentials2.getNotes());

        //aufräumen
        file.delete();
    }
}