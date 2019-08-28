package de.sopra.passwordmanager.util.strategy;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 28.08.2019
 * @since 28.08.2019
 */
public interface ItemNamingStrategy<Type> {

    String getName(Type item);

}