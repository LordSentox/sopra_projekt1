package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class CredentialsControllerTest {

    private PasswordManagerController pmc;
    private CredentialsController cc;
    private PasswordManager pm;

    @Before
    public void setUp() throws Exception {
        pmc = PasswordManagerControllerDummy.getNewController();
        cc = pmc.getCredentialsController();
        pm = pmc.getPasswordManager();
    }

    @Test
    public void saveCredentialsBasic() {
        Credentials cred = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();

        cc.saveCredentials(null, cred);
        Assert.assertTrue(pmc.getPasswordManager().getRootCategory().getCredentials().contains(cred));
    }

    @Test
    public void replaceCredentialsBasic() {
        Credentials cred1 = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();

        Credentials cred2 = new CredentialsBuilder()
                .withName("cred2")
                .withUserName("user2")
                .withPassword("456passwort")
                .withWebsite("www.hi.com")
                .build();

        cc.saveCredentials(null, cred1);
        Assert.assertTrue(pm.getRootCategory().getCredentials().contains(cred1));
        cc.saveCredentials(cred1, cred2);

        //Nach dem ersetzen darf nur 1 Eintrag im Passwortmanager existieren und dieser muss mit cred2 übereinstimmen
        Assert.assertEquals(1, pm.getRootCategory().getCredentials().size());
        Assert.assertTrue(pm.getRootCategory().getCredentials().contains(cred2));
    }

    @Test
    public void replaceCredentialsSameObject() {
        Credentials cred = new CredentialsBuilder()
                .withName("cred")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();
        cc.saveCredentials(null, cred);
        Assert.assertTrue(pm.getRootCategory().getCredentials().contains(cred));
        cc.saveCredentials(cred, cred);

        //Nach dem ersetzen darf nur 1 Eintrag im Passwortmanager existieren und dieser muss mit cred übereinstimmen
        Assert.assertEquals(1, pm.getRootCategory().getCredentials().size());
        Assert.assertTrue(pm.getRootCategory().getCredentials().contains(cred));
    }

    @Test
    public void saveCredentialsNull() {
        Credentials cred = new CredentialsBuilder()
                .withName("cred1")
                .withUserName("user1")
                .withPassword("passwort123")
                .withWebsite("www.hallo.de")
                .build();

        cc.saveCredentials(cred, null);
        Assert.assertEquals(0, pm.getRootCategory().getCredentials().size());
        cc.saveCredentials(null, null);
        Assert.assertEquals(0, pm.getRootCategory().getCredentials().size());
    }

    @Test
    public void removeCredentials() {

    }

    @Test
    public void addSecurityQuestion() {
    }

    @Test
    public void removeSecurityQuestion() {
    }

    @Test
    public void filterCredentials() {
    }

    @Test
    public void copyPasswordToClipboard() {
    }

    @Test
    public void setPasswordShown() {
    }

    @Test
    public void getCredentialsByCategoryName() {
    }

    @Test
    public void clearPasswordFromClipboard() {
    }

    @Test
    public void reencryptAll() {
    }
}