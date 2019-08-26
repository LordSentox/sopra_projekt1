package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.Credentials;

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
public interface EntryListSelectionStrategy {

    /**
     * Wählt aus den gegebenen Daten die anzuzeigende Liste.
     *
     * @param credentials alle bekannten Elemente
     * @return die Liste mit allen Inhalten, die zur Anzeige gebracht werden sollen
     */
    List<Credentials> select(List<Credentials> credentials);

}