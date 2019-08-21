package de.sopra.passwordmanager.model;

import java.util.Collection;

public class Category {

	private String name;

	private Collection<Credentials> credentials;

	private Collection<Category> subCategories;

}
