package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.Category;

import java.util.List;

/**
 * <h1>projekt1</h1>
 * <p>
 * Die Strategy, die aus dem Datensatz nach einer variierenden Vorgehensweise die anzuzeigenden Daten extrahiert.
 * Übernimmt Suchen, Filtern und Sortieren.
 *
 * @author Julius Korweck
 * @version 26.08.2019
 * @since 26.08.2019
 */
public interface EntryListStrategy {

    /**
     * Extrahiert aus den gegebenen Daten die anzuzeigende Liste.
     *
     * @param selectedCategory die ausgewählte Kategorie
     * @param pattern          das aktuelle Such-Pattern
     * @return die Liste mit allen Inhalten, die zur Anzeige gebracht werden sollen
     */
    List<String> apply(Category selectedCategory, String pattern);

}