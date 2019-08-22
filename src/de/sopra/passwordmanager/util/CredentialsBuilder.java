package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.model.BasePassword;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.model.SecurityQuestion;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Builder für {@link Credentials}. Attribute sind gleich denen von {@link Credentials}
 * @see Credentials
 * @see BasePassword
 */
public class CredentialsBuilder {

    private String name = null;
    private String userName = null;
    private String password = null;
    private String website = null;
    private Integer changeReminderDays = null;
    private Date lastChanged = null;
    private Date created = null;
    private String notes = "";
    private Collection<SecurityQuestion> securityQuestions = new HashSet<>();


    /**
     * Baut aus den hinzugefügte Daten ein {@link Credentials} Objekt.
     * Hierbei erforderlich sind: <br>
     *     - Name
     *     - Nutzername
     *     - Passwort
     *     - Website
     * @return {@link Credentials}, die die zuvor hinzugefügten Daten enthalten
     * @throws CredentialsBuilderException wenn:
     *  - Erforderliche Daten fehlen
     *  - {@code changeReminderDays}, falls angegeben, weniger als 1 Tag ist
     */
    public Credentials build() throws CredentialsBuilderException{
        if (name == null)       throw new CredentialsBuilderException("name is null");
        if (userName == null)   throw new CredentialsBuilderException("user name is null");
        if (password == null)   throw new CredentialsBuilderException("password is null");
        if (website == null)    throw new CredentialsBuilderException("website is null");
        if (changeReminderDays != null && changeReminderDays < 1) throw new CredentialsBuilderException("change reminder less than 1 day: " + changeReminderDays);
        if (lastChanged == null) {
               lastChanged = new Date();
               created = new Date();
        }

        return new Credentials(name, userName, password, website, changeReminderDays, created, lastChanged, notes, securityQuestions);
    }

    /**
     * Fügt den {@link Credentials} einen Namen hinzu
     * @param name Der hinzuzufügende Name
     * @return Den Builder selbst
     */
    public CredentialsBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Fügt den {@link Credentials} einen Nutzername hinzu
     * @param userName Der hinzuzufügende Nutzername
     * @return Den Builder selbst
     */
    public CredentialsBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * Fügt den {@link Credentials} ein Passwort hinzu
     * @param password Das hinzuzufügende Passwort
     * @return Den Builder selbst
     */
    public CredentialsBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Fügt den {@link Credentials} eine Website hinzu
     * @param website Die hinzuzuzufügende Website
     * @return Den Builder selbst
     */
    public CredentialsBuilder withWebsite(String website) {
        this.website = website;
        return this;
    }

    /**
     * Fügt den {@link Credentials} einen Änderungswecker hinzu
     * @param changeReminderDays Die Zeitspanne nach dessen Ablauf das Passwort geändert werden soll (in Tagen)
     * @return Den Builder selbst
     */
    public CredentialsBuilder withChangeReminderDays(int changeReminderDays) {
        this.changeReminderDays = changeReminderDays;
        return this;
    }

    /**
     * Fügt den {@link Credentials} ein Datum hinzu, an dem sie zuletzt geändert wurden
     * @param lastChanged Das zu setzende Datum
     * @return Den Builder selbst
     */
    public CredentialsBuilder withLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
        return this;
    }


    /**
     * Fügt den {@link Credentials} ein Datum hinzu, an dem sie erstellt wurden
     * @param created Das zu setzende Datum
     * @return Den Builder selbst
     */
    public CredentialsBuilder withCreated(Date created) {
        this.created = created;
        return this;
    }

    /**
     * Fügt den {@link Credentials} Notizen hinzu
     * @param notes Die hinzuzufügenden Notizen
     * @return Den Builder selbst
     */
    public CredentialsBuilder withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    /**
     * Fügt den {@link Credentials} eine {@link SecurityQuestion} hinzu
     * @param securityQuestion Die hinzuzufügende Sicherheitsfrage
     * @return Den Builder selbst
     */
    public CredentialsBuilder withSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestions.add(securityQuestion);
        return this;
    }

    /**
     * Fügt den {@link Credentials} eine {@link SecurityQuestion} hinzu
     * @param question Die Frage der hinzuzufügenden Sicherheitsfrage
     * @param answer Die Antwort der hinzuzufügenden Sicherheitsfrage
     * @return Den Builder selbst
     */
    public CredentialsBuilder withSecurityQuestion(String question, String answer) {
        this.securityQuestions.add(new SecurityQuestion(question, answer));
        return this;
    }

    /**
     * Fügt den {@link Credentials} alle {@link SecurityQuestion} einer {@link Collection} hinzu
     * @param questions Eine {@link Collection} aller hinzuzufügenden Sicherheitsfragen
     * @return Den Builder selbst
     */
    public CredentialsBuilder withSecurityQuestions(Collection<SecurityQuestion> questions) {
        securityQuestions.addAll(questions);
        return this;
    }

    /**
     * Eine Exception, die bei einem Fehler im Buildprozess des {@link CredentialsBuilder} geworfen wird
     * @see #build()
     */
    public static class CredentialsBuilderException extends RuntimeException {
        CredentialsBuilderException(String msg) {
            super(msg);
        }

        CredentialsBuilderException(String msg, Throwable e) {
            super(msg, e);
        }
    }
}
