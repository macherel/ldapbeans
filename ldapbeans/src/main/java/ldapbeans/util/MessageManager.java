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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

    /** Map of instances */
    private final static Map<String, MessageManager> INSTANCES = new HashMap<String, MessageManager>();

    /** Pattern of parameter in messages */
    private final static Pattern PATTERN = Pattern.compile("(\\$\\d+)");

    /**
     * Return the default instance
     * 
     * @return The default instance
     */
    public static MessageManager getInstance() {
	return getInstance("ldapbeans");
    }

    /**
     * Return an instance corresponding to the bundle name
     * 
     * @param p_BundleName
     *            The name of the bundle of the message to manage
     * @return The singleton instance
     */
    public static MessageManager getInstance(String p_BundleName) {
	MessageManager instance = INSTANCES.get(p_BundleName);
	if (instance == null) {
	    instance = new MessageManager(p_BundleName);
	    INSTANCES.put(p_BundleName, instance);
	}
	return instance;
    }

    /** The name of the bundle of the message to manage */
    private final ResourceBundle m_ResourceBundle;

    /**
     * Constructor of the message manager
     * 
     * @param p_BundleName
     *            The name of the resource bundle to manage
     */
    private MessageManager(String p_BundleName) {
	m_ResourceBundle = ResourceBundle.getBundle(p_BundleName);
    }

    /**
     * Return the message depending the local
     * 
     * @param p_Key
     *            The key of the message
     * @param p_Params
     *            Optional parameters of the message
     * @return The message corresponding to the key and depending the local
     */
    public String getMessage(String p_Key, Object... p_Params) {
	String message;
	ArrayList<Object> params = new ArrayList<Object>();

	message = m_ResourceBundle.getString(p_Key);

	Matcher matcher = PATTERN.matcher(message);
	int index = 0;
	int i = -1;
	while (matcher.find(index)) {
	    i = Integer.parseInt(matcher.group().substring(1));
	    params.add(p_Params[i - 1]);
	    index = matcher.end();
	}

	// Replace every "$i" with "%s"
	matcher.reset();
	message = matcher.replaceAll("%s");

	// Replace every "%s" with parameters
	message = String.format(message, params.toArray());

	// Return formatted message
	return message;
    }
}
