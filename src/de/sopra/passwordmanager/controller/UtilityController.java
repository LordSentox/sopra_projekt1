package de.sopra.passwordmanager.controller;

import aes.AES;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.EncryptedString;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Validate;
import exceptions.DecryptionException;
import exceptions.EncryptionException;
import org.passay.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.rmi.CORBA.Util;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.w3c.dom.*;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

/**
 * Der UtilityController stellt verschiedene Hilfsdienste zur Verfügung
 *
 * @author sopr049, sopr043
 */
public class UtilityController {
    public enum Charset {
        CHARSET_LOWERCASE(new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}),
        CHARSET_UPPERCASE(new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'}),
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

    /**
     * Referenz zum Passwortmanagercontroller
     */
    private PasswordManagerController passwordManagerController;

    public UtilityController(PasswordManagerController controller) {
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

    // Gibt den Index des charsets zurück, welches für den nächsten Buchstaben benutzt werden sollte.
    private int decideNextCharset(Random random, int[] missingChars) {
        int next = random.nextInt(Charset.values().length);

        // Erlaube dieses charset nur, wenn es noch benutzt werden muss oder alle charsets schon genug benutzt wurden
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
     * Generiert ein Passwort, welches den Sicherheitsanforderungen entspricht und dieses wird dann in der GUI angezeigt
     */
    // TODO: Benutze den PasswordGenerator aus der Bibliothek
    public void generatePassword(CredentialsBuilder credentials) {
        Random random = new Random();
        String password = null;
        do {
            // Setze die Passwortlänge zufällig zwischen 12 und 18
            int length = 12 + random.nextInt(6);
            System.out.println("Generating password length " + length + ".");

            // Stellt sicher, dass jedes Charset mindestens drei mal benutzt wird. Jeder Eintrag steht für die Anzahl die
            // das an dieser Stelle stehende Charset noch verwendet werden muss. z.B. Eintrag 1 für Majuskel-Charset.
            int[] missingChars = {3, 3, 3, 3};

            StringBuilder passwordBuilder = new StringBuilder();
            for (int i = 0; i < length; ++i) {
                // Das nächste Charset feststellen und ein Zeichen an das Resultat anhängen.
                int charsetNum = decideNextCharset(random, missingChars);

                passwordBuilder.append(randomCharacter(random, Charset.values()[charsetNum]));
                missingChars[charsetNum]--;
            }

            password = passwordBuilder.toString();
        } while (checkQuality(password) < 100);

        credentials.withPassword(password);
    }

    /**
     * Die Methode entschlüsselt einen eingegebenen text mit dem Masterpasswort
     *
     * @param text Der zu entschlüsselnde Text, dabei kann es sich um Passwörter oder die Sicherheitsfragen handeln
     * @return Der zurückgegebene String ist die entschlüsselte Version des eingegebenen Textes oder <code>null</code>,
     * wenn er nicht entschlüsselt werden konnte
     */
    public String decryptText(EncryptedString text) {
        return decryptText(text, passwordManagerController.getPasswordManager().getMasterPassword());
    }

    public static String decryptText(EncryptedString text, String password) {
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
     * @param text Der zu verschlüsselnde Text, dabei kann es sich um Passwörter oder die Sicherheitsfragen handeln
     * @return Der zurückgegebene String ist die verschlüsselte Version des eingegebenen Textes
     */
    public EncryptedString encryptText(String text) {
        return encryptText(text, passwordManagerController.getPasswordManager().getMasterPassword());
    }

    public static EncryptedString encryptText(String text, String password) {
        try {
            return new EncryptedString(AES.encrypt(text, password));
        } catch (EncryptionException e) {
            System.err.println("Ein Text konnte nicht verschlüsselt werden.");
            e.printStackTrace();
            return null;
        }
    }

    // Helferklasse, um die Qualität des Passwortes auf bestimmte Merkmale zu überprüfen, die unterschiedlich gewichtet
    // sind.
    private final class WeighedRule {
        private final Rule rule;
        private final double weight;

        public WeighedRule(Rule rule, double weight) {
            this.rule = rule;
            this.weight = weight;
        }

        public Rule getRule() {
            return rule;
        }

        public double getWeight() {
            return weight;
        }
    }

    // Generiert die Gesichtspunkte, nach denen ein Passwort beurteilt werden soll
    private List<WeighedRule> generateRules() {
        // Stelle sicher, dass mindestens ein kleiner und ein großer Buchstabe vorkommt
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
        LengthRule minimumLength = new LengthRule(8, 256);

        // Gibt es ein bestimmtes Doppelzeichen drei oder mehr mal?
        RepeatCharactersRule repeatCharacters = new RepeatCharactersRule(2, 3);

        return Arrays.asList(new WeighedRule(characterOrdinaryRule, 0.5),
                new WeighedRule(characterSpecialRule, 0.75),
                new WeighedRule(notAllTheSame, 0.5),
                new WeighedRule(minimumLength, 1.0),
                new WeighedRule(repeatCharacters, 0.75));
    }

    /**
     * Diese Methode überprüft die Qualität eines Passwortes und gibt eine Zahl zwischen 0 und 100 zurück ,wobei mehr besser ist
     *
     * @param text Das zu überprüfende Passwort
     * @return Es wird ein Wert von 0 bis 100 geliefert, der die Qualität des Passwortes angibt, dabei steht 0 für sehr schlecht und 100 für sehr sicher
     */
    // TODO: Sollte noch den Nutzernamen bekommen, um es mit dem Passwort zu vergleichen
    int checkQuality(String text) {
        PasswordData pwData = new PasswordData(text);

        List<WeighedRule> rules = generateRules();

        // Für jede Regel die eingehalten wird, wird das Gewicht als Wert der unangepassten Qualität hinzugefügt
        double quality = 0.0;
        for (WeighedRule rule : rules) {
            if (rule.getRule().validate(pwData).isValid()) {
                quality += rule.getWeight();
            }
        }

        // Die Qualität auf einen int im Bereich von 0 bis 100 anpassen.
        double totalWeight = 0.f;
        for (WeighedRule rule : rules) {
            totalWeight += rule.getWeight();
        }
        double percent = quality / totalWeight * 100.f;

        // Überprüfe auf Bereichsüberschreitungen und gebe den entsprechenden Wert zurück
        int wholePercent;
        final double LOWEST_POINTS = 0.5;
        final double HIGHEST_POINTS = 99.5;
        if (percent <= LOWEST_POINTS) {
            wholePercent = 0;
        } else if (percent >= HIGHEST_POINTS) {
            wholePercent = 100;
        } else {
            wholePercent = (int) percent;
        }

        return wholePercent;
    }

    /**
     * Die Methode importiert eine neü Datei mit Anmeldedaten. Für den Import wird das Masterpasswort der Datei benötigt.
     * Das Importieren einer neün Datei überschreibt die aktüllen Einträge.
     *
     * @param file           Die zu importierende Datei
     * @param decryptionPassword das Masterpasswort des zu importierenden Projektes
     * @param encryptionPassword das neue Passwort zum reencrypten, kann identisch zu decryptionPassword sein
     * @return Die Methode liefert false, wenn ein fehler beim importieren passiert, wenn true geliefert wird,
     * hat der Import funktioniert
     */
    boolean importFile(File file, String decryptionPassword, String encryptionPassword, boolean setMaster) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

            document.getDocumentElement().normalize();
            Node moepse = document.getDocumentElement();
            if (!moepse.getNodeName().equals("moepse")) {
                System.err.println("Root has wrong Name");
                return false;
            }

            // Überprüfe, ob das Masterpasswort richtig ist und breche ab, falls es fehlschlägt
            NamedNodeMap attributes = moepse.getAttributes();
            if (attributes == null) {
                System.err.println("MoePse has too few attributes");
                return false;
            }
            byte[] keySalt = parseHexBinary(attributes.getNamedItem("key-salt").getNodeValue());
            byte[] keyHash = parseHexBinary(attributes.getNamedItem("key-hash").getNodeValue());

            byte[] decryptionPasswordBytes = decryptionPassword.getBytes(StandardCharsets.UTF_8);
            byte[] enteredKeyHash = hashString(decryptionPasswordBytes, keySalt)[0];

            if (!Arrays.equals(keyHash, enteredKeyHash)) {
                System.err.println("The password entered was not correct");
                return false;
            }

            // Der Änderungswecker und die letzte Passwortänderung müssen beim Masterpasswort zwingend gesetzt sein
            LocalDateTime passwordLastChanged = LocalDateTime.parse(attributes.getNamedItem("last-changed").getNodeValue());
            String changeReminderDaysString = attributes.getNamedItem("change-reminder-days").getNodeValue();
            if (changeReminderDaysString == null) {
                System.err.println("The master password needs a change reminder");
                return false;
            }
            int changeReminderDays = Integer.parseUnsignedInt(changeReminderDaysString);

            List<Node> childNodes = IntStream.range(0, moepse.getChildNodes().getLength()).mapToObj(moepse.getChildNodes()::item).collect(Collectors.toList());

            Node treeNode = childNodes.stream().filter(node -> node.getNodeName().equals("tree")).findFirst().get();
            Node dataNode = childNodes.stream().filter(node -> node.getNodeName().equals("data")).findFirst().get();
            Validate.notNull(treeNode, "TreeNode does not exist");
            Validate.notNull(dataNode, "DataNode does not exist");

            // Die entschlüsselten Daten auslesen.
            List<CredentialsBuilder> dataList = extractCredentials(dataNode, decryptionPassword);
            Validate.notNull(dataList, "The credentials could not be read. Incorrect format");

            // Setzen des Masterpasswortes und Verschlüsselung der Daten
            // TODO: Sehr unschön, da man es bei dieser Methode nicht erwarten würde, wenn man setMasterPassword auf false setzt
            this.passwordManagerController.getPasswordManager().setMasterPassword(encryptionPassword);
            Map<String, Credentials> credentials = dataList.stream().collect(Collectors.toMap(CredentialsBuilder::getName, builder -> builder.build(this)));

            // TODO: Merge into the old MasterPasswordController instead of resetting the root
            //this.passwordManagerController.removeAll();
            extractAndFillCategories(this.passwordManagerController.getPasswordManager().getRootCategory(), treeNode, credentials);

            // Wenn das Masterpasswort neu gesetzt werden soll muss der Änderungswecker und das Erstellungsdatum im
            // Passwortmanager gesetzt werden.
            if (setMaster) {
                this.passwordManagerController.getPasswordManager().setMasterPasswordLastChanged(passwordLastChanged);
                this.passwordManagerController.getPasswordManager().setMasterPasswordReminderDays(changeReminderDays);
            }
        } catch (Exception e) {
            // TODO: Schönere Fehlerbehandlung
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void extractAndFillCategories(Category currentRoot, Node treeNode, Map<String, Credentials> credentials) {
        List<Node> children = listFromNodeList(treeNode.getChildNodes());
        for (Node child : children) {
            if (child.getNodeName().equals("category")) {
                Category newCategory = new Category(child.getAttributes().getNamedItem("name").getNodeValue());
                currentRoot.addSubCategory(newCategory);
                extractAndFillCategories(newCategory, child, credentials);
            } else if (child.getNodeName().equals("entry")) {
                Credentials entry = credentials.get(child.getAttributes().getNamedItem("name").getNodeValue());
                if (entry != null) {
                    currentRoot.addCredentials(entry);
                } else {
                    System.err.println("Could not attach entry to category. Credentials were not found");
                }
            }
        }
    }

    //Baut eine Liste der Credentials aus dem Data-Tag, oder <code>null</code> wenn ein Fehler auftritt
    private List<CredentialsBuilder> extractCredentials(Node dataNode, String decryptionPassword) {
        List<Node> entries = listFromNodeList(dataNode.getChildNodes()).stream().filter(node -> node.getNodeName().equals("entry")).collect(Collectors.toList());

        List<CredentialsBuilder> credNodes = new ArrayList<>(entries.size());
        for (Node entry : entries) {
            CredentialsBuilder credentials = extractCredentialsObject(entry, decryptionPassword);
            if (credentials != null) {
                credNodes.add(credentials);
            }
        }
        return credNodes;
    }

    private CredentialsBuilder extractCredentialsObject(Node entry, String decryptionPassword) {
        CredentialsBuilder bobTheBuilder = new CredentialsBuilder();

        // Finde das Namensattribut
        if (!entry.hasAttributes()) {
            return null;
        }
        NamedNodeMap attributes = entry.getAttributes();
        if (attributes == null) {
            return null;
        }
        Node name = attributes.getNamedItem("name");
        if (name == null) {
            return null;
        }
        bobTheBuilder.withName(name.getNodeValue());

        // Lese den Inhalt der Credentials
        List<Node> elements = listFromNodeList(entry.getChildNodes());
        for (Node element : elements) {
            switch (element.getNodeName()) {
                case "username": bobTheBuilder.withUserName(element.getTextContent()); break;
                case "website": bobTheBuilder.withWebsite(element.getTextContent()); break;
                case "created": bobTheBuilder.withCreated(LocalDateTime.parse(element.getTextContent())); break;
                case "last-changed": bobTheBuilder.withLastChanged(LocalDateTime.parse(element.getTextContent())); break;
                case "notes": bobTheBuilder.withNotes(element.getTextContent()); break;
                case "password": bobTheBuilder.withPassword(decryptText(new EncryptedString(element.getTextContent()), decryptionPassword)); break;
                case "questions": extractSecurityQuestionsIntoBuilder(element, bobTheBuilder, decryptionPassword);
            }
        }

        return bobTheBuilder;
    }

    private void extractSecurityQuestionsIntoBuilder(Node element, CredentialsBuilder bobTheBuilder, String decryptionPassword) {
        List<Node> securityQuestions = listFromNodeList(element.getChildNodes());

        // Gehe durch die Liste der Sicherheitsfragen
        for (Node securityQuestion : securityQuestions) {
            // Stelle sicher, dass es sich auch wirklich um eine Sicherheitsfrage handelt und füge sie dann zu der Liste
            // der Sicherheitsfragen hinzu, wenn dies der Fall ist.
            if (!securityQuestion.getNodeName().equals("security-question")) continue;
            if (!securityQuestion.hasAttributes()) continue;

            // Die Attribute sollten Frage und Antwort enthalten
            NamedNodeMap attributes = securityQuestion.getAttributes();
            // TODO: Hier braucht es noch Fehlerbehandlung
            String question = decryptText(new EncryptedString(attributes.getNamedItem("question").getNodeValue()), decryptionPassword);
            String answer = decryptText(new EncryptedString(attributes.getNamedItem("answer").getNodeValue()), decryptionPassword);

            bobTheBuilder.withSecurityQuestion(question, answer);
        }
    }

    private static List<Node> listFromNodeList(NodeList nodes) {
        return IntStream.range(0, nodes.getLength()).mapToObj(nodes::item).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Die Methode exportiert die aktuellen Daten in die angegebene Datei, wenn die Datei bereits etwas enthält, wird diese überschrieben
     *
     * @param file Die Datei, in welche die daten exportiert werden sollen
     * @throws IllegalArgumentException Wenn file null ist oder der Pfad nicht existiert
     */
    public void exportFile(File file) throws IllegalArgumentException {
    }

    private static final java.nio.charset.Charset SHA_CHARSET = StandardCharsets.UTF_8;

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Generiert ein zufälligen neuen Salt
     *
     * @return ein neuer Salt
     */
    private static byte[] generateRandomSalt() {
        //zu hohe Längen können die Effizienz beinträchtigen, 16 ist Standard
        final int saltLength = 16;
        byte[] salt = new byte[saltLength];

        new Random().nextBytes(salt);
        return salt;
    }

    /**
     * Generiert zu einem Eingabe String einen entsprechenden Hash.
     * Hierfür kann ein vorhandener Salt angegeben werden, falls nicht, wird ein zufälliger neuer Salt generiert.
     * Das Tupel aus Salt und Hash ergeben die Darstellung des gehashten Strings
     *
     * @param input der Eingabe String für die Hash Funktion
     * @param salt  der Salt für die Hash-Operation, wenn <code>null</code> wird ein neuer salt generiert
     * @return ein array, welche an index 0 den salt und and index 1 den gehashten String beinhaltet
     */
    private static byte[][] hashString(byte[] input, byte[] salt) {
        //wenn kein salt vorhanden, wird ein zufälliges neues salt ergänzt
        if (salt == null) {
            salt = generateRandomSalt();
        }

        //hashing vorbereiten
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512"); //algorithmus fürs hashing festlegen
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //salt setzen
        md.update(salt);

        //den Eingabe-String per SHA-512 hashen
        byte[] result = md.digest(input);

        return new byte[][]{result, salt};
    }
}