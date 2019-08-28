package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.PasswordManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class MasterPasswordControllerTest {
    private PasswordManagerController passwordManagerController;
    private PasswordManager passwordManager;
    private MasterPasswordController masterPasswordController;

    @Before
    public void setUp() throws Exception {
        this.passwordManagerController = PasswordManagerControllerDummy.getNewController();
        this.passwordManager = this.passwordManagerController.getPasswordManager();
        this.masterPasswordController = this.passwordManagerController.getMasterPasswordController();
    }

    @Test
    public void changePasswordTest() {
        //ändert Masterpasswort
        //assertion, wenn das alte Passwort weiterhin gespeichert ist
        //assertion, wenn neues Passwort nicht gespeichert wurde
        LocalDateTime sixDaysEarlier = LocalDateTime.now().minus(6, ChronoUnit.DAYS);
        String password = "hello_there";
        this.passwordManager.setMasterPassword(password);
        this.passwordManager.setMasterPasswordReminderDays(5);
        this.passwordManager.setMasterPasswordLastChanged(sixDaysEarlier);
        this.passwordManagerController.getMasterPasswordController().changePassword("hello", 10);


        Assert.assertFalse("Passwort entspricht dem alten", this.passwordManagerController.getMasterPasswordController().checkPassword(password));
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().checkPassword("hello"));

    }

    @Test (expected = NullPointerException.class)
    public void checkQualityTestNull() {
        this.masterPasswordController.checkQuality(null);
    }

    @Test
    public void checkQualityTest() {
        List<String> passwords = Arrays.asList("123", "", "Hello, there", "142aB5][9p5assw15ort!5xD", "aA53]@`");

        for (String password: passwords) {
            // TODO: Sollten hier feste Werte angegeben werden, statt einen Aufruf auf checkQuality zu tätigen?
            int passwordQuality = this.passwordManagerController.getUtilityController().checkQuality(password, "passwort");

            this.masterPasswordController.checkQuality(password);

            PasswordManagerControllerDummy.MasterPasswordView masterPasswordView =
                    (PasswordManagerControllerDummy.MasterPasswordView) this.passwordManagerController.getMasterPasswordViewAUI();
            Assert.assertEquals(passwordQuality, masterPasswordView.getCurrentQuality());
        }
    }

    @Test
    public void checkPasswordTest() {
        //legt Masterpasswort an
        //prüft, ob true ausgegeben wird, wenn dieses Passwort eingegeben wird
        //prüft, ob false ausgegeben wird, wenn dieses Passwort nicht eingegeben wird
        LocalDateTime sixDaysEarlier = LocalDateTime.now().minus(6, ChronoUnit.DAYS);
        String password = "hello_there";
        this.passwordManager.setMasterPassword(password);
        this.passwordManager.setMasterPasswordReminderDays(5);
        this.passwordManager.setMasterPasswordLastChanged(sixDaysEarlier);
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().checkPassword(password));

        Assert.assertFalse(this.passwordManagerController.getMasterPasswordController().checkPassword("hello_tree"));
    }

    @Test
    public void hasToBeChangedTest() {
        // Setze das Masterpasswort auf ein im Fünftagetakt zu Änderndes und teste, ob die Methode richtig erkennt, ob ein
        // dass ein vor sechs Tagen geändertes Passwort geändert werden muss
        LocalDateTime sixDaysEarlier = LocalDateTime.now().minus(6, ChronoUnit.DAYS);
        String password = "hello_there";
        this.passwordManager.setMasterPassword(password);
        this.passwordManager.setMasterPasswordReminderDays(5);
        this.passwordManager.setMasterPasswordLastChanged(sixDaysEarlier);
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());

        // Setze das Masterpasswort auf ein Passwort, welches nicht geändert werden muss, und teste, ob es als noch
        // gültiges Passwort erkannt werden muss
        LocalDateTime fourDaysEarlier = LocalDateTime.now().minus(4, ChronoUnit.DAYS);
        this.passwordManager.setMasterPassword(password);
        this.passwordManager.setMasterPasswordReminderDays(7);
        this.passwordManager.setMasterPasswordLastChanged(fourDaysEarlier);
        Assert.assertFalse(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());

        // Setze das Masterpasswort auf ein Datum, welches gerade heute geändert werden muss.
        // Auch hier muss es geändert werden.
        LocalDateTime halfYearEarlier = LocalDateTime.now().minus(182, ChronoUnit.DAYS);
        this.passwordManager.setMasterPassword(password);
        this.passwordManager.setMasterPasswordReminderDays(182);
        this.passwordManager.setMasterPasswordLastChanged(halfYearEarlier);
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());
    }
}