package de.sopra.passwordmanager.model;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy;
import de.sopra.passwordmanager.controller.UtilityController;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.EncryptedString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CredentialsTest {

	private PasswordManagerController pmc;
	private UtilityController uc;

	@Before
	public void setUp() {
		pmc = PasswordManagerControllerDummy.getNewController();
		uc = pmc.getUtilityController();
	}

	@Test
	public void getSetTest() {
		Credentials cred = new CredentialsBuilder()
                .withName("cred")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build(uc);
		cred.setName("cred2");
		Assert.assertEquals("retrieved value not equal to expected","cred2", cred.getName());
		cred.setUserName("user2");
		Assert.assertEquals("retrieved value not equal to expected","user2", cred.getUserName());
		EncryptedString encryptedPassword = new EncryptedString(uc.encryptText("Passwort123"));
		cred.setPassword(encryptedPassword);
		Assert.assertEquals("retrieved value not equal to expected",encryptedPassword, cred.getPassword());
		cred.setWebsite("www.hallo.com");
		Assert.assertEquals("retrieved value not equal to expected","www.hallo.com",cred.getWebsite());
	}
	
}
