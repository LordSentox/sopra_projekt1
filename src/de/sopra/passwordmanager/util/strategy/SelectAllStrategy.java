package de.sopra.passwordmanager.util.strategy;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class SelectAllStrategy implements EntryListSelectionStrategy {

    @Override
    public List<CredentialsItem> select(List<Credentials> credentials) {
        return credentials.stream().map(CredentialsItem::new).collect(Collectors.toList());
    }
}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/