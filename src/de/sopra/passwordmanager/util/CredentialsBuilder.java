package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.UtilityController;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.EncryptedString;
import de.sopra.passwordmanager.model.SecurityQuestion;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Builder für {@link Credentials}. Attribute sind gleich denen von {@link Credentials}
 *
 * @see Credentials
 */
public class CredentialsBuilder {

    private String name = "";
    private String userName = "";
    private String password = "";
    private String website = "";
    private Integer changeReminderDays = null;
    private LocalDateTime lastChanged = null;
    private LocalDateTime created = null;
    private String notes = "";
    private Map<String, String> securityQuestions = new HashMap<>();

    /**
     * Erstellt einen {@link CredentialsBuilder} für {@link Credentials}, der keine Daten enthält, eingeschlossen der Daten,
     * die in jedem Fall benötigt sind.
     *
     * @see #build(UtilityController)
     */
    public CredentialsBuilder() {
    }

    /**
     * Erstellt einen {@link CredentialsBuilder} für {@link Credentials}, der die in jedem Fall benötigten Daten enthält.
     *
     * @param name     Name des Credentials-Objektes
     * @param userName Nutzername zur Anmeldung auf der Netzseite
     * @param password Passwort zur Anmeldung auf der Netzseite
     * @param website  Netzseite, auf die die Anmeldedaten zutreffen
     * @see #build(UtilityController)
     */
    public CredentialsBuilder(String name, String userName, String password, String website) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.website = website;
    }

    /**
     * Erstellt einen {@link CredentialsBuilder} für {@link Credentials}, der alle Daten, der gegebenen Credentials enthält.
     *
     * @see #build(UtilityController)
     */
    public CredentialsBuilder(Credentials cred, UtilityController utilityController) {
        this.name = cred.getName();
        this.userName = cred.getUserName();
        this.password = utilityController.decryptText(cred.getPassword());
        this.website = cred.getWebsite();
        this.changeReminderDays = cred.getChangeReminderDays();
        this.created = cred.getCreatedAt();
        this.lastChanged = cred.getLastChanged();
        this.notes = cred.getNotes();
        this.securityQuestions = new HashMap<>();
        cred.getSecurityQuestions().forEach(securityQuestion -> {
            String decryptedQuestion = utilityController.decryptText(securityQuestion.getQuestion());
            String decryptedAnswer = utilityController.decryptText(securityQuestion.getAnswer());
            securityQuestions.put(decryptedQuestion, decryptedAnswer);
        });
    }

    /**
     * Baut aus den hinzugefügte Daten ein {@link Credentials} Objekt.
     * Hierbei erforderlich sind: <br>
     * - Name
     * - Nutzername
     * - Passwort
     * - Website
     *
     * @param utilityController Der {@link UtilityController}, der die zum verschlüsseln benötigten Methoden bereitstellt
     * @return {@link Credentials}, die die zuvor hinzugefügten Daten enthalten
     * @throws CredentialsBuilderException wenn:
     *                                     - Erforderliche Daten fehlen
     *                                     - {@code #changeReminderDays}, falls angegeben, weniger als 1 Tag ist
     */
    public Credentials build(UtilityController utilityController) throws CredentialsBuilderException {
        ValidationUtil.notEmptyOrNull(name, "name");
        ValidationUtil.notEmptyOrNull(userName, "userName");
        ValidationUtil.notEmptyOrNull(password, "password");
        ValidationUtil.notEmptyOrNull(website, "website");
        if (changeReminderDays != null && changeReminderDays < 1)
            throw new CredentialsBuilderException("change reminder less than 1 day: " + changeReminderDays);

        LocalDateTime now = LocalDateTime.now();
        created = created == null ? now : created;
        lastChanged = lastChanged == null ? now : lastChanged;

        EncryptedString encryptedPassword = utilityController.encryptText(password);

        Credentials credentials = new Credentials(name, userName, encryptedPassword, created);
        credentials.setNotes(notes);
        credentials.setWebsite(website);
        credentials.setChangeReminderDays(changeReminderDays);
        credentials.setLastChanged(lastChanged);
        securityQuestions.forEach((question, answer) -> credentials.addSecurityQuestion(
                new SecurityQuestion(utilityController.encryptText(question), utilityController.encryptText(answer))
                )
        );
        return credentials;
    }

    /**
     * Fügt den {@link Credentials} einen Namen hinzu
     *
     * @param name Der hinzuzufügende Name
     * @return Den Builder selbst
     */
    public CredentialsBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Fügt den {@link Credentials} einen Nutzername hinzu
     *
     * @param userName Der hinzuzufügende Nutzername
     * @return Den Builder selbst
     */
    public CredentialsBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * Fügt den {@link Credentials} ein Passwort hinzu
     *
     * @param password Das hinzuzufügende Passwort
     * @return Den Builder selbst
     */
    public CredentialsBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Fügt den {@link Credentials} eine Website hinzu
     *
     * @param website Die hinzuzuzufügende Website
     * @return Den Builder selbst
     */
    public CredentialsBuilder withWebsite(String website) {
        this.website = website;
        return this;
    }

    /**
     * Fügt den {@link Credentials} einen Änderungswecker hinzu
     *
     * @param changeReminderDays Die Zeitspanne nach dessen Ablauf das Passwort geändert werden soll (in Tagen)
     * @return Den Builder selbst
     */
    public CredentialsBuilder withChangeReminderDays(Integer changeReminderDays) {
        this.changeReminderDays = changeReminderDays;
        return this;
    }

    /**
     * Fügt den {@link Credentials} ein Datum hinzu, an dem sie zuletzt geändert wurden
     *
     * @param lastChanged Das zu setzende Datum
     * @return Den Builder selbst
     */
    public CredentialsBuilder withLastChanged(LocalDateTime lastChanged) {
        this.lastChanged = lastChanged;
        return this;
    }


    /**
     * Fügt den {@link Credentials} ein Datum hinzu, an dem sie erstellt wurden
     *
     * @param created Das zu setzende Datum
     * @return Den Builder selbst
     */
    public CredentialsBuilder withCreated(LocalDateTime created) {
        this.created = created;
        return this;
    }

    /**
     * Fügt den {@link Credentials} Notizen hinzu
     *
     * @param notes Die hinzuzufügenden Notizen
     * @return Den Builder selbst
     */
    public CredentialsBuilder withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    /**
     * Fügt den {@link Credentials} eine {@link SecurityQuestion} hinzu
     *
     * @param question Die Frage der hinzuzufügenden Sicherheitsfrage
     * @param answer   Die Antwort der hinzuzufügenden Sicherheitsfrage
     * @return Den Builder selbst
     */
    public CredentialsBuilder withSecurityQuestion(String question, String answer) {
        this.securityQuestions.put(question, answer);
        return this;
    }

    /**
     * Fügt den {@link Credentials} alle {@link SecurityQuestion} einer {@link Collection} hinzu
     *
     * @param questions Eine {@link Collection} aller hinzuzufügenden Sicherheitsfragen
     * @return Den Builder selbst
     */
    public CredentialsBuilder withSecurityQuestions(Map<String, String> questions) {
        securityQuestions.putAll(questions);
        return this;
    }

    public CredentialsBuilder withoutSecurityQuestion(String question, String answer) {
        securityQuestions.remove(question, answer);
        return this;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getWebsite() {
        return website;
    }

    public Integer getChangeReminderDays() {
        return changeReminderDays;
    }

    public LocalDateTime getLastChanged() {
        return lastChanged;
    }

    public LocalDateTime getCreatedAt() {
        return created;
    }

    public String getNotes() {
        return notes;
    }

    public Map<String, String> getSecurityQuestions() {
        return Collections.unmodifiableMap(securityQuestions);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CredentialsBuilder that = (CredentialsBuilder) object;
        return Objects.equals(name, that.name) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(password, that.password) &&
                Objects.equals(website, that.website) &&
                Objects.equals(changeReminderDays, that.changeReminderDays) &&
                Objects.equals(lastChanged, that.lastChanged) &&
                Objects.equals(created, that.created) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(securityQuestions, that.securityQuestions);
    }

    /**
     * Eine Exception, die bei einem Fehler im Buildprozess des {@link CredentialsBuilder} geworfen wird
     *
     * @see #build(UtilityController)
     */
    public static class CredentialsBuilderException extends RuntimeException {
        CredentialsBuilderException(String msg) {
            super(msg);
        }
    }
}
