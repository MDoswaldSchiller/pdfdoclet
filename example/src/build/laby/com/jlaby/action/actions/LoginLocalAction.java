package com.jlaby.action.actions;

/*
 * @(#)LoginLocalAction.java 0.1 99/Feb/12
 *
 * Copyright TARSEC Corp.
 * 8047 Zurich, Switzerland,  All Rights Reserved.
 *
 * CopyrightVersion 1.0
 */
import com.jlaby.action.*;
import com.jlaby.character.*;

import java.util.*;

/**
 * Action for character logging in locally
 *
 * @author  Marcel Schoen
 * @version $Id: LoginLocalAction.java,v 1.1 2007/07/18 22:15:19 marcelschoen Exp $
 */
public class LoginLocalAction extends LabyAction {

    // **************************************************
    // VARIABLES
    // **************************************************
    private String m_characterName = "";
    private String m_password = "";

    public LoginLocalAction(String characterName, String password) {
        m_characterName = characterName;
        m_password = password;
    }

    /**
     * Returns the type of the action (authenticated or public).
     *
     * @return The type of the action.
     */
    public int getType() {
        return LabyAction.PUBLIC_ACTION;
    }

    /**
     * Returns the name of the handler for the action.
     *
     * @returns The class name of the handler for this action.
     */
    public String getHandler() {
        return "com.jlaby.action.handlers.LoginLocalActionHandler";
    }

    public String getCharacterName() {
        return m_characterName;
    }

    public String getPassword() {
        return m_password;
    }
}
