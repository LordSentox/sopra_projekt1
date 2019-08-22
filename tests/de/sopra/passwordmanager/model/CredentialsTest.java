package de.sopra.passwordmanager.model;

import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Assert;
import org.junit.Test;


public class CredentialsTest {

	@Test
	public void getSetTest() {
		Credentials cred = new CredentialsBuilder()
                .withName("cred")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();
		cred.setName("cred2");;
		Assert.assertEquals("retrieved value not equal to expected","cred2", cred.getName());
		cred.setUserName("user2");;
		Assert.assertEquals("retrieved value not equal to expected","user2", cred.getUserName());
		cred.setPassword("Passwort321");;
		Assert.assertEquals("retrieved value not equal to expected","Passwort321", cred.getPassword());
		cred.setWebsite("www.hallo.com");
		Assert.assertEquals("retrieved value not equal to expected","www.hallo.com",cred.getWebsite());
	}
	
}
