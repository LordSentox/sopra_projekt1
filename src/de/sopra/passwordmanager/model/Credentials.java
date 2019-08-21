package de.sopra.passwordmanager.model;

import java.net.URL;
import java.util.Collection;

public class Credentials extends BasePassword
{

    private String name;

    private String userName;

    private URL website;

    private String notes;

    private final long created;

    private Collection<SecurityQuestion> securityQuestions;

    public Credentials( String password, long lastChanged, Integer changeReminderDays,
                        String name, String userName, URL website, String notes,
                        long created, Collection<SecurityQuestion> securityQuestions )
    {
        super( password, lastChanged, changeReminderDays );
        this.name = name;
        this.userName = userName;
        this.website = website;
        this.notes = notes;
        this.created = created;
        this.securityQuestions = securityQuestions;
    }

    public String getName()
    {
        return name;
    }

    public String getUserName()
    {
        return userName;
    }

    public URL getWebsite()
    {
        return website;
    }

    public String getNotes()
    {
        return notes;
    }

    public long getCreatedAt()
    {
        return created;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setUserName( String userName )
    {
        this.userName = userName;
    }

    public void setWebsite( URL website )
    {
        this.website = website;
    }

    public void setNotes( String notes )
    {
        this.notes = notes;
    }

}
