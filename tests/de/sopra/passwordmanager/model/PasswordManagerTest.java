package de.sopra.passwordmanager.model;

import org.junit.Test;

import java.util.Date;

import org.junit.Assert;


public class PasswordManagerTest {

	@Test
	public void getSetTest() {
		PasswordManager pm= new PasswordManager();
		Date now = new Date();
		BasePassword bp = new BasePassword("pw",1,now);
		pm.setMasterPassword(bp);	
		Assert.assertEquals("retrieved value not equal to expected",bp, pm.getMasterPassword());
	}	
		
}
