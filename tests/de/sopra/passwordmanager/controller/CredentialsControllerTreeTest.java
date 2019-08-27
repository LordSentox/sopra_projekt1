package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy.MainView;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.PasswordManager;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.util.PatternSyntax;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

public class CredentialsControllerTreeTest {
    private PasswordManagerController pmc;
    private CredentialsController cc;
    private PasswordManager pm;
    // Liste im MainView eine Liste mit einem einzigen Eintrag und füge diesen auch ins Datenmodell ein
    private MainView mainView;
    private Category rootCategory;


    private Credentials obar;
    private Credentials hbar;
    private Credentials fbar;
    private UtilityController uc;

    @Before
    public void setUp() throws Exception {
        pmc = PasswordManagerControllerDummy.getNewController();
        cc = pmc.getCredentialsController();
        pm = pmc.getPasswordManager();
        mainView = (MainView) this.pmc.getMainWindowAUI();
        rootCategory = pm.getRootCategory();
        uc = pmc.getUtilityController();

        // Oberkategorien an die root-Kategorie anhängen
        Category rootFoo = new Category("foo");
        Category rootBeer = new Category("beer");
        rootCategory.addSubCategory(rootFoo);
        rootCategory.addSubCategory(rootBeer);

        // An erste Oberkategorie zwei Unterkategorien anhängen
        Category fooFoo = new Category("foo");
        Category fooBeer = new Category("beer");
        rootFoo.addSubCategory(fooFoo);
        rootFoo.addSubCategory(fooBeer);

        // An zweite Oberkategorie eine Unterkategorie anhängen
        Category beerFoo = new Category("foo");
        rootBeer.addSubCategory(beerFoo);

        // Credentials zum Baum hinzufügen
        this.obar = new CredentialsBuilder("obar", "Hodor", "hoDor", "schmo.de")
                .withSecurityQuestion("Hey?", "Ja").build(uc);
        this.hbar = new CredentialsBuilder("hbar", "Hodor", "hoDor", "hodortmund.de").build(uc);
        this.fbar = new CredentialsBuilder("fbar", "Hodor", "hoDor", "zwergenfreun.de").build(uc);

        rootBeer.addCredentials(obar);
        fooBeer.addCredentials(hbar);
        fooBeer.addCredentials(fbar);
        beerFoo.addCredentials(fbar);
    }

    //region Filter
    @Test
    public void filterCredentialsTestNoFilter() {
        cc.filterCredentials(new PatternSyntax("")); //info: dont be null pls :)

        List<Credentials> list = mainView.getCurrentCredentialsList();
        Assert.assertNotNull("credentials list null after filtering", list);
        Assert.assertTrue("credentials list does not contain obar", list.contains(obar));
        Assert.assertTrue("credentials list does not contain hbar", list.contains(hbar));
        Assert.assertTrue("credentials list does not contain fbar", list.contains(fbar));
        Assert.assertEquals("credentials list does not contain exactly 3 elements", 3, list.size());
    }

