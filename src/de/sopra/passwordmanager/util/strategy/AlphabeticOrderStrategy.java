package de.sopra.passwordmanager.util.strategy;

import de.sopra.passwordmanager.model.Credentials;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class AlphabeticOrderStrategy implements EntryListOrderStrategy {

    @Override
    public List<Credentials> order(List<Credentials> credentials) {
        LinkedList<Credentials> list = new LinkedList<>(credentials);
        list.sort(Comparator.comparing(Credentials::getName));
        return list;
    }

}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/