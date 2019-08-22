package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.controller.PasswordManagerControllerDummy.MainView;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.Path;
import de.sopra.passwordmanager.view.MainWindowAUI;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static junit.framework.Assert.*;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
@SuppressWarnings("deprecation")

public class CategoryControllerTest
{
	private CategoryController catController;
	private PasswordManagerControllerDummy dummy ;
	private PasswordManagerController pmc;
	private Category root;
	private MainWindowAUI mwaui;
	private MainView mv;

    @Before
    public void setUp() throws Exception
    {
    	dummy = new PasswordManagerControllerDummy();
    	pmc = dummy.getNewController();
    	mwaui = pmc.getMainWindowAUI();
    	mv = ((MainView) mwaui);
    	catController = pmc.getCategoryController();
    	
    }

    @Test
    public void createCategory()
    {
    	
    	root = pmc.getPasswordManager().getRootCategory();
    	
    	
    	//valide Kategorieerstellung    	
    	//neue Oberkategorie
    	 catController.createCategory(root, "category without children and credentials");
    	 Collection<Category> subCategories = root.getSubCategories();
    	 
    	 assertTrue("Kategorie wurde nicht an Wurzel angehängt", subCategories.contains(catController.getCategory("root/category without children and credentials")));
    	 
    	 Category categoryWithoutCredentials = new Category("categoryWithoutCredentials");
    	 Category categoryWithoutChildren = new Category("categoryWithoutChildren");
    	 
    	 root.addSubCategory(categoryWithoutCredentials);
    	 categoryWithoutCredentials.addSubCategory(categoryWithoutChildren);
    	 
    	//neue Unterkategorie
    	 catController.createCategory(categoryWithoutChildren, "childCategory");
    	 subCategories = categoryWithoutCredentials.getSubCategories();
    	 
    	 assertTrue("Kategorie wurde nicht an Wurzel angehängt", 
    			 			subCategories.contains(catController.getCategory("root/categoryWithoutCredentials/categoryWithoutChildren/childCategory")));
    	    	
    	
    	 
    	 //nicht valide Kategorieerstellung    	 
    	 //kein Name eingegeben
    	 catController.createCategory(root, "");

    	 Collection<String> errors = mv.getErrorsShown();
    	 assertTrue("Fehler hätte aufgerufen werden müssen",errors.contains("Eingegebener Kategoriename darf nicht leer sein"));
    	 
    	 //Kategorie soll gleichen Namen haben, wie schon existierendes Kind
    	 catController.createCategory(root, "category without children and credentials");

    	 errors = mv.getErrorsShown();
    	 assertTrue("Fehler hätte aufgerufen werden müssen",errors.contains("Eingegebener Kategoriename ist schon vergeben"));
    	 

    }

