package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy;
import de.sopra.passwordmanager.controller.UtilityController;
import org.junit.Before;

public class EntryListOrderStrategyTest {


    private PasswordManagerController pmc;
    private UtilityController uc;

    @Before
    public void setUp() {
        pmc = PasswordManagerControllerDummy.getNewController();
        uc = pmc.getUtilityController();
    }

}
