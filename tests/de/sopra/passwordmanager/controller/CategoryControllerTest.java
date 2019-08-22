package de.sopra.passwordmanager.controller;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.sopra.passwordmanager.model.*;
import junit.framework.Assert;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class CategoryControllerTest
{
	private CategoryController catController;
	private PasswordManagerControllerDummy dummy ;
	private PasswordManagerController pmc;
	private Category root;

    @Before
    public void setUp() throws Exception
    {
    	dummy = new PasswordManagerControllerDummy();
    	pmc = dummy.getNewController();
    	
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
    	 
    	 Assert.assertTrue("Kategorie wurde nicht an Wurzel angehängt", subCategories.contains(catController.getCategory("root/category without children and credentials")));
    	 
    	 Category categoryWithoutCredentials = new Category();
    	 Category categoryWithoutChildren = new Category();
    	 
    	 root.getSubCategories().add(categoryWithoutCredentials);
    	 categoryWithoutCredentials.getSubCategories().add(categoryWithoutChildren);
    	 
    	//neue Unterkategorie
    	 catController.createCategory(categoryWithoutChildren, "childCategory");
    	 subCategories = categoryWithoutCredentials.getSubCategories();
    	 
    	 Assert.assertTrue("Kategorie wurde nicht an Wurzel angehängt", 
    			 			subCategories.contains(catController.getCategory("root/categoryWithoutCredentials/categoryWithoutChildren/childCategory")));
    	    	
    	
    	 
    	 //nicht valide Kategorieerstellung    	 
    	 //kein Name eingegeben
    	 catController.createCategory(root, "");
    	 //--> showError
    	 //--> Assert
    	 
    	 //Kategorie soll gleichen Namen haben, wie schon existierendes Kind
    	 catController.createCategory(root, "category without children and credentials");
    	 //--> showError
    	 //--> Assert
    	 

    }

    @Test
    public void removeCategory()
    {
    	
    	root = pmc.getPasswordManager().getRootCategory();
    	Category childCategoryWithContent = new Category();
    	Category childCategoryWithSubCategories = new Category();
    	Category childCategoryWithoutContent = new Category();
    	Category childCategoryWithCredentialsAndSubCat = new Category();
    	Category emptyChildCategoryDontDelete = new Category();
    	Category emptyChildCategoryDoDelete = new Category();
    	
    	Credentials credentialsDummyNotInDeletedSubCategory = new Credentials("pwcredentialsDummyNotInDeletedSubCategory", 0, 2, "credentialsDummyNotInDeletedSubCategory", "credentialsDummyNotInDeletedSubCategoryUser", null, null, 0, null);
    	Credentials credentialsDummyDoDelete = new Credentials("pwDoDelete", 0, 2, "credentialsDummyDoDelete", "credentialsDummyDoDeleteUser", null, null, 0, null);
    	Credentials credentialsDummyDoDelete2 = new Credentials("pwDoDelete2", 0, 2, "credentialsDummyDoDelete2", "credentialsDummyDoDeleteUser2", null, null, 0, null);

    	Credentials credentialsDummyDontDelete = new Credentials("pwDontDelete", 0, 2, "credentialsDummyDontDelete", "credentialsDummyDontDeleteUser", null, null, 0, null);
    	
    	root.getSubCategories().add(childCategoryWithoutContent);
    	root.getSubCategories().add(childCategoryWithSubCategories);
    	
    	childCategoryWithSubCategories.getSubCategories().add(childCategoryWithContent);
    	childCategoryWithSubCategories.getSubCategories().add(childCategoryWithCredentialsAndSubCat);
    	
    	childCategoryWithCredentialsAndSubCat.getCredentials().add(credentialsDummyDontDelete);
    	childCategoryWithCredentialsAndSubCat.getSubCategories().add(emptyChildCategoryDontDelete);
    	
    	childCategoryWithContent.getCredentials().add(credentialsDummyNotInDeletedSubCategory);
    	
    	
    	Collection<Credentials> credentialsToCheck;
    	Collection<Category> catsToCheck;
    	
    	//valides Löschen
    	//nur Kategorie, kein Inhalt löschen, aber Inhalt vorhanden
    	catController.removeCategory(childCategoryWithCredentialsAndSubCat, false);
    	credentialsToCheck = childCategoryWithContent.getCredentials();
    	catsToCheck = childCategoryWithContent.getSubCategories();
    	
    	Assert.assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyDontDelete));
    	Assert.assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyNotInDeletedSubCategory));
    	Assert.assertTrue("Kategorie fälschlicherweise entfernt", catsToCheck.contains(emptyChildCategoryDontDelete));
    	Assert.assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithCredentialsAndSubCat));
    	
    	
    	//nur Kategorie, kein Inhalt löschen, aber kein Inhalt vorhanden
    	catController.removeCategory(childCategoryWithoutContent, false);
    	credentialsToCheck = root.getCredentials();
    	catsToCheck = root.getSubCategories();
    	
    	Assert.assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithoutContent));
    	
    	
    	root.getSubCategories().add(childCategoryWithoutContent);
    	childCategoryWithSubCategories.getSubCategories().add(childCategoryWithCredentialsAndSubCat);
    	childCategoryWithoutContent.getSubCategories().remove(emptyChildCategoryDontDelete);
    	
    	childCategoryWithCredentialsAndSubCat.getSubCategories().add(emptyChildCategoryDoDelete);
    	childCategoryWithCredentialsAndSubCat.getCredentials().add(credentialsDummyDoDelete);
    	childCategoryWithCredentialsAndSubCat.getCredentials().add(credentialsDummyDoDelete2);
    	
    	//Kategorie und gesamten Inhalt löschen, Inhalt vorhanden
    	catController.removeCategory(childCategoryWithCredentialsAndSubCat, true);
    	credentialsToCheck = childCategoryWithContent.getCredentials();
    	catsToCheck = childCategoryWithContent.getSubCategories();

    	Assert.assertFalse("Credentials wurden fälschlicherweise nicht entfernt", credentialsToCheck.contains(credentialsDummyDoDelete));
    	Assert.assertFalse("Credentials wurden fälschlicherweise nicht entfernt", credentialsToCheck.contains(credentialsDummyDoDelete2));
    	Assert.assertFalse("Kategorie fälschlicherweise nicht entfernt", credentialsToCheck.contains(emptyChildCategoryDoDelete));
    	Assert.assertTrue("Credentials wurden fälschlicherweise entfernt", credentialsToCheck.contains(credentialsDummyNotInDeletedSubCategory));
    	Assert.assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithCredentialsAndSubCat));
    	
    	
    	
    	//Kategorie und gesamten Inhalt löschen, kein Inhalt vorhanden
    	catController.removeCategory(childCategoryWithoutContent, true);
    	credentialsToCheck = root.getCredentials();
    	catsToCheck = root.getSubCategories();

    	Assert.assertFalse("Kategorie fälschlicherweise nicht entfernt", catsToCheck.contains(childCategoryWithoutContent));
    	
    	
    	
    	//nicht valides Löschen
    	//gewählte Kategorie null
    	//--> showError
    	//--> Assert    	
    	
    }

    @Test
    public void moveCategory()
    {
    	
    	root = pmc.getPasswordManager().getRootCategory();
    	
    	
    	//valides Move
    	//tatsächlich verschieben, kein umbenennen
    	
    	//nur umbenennen
    	
    	//verschieben und umbenennen
    	
    	
    	
    	//nicht valides move
    	//eienr der Pfade ist null --> Exception wurde geworfen
    	
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