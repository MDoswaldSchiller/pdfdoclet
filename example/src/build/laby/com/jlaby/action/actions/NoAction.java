package com.jlaby.action.actions;

/*
 * @(#)NoAction.java 0.1 99/Feb/12
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
 * Action for no action
 * ("...nothing ever happens....")
 *
 * @author  Marcel Schoen
 * @version $Id: NoAction.java,v 1.1 2007/07/18 22:15:19 marcelschoen Exp $
 */
public class NoAction extends LabyAction {

    /**
     * Returns the type of the action (authenticated or public).
     *
     * @return The type of the action.
     */
    public int getType() {
        return LabyAction.AUTHENTICATED_ACTION;
    }

    /**
     * Returns the name of the handler for the action.
     *
     * @returns The class name of the handler for this action.
     */
    public String getHandler() {
        return "com.jlaby.action.handlers.NoActionHandler";
    }
}