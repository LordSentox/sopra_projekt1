package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.EncryptedString;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.Validate;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

public class IOController {
    private PasswordManagerController passwordManagerController;

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    IOController(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
    }

    /**
     * Die Methode importiert eine neü Datei mit Anmeldedaten. Für den Import wird das Masterpasswort der Datei benötigt.
     * Das Importieren einer neün Datei überschreibt die aktüllen Einträge.
     *
     * @param file               Die zu importierende Datei
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
            if (!checkMoepseAttributes(decryptionPassword, attributes)) {
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

            extractXMLContents(moepse, decryptionPassword, encryptionPassword);

            // Wenn das Masterpasswort neu gesetzt werden soll muss der Änderungswecker und das Erstellungsdatum im
            // Passwortmanager gesetzt werden.
            if (setMaster) {
                this.passwordManagerController.getPasswordManager().setMasterPasswordLastChanged(passwordLastChanged);
                this.passwordManagerController.getPasswordManager().setMasterPasswordReminderDays(changeReminderDays);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void extractXMLContents(Node moepse, String decryptionPassword, String encryptionPassword) {
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
        Map<String, Credentials> credentials = dataList.stream().collect(Collectors.toMap(CredentialsBuilder::getName, builder -> builder.build(passwordManagerController.getUtilityController())));

        // TODO: Merge into the old MasterPasswordController instead of resetting the root
        this.passwordManagerController.getPasswordManager().clearAll();
        extractAndFillCategories(this.passwordManagerController.getPasswordManager().getRootCategory(), treeNode, credentials);
    }

    private static boolean checkMoepseAttributes(String decryptionPassword, NamedNodeMap attributes) {
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
        if (!extractNameToBuilder(entry, bobTheBuilder)) return null;

        // Lese den Inhalt der Credentials
        List<Node> elements = listFromNodeList(entry.getChildNodes());
        for (Node element : elements) {
            updateBuilderFromElement(element, bobTheBuilder, decryptionPassword);
        }

        return bobTheBuilder;
    }

    private void updateBuilderFromElement(Node element, CredentialsBuilder bobTheBuilder, String decryptionPassword) {
        switch (element.getNodeName()) {
            case "username":
                bobTheBuilder.withUserName(element.getTextContent());
                break;
            case "website":
                bobTheBuilder.withWebsite(element.getTextContent());
                break;
            case "created":
                bobTheBuilder.withCreated(LocalDateTime.parse(element.getTextContent()));
                break;
            case "last-changed":
                bobTheBuilder.withLastChanged(LocalDateTime.parse(element.getTextContent()));
                break;
            case "notes":
                bobTheBuilder.withNotes(element.getTextContent());
                break;
            case "change-reminder-days":
                bobTheBuilder.withChangeReminderDays(Integer.parseUnsignedInt(element.getTextContent()));
                break;
            case "password":
                bobTheBuilder.withPassword(UtilityController.decryptText(new EncryptedString(element.getTextContent()), decryptionPassword));
                break;
            case "questions":
                extractSecurityQuestionsIntoBuilder(element, bobTheBuilder, decryptionPassword);
        }
    }

    // Versucht das Namensattribut des entrys auszulesen und es im Builder als Namen zu speichern. Gelingt es, gibt die
    // Funktion true zurück, sonst false
    private static boolean extractNameToBuilder(Node entry, CredentialsBuilder bobTheBuilder) {
        // Finde das Namensattribut
        if (!entry.hasAttributes()) {
            return false;
        }
        NamedNodeMap attributes = entry.getAttributes();
        if (attributes == null) {
            return false;
        }
        Node name = attributes.getNamedItem("name");
        if (name == null) {
            return false;
        }
        bobTheBuilder.withName(name.getNodeValue());
        return true;
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
            String question = UtilityController.decryptText(new EncryptedString(attributes.getNamedItem("question").getNodeValue()), decryptionPassword);
            String answer = UtilityController.decryptText(new EncryptedString(attributes.getNamedItem("answer").getNodeValue()), decryptionPassword);

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
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            // moepse-tag generieren und als root benutzen. Die notwendigen Attribute werden hier direkt aus dem
            // Passwordmanager gesetzt.
            Element moepse = initMoepseTag(document, passwordManagerController);

            // Den Tag für die Kategorien und den Tag in dem die Daten gespeichert werden erstellen
            Element tree = document.createElement("tree");
            Element data = document.createElement("data");
            moepse.appendChild(tree);
            moepse.appendChild(data);

            // Lege eine HashMap an, in der alle Credentials gespeichert werden, sodass sie nachdem die Kategorien
            // geschrieben wurden problemlos abspeicherbar sind.
            Map<String, Credentials> credentials = new HashMap<>();
            writeCategories(passwordManagerController.getPasswordManager().getRootCategory(), tree, credentials, document);

            // Speichere alle Credentials, die vorkommen
            credentials.values().forEach(cred -> writeCredentials(cred, data, document));

            // Speichere die Datei am vorgegebenen Ort
            writeDocumentToFile(document, file);
        } catch (Exception e) {
            e.printStackTrace();
            passwordManagerController.getMainWindowAUI().showError("Datei konnte nicht exportiert werden. Pech gehabt.");
        }
    }

    private static Element initMoepseTag(Document document, PasswordManagerController passwordManagerController) {
        Element moepse = document.createElement("moepse");
        document.appendChild(moepse);

        byte[][] hashAndSalt = hashString(passwordManagerController.getPasswordManager().getMasterPassword().getBytes(StandardCharsets.UTF_8), null);
        String salt = bytesToHex(hashAndSalt[1]);
        String hash = bytesToHex(hashAndSalt[0]);
        LocalDateTime lastChanged = passwordManagerController.getPasswordManager().getMasterPasswordLastChanged();
        int changeReminder = passwordManagerController.getPasswordManager().getMasterPasswordReminderDays();

        moepse.setAttribute("key-salt", salt);
        moepse.setAttribute("key-hash", hash);
        if (lastChanged != null) {
            moepse.setAttribute("last-changed", lastChanged.toString());
        }
        moepse.setAttribute("change-reminder-days", Integer.toString(changeReminder));
        return moepse;
    }

    private static void writeDocumentToFile(Document document, File file) throws TransformerException {
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(domSource, streamResult);
    }

    private static void writeCredentials(Credentials credentials, Element data, Document document) {
        Element entry = document.createElement("entry");
        data.appendChild(entry);
        entry.setAttribute("name", credentials.getName());

        addTextTagChild(entry, "username", credentials.getUserName(), document);
        addTextTagChild(entry, "password", credentials.getPassword().getEncryptedContent(), document);
        addTextTagChild(entry, "website", credentials.getWebsite(), document);
        addTextTagChild(entry, "created", credentials.getCreatedAt().toString(), document);
        addTextTagChild(entry, "last-changed", credentials.getLastChanged().toString(), document);
        if (credentials.getChangeReminderDays() != null) {
            addTextTagChild(entry, "change-reminder-days", credentials.getChangeReminderDays().toString(), document);
        }
        if (credentials.getNotes() != null) {
            addTextTagChild(entry, "notes", credentials.getNotes(), document);
        }

        Element securityQuestions = document.createElement("questions");
        entry.appendChild(securityQuestions);
        for (SecurityQuestion securityQuestion : credentials.getSecurityQuestions()) {
            Element securityQuestionElement = document.createElement("security-question");
            securityQuestions.appendChild(securityQuestionElement);
            securityQuestionElement.setAttribute("question", securityQuestion.getQuestion().getEncryptedContent());
            securityQuestionElement.setAttribute("answer", securityQuestion.getAnswer().getEncryptedContent());
        }
    }

    private static void addTextTagChild(Element entry, String tag, String content, Document document) {
        Element textTag = document.createElement(tag);
        entry.appendChild(textTag);
        textTag.setTextContent(content);
    }

    // Schreibt die in Kategorie enthaltenen Einträge und Unterkategorien rekursiv in einen XML-Baum
    private static void writeCategories(Category currentCategory, Element currentRoot, Map<String, Credentials> credentials, Document document) {
        // Hinzufügen von Credentials, falls sie noch nicht in der Map vorkommen.
        for (Credentials cred : currentCategory.getCredentials()) {
            credentials.put(cred.getName(), cred);
            Element entry = document.createElement("entry");
            entry.setAttribute("name", cred.getName());
            currentRoot.appendChild(entry);
        }

        // writeCategories für die Unterkategorien aufrufen
        for (Category category : currentCategory.getSubCategories()) {
            Element child = document.createElement("category");
            child.setAttribute("name", category.getName());
            currentRoot.appendChild(child);

            writeCategories(category, child, credentials, document);
        }
    }

    private static String bytesToHex(byte[] bytes) {
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
    private static byte[] generateRandomSalt() {
        //zu hohe Längen können die Effizienz beinträchtigen, 16 ist Standard
        final int saltLength = 16;
        byte[] salt = new byte[saltLength];

        new Random().nextBytes(salt);
        return salt;
    }

    // Generiert aus dem input und dem salt einen mit SHA-512 verschlüsselten Hash und gibt ihn, sowie den salt zurück.
    // Wird kein Salt übergeben wird ein zufälliger generiert.
    private static byte[][] hashString(byte[] input, byte[] salt) {
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
