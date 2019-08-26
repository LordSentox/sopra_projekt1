package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.Credentials;

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
     * Sortiert die gegebenen Daten in der Liste neu
     * @param credentials
     * @return
     */
    List<Credentials> order(List<Credentials> credentials);

    default EntryListOrderStrategy nextOrder(EntryListOrderStrategy strategy) {
        return input -> strategy.order(order(input));
    }

}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/