package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.BasePassword;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.PasswordManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class MasterPasswordControllerTest {
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
    public void changePasswordTest() {
    	
    	//ändert Masterpasswort
    	//assertion, wenn das alte Passwort weiterhin gespeichert ist
    	//assertion, wenn neues Passwort nicht gespeichert wurde
    	Date sixDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(6));
        this.passwordManager.setMasterPassword(new BasePassword("hello_there", 5, sixDaysEarlier));
        this.passwordManagerController.getMasterPasswordController().changePassword("hello",10);
        
        
        Assert.assertFalse("Passwort entspricht dem alten",this.passwordManagerController.getMasterPasswordController().checkPassword("hello_there"));
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().checkPassword("hello"));

    }

    @Test
    public void checkPasswordTest() {
    	//legt Masterpasswort an
    	//prüft, ob true ausgegeben wird, wenn dieses Passwort eingegeben wird
    	//prüft, ob false ausgegeben wird, wenn dieses Passwort nicht eingegeben wird
    	Date sixDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(6));
        this.passwordManager.setMasterPassword(new BasePassword("hello_there", 5, sixDaysEarlier));
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().checkPassword("hello_there"));
        
        Assert.assertFalse(this.passwordManagerController.getMasterPasswordController().checkPassword("hello_tree"));
    }

    @Test
    public void hasToBeChangedTest() {
        // Setze das Masterpasswort auf ein im Fünftagetakt zu Änderndes und teste, ob die Methode richtig erkennt, ob ein
        // dass ein vor sechs Tagen geändertes Passwort geändert werden muss
        Date sixDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(6));
        this.passwordManager.setMasterPassword(new BasePassword("hello_there", 5, sixDaysEarlier));
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());

        // Setze das Masterpasswort auf ein Passwort, welches nicht geändert werden muss, und teste, ob es als noch
        // gültiges Passwort erkannt werden muss
        Date fourDaysEarlier = new Date(System.currentTimeMillis() - daysToMillis(4));
        this.passwordManager.setMasterPassword(new BasePassword("hello_there", 7, fourDaysEarlier));
        Assert.assertFalse(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());

        // Setze das Masterpasswort auf ein Datum, welches gerade heute geändert werden muss.
        // Auch hier muss es geändert werden.
        Date halfYearEarlier = new Date(System.currentTimeMillis() - daysToMillis(182));
        this.passwordManager.setMasterPassword(new BasePassword("hello_there", 182, halfYearEarlier));
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());
    }
}