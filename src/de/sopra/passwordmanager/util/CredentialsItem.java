package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.UtilityController;
import de.sopra.passwordmanager.model.Credentials;

import java.util.function.Function;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class CredentialsItem {

    private Credentials credentials;
    private Function<Credentials, String> namingStrategy;

    public CredentialsItem(Credentials credentials) {
        this.credentials = credentials;
        //standardmäßig wird der name der credentials verwendet
        this.namingStrategy = creds -> creds.getName();
    }

    public void setNamingStrategy(Function<Credentials, String> namingStrategy) {
        this.namingStrategy = namingStrategy == null ? this.namingStrategy : namingStrategy;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public CredentialsBuilder getNewBuilder(UtilityController utilityController) {
        return new CredentialsBuilder(credentials, utilityController);
    }

    @Override
    public String toString() {
        return namingStrategy.apply(credentials);
    }
}