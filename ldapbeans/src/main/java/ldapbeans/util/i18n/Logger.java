/*
 * This file is part of ldapbeans
 *
 * Released under LGPL
 *
 * ldapbeans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ldapbeans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ldapbeans.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Bruno Macherel
 */
package ldapbeans.util.i18n;

import org.apache.log4j.Level;

public final class Logger {

    /** Message manager instance */
    private final static MessageManager MESSAGE = MessageManager.getInstance();

    /**
     * Retrieve a logger.
     * 
     * @param p_Class
     *            The class to log
     * @return A logger
     */
    public static Logger getLogger(Class<?> p_Class) {
	return new Logger(p_Class);
    }

    /**
     * Retrieve a logger.
     * 
     * @return A logger
     */
    public static Logger getLogger() {
	return new Logger();
    }

    private final org.apache.log4j.Logger m_Logger;

    /**
     * Create a new Logger
     * 
     * @param p_Class
     *            The class to log
     */
    private Logger(Class<?> p_Class) {
	m_Logger = org.apache.log4j.Logger.getLogger(p_Class);
    }

    /**
     * Create a new Logger
     */
    private Logger() {
	String className = new Throwable().getStackTrace()[2].getClassName();
	m_Logger = org.apache.log4j.Logger.getLogger(className);
    }

    /**
     * Log a message object with the INFO Level.
     * 
     * @param p_Message
     *            The message to log
     */
    public void info(Message p_Message) {
	if (m_Logger.isEnabledFor(Level.INFO)) {
	    m_Logger.info(convertMessage(p_Message));
	}

    }

    /**
     * Log a message object with the WARN Level.
     * 
     * @param p_Message
     *            The message to log
     */
    public void warn(Message p_Message) {
	if (m_Logger.isEnabledFor(Level.WARN)) {
	    m_Logger.warn(convertMessage(p_Message));
	}

    }

    /**
     * Log a message object with the WARN Level.
     * 
     * @param p_Message
     *            The message to log
     * @param p_Throwable
     *            The source of the warning
     */
    public void warn(Message p_Message, Throwable p_Throwable) {
	if (m_Logger.isEnabledFor(Level.WARN)) {
	    m_Logger.warn(convertMessage(p_Message), p_Throwable);
	}
    }

    /**
     * Log a message object with the ERROR Level.
     * 
     * @param p_Message
     *            The message to log
     */
    public void error(Message p_Message) {
	if (m_Logger.isEnabledFor(Level.ERROR)) {
	    m_Logger.error(convertMessage(p_Message));
	}

    }

    /**
     * Log a message object with the ERROR Level.
     * 
     * @param p_Message
     *            The message to log
     * @param p_Throwable
     *            The source of the error
     */
    public void error(Message p_Message, Throwable p_Throwable) {
	if (m_Logger.isEnabledFor(Level.ERROR)) {
	    m_Logger.error(convertMessage(p_Message), p_Throwable);
	}
    }

    /**
     * Convert message to String by replacing the parameters by their value
     * 
     * @param p_Message
     *            Message to convert
     * @return The String that represent the message
     */
    private static String convertMessage(Message p_Message) {
	return MESSAGE.getMessage(p_Message.getMessageProperty(),
		p_Message.getParameters());
    }
}
