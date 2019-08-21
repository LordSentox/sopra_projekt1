package de.sopra.passwordmanager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Die Daten, die zu einem Passwort zusätzlich als Anmeldedaten gespeichert werden und das Passwort selbst.
 */
public class Credentials extends BasePassword {
    /**
     * Der eindeutige Name der Credentials. Auch in einer anderen Kategorie darf es kein Objekt mit diesem Namen geben
     * Darf nicht <code>null</code> sein.
     */
    private String name;

    /**
     * Der Benutzername, den der Anwender zur Anmeldung auf der Netzseite benutzt.
     * Darf nicht <code>null</code> sein.
     */
    private String userName;

    /**
     * Die Netzseite, auf der der Nutzer sich mit den im Credentials-Objekt gespeicherten Daten anmelden kann.
     * Darf nicht <code>null</code> sein.
     */
    private String website;

    /**
     * Mögliche Notizen, die vom Nutzer eingegeben werden können. Kann <code>null</code> sein, oder leer, wenn der Nutzer
     * keine Notizen angelegt hat.
     */
    private String notes;

    /**
     * Das Datum, zu dem das Credentials-Objekt zum ersten Mal angelegt wurde.
     */
    private final Date created;

    /**
     * Mögliche Sicherheitsfragen, die der Nutzer auf der Netzseite eingegeben hat.
     */
    private Collection<SecurityQuestion> securityQuestions;

    public Credentials(String name, String userName, String password, String website, Integer changeReminderDays, Date created, Date lastChanged,
                       String notes, Collection<SecurityQuestion> securityQuestions)
    {
        super( password, changeReminderDays, lastChanged);
        this.name = name;
        this.userName = userName;
        this.website = website;
        this.notes = notes;
        this.created = created;
        this.securityQuestions = securityQuestions;
    }

    public Credentials(String name, String userName, String password, String website, Integer changeReminderDays,
                       String notes)
    {
        this(name, userName, password, website, changeReminderDays, new Date(), new Date(), notes, new ArrayList<>());
    }

    public String getName() { return name; }

    public String getUserName() {
        return userName;
    }

    public String getWebsite() {
        return website;
    }

    public String getNotes() {
        return notes;
    }

    public Date getCreatedAt() {
        return created;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setWebsite( String website ) { this.website = website; }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
