package de.sopra.passwordmanager.model;

import java.util.Collection;
import java.util.Objects;

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

    /**
     * Unsafe, aber nötig, damit etwa {@link Collection#contains(Object)} funktioniert
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EncryptedString that = (EncryptedString) object;
        return Objects.equals(encryptedContent, that.encryptedContent);
    }

    @Override
    public int hashCode() {
        return encryptedContent.hashCode();
    }

    @Override
    public String toString() {
        return "EncryptedString{" + encryptedContent + "}";
    }
}
