package de.sopra.passwordmanager.util;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 26.08.2019
 * @since 26.08.2019
 */
public class ValidationUtil {

    public static void notNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    public static void notEmpty(String string, String errorMessage) {
        if (string.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void notEmptyOrNull(String string, String stringName) {
        notNull(string, stringName + " is null");
        notEmpty(string, stringName + "is empty");
    }

}