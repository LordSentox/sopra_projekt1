package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

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
	}

	@Test
	public void testHasToBeChanged() {
		//legt Passworteintrag an sowie Datum
		Credentials credentials = new CredentialsBuilder("Name", "Benutzername", "Hello_world", "URL")
				.withChangeReminderDays(5)
				.build(uc);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime sixDaysEarlier = now.minus(6, ChronoUnit.DAYS);
		//ändert LanstChanged-Datum des Passworteintrages auf vor 6 Tagen
		credentials.setLastChanged(sixDaysEarlier);	
		//prüft, ob hasToBeChanged für den Passworteintrag true zurückgibt
        assertTrue(this.passwordManagerController.getPasswordReminderController().hasToBeChanged(credentials));

		LocalDateTime fourDaysEarlier = now.minus(4, ChronoUnit.DAYS);
        credentials.setLastChanged(fourDaysEarlier);
        assertFalse(this.passwordManagerController.getPasswordReminderController().hasToBeChanged(credentials));

		LocalDateTime fiveDaysEarlier = now.minus(5, ChronoUnit.DAYS);
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
		LocalDateTime fourDaysEarlier = LocalDateTime.now().minus(4, ChronoUnit.DAYS);
		//legt ArrayListe mit Passworteinträgen an
		Set<Credentials> credentials = new HashSet<>();
		credentials.add(credentialsa);
		credentials.add(credentialsb);
		credentials.add(credentialsc);
		credentials.add(credentialsd);
		credentials.add(credentialse);
		
		//ändert die Testliste so ab, dass die LastChanged-Daten sieben Tage zurück liegen
		credentials.forEach(cred -> {
			cred.setLastChanged(sevenDaysEarlier);
			passwordManager.getRootCategory().addCredentials(cred);
		});


		//prüft, ob die komplette Testliste zurückgegeben wird, bei der alle Timer abgelaufen sind
		{
			Set<Credentials> toBeChanged = this.passwordManagerController.getPasswordReminderController().passwordsToBeChanged();
			assertEquals("Filtering did not return all credentials", credentials, toBeChanged);
		}

		//Legt Set mit Passworteinträgen an, die angezeigt werden müssen, wenn vier Tage vergangen sind
		Set<Credentials> credentialsTestPartial = new HashSet<>();
		credentialsTestPartial.add(credentialsa);
		credentialsTestPartial.add(credentialsb);
		credentialsTestPartial.add(credentialsc);
		
		//ändert die Testliste so ab, dass die LastChanged-Daten vier Tage zurück liegen
		credentials.forEach(cred -> cred.setLastChanged(fourDaysEarlier));

		//prüft, ob die credentialsTestPartial-Liste von passwordsToBeChanged zurückgegeben wird
		assertEquals(credentialsTestPartial, this.passwordManagerController.getPasswordReminderController().passwordsToBeChanged());
	}

}
