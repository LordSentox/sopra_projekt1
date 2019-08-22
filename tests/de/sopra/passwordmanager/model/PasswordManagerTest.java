package de.sopra.passwordmanager.model;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;


public class PasswordManagerTest {

	@Test
	public void getSetTest() {
		PasswordManager pm= new PasswordManager();
		LocalDateTime now = LocalDateTime.now();
		BasePassword bp = new BasePassword("pw",1,now);
		pm.setMasterPassword(bp);	
		Assert.assertEquals("retrieved value not equal to expected",bp, pm.getMasterPassword());
	}	
		
}
