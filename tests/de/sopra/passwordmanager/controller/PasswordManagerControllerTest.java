package de.sopra.passwordmanager.controller;
import de.sopra.passwordmanager.model.Credentials;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.sopra.passwordmanager.model.PasswordManager;
import junit.framework.Assert;

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
				Credentials credentialsa = new Credentials("Namea", "Benutzernamea", "Hello_world", "URL", 3, "");
				Credentials credentialsb = new Credentials("Nameb", "Benutzernameb", "Hello_world", "URL", 2, "");
				Credentials credentialsc = new Credentials("Namec", "Benutzernamec", "Hello_world", "URL", 4, "");
				Credentials credentialsd = new Credentials("Named", "Benutzernamed", "Hello_world", "URL", 6, "");
				Credentials credentialse = new Credentials("Namee", "Benutzernamee", "Hello_world", "URL", 5, "");
				
		//holt sich RootCollection vom PasswordManager
		Collection<Credentials> testList = passwordManagerController.getPasswordManager().getRootCategory().getCredentials();
		//füllt RootCollection
		testList.add(credentialsa);
		testList.add(credentialsb);
		testList.add(credentialsc);
		testList.add(credentialsd);
		testList.add(credentialse);
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
