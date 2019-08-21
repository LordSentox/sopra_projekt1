package de.sopra.passwordmanager.model;

public class BasePassword
{

    private String password;

    private long lastChanged;

    private Integer changeReminderDays;

    public BasePassword( String password, long lastChanged, Integer changeReminderDays )
    {
        this.password = password;
        this.lastChanged = lastChanged;
        this.changeReminderDays = changeReminderDays;
    }

    public String getPassword()
    {
        return password;
    }

    public Integer getChangeReminderDays()
    {
        return changeReminderDays;
    }

    public long getLastChanged()
    {
        return lastChanged;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public void setChangeReminderDays( Integer changeReminderDays )
    {
        this.changeReminderDays = changeReminderDays;
    }

    public void setLastChanged( long lastChanged )
    {
        this.lastChanged = lastChanged;
    }

}
