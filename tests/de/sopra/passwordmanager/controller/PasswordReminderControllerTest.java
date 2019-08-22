package de.sopra.passwordmanager.controller;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import de.sopra.passwordmanager.model.BasePassword;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;

public class PasswordReminderControllerTest {
	
	private PasswordManagerController passwordManagerController;
    private PasswordManager passwordManager;

    private long daysToMillis(int days) {
        return days * 3600 * 24 * 1000;
    }

    @Before
    public void setUp() throws Exception {
        this.passwordManagerController = PasswordManagerControllerDummy.getNewController();
        this.passwordManager = this.passwordManagerController.getPasswordManager();
    }

	@Test
	public void testPasswordReminderController() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasToBeChanged() {
		//legt Passworteintrag an sowie Datum
		Credentials credentials = new Credentials("Name", "Benutzername", "Hello_world", "URL", 5, "");
		Date sixDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(6));
		//ändert LanstChanged-Datum des Passworteintrages auf vor 6 Tagen
		credentials.setLastChanged(sixDaysEarlier);	
		//prüft, ob hasToBeChanged für den Passworteintrag true zurückgibt
        assertTrue(this.passwordManagerController.getPasswordReminderController().hasToBeChanged(credentials));
        
        Date fourDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(4));
        credentials.setLastChanged(fourDaysEarlier);
        assertFalse(this.passwordManagerController.getPasswordReminderController().hasToBeChanged(credentials));
        
        Date fiveDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(5));
        credentials.setLastChanged(fiveDaysEarlier);
        assertTrue("Grenzfall", this.passwordManagerController.getPasswordReminderController().hasToBeChanged(credentials));
        
	}

	@Test
	public void testPasswordsToBeChanged() {
		//legt Passworteinträge an
		Credentials credentialsa = new Credentials("Namea", "Benutzernamea", "Hello_world", "URL", 3, "");
		Credentials credentialsb = new Credentials("Nameb", "Benutzernameb", "Hello_world", "URL", 2, "");
		Credentials credentialsc = new Credentials("Namec", "Benutzernamec", "Hello_world", "URL", 4, "");
		Credentials credentialsd = new Credentials("Named", "Benutzernamed", "Hello_world", "URL", 6, "");
		Credentials credentialse = new Credentials("Namee", "Benutzernamee", "Hello_world", "URL", 5, "");
		//legt Testdaten an
		Date sevenDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(7));
		Date fourDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(4));
		//legt ArrayListe mit Passworteinträgen an
		ArrayList<Credentials> credentials = new ArrayList<Credentials>();
		credentials.add(credentialsa);
		credentials.add(credentialsb);
		credentials.add(credentialsc);
		credentials.add(credentialsd);
		credentials.add(credentialse);
		
		//ändert die Testliste so ab, dass die LastChanged-Daten sieben Tage zurück liegen
		for(int i=0; i<5; i++){
			credentials.get(i).setLastChanged(sevenDaysEarlier);
		}
		//prüft, ob die komplette Testliste zurückgegeben wird, bei der alle Timer abgelaufen sind
		assertEquals(this.passwordManagerController.getPasswordReminderController().passwordsToBeChanged(), credentials);
		
		//Legt Arrayliste mit Passworteinträgen an, die angezeigt werden müssen, wenn vier Tage vergangen sind
		ArrayList<Credentials> credentialsTestPartial = new ArrayList<Credentials>();
		credentialsTestPartial.add(credentialsa);
		credentialsTestPartial.add(credentialsb);
		credentialsTestPartial.add(credentialsc);
		
		//ändert die Testliste so ab, dass die LastChanged-Daten vier Tage zurück liegen
		for(int i=0; i<5; i++){
			credentials.get(i).setLastChanged(fourDaysEarlier);
		}
		
		//prüft, ob die credentialsTestPartial-Liste von passwordsToBeChanged zurückgegeben wird
		assertEquals(this.passwordManagerController.getPasswordReminderController().passwordsToBeChanged(), credentialsTestPartial);
		
	}

}
