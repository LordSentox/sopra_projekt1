package de.sopra.passwordmanager.model;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;


public class PasswordManagerTest {


	@Test
	public void getSetTest() {
		PasswordManager pm = new PasswordManager();
		LocalDateTime now = LocalDateTime.now();
		pm.setMasterPassword("pw");
		pm.setMasterPasswordLastChanged(now);
		pm.setMasterPasswordReminderDays(5);
		Assert.assertEquals("retrieved master password not equal to expected", "pw", pm.getMasterPassword());
		Assert.assertEquals("retrieved lastChanged value not equal to expected", now, pm.getMasterPasswordLastChanged());
		Assert.assertEquals("retrieved reminderDays value not equal to expected", 5, pm.getMasterPasswordReminderDays());
		Assert.assertEquals("retrieved reminderDays value not equal to expected", 5, pm.getMasterPasswordReminderDays());
		pm.setMasterPasswordLastChanged();
	}

}
