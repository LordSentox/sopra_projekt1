package de.sopra.passwordmanager.util.strategy;

import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsItem;
import de.sopra.passwordmanager.util.PatternSyntax;

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
        List<CredentialsItem> items = credentials.stream().map(CredentialsItem::new).collect(Collectors.toList());
        items.forEach(item -> item.setNamingStrategy(PatternSyntax.PatternSyntaxFilter.NAME));
        return items;
    }
}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/