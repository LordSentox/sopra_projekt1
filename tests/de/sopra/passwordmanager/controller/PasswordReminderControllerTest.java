package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class PasswordReminderControllerTest {
	
	private PasswordManagerController passwordManagerController;
    private PasswordManager passwordManager;
	private UtilityController uc;

	@Before
    public void setUp() throws Exception {
        this.passwordManagerController = PasswordManagerControllerDummy.getNewController();
        this.passwordManager = this.passwordManagerController.getPasswordManager();
        uc = passwordManagerController.getUtilityController();
    }

	@Test
	public void testPasswordReminderController() {
		//TODO: testPasswordReminderController
		fail("Not yet implemented");
	}

	@Test
	public void testHasToBeChanged() {
		//legt Passworteintrag an sowie Datum
		Credentials credentials = new CredentialsBuilder("Name", "Benutzername", "Hello_world", "URL")
				.withChangeReminderDays(5)
				.build(uc);
		LocalDateTime sixDaysEarlier = LocalDateTime.now().minus(6, ChronoUnit.DAYS);
		//ändert LanstChanged-Datum des Passworteintrages auf vor 6 Tagen
		credentials.setLastChanged(sixDaysEarlier);	
		//prüft, ob hasToBeChanged für den Passworteintrag true zurückgibt
        assertTrue(this.passwordManagerController.getPasswordReminderController().hasToBeChanged(credentials));

		LocalDateTime fourDaysEarlier = LocalDateTime.now().minus(4, ChronoUnit.DAYS);
        credentials.setLastChanged(fourDaysEarlier);
        assertFalse(this.passwordManagerController.getPasswordReminderController().hasToBeChanged(credentials));

		LocalDateTime fiveDaysEarlier = LocalDateTime.now().minus(5, ChronoUnit.DAYS);
        credentials.setLastChanged(fiveDaysEarlier);
        assertTrue("Grenzfall", this.passwordManagerController.getPasswordReminderController().hasToBeChanged(credentials));
        
	}

	@Test
	public void testPasswordsToBeChanged() {
		//legt Passworteinträge an
		Credentials credentialsa = new CredentialsBuilder("Namea", "Benutzernamea", "Hello_world", "URL")
				.withChangeReminderDays(3)
				.build(uc);
		Credentials credentialsb = new CredentialsBuilder("Nameb", "Benutzernameb", "Hello_world", "URL")
				.withChangeReminderDays(2)
				.build(uc);
		Credentials credentialsc = new CredentialsBuilder("Namec", "Benutzernamec", "Hello_world", "URL")
				.withChangeReminderDays(4)
				.build(uc);
		Credentials credentialsd = new CredentialsBuilder("Named", "Benutzernamed", "Hello_world", "URL")
				.withChangeReminderDays(6)
				.build(uc);
		Credentials credentialse = new CredentialsBuilder("Namee", "Benutzernamee", "Hello_world", "URL")
				.withChangeReminderDays(5)
				.build(uc);
		//legt Testdaten an
		LocalDateTime sevenDaysEarlier = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
		LocalDateTime fourDaysEarlier = LocalDateTime.now().minus(6, ChronoUnit.DAYS);
		//legt ArrayListe mit Passworteinträgen an
		ArrayList<Credentials> credentials = new ArrayList<>();
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
