package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.Category;

import java.util.List;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 26.08.2019
 * @since 26.08.2019
 */
public class DefaultEntryListStrategy implements EntryListStrategy {

    @Override
    public List<String> apply(Category selectedCategory, String pattern) {
        //TODO: default, filter input by categories -> dont modify input, create new list
        return null;
    }
}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/