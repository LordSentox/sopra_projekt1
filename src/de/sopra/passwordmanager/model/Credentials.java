package de.sopra.passwordmanager.model;

import java.net.URL;
import java.util.Collection;
import java.util.Date;

/**
 * Die Daten, die zu einem Passwort zusätzlich als Anmeldedaten gespeichert werden und das Passwort selbst.
 */
public class Credentials extends BasePassword
{
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
    private URL website;

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

    public Credentials(String password, long lastChanged, Integer changeReminderDays,
                        String name, String userName, URL website, String notes,
                        Date created, Collection<SecurityQuestion> securityQuestions)
    {
        super( password, lastChanged, changeReminderDays );
        this.name = name;
        this.userName = userName;
        this.website = website;
        this.notes = notes;
        this.created = created;
        this.securityQuestions = securityQuestions;
    }

    public String getName() { return name; }

    public String getUserName() { return userName; }

    public URL getWebsite() { return website; }

    public String getNotes() { return notes; }

    public Date getCreatedAt() { return created; }

    public void setName( String name ) { this.name = name; }

    public void setUserName( String userName ) { this.userName = userName; }

    public void setWebsite( URL website ) { this.website = website; }

    public void setNotes( String notes ) { this.notes = notes; }
}
