package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.SecurityQuestion;
import de.sopra.passwordmanager.util.HashUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Exporter {
    private PasswordManagerController passwordManagerController;

    private Document document;

    public Exporter(PasswordManagerController passwordManagerController) {
        this.passwordManagerController = passwordManagerController;
        this.document = null;
    }

    /**
     * Die Methode exportiert die aktuellen Daten in die angegebene Datei, wenn die Datei bereits etwas enthält, wird diese überschrieben
     *
     * @param file Die Datei, in welche die daten exportiert werden sollen
     * @throws IllegalArgumentException Wenn file null ist oder der Pfad nicht existiert
     */
    void exportFile(File file) throws IllegalArgumentException {
        try {
            this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            // moepse-tag generieren und als root benutzen. Die notwendigen Attribute werden hier direkt aus dem
            // Passwordmanager gesetzt.
            Element moepse = initMoepseTag();

            // Den Tag für die Kategorien und den Tag in dem die Daten gespeichert werden erstellen
            Element tree = this.document.createElement("tree");
            Element data = this.document.createElement("data");
            moepse.appendChild(tree);
            moepse.appendChild(data);

            // Lege eine HashMap an, in der alle Credentials gespeichert werden, sodass sie nachdem die Kategorien
            // geschrieben wurden problemlos abspeicherbar sind.
            Map<String, Credentials> credentials = new HashMap<>();
            writeCategories(passwordManagerController.getPasswordManager().getRootCategory(), tree, credentials);

            // Speichere alle Credentials, die vorkommen
            credentials.values().forEach(cred -> writeCredentials(cred, data));

            // Speichere die Datei am vorgegebenen Ort
            writeDocumentToFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            passwordManagerController.getMainWindowAUI().showError("Datei konnte nicht exportiert werden. Pech gehabt.");
        }
    }

    private Element initMoepseTag() {
        Element moepse = this.document.createElement("moepse");
        this.document.appendChild(moepse);

        byte[][] hashAndSalt = HashUtil.hashString(passwordManagerController.getPasswordManager().getMasterPassword().getBytes(StandardCharsets.UTF_8), null);
        String salt = HashUtil.bytesToHex(hashAndSalt[1]);
        String hash = HashUtil.bytesToHex(hashAndSalt[0]);
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

    private void writeDocumentToFile(File file) throws TransformerException {
        DOMSource domSource = new DOMSource(this.document);
        StreamResult streamResult = new StreamResult(file);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(domSource, streamResult);
    }

    private void writeCredentials(Credentials credentials, Element data) {
        Element entry = this.document.createElement("entry");
        data.appendChild(entry);
        entry.setAttribute("name", credentials.getName());

        addTextTagChild(entry, "username", credentials.getUserName());
        addTextTagChild(entry, "password", credentials.getPassword().getEncryptedContent());
        addTextTagChild(entry, "website", credentials.getWebsite());
        addTextTagChild(entry, "created", credentials.getCreatedAt().toString());
        addTextTagChild(entry, "last-changed", credentials.getLastChanged().toString());
        if (credentials.getChangeReminderDays() != null) {
            addTextTagChild(entry, "change-reminder-days", credentials.getChangeReminderDays().toString());
        }
        if (credentials.getNotes() != null) {
            addTextTagChild(entry, "notes", credentials.getNotes());
        }

        Element securityQuestions = this.document.createElement("questions");
        entry.appendChild(securityQuestions);
        for (SecurityQuestion securityQuestion : credentials.getSecurityQuestions()) {
            Element securityQuestionElement = this.document.createElement("security-question");
            securityQuestions.appendChild(securityQuestionElement);
            securityQuestionElement.setAttribute("question", securityQuestion.getQuestion().getEncryptedContent());
            securityQuestionElement.setAttribute("answer", securityQuestion.getAnswer().getEncryptedContent());
        }
    }

    private void addTextTagChild(Element entry, String tag, String content) {
        Element textTag = this.document.createElement(tag);
        entry.appendChild(textTag);
        textTag.setTextContent(content);
    }

    // Schreibt die in Kategorie enthaltenen Einträge und Unterkategorien rekursiv in einen XML-Baum
    private void writeCategories(Category currentCategory, Element currentRoot, Map<String, Credentials> credentials) {
        // Hinzufügen von Credentials, falls sie noch nicht in der Map vorkommen.
        for (Credentials cred : currentCategory.getCredentials()) {
            credentials.put(cred.getName(), cred);
            Element entry = this.document.createElement("entry");
            entry.setAttribute("name", cred.getName());
            currentRoot.appendChild(entry);
        }

        // writeCategories für die Unterkategorien aufrufen
        for (Category category : currentCategory.getSubCategories()) {
            Element child = this.document.createElement("category");
            child.setAttribute("name", category.getName());
            currentRoot.appendChild(child);

            writeCategories(category, child, credentials);
        }
    }
}
