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
package ldapbeans.util;

public final class Logger {

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
    public void info(String p_Message) {
	m_Logger.info(p_Message);

    }

    /**
     * Log a message object with the WARN Level.
     * 
     * @param p_Message
     *            The message to log
     */
    public void warn(String p_Message) {
	m_Logger.warn(p_Message);

    }

    /**
     * Log a message object with the WARN Level.
     * 
     * @param p_Message
     *            The message to log
     * @param p_Throwable
     *            The source of the warning
     */
    public void warn(String p_Message, Throwable p_Throwable) {
	m_Logger.warn(p_Message, p_Throwable);
    }

    /**
     * Log a message object with the ERROR Level.
     * 
     * @param p_Message
     *            The message to log
     */
    public void error(String p_Message) {
	m_Logger.error(p_Message);

    }

    /**
     * Log a message object with the ERROR Level.
     * 
     * @param p_Message
     *            The message to log
     * @param p_Throwable
     *            The source of the error
     */
    public void error(String p_Message, Throwable p_Throwable) {
	m_Logger.error(p_Message, p_Throwable);
    }
}
