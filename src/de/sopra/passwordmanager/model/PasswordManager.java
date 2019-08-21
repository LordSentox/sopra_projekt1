package de.sopra.passwordmanager.model;

public class PasswordManager
{

    private BasePassword masterPassword;

    private Category rootCategory;

    public PasswordManager()
    {
        rootCategory = new Category("alle");
    }

    public BasePassword getMasterPassword()
    {
        return masterPassword;
    }

    public Category getRootCategory()
    {
        return rootCategory;
    }

    public void setMasterPassword( BasePassword masterPassword )
    {
        this.masterPassword = masterPassword;
    }

}
