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
        Credentials credentials = new Credentials( "name", "user", "pw", "web", 5, "" );
        Credentials credentials2 = new Credentials( "name2", "user2", "pw2", "web", 4, "" );

        Category category = new Category( "cat1" );
        Category categorySub1 = new Category( "catSub1" );
        Category categorySub2 = new Category( "catSub2" );

        category.addSubCategory( categorySub1 );
        category.addSubCategory( categorySub2 );
        categorySub1.addCredentials( credentials );
        categorySub2.addCredentials( credentials );
        categorySub2.addCredentials( credentials2 );
        category.removeCredentialsFromTree( credentials );

        assertEquals( categorySub1.getCredentials().size(), 0 );
        assertEquals( categorySub2.getCredentials().size(), 1 );
    }

    @Test
    public void getCategoryByPathTest()
    {
        Category root = new Category( "root" );
        Category categorySub1 = new Category( "layer" );
        Category categorySub2 = new Category( "sameLayer" );
        Category categorySubSub1 = new Category( "deeper" );

        root.addSubCategory( categorySub1 );
        root.addSubCategory( categorySub2 );
        categorySub1.addSubCategory( categorySubSub1 );

        Category deeper1 = root.getCategoryByPath( new Path( "root/layer/deeper" ) );
        Category notFound = root.getCategoryByPath( new Path( "root/sameLayer/deeper" ) );

        assertEquals( deeper1, categorySubSub1 );
        assertNull( notFound );
    }

}