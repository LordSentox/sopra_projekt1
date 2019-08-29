package de.sopra.passwordmanager.util.strategy;

import de.sopra.passwordmanager.model.Credentials;

import java.time.LocalDateTime;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 28.08.2019
 * @since 28.08.2019
 */
public class GenerateReminderPrefixStrategy implements ItemNamingStrategy<Credentials> {

    @Override
    public String getName(Credentials item) {
        return hasToBeChanged(item) ? "!!TÜDELÜ!! - " : "";
    }

    private boolean hasToBeChanged(Credentials item) {
        if (item.getChangeReminderDays() == null)
            return false;
        LocalDateTime time1 = item.getLastChanged().plusDays(item.getChangeReminderDays());
        return time1.isBefore(LocalDateTime.now());
    }

}