package de.sopra.passwordmanager.controller;

import org.junit.Before;
import org.junit.Test;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class CredentialsControllerTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void saveCredentials()
    {
        //valid
        //case save..(null, newCred) - create new credentials
        //case save..(oldCred, changedCred) - change credentials values
        //case save..(oldCred, nameChangedCred) - change credentials name
        //invalid
        //case save..(null, null) - nothing to be saved
        //case save..(oldCred, null) - nothing to change
        //case save..(oldNonExistingCred, newCred) - old not found
    }

    @Test
    public void removeCredentials()
    {

    }

    @Test
    public void addSecurityQuestion()
    {
    }

    @Test
    public void removeSecurityQuestion()
    {
    }

    @Test
    public void filterCredentials()
    {
    }

    @Test
    public void copyPasswordToClipboard()
    {
    }

    @Test
    public void setPasswordShown()
    {
    }

    @Test
    public void getCredentialsByCategoryName()
    {
    }

    @Test
    public void clearPasswordFromClipboard()
    {
    }

    @Test
    public void reencryptAll()
    {
    }
}