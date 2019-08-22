package de.sopra.passwordmanager.model;

import de.sopra.passwordmanager.util.CredentialsBuilder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

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
    private final LocalDateTime created;

    /**
     * Mögliche Sicherheitsfragen, die der Nutzer auf der Netzseite eingegeben hat.
     */
    private Collection<SecurityQuestion> securityQuestions;


    /**
     * @deprecated Benutzt stattdessen {@link CredentialsBuilder}.
     * XXX Und der Konstruktor funktioniert nicht aus folgendem Grund:
     * In jedem Fall wirft das Objekt eine {@link NullPointerException}, falls auf einem so erstellten Objekt irgendeine der folgenden Methoden aufgerufen wird:
     * @see #addSecurityQuestion(SecurityQuestion)
     * @see #addSecurityQuestion(String, String)
     * @see #addSecurityQuestions(Collection)
     * @see #removeSecurityQuestion(SecurityQuestion)
     * @see #removeSecurityQuestions(Collection)
     *
     * Dies liegt daran, dass {@link #securityQuestions} <code>null</code> ist, und es keine Möglichkeit gibt
     * das Attribut mit einer {@link Collection} zu füllen.
     * Außerdem wird {@link #created} auf LocalDateTime.now() gesetzt, während lastChanged auf <code>null</code> gesetzt wird
     */
    @Deprecated
    public Credentials(String name, String userName, String password, String website) {
    	super(password, null, null);
        this.name = name;
        this.userName = userName;
        this.website = website;
        this.created = LocalDateTime.now();
    }

    public Credentials(String name, String userName, String password, String website, Integer changeReminderDays,
                       LocalDateTime created, LocalDateTime lastChanged, String notes, Collection<SecurityQuestion> securityQuestions) {
        super(password, changeReminderDays, lastChanged);
        this.name = name;
        this.userName = userName;
        this.website = website;
        this.created = created;
        this.notes = notes;
        this.securityQuestions = securityQuestions;
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

    public LocalDateTime getCreatedAt() {
        return created;
    }

    /**
     * Liefert alle {@link SecurityQuestion}, die diesem {@link Credentials} Objekt angehören
     * Die gelieferte {@link Collection} ist unmodifizierbar. Zum {@link SecurityQuestion} hinzuzufügen oder zu entfernen,
     * dienen andere Methoden
     * @return Eine unmodifizierbare Sammlung aller Sicherheitsfragen
     * @see #addSecurityQuestion(SecurityQuestion)
     * @see #removeSecurityQuestion(SecurityQuestion)
     */
    public Collection<SecurityQuestion> getSecurityQuestions() {
        return Collections.unmodifiableCollection(securityQuestions);
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

    public void addSecurityQuestion(SecurityQuestion securityQuestion) {
        securityQuestions.add(securityQuestion);
    }

    public void addSecurityQuestion(String question, String answer) {
        securityQuestions.add(new SecurityQuestion(question, answer));
    }

    public void addSecurityQuestions(Collection<SecurityQuestion> questions) {
        securityQuestions.addAll(questions);
    }

    public void removeSecurityQuestion(SecurityQuestion securityQuestion) {
        securityQuestions.remove(securityQuestion);
    }

    public void removeSecurityQuestions(Collection<SecurityQuestion> questions) {
        securityQuestions.removeAll(questions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return name.equals(that.name) &&
                userName.equals(that.userName) &&
                website.equals(that.website) &&
                notes.equals(that.notes) &&
                created.equals(that.created) &&
                Objects.equals(getLastChanged(), that.getLastChanged()) &&
                Objects.equals(getChangeReminderDays(), that.getChangeReminderDays()) &&
                getPassword().equals(that.getPassword()) &&
                securityQuestions.equals(that.securityQuestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, userName, website, notes, created, securityQuestions, getLastChanged(), getPassword(), getChangeReminderDays());
    }
}
