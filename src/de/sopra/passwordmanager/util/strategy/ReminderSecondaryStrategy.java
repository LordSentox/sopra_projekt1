package de.sopra.passwordmanager.util.strategy;

import de.sopra.passwordmanager.util.CredentialsItem;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 28.08.2019
 * @since 28.08.2019
 */
public class ReminderSecondaryStrategy implements EntryListOrderStrategy {

    @Override
    public List<CredentialsItem> order(List<CredentialsItem> credentials) {
        List<CredentialsItem> ordered = new LinkedList<>(credentials);
        Comparator<CredentialsItem> comparator = (item1, item2) -> {
            if (item1 == null)
                return 1;
            if (item2 == null)
                return -1;
            boolean check1 = item1.hasToBeChanged();
            boolean check2 = item2.hasToBeChanged();
            if (check1 && !check2)
                return -1;
            else if (!check1 & check2)
                return 1;
            return 0;
        };
        if (ordered != null)
            ordered.sort(comparator);
        return ordered;
    }

}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/