package de.sopra.passwordmanager.model;

import org.junit.Assert;
import org.junit.Test;

public class EncryptedStringTest {

    @Test
    public void test() {
        EncryptedString s1 = new EncryptedString("hi");
        EncryptedString s2 = new EncryptedString("ho");
        EncryptedString s3 = new EncryptedString("hi");

        Assert.assertEquals("content getter value not equal", "hi", s1.getEncryptedContent());
        Assert.assertEquals("hashCode value not equal", "hi".hashCode(), s1.getEncryptedContent().hashCode());
        Assert.assertTrue("equals method faulty", s1.equals(s3));
        Assert.assertFalse("equals method faulty", s1.equals(s2));
        s1.toString();
    }

}
