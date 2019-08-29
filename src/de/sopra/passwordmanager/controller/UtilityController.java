package de.sopra.passwordmanager.controller;

import aes.AES;
import de.sopra.passwordmanager.model.EncryptedString;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import exceptions.DecryptionException;
import exceptions.EncryptionException;
import org.passay.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Der UtilityController stellt verschiedene Hilfsdienste zur Verfügung
 *
 * @author sopr049, sopr043
 */
public class UtilityController {
    public enum Charset {
        CHARSET_LOWERCASE(new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}),
        CHARSET_UPPERCASE(new char[]{'A', 'B', 'C', 'D',
                'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z'}),
        CHARSET_NUMBER(new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}),
        CHARSET_SPECIAL(new char[]{'$', '!', '^', '@', '?', '#', '[', '&', '{', '}', '(', '=', '*', ')', '+', ']'});

        private char[] chars;

        Charset(char[] chars) {
            this.chars = chars;
        }

        char[] getChars() {
            return this.chars;
        }
    }

    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    /**
     * Referenz zum Passwortmanagercontroller
     */
    private PasswordManagerController passwordManagerController;

    UtilityController(PasswordManagerController controller) {
        this.passwordManagerController = controller;
    }

    // Überprüfe ob für alle Elemente <= 0 gilt.
    private boolean allZeroOrLess(int[] values) {
        for (int value : values) {
            if (value > 0) {
                return false;
            }
        }

        return true;
    }

    // Gibt den Index des charsets zurück, welches für den nächsten Buchstaben
    // benutzt werden sollte.
    private int decideNextCharset(Random random, int[] missingChars) {
        int next = random.nextInt(Charset.values().length);

        // Erlaube dieses charset nur, wenn es noch benutzt werden muss oder
        // alle charsets schon genug benutzt wurden
        boolean accepted = false;
        while (!accepted) {
            if (missingChars[next] > 0) {
                accepted = true;
            } else if (allZeroOrLess(missingChars)) {
                accepted = true;
            } else {
                next = random.nextInt(Charset.values().length);
            }
        }

        return next;
    }

    private char randomCharacter(Random random, Charset charset) {
        return charset.getChars()[random.nextInt(charset.getChars().length)];
    }

    /**
     * Generiert ein Passwort, welches den Sicherheitsanforderungen entspricht
     * und dieses wird dann in der GUI angezeigt
     */
    // TODO: Benutze den PasswordGenerator aus der Bibliothek
    public void generatePassword(CredentialsBuilder credentials) {
        Random random = new Random();
        String password = null;
        do {
            // Setze die Passwortlänge zufällig zwischen 12 und 18
            int length = 16 + random.nextInt(6);
            System.out.println("Generating password length " + length + ".");

            // Stellt sicher, dass jedes Charset mindestens drei mal benutzt
            // wird. Jeder Eintrag steht für die Anzahl die
            // das an dieser Stelle stehende Charset noch verwendet werden muss.
            // z.B. Eintrag 1 für Majuskel-Charset.
            int[] missingChars = {3, 3, 3, 3};

            StringBuilder passwordBuilder = new StringBuilder();
            for (int i = 0; i < length; ++i) {
                // Das nächste Charset feststellen und ein Zeichen an das
                // Resultat anhängen.
                int charsetNum = decideNextCharset(random, missingChars);

                passwordBuilder.append(randomCharacter(random, Charset.values()[charsetNum]));
                missingChars[charsetNum]--;
            }

            password = passwordBuilder.toString();
        } while (checkQuality(password, null) < 100);

        credentials.withPassword(password);
        passwordManagerController.getMainWindowAUI().refreshEntry();
        passwordManagerController.getMainWindowAUI().refreshEntryPasswordQuality(100);
    }

    /**
     * Die Methode entschlüsselt einen eingegebenen text mit dem Masterpasswort
     *
     * @param text Der zu entschlüsselnde Text, dabei kann es sich um Passwörter
     *             oder die Sicherheitsfragen handeln
     * @return Der zurückgegebene String ist die entschlüsselte Version des
     * eingegebenen Textes oder <code>null</code>, wenn er nicht
     * entschlüsselt werden konnte
     */
    public String decryptText(EncryptedString text) {
        return decryptText(text, passwordManagerController.getPasswordManager().getMasterPassword());
    }

    static String decryptText(EncryptedString text, String password) {
        try {
            return AES.decrypt(text.getEncryptedContent(), password);
        } catch (DecryptionException e) {
            System.err.println(text.getEncryptedContent() + " konnte nicht entschlüsselt werden.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Die Methode verschlüsselt einen eingegebenen text mit dem Masterpasswort
     *
     * @param text Der zu verschlüsselnde Text, dabei kann es sich um Passwörter
     *             oder die Sicherheitsfragen handeln
     * @return Der zurückgegebene String ist die verschlüsselte Version des
     * eingegebenen Textes
     */
    public EncryptedString encryptText(String text) {
        return encryptText(text, passwordManagerController.getPasswordManager().getMasterPassword());
    }

    private static EncryptedString encryptText(String text, String password) {
        try {
            return new EncryptedString(AES.encrypt(text, password));
        } catch (EncryptionException e) {
            System.err.println("Ein Text konnte nicht verschlüsselt werden.");
            e.printStackTrace();
            return null;
        }
    }

    // Helferklasse, um die Qualität des Passwortes auf bestimmte Merkmale zu
    // überprüfen, die unterschiedlich gewichtet
    // sind.
    private static final class WeighedRule {
        private final Rule rule;
        private final double weight;

        WeighedRule(Rule rule, double weight) {
            this.rule = rule;
            this.weight = weight;
        }

        Rule getRule() {
            return rule;
        }

        double getWeight() {
            return weight;
        }
    }

    // Generiert die Gesichtspunkte, nach denen ein Passwort beurteilt werden
    // soll
    private static List<WeighedRule> generateRules(boolean checkUsername) {
        // Stelle sicher, dass mindestens ein kleiner und ein großer Buchstabe
        // vorkommt
        CharacterCharacteristicsRule characterOrdinaryRule = new CharacterCharacteristicsRule();
        characterOrdinaryRule.getRules().add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        characterOrdinaryRule.getRules().add(new CharacterRule(EnglishCharacterData.UpperCase, 1));

        // Höher gewertete Zeichengruppen sind Sonderzeichen und Zahlen
        CharacterCharacteristicsRule characterSpecialRule = new CharacterCharacteristicsRule();
        characterSpecialRule.getRules().add(new CharacterRule(EnglishCharacterData.Special, 1));
        characterSpecialRule.getRules().add(new CharacterRule(EnglishCharacterData.Digit, 1));

        // Es soll nicht zu oft der gleiche Buchstabe benutzt werden.
        CharacterOccurrencesRule notAllTheSame = new CharacterOccurrencesRule(3);

        // Stelle sicher, dass die Länge nicht zu kurz ist.
        LengthRule minimumLength = new LengthRule(MINIMUM_PASSWORD_LENGTH, 256);

        // Gibt es ein bestimmtes Doppelzeichen drei oder mehr mal?
        RepeatCharactersRule repeatCharacters = new RepeatCharactersRule(2, 3);

        //Gibt es wiederholte regulaere Ausdruecke?
        RepeatCharacterRegexRule regex = new RepeatCharacterRegexRule(3);

        List<WeighedRule> list = new ArrayList<>(Arrays.asList(new WeighedRule(characterOrdinaryRule, 1.0),
                new WeighedRule(characterSpecialRule, 2.5),
                new WeighedRule(notAllTheSame, 0.75),
                new WeighedRule(minimumLength, 0.5),
                new WeighedRule(repeatCharacters, 0.5),
                new WeighedRule(regex, 1.25)));
        if (checkUsername) {
            // Stelle sicher, dass im Passwort wenig alphabetische Sequenzen
            // vorkomen.
            UsernameRule username = new UsernameRule();
            list.add(new WeighedRule(username, 5.0));
        }
        return list;
    }

    /**
     * Diese Methode überprüft die Qualität eines Passwortes und gibt eine Zahl zwischen 0 und 100 zurück ,wobei mehr besser ist
     *
     * @param  Das zu überprüfende Passwort
     * @return Es wird ein Wert von 0 bis 100 geliefert, der die Qualität des Passwortes angibt, dabei steht 0 für sehr schlecht und 100 für sehr sicher
     */
    // TODO: Sollte noch den Nutzernamen bekommen, um es mit dem Passwort zu vergleichen
    int checkQuality(String password, String username) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        String text = new String();
        char previous = password.charAt(password.length()-1);
        
        for(char current : password.toCharArray()) {
            if (current != previous) {
                text = text + current;
                previous = current;
            }
        }
        
        PasswordData pwData = new PasswordData(text);
        boolean checkUsername = username != null;
        if (checkUsername) {
            pwData.setUsername(username);
        }
        double ofOne = calculateRuleAdherence(pwData, checkUsername);

        // Die Punkte mit der Länge normalisieren, um auf eine bessere Qualitätsanzeige zu kommen
        int thresholdLength = 16;
        if (text.length() <= thresholdLength) {
            ofOne *= (double) text.length() / (double) thresholdLength;
        }

        // Überprüfe auf Bereichsüberschreitungen und gebe den entsprechenden Wert zurück
        if (text.length() < MINIMUM_PASSWORD_LENGTH) {
            ofOne /= 1.2;
        }

        return scaleToWholePercent(ofOne);

    }

    private static double calculateRuleAdherence(PasswordData pwData, boolean checkUsername) {
        List<WeighedRule> rules = generateRules(checkUsername);
        // Für jede Regel die eingehalten wird, wird das Gewicht als Wert der unangepassten Qualität hinzugefügt
        double quality = 0.0;
        for (WeighedRule rule : rules) {
            if (rule.getRule().validate(pwData).isValid()) {
                quality += rule.getWeight();
            }
        }

        // Die Qualität auf einen int im Bereich von 0 bis 100 anpassen.
        double totalWeight = 0.0;
        for (WeighedRule rule : rules) {
            totalWeight += rule.getWeight();
        }
        return quality / totalWeight;
    }

    private static int scaleToWholePercent(double ofOne) {
        double ofHundred = ofOne * 100.0;

        int wholePercent;
        final double LOWEST_POINTS = 0.005;
        final double HIGHEST_POINTS = 0.995;
        if (ofOne <= LOWEST_POINTS) {
            wholePercent = 0;
        } else if (ofOne >= HIGHEST_POINTS) {
            wholePercent = 100;
        } else {
            wholePercent = (int) ofHundred;
        }
        return wholePercent;
    }
}