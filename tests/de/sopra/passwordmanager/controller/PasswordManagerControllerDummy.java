package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.view.LoginViewAUI;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MasterPasswordViewAUI;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class PasswordManagerControllerDummy
{

    public static PasswordManagerController getNewController()
    {
        return create();
    }

    private static PasswordManagerController create()
    {
        return new PasswordManagerController( createMainWindow(), createLogin(), createMasterPass() );
    }

    private static MasterPasswordViewAUI createMasterPass()
    {
        return null; //TODO
    }

    private static LoginViewAUI createLogin()
    {
        return null; //TODO
    }

    private static MainWindowAUI createMainWindow()
    {
        return null; //TODO
    }

}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/