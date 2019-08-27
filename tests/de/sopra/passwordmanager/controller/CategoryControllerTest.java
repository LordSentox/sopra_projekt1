package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy.MainView;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Path;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class CategoryControllerTest {
    private CategoryController catController;
    private PasswordManagerController pmc;
    private Category root;
    private MainView mv;
    private UtilityController uc;

    @Before
    public void setUp() throws Exception {
        pmc = PasswordManagerControllerDummy.getNewController();
        mv = ((MainView) pmc.getMainWindowAUI());
        catController = pmc.getCategoryController();
        uc = pmc.getUtilityController();
    }

    @Test
    public void createCategory() {
        root = pmc.getPasswordManager().getRootCategory();


        //valide Kategorieerstellung
        //neue Oberkategorie
        catController.createCategory(root, "category without children and credentials");
        Collection<Category> subCategories = root.getSubCategories();

        assertTrue("Kategorie wurde nicht an Wurzel angehängt",
                subCategories.contains(catController.getCategory(Path.ROOT_CATEGORY_PATH.createChildPath("category without children and credentials"))));

        Category categoryWithoutCredentials = new Category("categoryWithoutCredentials");
        Category categoryWithoutChildren = new Category("categoryWithoutChildren");

        root.addSubCategory(categoryWithoutCredentials);
        categoryWithoutCredentials.addSubCategory(categoryWithoutChildren);

        //neue Unterkategorie
        catController.createCategory(categoryWithoutChildren, "childCategory");
        subCategories = categoryWithoutChildren.getSubCategories();

        assertTrue("Kategorie wurde nicht an Wurzel angehängt",
                subCategories.contains(catController.getCategory(Path.ROOT_CATEGORY_PATH.createChildPath("categoryWithoutCredentials/categoryWithoutChildren/childCategory"))));


        //nicht valide Kategorieerstellung
        //kein Name eingegeben
        catController.createCategory(root, "");

        Collection<String> errors = mv.getErrorsShown();
        assertTrue("Fehler hätte aufgerufen werden müssen", errors.contains("Eingegebener Kategoriename darf nicht leer sein"));

        //Kategorie soll gleichen Namen haben, wie schon existierendes Kind
        catController.createCategory(root, "category without children and credentials");

        errors = mv.getErrorsShown();
        assertTrue("Fehler hätte aufgerufen werden müssen", errors.contains("Eingegebener Kategoriename ist schon vergeben"));
    }

    @Test
    public void removeCategory() {
        root = pmc.getPasswordManager().getRootCategory();
        Category childCategoryWithContent = new Category("childCategoryWithContent");
        Category childCategoryWithSubCategories = new Category("childCategoryWithSubCategories");
        Category childCategoryWithoutContent = new Category("childCategoryWithoutContent");
        Category childCategoryWithCredentialsAndSubCat = new Category("childCategoryWithCredentialsAndSubCat");
        Category emptyChildCategoryDontDelete = new Category("emptyChildCategoryDontDelete");
        Category emptyChildCategoryDoDelete = new Category("emptyChildCategoryDoDelete");

        Credentials credentialsDummyNotInDeletedSubCategory = new CredentialsBuilder("credentialsDummyNotInDeletedSubCategory", "credentialsDummyNotInDeletedSubCategoryUser", "PWcredentialsDummyNotInDeletedSubCategory", "website").build(uc);
        Credentials credentialsDummyDoDelete = new CredentialsBuilder("DoDelete", "credentialsDummyDoDeleteUser", "PWcredentialsDummyDoDelete", "website").build(uc);
        Credentials credentialsDummyDoDelete2 = new CredentialsBuilder("DoDelete2", "credentialsDummyDoDeleteUser2", "PWcredentialsDummyDoDelete2", "website").build(uc);
        Credentials credentialsDummyDontDelete = new CredentialsBuilder("DontDelete", "credentialsDummyDontDeleteUser", "PWcredentialsDummyDontDelete", "website").build(uc);

        root.addSubCategory(childCategoryWithoutContent);
        root.addSubCategory(childCategoryWithSubCategories);

        childCategoryWithSubCategories.addSubCategory(childCategoryWithContent);
        childCategoryWithSubCategories.addSubCategory(childCategoryWithCredentialsAndSubCat);

        childCategoryWithCredentialsAndSubCat.addCredentials(credentialsDummyDontDelete);
        childCategoryWithCredentialsAndSubCat.addSubCategory(emptyChildCategoryDontDelete);

        childCategoryWithSubCategories.addCredentials(credentialsDummyNotInDeletedSubCategory);


        Collection<Credentials> credentialsToCheck;
        Collection<Category> catsToCheck;

        //valides Löschen
        //nur Kategorie, kein Inhalt löschen, aber Inhalt vorhanden
        catController.removeCategory(Path.ROOT_CATEGORY_PATH.createChildPath("childCategoryWithSubCategories/childCategoryWithCredentialsAndSubCat"), false);
        credentialsToCheck = childCategoryWithSubCategories.getCredentials();
        catsToCheck = childCategoryWithSubCategories.getSubCategories();

        assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyDontDelete));
        assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyNotInDeletedSubCategory));
        assertTrue("Kategorie fälschlicherweise entfernt", catsToCheck.contains(emptyChildCategoryDontDelete));
        assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithCredentialsAndSubCat));


        //nur Kategorie, kein Inhalt löschen, aber kein Inhalt vorhanden
        catController.removeCategory(Path.ROOT_CATEGORY_PATH.createChildPath("childCategoryWithoutContent"), false);
        credentialsToCheck = root.getCredentials();
        catsToCheck = root.getSubCategories();

        assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithoutContent));


        root.getSubCategories().add(childCategoryWithoutContent);
        childCategoryWithSubCategories.addSubCategory(childCategoryWithCredentialsAndSubCat);
        childCategoryWithoutContent.getSubCategories().remove(emptyChildCategoryDontDelete);

        childCategoryWithCredentialsAndSubCat.addSubCategory(emptyChildCategoryDoDelete);
        childCategoryWithCredentialsAndSubCat.addCredentials(credentialsDummyDoDelete);
        childCategoryWithCredentialsAndSubCat.addCredentials(credentialsDummyDoDelete2);

        //Kategorie und gesamten Inhalt löschen, Inhalt vorhanden
        catController.removeCategory(Path.ROOT_CATEGORY_PATH.createChildPath("childCategoryWithSubCategories/childCategoryWithCredentialsAndSubCat"), true);
        credentialsToCheck = childCategoryWithSubCategories.getCredentials();
        catsToCheck = childCategoryWithContent.getSubCategories();

        assertFalse("Credentials wurden fälschlicherweise nicht entfernt", credentialsToCheck.contains(credentialsDummyDoDelete));
        assertFalse("Credentials wurden fälschlicherweise nicht entfernt", credentialsToCheck.contains(credentialsDummyDoDelete2));
        assertFalse("Kategorie fälschlicherweise nicht entfernt", credentialsToCheck.contains(emptyChildCategoryDoDelete));
        assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyNotInDeletedSubCategory));
        assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithCredentialsAndSubCat));


        //Kategorie und gesamten Inhalt löschen, kein Inhalt vorhanden
        catController.removeCategory(Path.ROOT_CATEGORY_PATH.createChildPath("childCategoryWithoutContent"), true);
        credentialsToCheck = root.getCredentials();
        catsToCheck = root.getSubCategories();

        assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithoutContent));

    }

    @Test(expected = NullPointerException.class)
    public void moveCategory() {
        root = pmc.getPasswordManager().getRootCategory();
        Category moveTo = new Category("moveTo");
        Category moveFrom = new Category("moveFrom");
        Category toMoveRenameEmpty = new Category("toMoveRenameEmpty");
        Category toMoveRenameContent = new Category("toMoveRenameContent");
        Category subToMove = new Category("subToMove");
        Category dontMove = new Category("dontMove");

        Credentials credentialsDontMove = new CredentialsBuilder("credentialsDontMove", "credentialsDontMoveUser", "PWcredentialsDontMove", "website").build(uc);
        Credentials credentialsToMove = new CredentialsBuilder("credentialsToMove", "credentialsToMoveUser", "PWcredentialsToMove", "website").build(uc);

        root.addSubCategory(moveFrom);
        root.addSubCategory(moveTo);
        moveFrom.addSubCategory(toMoveRenameContent);
        moveFrom.addSubCategory(toMoveRenameEmpty);
        moveFrom.addSubCategory(dontMove);
        toMoveRenameContent.addSubCategory(subToMove);
        toMoveRenameContent.addCredentials(credentialsToMove);
        moveFrom.addCredentials(credentialsDontMove);
        dontMove.addCredentials(credentialsDontMove);


        //valides Move
        //tatsächlich verschieben, kein umbenennen
        catController.moveCategory(Path.ROOT_CATEGORY_PATH.createChildPath("moveFrom/toMoveRenameContent"), Path.ROOT_CATEGORY_PATH.createChildPath("moveTo/toMoveRenameContent"));
        assertTrue("Kategorie hätte hierhin verschoben werden sollen", moveTo.hasSubCategory(toMoveRenameContent.getName()));
        assertTrue("Kategorie sollte nach Verschieben die Unterkategorie enthalten", toMoveRenameContent.hasSubCategory(subToMove.getName()));
        assertTrue("Kategorie sollte nach Verschieben die Credentials enthalten", toMoveRenameContent.getCredentials().contains(credentialsToMove));
        assertTrue("Kategorie hätte nicht verschoben werden sollen", moveFrom.hasSubCategory(dontMove.getName()));
        assertTrue("Credentials hätte nicht verschoben werden sollen", moveFrom.getCredentials().contains(credentialsDontMove));
        assertFalse("Kategorie hätte verschoben werden sollen", moveFrom.hasSubCategory(toMoveRenameContent.getName()));

        //nur umbenennen
        catController.moveCategory(Path.ROOT_CATEGORY_PATH.createChildPath("moveTo/toMoveRenameContent"), Path.ROOT_CATEGORY_PATH.createChildPath("moveTo/toMoveContentRenamed"));
        assertTrue("Kategorie hätte hierhin verschoben werden sollen", moveTo.hasSubCategory("toMoveContentRenamed"));
        assertEquals("Kategorie falsch umbenannt", "toMoveContentRenamed", moveTo.getCategoryByPath(new Path("moveTo/toMoveContentRenamed")).getName());
        assertTrue("Kategorie sollte nach Verschieben die Unterkategorie enthalten", toMoveRenameContent.getSubCategories().contains(subToMove));
        assertTrue("Kategorie sollte nach Verschieben die Credentials enthalten", toMoveRenameContent.getCredentials().contains(credentialsToMove));

        //verschieben und umbenennen
        catController.moveCategory(Path.ROOT_CATEGORY_PATH.createChildPath("moveTo/toMoveContentRenamed"), Path.ROOT_CATEGORY_PATH.createChildPath("moveFrom/toMoveRenameContent"));
        assertTrue("Kategorie hätte hierhin verschoben werden sollen", moveFrom.hasSubCategory(toMoveRenameContent.getName()));
        assertEquals("toMoveRenameContent", toMoveRenameContent.getName());
        assertTrue("Kategorie sollte nach Verschieben die Unterkategorie enthalten", toMoveRenameContent.getSubCategories().contains(subToMove));
        assertTrue("Kategorie sollte nach Verschieben die Credentials enthalten", toMoveRenameContent.getCredentials().contains(credentialsToMove));
        assertTrue("Kategorie hätte nicht verschoben werden sollen", moveFrom.getSubCategories().contains(dontMove));
        assertTrue("Credentials hätte nicht verschoben werden sollen", moveFrom.getCredentials().contains(credentialsDontMove));
        assertFalse("Kategorie hätte verschoben werden sollen", moveTo.getSubCategories().contains(toMoveRenameContent));


        //nicht valides move
        //einer der Pfade ist null --> Exception wurde geworfen
        catController.moveCategory(null, Path.ROOT_CATEGORY_PATH.createChildPath("moveFrom/toMoveRenameContent"));
    }

    //---------------------------------
    @Test(expected = NullPointerException.class)
    public void getCategory() {
        // Erstellen eines Testbaumes
        this.root = pmc.getPasswordManager().getRootCategory();
        Category regret = new Category("regret");
        Category life = new Category("is life");
        regret.addSubCategory(life);
        root.addSubCategory(new Category("re"));
        root.addSubCategory(new Category("gret"));
        root.addSubCategory(regret);

        // Können hinzugefügte Kategorien bekommen werden?
        Assert.assertEquals(root, this.catController.getCategory(new Path(Path.ROOT_CATEGORY)));
        Assert.assertEquals(regret, this.catController.getCategory(new Path(Path.ROOT_CATEGORY + "/regret")));
        Assert.assertEquals(life, this.catController.getCategory(new Path(Path.ROOT_CATEGORY + "/regret/is life")));

        // Nicht existierende Kategorien bzw. Pfade und null sollen null zurückgeben
        Assert.assertEquals(root, this.catController.getCategory(new Path("")));
        Assert.assertEquals(root, this.catController.getCategory(null));
        Assert.assertNull(this.catController.getCategory(new Path(Path.ROOT_CATEGORY + "/is life/regret")));
        Assert.assertNull(this.catController.getCategory(new Path("regret")));
    }

    @Test
    public void findCategory() {
        // Erstellen eines Testbaumes
        this.root = pmc.getPasswordManager().getRootCategory();
        Category hello = new Category("hello");
        Category there = new Category("there");
        Category helloHello = new Category("hello");

        this.root.addSubCategory(there);
        this.root.addSubCategory(hello);
        hello.addSubCategory(helloHello);

        // Finde eine oder mehrere Kategorien wie angegeben
        Assert.assertEquals(Collections.singletonList(there), catController.findCategory("there"));
        List<Category> hellos = this.catController.findCategory("hello");
        Assert.assertEquals(2, hellos.size());
        Assert.assertTrue(hellos.contains(hello));
        Assert.assertTrue(hellos.contains(helloHello));
        // Wird auch die root-Kategorie gefunden?
        Assert.assertEquals(Collections.singletonList(this.root), this.catController.findCategory(Path.ROOT_CATEGORY));

        // Null oder falsche Kategorie soll nicht gefunden werden
        Assert.assertEquals(0, this.catController.findCategory(null).size());
        Assert.assertEquals(0, this.catController.findCategory("").size());
        Assert.assertEquals(0, this.catController.findCategory("変な名").size());
        Assert.assertEquals(0, this.catController.findCategory(Path.ROOT_CATEGORY + "/hello").size());
    }

    @Test
    public void addCredentialsToCategories() {
        // Erstellen eines Testbaumes
        this.root = pmc.getPasswordManager().getRootCategory();
        Category one = new Category("one");
        Category two = new Category("two");
        Category three = new Category("three");

        this.root.addSubCategory(one);
        one.addSubCategory(two);
        this.root.addSubCategory(three);

        // Die hinzuzufügenden Credentials-Objekte
        Credentials credentials = new CredentialsBuilder("name", "legend27", "IAMGOD", "xd.net").build(uc);
        Credentials credentials2 = new CredentialsBuilder("Geht niemand an was", "Hugo", "Hugo", "dx.xd").build(uc);

        // Teste das hinzufügen zu einer und zu mehreren Kategorien
        this.catController.addCredentialsToCategories(credentials, Collections.singletonList(one));
        Assert.assertFalse(this.root.getCredentials().contains(credentials));
        Assert.assertTrue(one.getCredentials().contains(credentials));
        Assert.assertFalse(two.getCredentials().contains(credentials));
        Assert.assertFalse(three.getCredentials().contains(credentials));

        this.catController.addCredentialsToCategories(credentials2, Arrays.asList(two, three));
        Assert.assertFalse(this.root.getCredentials().contains(credentials2));
        Assert.assertTrue(one.getCredentials().contains(credentials));
        Assert.assertFalse(one.getCredentials().contains(credentials2));
        Assert.assertTrue(two.getCredentials().contains(credentials2));
        Assert.assertTrue(three.getCredentials().contains(credentials2));
    }

    @Test
    public void getPathForCategoryTest() {
        this.root = pmc.getPasswordManager().getRootCategory();
        Category one = new Category("one");
        Category two = new Category("two");
        Category three = new Category("three");

        this.root.addSubCategory(one);
        one.addSubCategory(two);
        two.addSubCategory(three);
        assertEquals(root.getCategoryByPath(catController.getPathForCategory(three)), three);
    }

    @Test
    public void removeCredentialsFromCategories() {
        // Erstellen eines Testbaumes
        this.root = pmc.getPasswordManager().getRootCategory();
        Category one = new Category("one");
        Category two = new Category("two");
        Category three = new Category("three");

        this.root.addSubCategory(one);
        one.addSubCategory(two);
        this.root.addSubCategory(three);

        // Die zu löschenden Credentials
        Credentials credentials = new CredentialsBuilder("name", "legend27", "IAMGOD", "xd.net").build(uc);
        Credentials credentials2 = new CredentialsBuilder("Geht niemand an was", "Hugo", "Hugo", "dx.xd").build(uc);

        one.addCredentials(credentials);
        one.addCredentials(credentials2);
        two.addCredentials(credentials2);

        catController.removeCredentialsFromCategories(credentials);

        // Stelle sicher, dass credentials2 noch im Baum vorhanden ist, aber credentials nicht mehr
        Assert.assertFalse(one.getCredentials().contains(credentials));
        Assert.assertTrue(one.getCredentials().contains(credentials2));
        Assert.assertFalse(two.getCredentials().contains(credentials));
        Assert.assertTrue(two.getCredentials().contains(credentials2));
        Assert.assertFalse(three.getCredentials().contains(credentials));
        Assert.assertFalse(three.getCredentials().contains(credentials2));

        catController.removeCredentialsFromCategories(credentials2);

        // Keine Credentials dürfen mehr im Baum sein
        Assert.assertFalse(one.getCredentials().contains(credentials));
        Assert.assertFalse(one.getCredentials().contains(credentials2));
        Assert.assertFalse(two.getCredentials().contains(credentials));
        Assert.assertFalse(two.getCredentials().contains(credentials2));
        Assert.assertFalse(three.getCredentials().contains(credentials));
        Assert.assertFalse(three.getCredentials().contains(credentials2));
    }
}