    @Test
    public void removeCategory()
    {
    	
    	root = pmc.getPasswordManager().getRootCategory();
    	Category childCategoryWithContent = new Category("childCategoryWithContent");
    	Category childCategoryWithSubCategories = new Category("childCategoryWithSubCategories");
    	Category childCategoryWithoutContent = new Category("childCategoryWithoutContent");
    	Category childCategoryWithCredentialsAndSubCat = new Category("childCategoryWithCredentialsAndSubCat");
    	Category emptyChildCategoryDontDelete = new Category("emptyChildCategoryDontDelete");
    	Category emptyChildCategoryDoDelete = new Category("emptyChildCategoryDoDelete");
    	
    	Credentials credentialsDummyNotInDeletedSubCategory = new Credentials("credentialsDummyNotInDeletedSubCategory", "credentialsDummyNotInDeletedSubCategoryUser", "PWcredentialsDummyNotInDeletedSubCategory", "website");
    	Credentials credentialsDummyDoDelete = new Credentials("DoDelete", "credentialsDummyDoDeleteUser", "PWcredentialsDummyDoDelete", "website");
    	Credentials credentialsDummyDoDelete2 = new Credentials("DoDelete2", "credentialsDummyDoDeleteUser2", "PWcredentialsDummyDoDelete2", "website");
    	Credentials credentialsDummyDontDelete = new Credentials("DontDelete", "credentialsDummyDontDeleteUser", "PWcredentialsDummyDontDelete", "website");
    	
    	root.addSubCategory(childCategoryWithoutContent);
    	root.addSubCategory(childCategoryWithSubCategories);
    	
    	childCategoryWithSubCategories.addSubCategory(childCategoryWithContent);
    	childCategoryWithSubCategories.addSubCategory(childCategoryWithCredentialsAndSubCat);
    	
    	childCategoryWithCredentialsAndSubCat.getCredentials().add(credentialsDummyDontDelete);
    	childCategoryWithCredentialsAndSubCat.addSubCategory(emptyChildCategoryDontDelete);
    	
    	childCategoryWithContent.getCredentials().add(credentialsDummyNotInDeletedSubCategory);
    	
    	
    	Collection<Credentials> credentialsToCheck;
    	Collection<Category> catsToCheck;
    	
    	//valides Löschen
    	//nur Kategorie, kein Inhalt löschen, aber Inhalt vorhanden
    	catController.removeCategory(childCategoryWithCredentialsAndSubCat, false);
    	credentialsToCheck = childCategoryWithContent.getCredentials();
    	catsToCheck = childCategoryWithContent.getSubCategories();
    	
    	assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyDontDelete));
    	assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyNotInDeletedSubCategory));
    	assertTrue("Kategorie fälschlicherweise entfernt", catsToCheck.contains(emptyChildCategoryDontDelete));
    	assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithCredentialsAndSubCat));
    	
    	
    	//nur Kategorie, kein Inhalt löschen, aber kein Inhalt vorhanden
    	catController.removeCategory(childCategoryWithoutContent, false);
    	credentialsToCheck = root.getCredentials();
    	catsToCheck = root.getSubCategories();
    	
    	assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithoutContent));
    	
    	
    	root.getSubCategories().add(childCategoryWithoutContent);
    	childCategoryWithSubCategories.addSubCategory(childCategoryWithCredentialsAndSubCat);
    	childCategoryWithoutContent.getSubCategories().remove(emptyChildCategoryDontDelete);
    	
    	childCategoryWithCredentialsAndSubCat.addSubCategory(emptyChildCategoryDoDelete);
    	childCategoryWithCredentialsAndSubCat.getCredentials().add(credentialsDummyDoDelete);
    	childCategoryWithCredentialsAndSubCat.getCredentials().add(credentialsDummyDoDelete2);
    	
    	//Kategorie und gesamten Inhalt löschen, Inhalt vorhanden
    	catController.removeCategory(childCategoryWithCredentialsAndSubCat, true);
    	credentialsToCheck = childCategoryWithContent.getCredentials();
    	catsToCheck = childCategoryWithContent.getSubCategories();

    	assertFalse("Credentials wurden fälschlicherweise nicht entfernt", credentialsToCheck.contains(credentialsDummyDoDelete));
    	assertFalse("Credentials wurden fälschlicherweise nicht entfernt", credentialsToCheck.contains(credentialsDummyDoDelete2));
    	assertFalse("Kategorie fälschlicherweise nicht entfernt", credentialsToCheck.contains(emptyChildCategoryDoDelete));
    	assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyNotInDeletedSubCategory));
    	assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithCredentialsAndSubCat));
    	
    	
    	
    	//Kategorie und gesamten Inhalt löschen, kein Inhalt vorhanden
    	catController.removeCategory(childCategoryWithoutContent, true);
    	credentialsToCheck = root.getCredentials();
    	catsToCheck = root.getSubCategories();

    	assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithoutContent));
    	
    	
    	
    	//nicht valides Löschen
    	//gewählte Kategorie null
    	catController.removeCategory(null, false);
   	 	
    	Collection<String> errors = mv.getErrorsShown();
   	 	assertTrue("Fehler hätte aufgerufen werden müssen",errors.contains("Es muss eine Kategorie ausgewählt sein"));
   	 
    	
    }

    @Test (expected = IllegalArgumentException.class)
    public void moveCategory()
    {
    	
    	root = pmc.getPasswordManager().getRootCategory();
    	Category moveTo = new Category("moveTo");
    	Category moveFrom = new Category("moveFrom");
    	Category toMoveRenameEmpty = new Category("toMoveRenameEmpty");
    	Category toMoveRenameContent = new Category("toMoveRenameContent");
    	Category subToMove = new Category("subToMove");
    	Category dontMove = new Category("dontMove");
    	
    	Credentials credentialsDontMove = new Credentials("credentialsDontMove", "credentialsDontMoveUser", "PWcredentialsDontMove", "website");
    	Credentials credentialsToMove = new Credentials("credentialsToMove", "credentialsToMoveUser", "PWcredentialsToMove", "website");
    	
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
    	catController.moveCategory(new Path("root/moveFrom/toMoveRenameContent"), new Path("root/moveTo/toMoveRenameContent"));
    	assertTrue("Kategorie hätte hierhin verschoben werden sollen",moveTo.getSubCategories().contains(toMoveRenameContent));
    	assertTrue("Kategorie sollte nach Verschieben die Unterkategorie enthalten",toMoveRenameContent.getSubCategories().contains(subToMove));
    	assertTrue("Kategorie sollte nach Verschieben die Credentials enthalten",toMoveRenameContent.getCredentials().contains(credentialsToMove));
    	assertTrue("Kategorie hätte nicht verschoben werden sollen",moveFrom.getSubCategories().contains(dontMove));
    	assertTrue("Credentials hätte nicht verschoben werden sollen",moveFrom.getCredentials().contains(credentialsDontMove));
    	assertFalse("Kategorie hätte verschoben werden sollen",moveFrom.getSubCategories().contains(toMoveRenameContent));
    	
    	//nur umbenennen
    	catController.moveCategory(new Path("root/moveTo/toMoveRenameContent"), new Path("root/moveTo/toMoveContentRenamed"));
    	assertTrue("Kategorie hätte hierhin verschoben werden sollen",moveTo.getSubCategories().contains(toMoveRenameContent));
    	assertEquals("Kategorie falsch umbenannt", "toMoveContentRenamed", toMoveRenameContent.getName());
    	assertTrue("Kategorie sollte nach Verschieben die Unterkategorie enthalten",toMoveRenameContent.getSubCategories().contains(subToMove));
    	assertTrue("Kategorie sollte nach Verschieben die Credentials enthalten",toMoveRenameContent.getCredentials().contains(credentialsToMove));
    	
    	//verschieben und umbenennen
    	catController.moveCategory(new Path("root/moveTo/toMoveContentRenamed"), new Path("root/moveFrom/toMoveRenameContent"));
    	assertTrue("Kategorie hätte hierhin verschoben werden sollen",moveFrom.getSubCategories().contains(toMoveRenameContent));
    	assertEquals("toMoveRenameContent", toMoveRenameContent.getName());
    	assertTrue("Kategorie sollte nach Verschieben die Unterkategorie enthalten",toMoveRenameContent.getSubCategories().contains(subToMove));
    	assertTrue("Kategorie sollte nach Verschieben die Credentials enthalten",toMoveRenameContent.getCredentials().contains(credentialsToMove));
    	assertTrue("Kategorie hätte nicht verschoben werden sollen",moveFrom.getSubCategories().contains(dontMove));
    	assertTrue("Credentials hätte nicht verschoben werden sollen",moveFrom.getCredentials().contains(credentialsDontMove));
    	assertFalse("Kategorie hätte verschoben werden sollen",moveTo.getSubCategories().contains(toMoveRenameContent));
    	
    	
    	//nicht valides move
    	//einer der Pfade ist null --> Exception wurde geworfen
    	catController.moveCategory(null, new Path("root/moveFrom/toMoveRenameContent"));
    }
//---------------------------------
    @Test
    public void getCategory()
    {
    }

    @Test
    public void findCategory()
    {
    }
}