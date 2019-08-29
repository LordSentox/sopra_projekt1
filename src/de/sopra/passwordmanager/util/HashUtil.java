package de.sopra.passwordmanager.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

public class HashUtil {
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static byte[] hexToBytes(String hexString) {
        return parseHexBinary(hexString);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int currentByte = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[currentByte >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[currentByte & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Generiert ein zufälligen neuen Salt
     *
     * @return ein neuer Salt
     */
    public static byte[] generateRandomSalt() {
        //zu hohe Längen können die Effizienz beinträchtigen, 16 ist Standard
        final int saltLength = 16;
        byte[] salt = new byte[saltLength];

        new Random().nextBytes(salt);
        return salt;
    }

    // Generiert aus dem input und dem salt einen mit SHA-512 verschlüsselten Hash und gibt ihn, sowie den salt zurück.
    // Wird kein Salt übergeben wird ein zufälliger generiert.
    public static byte[][] hashString(byte[] input, byte[] salt) {
        //wenn kein salt vorhanden, wird ein zufälliges neues salt ergänzt
        if (salt == null) {
            salt = generateRandomSalt();
        }

        //hashing vorbereiten
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512"); //algorithmus fürs hashing festlegen
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //salt setzen
        messageDigest.update(salt);

        //den Eingabe-String per SHA-512 hashen
        byte[] result = messageDigest.digest(input);

        return new byte[][]{result, salt};
    }
}
