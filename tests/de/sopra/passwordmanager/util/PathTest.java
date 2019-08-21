package de.sopra.passwordmanager.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class PathTest
{

    private Path getPath()
    {
        return new Path( "root/test/path/is5/deep" );
    }

//    private Path genRandomPath()
//    {
//        int length = new Random().nextInt( 10 );
//        String path = "";
//        for ( int i = 0; i < length; i++ )
//        {
//            path = path + "/item" + i;
//        }
//        return new Path( path );
//    }

    @Test
    public void constructorTest()
    {
        assertEquals( "construction by string or toString failed",
                getPath().toString(), "root/test/path/is5/deep" );
        assertEquals( "construction does not remove leading or ending slashes",
                new Path( "/root/test/path/is5/deep/" ).toString(), "root/test/path/is5/deep" );
    }

    @Test
    public void getParentAndChildTest()
    {
        Path parent = getPath().getParent();

        assertEquals( "parent has the wrong name", parent.getName(), "is5" );
        assertTrue( "parent should have a child", parent.hasChild() );

        Path path = getPath();

        //auf root parent zeigen lassen
        path.navigate( 0 );
        assertFalse( "root should have no parent", path.hasParent() );
        assertEquals( "parent of root should point to root itself", path.getName(), path.getParent().getName() );

        //auf leaf child zeigen lassen
        path.navigate( path.length()-1 );
        assertFalse( "leaf should have no child", path.hasChild() );
        assertEquals( "child of leaf should point to leaf itself", path.getName(), path.getChild().getName() );
    }

    @Test
    public void subPathAndAbsolutePathTest()
    {
        assertEquals( "absolutePath does change the path itself",
                getPath().toString(), getPath().absolutePath().toString() );
        assertEquals( "absolutePath does cut the path the wrong way",
                getPath().getParent().absolutePath().toString(), "root/test/path/is5" );
        assertEquals( "absolutePath does not match the correct length",
                getPath().getParent().absolutePath().length(), 4 );
    }

    @Test
    public void navigate()
    {
        Path path = getPath();
        assertEquals( "start navigation failed", path.getName(), "deep" );
        path.navigate(2);
        assertEquals( "inner navigation failed", path.getName(), "path" );
        path.navigate( 0 );
        assertEquals( "root navigation failed", path.getName(), "root" );
        path.navigate( 6 );
        assertEquals( "over end navigation failed", path.getName(), "deep" );
        path.navigate( -1 );
        assertEquals( "before root navigation failed", path.getName(), "root" );
    }

}