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

import java.util.ResourceBundle;

public class MessageManager {

    private final static MessageManager INSTANCE = new MessageManager();

    /**
     * Return the singleton instance
     * 
     * @return The singleton instance
     */
    public static MessageManager getInstance() {
	return INSTANCE;
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
	message = ResourceBundle.getBundle("ldapbeans").getString(p_Key);
	message = String.format(message, p_Params);
	return message;
    }
}
