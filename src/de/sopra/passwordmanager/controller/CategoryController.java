package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.Path;

import java.util.Collection;
import java.util.List;


/**
 * sorgt für das Erstellen, Bearbeiten und Löschen. Intern kann nach Kategorien gesucht werden.
 *
 * @authors Max, Lisa
 */
public class CategoryController {

    private PasswordManagerController passwordManagerController;

    public CategoryController(PasswordManagerController controller) {
        this.passwordManagerController = controller;
    }

    /**
     * erstellt eine neue Kategorie
     * die eine Oberkategorie und einen Namen haben muss.
     * Die anzulegende Kategorie muss der Oberkategorie als Kind angehängt werden.
     *
     * @param superCategory übergebene Oberkategorie. Darf null sein.
     *                      Wenn null, dann wird die rootCategory aus dem Datenmodell geholt (getCategory)
     *                      und die anzulegende Kategorie wird an diese angehängt.
     * @param name          Name der anzulegenden Kategorie, darf nicht null sein.
     *                      Darf nicht leer sein.
     *                      Wenn das der Fall ist, wird die MainWindowAUI geholt und darauf showError("Eingegebener Kategoriename darf nicht leer sein") aufgerufen
     *                      Darf nicht den gleichen Namen haben wie ein anderes Kind der Oberkategorie.
     *                      Wenn das der Fall ist, wird die MainWindowAUI geholt und darauf showError("Eingegebener Kategoriename ist schon vergeben") aufgerufen
     * @throws IllegalArgumentException wenn name null ist, wird die Exception geworfen
     */
    public void createCategory(Category superCategory, String name) throws IllegalArgumentException {
    }

    /**
     * entfernt die Kategorie aus dem Datenmodell.
     * Je nach übergebenem Boolean werden die enthaltenen Credentials und Unterkategorien mit gelöscht oder nicht.
     *
     * @param category             Die zu löschende Kategorie, darf nicht Null sein
     *                             Wenn das der Fall ist, wird die MainWindowAUI geholt und darauf showError("Es muss eine Kategorie ausgewählt sein") aufgerufen
     * @param removeCredentialsToo gibt an, ob die enthaltenen Credentials und Unterkategorien mit gelöscht werden oder nicht.
     *                             <p>
     *                             Wenn nur die Kategorie gelöscht werden soll, dann müssen die enthaltenen Unterkategorien und Anmeldedaten
     *                             der Oberkategorie der zu löschenden Kategorie angehängt werden.
     *                             <p>
     *                             Wenn alles anhängende gelöscht werden soll, dann wird die rootCategory aus dem Datenmodell geholt.
     *                             Alle zu löschenden Anmeldedaten werden dann aus allen Kategorien, denen sie angehängt sind, entfernt,
     *                             indem alle Referenzen, die auf diese Daten verweisen, entfernt werden.
     *                             <p>
     *                             Dann wird die Referenz auf die zu Löschende Kategorie entfernt.
     */
    public void removeCategory(Category category, boolean removeCredentialsToo) {
    }

    /**
     * holt sich mittels getCategory(oldPath) die zu verschiebende Kategorie [oldPath mit dem Namen der zu verschiebenden Kategorie]
     * und die zugehörige Oberkategorie [oldPath ohne den Namen der zu verschiebenden Kategorie],
     * mittels getCategory(newPath) die neue Oberkategorie [newPath ohne den Namen der zu verschiebenden Kategorie] und hängen dieser die zu verschiebende Kategorie an.
     * Danach wird die Referenz von der Alten Oberkategorie auf die verschobene Kategorie entfernt.
     * Dann wird aus newPath der neue Name für die verschobene Kategorie ausgelesen und gesetzt.
     *
     * @param oldPath kompletter Pfad bis inkl. Kategoriename der zu verschiebenden Kategorie
     * @param newPath kompletter Pfad bis inkl. Kategoriename der verschobenen Kategorie
     * @throws IllegalArgumentException wenn einer der Pfade null ist, wird die Exception geworfen
     */
    public void moveCategory(Path oldPath, Path newPath) throws IllegalArgumentException {
    }

    /**
     * holt sich vom passwordManagerController die rootCategory.
     * Dann wird der übergebene Pfad Schritt für Schritt (Kategorien getrennt durch "/") durchlaufen
     * und jeweils die entsprechende Unterkategorie aus dem Datenmodell geholt, bis der String abgearbeitet ist.
     * Die letzte geholte Kategorie ist die gesuchte, die zurückgegeben wird.
     *
     * @param path kompletter Pfad bis inkl. Kategoriename der gesuchten Kategorie.
     *             wenn der Pfad null oder leerer String ist, wird die rootCategory zurükgegeben.
     * @return gibt die letzte Kategorie des angegebenen Pfades zurück
     */
    Category getCategory(String path) {
        return null;
    }

    /**
     * holt sich mittels getCategory() die rootCategory und erstellt eine leere ArrayList<Category>.
     * Dann durchsucht es alle Unterkategorien und fügt alle, deren Name dem eingegebenen Namen entspricht der ArrayList<Category> hinzu.
     * Sind alle Unterkategorien durchsucht, wird die ArrayList<Category> zurückgegeben.
     * Gibt es keine passende Unterkategorie, ist die ArrayList<Category> leer und wird ebenfalls zurückgegeben.
     *
     * @param name der Name der gesuchten Unterkategorien
     * @return die angelegte und (eventuell) gefüllte ArrayList<Category>
     */
    List<Category> findCategory(String name) {
        return null;
    }

    /**
     * //TODO
     *
     * @param credentials
     * @param categories
     */
    void addCredentialsToCategories(Credentials credentials, Collection<Category> categories) {

    }

    /**
     * //TODO
     *
     * @param credentials
     */
    void removeCredentialsFromCategories(Credentials credentials) {

    }

}
