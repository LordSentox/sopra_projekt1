package de.sopra.passwordmanager.model;

import de.sopra.passwordmanager.util.Path;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class CategoryTest
{

    @Test
    public void removeCategoryFromTreeTest()
    {
        //credentials anlegen
        Credentials credentials = new Credentials( "name", "user", "pw", "web" );
        Credentials credentials2 = new Credentials( "name2", "user2", "pw2", "web" );

        //categories anlegen
        Category category = new Category( "cat1" );
        Category categorySub1 = new Category( "catSub1" );
        Category categorySub2 = new Category( "catSub2" );

        //Konstrukt zusammenbauen
        category.addSubCategory( categorySub1 );
        category.addSubCategory( categorySub2 );
        categorySub1.addCredentials( credentials );
        categorySub2.addCredentials( credentials );
        categorySub2.addCredentials( credentials2 );
        category.removeCredentialsFromTree( credentials );

        //Test
        assertEquals( "category was not removed correctly", categorySub1.getCredentials().size(), 0 );
        assertEquals( "category was not removed correctly", categorySub2.getCredentials().size(), 1 );
    }

    @Test
    public void getCategoryByPathTest()
    {
        //categories anlegen
        Category root = new Category( "root" );
        Category categorySub1 = new Category( "layer" );
        Category categorySub2 = new Category( "sameLayer" );
        Category categorySubSub1 = new Category( "deeper" );

        //Konstrukt zusammenbauen
        root.addSubCategory( categorySub1 );
        root.addSubCategory( categorySub2 );
        categorySub1.addSubCategory( categorySubSub1 );

        //Testaufrufe
        Category deeper1 = root.getCategoryByPath( new Path( "root/layer/deeper" ) );
        Category notFound = root.getCategoryByPath( new Path( "root/sameLayer/deeper" ) );

        //Test der Ergebnisse
        assertEquals( "category not found by path", deeper1, categorySubSub1 );
        assertNull( "anything was found for a path pointing nowhere", notFound );
    }

}