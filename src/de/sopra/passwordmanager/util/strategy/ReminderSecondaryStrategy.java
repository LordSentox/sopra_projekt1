package de.sopra.passwordmanager.util.strategy;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsItem;

import java.time.LocalDateTime;
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
            boolean check1 = hasToBeChanged(item1);
            boolean check2 = hasToBeChanged(item2);
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

    private boolean hasToBeChanged(CredentialsItem item) {
        Credentials credentials1 = item.getCredentials();
        if (credentials1.getChangeReminderDays() == null)
            return false;
        Integer reminderDays = credentials1.getChangeReminderDays();
        LocalDateTime time1 = credentials1.getLastChanged().plusDays(reminderDays);
        return time1.isBefore(LocalDateTime.now());
    }

}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/