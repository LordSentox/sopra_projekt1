package de.sopra.passwordmanager.model;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;


public class PasswordManagerTest {

    @Test
    public void getSetTest() {
        PasswordManager passwordManager = new PasswordManager();
        LocalDateTime now = LocalDateTime.now();
        passwordManager.setMasterPassword("pw");
        passwordManager.setMasterPasswordReminderDays(1);
        passwordManager.setMasterPassordLastChanged(now);
        Assert.assertEquals("retrieved password not equal to expected", "pw", passwordManager.getMasterPassword());
        Assert.assertEquals("retrieved reminder not equal to expected", 1, passwordManager.getMasterPasswordReminderDays());
        Assert.assertEquals("retrieved lastChanged time not equal to expected", now, passwordManager.getMasterPassordLastChanged());
    }

}
