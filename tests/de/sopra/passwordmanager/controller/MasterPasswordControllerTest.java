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
    PasswordManagerController passwordManagerController;
    PasswordManager passwordManager;

    @Before
    public void setUp() throws Exception {
        this.passwordManagerController = PasswordManagerControllerDummy.getNewController();
        this.passwordManager = this.passwordManagerController.getPasswordManager();
    }

    @Test
    public void changePasswordTest() {

    }

    @Test
    public void checkPasswordTest() {
    }

    @Test
    public void hasToBeChangedTest() {
        // Setze das Masterpasswort auf ein im Fünftagetakt zu Änderndes und teste, ob die Methode richtig erkennt, ob ein
        // dass ein vor sechs Tagen geändertes Passwort geändert werden muss
        Date sixDaysEarlier = new Date(System.currentTimeMillis() - 3600*24*6*1000);
        this.passwordManager.setMasterPassword(new BasePassword("hello_there", 5, sixDaysEarlier));
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());

        // Setze das Masterpasswort auf ein Passwort, welches nicht geändert werden muss, und teste, ob es als noch
        // gültiges Passwort erkannt werden muss
        sixDaysEarlier = new Date(System.currentTimeMillis() - 3600*24*6*1000);
        this.passwordManager.setMasterPassword(new BasePassword("hello_there", 7, sixDaysEarlier));
        Assert.assertFalse(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());

        // Setze das Masterpasswort auf ein Datum, welches gerade heute geändert werden muss.
        // Auch hier muss es geändert werden.
        Date halfYearEarlier = new Date(System.currentTimeMillis() - 3600*24*182*1000);
        this.passwordManager.setMasterPassword(new BasePassword("hello_there", 182, halfYearEarlier));
        Assert.assertTrue(this.passwordManagerController.getMasterPasswordController().hasToBeChanged());
    }
}