    @Test
    public void filterCredentialsTestCategory() {
        //FIXME
//        cc.filterCredentials(new Path(Path.ROOT_CATEGORY + "/beer/foo"), null);
//
//        // Kategorie mit 1 Element
//        List<Credentials> list1 = mainView.getCurrentCredentialsList();
//        Assert.assertNotNull("credentials list null after filtering", list1);
//        Assert.assertFalse("credentials list contains obar", list1.contains(obar));
//        Assert.assertFalse("credentials list contains hbar", list1.contains(hbar));
//        Assert.assertTrue("credentials list does not contain fbar", list1.contains(fbar));
//        Assert.assertEquals("credentials list does not contain exactly 1 element after filtering for " + Path.ROOT_CATEGORY + "/beer/foo", 1, list1.size());
//
//        // Kategorie mit 2 Elementen
//        cc.filterCredentials(new Path(Path.ROOT_CATEGORY + "/foo/beer"), null);
//        List<Credentials> list2 = mainView.getCurrentCredentialsList();
//        Assert.assertNotNull("credentials list null after filtering", list2);
//        Assert.assertFalse("credentials list contains obar", list2.contains(obar));
//        Assert.assertTrue("credentials list does not contain hbar", list2.contains(hbar));
//        Assert.assertTrue("credentials list does not contain fbar", list2.contains(fbar));
//        Assert.assertEquals("credentials list does not contain exactly 2 elements after filtering for " + Path.ROOT_CATEGORY + "/foo/beer", 2, list2.size());
//
//        // Leere kategorie
//        cc.filterCredentials(new Path(Path.ROOT_CATEGORY + "/foo/foo"), null);
//        List<Credentials> list3 = mainView.getCurrentCredentialsList();
//        Assert.assertNotNull("credentials list null after filtering", list3);
//        Assert.assertFalse("credentials list contains obar", list3.contains(obar));
//        Assert.assertFalse("credentials list contains hbar", list3.contains(hbar));
//        Assert.assertFalse("credentials list contains fbar", list3.contains(fbar));
//        Assert.assertEquals("credentials list contains elements after filtering for " + Path.ROOT_CATEGORY + "/foo/foo, which is empty", 0, list3.size());
//
//        //Alle Elemente aus Kategorie und unterkategorien
//        cc.filterCredentials(new Path(Path.ROOT_CATEGORY + "/beer"), null);
//        List<Credentials> list4 = mainView.getCurrentCredentialsList();
//        Assert.assertNotNull("credentials list null after filtering", list4);
//        Assert.assertTrue("credentials list does not contain obar", list4.contains(obar));
//        Assert.assertFalse("credentials list contains hbar", list4.contains(hbar));
//        Assert.assertTrue("credentials list does not contain fbar", list4.contains(fbar));
//        Assert.assertEquals("credentials list does not contain exactly 2 elements after filtering for " + Path.ROOT_CATEGORY + "/beer", 2, list4.size());
//
//        // Nicht existente Kategorie
//        cc.filterCredentials(new Path(Path.ROOT_CATEGORY + "/beb"), null);
//        List<Credentials> list5 = mainView.getCurrentCredentialsList();
//        Assert.assertNotNull("credentials list null after filtering", list5);
//        Assert.assertFalse("credentials list contains obar", list5.contains(obar));
//        Assert.assertFalse("credentials list contains hbar", list5.contains(hbar));
//        Assert.assertFalse("credentials list contains fbar", list5.contains(fbar));
//        Assert.assertEquals("credentials list contains elements after filtering for " + Path.ROOT_CATEGORY + "/beb, which does not exist", 0, list5.size());
    }

    @Test
    public void filterCredentialsTestPattern() {
        cc.filterCredentials(new PatternSyntax("bar"));
        List<Credentials> list1 = mainView.getCurrentCredentialsList();
        Assert.assertNotNull("credentials list null after filtering", list1);
        Assert.assertTrue("credentials list does not contain obar", list1.contains(obar));
        Assert.assertTrue("credentials list does not contain hbar", list1.contains(hbar));
        Assert.assertTrue("credentials list does not contain fbar", list1.contains(fbar));
        Assert.assertEquals("credentials list does not contain exactly 3 elements after filtering for 'bar'", 3, list1.size());

        cc.filterCredentials(new PatternSyntax("f"));
        List<Credentials> list2 = mainView.getCurrentCredentialsList();
        Assert.assertNotNull("credentials list null after filtering", list2);
        Assert.assertFalse("credentials list contains obar", list2.contains(obar));
        Assert.assertFalse("credentials list contains hbar", list2.contains(hbar));
        Assert.assertTrue("credentials list does not contain fbar", list2.contains(fbar));
        Assert.assertEquals("credentials list does not contain exactly 1 element after filtering for 'f'", 1, list2.size());
    }

