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

import java.lang.reflect.Method;

public final class LdapbeansMessageManager {

    /** Singleton instance of this class */
    private final static LdapbeansMessageManager INSTANCE = new LdapbeansMessageManager();

    /** Message manager instance */
    private final static MessageManager MESSAGE = MessageManager.getInstance();

    /**
     * Return the singleton instance
     * 
     * @return The singleton instance
     */
    public static LdapbeansMessageManager getInstance() {
	return INSTANCE;
    }

    /**
     * Create a new message manager
     */
    private LdapbeansMessageManager() {
	// Nothing to do
    }

    /**
     * Return cache implementation message
     * 
     * @param p_ClassName
     *            The name of the class used as cache implementation
     * @return The cache implementation message
     */
    public String getCacheImplementationMessage(String p_ClassName) {
	return MESSAGE.getMessage("ldapbeans.cache.impl", p_ClassName);
    }

    /**
     * Return cache implementation error message
     * 
     * @param p_ClassName
     *            The name of the class not used as cache implementation
     * @return The cache implementation error message
     */
    public String getCacheImplementationErrorMessage(String p_ClassName) {
	return MESSAGE.getMessage("ldapbeans.cache.impl.error", p_ClassName);
    }

    /**
     * Return the message to use when an error occurs when trying to write
     * generated class in a file
     * 
     * @param p_ClassName
     *            The name of the class to serialize
     * @param p_FileName
     *            The name of the file
     * @return The message to use when an error occurs when trying to write
     *         generated class in a file
     */
    public String getGeneratedClassWriteErrorMessage(String p_ClassName,
	    String p_FileName) {
	return MESSAGE.getMessage("ldapbeans.generated.class.write.error",
		p_ClassName, p_FileName);
    }

    /**
     * Return the message to use when an error occurs when a class is generated
     * 
     * @param p_ClassName
     *            The name of the generated class
     * @param p_Method
     *            The generated method
     * @return The message to use when an error occurs when a class is generated
     */
    public String getGeneratedClassErrorMessage(String p_ClassName,
	    Method p_Method) {
	return MESSAGE.getMessage("ldapbeans.generated.class.error",
		p_ClassName, p_Method);
    }

    /**
     * Return the message to use if a method is already generated
     * 
     * @param p_ClassName
     *            The name of the generated class
     * @param p_Method
     *            The generated method
     * @return The message to use if a method is already generated
     */
    public String getGeneratedMethodExistsMessage(String p_ClassName,
	    Method p_Method) {
	return MESSAGE.getMessage("ldapbeans.generated.method.exists",
		p_ClassName, p_Method);
    }

    /**
     * Return the message to use when an error occurs during the creation of a
     * LdapBean
     * 
     * @return The message to use when an error occurs during the creation of a
     *         LdapBean
     */
    public String getLdapBeanCreationErrorMessage() {
	return MESSAGE.getMessage("ldapbeans.bean.creation.error");
    }

}
