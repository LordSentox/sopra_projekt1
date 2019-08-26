package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.Category;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 26.08.2019
 * @since 26.08.2019
 */
public class EntryListProducer {

    private Category root;

    private Path currentCategoryFilter;

    public void refreshRoot(Category root)
    {
        this.root = root;
    }

}