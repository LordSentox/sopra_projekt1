package de.sopra.passwordmanager.util.strategy;

import de.sopra.passwordmanager.util.CredentialsItem;

import java.util.List;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 26.08.2019
 * @since 26.08.2019
 */
public interface EntryListOrderStrategy {

    /**
     * Sortiert die gegebenen Daten in der Liste neu.
     * Soll die urprüngliche Liste nicht verändern.
     * Die neue Liste soll die gleiche Länge haben, wie die ursprüngliche Liste.
     *
     * @param credentials die Liste der unsortierten Credentials
     * @return eine neue sortierte Liste der Credentials
     */
    List<CredentialsItem> order(List<CredentialsItem> credentials);

    default EntryListOrderStrategy nextOrder(EntryListOrderStrategy strategy) {
        return input -> strategy.order(order(input));
    }

}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/