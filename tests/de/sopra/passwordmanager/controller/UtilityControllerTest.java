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
	private IOController ioController;
	private MainView mainWindowAUI;
	private PasswordManager passwordManager;

    @Before
    public void setUp() throws Exception {
    	passwordManagerController = PasswordManagerControllerDummy.getNewController();
    	utilityController = passwordManagerController.getUtilityController();
    	ioController = passwordManagerController.getIOController();
    	mainWindowAUI = (MainView) passwordManagerController.getMainWindowAUI();
		passwordManager = passwordManagerController.getPasswordManager();
    }

    @Test
    public void generatePasswordTest() {
    	CredentialsBuilder credBuilder = new CredentialsBuilder("Hi", "How", "name", "asd");
        utilityController.generatePassword(credBuilder);

        Assert.assertTrue(utilityController.checkQuality(credBuilder.getPassword(), credBuilder.getUserName()) > PasswordManagerController.MINIMUM_SAFE_QUALITY);
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
	public void encryptTextBreak() {
    	Assert.assertNull("Encryption did not break :(", utilityController.encryptText(null));
	}

	@Test
	public void decryptTextBreak() {
		Assert.assertNull("Decryption did not break :(", utilityController.decryptText(new EncryptedString(null)));
	}


    @Test
    public void checkQuality() {
        //TODO:Braucht jetzt nicht.
    }
}
