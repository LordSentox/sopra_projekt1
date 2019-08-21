package de.sopra.passwordmanager.model;

import java.util.Collection;

public class Category
{

    private String name;

    private Collection<Credentials> credentials;

    private Collection<Category> subCategories;

    public String getName()
    {
        return name;
    }

    public Collection<Credentials> getCredentials()
    {
        return credentials;
    }

    public Collection<Category> getSubCategories()
    {
        return subCategories;
    }

}
