package de.sopra.passwordmanager.model;

import java.util.Date;

/**
 * Die einfachsten Passwort-Funktionalitäten. Sie werden sowohl von dem Masterpasswort, als auch vom Passwortteil in den
 * Credentials gebraucht.
 *
 * @author Arne Dußin
 */
public class BasePassword {
    /**
     * Der tatsächliche Passwortstring. Er liegt nur verschlüsselt im Speicher, außer beim Masterpasswort. In diesem Fall
     * ist es das tatsächliche Passwort.
     */
    private String password;

    /**
     * Der Zeitpunkt, zu dem das Passwort das letzte Mal geändert wurde, gespeichert als lokaler Unix-Zeitstempel.
     */
    private Date lastChanged;

    /**
     * Die Zeit in Tagen, nach denen ein Passwort geändert werden soll. Kann mit lastChanged verglichen werden
     * <code>null</code>, falls nicht erinnert werden soll, also das Passwort ewig verwendet werden kann.
     */
    private Integer changeReminderDays;

    public BasePassword(String password, Date lastChanged, Integer changeReminderDays) {
        this.password = password;
        this.lastChanged = lastChanged;
        this.changeReminderDays = changeReminderDays;
    }

    public String getPassword() {
        return password;
    }

    public Integer getChangeReminderDays() {
        return changeReminderDays;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setChangeReminderDays(Integer changeReminderDays) {
        this.changeReminderDays = changeReminderDays;
    }

    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }

}
