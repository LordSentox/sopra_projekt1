package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.UtilityController;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.strategy.AdaptableNamingStrategy;
import de.sopra.passwordmanager.util.strategy.GenerateReminderPrefixStrategy;
import de.sopra.passwordmanager.util.strategy.ItemNamingStrategy;

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
            this.namingStrategy = AdaptableNamingStrategy.as(namingStrategy)
                    .withPrefix(new GenerateReminderPrefixStrategy());
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public CredentialsBuilder getNewBuilder(UtilityController utilityController) {
        return new CredentialsBuilder(credentials, utilityController);
    }

    @Override
    public String toString() {
        return namingStrategy.getName(credentials);
    }
}