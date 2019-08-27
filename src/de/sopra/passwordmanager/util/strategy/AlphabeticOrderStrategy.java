package de.sopra.passwordmanager.util.strategy;

import de.sopra.passwordmanager.util.CredentialsItem;

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
    public List<CredentialsItem> order(List<CredentialsItem> credentials) {
        LinkedList<CredentialsItem> list = new LinkedList<>(credentials);
        list.sort(Comparator.comparing(o -> o.getCredentials().getName()));
        return list;
    }

}