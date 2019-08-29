package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy;
import de.sopra.passwordmanager.controller.UtilityController;
import org.junit.Before;
import org.junit.Test;

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
        /**
        EntryListOrderStrategy strategy = list -> {
            List<Credentials> collect = list.stream().map(CredentialsItem::getCredentials).sorted(Comparator.comparing(Credentials::getName)).collect(Collectors.toList());
            return collect.stream().map(CredentialsItem::new).collect(Collectors.toList());
        };

        EntryListOrderStrategy strategy2 = list -> {
            list.sort(Comparator.comparing(creds -> creds.getCredentials().getLastChanged()));
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

        chained.order(credentials.stream().map(CredentialsItem::new).collect(Collectors.toList()));

        Assert.assertEquals("ordering failed", credA, credentials.get(0));
        Assert.assertEquals("ordering failed", credB, credentials.get(1));
        Assert.assertEquals("ordering failed", credC, credentials.get(2));
         */
    }
}
