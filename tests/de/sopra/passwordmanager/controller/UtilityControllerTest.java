package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy.MainView;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.EncryptedString;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;

/**
 * @authors Niklas Falke, Mengcheng Jin
 */

public class UtilityControllerTest {
	private PasswordManagerController passwordManagerController;
	private UtilityController utilityController;
	private MainView mainWindowAUI;
	private PasswordManager passwordManager;

    @Before
    public void setUp() throws Exception {
    	passwordManagerController = PasswordManagerControllerDummy.getNewController();
    	utilityController = passwordManagerController.getUtilityController();
    	mainWindowAUI = (MainView) passwordManagerController.getMainWindowAUI();
		passwordManager = passwordManagerController.getPasswordManager();
    }

    @Test
    public void generatePasswordTest() {
        utilityController.generatePassword();
        String password = mainWindowAUI.getPasswordShown();

        Assert.assertTrue(utilityController.checkQuality(password) > PasswordManagerController.MINUM_SAFE_QUALITY);
    }

    @Test
    public void exportImportFileTest() {
    	//daten in das Modell eintragen
    	File file = null; //FIXME: Dateipfad festlegen
    	String masterPassword = "test";
		passwordManager.setMasterPassword(masterPassword);
		passwordManager.setMasterPasswordReminderDays(5);
    	passwordManager.setMasterPasswordLastChanged(LocalDateTime.now());

    	Category root = passwordManager.getRootCategory();
    	Category sub = new Category("sub");
    	root.addSubCategory(sub);
    	Category sub1 = new Category("sub1");
    	sub.addSubCategory(sub1);
    	Credentials c1 = new CredentialsBuilder("c1", "c2", "pw", "url")
				.withChangeReminderDays(5)
				.build(utilityController);
    	Credentials c2 = new CredentialsBuilder("c21", "c22", "pw2", "url2")
				.withChangeReminderDays(6)
				.build(utilityController);
    	sub.addCredentials(c1);
    	sub.addCredentials(c2);
    	sub1.addCredentials(c2);

    	//Test
    	utilityController.exportFile(file);
    	utilityController.importFile(file, masterPassword);

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
    	Assert.assertEquals(credentials.getName(), c2.getName());
    	Assert.assertEquals(credentials.getUserName(), c2.getUserName());
    	Assert.assertEquals(credentials.getPassword(), c2.getPassword());
    	Assert.assertEquals(credentials.getWebsite(), c2.getWebsite());
    	Assert.assertEquals(credentials.getChangeReminderDays(), c2.getChangeReminderDays());
    	Assert.assertEquals(credentials.getNotes(), c2.getNotes());
    	   	
    	//aufräumen
    	file.delete();
    }

    @Test
    public void encryptDecryptTextTest() {
        String text = "password1";
        EncryptedString encrypted = utilityController.encryptText(text);
        String decrypted = utilityController.decryptText(encrypted);
        Assert.assertEquals(text, decrypted);
        Assert.assertNotEquals("encryption does not encrypt", text, encrypted.getEncryptedContent());
        Assert.assertNotEquals("decryption does not decrypt", decrypted, encrypted.getEncryptedContent());
    }


    @Test
    public void checkQuality() {
        //TODO:Braucht jetzt nicht.

    }
}
