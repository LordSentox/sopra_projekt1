package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.EncryptedString;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.HashUtil;
import de.sopra.passwordmanager.util.ValidationUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

class Importer {
    private PasswordManagerController passwordManagerController;

    private final File file;
    private final String decryptionPassword;

    /**
     * @param file               Die zu importierende Datei
     * @param decryptionPassword das Masterpasswort des zu importierenden Projektes
     *
     */
    Importer(File file, String decryptionPassword, PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
        this.file = file;
        this.decryptionPassword = decryptionPassword;
    }

    /**
     * Die Methode importiert eine neü Datei mit Anmeldedaten. Für den Import wird das Masterpasswort der Datei benötigt.
     * Das Importieren einer neün Datei überschreibt die aktüllen Einträge.
     *
     * @param encryptionPassword das neue Passwort zum reencrypten, kann identisch zu decryptionPassword sein
     * @return Die Methode liefert false, wenn ein fehler beim importieren passiert, wenn true geliefert wird,
     * hat der Import funktioniert
     */
    boolean importFile(String encryptionPassword, boolean setMaster) {
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
            if (!checkMoepseAttributes(attributes)) {
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

            extractXMLContents(moepse, encryptionPassword);

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

    private void extractXMLContents(Node moepse, String encryptionPassword) {
        List<Node> childNodes = IntStream.range(0, moepse.getChildNodes().getLength()).mapToObj(moepse.getChildNodes()::item).collect(Collectors.toList());

        Node treeNode = childNodes.stream().filter(node -> node.getNodeName().equals("tree")).findFirst().get();
        Node dataNode = childNodes.stream().filter(node -> node.getNodeName().equals("data")).findFirst().get();
        ValidationUtil.notNull(treeNode, "TreeNode does not exist");
        ValidationUtil.notNull(dataNode, "DataNode does not exist");

        // Die entschlüsselten Daten auslesen.
        List<CredentialsBuilder> dataList = extractCredentials(dataNode);
        ValidationUtil.notNull(dataList, "The credentials could not be read. Incorrect format");

        // Setzen des Masterpasswortes und Verschlüsselung der Daten
        this.passwordManagerController.getPasswordManager().setMasterPassword(encryptionPassword);
        Map<String, Credentials> credentials = dataList.stream().collect(Collectors.toMap(CredentialsBuilder::getName, builder -> builder.build(passwordManagerController.getUtilityController())));

        this.passwordManagerController.getPasswordManager().clearAll();
        extractAndFillCategories(this.passwordManagerController.getPasswordManager().getRootCategory(), treeNode, credentials);
    }

    private boolean checkMoepseAttributes(NamedNodeMap attributes) {
        if (attributes == null) {
            System.err.println("MoePse has too few attributes");
            return false;
        }

        byte[] keySalt = parseHexBinary(attributes.getNamedItem("key-salt").getNodeValue());
        byte[] keyHash = parseHexBinary(attributes.getNamedItem("key-hash").getNodeValue());

        byte[] decryptionPasswordBytes = decryptionPassword.getBytes(StandardCharsets.UTF_8);
        byte[] enteredKeyHash = HashUtil.hashString(decryptionPasswordBytes, keySalt)[0];

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
    private List<CredentialsBuilder> extractCredentials(Node dataNode) {
        List<Node> entries = listFromNodeList(dataNode.getChildNodes()).stream().filter(node -> node.getNodeName().equals("entry")).collect(Collectors.toList());

        List<CredentialsBuilder> credNodes = new ArrayList<>(entries.size());
        for (Node entry : entries) {
            CredentialsBuilder credentials = extractCredentialsObject(entry);
            if (credentials != null) {
                credNodes.add(credentials);
            }
        }
        return credNodes;
    }

    private CredentialsBuilder extractCredentialsObject(Node entry) {
        CredentialsBuilder bobTheBuilder = new CredentialsBuilder();
        if (!extractNameToBuilder(entry, bobTheBuilder)) return null;

        // Lese den Inhalt der Credentials
        List<Node> elements = listFromNodeList(entry.getChildNodes());
        for (Node element : elements) {
            updateBuilderFromElement(element, bobTheBuilder);
        }

        return bobTheBuilder;
    }

    private void updateBuilderFromElement(Node element, CredentialsBuilder bobTheBuilder) {
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
                extractSecurityQuestionsIntoBuilder(element, bobTheBuilder);
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

    private void extractSecurityQuestionsIntoBuilder(Node element, CredentialsBuilder bobTheBuilder) {
        List<Node> securityQuestions = listFromNodeList(element.getChildNodes());

        // Gehe durch die Liste der Sicherheitsfragen
        for (Node securityQuestion : securityQuestions) {
            // Stelle sicher, dass es sich auch wirklich um eine Sicherheitsfrage handelt und füge sie dann zu der Liste
            // der Sicherheitsfragen hinzu, wenn dies der Fall ist.
            if (!securityQuestion.getNodeName().equals("security-question")) continue;
            if (!securityQuestion.hasAttributes()) continue;

            // Die Attribute sollten Frage und Antwort enthalten
            NamedNodeMap attributes = securityQuestion.getAttributes();

            String question = UtilityController.decryptText(new EncryptedString(attributes.getNamedItem("question").getNodeValue()), decryptionPassword);
            String answer = UtilityController.decryptText(new EncryptedString(attributes.getNamedItem("answer").getNodeValue()), decryptionPassword);

            bobTheBuilder.withSecurityQuestion(question, answer);
        }
    }

    private static List<Node> listFromNodeList(NodeList nodes) {
        return IntStream.range(0, nodes.getLength()).mapToObj(nodes::item).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
