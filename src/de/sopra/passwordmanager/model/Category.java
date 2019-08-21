package de.sopra.passwordmanager.model;

import de.sopra.passwordmanager.util.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Enthält {@link Credentials} und Unterkategorien, die in die Kategorie einsortiert wurden. Es gibt eine rootCategory im
 * {@link PasswordManager}, welche alle obersten Kategorien, sowie die nicht eingeordneten {@link Credentials} enthält.
 */
public class Category {
    /**
     * Der Name der Kategorie. Nicht zu verwechseln mit dem Pfad, welcher nur von der Wurzel aus gefunden werden kann.
     */
    private String name;

    /**
     * Alle Credentials, die dieser Kategorie zugeordnet sind.
     */
    private Collection<Credentials> credentials;

    /**
     * Die nächsten Unterkategorien, welche dieser zugeordnet sind.
     */
    private Collection<Category> subCategories;

    public Category( String name ) {
        this.name = name;
        this.credentials = new ArrayList<>();
        this.subCategories = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Collection<Credentials> getCredentials() {
        return credentials;
    }

    public Collection<Category> getSubCategories() {
        return subCategories;
    }

    public void addSubCategory( Category category ) {
        subCategories.add( category );
    }

    public void removeSubCategory( Category category ) {
        subCategories.remove( category );
    }

    public void addCredentials(Credentials credentials) {
        this.credentials.add( credentials );
    }

    public void removeCredentials(Credentials credentials) {
        this.credentials.remove( credentials );
    }

    public void removeCredentialsFromTree(Credentials credentials) {
        removeCredentials( credentials );
        subCategories.forEach( s -> s.removeCredentialsFromTree( credentials ) );
    }

    public Category getCategoryByPath(Path path) {
        path.navigate( 0 );
        if(path.getName().equals( getName() )) {
            if(path.length() == 1) {
                return this;
            } else {
                Optional<Category> any = subCategories.stream()
                        .filter( s -> s.getCategoryByPath( path.subPath( 1, path.length() ) ) != null )
                        .findAny();
                if(any.isPresent()) return any.get();
                else return null;
            }
        }
        else return null;
    }

}
