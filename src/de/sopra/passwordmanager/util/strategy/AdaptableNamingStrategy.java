package de.sopra.passwordmanager.util.strategy;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 28.08.2019
 * @since 28.08.2019
 */
public abstract class AdaptableNamingStrategy<T> implements ItemNamingStrategy<T> {

    public static <A> AdaptableNamingStrategy<A> as(ItemNamingStrategy<A> strategy) {
        return new AdaptableNamingStrategy<A>() {
            @Override
            public String getName(A item) {
                return strategy.getName(item);
            }
        };
    }

    public AdaptableNamingStrategy<T> withPrefix(ItemNamingStrategy<T> prefixGenerator) {
        final AdaptableNamingStrategy<T> strategy = this;
        return new AdaptableNamingStrategy<T>() {
            @Override
            public String getName(T item) {
                return prefixGenerator.getName(item) + strategy.getName(item);
            }
        };
    }

}