    @Test
    public void filterCredentialsTestAll() {
        //FIXME
//        cc.filterCredentials(new Path(Path.ROOT_CATEGORY + "/beer"), "fbar");
//        List<Credentials> list1 = mainView.getCurrentCredentialsList();
//        Assert.assertNotNull("credentials list null after filtering", list1);
//        Assert.assertFalse("credentials list contains obar", list1.contains(obar));
//        Assert.assertFalse("credentials list contains hbar", list1.contains(hbar));
//        Assert.assertTrue("credentials list does not contain fbar", list1.contains(fbar));
//        Assert.assertEquals("credentials list does not contain exactly 1 element after filtering for category " +  Path.ROOT_CATEGORY + "/beer, and pattern 'fbar'", 1, list1.size());
//
//        cc.filterCredentials(new Path(Path.ROOT_CATEGORY + "/foo/beer"), "o");
//        List<Credentials> list2 = mainView.getCurrentCredentialsList();
//        Assert.assertNotNull("credentials list null after filtering", list2);
//        Assert.assertFalse("credentials list contains obar", list2.contains(obar));
//        Assert.assertFalse("credentials list contains hbar", list2.contains(hbar));
//        Assert.assertFalse("credentials list contains fbar", list2.contains(fbar));
//        Assert.assertEquals("credentials list contains elements after filtering for category " +  Path.ROOT_CATEGORY + "/foo/beer, and pattern 'o'", 0, list2.size());
//
//        cc.filterCredentials(new Path(Path.ROOT_CATEGORY + "/beb"), "f");
//        List<Credentials> list3 = mainView.getCurrentCredentialsList();
//        Assert.assertNotNull("credentials list null after filtering", list3);
//        Assert.assertFalse("credentials list contains obar", list3.contains(obar));
//        Assert.assertFalse("credentials list contains hbar", list3.contains(hbar));
//        Assert.assertFalse("credentials list contains fbar", list3.contains(fbar));
//        Assert.assertEquals("credentials list contains elements after filtering for category " +  Path.ROOT_CATEGORY + "/beb, and pattern 'f' (/beb does not exist)", 0, list3.size());
    }
    //endregion

    @Test
    public void getCredentialsByCategoryPathTest() {
        // Root Category
        Collection<Credentials> creds1 = cc.getCredentialsByCategoryPath(new Path(Path.ROOT_CATEGORY));
        Assert.assertTrue("credentials list does not contain obar", creds1.contains(obar));
        Assert.assertTrue("credentials list does not contain hbar", creds1.contains(hbar));
        Assert.assertTrue("credentials list does not contain fbar", creds1.contains(fbar));
        Assert.assertEquals("credentials list does not contain exactly 3 elements after filtering for 'bar'", 3, creds1.size());

        // Category "alle/beer" mit Eintrag "obar" und Category "alle/beer/foo" mit Eintrag
        Collection<Credentials> creds2 = cc.getCredentialsByCategoryPath(new Path(Path.ROOT_CATEGORY + "/beer"));
        Assert.assertTrue("credentials list does not contain obar", creds2.contains(obar));
        Assert.assertFalse("credentials list contains hbar", creds2.contains(hbar));
        Assert.assertTrue("credentials list does not contain fbar", creds2.contains(fbar));
        Assert.assertEquals("credentials list does not contain exactly 2 elements after filtering for 'bar'", 2, creds2.size());
    }

    @Test
    public void getCredentialsByCategoryPathTestNonExistantCategory() {
        // Kategorie, die nicht existiert
        Collection<Credentials> creds = cc.getCredentialsByCategoryPath(new Path("lol"));
        Assert.assertTrue("collection non empty despite category not existing", creds.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void getCredentialsByCategoryPathTestPathNull() {
        // Kategorie, die nicht existiert
        //XXX entfernen?
        Collection<Credentials> creds = cc.getCredentialsByCategoryPath(null);
    }

    @Test
    public void reencryptAllTest() {
        String masterPassword = pm.getMasterPassword();

        cc.reencryptAll(masterPassword, "Hallo");
        pm.setMasterPassword("Hallo");
        Category rootBeer = pmc.getPasswordManager().getRootCategory().getCategoryByPath(Path.ROOT_CATEGORY_PATH.createChildPath("beer"));
        Credentials obar = rootBeer.getCredentials().stream().findFirst().get();
        SecurityQuestion question = obar.getSecurityQuestions().stream().findFirst().get();
        Assert.assertEquals("reencrypting password failed", "hoDor", uc.decryptText(obar.getPassword()));
        Assert.assertEquals("reencrypting security question failed", "Hey?", uc.decryptText(question.getQuestion()));
        Assert.assertEquals("reencrypting security question failed", "Ja", uc.decryptText(question.getAnswer()));
    }
}
