package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.UtilityController;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.strategy.AdaptableNamingStrategy;
import de.sopra.passwordmanager.util.strategy.GenerateReminderPrefixStrategy;
import de.sopra.passwordmanager.util.strategy.ItemNamingStrategy;

import java.time.LocalDateTime;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class CredentialsItem {

    private Credentials credentials;
    private ItemNamingStrategy<Credentials> namingStrategy;

    public CredentialsItem(Credentials credentials) {
        this.credentials = credentials;
        //standardmäßig wird der name der credentials verwendet
        this.namingStrategy = creds -> creds.getName();
    }

    public void setNamingStrategy(ItemNamingStrategy<Credentials> namingStrategy) {
        if (namingStrategy != null) {
            this.namingStrategy = AdaptableNamingStrategy.asItemNamingStrategy(namingStrategy)
                    .withPrefix(new GenerateReminderPrefixStrategy());
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public CredentialsBuilder getNewBuilder(UtilityController utilityController) {
        return new CredentialsBuilder(credentials, utilityController);
    }

    public boolean hasToBeChanged() {
        if (credentials.getChangeReminderDays() == null)
            return false;
        Integer reminderDays = credentials.getChangeReminderDays();
        LocalDateTime time1 = credentials.getLastChanged().plusDays(reminderDays);
        return time1.isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return namingStrategy.getName(credentials);
    }
}