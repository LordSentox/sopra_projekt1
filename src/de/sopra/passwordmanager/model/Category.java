package de.sopra.passwordmanager.model;

import de.sopra.passwordmanager.util.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Enthält {@link Credentials} und Unterkategorien, die in die Kategorie einsortiert wurden. Es gibt eine rootCategory im
 * {@link PasswordManager}, welche alle obersten Kategorien, sowie die nicht eingeordneten {@link Credentials} enthält.
 */
public class Category {
    /**
     * Der Name der Kategorie. Entspricht nicht dem gesamten Pfad der Kategorie.
     */
    private String name;

    /**
     * Alle Credentials, die dieser Kategorie zugeordnet sind.
     */
    private Collection<Credentials> credentials;

    /**
     * Die Liste aller Kategorien, welche der aktuellen Kategorie untergeordnet sind.
     */
    private Collection<Category> subCategories;

    public Category(String name) {
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

    /**
     * Fügt eine Kategorie in die Liste der untergeordneten Kategorien ein
     *
     * @param category die einzufügende Kategorie
     */
    public void addSubCategory(Category category) {
        subCategories.add(category);
    }

    /**
     * Entfernt eine Kategorie aus der Liste der untergeordneten Kategorien
     *
     * @param category die zu entfernende Kategorie
     */
    public void removeSubCategory(Category category) {
        subCategories.remove(category);
    }

    /**
     * Prüft die Existenz einer untergeordneten Kategorie über ihren Namen.
     *
     * @param name den Namen der zu suchenden untergeordneten Kategorie
     * @return <code>true</code> wenn eine Kategorie mit dem Namen der aktuellen Kategorie untergeordnet ist, sonst <code>false</code>
     */
    public boolean hasSubCategory(String name) {
        return subCategories.stream().anyMatch(category -> category.getName().equals(name));
    }

    /**
     * Ordnete ein existierendes Credentials Objekt der aktuellen Kategorie Instanz zu
     *
     * @param credentials die zuzuordnenden Credentials
     */
    public void addCredentials(Credentials credentials) {
        this.credentials.add(credentials);
    }

    /**
     * Entfernt die Zuordnung von einer Credentials Instanz zu dieser Kategorie.
     * Ist ein Credentials Objekt keiner Kategorie zugeordnet gilt es bis zur erneuten Zuordnung als gelöscht.
     *
     * @param credentials die von dieser Kategorie zu entfernende Credentials Instanz
     */
    public void removeCredentials(Credentials credentials) {
        this.credentials.remove(credentials);
    }

    /**
     * Entfernt die Zuordnung von einer Credentials Instanz zu dieser Kategorie und allen untergeordneten Kategorien.
     *
     * @param credentials die von dieser Kategorie und untergeordneten Kategorien zu entfernende Credentials Instanz
     * @see #removeCredentials(Credentials)
     */
    public void removeCredentialsFromTree(Credentials credentials) {
        removeCredentials(credentials);
        subCategories.forEach(subCategory -> subCategory.removeCredentialsFromTree(credentials));
    }

    /**
     * Bestimmt eine Kategorie über ihren absoluten Pfad.
     * Ein absoluter Pfad beginnt bei einer root-Kategorie und endet bei der Zielkategorie.
     * Die Funktionalität dieser Methode ist gewährleistet, wenn die aktuelle Kategorie als root im Pfad behandelt wird.
     * Ein Pfad ohne Child Element {@link Path#hasChild()} und ohne Parent Element {@link Path#hasParent()}
     * führt zu der aktuellen Kategorie, wenn der name {@link Path#getName()} mit dem Namen der Kategorie übereinstimmt.
     * Wenn der Pfad falsch ist oder die Kategorie nicht existiert, dann ist das Ergebnis null.
     * Das übergebene {@link Path} Objekt sollte eine Kopie des original Pfades darstellen, da die Methode Änderungen an der Instanz vornimmt.
     *
     * @param path die Kopie des Pfades zu der die entsprechende Kategorie ermittelt werden soll
     * @return die Kategorie, auf welche der Pfad zeigt oder <code>null</code> wenn die Kategorie fehlt oder der Pfad falsch ist
     */
    public Category getCategoryByPath(Path path) {
        path.navigate(0);
        if (path.getName().equals(getName())) {
            if (!path.hasParent() && !path.hasChild()) {
                return this;
            } else {
                for (Category cat : subCategories) {
                    Category categoryByPath = cat.getCategoryByPath(path.subPath(1, path.length()));
                    if (categoryByPath != null) return categoryByPath;
                }
                return null;
            }
        }
        return null;
    }

    /**
     * Erstellt eine Map mit dieser und allen untergeordneten Kategorien.
     * Die Kategorien sind gegenüber dem angegebenen parent Path mit ihrem absoluten Pfad als Key aus der Map zu erhalten.
     * Wird diese Methode mit einem relativen parent Path aufgerufen, so werden auch alle Pfade in der Ergebnis Map zu relativen Pfaden.
     *
     * @return eine Map die von der aktuellen Kategorie ausgehend alle Kategorien und die dazugehörigen Pfade enthält
     */
    public Map<Path, Category> createPathMap(Path parent) {
        HashMap<Path, Category> categoryHashMap = new HashMap<>();
        Path childPath = parent.createChildPath(getName());
        categoryHashMap.put(childPath, this);
        for (Category sub : subCategories)
            categoryHashMap.putAll(sub.createPathMap(childPath));
        return categoryHashMap;
    }

}
