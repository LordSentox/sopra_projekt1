package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy;
import de.sopra.passwordmanager.controller.UtilityController;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.strategy.EntryListOrderStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EntryListOrderStrategyTest {


    private PasswordManagerController pmc;
    private UtilityController uc;

    @Before
    public void setUp() {
        pmc = PasswordManagerControllerDummy.getNewController();
        uc = pmc.getUtilityController();
    }

    @Test
    public void orderTest() {
        EntryListOrderStrategy strategy = list -> {
            list.sort(Comparator.comparing(Credentials::getName));
            return list;
        };

        EntryListOrderStrategy strategy2 = list -> {
            list.sort(Comparator.comparing(Credentials::getLastChanged));
            return list;
        };

        EntryListOrderStrategy chained = strategy2.nextOrder(strategy);

        List<Credentials> credentials = new ArrayList<>();
        Credentials credA = new CredentialsBuilder("a", "fhjl", "eqwe", "gfd.de").build(uc);
        Credentials credB = new CredentialsBuilder("b", "sus", "aa", "dad.de").build(uc);
        Credentials credC = new CredentialsBuilder("c", "sass", "dasd", "dfadsf.de").build(uc);
        credentials.add(credB);
        credentials.add(credC);
        credentials.add(credA);

        chained.order(credentials);

        Assert.assertEquals("ordering failed", credA, credentials.get(0));
        Assert.assertEquals("ordering failed", credB, credentials.get(1));
        Assert.assertEquals("ordering failed", credC, credentials.get(2));
    }
}
