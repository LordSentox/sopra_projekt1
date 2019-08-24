package de.sopra.passwordmanager.util;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 23.08.2019
 * @since 23.08.2019
 */
public final class EncryptedString {

    private final String encryptedContent;

    public EncryptedString(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

}
